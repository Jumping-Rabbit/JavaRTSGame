package com.game.lwjgl.vulkan;

import com.game.lwjgl.LwjglData;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK14;
import org.lwjgl.vulkan.VkCommandBuffer;
import org.lwjgl.vulkan.VkCommandBufferAllocateInfo;
import org.lwjgl.vulkan.VkCommandPoolCreateInfo;

import java.nio.LongBuffer;

public class VulkanCommandPool {
  private VulkanCommandPool() {
  }
  
  public static long makeCommandPool(LwjglData lwjglData, int flags) {
    try (MemoryStack stack = MemoryStack.stackPush()) {
      VkCommandPoolCreateInfo info = VkCommandPoolCreateInfo.calloc(stack)
          .sType$Default()
          .flags(flags)
          .queueFamilyIndex(lwjglData.graphicsQueueFamilyIndex);
      LongBuffer pointer = stack.mallocLong(1);
      if (VK14.vkCreateCommandPool(lwjglData.vkDevice, info, null, pointer) != VK14.VK_SUCCESS) {
        throw new RuntimeException("failed to create command pool");
      }
      return pointer.get(0);
    }
  }
  
  public static VkCommandBuffer[] makeBuffers(LwjglData lwjglData, long pool, int level, int count) {
    try (MemoryStack stack = MemoryStack.stackPush()) {
      VkCommandBufferAllocateInfo allocateInfo = VkCommandBufferAllocateInfo.calloc(stack)
          .sType$Default()
          .commandPool(pool)
          .level(level)
          .commandBufferCount(count);
      PointerBuffer pointerBuffer = stack.mallocPointer(count);
      if (VK14.vkAllocateCommandBuffers(lwjglData.vkDevice, allocateInfo, pointerBuffer) != VK14.VK_SUCCESS) {
        throw new RuntimeException("failed to allocate command buffers");
      }
      VkCommandBuffer[] out = new VkCommandBuffer[count];
      for (int i = 0; i < count; i++) out[i] = new VkCommandBuffer(pointerBuffer.get(i), lwjglData.vkDevice);
      return out;
    }
  }
  
  public static void cleanupCommandPool(LwjglData lwjglData) {
    if (lwjglData.commandPool != 0) {
      VK14.vkDestroyCommandPool(lwjglData.vkDevice, lwjglData.commandPool, null);
      lwjglData.commandPool = 0;
    }
  }
}