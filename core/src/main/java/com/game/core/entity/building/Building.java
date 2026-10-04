package com.game.core.entity.building;

import com.game.core.entity.Entity;
import com.game.core.entity.EntityPosition;
import com.game.core.entity.PlayerColor;

public abstract class Building extends Entity {
  
  public Building(PlayerColor color/*, ModelInstance modelInstance*/, EntityPosition entityPosition) {
    super(color/*, modelInstance*/, entityPosition);
  }
  
  @Override
  public void updateOnFrame() {
    entityPosition.tick();
  }
  
  protected long timer;
}
