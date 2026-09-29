package com.game.lwjgl.internal.vulkan.context;

import com.game.lwjgl.internal.glfw.GlfwManager;
import com.game.lwjgl.internal.vulkan.VulkanManager;
import org.lwjgl.vulkan.VkDevice;

public class VulkanContextManager {
  VulkanManager vulkanManager;
  
  VulkanDevice vulkanDevice;
  VulkanInstance vulkanInstance;
  GlfwManager glfwManager;
  public VulkanContextManager(VulkanManager vulkanManager){
    this.vulkanManager = vulkanManager;
    vulkanInstance = new VulkanInstance(this);
    vulkanDevice = new VulkanDevice(this);
  }
  public VkDevice getVkDevice(){
    return vulkanDevice.getVkDevice();
  }
  public void waitIdle(){
    vulkanDevice.waitIdle();
  }
  public void cleanupDeviceAndInstance(){
    vulkanDevice.cleanup();
    vulkanInstance.cleanup();
  }
}
