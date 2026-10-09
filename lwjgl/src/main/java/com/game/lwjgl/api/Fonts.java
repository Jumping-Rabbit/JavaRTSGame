package com.game.lwjgl.api;

public enum Fonts {
  DEFAULT("resources/fonts/SpaceMono-Regular.ttf");
  private final String filePath;
  
  Fonts(String filePath) {
    this.filePath = filePath;
  }
  
  public String getFilePath() {
    return filePath;
  }
}
