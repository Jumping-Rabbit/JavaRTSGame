package com.game.core.entity.data;

import com.game.core.entity.Entity;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

public class EntityComponentSystem {
  Int2ObjectOpenHashMap<Entity> unitTypeHashMap;
  EntityComponentSnapshot snapshot1;
  EntityComponentSnapshot snapshot2;
  volatile boolean isSnapshot1;
  
  public EntityComponentSystem(int size){
    snapshot1 = new EntityComponentSnapshot(size);
    snapshot2 = new EntityComponentSnapshot(size);
    isSnapshot1 = true;
  }
}
