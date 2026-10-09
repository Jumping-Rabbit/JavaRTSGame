package com.game.lwjgl.vulkan;

import com.game.lwjgl.api.TextAlignment;
import com.game.lwjgl.stb.BakedAtlas;
import com.game.lwjgl.stb.GlyphInfo;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public final class Batch2D {
  public static final int STRIDE = 40;
  public static final int RECT = 0, CIRCLE = 1, LINE = 2, TEXT = 3;
  private static final float ATLAS_EM_PX = 52f * 0.45f;
  
  private final ByteBuffer buffer;
  private final int max;
  public int count;
  
  public Batch2D(ByteBuffer slice, int max) {
    this.buffer = slice.order(ByteOrder.nativeOrder());
    this.max = max;
  }
  
  public static int rgba(int r, int g, int b, int a) {
    return (a << 24) | (b << 16) | (g << 8) | r;
  }
  
  public static float textWidth(String s, BakedAtlas atlas, float size) {
    float k = size / ATLAS_EM_PX;
    float w = 0;
    for (int i = 0; i < s.length(); i++) {
      int glyphIndex = s.charAt(i) - 32;
      if (glyphIndex < 0 || glyphIndex >= 96) continue;
      w += atlas.glyphs[glyphIndex].advance;
    }
    return w * k;
  }
  
  public void reset() {
    count = 0;
  }
  
  private void put(float a, float b, float c, float d, float e, float f, float g, float h, int rgba, int type) {
    if (count >= max) return;
    int o = count++ * STRIDE;
    buffer.putFloat(o, a).putFloat(o + 4, b).putFloat(o + 8, c).putFloat(o + 12, d);
    buffer.putFloat(o + 16, e).putFloat(o + 20, f).putFloat(o + 24, g).putFloat(o + 28, h);
    buffer.putInt(o + 32, rgba).putInt(o + 36, type);
  }
  
  public void rect(float x, float y, float w, float h, int c) {
    put(x, y, w, h, 0, 0, 0, 0, c, RECT);
  }
  
  public void circle(float cx, float cy, float r, int c) {
    put(cx, cy, r, 0, 0, 0, 0, 0, c, CIRCLE);
  }
  
  public void line(float x1, float y1, float x2, float y2, float w, int c) {
    put(x1, y1, x2, y2, w, 0, 0, 0, c, LINE);
  }
  
  public void text(String s, BakedAtlas atlas, int font, float x, float y, float size, int c, TextAlignment align) {
    float k = size / ATLAS_EM_PX;
    
    float left = switch (align.getHorizontalAlign()) {
      case TextAlignment.Align.CENTER -> x - textWidth(s, atlas, size) * 0.5f;
      case TextAlignment.Align.RIGHT -> x - textWidth(s, atlas, size);
      default -> x;
    };
    
    float baseline = switch (align.getVerticalAlign()) {
      case TextAlignment.Align.CENTER -> y + (atlas.ascent + atlas.descent) * 0.5f * k;
      case TextAlignment.Align.BOTTOM -> y + atlas.descent * k;
      default -> y + atlas.ascent * k;
    };
    
    float px = left;
    for (int i = 0; i < s.length(); i++) {
      int glyphIndex = s.charAt(i) - 32;
      if (glyphIndex < 0 || glyphIndex >= 96) continue;
      GlyphInfo glyphInfo = atlas.glyphs[glyphIndex];
      if (glyphInfo.width > 0) {
        put(px + glyphInfo.xOffset * k, baseline + glyphInfo.yOffset * k, glyphInfo.width * k, glyphInfo.height * k,
            glyphInfo.u0, glyphInfo.v0, glyphInfo.u1, glyphInfo.v1, c, TEXT | (font << 8));
      }
      px += glyphInfo.advance * k;
    }
  }
}