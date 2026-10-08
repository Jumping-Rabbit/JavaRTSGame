package com.game.lwjgl.Vulkan;

import com.game.lwjgl.LwjglData;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.lwjgl.vulkan.VkCommandBuffer;

public class VulkanThreadState {
  private VulkanThreadState(){}
  
  public static void makeThreadLocalBuffers(LwjglData lwjglData){
    lwjglData.threadBuffers = ThreadLocal.withInitial(() -> {
      long pool = VulkanCommandPool.makeCommandPool(lwjglData);
      lwjglData.allMadePools.add(pool);
      return VulkanCommandPool.makeBuffer(pool, lwjglData);
    });
  }
  public static VkCommandBuffer getBufferForCurrentThread(LwjglData lwjglData, int currentFrameIndex) {
    ObjectArrayList<VkCommandBuffer> myBuffers = lwjglData.threadBuffers.get();
    return myBuffers.get(currentFrameIndex);
  }
}
