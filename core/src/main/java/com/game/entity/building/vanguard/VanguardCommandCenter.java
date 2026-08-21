package com.game.entity.building.vanguard;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.game.entity.*;
import com.game.entity.building.Building;
import com.game.entity.unit.UnitStats;
import com.game.inputHandler.InputType;
import com.game.utils.Models;

import java.util.ArrayList;
import java.util.EnumSet;

import static com.game.utils.NumUtil.FTL;

@Init
public class VanguardCommandCenter extends Building {
    private static Models model;
    private static EntityDimension entityDimension;
    private static EnumSet<Tags> tags;
    private static UnitStats unitStats;
    private static EntityStats entityStats;


    @Override
    public Models getModel() {
        return model;
    }

    @Override
    protected EntityStats getEntityStats() {
        return null;
    }

    @Override
    protected EntityDimension getEntityDimension() {
        return null;
    }

    @Override
    public EnumSet<Tags> getTags() {
        return null;
    }

    public VanguardCommandCenter(long x, long y, PlayerColor player, ModelInstance modelInstance) {
        super(player, modelInstance, new EntityPosition(x, y, 0, 0));
        hp = FTL((float) (Math.random() * 2000));
        effects = new ArrayList<>();
    }

    public static void init() {
        Entity.hasCollision = true;
        Entity.validCommandTypes = new ArrayList<>();
        Entity.validCommandTypes.add(InputType.RIGHT_CLICK);

        tags = EnumSet.of(Tags.SUPER_HEAVY_ARMORED, Tags.MECHANICAL, Tags.UNMOVABLE);

        model = Models.vanguardCommandCenter;
        entityDimension = new EntityDimension(100, 100, 50);
        entityStats = new EntityStats(FTL(2000), FTL(3));
    }
}
