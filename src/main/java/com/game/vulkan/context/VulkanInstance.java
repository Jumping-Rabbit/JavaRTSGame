package com.game.vulkan.context;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK14;
import org.lwjgl.vulkan.VkInstance;
import org.lwjgl.vulkan.VkInstanceCreateInfo;

class VulkanInstance {
  private VulkanContextManager vulkanContextManager;
  VkInstance vkInstance;
  VulkanInstance(VulkanContextManager vulkanContextManager){
    this.vulkanContextManager = vulkanContextManager;
    try (MemoryStack stack = MemoryStack.stackPush()) {
      VkInstanceCreateInfo createInfo = VkInstanceCreateInfo.calloc(stack)
          .sType(VK14.VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO);
      
      PointerBuffer pInstance = stack.mallocPointer(1);
      
      int result = VK14.vkCreateInstance(createInfo, null, pInstance);
      if (result != VK14.VK_SUCCESS) {
        throw new RuntimeException("Failed to create Vulkan instance. Error code: " + result);
      }
      
      long instanceHandle = pInstance.get(0);
      vkInstance = new VkInstance(instanceHandle, createInfo);
    }
  }
  void cleanup(){
    VK14.vkDestroyInstance(vkInstance, null);
  }
  VkInstance getVkInstance(){
    return vkInstance;
  }
}
