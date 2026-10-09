package com.game.lwjgl.vulkan;


import com.game.lwjgl.LwjglData;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.KHRSwapchain;
import org.lwjgl.vulkan.VK14;
import org.lwjgl.vulkan.VkClearValue;
import org.lwjgl.vulkan.VkCommandBuffer;
import org.lwjgl.vulkan.VkCommandBufferBeginInfo;
import org.lwjgl.vulkan.VkDependencyInfo;
import org.lwjgl.vulkan.VkImageMemoryBarrier2;
import org.lwjgl.vulkan.VkPresentInfoKHR;
import org.lwjgl.vulkan.VkRect2D;
import org.lwjgl.vulkan.VkRenderingAttachmentInfo;
import org.lwjgl.vulkan.VkRenderingInfo;
import org.lwjgl.vulkan.VkSubmitInfo;
import org.lwjgl.vulkan.VkViewport;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class VulkanRender {
  private VulkanRender() {
  }
  
  public static void drawFrame(LwjglData lwjglData) {
    try (MemoryStack stack = MemoryStack.stackPush()) {
      if (lwjglData.framebufferResized) VulkanSwapchain.recreate(lwjglData);
      if (lwjglData.swapchainWidth == 0 || lwjglData.swapchainHeight == 0) return;
      
      int f = lwjglData.currentFrame;
      VK14.vkWaitForFences(lwjglData.vkDevice, lwjglData.inFlightFences[f], true, Long.MAX_VALUE);
      
      VulkanThreadState.resetFrame(lwjglData, f);
      for (VulkanRenderBatchTask2D t : lwjglData.renderTasks) t.prepare(lwjglData.renderData, f);
      List<Future<VkCommandBuffer>> results;
      try {
        results = lwjglData.renderExecutor.invokeAll(lwjglData.renderTasks);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        return;
      }
      PointerBuffer secondaries = stack.mallocPointer(results.size());
      int n = 0;
      for (Future<VkCommandBuffer> future : results) {
        VkCommandBuffer commandBuffer;
        try {
          commandBuffer = future.get();
        } catch (InterruptedException | ExecutionException e) {
          throw new RuntimeException("render task failed", e);
        }
        if (commandBuffer != null) secondaries.put(n++, commandBuffer.address());
      }
      secondaries.limit(n);
      
      IntBuffer pImageIndex = stack.mallocInt(1);
      int result = KHRSwapchain.vkAcquireNextImageKHR(lwjglData.vkDevice, lwjglData.swapchain, Long.MAX_VALUE,
          lwjglData.imageAvailableSemaphores[f], VK14.VK_NULL_HANDLE, pImageIndex);
      if (result == KHRSwapchain.VK_ERROR_OUT_OF_DATE_KHR) {
        VulkanSwapchain.recreate(lwjglData);
        return;
      }
      int imageIndex = pImageIndex.get(0);
      VK14.vkResetFences(lwjglData.vkDevice, lwjglData.inFlightFences[f]);
      
      VkCommandBuffer commandBuffer = lwjglData.commandBuffers[f];
      VK14.vkResetCommandBuffer(commandBuffer, 0);
      VK14.vkBeginCommandBuffer(commandBuffer, VkCommandBufferBeginInfo.calloc(stack)
          .sType$Default().flags(VK14.VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT));
      
      transitionImageLayout(commandBuffer, lwjglData.swapchainImages[imageIndex],
          VK14.VK_IMAGE_LAYOUT_UNDEFINED, VK14.VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL, stack);
      
      VkClearValue clear = VkClearValue.calloc(stack);
      clear.color().float32(stack.floats(0f, 0f, 0f, 1f));
      VkRenderingAttachmentInfo.Buffer color = VkRenderingAttachmentInfo.calloc(1, stack)
          .sType$Default()
          .imageView(lwjglData.swapchainImageViews[imageIndex])
          .imageLayout(VK14.VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL)
          .loadOp(VK14.VK_ATTACHMENT_LOAD_OP_CLEAR)
          .storeOp(VK14.VK_ATTACHMENT_STORE_OP_STORE)
          .clearValue(clear);
      VkRenderingInfo renderingInfo = VkRenderingInfo.calloc(stack)
          .sType$Default()
          .flags(VK14.VK_RENDERING_CONTENTS_SECONDARY_COMMAND_BUFFERS_BIT)
          .renderArea(r -> r.extent(e -> e.set(lwjglData.swapchainWidth, lwjglData.swapchainHeight)))
          .layerCount(1)
          .pColorAttachments(color);
      
      VK14.vkCmdBeginRendering(commandBuffer, renderingInfo);
      if (n > 0) VK14.vkCmdExecuteCommands(commandBuffer, secondaries);
      VK14.vkCmdEndRendering(commandBuffer);
      
      transitionImageLayout(commandBuffer, lwjglData.swapchainImages[imageIndex],
          VK14.VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL, KHRSwapchain.VK_IMAGE_LAYOUT_PRESENT_SRC_KHR, stack);
      VK14.vkEndCommandBuffer(commandBuffer);
      
      long renderDone = lwjglData.renderFinishedSemaphores[imageIndex];
      VkSubmitInfo submit = VkSubmitInfo.calloc(stack)
          .sType$Default()
          .waitSemaphoreCount(1)
          .pWaitSemaphores(stack.longs(lwjglData.imageAvailableSemaphores[f]))
          .pWaitDstStageMask(stack.ints(VK14.VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT))
          .pCommandBuffers(stack.pointers(commandBuffer))
          .pSignalSemaphores(stack.longs(renderDone));
      VK14.vkQueueSubmit(lwjglData.graphicsQueue, submit, lwjglData.inFlightFences[f]);
      
      VkPresentInfoKHR present = VkPresentInfoKHR.calloc(stack)
          .sType(KHRSwapchain.VK_STRUCTURE_TYPE_PRESENT_INFO_KHR)
          .pWaitSemaphores(stack.longs(renderDone))
          .swapchainCount(1)
          .pSwapchains(stack.longs(lwjglData.swapchain))
          .pImageIndices(pImageIndex);
      int presentKHR = KHRSwapchain.vkQueuePresentKHR(lwjglData.presentQueue, present);
      if (presentKHR == KHRSwapchain.VK_ERROR_OUT_OF_DATE_KHR || presentKHR == KHRSwapchain.VK_SUBOPTIMAL_KHR) {
        lwjglData.framebufferResized = true;
      }
      
      lwjglData.currentFrame = (f + 1) % lwjglData.MAX_FRAMES_IN_FLIGHT;
    }
  }
  
  static void bindCommonState(VkCommandBuffer commandBuffer, LwjglData lwjglData, MemoryStack stack) {
    VK14.vkCmdBindPipeline(commandBuffer, VK14.VK_PIPELINE_BIND_POINT_GRAPHICS, lwjglData.vulkanPipelines.getLong(PipelineTypes.TWO_D));
    VK14.vkCmdBindDescriptorSets(commandBuffer, VK14.VK_PIPELINE_BIND_POINT_GRAPHICS,
        lwjglData.pipelineLayout2D, 0, stack.longs(lwjglData.descriptorSet), null);
    
    float w = lwjglData.swapchainWidth, h = lwjglData.swapchainHeight;
    float scale = Math.min(w / 1920f, h / 1080f);
    ByteBuffer pc = stack.calloc(24);
    pc.putFloat(0, w).putFloat(4, h);
    pc.putFloat(8, (w - 1920f * scale) * 0.5f).putFloat(12, (h - 1080f * scale) * 0.5f);
    pc.putFloat(16, scale).putFloat(20, 1f);
    VK14.vkCmdPushConstants(commandBuffer, lwjglData.pipelineLayout2D, VK14.VK_SHADER_STAGE_VERTEX_BIT, 0, pc);
    
    VkViewport.Buffer vp = VkViewport.calloc(1, stack).x(0f).y(0f).width(w).height(h).minDepth(0f).maxDepth(1f);
    VK14.vkCmdSetViewport(commandBuffer, 0, vp);
    VkRect2D.Buffer sc = VkRect2D.calloc(1, stack)
        .offset(o -> o.set(0, 0)).extent(e -> e.set(lwjglData.swapchainWidth, lwjglData.swapchainHeight));
    VK14.vkCmdSetScissor(commandBuffer, 0, sc);
  }
  
  private static void transitionImageLayout(
      VkCommandBuffer commandBuffer,
      long image,
      int oldLayout,
      int newLayout,
      MemoryStack stack
  ) {
    long srcStageMask;
    long srcAccessMask;
    long dstStageMask;
    long dstAccessMask;
    
    if (oldLayout == VK14.VK_IMAGE_LAYOUT_UNDEFINED && newLayout == VK14.VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL) {
      srcStageMask = VK14.VK_PIPELINE_STAGE_2_TOP_OF_PIPE_BIT;
      srcAccessMask = 0;
      dstStageMask = VK14.VK_PIPELINE_STAGE_2_COLOR_ATTACHMENT_OUTPUT_BIT;
      dstAccessMask = VK14.VK_ACCESS_2_COLOR_ATTACHMENT_WRITE_BIT;
    } else if (oldLayout == VK14.VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL && newLayout == KHRSwapchain.VK_IMAGE_LAYOUT_PRESENT_SRC_KHR) {
      srcStageMask = VK14.VK_PIPELINE_STAGE_2_COLOR_ATTACHMENT_OUTPUT_BIT;
      srcAccessMask = VK14.VK_ACCESS_2_COLOR_ATTACHMENT_WRITE_BIT;
      dstStageMask = VK14.VK_PIPELINE_STAGE_2_BOTTOM_OF_PIPE_BIT;
      dstAccessMask = 0;
    } else {
      throw new IllegalArgumentException("unsupported layout transition");
    }
    
    VkImageMemoryBarrier2.Buffer barrier = VkImageMemoryBarrier2.calloc(1, stack)
        .sType(VK14.VK_STRUCTURE_TYPE_IMAGE_MEMORY_BARRIER_2)
        .srcStageMask(srcStageMask)
        .srcAccessMask(srcAccessMask)
        .dstStageMask(dstStageMask)
        .dstAccessMask(dstAccessMask)
        .oldLayout(oldLayout)
        .newLayout(newLayout)
        .srcQueueFamilyIndex(VK14.VK_QUEUE_FAMILY_IGNORED)
        .dstQueueFamilyIndex(VK14.VK_QUEUE_FAMILY_IGNORED)
        .image(image)
        .subresourceRange(r -> r
            .aspectMask(VK14.VK_IMAGE_ASPECT_COLOR_BIT)
            .baseMipLevel(0)
            .levelCount(1)
            .baseArrayLayer(0)
            .layerCount(1)
        );
    
    VkDependencyInfo dependencyInfo = VkDependencyInfo.calloc(stack)
        .sType(VK14.VK_STRUCTURE_TYPE_DEPENDENCY_INFO)
        .pImageMemoryBarriers(barrier);
    
    VK14.vkCmdPipelineBarrier2(commandBuffer, dependencyInfo);
  }
}
