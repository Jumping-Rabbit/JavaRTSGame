package com.game.core.entity.object;

import com.game.core.entity.Entity;
import com.game.core.entity.EntityPosition;
import com.game.core.entity.PlayerColor;

public abstract class Object extends Entity {
  public Object(PlayerColor color/*, ModelInstance modelInstance*/) {
    super(color/*, modelInstance*/, new EntityPosition(0, 0, 0, 0));
  }
  
//  protected static EnumSet<Tags> tags;
  
//  @Override
//  public EnumSet<Tags> getTags() {
//    return tags;
//  }
}
