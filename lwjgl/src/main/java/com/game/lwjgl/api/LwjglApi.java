package com.game.lwjgl.api;

import com.game.lwjgl.internal.LwjglManager;

public class LwjglApi {
  LwjglManager lwjglManager;
  
  public LwjglApi(){
    lwjglManager = new LwjglManager();
  }
  
  public void cleanup(){
    lwjglManager.cleanup();
  }
  
  public void setShouldClose() {
    lwjglManager.getGlfwManager().setShouldClose();
  }
  
  public boolean shouldClose() {
    return !lwjglManager.getGlfwManager().shouldClose();
  }
  
  public long getWindowHandle() {
    return lwjglManager.getGlfwManager().getWindowHandle();
  }
  
  public void pollEvents() {
    lwjglManager.getGlfwManager().pollEvents();
  }
}
