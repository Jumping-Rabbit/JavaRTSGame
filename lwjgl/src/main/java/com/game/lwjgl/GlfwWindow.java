package com.game.lwjgl;

import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWVulkan;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.KHRSurface;
import org.lwjgl.vulkan.VK14;

import java.nio.LongBuffer;

class GlfwWindow {
  
  
  private GlfwWindow() {
  }
  
  static void makeWindow(LwjglData lwjglData) {
    if (!GLFW.glfwInit()) {
      throw new RuntimeException("Unable to initialize GLFW");
    }
    
    if (!GLFWVulkan.glfwVulkanSupported()) {
      throw new RuntimeException("Cannot find a compatible Vulkan installable client driver (ICD)");
    }
    
    GLFWVidMode vidMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
    if (vidMode == null) {
      throw new RuntimeException("Error getting primary monitor");
    }
    int width = vidMode.width();
    int height = vidMode.height();
    
    GLFW.glfwDefaultWindowHints();
    GLFW.glfwWindowHint(GLFW.GLFW_CLIENT_API, GLFW.GLFW_NO_API);
    GLFW.glfwWindowHint(GLFW.GLFW_MAXIMIZED, GLFW.GLFW_FALSE);
    
    // Create the window
    long windowHandle = GLFW.glfwCreateWindow(width, height, "Java RTS Game", MemoryUtil.NULL, MemoryUtil.NULL);
    if (windowHandle == MemoryUtil.NULL) {
      throw new RuntimeException("Failed to create the GLFW window");
    }
    GLFW.glfwSetFramebufferSizeCallback(windowHandle, (window, w, h) -> {
      lwjglData.windowWidth = w;
      lwjglData.windowHeight = h;
      lwjglData.framebufferResized = true;
    });
    try (MemoryStack stack = MemoryStack.stackPush()) {
      java.nio.IntBuffer frameBufferWidth = stack.mallocInt(1), frameBufferHeight = stack.mallocInt(1);
      GLFW.glfwGetFramebufferSize(windowHandle, frameBufferWidth, frameBufferHeight);
      lwjglData.windowWidth = frameBufferWidth.get(0);
      lwjglData.windowHeight = frameBufferHeight.get(0);
      lwjglData.framebufferResized = true;
    }
    lwjglData.vidMode = vidMode;
    lwjglData.windowHandle = windowHandle;
    lwjglData.requiredExtensions = GLFWVulkan.glfwGetRequiredInstanceExtensions();
  }
  
  static void createSurface(LwjglData lwjglData) {
    try (MemoryStack stack = MemoryStack.stackPush()) {
      LongBuffer pSurface = stack.longs(VK14.VK_NULL_HANDLE);
      
      int result = GLFWVulkan.glfwCreateWindowSurface(lwjglData.vkInstance, lwjglData.windowHandle, null, pSurface);
      
      if (result != VK14.VK_SUCCESS) {
        throw new RuntimeException("Failed to create window surface. " + result);
      }
      
      lwjglData.surface = pSurface.get(0);
    }
  }
  
  
  static void setShouldClose(LwjglData lwjglData) {
    GLFW.glfwSetWindowShouldClose(lwjglData.windowHandle, true);
  }
  
  static boolean shouldClose(LwjglData lwjglData) {
    return GLFW.glfwWindowShouldClose(lwjglData.windowHandle);
  }
  
  static void cleanupWindow(LwjglData lwjglData) {
    Callbacks.glfwFreeCallbacks(lwjglData.windowHandle);
    GLFW.glfwDestroyWindow(lwjglData.windowHandle);
    GLFW.glfwTerminate();
  }
  
  static void cleanupSurface(LwjglData lwjglData){
    KHRSurface.vkDestroySurfaceKHR(lwjglData.vkInstance, lwjglData.surface, null);
  }
  
  
  static void pollEvents() {
    GLFW.glfwPollEvents();
  }
}
