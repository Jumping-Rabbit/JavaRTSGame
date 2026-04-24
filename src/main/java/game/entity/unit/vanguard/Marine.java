package game.entity.unit.vanguard;

import game.entity.Entity;
import game.entity.Tags;
import game.entity.players;
import game.entity.unit.Unit;
import game.entity.unit.UnitState;
import inputHandler.InputType;
import utils.DrawUtil;
import utils.Models;
import utils.NumUtil;

import java.util.ArrayList;
import java.util.EnumSet;

public class Marine extends Unit {
    public Marine(long x, long y, players player) {
        super();

        this.x = NumUtil.DTL(x);
        this.y = NumUtil.DTL(y);
        this.z = 0;

        lastZ = 0;
        lastX = NumUtil.DTL(x);
        lastY = NumUtil.DTL(y);
        this.player = player;
        hp = NumUtil.DTL(Math.random()*40);
        armor = NumUtil.DTL(1);
        speed = NumUtil.DTL(10);
        turnSpeed = NumUtil.DTL(100);
        direction = NumUtil.DTL(0);
        damage = NumUtil.DTL(0.5);
        attackSpeed = 4;
        ticksUntilAttack = 4;
        effects = new ArrayList<>();
        unitState = UnitState.IDLE;
    }


    public static void init() {
        hasCollision = true;
        validCommandTypes = new ArrayList<>();
        validCommandTypes.add(InputType.RIGHT_CLICK);
        tags = EnumSet.of(Tags.LIGHT, Tags.BIOLOGICAL, Tags.RANGED);
        model = Models.vanguardMarine;
        maxHp = NumUtil.DTL(40);
    }

}
