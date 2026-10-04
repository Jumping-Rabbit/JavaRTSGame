package com.game.lwjgl;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

class VulkanDevice {
  

  
  private VulkanDevice(){}
  
  void cleanup(LwjglData lwjglData){
    VK14.vkDestroyDevice(lwjglData.vkDevice, null);
  }
  
  static void getAllPhysicalDevices(LwjglData lwjglData) {
    try (MemoryStack stack = MemoryStack.stackPush()) {
      IntBuffer pDeviceCount = stack.mallocInt(1);
      
      VK14.vkEnumeratePhysicalDevices(lwjglData.vkInstance, pDeviceCount, null);
      int deviceCount = pDeviceCount.get(0);
      
      if (deviceCount == 0) {
        throw new IllegalStateException("Failed to find GPUs with Vulkan support!");
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
  static void chooseVkPhysicalDevice(LwjglData lwjglData){
    lwjglData.vkPhysicalDevice = lwjglData.vkPhysicalDevices[0];
  }
  static void makeVkDevice(LwjglData lwjglData) {
    try (MemoryStack stack = MemoryStack.stackPush()) {
      FloatBuffer queuePriorities = stack.floats(1.0f);
      
      VkDeviceQueueCreateInfo.Buffer queueCreateInfo = VkDeviceQueueCreateInfo.calloc(1, stack)
          .sType(VK14.VK_STRUCTURE_TYPE_DEVICE_QUEUE_CREATE_INFO)
          .queueFamilyIndex(lwjglData.graphicsQueueFamilyIndex)
          .pQueuePriorities(queuePriorities);
      
      VkPhysicalDeviceFeatures deviceFeatures = VkPhysicalDeviceFeatures.calloc(stack);
      
      VkPhysicalDeviceVulkan13Features vk13Features = VkPhysicalDeviceVulkan13Features.calloc(stack)
          .sType(VK14.VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VULKAN_1_3_FEATURES)
          .dynamicRendering(true);
      
      VkDeviceCreateInfo createInfo = VkDeviceCreateInfo.calloc(stack)
          .sType(VK14.VK_STRUCTURE_TYPE_DEVICE_CREATE_INFO)
          .pNext(vk13Features)
          .pQueueCreateInfos(queueCreateInfo)
          .pEnabledFeatures(deviceFeatures);
      
      PointerBuffer pDevice = stack.pointers(VK14.VK_NULL_HANDLE);
      
      int result = VK14.vkCreateDevice(lwjglData.vkPhysicalDevice, createInfo, null, pDevice);
      
      if (result != VK14.VK_SUCCESS) {
        throw new RuntimeException("Failed to create logical device! Error code: " + result);
      }
      
      lwjglData.vkDevice = new VkDevice(pDevice.get(0), lwjglData.vkPhysicalDevice, createInfo);
    }
  }
  static void findGraphicsQueueFamilyIndex(LwjglData lwjglData){
    try (MemoryStack stack = MemoryStack.stackPush()) {
      IntBuffer pQueueFamilyPropertyCount = stack.mallocInt(1);
      
      VK14.vkGetPhysicalDeviceQueueFamilyProperties(lwjglData.vkPhysicalDevice, pQueueFamilyPropertyCount, null);
      int queueCount = pQueueFamilyPropertyCount.get(0);
      
      VkQueueFamilyProperties.Buffer queueProps = VkQueueFamilyProperties.calloc(queueCount, stack);
      
      VK14.vkGetPhysicalDeviceQueueFamilyProperties(lwjglData.vkPhysicalDevice, pQueueFamilyPropertyCount, queueProps);
      
      for (int i = 0; i < queueCount; i++) {
        int queueFlags = queueProps.get(i).queueFlags();
        if ((queueFlags & VK14.VK_QUEUE_GRAPHICS_BIT) != 0) {
          lwjglData.graphicsQueueFamilyIndex = i;
          return;
        }
      }
      
      throw new RuntimeException("Failed to find a queue family that supports graphics.");
    }
  }
  
  static void waitIdle(LwjglData lwjglData){
    VK14.vkDeviceWaitIdle(lwjglData.vkDevice);
  }
  
}
