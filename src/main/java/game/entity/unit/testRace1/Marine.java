package game.entity.unit.testRace1;

import game.entity.Command;
import javafx.scene.image.Image;
import utils.NumUtil;
import game.entity.players;
import game.entity.unit.Unit;
import game.entity.unit.UnitState;
import utils.DrawUtil;
import inputHandler.InputType;

import java.util.ArrayList;

public class Marine extends Unit {
    private static final Image MARINE_IMAGE = new Image(Marine.class.getResource("/units/testRace1/marine/blueMarine.png").toExternalForm());
    public Marine(DrawUtil drawUtil, long x, long y, players player){
        this.player = player;
        this.drawUtil = drawUtil;
        this.x = NumUtil.DTL(x);
        this.y = NumUtil.DTL(y);
        lastX = NumUtil.DTL(x);
        lastY = NumUtil.DTL(y);
        validCommandTypes = new ArrayList<>();
        validCommandTypes.add(InputType.RIGHT_CLICK);
        hp = NumUtil.DTL(40);
        armor = NumUtil.DTL(1);
        speed = NumUtil.DTL(10);
        turnSpeed = NumUtil.DTL(100);
        direction = NumUtil.DTL(0);
        damage = NumUtil.DTL(0.5);
        attackSpeed = 1;
        ticksUntilAttack = 1;
        effects = new ArrayList<>();
        hasCollision = true;
        unitState = UnitState.IDLE;
        radius = NumUtil.DTL(20);
        collisionRadius = NumUtil.DTL(25);
        image = MARINE_IMAGE;
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
        validCommandTypes = new ArrayList<>();
        validCommandTypes.add(InputType.RIGHT_CLICK);
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

        hasCollision = true;
        unitState = marine.unitState;

        targetDirection = marine.targetDirection;
        targetX = marine.targetX;
        targetY = marine.targetY;
        radius = marine.radius;
        collisionRadius = marine.collisionRadius;

        commands = marine.commands;
        image = MARINE_IMAGE;
    }

    public void draw(){
        drawUtil.startRotation(NumUtil.LTD(lastX), NumUtil.LTD(lastY), NumUtil.LTD(x), NumUtil.LTD(y), 20, 20, NumUtil.LTD(lastDirection), NumUtil.LTD(direction));
        drawUtil.fillImageInterpolate(image, NumUtil.LTD(lastX), NumUtil.LTD(lastY), 40, 40, NumUtil.LTD(x), NumUtil.LTD(y));
//        drawUtil.setColor(0, 0, 0);
//        drawUtil.fillRectInterpolate(NumUtil.LTD(lastX) + 25, NumUtil.LTD(lastY)+30, 20, 5, NumUtil.LTD(x) + 25, NumUtil.LTD(y) + 30, NumUtil.LTD(lastDirection), NumUtil.LTD(direction));
//        drawUtil.setColor(80, 80, 80);
//        drawUtil.fillCircleInterpolate(NumUtil.LTD(lastX), NumUtil.LTD(lastY), 20, NumUtil.LTD(x), NumUtil.LTD(y), NumUtil.LTD(lastDirection), NumUtil.LTD(direction));
//        drawUtil.setColor(player.getColor());
//        drawUtil.fillCircleInterpolate(NumUtil.LTD(lastX) + 10, NumUtil.LTD(lastY) + 10,10, NumUtil.LTD(x) + 10, NumUtil.LTD(y)+10, NumUtil.LTD(lastDirection), NumUtil.LTD(direction));
////        drawUtil.fillRectInterpolate(NumUtil.LTD(lastX), NumUtil.LTD(lastY), 5, 40, NumUtil.LTD(x), NumUtil.LTD(y), NumUtil.LTD(lastDirection), NumUtil.LTD(direction));
//        drawUtil.fillRoundRectInterpolate(NumUtil.LTD(lastX), NumUtil.LTD(lastY), 10, 40, 10, NumUtil.LTD(x), NumUtil.LTD(y), NumUtil.LTD(lastDirection), NumUtil.LTD(direction));
        drawUtil.resetRotation();
    }
    public void updateOnFrame(){
//        /*
        if (!commands.isEmpty()){
            for (Command command : commands){
                if (command.getInputType() == InputType.RIGHT_CLICK){
                    unitState = UnitState.MOVING;
                    targetX = command.getX()-radius;
                    targetY = command.getY()-radius;
                    break;
                }
            }
        } else {
            unitState = UnitState.IDLE;
        }

        lastDirection = direction;
        lastX = x;
        lastY = y;
        switch (unitState){
            case MOVING:
                targetDirection = NumUtil.DTL(NumUtil.atan2(targetY - y, targetX - x));
                if (StrictMath.abs(direction - targetDirection) > turnSpeed){
                    if (direction > targetDirection){
                        if (direction - turnSpeed < targetDirection){
                            direction = targetDirection;
                        } else {
                            direction -= turnSpeed;
                        }
                    } else {
                        if (direction + turnSpeed > targetDirection){
                            direction = targetDirection;
                        } else {
                            direction += turnSpeed;
                        }
                    }
                } else {
                    direction = targetDirection;
                    long xChange = (long) (speed*NumUtil.cos(NumUtil.LTD(targetDirection)));
                    long yChange = (long) (speed*NumUtil.sin(NumUtil.LTD(targetDirection)));
                    if (targetX > x ? x+xChange >= targetX : x+xChange <= targetX){
                        x = targetX;
                    } else {
                        x += xChange;
                    }
                    if (targetY > y ? y+yChange >= targetY : y+yChange <= targetY){
                        y = targetY;
                    } else {
                        y += yChange;
                    }
                    if (((x - targetX) * (x - targetX)) + ((y - targetY) * (y - targetY)) <= (radius * radius)){
                        unitState = UnitState.IDLE;
                        removeCommand();
                    }
                }
//                System.out.println(NumUtil.longToDouble(targetDirection));
//                System.out.println("targetx:" + NumUtil.longToDouble(targetX) + ":" + NumUtil.longToDouble(x));
//                System.out.println("targety:" + NumUtil.longToDouble(targetY) + ":" + NumUtil.longToDouble(y));
                break;
        }
//        */
    }

}
