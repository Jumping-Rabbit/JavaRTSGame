package com.game.lwjgl;

import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.vulkan.*;

class LwjglData {
  final int MAX_FRAMES_IN_FLIGHT = 1;
  
  //Window
  int windowWidth;
  int windowHeight;
  GLFWVidMode vidMode;
  long windowHandle;
  PointerBuffer requiredExtensions;
  long surface;
  int currentFrame = 0;
  
  //Shaderc
  Object2LongOpenHashMap<Shaders> shadersToLongHandle = new Object2LongOpenHashMap<>();
  
  //Pipeline
  Object2LongOpenHashMap<PipelineTypes> vulkanPipelines = new Object2LongOpenHashMap<>();
  Object2LongOpenHashMap<ComputePipelineTypes> vulkanComputePipelines = new Object2LongOpenHashMap<>();
  long pipelineLayout2D;
  long descriptorSetLayout2D;
  long vertexBufferMemory;
  long descriptorPool;
  long textureImage;
  long textureMemory;
  long textureView;
  long textureSampler;
  
  //Device
  VkPhysicalDevice[] vkPhysicalDevices;
  VkPhysicalDevice vkPhysicalDevice;
  VkDevice vkDevice;
  int graphicsQueueFamilyIndex;
  int presentQueueFamilyIndex;
  VkQueue graphicsQueue;
  VkQueue presentQueue;
  //Instance
  VkInstance vkInstance;
  
  //Swapchain
  long swapchain;
  long[] swapchainImages;
  long[] swapchainImageViews;
  int swapchainImageFormat;
  int swapchainWidth;
  int swapchainHeight;
  volatile boolean framebufferResized;
  
  //sync
  long[] imageAvailableSemaphores = new long[MAX_FRAMES_IN_FLIGHT];
  long[] renderFinishedSemaphores = new long[MAX_FRAMES_IN_FLIGHT];
  long[] inFlightFences = new long[MAX_FRAMES_IN_FLIGHT];
  
  //command buffer
  VkCommandBuffer[] commandBuffers= new VkCommandBuffer[MAX_FRAMES_IN_FLIGHT];
  long commandPool;
  
  long descriptorSet;
  long vertexBuffer;

}
