package com.game.lwjgl.internal.glfw;

public class GlfwManager {
  private GlfwWindow glfwWindow;
  public GlfwManager(){
    glfwWindow = new GlfwWindow(this);
  }
  public void cleanup(){
    glfwWindow.cleanup();
  }
  
  public void setShouldClose() {
    glfwWindow.setShouldClose();
  }
  
  public boolean shouldClose() {
    return glfwWindow.shouldClose();
  }
  
  
  public long getWindowHandle() {
    return glfwWindow.getWindowHandle();
  }
  
  public void pollEvents() {
    glfwWindow.pollEvents();
  }
}
