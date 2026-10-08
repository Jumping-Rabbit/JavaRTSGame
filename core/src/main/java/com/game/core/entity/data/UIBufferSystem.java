package com.game.core.entity.data;

public class UIBufferSystem {
  UIBufferSnapshot snapshot1;
  UIBufferSnapshot snapshot2;
  volatile boolean isSnapshot1;
  
  public UIBufferSystem(){
    snapshot1 = new UIBufferSnapshot();
    snapshot2 = new UIBufferSnapshot();
    isSnapshot1 = true;
  }
}
