package com.game.lwjgl;

import com.game.lwjgl.shaderc.Shaders;
import com.game.lwjgl.stb.BakedAtlas;
import com.game.lwjgl.vulkan.ComputePipelineTypes;
import com.game.lwjgl.vulkan.PipelineTypes;
import com.game.lwjgl.vulkan.RenderData;
import com.game.lwjgl.vulkan.VulkanRenderBatchTask2D;
import com.game.lwjgl.vulkan.VulkanThreadState;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.vulkan.VkCommandBuffer;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkInstance;
import org.lwjgl.vulkan.VkPhysicalDevice;
import org.lwjgl.vulkan.VkQueue;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;

public class LwjglData {
  public final int MAX_FRAMES_IN_FLIGHT = 1;
  public int renderThreads = 2;
  public int instancesPerTask = 1 << 14;
  public int taskCount;
  
  public RenderData renderData = new RenderData();
  
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
  public long descriptorPool;
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
  public long[] renderFinishedSemaphores;
  public long[] inFlightFences = new long[MAX_FRAMES_IN_FLIGHT];
  
  //command buffer
  public VkCommandBuffer[] commandBuffers = new VkCommandBuffer[MAX_FRAMES_IN_FLIGHT];
  
  public long descriptorSet;
  
  public long commandPool;                // main-thread pool (primary buffers + one-shot uploads)
  public long instanceBuffer, instanceMemory;
  public ByteBuffer instanceMapped;
  
  //render
  public ExecutorService renderExecutor;
  public List<VulkanRenderBatchTask2D> renderTasks;
  public ConcurrentLinkedQueue<VulkanThreadState.ThreadState> threadStates = new ConcurrentLinkedQueue<>();
  public ThreadLocal<VulkanThreadState.ThreadState> threadState;
  
  //stb
  public long fontImage;
  public long fontAllocation;
  public long fontImageView;
  public long fontSampler;
  public BakedAtlas[] bakedAtlases;
  
  //vma
  public long vmaAllocator;
}
