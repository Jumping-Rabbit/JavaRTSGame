package com.game.lwjgl.Vulkan;


import com.game.lwjgl.LwjglData;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class VulkanRender {
  private VulkanRender(){}
  
  public static void drawFrame(LwjglData lwjglData) {
    try (MemoryStack stack = MemoryStack.stackPush()) {
      if (lwjglData.framebufferResized) {
        VulkanSwapchain.recreate(lwjglData);
      }
      if (lwjglData.swapchainWidth == 0 || lwjglData.swapchainHeight == 0) return;
      
      VK14.vkWaitForFences(lwjglData.vkDevice, lwjglData.inFlightFences[lwjglData.currentFrame], true, Long.MAX_VALUE);
      
      IntBuffer pImageIndex = stack.mallocInt(1);
      int result = KHRSwapchain.vkAcquireNextImageKHR(
          lwjglData.vkDevice,
          lwjglData.swapchain,
          Long.MAX_VALUE,
          lwjglData.imageAvailableSemaphores[lwjglData.currentFrame],
          VK14.VK_NULL_HANDLE,
          pImageIndex
      );
      
      if (result == KHRSwapchain.VK_ERROR_OUT_OF_DATE_KHR) {
        VulkanSwapchain.recreate(lwjglData);
        return;
      }
      
      int imageIndex = pImageIndex.get(0);
      
      VK14.vkResetFences(lwjglData.vkDevice, lwjglData.inFlightFences[lwjglData.currentFrame]);
      
      VkCommandBuffer cmd = lwjglData.commandBuffers[lwjglData.currentFrame];
      VK14.vkResetCommandBuffer(cmd, 0);
      
      VkCommandBufferBeginInfo beginInfo = VkCommandBufferBeginInfo.calloc(stack)
          .sType(VK14.VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO);
      
      VK14.vkBeginCommandBuffer(cmd, beginInfo);
      
      transitionImageLayout(
          cmd,
          lwjglData.swapchainImages[imageIndex],
          VK14.VK_IMAGE_LAYOUT_UNDEFINED,
          VK14.VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL,
          stack
      );
      
      VkClearValue clearColor = VkClearValue.calloc(stack);
      clearColor.color().float32(stack.floats(0.0f, 0.0f, 0.0f, 1.0f));
      
      VkRenderingAttachmentInfo.Buffer colorAttachment = VkRenderingAttachmentInfo.calloc(1, stack)
          .sType(VK14.VK_STRUCTURE_TYPE_RENDERING_ATTACHMENT_INFO)
          .imageView(lwjglData.swapchainImageViews[imageIndex])
          .imageLayout(VK14.VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL)
          .loadOp(VK14.VK_ATTACHMENT_LOAD_OP_CLEAR)
          .storeOp(VK14.VK_ATTACHMENT_STORE_OP_STORE)
          .clearValue(clearColor);
      
      VkRenderingInfo renderingInfo = VkRenderingInfo.calloc(stack)
          .sType(VK14.VK_STRUCTURE_TYPE_RENDERING_INFO)
          .renderArea(r -> r.extent(e -> e.set(lwjglData.swapchainWidth, lwjglData.swapchainHeight)))
          .layerCount(1)
          .pColorAttachments(colorAttachment);
      
      VK14.vkCmdBeginRendering(cmd, renderingInfo);
      
      VK14.vkCmdBindPipeline(cmd, VK14.VK_PIPELINE_BIND_POINT_GRAPHICS, lwjglData.vulkanPipelines.getLong(PipelineTypes.TWO_D));
      
      VK14.vkCmdBindDescriptorSets(cmd, VK14.VK_PIPELINE_BIND_POINT_GRAPHICS,
          lwjglData.pipelineLayout2D, 0, stack.longs(lwjglData.descriptorSet), null);
      VK14.vkCmdBindVertexBuffers(cmd, 0, stack.longs(lwjglData.vertexBuffer), stack.longs(0));
      
      ByteBuffer pc = stack.calloc(24);
      pc.putFloat(0, lwjglData.swapchainWidth).putFloat(4, lwjglData.swapchainHeight);
      pc.putFloat(8, 0f).putFloat(12, 0f);
      pc.putFloat(16, lwjglData.swapchainWidth / 1920f);
      pc.putFloat(20, 1f);
      VK14.vkCmdPushConstants(cmd, lwjglData.pipelineLayout2D, VK14.VK_SHADER_STAGE_VERTEX_BIT, 0, pc);
      
      try (MemoryStack innerStack = MemoryStack.stackPush()) {
        VkViewport.Buffer viewport = VkViewport.calloc(1, innerStack)
            .x(0.0f)
            .y(0.0f)
            .width((float) lwjglData.swapchainWidth)
            .height((float) lwjglData.swapchainHeight)
            .minDepth(0.0f)
            .maxDepth(1.0f);
        VK14.vkCmdSetViewport(cmd, 0, viewport);
        
        VkRect2D.Buffer scissor = VkRect2D.calloc(1, innerStack)
            .offset(o -> o.set(0, 0))
            .extent(e -> e.set(lwjglData.swapchainWidth, lwjglData.swapchainHeight));
        VK14.vkCmdSetScissor(cmd, 0, scissor);
      }
//      VK14.vkCmdDraw(cmd, 4, 1, 0, 0);
//      VK14.vkCmdBindVertexBuffers(cmd, 0, stack.longs(vertexBuffer), stack.longs(0));//TODO: make a buffer ig
      
      VK14.vkCmdEndRendering(cmd);
      
      transitionImageLayout(
          cmd,
          lwjglData.swapchainImages[imageIndex],
          VK14.VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL,
          KHRSwapchain.VK_IMAGE_LAYOUT_PRESENT_SRC_KHR,
          stack
      );
      
      VK14.vkEndCommandBuffer(cmd);
      
      VkSubmitInfo submitInfo = VkSubmitInfo.calloc(stack)
          .sType(VK14.VK_STRUCTURE_TYPE_SUBMIT_INFO)
          .waitSemaphoreCount(1)
          .pWaitSemaphores(stack.longs(lwjglData.imageAvailableSemaphores[lwjglData.currentFrame]))
          .pWaitDstStageMask(stack.ints(VK14.VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT))
          .pCommandBuffers(stack.pointers(cmd))
          .pSignalSemaphores(stack.longs(lwjglData.renderFinishedSemaphores[lwjglData.currentFrame]));
      
      VK14.vkQueueSubmit(lwjglData.graphicsQueue, submitInfo, lwjglData.inFlightFences[lwjglData.currentFrame]);
      
      VkPresentInfoKHR presentInfo = VkPresentInfoKHR.calloc(stack)
          .sType(KHRSwapchain.VK_STRUCTURE_TYPE_PRESENT_INFO_KHR)
          .pWaitSemaphores(stack.longs(lwjglData.renderFinishedSemaphores[lwjglData.currentFrame]))
          .swapchainCount(1)
          .pSwapchains(stack.longs(lwjglData.swapchain))
          .pImageIndices(pImageIndex);
      
      int presentResult = KHRSwapchain.vkQueuePresentKHR(lwjglData.presentQueue, presentInfo);
      if (presentResult == KHRSwapchain.VK_ERROR_OUT_OF_DATE_KHR || presentResult == KHRSwapchain.VK_SUBOPTIMAL_KHR) {
        lwjglData.framebufferResized = true;
      }
    }
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
      throw new IllegalArgumentException("Unsupported layout transition");
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
