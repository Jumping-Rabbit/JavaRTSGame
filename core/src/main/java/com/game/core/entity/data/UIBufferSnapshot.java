package com.game.core.entity.data;

import com.game.core.Init;
import com.game.core.screens.LoadingScreen;
import it.unimi.dsi.fastutil.chars.Char2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.objects.Object2ObjectFunction;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

@Init(stage = 2)
public class UIBufferSnapshot {
  static Char2IntOpenHashMap charBits;//TODO:generate this on startup
  static Object2ObjectOpenHashMap<String, long[]> stringHashMap;
  static Object2ObjectFunction<String, long[]> longsGenerator = (string->{
    return new long[3];
  });//TODO: make sure this works and make it actaully computes the longs
  static long[] getLongs(String string){
    return stringHashMap.computeIfAbsent(string, longsGenerator);
  }
  public static void init(LoadingScreen loadingScreen){
    //generate charBits here;
  }
  //43 letters 6 bits per letter
  LongArrayList UIX;//13 bits of x when text
  LongArrayList UIY;//same for y
  LongArrayList UIData;
  LongArrayList UIData2;
  IntArrayList UIType;//last 4 bits is aligment type
  //could maybe store some data in here for strings but its fine.
  
  UIBufferSnapshot(){
    UIX = new LongArrayList();
    UIY = new LongArrayList();
    UIData = new LongArrayList();
    UIData2 = new LongArrayList();
    UIType = new IntArrayList();
  }
  //rect
  void addRectangle(long x, long y, long width, long height){
    UIX.add(x);
    UIY.add(y);
    UIData.add(width);
    UIData2.add(height);
    UIType.add(0);
  }
  
  //circle
  void addCicle(long x, long y, long radius){
    UIX.add(x);
    UIY.add(y);
    UIData.add(radius);
    UIData2.add(-1);
    UIType.add(1);
  }
  
  //string
  void addText(long x, long y, String string){
    UIX.add(x);
    UIY.add(y);
    long[] value = getLongs(string);
    UIData.add(value[0]);
    UIData2.add(value[1]);
    UIType.add((int)value[2]);//TODO: or the last 4 aligment bits onto the int
  }
}
