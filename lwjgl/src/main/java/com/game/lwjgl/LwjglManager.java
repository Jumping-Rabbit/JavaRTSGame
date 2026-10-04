package com.game.lwjgl;

public class LwjglManager {

  
  private LwjglData lwjglData;
  public LwjglManager(){
    reload();
  }
  public void reload(){
    lwjglData = new LwjglData();
    GlfwWindow.makeWindow(lwjglData);
    VulkanInstance.makeVkInstance(lwjglData);
    VulkanDevice.getAllPhysicalDevices(lwjglData);
    VulkanDevice.chooseVkPhysicalDevice(lwjglData);
    VulkanDevice.findGraphicsQueueFamilyIndex(lwjglData);
    VulkanDevice.makeVkDevice(lwjglData);
    LwjglShaderc.compileShaders(lwjglData);
    VulkanPipeline.makePipelines(lwjglData);
  }
  
  public void pollEvents(){
    GlfwWindow.pollEvents();
  }
  public long getWindowHandle(){
    return lwjglData.windowHandle;
  }
  public boolean shouldClose(){
    return GlfwWindow.shouldClose(lwjglData);
  }
  public void setShouldClose(){
    GlfwWindow.setShouldClose(lwjglData);
  }
  public void cleanup(){
    VulkanDevice.waitIdle(lwjglData);
  }
  
  
  
}
