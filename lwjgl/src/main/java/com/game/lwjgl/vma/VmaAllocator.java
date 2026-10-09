package com.game.lwjgl.vma;

import com.game.lwjgl.LwjglData;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.vma.Vma;
import org.lwjgl.util.vma.VmaAllocatorCreateInfo;
import org.lwjgl.util.vma.VmaVulkanFunctions;
import org.lwjgl.vulkan.VK14;

public class VmaAllocator {
  
  public static void createAllocator(LwjglData lwjglData) {
    try (MemoryStack stack = MemoryStack.stackPush()) {
      
      VmaVulkanFunctions vulkanFunctions = VmaVulkanFunctions.calloc(stack)
          .set(lwjglData.vkInstance, lwjglData.vkDevice);
      
      VmaAllocatorCreateInfo allocatorCreateInfo = VmaAllocatorCreateInfo.calloc(stack)
          .vulkanApiVersion(VK14.VK_API_VERSION_1_3)
          .instance(lwjglData.vkInstance)
          .physicalDevice(lwjglData.vkPhysicalDevice)
          .device(lwjglData.vkDevice)
          .pVulkanFunctions(vulkanFunctions);
      
      PointerBuffer pAllocator = stack.mallocPointer(1);
      
      int result = Vma.vmaCreateAllocator(allocatorCreateInfo, pAllocator);
      if (result != VK14.VK_SUCCESS) {
        throw new RuntimeException("failed to create VMA allocator: " + result);
      }
      
      lwjglData.vmaAllocator = pAllocator.get(0);
    }
  }
  
  public static void cleanupAllocator(LwjglData lwjglData) {
    Vma.vmaDestroyAllocator(lwjglData.vmaAllocator);
  }
  
}
