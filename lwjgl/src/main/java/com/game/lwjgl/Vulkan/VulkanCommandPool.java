package com.game.lwjgl.Vulkan;

import com.game.lwjgl.LwjglData;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK14;
import org.lwjgl.vulkan.VkCommandBuffer;
import org.lwjgl.vulkan.VkCommandBufferAllocateInfo;
import org.lwjgl.vulkan.VkCommandPoolCreateInfo;

import java.nio.LongBuffer;

public class VulkanCommandPool {
  private VulkanCommandPool(){}
  public static long makeCommandPool(LwjglData lwjglData) {
    try (MemoryStack stack = MemoryStack.stackPush()) {
      VkCommandPoolCreateInfo poolInfo = VkCommandPoolCreateInfo.calloc(stack)
          .sType(VK14.VK_STRUCTURE_TYPE_COMMAND_POOL_CREATE_INFO)
          .flags(VK14.VK_COMMAND_POOL_CREATE_RESET_COMMAND_BUFFER_BIT)
          .queueFamilyIndex(lwjglData.graphicsQueueFamilyIndex);
      
      LongBuffer pCmdPool = stack.mallocLong(1);
      if (VK14.vkCreateCommandPool(lwjglData.vkDevice, poolInfo, null, pCmdPool) != VK14.VK_SUCCESS) {
        throw new RuntimeException("Failed to create command pool");
      }
      
      return  pCmdPool.get(0);
    }
  }
  
  public static void cleanupCommandPool(LwjglData lwjglData) {
    for (Long commandPool : lwjglData.allMadePools){
      VK14.vkDestroyCommandPool(lwjglData.vkDevice, commandPool, null);
    }
    
  }
  
  public static ObjectArrayList<VkCommandBuffer> makeBuffer(long commandPool, LwjglData lwjglData){
    try (MemoryStack stack = MemoryStack.stackPush()){
      VkCommandBufferAllocateInfo allocInfo = VkCommandBufferAllocateInfo.calloc(stack)
          .sType(VK14.VK_STRUCTURE_TYPE_COMMAND_BUFFER_ALLOCATE_INFO)
          .commandPool(commandPool)
          .level(VK14.VK_COMMAND_BUFFER_LEVEL_PRIMARY)
          .commandBufferCount(lwjglData.MAX_FRAMES_IN_FLIGHT);
      
      PointerBuffer pCommandBuffers = stack.mallocPointer(lwjglData.MAX_FRAMES_IN_FLIGHT);
      if (VK14.vkAllocateCommandBuffers(lwjglData.vkDevice, allocInfo, pCommandBuffers) != VK14.VK_SUCCESS) {
        throw new RuntimeException("Failed to allocate command buffers");
      }
      
      ObjectArrayList<VkCommandBuffer> buffers = new ObjectArrayList<>();
      for (int i = 0; i < lwjglData.MAX_FRAMES_IN_FLIGHT; i++) {
        buffers.add(new VkCommandBuffer(pCommandBuffers.get(i), lwjglData.vkDevice));
      }
      return buffers;
    }
  }
}
