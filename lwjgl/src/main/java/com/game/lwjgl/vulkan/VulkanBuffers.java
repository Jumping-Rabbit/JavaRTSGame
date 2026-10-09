package com.game.lwjgl.vulkan;

import com.game.lwjgl.LwjglData;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.VK14;
import org.lwjgl.vulkan.VkBufferCreateInfo;
import org.lwjgl.vulkan.VkMemoryAllocateInfo;
import org.lwjgl.vulkan.VkMemoryRequirements;
import org.lwjgl.vulkan.VkPhysicalDeviceMemoryProperties;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.util.ArrayList;

public class VulkanBuffers {
  private VulkanBuffers() {
  }
  
  public static int findMemoryType(LwjglData lwjglData, int typeBits, int props) {
    try (MemoryStack stack = MemoryStack.stackPush()) {
      VkPhysicalDeviceMemoryProperties physicalDeviceMemoryProperties = VkPhysicalDeviceMemoryProperties.malloc(stack);
      VK14.vkGetPhysicalDeviceMemoryProperties(lwjglData.vkPhysicalDevice, physicalDeviceMemoryProperties);
      for (int i = 0; i < physicalDeviceMemoryProperties.memoryTypeCount(); i++) {
        if ((typeBits & (1 << i)) != 0 && (physicalDeviceMemoryProperties.memoryTypes(i).propertyFlags() & props) == props) {
          return i;
        }
      }
    }
    throw new RuntimeException("no suitable memory type");
  }
  
  public static long[] createBuffer(LwjglData lwjglData, long size, int usage, int props) {
    try (MemoryStack stack = MemoryStack.stackPush()) {
      VkBufferCreateInfo info = VkBufferCreateInfo.calloc(stack)
          .sType$Default()
          .size(size)
          .usage(usage)
          .sharingMode(VK14.VK_SHARING_MODE_EXCLUSIVE);
      LongBuffer pBuffer = stack.mallocLong(1);
      if (VK14.vkCreateBuffer(lwjglData.vkDevice, info, null, pBuffer) != VK14.VK_SUCCESS) {
        throw new RuntimeException("failed to create buffer");
      }
      long buffer = pBuffer.get(0);
      
      VkMemoryRequirements req = VkMemoryRequirements.malloc(stack);
      VK14.vkGetBufferMemoryRequirements(lwjglData.vkDevice, buffer, req);
      
      VkMemoryAllocateInfo alloc = VkMemoryAllocateInfo.calloc(stack)
          .sType$Default()
          .allocationSize(req.size())
          .memoryTypeIndex(findMemoryType(lwjglData, req.memoryTypeBits(), props));
      LongBuffer pMem = stack.mallocLong(1);
      if (VK14.vkAllocateMemory(lwjglData.vkDevice, alloc, null, pMem) != VK14.VK_SUCCESS) {
        throw new RuntimeException("failed to allocate buffer memory");
      }
      VK14.vkBindBufferMemory(lwjglData.vkDevice, buffer, pMem.get(0), 0);
      return new long[]{buffer, pMem.get(0)};
    }
  }
  
  public static void upload(LwjglData lwjglData, long memory, ByteBuffer data) {
    try (MemoryStack stack = MemoryStack.stackPush()) {
      PointerBuffer pData = stack.mallocPointer(1);
      VK14.vkMapMemory(lwjglData.vkDevice, memory, 0, data.remaining(), 0, pData);
      MemoryUtil.memCopy(MemoryUtil.memAddress(data), pData.get(0), data.remaining());
      VK14.vkUnmapMemory(lwjglData.vkDevice, memory);
    }
  }
  
  public static void makeInstanceBuffer(LwjglData lwjglData) {
    lwjglData.taskCount = 2 * lwjglData.renderThreads + 1;
    long size = (long) lwjglData.taskCount * lwjglData.instancesPerTask * Batch2D.STRIDE;
    long[] vb = createBuffer(lwjglData, size, VK14.VK_BUFFER_USAGE_VERTEX_BUFFER_BIT,
        VK14.VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT | VK14.VK_MEMORY_PROPERTY_HOST_COHERENT_BIT);
    lwjglData.instanceBuffer = vb[0];
    lwjglData.instanceMemory = vb[1];
    
    try (MemoryStack stack = MemoryStack.stackPush()) {
      PointerBuffer pData = stack.mallocPointer(1);
      VK14.vkMapMemory(lwjglData.vkDevice, vb[1], 0, size, 0, pData);
      lwjglData.instanceMapped = MemoryUtil.memByteBuffer(pData.get(0), (int) size);
    }
    
    lwjglData.renderTasks = new ArrayList<>();
    int t = 0;
    for (int i = 0; i < lwjglData.renderThreads; i++)
      lwjglData.renderTasks.add(makeTask(lwjglData, VulkanRenderBatchTask2D.Kind.ENTITIES, i, lwjglData.renderThreads, t++));
    lwjglData.renderTasks.add(makeTask(lwjglData, VulkanRenderBatchTask2D.Kind.EXTRAS, 0, 1, t++));
    for (int i = 0; i < lwjglData.renderThreads; i++)
      lwjglData.renderTasks.add(makeTask(lwjglData, VulkanRenderBatchTask2D.Kind.UI, i, lwjglData.renderThreads, t++));
  }
  
  private static VulkanRenderBatchTask2D makeTask(LwjglData lwjglData, VulkanRenderBatchTask2D.Kind kind, int index, int chunks, int slot) {
    int first = slot * lwjglData.instancesPerTask;
    ByteBuffer slice = lwjglData.instanceMapped.slice(first * Batch2D.STRIDE, lwjglData.instancesPerTask * Batch2D.STRIDE);
    return new VulkanRenderBatchTask2D(lwjglData, kind, index, chunks, first, new Batch2D(slice, lwjglData.instancesPerTask));
  }
  
  public static void cleanupBuffers(LwjglData lwjglData) {
    VK14.vkUnmapMemory(lwjglData.vkDevice, lwjglData.instanceMemory);
    VK14.vkDestroyBuffer(lwjglData.vkDevice, lwjglData.instanceBuffer, null);
    VK14.vkFreeMemory(lwjglData.vkDevice, lwjglData.instanceMemory, null);
  }
}
