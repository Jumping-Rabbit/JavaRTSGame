package com.game.lwjgl;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.IntBuffer;
import java.nio.LongBuffer;

class VulkanSwapchain {
  static void makeSwapChain(LwjglData lwjglData) {
    try (MemoryStack stack = MemoryStack.stackPush()) {
      
      VkSurfaceCapabilitiesKHR capabilities = VkSurfaceCapabilitiesKHR.malloc(stack);
      KHRSurface.vkGetPhysicalDeviceSurfaceCapabilitiesKHR(lwjglData.vkPhysicalDevice, lwjglData.surface, capabilities);
      
      int imageCount = capabilities.minImageCount() + 1;
      if (capabilities.maxImageCount() > 0 && imageCount > capabilities.maxImageCount()) {
        imageCount = capabilities.maxImageCount();
      }
      
      IntBuffer formatCount = stack.mallocInt(1);
      KHRSurface.vkGetPhysicalDeviceSurfaceFormatsKHR(lwjglData.vkPhysicalDevice, lwjglData.surface, formatCount, null);
      if (formatCount.get(0) == 0) {
        throw new RuntimeException("Failed to find supported surface formats");
      }
      VkSurfaceFormatKHR.Buffer surfaceFormats = VkSurfaceFormatKHR.malloc(formatCount.get(0), stack);
      KHRSurface.vkGetPhysicalDeviceSurfaceFormatsKHR(lwjglData.vkPhysicalDevice, lwjglData.surface, formatCount, surfaceFormats);
      
      VkSurfaceFormatKHR chosenFormat = surfaceFormats.get(0);
      for (int i = 0; i < surfaceFormats.capacity(); i++) {
        VkSurfaceFormatKHR fmt = surfaceFormats.get(i);
        if (fmt.format() == VK14.VK_FORMAT_B8G8R8A8_SRGB && fmt.colorSpace() == KHRSurface.VK_COLOR_SPACE_SRGB_NONLINEAR_KHR) {
          chosenFormat = fmt;
          break;
        }
      }
      lwjglData.swapchainImageFormat = chosenFormat.format();
      
      IntBuffer presentModeCount = stack.mallocInt(1);
      KHRSurface.vkGetPhysicalDeviceSurfacePresentModesKHR(lwjglData.vkPhysicalDevice, lwjglData.surface, presentModeCount, null);
      int chosenPresentMode = KHRSurface.VK_PRESENT_MODE_FIFO_KHR;
      if (presentModeCount.get(0) > 0) {
        IntBuffer presentModes = stack.mallocInt(presentModeCount.get(0));
        KHRSurface.vkGetPhysicalDeviceSurfacePresentModesKHR(lwjglData.vkPhysicalDevice, lwjglData.surface, presentModeCount, presentModes);
        for (int i = 0; i < presentModes.capacity(); i++) {
          if (presentModes.get(i) == KHRSurface.VK_PRESENT_MODE_MAILBOX_KHR) {
            chosenPresentMode = KHRSurface.VK_PRESENT_MODE_MAILBOX_KHR;
            break;
          }
        }
      }
      
      VkExtent2D extent = VkExtent2D.malloc(stack);
      if (capabilities.currentExtent().width() != 0xFFFFFFFF) {
        extent.set(capabilities.currentExtent());
      } else {
        extent.set(
            Math.clamp(lwjglData.windowWidth, capabilities.minImageExtent().width(), capabilities.maxImageExtent().width()),
            Math.clamp(lwjglData.windowHeight, capabilities.minImageExtent().height(), capabilities.maxImageExtent().height())
        );
      }
      lwjglData.swapchainWidth = extent.width();
      lwjglData.swapchainHeight = extent.height();
      
      VkSwapchainCreateInfoKHR createInfo = VkSwapchainCreateInfoKHR.calloc(stack)
          .sType(KHRSwapchain.VK_STRUCTURE_TYPE_SWAPCHAIN_CREATE_INFO_KHR)
          .surface(lwjglData.surface)
          .minImageCount(imageCount)
          .imageFormat(chosenFormat.format())
          .imageColorSpace(chosenFormat.colorSpace())
          .imageExtent(extent)
          .imageArrayLayers(1)
          .imageUsage(VK14.VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT)
          .imageSharingMode(VK14.VK_SHARING_MODE_EXCLUSIVE)
          .preTransform(capabilities.currentTransform())
          .compositeAlpha(KHRSurface.VK_COMPOSITE_ALPHA_OPAQUE_BIT_KHR)
          .presentMode(chosenPresentMode)
          .clipped(true)
          .oldSwapchain(VK14.VK_NULL_HANDLE);
      
      LongBuffer pSwapchain = stack.mallocLong(1);
      if (KHRSwapchain.vkCreateSwapchainKHR(lwjglData.vkDevice, createInfo, null, pSwapchain) != VK14.VK_SUCCESS) {
        throw new RuntimeException("Failed to create swapchain");
      }
      
      lwjglData.swapchain = pSwapchain.get(0);
    }
  }
  
  static void getSwapchainImages(LwjglData lwjglData) {
    try (MemoryStack stack = MemoryStack.stackPush()) {
      IntBuffer imageCount = stack.mallocInt(1);
      KHRSwapchain.vkGetSwapchainImagesKHR(lwjglData.vkDevice, lwjglData.swapchain, imageCount, null);
      
      LongBuffer pSwapchainImages = stack.mallocLong(imageCount.get(0));
      KHRSwapchain.vkGetSwapchainImagesKHR(lwjglData.vkDevice, lwjglData.swapchain, imageCount, pSwapchainImages);
      
      lwjglData.swapchainImages = new long[imageCount.get(0)];
      for (int i = 0; i < lwjglData.swapchainImages.length; i++) {
        lwjglData.swapchainImages[i] = pSwapchainImages.get(i);
      }
    }
  }
  
  static void makeImageViews(LwjglData lwjglData) {
    lwjglData.swapchainImageViews = new long[lwjglData.swapchainImages.length];
    
    try (MemoryStack stack = MemoryStack.stackPush()) {
      VkImageViewCreateInfo viewInfo = VkImageViewCreateInfo.calloc(stack)
          .sType(VK14.VK_STRUCTURE_TYPE_IMAGE_VIEW_CREATE_INFO)
          .viewType(VK14.VK_IMAGE_VIEW_TYPE_2D)
          .format(lwjglData.swapchainImageFormat)
          .subresourceRange(r -> r
              .aspectMask(VK14.VK_IMAGE_ASPECT_COLOR_BIT)
              .baseMipLevel(0)
              .levelCount(1)
              .baseArrayLayer(0)
              .layerCount(1));
      
      LongBuffer pView = stack.mallocLong(1);
      for (int i = 0; i < lwjglData.swapchainImages.length; i++) {
        viewInfo.image(lwjglData.swapchainImages[i]);
        if (VK14.vkCreateImageView(lwjglData.vkDevice, viewInfo, null, pView) != VK14.VK_SUCCESS) {
          throw new RuntimeException("Failed to create image view at index " + i);
        }
        lwjglData.swapchainImageViews[i] = pView.get(0);
      }
    }
  }
  
  static void recreate(LwjglData lwjglData) {
    try (MemoryStack stack = MemoryStack.stackPush()) {
      IntBuffer w = stack.mallocInt(1), h = stack.mallocInt(1);
      org.lwjgl.glfw.GLFW.glfwGetFramebufferSize(lwjglData.windowHandle, w, h);
      if (w.get(0) == 0 || h.get(0) == 0) return; // minimized, try again later
      lwjglData.windowWidth = w.get(0);
      lwjglData.windowHeight = h.get(0);
    }
    
    VK14.vkDeviceWaitIdle(lwjglData.vkDevice);
    
   cleanupSwapchain(lwjglData);
    
    makeSwapChain(lwjglData);
    getSwapchainImages(lwjglData);
    makeImageViews(lwjglData);
    lwjglData.framebufferResized = false;
  }
  
  static void cleanupSwapchain(LwjglData lwjglData){
    if (lwjglData == null || lwjglData.vkDevice == null) {
      return;
    }
    
    if (lwjglData.swapchainImageViews != null) {
      for (long view : lwjglData.swapchainImageViews) {
        if (view != VK14.VK_NULL_HANDLE) {
          VK14.vkDestroyImageView(lwjglData.vkDevice, view, null);
        }
      }
      lwjglData.swapchainImageViews = null;
    }
    
    if (lwjglData.swapchain != VK14.VK_NULL_HANDLE) {
      KHRSwapchain.vkDestroySwapchainKHR(lwjglData.vkDevice, lwjglData.swapchain, null);
      lwjglData.swapchain = VK14.VK_NULL_HANDLE;
    }
    
    lwjglData.swapchainImages = null;
  }
}
