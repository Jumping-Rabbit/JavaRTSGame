package com.game.entity.building.vanguard;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.game.entity.Entity;
import com.game.entity.Init;
import com.game.entity.PlayerColor;
import com.game.entity.Tags;
import com.game.entity.building.Building;
import com.game.inputHandler.InputType;
import com.game.utils.Models;
import com.game.utils.NumUtil;

import java.util.ArrayList;
import java.util.EnumSet;

@Init
public class VanguardBarracks extends Building {
    private static Models model;
    private static long maxHp;

    @Override
    protected long getMaxHp() {
        return maxHp;
    }

    @Override
    public Models getModel() {
        return model;
    }

    public VanguardBarracks(long x, long y, PlayerColor player, ModelInstance modelInstance) {
        super(player, modelInstance);

        this.x = NumUtil.FTL(x);
        this.y = NumUtil.FTL(y);
        this.z = 0;
        tags = EnumSet.of(Tags.HEAVY_ARMORED, Tags.MECHANICAL, Tags.UNMOVABLE);
        lastZ = 0;
        lastX = NumUtil.FTL(x);
        lastY = NumUtil.FTL(y);
        hp = NumUtil.FTL((float) (Math.random() * 2000));
        armor = NumUtil.FTL(3);
        direction = NumUtil.FTL(0);
        effects = new ArrayList<>();
    }

    public static void init() {
        Entity.hasCollision = true;
        Entity.validCommandTypes = new ArrayList<>();
        Entity.validCommandTypes.add(InputType.RIGHT_CLICK);

        model = Models.vanguardBarracks;
        maxHp = NumUtil.FTL(2000);
        Models.vanguardBarracks.set(55, 60, 30, Models.ModelType.BUILDING);
    }
}
