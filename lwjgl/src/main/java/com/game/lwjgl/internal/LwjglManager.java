package com.game.lwjgl.internal;

import com.game.lwjgl.internal.assimp.AssimpManager;
import com.game.lwjgl.internal.glfw.GlfwManager;
import com.game.lwjgl.internal.ktx.KtxManager;
import com.game.lwjgl.internal.openAl.OpenAlManager;
import com.game.lwjgl.internal.shaderc.ShadercManager;
import com.game.lwjgl.internal.stb.StbManager;
import com.game.lwjgl.internal.vma.VmaManager;
import com.game.lwjgl.internal.vulkan.VulkanManager;

public class LwjglManager {
  private final AssimpManager assimpManager;
  private final GlfwManager glfwManager;
  private final KtxManager ktxManager;
  private final OpenAlManager openAlManager;
  private final ShadercManager shadercManager;
  private final StbManager stbManager;
  private final VmaManager vmaManager;
  private final VulkanManager vulkanManager;
  
  public LwjglManager(){
    assimpManager = new AssimpManager();
    glfwManager = new GlfwManager();
    ktxManager = new KtxManager();
    openAlManager = new OpenAlManager();
    shadercManager = new ShadercManager();
    stbManager = new StbManager();
    vmaManager = new VmaManager();
    vulkanManager = new VulkanManager();
  }
  public void cleanup(){
    vulkanManager.cleanup();
    glfwManager.cleanup();
  }
  
  public AssimpManager getAssimpManager() {
    return assimpManager;
  }
  
  public GlfwManager getGlfwManager() {
    return glfwManager;
  }
  
  public KtxManager getKtxManager() {
    return ktxManager;
  }
  
  public OpenAlManager getOpenAlManager() {
    return openAlManager;
  }
  
  public ShadercManager getShadercManager() {
    return shadercManager;
  }
  
  public StbManager getStbManager() {
    return stbManager;
  }
  
  public VmaManager getVmaManager() {
    return vmaManager;
  }
  
  public VulkanManager getVulkanManager() {
    return vulkanManager;
  }
  

  
  
}
