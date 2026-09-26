package com.game.entity;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

public class EntityComponentSystem {
  Int2ObjectOpenHashMap<Entity> unitTypeHashMap;
  EntityComponentSnapshot Snapshot1;
  EntityComponentSnapshot Snapshot2;
  volatile boolean isSnapshot1;
  public EntityComponentSystem(int size){
  
  }
}
