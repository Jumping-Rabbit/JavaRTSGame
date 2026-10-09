package com.game.lwjgl.api;

import com.game.lwjgl.LwjglData;
import com.game.lwjgl.glfw.GlfwWindow;
import com.game.lwjgl.shaderc.LwjglShaderc;
import com.game.lwjgl.stb.StbSDF;
import com.game.lwjgl.vma.VmaAllocator;
import com.game.lwjgl.vulkan.Source2D;
import com.game.lwjgl.vulkan.VulkanBuffers;
import com.game.lwjgl.vulkan.VulkanCommandPool;
import com.game.lwjgl.vulkan.VulkanDescriptors;
import com.game.lwjgl.vulkan.VulkanDevice;
import com.game.lwjgl.vulkan.VulkanInstance;
import com.game.lwjgl.vulkan.VulkanPipeline;
import com.game.lwjgl.vulkan.VulkanRender;
import com.game.lwjgl.vulkan.VulkanSwapchain;
import com.game.lwjgl.vulkan.VulkanSync;
import com.game.lwjgl.vulkan.VulkanThreadState;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import org.lwjgl.vulkan.VK14;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class LwjglManager {
  private final int renderThreads;
  private LwjglData lwjglData;
  
  public LwjglManager() {
    this(2);
  }
  
  public LwjglManager(int renderThreads) {
    this.renderThreads = Math.max(1, renderThreads);
    reload();
  }
  
  public void reload() {
    lwjglData = new LwjglData();
    lwjglData.renderThreads = renderThreads;
    
    GlfwWindow.makeWindow(lwjglData);
    VulkanInstance.makeVkInstance(lwjglData);
    VulkanDevice.getAllPhysicalDevices(lwjglData);
    VulkanDevice.chooseVkPhysicalDevice(lwjglData);
    GlfwWindow.createSurface(lwjglData);
    VulkanDevice.findGraphicsQueueFamilyIndex(lwjglData);
    VulkanDevice.makeVkDevice(lwjglData);
    VulkanDevice.getQueues(lwjglData);
    VmaAllocator.createAllocator(lwjglData);
    LwjglShaderc.compileShaders(lwjglData);
    VulkanSwapchain.makeSwapChain(lwjglData);
    VulkanSwapchain.getSwapchainImages(lwjglData);
    VulkanSwapchain.makeImageViews(lwjglData);
    VulkanPipeline.makePipelines(lwjglData);
    VulkanSync.makeSync(lwjglData);
    
    lwjglData.commandPool = VulkanCommandPool.makeCommandPool(lwjglData,
        VK14.VK_COMMAND_POOL_CREATE_RESET_COMMAND_BUFFER_BIT);
    lwjglData.commandBuffers = VulkanCommandPool.makeBuffers(lwjglData, lwjglData.commandPool,
        VK14.VK_COMMAND_BUFFER_LEVEL_PRIMARY, lwjglData.MAX_FRAMES_IN_FLIGHT);
    
    StbSDF.makeFonts(lwjglData);
    VulkanDescriptors.makeDescriptorSet(lwjglData);
    
    // multithreading
    VulkanThreadState.init(lwjglData);
    VulkanBuffers.makeInstanceBuffer(lwjglData);
    lwjglData.renderExecutor = Executors.newFixedThreadPool(lwjglData.renderThreads, r -> {
      Thread thread = new Thread(r, "vk-render");
      thread.setDaemon(true);
      return thread;
    });
  }
  
  public void render() {
    VulkanRender.drawFrame(lwjglData);
  }
  
  public void pollEvents() {
    GlfwWindow.pollEvents();
  }
  
  public long getWindowHandle() {
    return lwjglData.windowHandle;
  }
  
  public boolean shouldClose() {
    return GlfwWindow.shouldClose(lwjglData);
  }
  
  public void setShouldClose() {
    GlfwWindow.setShouldClose(lwjglData);
  }
  
  public void cleanup() {
    VulkanDevice.waitIdle(lwjglData);
    lwjglData.renderExecutor.shutdown();
    try {
      lwjglData.renderExecutor.awaitTermination(5, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
    
    VulkanThreadState.cleanup(lwjglData);
    VulkanBuffers.cleanupBuffers(lwjglData);
    VulkanDescriptors.cleanupDescriptors(lwjglData);
    StbSDF.cleanupFonts(lwjglData);
    VulkanCommandPool.cleanupCommandPool(lwjglData);
    VulkanSync.cleanupSync(lwjglData);
    VulkanPipeline.cleanupPipelines(lwjglData);
    LwjglShaderc.cleanupShaders(lwjglData);
    VulkanSwapchain.cleanupSwapchain(lwjglData);
    VmaAllocator.cleanupAllocator(lwjglData);
    VulkanDevice.cleanupDevice(lwjglData);
    GlfwWindow.cleanupSurface(lwjglData);
    VulkanInstance.cleanupInstance(lwjglData);
    GlfwWindow.cleanupWindow(lwjglData);
  }
  
  
  // UI
  public void bindUIX(LongArrayList value) {
    lwjglData.renderData.UIX = value;
  }
  
  public void bindUIY(LongArrayList value) {
    lwjglData.renderData.UIY = value;
  }
  
  public void bindUIData(LongArrayList value) {
    lwjglData.renderData.UIData = value;
  }
  
  public void bindUIData2(LongArrayList value) {
    lwjglData.renderData.UIData2 = value;
  }
  
  public void bindUIType(IntArrayList value) {
    lwjglData.renderData.UIType = value;
  }
  
  public void bindUIColor(IntArrayList value) {
    lwjglData.renderData.UIColor = value;
  }
  
  public void bindTextMap(Long2ObjectOpenHashMap<String> value) {
    lwjglData.renderData.textMap = value;
  }
  
  // entities
  public void bindEntityX(LongArrayList value) {
    lwjglData.renderData.entityX = value;
  }
  
  public void bindEntityLastX(LongArrayList value) {
    lwjglData.renderData.entityLastX = value;
  }
  
  public void bindEntityY(LongArrayList value) {
    lwjglData.renderData.entityY = value;
  }
  
  public void bindEntityLastY(LongArrayList value) {
    lwjglData.renderData.entityLastY = value;
  }
  
  public void bindEntityRadius(LongArrayList value) {
    lwjglData.renderData.entityRadius = value;
  }
  
  public void bindActiveIndex(IntArrayList value) {
    lwjglData.renderData.activeIndex = value;
  }
  
  public void setAlpha(float alpha) {
    lwjglData.renderData.alpha = alpha;
  }
  
  public void addSource2D(Source2D source) {
    lwjglData.renderData.extra2D.add(source);
  }
  
  public void removeSource2D(Source2D source) {
    lwjglData.renderData.extra2D.remove(source);
  }
  
}