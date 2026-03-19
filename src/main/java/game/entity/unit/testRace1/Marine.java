package game.entity.unit.testRace1;

import game.entity.Command;
import game.entity.Tags;
import javafx.scene.image.Image;
import utils.NumUtil;
import game.entity.players;
import game.entity.unit.Unit;
import game.entity.unit.UnitState;
import utils.DrawUtil;
import inputHandler.InputType;

import java.io.File;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Set;

public class Marine extends Unit {
    static {
        image = new Image(new File("resources/units/testRace1/marine/blueMarine.png").toURI().toString());
        radius = NumUtil.DTL(20);
        collisionRadius = NumUtil.DTL(25);
        hasCollision = true;
        validCommandTypes = new ArrayList<>();
        validCommandTypes.add(InputType.RIGHT_CLICK);
        tags = EnumSet.of(Tags.LIGHT, Tags.BIOLOGICAL, Tags.RANGED);
    }
    public Marine(DrawUtil drawUtil, long x, long y, players player){
        this.player = player;
        this.drawUtil = drawUtil;
        this.x = NumUtil.DTL(x);
        this.y = NumUtil.DTL(y);
        lastX = NumUtil.DTL(x);
        lastY = NumUtil.DTL(y);
        hp = NumUtil.DTL(40);
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
    @Override
    public Unit copy(){
        return new Marine(this);
    }

    private Marine(Marine marine){
        player = marine.player;
        drawUtil = marine.drawUtil;
        x = marine.x;
        y = marine.y;
        lastX = marine.x;
        lastY = marine.y;
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
    }

}
