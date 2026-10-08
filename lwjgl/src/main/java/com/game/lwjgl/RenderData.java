package com.game.lwjgl;

import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;

public class RenderData {
  
  public FloatArrayList UIX;
  public FloatArrayList UIY;
  public FloatArrayList UIData;
  public FloatArrayList UIData2;
  public IntArrayList UIType;
  
  public LongArrayList entityX;
  public LongArrayList entityLastX;
  public LongArrayList entityY;
  public LongArrayList entityLastY;
  public LongArrayList entityZ;
  public LongArrayList entityLastZ;
  public LongArrayList entityDirection;
  public LongArrayList entityLastDirection;
  public LongArrayList entityRadius;
  public LongArrayList hp;
  
  public IntArrayList activeIndex;
  public IntArrayList activeSelectedIndex;
  public LongArrayList entityModel;
  
  public float alpha;
  
  public Long2ObjectOpenHashMap<String> textMap;
}
