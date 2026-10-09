package com.game.lwjgl.vulkan;

import com.game.lwjgl.LwjglData;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.KHRSurface;
import org.lwjgl.vulkan.KHRSwapchain;
import org.lwjgl.vulkan.VK14;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkDeviceCreateInfo;
import org.lwjgl.vulkan.VkDeviceQueueCreateInfo;
import org.lwjgl.vulkan.VkPhysicalDevice;
import org.lwjgl.vulkan.VkPhysicalDeviceFeatures;
import org.lwjgl.vulkan.VkPhysicalDeviceVulkan13Features;
import org.lwjgl.vulkan.VkQueue;
import org.lwjgl.vulkan.VkQueueFamilyProperties;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class VulkanDevice {
  private VulkanDevice() {
  }
  
  public static void getAllPhysicalDevices(LwjglData lwjglData) {
    try (MemoryStack stack = MemoryStack.stackPush()) {
      IntBuffer pDeviceCount = stack.mallocInt(1);
      
      VK14.vkEnumeratePhysicalDevices(lwjglData.vkInstance, pDeviceCount, null);
      int deviceCount = pDeviceCount.get(0);
      
      if (deviceCount == 0) {
        throw new IllegalStateException("failed to find GPUs with Vulkan support");
      }
      
      PointerBuffer pPhysicalDevices = stack.mallocPointer(deviceCount);
      VK14.vkEnumeratePhysicalDevices(lwjglData.vkInstance, pDeviceCount, pPhysicalDevices);
      
      VkPhysicalDevice[] devices = new VkPhysicalDevice[deviceCount];
      for (int i = 0; i < deviceCount; i++) {
        devices[i] = new VkPhysicalDevice(pPhysicalDevices.get(i), lwjglData.vkInstance);
      }
      
      lwjglData.vkPhysicalDevices = devices;
    }
  }
  
  public static void chooseVkPhysicalDevice(LwjglData lwjglData) {
    lwjglData.vkPhysicalDevice = lwjglData.vkPhysicalDevices[0];
  }
  
  public static void makeVkDevice(LwjglData lwjglData) {
    try (MemoryStack stack = MemoryStack.stackPush()) {
      FloatBuffer queuePriorities = stack.floats(1.0f);
      
      VkDeviceQueueCreateInfo.Buffer queueCreateInfo = VkDeviceQueueCreateInfo.calloc(1, stack)
          .sType(VK14.VK_STRUCTURE_TYPE_DEVICE_QUEUE_CREATE_INFO)
          .queueFamilyIndex(lwjglData.graphicsQueueFamilyIndex)
          .pQueuePriorities(queuePriorities);
      
      VkPhysicalDeviceFeatures deviceFeatures = VkPhysicalDeviceFeatures.calloc(stack);
      
      VkPhysicalDeviceVulkan13Features vk13Features = VkPhysicalDeviceVulkan13Features.calloc(stack)
          .sType(VK14.VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VULKAN_1_3_FEATURES)
          .dynamicRendering(true)
          .synchronization2(true);
      
      PointerBuffer deviceExtensions = stack.mallocPointer(1);
      deviceExtensions.put(0, stack.UTF8(KHRSwapchain.VK_KHR_SWAPCHAIN_EXTENSION_NAME));
      
      VkDeviceCreateInfo createInfo = VkDeviceCreateInfo.calloc(stack)
          .sType(VK14.VK_STRUCTURE_TYPE_DEVICE_CREATE_INFO)
          .pNext(vk13Features)
          .pQueueCreateInfos(queueCreateInfo)
          .ppEnabledExtensionNames(deviceExtensions)
          .pEnabledFeatures(deviceFeatures);
      
      PointerBuffer pDevice = stack.pointers(VK14.VK_NULL_HANDLE);
      
      int result = VK14.vkCreateDevice(lwjglData.vkPhysicalDevice, createInfo, null, pDevice);
      
      if (result != VK14.VK_SUCCESS) {
        throw new RuntimeException("failed to create logical device. " + result);
      }
      
      lwjglData.vkDevice = new VkDevice(pDevice.get(0), lwjglData.vkPhysicalDevice, createInfo);
    }
  }
  
  public static void findGraphicsQueueFamilyIndex(LwjglData lwjglData) {
    try (MemoryStack stack = MemoryStack.stackPush()) {
      IntBuffer queueFamilyCount = stack.mallocInt(1);
      VK14.vkGetPhysicalDeviceQueueFamilyProperties(lwjglData.vkPhysicalDevice, queueFamilyCount, null);
      
      VkQueueFamilyProperties.Buffer queueFamilies = VkQueueFamilyProperties.malloc(queueFamilyCount.get(0), stack);
      VK14.vkGetPhysicalDeviceQueueFamilyProperties(lwjglData.vkPhysicalDevice, queueFamilyCount, queueFamilies);
      
      IntBuffer presentSupport = stack.mallocInt(1);
      
      for (int i = 0; i < queueFamilies.capacity(); i++) {
        if ((queueFamilies.get(i).queueFlags() & VK14.VK_QUEUE_GRAPHICS_BIT) != 0) {
          lwjglData.graphicsQueueFamilyIndex = i;
        }
        
        KHRSurface.vkGetPhysicalDeviceSurfaceSupportKHR(
            lwjglData.vkPhysicalDevice, i, lwjglData.surface, presentSupport
        );
        if (presentSupport.get(0) == VK14.VK_TRUE) {
          lwjglData.presentQueueFamilyIndex = i;
        }
        
        if (lwjglData.graphicsQueueFamilyIndex != -1 && lwjglData.presentQueueFamilyIndex != -1) {
          break;
        }
      }
    }
  }
  
  public static void getQueues(LwjglData lwjglData) {
    try (MemoryStack stack = MemoryStack.stackPush()) {
      PointerBuffer pQueue = stack.mallocPointer(1);
      
      VK14.vkGetDeviceQueue(lwjglData.vkDevice, lwjglData.graphicsQueueFamilyIndex, 0, pQueue);
      lwjglData.graphicsQueue = new VkQueue(pQueue.get(0), lwjglData.vkDevice);
      
      VK14.vkGetDeviceQueue(lwjglData.vkDevice, lwjglData.presentQueueFamilyIndex, 0, pQueue);
      lwjglData.presentQueue = new VkQueue(pQueue.get(0), lwjglData.vkDevice);
    }
  }
  
  public static void waitIdle(LwjglData lwjglData) {
    VK14.vkDeviceWaitIdle(lwjglData.vkDevice);
  }
  
  public static void cleanupDevice(LwjglData lwjglData) {
    VK14.vkDestroyDevice(lwjglData.vkDevice, null);
  }
  
}
