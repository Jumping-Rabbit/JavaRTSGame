package com.game.lwjgl.internal.vulkan.pipeline;

import com.game.lwjgl.internal.vulkan.VulkanManager;
import org.lwjgl.vulkan.VkDevice;

public class VulkanPipelineManager {
  VulkanManager vulkanManager;
  private final VulkanPipeline vulkanPipeline;
  private final VulkanComputePipeline vulkanComputePipeline;
  private final VulkanDescriptors vulkanDescriptors;
  private final VulkanShaderc vulkanShaderc;
  
  public VulkanPipelineManager(VulkanManager vulkanManager){
    this.vulkanManager = vulkanManager;
    vulkanPipeline = new VulkanPipeline(this);
    vulkanComputePipeline = new VulkanComputePipeline(this);
    vulkanDescriptors = new VulkanDescriptors(this);
    vulkanShaderc = new VulkanShaderc(this);
    vulkanShaderc.compile();
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
