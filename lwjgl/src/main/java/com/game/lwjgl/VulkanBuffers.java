package com.game.lwjgl;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.*;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;

class VulkanBuffers {
  static int findMemoryType(LwjglData lwjglData, int typeBits, int props) {
    try (MemoryStack stack = MemoryStack.stackPush()) {
      VkPhysicalDeviceMemoryProperties mem = VkPhysicalDeviceMemoryProperties.malloc(stack);
      VK14.vkGetPhysicalDeviceMemoryProperties(lwjglData.vkPhysicalDevice, mem);
      for (int i = 0; i < mem.memoryTypeCount(); i++) {
        if ((typeBits & (1 << i)) != 0 && (mem.memoryTypes(i).propertyFlags() & props) == props) {
          return i;
        }
      }
    }
    throw new RuntimeException("No suitable memory type");
  }
  
  static long[] createBuffer(LwjglData lwjglData, long size, int usage, int props) {
    try (MemoryStack stack = MemoryStack.stackPush()) {
      VkBufferCreateInfo info = VkBufferCreateInfo.calloc(stack)
          .sType$Default()
          .size(size)
          .usage(usage)
          .sharingMode(VK14.VK_SHARING_MODE_EXCLUSIVE);
      LongBuffer pBuf = stack.mallocLong(1);
      if (VK14.vkCreateBuffer(lwjglData.vkDevice, info, null, pBuf) != VK14.VK_SUCCESS) {
        throw new RuntimeException("Failed to create buffer");
      }
      long buffer = pBuf.get(0);
      
      VkMemoryRequirements req = VkMemoryRequirements.malloc(stack);
      VK14.vkGetBufferMemoryRequirements(lwjglData.vkDevice, buffer, req);
      
      VkMemoryAllocateInfo alloc = VkMemoryAllocateInfo.calloc(stack)
          .sType$Default()
          .allocationSize(req.size())
          .memoryTypeIndex(findMemoryType(lwjglData, req.memoryTypeBits(), props));
      LongBuffer pMem = stack.mallocLong(1);
      if (VK14.vkAllocateMemory(lwjglData.vkDevice, alloc, null, pMem) != VK14.VK_SUCCESS) {
        throw new RuntimeException("Failed to allocate buffer memory");
      }
      VK14.vkBindBufferMemory(lwjglData.vkDevice, buffer, pMem.get(0), 0);
      return new long[]{buffer, pMem.get(0)};
    }
  }
  
  static void upload(LwjglData lwjglData, long memory, ByteBuffer data) {
    try (MemoryStack stack = MemoryStack.stackPush()) {
      PointerBuffer pData = stack.mallocPointer(1);
      VK14.vkMapMemory(lwjglData.vkDevice, memory, 0, data.remaining(), 0, pData);
      MemoryUtil.memCopy(MemoryUtil.memAddress(data), pData.get(0), data.remaining());
      VK14.vkUnmapMemory(lwjglData.vkDevice, memory);
    }
  }
  
  static void makeVertexBuffer(LwjglData lwjglData) {
    try (MemoryStack stack = MemoryStack.stackPush()) {
      ByteBuffer data = stack.calloc(40);
      data.putFloat(0, 760f).putFloat(4, 340f).putFloat(8, 400f).putFloat(12, 400f); // x, y, w, h
      data.put(32, (byte) 255).put(33, (byte) 128).put(34, (byte) 51).put(35, (byte) 255); // orange RGBA
      data.putInt(36, 0); // type 0 = rect
      
      long[] vb = createBuffer(lwjglData, 40, VK14.VK_BUFFER_USAGE_VERTEX_BUFFER_BIT,
          VK14.VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT | VK14.VK_MEMORY_PROPERTY_HOST_COHERENT_BIT);
      upload(lwjglData, vb[1], data);
      lwjglData.vertexBuffer = vb[0];
      lwjglData.vertexBufferMemory = vb[1];
    }
  }
}
