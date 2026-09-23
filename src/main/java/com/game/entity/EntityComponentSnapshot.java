package com.game.entity;

import it.unimi.dsi.fastutil.booleans.BooleanArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.longs.LongArrayList;

public class EntityComponentSnapshot {
  LongArrayList xList;
  LongArrayList lastXList;
  LongArrayList yList;
  LongArrayList lastYList;
  LongArrayList directionList;
  LongArrayList lastDirectionList;
  LongArrayList hpList;
  BooleanArrayList isSelectedList;
  BooleanArrayList isActiveList;
  IntArrayList activeIndexList;
  IntArrayList nextInCellList;
  IntArrayList unitTypeList;
}
