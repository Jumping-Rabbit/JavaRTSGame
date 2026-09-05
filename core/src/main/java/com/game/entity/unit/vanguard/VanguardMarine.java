package com.game.entity.unit.vanguard;

import com.game.entity.*;
import com.game.entity.unit.Unit;
import com.game.entity.unit.UnitState;
import com.game.entity.unit.UnitStats;
import com.game.inputHandler.InputType;
import com.game.Models;

import java.util.ArrayList;
import java.util.EnumSet;

import static com.game.utils.NumUtil.FTL;

@Init
public class VanguardMarine extends Unit {
    private static Models model;
    private static EntityDimension entityDimension;
    private static EnumSet<Tags> tags;
    private static UnitStats unitStats;
    private static EntityStats entityStats;

    @Override
    protected UnitStats getUnitStats() {
        return unitStats;
    }

    @Override
    protected EntityStats getEntityStats() {
        return entityStats;
    }

    @Override
    public EnumSet<Tags> getTags(){
        return tags;
    }


    @Override
    protected EntityDimension getEntityDimension(){
        return entityDimension;
    }

    @Override
    public Models getModel() {
        return model;
    }

    public VanguardMarine(long x, long y, PlayerColor player) {
        super(player, Models.getModelInstance(Models.vanguardMarine), new EntityPosition(x, y, 0, 0));
        tags = EnumSet.of(Tags.LIGHT_ARMORED, Tags.BIOLOGICAL, Tags.RANGED);
        hp = FTL(50);
        ticksUntilAttack = 4;
        effects = new ArrayList<>();
        unitState = UnitState.IDLE;
    }


    public static void init() {
        hasCollision = true;
        validCommandTypes = new ArrayList<>();
        validCommandTypes.add(InputType.RIGHT_CLICK);

        model = Models.vanguardMarine;
        entityDimension = new EntityDimension(16, 10, 8);
        unitStats = new UnitStats(FTL(10), FTL(100), FTL(0.5f), FTL(4));
        entityStats = new EntityStats(FTL(50), FTL(1));
    }

}
