package com.game.lwjgl.internal.vulkan.pipeline;

import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;

class VulkanPipeline {
  private VulkanPipelineManager vulkanPipelineManager;
  private Object2LongOpenHashMap<PipelineTypes> vulkanPipelines;
  
  VulkanPipeline(VulkanPipelineManager vulkanPipelineManager){
    this.vulkanPipelineManager = vulkanPipelineManager;
    vulkanPipelines = new Object2LongOpenHashMap<>();
//    for (PipelineTypes pipelineType : PipelineTypes.values()){
//      vulkanPipelines.put(pipelineType, createGraphicsPipeline(
//          vulkanPipelineManager.getVkDevice(), pipelineType.vert
//      ))
//    }
  }
  
  
  void cleanup(VkDevice vkDevice){
    for (long pipeline : vulkanPipelines.values()) {
      VK14.vkDestroyPipeline(vkDevice, pipeline, null);
    }
    vulkanPipelines.clear();
  }
  
  
  long createGraphicsPipeline(
      VkDevice device,
      long vertShaderModule,
      long fragShaderModule,
      long pipelineLayout,
      int colorAttachmentFormat,
      int depthAttachmentFormat) {
    
    try (MemoryStack stack = MemoryStack.stackPush()) {
      ByteBuffer entryPoint = stack.UTF8("main");
      
      VkPipelineShaderStageCreateInfo.Buffer shaderStages = VkPipelineShaderStageCreateInfo.calloc(2, stack);
      
      shaderStages.get(0).sType$Default()
          .stage(VK14.VK_SHADER_STAGE_VERTEX_BIT)
          .module(vertShaderModule)
          .pName(entryPoint);
      
      shaderStages.get(1).sType$Default()
          .stage(VK14.VK_SHADER_STAGE_FRAGMENT_BIT)
          .module(fragShaderModule)
          .pName(entryPoint);
      
      VkPipelineVertexInputStateCreateInfo vertexInputInfo = VkPipelineVertexInputStateCreateInfo.calloc(stack)
          .sType$Default();
      
      VkPipelineInputAssemblyStateCreateInfo inputAssembly = VkPipelineInputAssemblyStateCreateInfo.calloc(stack)
          .sType$Default()
          .topology(VK14.VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST);
      
      VkPipelineViewportStateCreateInfo viewportState = VkPipelineViewportStateCreateInfo.calloc(stack)
          .sType$Default()
          .viewportCount(1)
          .scissorCount(1);
      
      VkPipelineDynamicStateCreateInfo dynamicState = VkPipelineDynamicStateCreateInfo.calloc(stack)
          .sType$Default()
          .pDynamicStates(stack.ints(VK14.VK_DYNAMIC_STATE_VIEWPORT, VK14.VK_DYNAMIC_STATE_SCISSOR));
      
      VkPipelineRasterizationStateCreateInfo rasterizer = VkPipelineRasterizationStateCreateInfo.calloc(stack)
          .sType$Default()
          .polygonMode(VK14.VK_POLYGON_MODE_FILL)
          .cullMode(VK14.VK_CULL_MODE_BACK_BIT)
          .frontFace(VK14.VK_FRONT_FACE_CLOCKWISE)
          .lineWidth(1.0f);
      
      VkPipelineMultisampleStateCreateInfo multisampling = VkPipelineMultisampleStateCreateInfo.calloc(stack)
          .sType$Default()
          .rasterizationSamples(VK14.VK_SAMPLE_COUNT_1_BIT);
      
      VkPipelineColorBlendAttachmentState.Buffer colorBlendAttachment = VkPipelineColorBlendAttachmentState.calloc(1, stack);
      colorBlendAttachment.get(0)
          .colorWriteMask(VK14.VK_COLOR_COMPONENT_R_BIT | VK14.VK_COLOR_COMPONENT_G_BIT |
              VK14.VK_COLOR_COMPONENT_B_BIT | VK14.VK_COLOR_COMPONENT_A_BIT)
          .blendEnable(false);
      
      VkPipelineColorBlendStateCreateInfo colorBlending = VkPipelineColorBlendStateCreateInfo.calloc(stack)
          .sType$Default()
          .pAttachments(colorBlendAttachment);
      
      VkPipelineRenderingCreateInfo pipelineRenderingInfo = VkPipelineRenderingCreateInfo.calloc(stack)
          .sType$Default()
          .pColorAttachmentFormats(stack.ints(colorAttachmentFormat))
          .depthAttachmentFormat(depthAttachmentFormat);
      
      VkGraphicsPipelineCreateInfo.Buffer pipelineInfo = VkGraphicsPipelineCreateInfo.calloc(1, stack);
      pipelineInfo.get(0).sType$Default()
          .pNext(pipelineRenderingInfo.address())
          .pStages(shaderStages)
          .pVertexInputState(vertexInputInfo)
          .pInputAssemblyState(inputAssembly)
          .pViewportState(viewportState)
          .pDynamicState(dynamicState)
          .pRasterizationState(rasterizer)
          .pMultisampleState(multisampling)
          .pColorBlendState(colorBlending)
          .layout(pipelineLayout);
      
      LongBuffer pPipeline = stack.mallocLong(1);
      int result = VK14.vkCreateGraphicsPipelines(device, VK14.VK_NULL_HANDLE, pipelineInfo, null, pPipeline);
      
      if (result != VK14.VK_SUCCESS) {
        throw new RuntimeException("Failed to create graphics pipeline! Error code: " + result);
      }
      
      return pPipeline.get(0);
    }
  }
}
