package com.game.lwjgl;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK14;
import org.lwjgl.vulkan.VkCommandBuffer;
import org.lwjgl.vulkan.VkCommandBufferAllocateInfo;
import org.lwjgl.vulkan.VkCommandPoolCreateInfo;

import java.nio.LongBuffer;

class VulkanCommandPool {
  static void makeCommandBuffer(LwjglData lwjglData) {
    try (MemoryStack stack = MemoryStack.stackPush()) {
      VkCommandPoolCreateInfo poolInfo = VkCommandPoolCreateInfo.calloc(stack)
          .sType(VK14.VK_STRUCTURE_TYPE_COMMAND_POOL_CREATE_INFO)
          .flags(VK14.VK_COMMAND_POOL_CREATE_RESET_COMMAND_BUFFER_BIT)
          .queueFamilyIndex(lwjglData.graphicsQueueFamilyIndex);
      
      LongBuffer pCmdPool = stack.mallocLong(1);
      if (VK14.vkCreateCommandPool(lwjglData.vkDevice, poolInfo, null, pCmdPool) != VK14.VK_SUCCESS) {
        throw new RuntimeException("Failed to create command pool!");
      }
      long commandPool = pCmdPool.get(0);
      lwjglData.commandPool = commandPool;
      VkCommandBufferAllocateInfo allocInfo = VkCommandBufferAllocateInfo.calloc(stack)
          .sType(VK14.VK_STRUCTURE_TYPE_COMMAND_BUFFER_ALLOCATE_INFO)
          .commandPool(commandPool)
          .level(VK14.VK_COMMAND_BUFFER_LEVEL_PRIMARY)
          .commandBufferCount(lwjglData.MAX_FRAMES_IN_FLIGHT);
      
      PointerBuffer pCommandBuffers = stack.mallocPointer(lwjglData.MAX_FRAMES_IN_FLIGHT);
      if (VK14.vkAllocateCommandBuffers(lwjglData.vkDevice, allocInfo, pCommandBuffers) != VK14.VK_SUCCESS) {
        throw new RuntimeException("Failed to allocate command buffers!");
      }
      
      for (int i = 0; i < lwjglData.MAX_FRAMES_IN_FLIGHT; i++) {
        lwjglData.commandBuffers[i] = new VkCommandBuffer(pCommandBuffers.get(i), lwjglData.vkDevice);
      }
    }
  }
  
  static void cleanupCommandPool(LwjglData lwjglData) {
    VK14.vkDestroyCommandPool(lwjglData.vkDevice, lwjglData.commandPool, null);
  }
}
