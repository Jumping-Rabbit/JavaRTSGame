package com.game.core;

public class Main {
  private static GameCore gameCore;
  
  static void main(String[] args) {
//        System.setProperty("org.lwjgl.librarypath", "");
    gameCore = new GameCore();
    gameCore.init();
  }
}
