package com.game.lwjgl.internal.vulkan.context;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

class VulkanDevice {
  private VulkanContextManager vulkanContextManager;
  
  private VkPhysicalDevice[] vkPhysicalDevices;
  private VkDevice vkDevice;
  
  VulkanDevice(VulkanContextManager vulkanContextManager){
    this.vulkanContextManager = vulkanContextManager;
    vkPhysicalDevices = getAllPhysicalDevices(vulkanContextManager.vulkanInstance.getVkInstance());
    makeVkDevice(vkPhysicalDevices[0]);//TODO: have a better way to choose and also user set
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
      FloatBuffer queuePriorities = stack.floats(1.0f);
      
      VkDeviceQueueCreateInfo.Buffer queueCreateInfo = VkDeviceQueueCreateInfo.calloc(1, stack)
          .sType(VK14.VK_STRUCTURE_TYPE_DEVICE_QUEUE_CREATE_INFO)
          .queueFamilyIndex(1)
          .pQueuePriorities(queuePriorities);
      VkPhysicalDeviceFeatures deviceFeatures = VkPhysicalDeviceFeatures.calloc(stack);
      PointerBuffer extensions = stack.pointers(stack.UTF8(KHRSwapchain.VK_KHR_SWAPCHAIN_EXTENSION_NAME));
      
      VkDeviceCreateInfo createInfo = VkDeviceCreateInfo.calloc(stack)
          .sType(VK14.VK_STRUCTURE_TYPE_DEVICE_CREATE_INFO)
          .pQueueCreateInfos(queueCreateInfo)
          .pEnabledFeatures(deviceFeatures)
          .ppEnabledExtensionNames(extensions);
      
      PointerBuffer pDevice = stack.pointers(VK14.VK_NULL_HANDLE);
      
      int result = VK14.vkCreateDevice(vkPhysicalDevice, createInfo, null, pDevice);
      
      if (result != VK14.VK_SUCCESS) {
        throw new RuntimeException("Failed to create logical device! Error code: " + result);
      }
      
      vkDevice = new VkDevice(pDevice.get(0), vkPhysicalDevice, createInfo);
    }
  }
  
  void waitIdle(){
    VK14.vkDeviceWaitIdle(vkDevice);
  }
  VkDevice getVkDevice(){
    return vkDevice;
  }
  
}
