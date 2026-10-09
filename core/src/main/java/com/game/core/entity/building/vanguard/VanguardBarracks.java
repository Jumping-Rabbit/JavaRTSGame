package com.game.core.entity.building.vanguard;


import com.game.core.Init;
import com.game.core.entity.Entity;
import com.game.core.entity.EntityDimension;
import com.game.core.entity.EntityPosition;
import com.game.core.entity.EntityStats;
import com.game.core.entity.PlayerColor;
import com.game.core.entity.building.Building;
import com.game.core.entity.unit.UnitStats;
import com.game.core.input.InputType;
import com.game.core.screens.LoadingScreen;

import java.util.ArrayList;

import static com.game.core.utils.NumUtil.FTL;

@Init(stage = 2)
public class VanguardBarracks extends Building {
//  private static Models model;
  private static EntityDimension entityDimension;
//  private static EnumSet<Tags> tags;
  private static UnitStats unitStats;
  private static EntityStats entityStats;
  
  
//  @Override
//  public Models getModel() {
//    return model;
//  }
  
  @Override
  protected EntityStats getEntityStats() {
    return entityStats;
  }
  
  @Override
  protected EntityDimension getEntityDimension() {
    return entityDimension;
  }
  
//  @Override
//  public EnumSet<Tags> getTags() {
//    return tags;
//  }
//
  public VanguardBarracks(long x, long y, PlayerColor player/*, ModelInstance modelInstance*/) {
    super(player/*, modelInstance*/, new EntityPosition(x, y, 0, 0));
    hp = FTL((float) (Math.random() * 2000));
    effects = new ArrayList<>();
  }
  
  public static void init(LoadingScreen loadingScreen) {
    Entity.hasCollision = true;
    Entity.validCommandTypes = new ArrayList<>();
    Entity.validCommandTypes.add(InputType.RIGHT_CLICK);
    
//    model = Models.vanguardBarracks;
//    tags = EnumSet.of(Tags.HEAVY_ARMORED, Tags.MECHANICAL, Tags.UNMOVABLE);
    entityDimension = new EntityDimension(55, 60, 30);
    entityStats = new EntityStats(FTL(2000), FTL(3));
    loadingScreen.increment();
  }
}
