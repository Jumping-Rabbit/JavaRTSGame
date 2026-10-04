package com.game.lwjgl;

import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkInstance;
import org.lwjgl.vulkan.VkPhysicalDevice;

class LwjglData {
  //Window
  int windowWidth;
  int windowHeight;
  GLFWVidMode vidMode;
  long windowHandle;
  
  //Shaderc
  Object2LongOpenHashMap<Shaders> shadersToLongHandle = new Object2LongOpenHashMap<>();
  
  //Pipeline
  Object2LongOpenHashMap<PipelineTypes> vulkanPipelines = new Object2LongOpenHashMap<>();
  Object2LongOpenHashMap<ComputePipelineTypes> vulkanComputePipelines = new Object2LongOpenHashMap<>();
  
  //Device
  VkPhysicalDevice[] vkPhysicalDevices;
  VkPhysicalDevice vkPhysicalDevice;
  VkDevice vkDevice;
  int graphicsQueueFamilyIndex;
  
  //Instance
  VkInstance vkInstance;
}
