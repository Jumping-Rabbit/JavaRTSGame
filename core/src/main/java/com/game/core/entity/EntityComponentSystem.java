package com.game.core.entity;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

public class EntityComponentSystem {
  Int2ObjectOpenHashMap<Entity> unitTypeHashMap;
  EntityComponentSnapshot Snapshot1;
  EntityComponentSnapshot Snapshot2;
  volatile boolean isSnapshot1;
  
  public EntityComponentSystem(int size){
    Snapshot1 = new EntityComponentSnapshot(size);
    Snapshot2 = new EntityComponentSnapshot(size);
    isSnapshot1 = true;
  }
}
