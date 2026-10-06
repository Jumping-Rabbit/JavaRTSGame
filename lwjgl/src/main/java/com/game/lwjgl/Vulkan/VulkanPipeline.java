package com.game.lwjgl.Vulkan;



import com.game.lwjgl.LwjglData;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;

public class VulkanPipeline {
  
  private VulkanPipeline() {}
  
  public static void makePipelines(LwjglData lwjglData) {
    long pipelineLayout = make2DPipelineLayout(lwjglData);
    for (PipelineTypes pipelineType : PipelineTypes.values()) {
      lwjglData.vulkanPipelines.put(pipelineType, createGraphicsPipeline(
          lwjglData.vkDevice,
          lwjglData.shadersToLongHandle.getLong(pipelineType.getVert()),
          lwjglData.shadersToLongHandle.getLong(pipelineType.getFrag()),
          pipelineLayout,
          lwjglData.swapchainImageFormat,
          pipelineType.getDepthFormat()
      ));
    }
    
    lwjglData.pipelineLayoutCollision = makeCollisionPipelineLayout(lwjglData);
    for (ComputePipelineTypes computePipelineType : ComputePipelineTypes.values()) {
      lwjglData.vulkanComputePipelines.put(computePipelineType, createComputePipeline(
          lwjglData.vkDevice,
          lwjglData.pipelineLayoutCollision,
          lwjglData.shadersToLongHandle.getLong(computePipelineType.getShader())
      ));
    }
  }
  
  private static long make2DPipelineLayout(LwjglData lwjglData) {
    try (MemoryStack stack = MemoryStack.stackPush()) {
      lwjglData.descriptorSetLayout2D = VulkanDescriptors.create2DDescriptorSetLayout(lwjglData);
      LongBuffer pSetLayouts = stack.longs(lwjglData.descriptorSetLayout2D);
      
      VkPushConstantRange.Buffer pPushConstantRanges = VkPushConstantRange.calloc(1, stack)
          .stageFlags(VK14.VK_SHADER_STAGE_VERTEX_BIT)
          .offset(0)
          .size(24);
      
      VkPipelineLayoutCreateInfo layoutInfo = VkPipelineLayoutCreateInfo.calloc(stack)
          .sType(VK14.VK_STRUCTURE_TYPE_PIPELINE_LAYOUT_CREATE_INFO)
          .pSetLayouts(pSetLayouts)
          .pPushConstantRanges(pPushConstantRanges);
      
      long[] pPipelineLayout = new long[1];
      if (VK14.vkCreatePipelineLayout(lwjglData.vkDevice, layoutInfo, null, pPipelineLayout) != VK14.VK_SUCCESS) {
        throw new RuntimeException("Failed to create pipeline layout");
      }
      lwjglData.pipelineLayout2D = pPipelineLayout[0];
      return pPipelineLayout[0];
    }
    
    
  }
  
  private static long makeCollisionPipelineLayout(LwjglData lwjglData) {
    try (MemoryStack stack = MemoryStack.stackPush()) {
      lwjglData.descriptorSetLayoutCollision = VulkanDescriptors.createCollisionDescriptorSetLayout(lwjglData);
      LongBuffer pSetLayouts = stack.longs(lwjglData.descriptorSetLayoutCollision);
      VkPushConstantRange.Buffer pPushConstantRanges = null;
      
      VkPipelineLayoutCreateInfo layoutInfo = VkPipelineLayoutCreateInfo.calloc(stack)
          .sType(VK14.VK_STRUCTURE_TYPE_PIPELINE_LAYOUT_CREATE_INFO)
          .pSetLayouts(pSetLayouts)
          .pPushConstantRanges(pPushConstantRanges);
      
      long[] pPipelineLayout = new long[1];
      if (VK14.vkCreatePipelineLayout(lwjglData.vkDevice, layoutInfo, null, pPipelineLayout) != VK14.VK_SUCCESS) {
        throw new RuntimeException("Failed to create pipeline layout");
      }
      return pPipelineLayout[0];
    }
  }
  
  public static void cleanupPipelines(LwjglData lwjglData) {
    for (long pipeline : lwjglData.vulkanPipelines.values()) {
      VK14.vkDestroyPipeline(lwjglData.vkDevice, pipeline, null);
    }
    lwjglData.vulkanPipelines.clear();
    
    for (long pipeline : lwjglData.vulkanComputePipelines.values()) {
      VK14.vkDestroyPipeline(lwjglData.vkDevice, pipeline, null);
    }
    lwjglData.vulkanComputePipelines.clear();
    
    VK14.vkDestroyPipelineLayout(lwjglData.vkDevice, lwjglData.pipelineLayout2D, null);
    VK14.vkDestroyPipelineLayout(lwjglData.vkDevice, lwjglData.pipelineLayoutCollision, null);
  }
  
  
  private static long createGraphicsPipeline(
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
      
      VkPipelineInputAssemblyStateCreateInfo inputAssembly = VkPipelineInputAssemblyStateCreateInfo.calloc(stack)
          .sType$Default()
          .topology(VK14.VK_PRIMITIVE_TOPOLOGY_TRIANGLE_STRIP);
      
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
      
      VkVertexInputBindingDescription.Buffer bindings = VkVertexInputBindingDescription.calloc(1, stack);
      bindings.get(0).binding(0).stride(40).inputRate(VK14.VK_VERTEX_INPUT_RATE_INSTANCE);
      
      VkVertexInputAttributeDescription.Buffer attrs = VkVertexInputAttributeDescription.calloc(4, stack);
      attrs.get(0).location(0).binding(0).format(VK14.VK_FORMAT_R32G32B32A32_SFLOAT).offset(0);
      attrs.get(1).location(1).binding(0).format(VK14.VK_FORMAT_R32G32B32A32_SFLOAT).offset(16);
      attrs.get(2).location(2).binding(0).format(VK14.VK_FORMAT_R8G8B8A8_UNORM).offset(32);
      attrs.get(3).location(3).binding(0).format(VK14.VK_FORMAT_R32_UINT).offset(36);
      
      
      VkPipelineVertexInputStateCreateInfo vertexInputInfo = VkPipelineVertexInputStateCreateInfo.calloc(stack)
          .sType$Default()
          .pVertexBindingDescriptions(bindings)
          .pVertexAttributeDescriptions(attrs);
      
      colorBlendAttachment.get(0)
          .colorWriteMask(VK14.VK_COLOR_COMPONENT_R_BIT | VK14.VK_COLOR_COMPONENT_G_BIT
              | VK14.VK_COLOR_COMPONENT_B_BIT | VK14.VK_COLOR_COMPONENT_A_BIT)
          .blendEnable(true)
          .srcColorBlendFactor(VK14.VK_BLEND_FACTOR_ONE)
          .dstColorBlendFactor(VK14.VK_BLEND_FACTOR_ONE_MINUS_SRC_ALPHA)
          .colorBlendOp(VK14.VK_BLEND_OP_ADD)
          .srcAlphaBlendFactor(VK14.VK_BLEND_FACTOR_ONE)
          .dstAlphaBlendFactor(VK14.VK_BLEND_FACTOR_ONE_MINUS_SRC_ALPHA)
          .alphaBlendOp(VK14.VK_BLEND_OP_ADD);
      
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
        throw new RuntimeException("Failed to create graphics pipeline " + result);
      }
      
      return pPipeline.get(0);
    }
  }
  
  private static long createComputePipeline(VkDevice device, long pipelineLayout, long shaderModule) {
    try (MemoryStack stack = MemoryStack.stackPush()) {
      
      VkPipelineShaderStageCreateInfo stageInfo = VkPipelineShaderStageCreateInfo.calloc(stack)
          .sType(VK14.VK_STRUCTURE_TYPE_PIPELINE_SHADER_STAGE_CREATE_INFO)
          .stage(VK14.VK_SHADER_STAGE_COMPUTE_BIT)
          .module(shaderModule)
          .pName(stack.UTF8("main"));
      
      VkComputePipelineCreateInfo.Buffer pipelineInfo = VkComputePipelineCreateInfo.calloc(1, stack)
          .sType(VK14.VK_STRUCTURE_TYPE_COMPUTE_PIPELINE_CREATE_INFO)
          .stage(stageInfo)
          .layout(pipelineLayout);
      
      long[] pPipeline = new long[1];
      
      int result = VK14.vkCreateComputePipelines(device, VK14.VK_NULL_HANDLE, pipelineInfo, null, pPipeline);
      
      if (result != VK14.VK_SUCCESS) {
        throw new RuntimeException("Failed to create compute pipeline: " + result);
      }
      
      return pPipeline[0];
    }
  }
}
