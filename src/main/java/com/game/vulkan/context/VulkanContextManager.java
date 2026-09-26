package com.game.vulkan.context;

public class VulkanContextManager {
  VulkanDevice vulkanDevice;
  VulkanInstance vulkanInstance;
  VulkanWindow vulkanWindow;
  public VulkanContextManager(){
    vulkanInstance = new VulkanInstance(this);
    vulkanDevice = new VulkanDevice(this);
    vulkanWindow = new VulkanWindow(this);
  }
  public void cleanupDevice(){
    vulkanDevice.cleanup();
  }
  public void cleanupWindowAndInstance(){
    vulkanWindow.cleanup();
    vulkanInstance.cleanup();
  }
  public long getWindowHandle(){
    return vulkanWindow.getWindowHandle();
  }
  public void setShouldClose(){
    vulkanWindow.setShouldClose();
  }
  public boolean shouldClose(){
    return vulkanWindow.shouldClose();
  }
  public void pollEvents(){
    vulkanWindow.pollEvents();
  }
  public void waitIdle(){
    vulkanDevice.waitIdle();
  }
 
  
}
