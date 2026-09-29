package com.game.lwjgl.internal.vulkan;

import com.game.lwjgl.internal.vulkan.context.VulkanContextManager;
import com.game.lwjgl.internal.vulkan.memory.VulkanMemoryManager;
import com.game.lwjgl.internal.vulkan.pipeline.VulkanPipelineManager;
import com.game.lwjgl.internal.vulkan.render.VulkanRenderManager;
import org.lwjgl.vulkan.VkDevice;


public class VulkanManager {
  private VulkanContextManager vulkanContextManager;
  private VulkanMemoryManager vulkanMemoryManager;
  private VulkanPipelineManager vulkanPipelineManager;
  private VulkanRenderManager vulkanRenderManager;
  
  
  public VkDevice getVkDevice(){
    return vulkanContextManager.getVkDevice();
  }
  public VulkanManager(){
    vulkanContextManager = new VulkanContextManager(this);
    vulkanMemoryManager = new VulkanMemoryManager(this);
    vulkanPipelineManager = new VulkanPipelineManager(this);
    vulkanRenderManager = new VulkanRenderManager(this);
  }
  public void cleanup(){
    vulkanContextManager.waitIdle();
    vulkanRenderManager.cleanupSync();
    vulkanRenderManager.cleanupCommandPool();
    vulkanPipelineManager.cleanupPipeline();
    vulkanPipelineManager.cleanupDescriptors();
    vulkanRenderManager.cleanupSwapChain();
    vulkanMemoryManager.cleanup();
    vulkanContextManager.cleanupDeviceAndInstance();
  }
}
