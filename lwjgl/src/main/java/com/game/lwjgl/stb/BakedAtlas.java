package com.game.lwjgl.stb;

import java.nio.ByteBuffer;

public class BakedAtlas {
  public ByteBuffer pixelData;
  public int atlasWidth = 512;
  public int atlasHeight = 512;
  public float ascent;
  public float descent;
  public GlyphInfo[] glyphs = new GlyphInfo[96];
}
