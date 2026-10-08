package com.game.lwjgl.Vulkan;

import com.game.lwjgl.LwjglData;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK14;
import org.lwjgl.vulkan.VkCommandBuffer;
import org.lwjgl.vulkan.VkCommandBufferBeginInfo;

import java.util.concurrent.Callable;

public class VulkanRenderBatchTask2D implements Callable<VkCommandBuffer> {
  private final LwjglData lwjglData;
  
  private LongArrayList x;
  private LongArrayList lastX;
  private LongArrayList y;
  private LongArrayList lastY;
  private LongArrayList data;
  private LongArrayList data2;
  private IntArrayList UIType;
  private IntArrayList index;
  private int start;
  private int end;
  
  private int currentFrameIndex;
  
  
  public VulkanRenderBatchTask2D(LwjglData lwjglData){
    this.lwjglData = lwjglData;
  }
  
  @Override
  public VkCommandBuffer call(){
    VkCommandBuffer buffer = VulkanThreadState.getBufferForCurrentThread(lwjglData, currentFrameIndex);
    
    try (MemoryStack stack = MemoryStack.stackPush()) {
      VkCommandBufferBeginInfo beginInfo = VkCommandBufferBeginInfo.calloc(stack)
          .sType(VK14.VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO)
          .flags(VK14.VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT);
      
      VK14.vkBeginCommandBuffer(buffer, beginInfo);
      VK14.vkCmdBindPipeline(
          buffer,
          VK14.VK_PIPELINE_BIND_POINT_GRAPHICS,
          lwjglData.vulkanPipelines.getLong(PipelineTypes.TWO_D)
      );
      for (int i = start; i < end; i++){
        VK14.vkCmdDraw(buffer, 4, 1, 0, 0);//TODO: make this read off of the array
      }
      
      VK14.vkEndCommandBuffer(buffer);
      
    }
    return buffer;
  }
}
