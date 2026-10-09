package com.game.lwjgl.stb;

import com.game.lwjgl.LwjglData;
import com.game.lwjgl.api.Fonts;
import com.game.lwjgl.vulkan.VulkanCommandPool;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.vma.Vma;
import org.lwjgl.util.vma.VmaAllocationCreateInfo;
import org.lwjgl.vulkan.VK14;
import org.lwjgl.vulkan.VkBufferCreateInfo;
import org.lwjgl.vulkan.VkBufferImageCopy;
import org.lwjgl.vulkan.VkCommandBuffer;
import org.lwjgl.vulkan.VkCommandBufferBeginInfo;
import org.lwjgl.vulkan.VkDependencyInfo;
import org.lwjgl.vulkan.VkImageCreateInfo;
import org.lwjgl.vulkan.VkImageMemoryBarrier2;
import org.lwjgl.vulkan.VkImageViewCreateInfo;
import org.lwjgl.vulkan.VkSamplerCreateInfo;
import org.lwjgl.vulkan.VkSubmitInfo;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.file.Files;
import java.nio.file.Path;

public class StbSDF {
  private static final int ATLAS_SIZE = 512;
  
  private StbSDF() {
  }
  
  public static void makeFonts(LwjglData lwjglData) {
    Fonts[] fontList = Fonts.values();
    int numFonts = fontList.length;
    int layerBytes = ATLAS_SIZE * ATLAS_SIZE;
    int totalBytes = layerBytes * numFonts;
    
    BakedAtlas[] atlases = new BakedAtlas[numFonts];
    ByteBuffer combined = BufferUtils.createByteBuffer(totalBytes);
    for (int i = 0; i < numFonts; i++) {
      atlases[i] = createSDFAtlas(ioResourceToBuffer(fontList[i].getFilePath()), ATLAS_SIZE);
      combined.put(atlases[i].pixelData);
    }
    combined.flip();
    lwjglData.bakedAtlases = atlases;
    
    try (MemoryStack stack = MemoryStack.stackPush()) {
      VkImageCreateInfo imageInfo = VkImageCreateInfo.calloc(stack)
          .sType$Default()
          .imageType(VK14.VK_IMAGE_TYPE_2D)
          .format(VK14.VK_FORMAT_R8_UNORM)
          .mipLevels(1)
          .arrayLayers(numFonts)
          .samples(VK14.VK_SAMPLE_COUNT_1_BIT)
          .tiling(VK14.VK_IMAGE_TILING_OPTIMAL)
          .usage(VK14.VK_IMAGE_USAGE_TRANSFER_DST_BIT | VK14.VK_IMAGE_USAGE_SAMPLED_BIT)
          .sharingMode(VK14.VK_SHARING_MODE_EXCLUSIVE)
          .initialLayout(VK14.VK_IMAGE_LAYOUT_UNDEFINED);
      imageInfo.extent().set(ATLAS_SIZE, ATLAS_SIZE, 1);
      
      VmaAllocationCreateInfo imageAlloc = VmaAllocationCreateInfo.calloc(stack)
          .usage(Vma.VMA_MEMORY_USAGE_AUTO)
          .preferredFlags(VK14.VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT);
      LongBuffer pImage = stack.mallocLong(1);
      PointerBuffer pAlloc = stack.mallocPointer(1);
      if (Vma.vmaCreateImage(lwjglData.vmaAllocator, imageInfo, imageAlloc, pImage, pAlloc, null) != VK14.VK_SUCCESS) {
        throw new RuntimeException("failed to allocate font image");
      }
      lwjglData.fontImage = pImage.get(0);
      lwjglData.fontAllocation = pAlloc.get(0);
      
      // staging buffer
      VkBufferCreateInfo bufInfo = VkBufferCreateInfo.calloc(stack)
          .sType$Default()
          .size(totalBytes)
          .usage(VK14.VK_BUFFER_USAGE_TRANSFER_SRC_BIT)
          .sharingMode(VK14.VK_SHARING_MODE_EXCLUSIVE);
      VmaAllocationCreateInfo stagingAlloc = VmaAllocationCreateInfo.calloc(stack)
          .usage(Vma.VMA_MEMORY_USAGE_AUTO)
          .flags(Vma.VMA_ALLOCATION_CREATE_HOST_ACCESS_SEQUENTIAL_WRITE_BIT);
      LongBuffer pStaging = stack.mallocLong(1);
      PointerBuffer pStagingAlloc = stack.mallocPointer(1);
      if (Vma.vmaCreateBuffer(lwjglData.vmaAllocator, bufInfo, stagingAlloc, pStaging, pStagingAlloc, null) != VK14.VK_SUCCESS) {
        throw new RuntimeException("failed to allocate font staging buffer");
      }
      long staging = pStaging.get(0), stagingMem = pStagingAlloc.get(0);
      
      PointerBuffer pMapped = stack.mallocPointer(1);
      Vma.vmaMapMemory(lwjglData.vmaAllocator, stagingMem, pMapped);
      pMapped.getByteBuffer(0, totalBytes).put(combined);
      Vma.vmaFlushAllocation(lwjglData.vmaAllocator, stagingMem, 0, VK14.VK_WHOLE_SIZE); // may not be coherent
      Vma.vmaUnmapMemory(lwjglData.vmaAllocator, stagingMem);
      
      VkBufferImageCopy.Buffer regions = VkBufferImageCopy.calloc(numFonts, stack);
      for (int i = 0; i < numFonts; i++) {
        VkBufferImageCopy region = regions.get(i);
        region.bufferOffset((long) i * layerBytes);
        region.imageSubresource().aspectMask(VK14.VK_IMAGE_ASPECT_COLOR_BIT).mipLevel(0).baseArrayLayer(i).layerCount(1);
        region.imageExtent().set(ATLAS_SIZE, ATLAS_SIZE, 1);
      }
      
      VkCommandBuffer commandBuffer = beginOneShot(lwjglData, stack);
      barrier(commandBuffer, lwjglData.fontImage, numFonts,
          VK14.VK_IMAGE_LAYOUT_UNDEFINED, VK14.VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL,
          VK14.VK_PIPELINE_STAGE_2_TOP_OF_PIPE_BIT, 0,
          VK14.VK_PIPELINE_STAGE_2_TRANSFER_BIT, VK14.VK_ACCESS_2_TRANSFER_WRITE_BIT, stack);
      VK14.vkCmdCopyBufferToImage(commandBuffer, staging, lwjglData.fontImage, VK14.VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL, regions);
      barrier(commandBuffer, lwjglData.fontImage, numFonts,
          VK14.VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL, VK14.VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL,
          VK14.VK_PIPELINE_STAGE_2_TRANSFER_BIT, VK14.VK_ACCESS_2_TRANSFER_WRITE_BIT,
          VK14.VK_PIPELINE_STAGE_2_FRAGMENT_SHADER_BIT, VK14.VK_ACCESS_2_SHADER_SAMPLED_READ_BIT, stack);
      endOneShot(lwjglData, commandBuffer);
      
      Vma.vmaDestroyBuffer(lwjglData.vmaAllocator, staging, stagingMem);
      createImageViewAndSampler(lwjglData, numFonts);
    }
  }
  
  public static void cleanupFonts(LwjglData lwjglData) {
    VK14.vkDestroySampler(lwjglData.vkDevice, lwjglData.fontSampler, null);
    VK14.vkDestroyImageView(lwjglData.vkDevice, lwjglData.fontImageView, null);
    Vma.vmaDestroyImage(lwjglData.vmaAllocator, lwjglData.fontImage, lwjglData.fontAllocation);
  }
  
  private static BakedAtlas createSDFAtlas(ByteBuffer ttf, int size) {
    STBTTFontinfo fontInfo = STBTTFontinfo.create();
    if (!STBTruetype.stbtt_InitFont(fontInfo, ttf)) throw new RuntimeException("bad font data");
    
    BakedAtlas atlas = new BakedAtlas();
    atlas.pixelData = BufferUtils.createByteBuffer(size * size);
    
    float scale = STBTruetype.stbtt_ScaleForPixelHeight(fontInfo, 52.0f) * 0.45f;
    int padding = 6;
    byte onEdge = (byte) 128;
    float pixelDistScale = 24.0f;
    int cell = 50;
    int maxCols = size / cell;
    
    try (MemoryStack stack = MemoryStack.stackPush()) {
      
      IntBuffer asc = stack.mallocInt(1), desc = stack.mallocInt(1), gap = stack.mallocInt(1);
      STBTruetype.stbtt_GetFontVMetrics(fontInfo, asc, desc, gap);
      atlas.ascent = asc.get(0) * scale;
      atlas.descent = desc.get(0) * scale;
      
      IntBuffer width = stack.mallocInt(1);
      IntBuffer height = stack.mallocInt(1);
      IntBuffer xoff = stack.mallocInt(1);
      IntBuffer yoff = stack.mallocInt(1);
      IntBuffer advanceWidth = stack.mallocInt(1);
      IntBuffer leftSideBearing = stack.mallocInt(1);
      
      for (int i = 0; i < 96; i++) {
        int codepoint = 32 + i;
        ByteBuffer sdf = STBTruetype.stbtt_GetCodepointSDF(fontInfo, scale, codepoint, padding, onEdge, pixelDistScale, width, height, xoff, yoff);
        STBTruetype.stbtt_GetCodepointHMetrics(fontInfo, codepoint, advanceWidth, leftSideBearing);
        
        GlyphInfo glyphInfo = new GlyphInfo();
        glyphInfo.width = width.get(0);
        glyphInfo.height = height.get(0);
        glyphInfo.xOffset = xoff.get(0);
        glyphInfo.yOffset = yoff.get(0);
        glyphInfo.advance = advanceWidth.get(0) * scale;
        
        int startX = (i % maxCols) * cell;
        int startY = (i / maxCols) * cell;
        
        if (sdf != null && glyphInfo.width > 0 && glyphInfo.height > 0) {
          for (int y = 0; y < glyphInfo.height; y++) {
            for (int x = 0; x < glyphInfo.width; x++) {
              atlas.pixelData.put((startX + x) + (startY + y) * size, sdf.get(x + y * glyphInfo.width));
            }
          }
          STBTruetype.stbtt_FreeSDF(sdf);
        }
        glyphInfo.u0 = (float) startX / size;
        glyphInfo.v0 = (float) startY / size;
        glyphInfo.u1 = (float) (startX + glyphInfo.width) / size;
        glyphInfo.v1 = (float) (startY + glyphInfo.height) / size;
        atlas.glyphs[i] = glyphInfo;
      }
    }
    return atlas;
  }
  
  private static void createImageViewAndSampler(LwjglData lwjglData, int numFonts) {
    try (MemoryStack stack = MemoryStack.stackPush()) {
      VkImageViewCreateInfo viewInfo = VkImageViewCreateInfo.calloc(stack)
          .sType$Default()
          .image(lwjglData.fontImage)
          .viewType(VK14.VK_IMAGE_VIEW_TYPE_2D_ARRAY)
          .format(VK14.VK_FORMAT_R8_UNORM);
      viewInfo.subresourceRange().aspectMask(VK14.VK_IMAGE_ASPECT_COLOR_BIT)
          .baseMipLevel(0).levelCount(1).baseArrayLayer(0).layerCount(numFonts);
      LongBuffer pView = stack.mallocLong(1);
      if (VK14.vkCreateImageView(lwjglData.vkDevice, viewInfo, null, pView) != VK14.VK_SUCCESS) {
        throw new RuntimeException("failed to create font image view");
      }
      lwjglData.fontImageView = pView.get(0);
      
      VkSamplerCreateInfo samplerInfo = VkSamplerCreateInfo.calloc(stack)
          .sType$Default()
          .magFilter(VK14.VK_FILTER_LINEAR)
          .minFilter(VK14.VK_FILTER_LINEAR)
          .addressModeU(VK14.VK_SAMPLER_ADDRESS_MODE_CLAMP_TO_EDGE)
          .addressModeV(VK14.VK_SAMPLER_ADDRESS_MODE_CLAMP_TO_EDGE)
          .addressModeW(VK14.VK_SAMPLER_ADDRESS_MODE_CLAMP_TO_EDGE);
      LongBuffer pSampler = stack.mallocLong(1);
      if (VK14.vkCreateSampler(lwjglData.vkDevice, samplerInfo, null, pSampler) != VK14.VK_SUCCESS) {
        throw new RuntimeException("failed to create font sampler");
      }
      lwjglData.fontSampler = pSampler.get(0);
    }
  }
  
  private static ByteBuffer ioResourceToBuffer(String path) {
    try {
      byte[] bytes = Files.readAllBytes(Path.of(path));
      return BufferUtils.createByteBuffer(bytes.length).put(bytes).flip();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
  
  private static VkCommandBuffer beginOneShot(LwjglData lwjglData, MemoryStack stack) {
    VkCommandBuffer commandBuffer = VulkanCommandPool.makeBuffers(lwjglData, lwjglData.commandPool, VK14.VK_COMMAND_BUFFER_LEVEL_PRIMARY, 1)[0];
    VK14.vkBeginCommandBuffer(commandBuffer, VkCommandBufferBeginInfo.calloc(stack)
        .sType$Default().flags(VK14.VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT));
    return commandBuffer;
  }
  
  private static void endOneShot(LwjglData lwjglData, VkCommandBuffer commandBuffer) {
    try (MemoryStack stack = MemoryStack.stackPush()) {
      VK14.vkEndCommandBuffer(commandBuffer);
      VK14.vkQueueSubmit(lwjglData.graphicsQueue,
          VkSubmitInfo.calloc(stack).sType$Default().pCommandBuffers(stack.pointers(commandBuffer)), VK14.VK_NULL_HANDLE);
      VK14.vkQueueWaitIdle(lwjglData.graphicsQueue);
      VK14.vkFreeCommandBuffers(lwjglData.vkDevice, lwjglData.commandPool, commandBuffer);
    }
  }
  
  private static void barrier(VkCommandBuffer commandBuffer, long image, int layers, int oldLayout, int newLayout,
                              long srcStage, long srcAccess, long dstStage, long dstAccess, MemoryStack stack) {
    VkImageMemoryBarrier2.Buffer b = VkImageMemoryBarrier2.calloc(1, stack)
        .sType$Default()
        .srcStageMask(srcStage).srcAccessMask(srcAccess)
        .dstStageMask(dstStage).dstAccessMask(dstAccess)
        .oldLayout(oldLayout).newLayout(newLayout)
        .srcQueueFamilyIndex(VK14.VK_QUEUE_FAMILY_IGNORED)
        .dstQueueFamilyIndex(VK14.VK_QUEUE_FAMILY_IGNORED)
        .image(image);
    b.subresourceRange().aspectMask(VK14.VK_IMAGE_ASPECT_COLOR_BIT)
        .baseMipLevel(0).levelCount(1).baseArrayLayer(0).layerCount(layers);
    VK14.vkCmdPipelineBarrier2(commandBuffer, VkDependencyInfo.calloc(stack).sType$Default().pImageMemoryBarriers(b));
  }
}