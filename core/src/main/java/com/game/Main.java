package com.game;

//import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
//import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

public class Main {
  private static GameCore gameCore;
  
  static void main(String[] args) {
//        System.setProperty("org.lwjgl.librarypath", "");
    gameCore = new GameCore();
    gameCore.init();
  }
}
