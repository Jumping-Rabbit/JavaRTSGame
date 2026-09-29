package com.game.lwjgl.internal.vulkan.pipeline;

import com.game.lwjgl.internal.vulkan.VulkanManager;
import org.lwjgl.vulkan.VkDevice;

public class VulkanPipelineManager {
  VulkanManager vulkanManager;
  public VulkanPipelineManager(VulkanManager vulkanManager){
    this.vulkanManager = vulkanManager;
  }
  VkDevice getVkDevice(){
    return vulkanManager.getVkDevice();
  }
  public void cleanupPipeline(){
  
  }
  public void cleanupDescriptors(){
  
  }
  public void cleanupShaderc(){
  
  }
}
