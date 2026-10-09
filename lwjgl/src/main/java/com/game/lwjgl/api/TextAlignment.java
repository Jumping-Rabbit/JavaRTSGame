package com.game.lwjgl.api;

public enum TextAlignment {
  TOP_LEFT(Align.TOP, Align.LEFT),
  TOP_MIDDLE(Align.TOP, Align.CENTER),
  TOP_RIGHT(Align.TOP, Align.RIGHT),
  
  CENTER_LEFT(Align.CENTER, Align.LEFT),
  CENTER_MIDDLE(Align.CENTER, Align.CENTER),
  CENTER_RIGHT(Align.CENTER, Align.RIGHT),
  
  BOTTOM_LEFT(Align.BOTTOM, Align.LEFT),
  BOTTOM_MIDDLE(Align.BOTTOM, Align.CENTER),
  BOTTOM_RIGHT(Align.BOTTOM, Align.RIGHT);
  
  public static final TextAlignment[] VALUES = values();
  private final int verticalAlign;
  private final int horizontalAlign;
  TextAlignment(int verticalAlign, int horizontalAlign) {
    this.verticalAlign = verticalAlign;
    this.horizontalAlign = horizontalAlign;
  }
  
  public int getVerticalAlign() {
    return verticalAlign;
  }
  
  public int getHorizontalAlign() {
    return horizontalAlign;
  }
  
  public static final class Align {
    public static final int TOP = 0, CENTER = 1, BOTTOM = 2; // vertical
    public static final int LEFT = 0, RIGHT = 2; // horizontal
    
    private Align() {
    }
  }
}