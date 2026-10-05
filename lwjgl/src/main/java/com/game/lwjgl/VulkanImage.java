package com.game.lwjgl;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;

class VulkanImage {
  static void makeWhiteTexture(LwjglData lwjglData) {
    try (MemoryStack stack = MemoryStack.stackPush()) {
      int format = VK14.VK_FORMAT_R8G8B8A8_UNORM;
      
      long[] staging = VulkanBuffers.createBuffer(lwjglData, 4, VK14.VK_BUFFER_USAGE_TRANSFER_SRC_BIT,
          VK14.VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT | VK14.VK_MEMORY_PROPERTY_HOST_COHERENT_BIT);
      ByteBuffer pixel = stack.bytes((byte) 255, (byte) 255, (byte) 255, (byte) 255);
      VulkanBuffers.upload(lwjglData, staging[1], pixel);
      
      VkImageCreateInfo imageInfo = VkImageCreateInfo.calloc(stack)
          .sType$Default()
          .imageType(VK14.VK_IMAGE_TYPE_2D)
          .format(format)
          .mipLevels(1)
          .arrayLayers(1)
          .samples(VK14.VK_SAMPLE_COUNT_1_BIT)
          .tiling(VK14.VK_IMAGE_TILING_OPTIMAL)
          .usage(VK14.VK_IMAGE_USAGE_TRANSFER_DST_BIT | VK14.VK_IMAGE_USAGE_SAMPLED_BIT)
          .sharingMode(VK14.VK_SHARING_MODE_EXCLUSIVE)
          .initialLayout(VK14.VK_IMAGE_LAYOUT_UNDEFINED);
      imageInfo.extent().set(1, 1, 1);
      
      LongBuffer pImage = stack.mallocLong(1);
      if (VK14.vkCreateImage(lwjglData.vkDevice, imageInfo, null, pImage) != VK14.VK_SUCCESS) {
        throw new RuntimeException("Failed to create texture image");
      }
      long image = pImage.get(0);
      
      VkMemoryRequirements req = VkMemoryRequirements.malloc(stack);
      VK14.vkGetImageMemoryRequirements(lwjglData.vkDevice, image, req);
      VkMemoryAllocateInfo alloc = VkMemoryAllocateInfo.calloc(stack)
          .sType$Default()
          .allocationSize(req.size())
          .memoryTypeIndex(VulkanBuffers.findMemoryType(lwjglData, req.memoryTypeBits(), VK14.VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT));
      LongBuffer pMem = stack.mallocLong(1);
      if (VK14.vkAllocateMemory(lwjglData.vkDevice, alloc, null, pMem) != VK14.VK_SUCCESS) {
        throw new RuntimeException("Failed to allocate texture memory");
      }
      VK14.vkBindImageMemory(lwjglData.vkDevice, image, pMem.get(0), 0);
      
      VkCommandBufferAllocateInfo cai = VkCommandBufferAllocateInfo.calloc(stack)
          .sType$Default()
          .commandPool(lwjglData.commandPool)
          .level(VK14.VK_COMMAND_BUFFER_LEVEL_PRIMARY)
          .commandBufferCount(1);
      PointerBuffer pCmd = stack.mallocPointer(1);
      VK14.vkAllocateCommandBuffers(lwjglData.vkDevice, cai, pCmd);
      VkCommandBuffer cmd = new VkCommandBuffer(pCmd.get(0), lwjglData.vkDevice);
      
      VK14.vkBeginCommandBuffer(cmd, VkCommandBufferBeginInfo.calloc(stack)
          .sType$Default()
          .flags(VK14.VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT));
      
      barrier(cmd, image, VK14.VK_IMAGE_LAYOUT_UNDEFINED, VK14.VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL,
          VK14.VK_PIPELINE_STAGE_2_TOP_OF_PIPE_BIT, 0,
          VK14.VK_PIPELINE_STAGE_2_TRANSFER_BIT, VK14.VK_ACCESS_2_TRANSFER_WRITE_BIT, stack);
      
      VkBufferImageCopy.Buffer region = VkBufferImageCopy.calloc(1, stack)
          .imageSubresource(s -> s.aspectMask(VK14.VK_IMAGE_ASPECT_COLOR_BIT)
              .mipLevel(0).baseArrayLayer(0).layerCount(1))
          .imageExtent(e -> e.set(1, 1, 1));
      VK14.vkCmdCopyBufferToImage(cmd, staging[0], image, VK14.VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL, region);
      
      barrier(cmd, image, VK14.VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL, VK14.VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL,
          VK14.VK_PIPELINE_STAGE_2_TRANSFER_BIT, VK14.VK_ACCESS_2_TRANSFER_WRITE_BIT,
          VK14.VK_PIPELINE_STAGE_2_FRAGMENT_SHADER_BIT, VK14.VK_ACCESS_2_SHADER_SAMPLED_READ_BIT, stack);
      
      VK14.vkEndCommandBuffer(cmd);
      VkSubmitInfo submit = VkSubmitInfo.calloc(stack)
          .sType$Default()
          .pCommandBuffers(stack.pointers(cmd));
      VK14.vkQueueSubmit(lwjglData.graphicsQueue, submit, VK14.VK_NULL_HANDLE);
      VK14.vkQueueWaitIdle(lwjglData.graphicsQueue);
      VK14.vkFreeCommandBuffers(lwjglData.vkDevice, lwjglData.commandPool, pCmd);
      
      VK14.vkDestroyBuffer(lwjglData.vkDevice, staging[0], null);
      VK14.vkFreeMemory(lwjglData.vkDevice, staging[1], null);
      
      VkImageViewCreateInfo viewInfo = VkImageViewCreateInfo.calloc(stack)
          .sType$Default()
          .image(image)
          .viewType(VK14.VK_IMAGE_VIEW_TYPE_2D_ARRAY)
          .format(format)
          .subresourceRange(r -> r.aspectMask(VK14.VK_IMAGE_ASPECT_COLOR_BIT)
              .baseMipLevel(0).levelCount(1).baseArrayLayer(0).layerCount(1));
      LongBuffer pView = stack.mallocLong(1);
      if (VK14.vkCreateImageView(lwjglData.vkDevice, viewInfo, null, pView) != VK14.VK_SUCCESS) {
        throw new RuntimeException("Failed to create texture view");
      }
      
      VkSamplerCreateInfo samplerInfo = VkSamplerCreateInfo.calloc(stack)
          .sType$Default()
          .magFilter(VK14.VK_FILTER_NEAREST)
          .minFilter(VK14.VK_FILTER_NEAREST)
          .mipmapMode(VK14.VK_SAMPLER_MIPMAP_MODE_NEAREST)
          .addressModeU(VK14.VK_SAMPLER_ADDRESS_MODE_CLAMP_TO_EDGE)
          .addressModeV(VK14.VK_SAMPLER_ADDRESS_MODE_CLAMP_TO_EDGE)
          .addressModeW(VK14.VK_SAMPLER_ADDRESS_MODE_CLAMP_TO_EDGE)
          .maxLod(1.0f);
      LongBuffer pSampler = stack.mallocLong(1);
      if (VK14.vkCreateSampler(lwjglData.vkDevice, samplerInfo, null, pSampler) != VK14.VK_SUCCESS) {
        throw new RuntimeException("Failed to create sampler");
      }
      
      lwjglData.textureImage = image;
      lwjglData.textureMemory = pMem.get(0);
      lwjglData.textureView = pView.get(0);
      lwjglData.textureSampler = pSampler.get(0);
    }
  }
  
  private static void barrier(VkCommandBuffer cmd, long image, int oldLayout, int newLayout,
                              long srcStage, long srcAccess, long dstStage, long dstAccess, MemoryStack stack) {
    VkImageMemoryBarrier2.Buffer b = VkImageMemoryBarrier2.calloc(1, stack)
        .sType$Default()
        .srcStageMask(srcStage).srcAccessMask(srcAccess)
        .dstStageMask(dstStage).dstAccessMask(dstAccess)
        .oldLayout(oldLayout).newLayout(newLayout)
        .srcQueueFamilyIndex(VK14.VK_QUEUE_FAMILY_IGNORED)
        .dstQueueFamilyIndex(VK14.VK_QUEUE_FAMILY_IGNORED)
        .image(image)
        .subresourceRange(r -> r.aspectMask(VK14.VK_IMAGE_ASPECT_COLOR_BIT)
            .baseMipLevel(0).levelCount(1).baseArrayLayer(0).layerCount(1));
    VkDependencyInfo dep = VkDependencyInfo.calloc(stack)
        .sType$Default()
        .pImageMemoryBarriers(b);
    VK14.vkCmdPipelineBarrier2(cmd, dep);
  }
}
