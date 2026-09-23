package com.game.vulkan;

import com.game.Init;
import com.game.screens.LoadingScreen;
import com.game.vulkan.context.VulkanContextManager;
import com.game.vulkan.memory.VulkanMemoryManager;
import com.game.vulkan.pipeline.VulkanPipelineManager;
import com.game.vulkan.render.VulkanRenderManager;


@Init(stage = 0)
public class VulkanManager {
  VulkanContextManager vulkanContextManager;
  VulkanMemoryManager vulkanMemoryManager;
  VulkanPipelineManager vulkanPipelineManager;
  VulkanRenderManager vulkanRenderManager;
  
  
  public static void initWindow() {
  }
  
  public static void init(LoadingScreen loadingScreen) {
    
    loadingScreen.increment();
  }
  
  public static long getWindow() {
    return 0;
  }
  
  public static void pollEvents() {
  
  }
  
  public static boolean shouldWindowClose() {
    return false;
  }
}
