package com.game.lwjgl.vulkan;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class RenderData {
  public final ObjectArrayList<Source2D> extra2D = new ObjectArrayList<>();
  public volatile LongArrayList UIX = new LongArrayList();
  public volatile LongArrayList UIY = new LongArrayList();
  public volatile LongArrayList UIData = new LongArrayList();
  public volatile LongArrayList UIData2 = new LongArrayList();
  public volatile IntArrayList UIType = new IntArrayList();
  public volatile IntArrayList UIColor = new IntArrayList();
  public volatile LongArrayList entityX = new LongArrayList();
  public volatile LongArrayList entityLastX = new LongArrayList();
  public volatile LongArrayList entityY = new LongArrayList();
  public volatile LongArrayList entityLastY = new LongArrayList();
  public volatile LongArrayList entityRadius = new LongArrayList();
  public volatile IntArrayList activeIndex = new IntArrayList();
  
  public volatile float alpha;
  public volatile Long2ObjectOpenHashMap<String> textMap = new Long2ObjectOpenHashMap<>();
  
  public volatile LongArrayList entityZ;
  public volatile LongArrayList entityLastZ;
  public volatile LongArrayList entityDirection;
  public volatile LongArrayList entityLastDirection;
  public volatile LongArrayList hp;
  
  public volatile IntArrayList activeSelectedIndex;
  public volatile LongArrayList entityModel;
  
}
