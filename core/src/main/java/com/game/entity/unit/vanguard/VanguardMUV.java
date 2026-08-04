package com.game.entity.unit.vanguard;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.game.entity.Init;
import com.game.entity.PlayerColor;
import com.game.entity.Tags;
import com.game.entity.unit.Unit;
import com.game.entity.unit.UnitState;
import com.game.inputHandler.InputType;
import com.game.utils.Models;
import com.game.utils.NumUtil;

import java.util.ArrayList;
import java.util.EnumSet;

@Init
public class VanguardMUV extends Unit {
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

    public VanguardMUV(long x, long y, PlayerColor player, ModelInstance modelInstance) {
        super(player, modelInstance);

        this.x = NumUtil.FTL(x);
        this.y = NumUtil.FTL(y);
        this.z = 0;
        tags = EnumSet.of(Tags.LIGHT_ARMORED, Tags.BIOLOGICAL, Tags.RANGED);
        lastZ = 0;
        lastX = NumUtil.FTL(x);
        lastY = NumUtil.FTL(y);

        hp = NumUtil.FTL((float) (Math.random() * 40));
        armor = NumUtil.FTL(1);
        speed = NumUtil.FTL(10);
        turnSpeed = NumUtil.FTL(100);
        direction = NumUtil.FTL(0);
        damage = NumUtil.FTL(0.5f);
        attackSpeed = 4;
        ticksUntilAttack = 4;
        effects = new ArrayList<>();
        unitState = UnitState.IDLE;
    }


    public static void init() {
        hasCollision = true;
        validCommandTypes = new ArrayList<>();
        validCommandTypes.add(InputType.RIGHT_CLICK);

        model = Models.vanguardMUV;
        maxHp = NumUtil.FTL(50);
        Models.vanguardMUV.set(10, 10, 6, Models.ModelType.UNIT);
    }

}
