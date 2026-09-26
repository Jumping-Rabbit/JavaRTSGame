package com.game.vulkan.context;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

class VulkanDevice {
  private VulkanContextManager vulkanContextManager;
  
  VkPhysicalDevice[] vkPhysicalDevices;
  VkDevice vkDevice;
  
  VulkanDevice(VulkanContextManager vulkanContextManager){
    this.vulkanContextManager = vulkanContextManager;
    vkPhysicalDevices = getAllPhysicalDevices(vulkanContextManager.vulkanInstance.vkInstance);
    makeVkDevice(vkPhysicalDevices[0]);
  }
  void cleanup(){
    VK14.vkDestroyDevice(vkDevice, null);
  }
  
  private VkPhysicalDevice[] getAllPhysicalDevices(VkInstance instance) {
    try (MemoryStack stack = MemoryStack.stackPush()) {
      IntBuffer pDeviceCount = stack.mallocInt(1);
      
      VK14.vkEnumeratePhysicalDevices(instance, pDeviceCount, null);
      int deviceCount = pDeviceCount.get(0);
      
      if (deviceCount == 0) {
        throw new IllegalStateException("Failed to find GPUs with Vulkan support!");
      }
      
      PointerBuffer pPhysicalDevices = stack.mallocPointer(deviceCount);
      VK14.vkEnumeratePhysicalDevices(instance, pDeviceCount, pPhysicalDevices);
      
      VkPhysicalDevice[] devices = new VkPhysicalDevice[deviceCount];
      for (int i = 0; i < deviceCount; i++) {
        devices[i] = new VkPhysicalDevice(pPhysicalDevices.get(i), instance);
      }
      
      return devices;
    }
  }
  private void makeVkDevice(VkPhysicalDevice vkPhysicalDevice){
    try (MemoryStack stack = MemoryStack.stackPush()) {
      
      // Define queue priorities (floating point numbers between 0.0 and 1.0)
      FloatBuffer queuePriorities = stack.floats(1.0f);
      
      // Fill out the queue creation struct
      VkDeviceQueueCreateInfo.Buffer queueCreateInfo = VkDeviceQueueCreateInfo.calloc(1, stack)
          .sType(VK14.VK_STRUCTURE_TYPE_DEVICE_QUEUE_CREATE_INFO)
          .queueFamilyIndex(1)
          .pQueuePriorities(queuePriorities);
      VkPhysicalDeviceFeatures deviceFeatures = VkPhysicalDeviceFeatures.calloc(stack);
      PointerBuffer extensions = stack.pointers(stack.UTF8(KHRSwapchain.VK_KHR_SWAPCHAIN_EXTENSION_NAME));
      
      // Fill out the logical device creation struct
      VkDeviceCreateInfo createInfo = VkDeviceCreateInfo.calloc(stack)
          .sType(VK14.VK_STRUCTURE_TYPE_DEVICE_CREATE_INFO)
          .pQueueCreateInfos(queueCreateInfo)
          .pEnabledFeatures(deviceFeatures)
          .ppEnabledExtensionNames(extensions);
      
      // Pointer to hold the resulting VkDevice handle
      PointerBuffer pDevice = stack.pointers(VK14.VK_NULL_HANDLE);
      
      // Call Vulkan to create the device
      // 'physicalDevice' is the VkPhysicalDevice you selected earlier
      int result = VK14.vkCreateDevice(vkPhysicalDevice, createInfo, null, pDevice);
      
      if (result != VK14.VK_SUCCESS) {
        throw new RuntimeException("Failed to create logical device! Error code: " + result);
      }
      
      // Extract the final device handle
      vkDevice = new VkDevice(pDevice.get(0), vkPhysicalDevice, createInfo);
    }
  }
  
  public void waitIdle(){
    VK14.vkDeviceWaitIdle(vkDevice);
  }
  
}
