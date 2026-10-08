package com.game.lwjgl.api;

import com.game.lwjgl.Glfw.GlfwWindow;
import com.game.lwjgl.LwjglData;
import com.game.lwjgl.Shaderc.LwjglShaderc;
import com.game.lwjgl.Vulkan.*;

public class LwjglManager {
  private LwjglData lwjglData;
  
  public LwjglManager() {
    reload();
  }
  
  public void reload() {
    lwjglData = new LwjglData();
    GlfwWindow.makeWindow(lwjglData);
    VulkanInstance.makeVkInstance(lwjglData);
    VulkanDevice.getAllPhysicalDevices(lwjglData);
    VulkanDevice.chooseVkPhysicalDevice(lwjglData);
    GlfwWindow.createSurface(lwjglData);
    VulkanDevice.findGraphicsQueueFamilyIndex(lwjglData);
    VulkanDevice.makeVkDevice(lwjglData);
    VulkanDevice.getQueues(lwjglData);
    LwjglShaderc.compileShaders(lwjglData);
    VulkanSwapchain.makeSwapChain(lwjglData);
    VulkanSwapchain.getSwapchainImages(lwjglData);
    VulkanSwapchain.makeImageViews(lwjglData);
    VulkanPipeline.makePipelines(lwjglData);
    VulkanSync.makeSync(lwjglData);
    VulkanThreadState.makeThreadLocalBuffers(lwjglData);
    VulkanBuffers.makeVertexBuffer(lwjglData);
    VulkanImage.makeWhiteTexture(lwjglData);
    VulkanDescriptors.makeDescriptorSet(lwjglData);
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
    VulkanBuffers.cleanupBuffers(lwjglData);
    VulkanDescriptors.cleanupDescriptors(lwjglData);
    VulkanImage.cleanupTexture(lwjglData);
    VulkanCommandPool.cleanupCommandPool(lwjglData);
    VulkanSync.cleanupSync(lwjglData);
    VulkanPipeline.cleanupPipelines(lwjglData);
    LwjglShaderc.cleanupShaders(lwjglData);
    VulkanSwapchain.cleanupSwapchain(lwjglData);
    VulkanDevice.cleanupDevice(lwjglData);
    GlfwWindow.cleanupSurface(lwjglData);
    VulkanInstance.cleanupInstance(lwjglData);
    GlfwWindow.cleanupWindow(lwjglData);
  }
}
