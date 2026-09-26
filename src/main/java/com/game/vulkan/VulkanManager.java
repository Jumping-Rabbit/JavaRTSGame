package com.game.vulkan;

import com.game.screens.LoadingScreen;
import com.game.vulkan.context.VulkanContextManager;
import com.game.vulkan.memory.VulkanMemoryManager;
import com.game.vulkan.pipeline.VulkanPipelineManager;
import com.game.vulkan.render.VulkanRenderManager;


public class VulkanManager {
  private VulkanContextManager vulkanContextManager;
  private VulkanMemoryManager vulkanMemoryManager;
  private VulkanPipelineManager vulkanPipelineManager;
  private VulkanRenderManager vulkanRenderManager;
  
  
  public VulkanManager(LoadingScreen loadingScreen){
    vulkanContextManager = new VulkanContextManager();
    vulkanMemoryManager = new VulkanMemoryManager();
    vulkanPipelineManager = new VulkanPipelineManager();
    vulkanRenderManager = new VulkanRenderManager();
  }
  public void cleanup(){
    vulkanContextManager.waitIdle();
    vulkanRenderManager.cleanupSync();
    vulkanRenderManager.cleanupCommandPool();
    vulkanPipelineManager.cleanupPipeline();
    vulkanPipelineManager.cleanupDescriptors();
    vulkanRenderManager.cleanupSwapChain();
    vulkanMemoryManager.cleanup();
    vulkanContextManager.cleanupDevice();
    vulkanContextManager.cleanupWindowAndInstance();
  }
  
  public long getWindowHandle(){
    return vulkanContextManager.getWindowHandle();
  }
  public void setShouldClose(){
    vulkanContextManager.setShouldClose();
  }
  public boolean shouldClose(){
    return vulkanContextManager.shouldClose();
  }
  public void pollEvents(){
    vulkanContextManager.pollEvents();
  }
}
