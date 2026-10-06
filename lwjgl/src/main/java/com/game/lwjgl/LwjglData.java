package com.game.lwjgl;

import com.game.lwjgl.Shaderc.Shaders;
import com.game.lwjgl.Vulkan.ComputePipelineTypes;
import com.game.lwjgl.Vulkan.PipelineTypes;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.vulkan.*;

public class LwjglData {
  public final int MAX_FRAMES_IN_FLIGHT = 1;
  
  //Window
  public int windowWidth;
  public int windowHeight;
  public GLFWVidMode vidMode;
  public long windowHandle;
  public PointerBuffer requiredExtensions;
  public long surface;
  public int currentFrame = 0;
  
  //Shaderc
  public Object2LongOpenHashMap<Shaders> shadersToLongHandle = new Object2LongOpenHashMap<>();
  
  //Pipeline
  public Object2LongOpenHashMap<PipelineTypes> vulkanPipelines = new Object2LongOpenHashMap<>();
  public Object2LongOpenHashMap<ComputePipelineTypes> vulkanComputePipelines = new Object2LongOpenHashMap<>();
  public long pipelineLayout2D;
  public long descriptorSetLayout2D;
  public long vertexBufferMemory;
  public long descriptorPool;
  public long textureImage;
  public long textureMemory;
  public long textureView;
  public long textureSampler;
  public long pipelineLayoutCollision;
  public long descriptorSetLayoutCollision;
  
  //Device
  public VkPhysicalDevice[] vkPhysicalDevices;
  public VkPhysicalDevice vkPhysicalDevice;
  public VkDevice vkDevice;
  public int graphicsQueueFamilyIndex = -1;
  public int presentQueueFamilyIndex = -1;
  public VkQueue graphicsQueue;
  public VkQueue presentQueue;
  
  //Instance
  public VkInstance vkInstance;
  
  //Swapchain
  public long swapchain;
  public long[] swapchainImages;
  public long[] swapchainImageViews;
  public int swapchainImageFormat;
  public int swapchainWidth;
  public int swapchainHeight;
  public volatile boolean framebufferResized;
  
  //sync
  public long[] imageAvailableSemaphores = new long[MAX_FRAMES_IN_FLIGHT];
  public long[] renderFinishedSemaphores = new long[MAX_FRAMES_IN_FLIGHT];
  public long[] inFlightFences = new long[MAX_FRAMES_IN_FLIGHT];
  
  //command buffer
  public VkCommandBuffer[] commandBuffers = new VkCommandBuffer[MAX_FRAMES_IN_FLIGHT];
  public long commandPool;
  
  public long descriptorSet;
  public long vertexBuffer;
  
}
