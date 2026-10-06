package com.game.lwjgl.Vulkan;

import com.game.lwjgl.LwjglData;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK14;
import org.lwjgl.vulkan.VkApplicationInfo;
import org.lwjgl.vulkan.VkInstance;
import org.lwjgl.vulkan.VkInstanceCreateInfo;

public class VulkanInstance {
  
  private VulkanInstance() {}
  
  public static void makeVkInstance(LwjglData lwjglData) {
    try (MemoryStack stack = MemoryStack.stackPush()) {
      VkApplicationInfo appInfo = VkApplicationInfo.calloc(stack)
          .sType(VK14.VK_STRUCTURE_TYPE_APPLICATION_INFO)
          .pApplicationName(stack.UTF8("Java RTS Game"))
          .apiVersion(VK14.VK_API_VERSION_1_4);
      
      VkInstanceCreateInfo createInfo = VkInstanceCreateInfo.calloc(stack)
          .sType(VK14.VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO)
          .pApplicationInfo(appInfo)
          .ppEnabledExtensionNames(lwjglData.requiredExtensions);
      
      PointerBuffer pInstance = stack.mallocPointer(1);
      
      int result = VK14.vkCreateInstance(createInfo, null, pInstance);
      if (result != VK14.VK_SUCCESS) {
        throw new RuntimeException("Failed to create Vulkan instance. Error code: " + result);
      }
      
      long instanceHandle = pInstance.get(0);
      lwjglData.vkInstance = new VkInstance(instanceHandle, createInfo);
    }
  }
  
  public static void cleanupInstance(LwjglData lwjglData) {
    VK14.vkDestroyInstance(lwjglData.vkInstance, null);
  }
}
