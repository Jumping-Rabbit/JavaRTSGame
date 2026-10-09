package com.game.lwjgl.vulkan;

import com.game.lwjgl.LwjglData;
import org.lwjgl.vulkan.VK14;
import org.lwjgl.vulkan.VkCommandBuffer;

import java.util.ArrayList;
import java.util.List;

public class VulkanThreadState {
  private VulkanThreadState() {
  }
  
  public static void init(LwjglData lwjglData) {
    lwjglData.threadState = ThreadLocal.withInitial(() -> {
      ThreadState state = new ThreadState(lwjglData.MAX_FRAMES_IN_FLIGHT);
      for (int i = 0; i < lwjglData.MAX_FRAMES_IN_FLIGHT; i++) {
        state.pools[i] = VulkanCommandPool.makeCommandPool(lwjglData, VK14.VK_COMMAND_POOL_CREATE_TRANSIENT_BIT);
      }
      lwjglData.threadStates.add(state);
      return state;
    });
  }
  
  public static VkCommandBuffer acquireSecondary(LwjglData lwjglData, int frame) {
    ThreadState state = lwjglData.threadState.get();
    List<VkCommandBuffer> list = state.buffers.get(frame);
    if (state.used[frame] == list.size()) {
      list.add(VulkanCommandPool.makeBuffers(lwjglData, state.pools[frame],
          VK14.VK_COMMAND_BUFFER_LEVEL_SECONDARY, 1)[0]);
    }
    return list.get(state.used[frame]++);
  }
  
  public static void resetFrame(LwjglData lwjglData, int frame) {
    for (ThreadState state : lwjglData.threadStates) {
      VK14.vkResetCommandPool(lwjglData.vkDevice, state.pools[frame], 0);
      state.used[frame] = 0;
    }
  }
  
  public static void cleanup(LwjglData lwjglData) {
    for (ThreadState state : lwjglData.threadStates) {
      for (long pool : state.pools) if (pool != 0) VK14.vkDestroyCommandPool(lwjglData.vkDevice, pool, null);
    }
    lwjglData.threadStates.clear();
  }
  
  public static final class ThreadState {
    final long[] pools;
    final List<List<VkCommandBuffer>> buffers = new ArrayList<>();
    final int[] used;
    
    ThreadState(int frames) {
      pools = new long[frames];
      used = new int[frames];
      for (int i = 0; i < frames; i++) buffers.add(new ArrayList<>());
    }
  }
}