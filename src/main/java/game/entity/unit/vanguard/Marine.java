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

    private Marine(Marine marine) {
        super(marine.id);
        player = marine.player;
        x = marine.x;
        y = marine.y;
        z = marine.z;
        lastX = marine.x;
        lastY = marine.y;
        lastZ = marine.z;
        hp = marine.hp;
        armor = marine.armor;
        speed = marine.speed;
        turnSpeed = marine.turnSpeed;
        direction = marine.direction;
        lastDirection = marine.lastDirection;
        damage = marine.damage;
        attackSpeed = marine.attackSpeed;
        ticksUntilAttack = marine.ticksUntilAttack;
        effects = new ArrayList<>();
        effects.addAll(marine.effects);
        unitState = marine.unitState;

        targetDirection = marine.targetDirection;
        targetX = marine.targetX;
        targetY = marine.targetY;
        commands = marine.commands;
        isSelected = marine.isSelected;
    }

    public static void init() {
        hasCollision = true;
        validCommandTypes = new ArrayList<>();
        validCommandTypes.add(InputType.RIGHT_CLICK);
        tags = EnumSet.of(Tags.LIGHT, Tags.BIOLOGICAL, Tags.RANGED);
        model = Models.vanguardMarine;
        maxHp = NumUtil.DTL(40);
    }

    @Override
    public Entity copy() {
        return new Marine(this);
    }

}
