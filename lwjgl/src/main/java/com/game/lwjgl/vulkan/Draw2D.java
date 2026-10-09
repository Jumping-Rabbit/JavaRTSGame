package com.game.lwjgl.vulkan;

import com.game.lwjgl.api.TextAlignment;
import com.game.lwjgl.stb.BakedAtlas;

public final class Draw2D {
  private static final float FIXED = 10000f;
  
  private Draw2D() {
  }
  
  public static void entities(Batch2D batch2D, RenderData renderData, int start, int end) {
    for (int n = start; n < end; n++) {
      int e = renderData.activeIndex.getInt(n);
      long lx = renderData.entityLastX.getLong(e), ly = renderData.entityLastY.getLong(e);
      double x = lx + (renderData.entityX.getLong(e) - lx) * (double) renderData.alpha;
      double y = ly + (renderData.entityY.getLong(e) - ly) * (double) renderData.alpha;
      // TODO: do camera stuff here once i have that
      float r = renderData.entityRadius.getLong(e) / FIXED;
      batch2D.circle((float) (x / FIXED) + r, (float) (y / FIXED) + r, r, Batch2D.rgba(255, 255, 255, 255));
    }
  }
  
  public static void extras(Batch2D batch2D, RenderData renderData) {
    for (int i = 0; i < renderData.extra2D.size(); i++) renderData.extra2D.get(i).emit(batch2D, renderData);
  }
  
  public static void ui(Batch2D batch2D, RenderData renderData, BakedAtlas[] atlases, int start, int end) {
    for (int i = start; i < end; i++) {
      int t = renderData.UIType.getInt(i);
      float x = (float) (renderData.UIX.getLong(i) / (double) FIXED);
      float y = (float) (renderData.UIY.getLong(i) / (double) FIXED);
      long d1 = renderData.UIData.getLong(i);
      long d2 = renderData.UIData2.getLong(i);
      int c = Integer.reverseBytes(renderData.UIColor.getInt(i));
      switch (t & 0xFF) {
        case Batch2D.RECT -> batch2D.rect(x, y, (float) (d1 / (double) FIXED), (float) (d2 / (double) FIXED), c);
        case Batch2D.CIRCLE -> batch2D.circle(x, y, (float) (d1 / (double) FIXED), c);
        case Batch2D.TEXT -> {
          int font = (t >> 8) & 0xFF;
          TextAlignment align = TextAlignment.VALUES[Math.min((t >> 16) & 0xF, 8)];
          String s = renderData.textMap.get(d1);
          if (s != null) batch2D.text(s, atlases[font], font, x, y, (float) (d2 / (double) FIXED), c, align);
        }
      }
    }
  }
}