package game.entity.unit.testRace1;

import game.entity.Command;
import javafx.scene.image.Image;
import utils.numUtil;
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
        this.x = numUtil.DTL(x);
        this.y = numUtil.DTL(y);
        lastX = numUtil.DTL(x);
        lastY = numUtil.DTL(y);
        validCommandTypes = new ArrayList<>();
        validCommandTypes.add(InputType.RIGHT_CLICK);
        hp = numUtil.DTL(40);
        armor = numUtil.DTL(1);
        speed = numUtil.DTL(10);
        turnSpeed = numUtil.DTL(100);
        direction = numUtil.DTL(0);
        damage = numUtil.DTL(0.5);
        attackSpeed = 1;
        ticksUntilAttack = 1;
        effects = new ArrayList<>();
        hasCollision = true;
        unitState = UnitState.IDLE;
        radius = 20;
        collisionRadius = 25;
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
        radius = 20;
        collisionRadius = 25;

        commands = marine.commands;
        image = MARINE_IMAGE;
    }

    public void draw(){
        drawUtil.startRotation(numUtil.LTD(lastX), numUtil.LTD(lastY), numUtil.LTD(x), numUtil.LTD(y), 20, 20, numUtil.LTD(lastDirection), numUtil.LTD(direction));
        drawUtil.fillImageInterpolate(image, numUtil.LTD(lastX), numUtil.LTD(lastY), 40, 40, numUtil.LTD(x), numUtil.LTD(y));
//        drawUtil.setColor(0, 0, 0);
//        drawUtil.fillRectInterpolate(numUtil.LTD(lastX) + 25, numUtil.LTD(lastY)+30, 20, 5, numUtil.LTD(x) + 25, numUtil.LTD(y) + 30, numUtil.LTD(lastDirection), numUtil.LTD(direction));
//        drawUtil.setColor(80, 80, 80);
//        drawUtil.fillCircleInterpolate(numUtil.LTD(lastX), numUtil.LTD(lastY), 20, numUtil.LTD(x), numUtil.LTD(y), numUtil.LTD(lastDirection), numUtil.LTD(direction));
//        drawUtil.setColor(player.getColor());
//        drawUtil.fillCircleInterpolate(numUtil.LTD(lastX) + 10, numUtil.LTD(lastY) + 10,10, numUtil.LTD(x) + 10, numUtil.LTD(y)+10, numUtil.LTD(lastDirection), numUtil.LTD(direction));
////        drawUtil.fillRectInterpolate(numUtil.LTD(lastX), numUtil.LTD(lastY), 5, 40, numUtil.LTD(x), numUtil.LTD(y), numUtil.LTD(lastDirection), numUtil.LTD(direction));
//        drawUtil.fillRoundRectInterpolate(numUtil.LTD(lastX), numUtil.LTD(lastY), 10, 40, 10, numUtil.LTD(x), numUtil.LTD(y), numUtil.LTD(lastDirection), numUtil.LTD(direction));
        drawUtil.resetRotation();
    }
    public void updateOnFrame(){
//        /*
        if (!commands.isEmpty()){
            for (Command command : commands){
                if (command.getInputType() == InputType.RIGHT_CLICK){
                    unitState = UnitState.MOVING;
                    targetX = numUtil.DTL(command.getX()-20);
                    targetY = numUtil.DTL(command.getY()-20);
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
                targetDirection = numUtil.DTL(Math.toDegrees(Math.atan2(targetY - y, targetX - x)));
                if (Math.abs(direction - targetDirection) > turnSpeed){
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
                    long xChange = (long) (speed*Math.cos(Math.toRadians(numUtil.LTD(targetDirection))));
                    long yChange = (long) (speed*Math.sin(Math.toRadians(numUtil.LTD(targetDirection))));
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
                    if ((numUtil.LTD(x - targetX) * numUtil.LTD(x - targetX)) + (numUtil.LTD(y - targetY) * numUtil.LTD(y - targetY)) <= (radius * radius)){

                        unitState = UnitState.IDLE;
                        removeCommand();
                    }
                }
//                System.out.println(numUtil.longToDouble(targetDirection));
//                System.out.println("targetx:" + numUtil.longToDouble(targetX) + ":" + numUtil.longToDouble(x));
//                System.out.println("targety:" + numUtil.longToDouble(targetY) + ":" + numUtil.longToDouble(y));
                break;
        }
//        */
    }

}
