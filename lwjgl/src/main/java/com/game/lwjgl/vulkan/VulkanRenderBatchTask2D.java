package com.game.lwjgl.vulkan;

import com.game.lwjgl.LwjglData;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK14;
import org.lwjgl.vulkan.VkCommandBuffer;
import org.lwjgl.vulkan.VkCommandBufferBeginInfo;
import org.lwjgl.vulkan.VkCommandBufferInheritanceInfo;
import org.lwjgl.vulkan.VkCommandBufferInheritanceRenderingInfo;

import java.util.concurrent.Callable;

public final class VulkanRenderBatchTask2D implements Callable<VkCommandBuffer> {
  private final LwjglData lwjglData;
  private final Kind kind;
  private final int index, chunks;
  private final int firstInstance;
  private final Batch2D batch;
  private RenderData renderData;
  private int frame;
  VulkanRenderBatchTask2D(LwjglData lwjglData, Kind kind, int index, int chunks, int firstInstance, Batch2D batch) {
    this.lwjglData = lwjglData;
    this.kind = kind;
    this.index = index;
    this.chunks = chunks;
    this.firstInstance = firstInstance;
    this.batch = batch;
  }
  
  public void prepare(RenderData renderData, int frame) {
    this.renderData = renderData;
    this.frame = frame;
  }
  
  private int from(int n) {
    return (int) ((long) n * index / chunks);
  }
  
  private int to(int n) {
    return (int) ((long) n * (index + 1) / chunks);
  }
  
  @Override
  public VkCommandBuffer call() {
    batch.reset();
    switch (kind) {
      case ENTITIES -> {
        int n = renderData.activeIndex.size();
        Draw2D.entities(batch, renderData, from(n), to(n));
      }
      case EXTRAS -> Draw2D.extras(batch, renderData);
      case UI -> {
        int n = renderData.UIType.size();
        Draw2D.ui(batch, renderData, lwjglData.bakedAtlases, from(n), to(n));
      }
    }
    if (batch.count == 0) return null;
    
    VkCommandBuffer commandBuffer = VulkanThreadState.acquireSecondary(lwjglData, frame);
    try (MemoryStack stack = MemoryStack.stackPush()) {
      VkCommandBufferInheritanceRenderingInfo renderingInfo = VkCommandBufferInheritanceRenderingInfo.calloc(stack)
          .sType$Default()
          .pColorAttachmentFormats(stack.ints(lwjglData.swapchainImageFormat))
          .depthAttachmentFormat(VK14.VK_FORMAT_UNDEFINED)
          .stencilAttachmentFormat(VK14.VK_FORMAT_UNDEFINED)
          .rasterizationSamples(VK14.VK_SAMPLE_COUNT_1_BIT);
      VkCommandBufferInheritanceInfo inheritanceInfo = VkCommandBufferInheritanceInfo.calloc(stack)
          .sType$Default()
          .pNext(renderingInfo.address());
      VkCommandBufferBeginInfo beginInfo = VkCommandBufferBeginInfo.calloc(stack)
          .sType$Default()
          .flags(VK14.VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT
              | VK14.VK_COMMAND_BUFFER_USAGE_RENDER_PASS_CONTINUE_BIT)
          .pInheritanceInfo(inheritanceInfo);
      VK14.vkBeginCommandBuffer(commandBuffer, beginInfo);
      
      VulkanRender.bindCommonState(commandBuffer, lwjglData, stack);
      VK14.vkCmdBindVertexBuffers(commandBuffer, 0, stack.longs(lwjglData.instanceBuffer), stack.longs(0));
      VK14.vkCmdDraw(commandBuffer, 4, batch.count, 0, firstInstance);
      
      VK14.vkEndCommandBuffer(commandBuffer);
    }
    return commandBuffer;
  }
  
  public enum Kind {ENTITIES, EXTRAS, UI}
}