package com.game.screens;

import com.game.Fonts;
import com.game.draw.DrawManager;

import java.util.concurrent.atomic.AtomicInteger;

public class LoadingScreen/* implements Screen */ {
  private final AtomicInteger loading = new AtomicInteger(0);
  private volatile String text = "";
  private final int total;
  
  public LoadingScreen(int total) {
    this.total = total;
  }
  
  public void increment() {
    loading.incrementAndGet();
  }
  
  public synchronized void addText(String text) {
    this.text = text + "\n" + this.text;
//        System.out.println(text);
  }
  
  public synchronized String getText() {
    return text;
  }
  
  public void draw() {
    DrawManager.clearCanvas();
    DrawManager.fillRect(0, 0, 1920, 1080, 0x000000FF);
    DrawManager.strokeRect(200, 880, 1520, 80, 0xFFFFFFFF, 4);
    DrawManager.fillRect(200, 880, ((float) loading.get() / total) * 1520, 80, 0xFFFFFFFF);
    DrawManager.fillText("(" + loading + "/" + total + ") " + text, 960, 780, Fonts.DEFAULT, 20/*, StringAlignment.TOP_MIDDLE*/, 0xFFFFFFFF);
  }

//    @Override
//    public void show() {
//
//    }
//
//    @Override
//    public void render(float v) {
//
//    }
//
//    @Override
//    public void resize(int i, int i1) {
//
//    }
//
//    @Override
//    public void pause() {
//
//    }
//
//    @Override
//    public void resume() {
//
//    }
//
//    @Override
//    public void hide() {
//
//    }
//
//    @Override
//    public void dispose() {
//
//    }
}
