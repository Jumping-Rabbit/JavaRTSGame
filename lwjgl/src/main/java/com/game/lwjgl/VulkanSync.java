package com.game.lwjgl;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK14;
import org.lwjgl.vulkan.VkFenceCreateInfo;
import org.lwjgl.vulkan.VkSemaphoreCreateInfo;

import java.nio.LongBuffer;

class VulkanSync {
  static void makeSync(LwjglData lwjglData) {
    try (MemoryStack stack = MemoryStack.stackPush()) {
      VkSemaphoreCreateInfo semaphoreInfo = VkSemaphoreCreateInfo.calloc(stack)
          .sType(VK14.VK_STRUCTURE_TYPE_SEMAPHORE_CREATE_INFO);
      
      VkFenceCreateInfo fenceInfo = VkFenceCreateInfo.calloc(stack)
          .sType(VK14.VK_STRUCTURE_TYPE_FENCE_CREATE_INFO)
          .flags(VK14.VK_FENCE_CREATE_SIGNALED_BIT);
      
      LongBuffer pSem1 = stack.mallocLong(1);
      LongBuffer pSem2 = stack.mallocLong(1);
      LongBuffer pFence = stack.mallocLong(1);
      
      for (int i = 0; i < lwjglData.MAX_FRAMES_IN_FLIGHT; i++) {
        if (VK14.vkCreateSemaphore(lwjglData.vkDevice, semaphoreInfo, null, pSem1) != VK14.VK_SUCCESS ||
            VK14.vkCreateSemaphore(lwjglData.vkDevice, semaphoreInfo, null, pSem2) != VK14.VK_SUCCESS ||
            VK14.vkCreateFence(lwjglData.vkDevice, fenceInfo, null, pFence) != VK14.VK_SUCCESS) {
          throw new RuntimeException("Failed to create synchronization objects for frame " + i);
        }
        
        lwjglData.imageAvailableSemaphores[i] = pSem1.get(0);
        lwjglData.renderFinishedSemaphores[i] = pSem2.get(0);
        lwjglData.inFlightFences[i] = pFence.get(0);
      }
    }
  }
  
  
  static void cleanupSync(LwjglData lwjglData){
    if (lwjglData == null || lwjglData.vkDevice == null) {
      return;
    }
    
    VK14.vkDeviceWaitIdle(lwjglData.vkDevice);
    
    for (int i = 0; i < lwjglData.MAX_FRAMES_IN_FLIGHT; i++) {
      if (lwjglData.imageAvailableSemaphores != null && lwjglData.imageAvailableSemaphores[i] != VK14.VK_NULL_HANDLE) {
        VK14.vkDestroySemaphore(lwjglData.vkDevice, lwjglData.imageAvailableSemaphores[i], null);
        lwjglData.imageAvailableSemaphores[i] = VK14.VK_NULL_HANDLE;
      }
      
      if (lwjglData.renderFinishedSemaphores != null && lwjglData.renderFinishedSemaphores[i] != VK14.VK_NULL_HANDLE) {
        VK14.vkDestroySemaphore(lwjglData.vkDevice, lwjglData.renderFinishedSemaphores[i], null);
        lwjglData.renderFinishedSemaphores[i] = VK14.VK_NULL_HANDLE;
      }
      
      if (lwjglData.inFlightFences != null && lwjglData.inFlightFences[i] != VK14.VK_NULL_HANDLE) {
        VK14.vkDestroyFence(lwjglData.vkDevice, lwjglData.inFlightFences[i], null);
        lwjglData.inFlightFences[i] = VK14.VK_NULL_HANDLE;
      }
    }
  }
}
