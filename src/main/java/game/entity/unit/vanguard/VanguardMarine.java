package game.entity.unit.vanguard;

import game.entity.Init;
import game.entity.Tags;
import game.entity.players;
import game.entity.unit.Unit;
import game.entity.unit.UnitState;
import inputHandler.InputType;
import utils.Models;
import utils.NumUtil;

import java.util.ArrayList;
import java.util.EnumSet;
@Init
public class VanguardMarine extends Unit {
    private static Models model;
    private static long maxHp;
    @Override
    protected long getMaxHp(){
        return maxHp;
    }
    @Override
    protected Models getModel(){
        return model;
    }
    public VanguardMarine(long x, long y, players player) {
        super();

        this.x = NumUtil.DTL(x);
        this.y = NumUtil.DTL(y);
        this.z = 0;
        tags = EnumSet.of(Tags.LIGHT_ARMORED, Tags.BIOLOGICAL, Tags.RANGED);
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

        model = Models.vanguardMarine;
        maxHp = NumUtil.DTL(50);
        Models.vanguardMarine.set(10, 10, 6, Models.ModelType.UNIT);
    }

}
