package entity.unit.testRace1;

import entity.Command;
import entity.numUtil;
import entity.players;
import entity.unit.Unit;
import entity.unit.UnitState;
import main.DrawUtil;
import main.inputHandler.InputType;

import java.util.ArrayList;

public class Marine extends Unit {

    public Marine(DrawUtil drawUtil, long x, long y, players player){
        this.player = player;
        this.drawUtil = drawUtil;
        this.x = numUtil.DTL(x);
        this.y = numUtil.DTL(y);
        lastX = numUtil.DTL(x);
        lastY = numUtil.DTL(y);
        validCommandTypes = new ArrayList<>();
        validCommandTypes.add(InputType.LEFT_CLICK);
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
    }
    public double getX(){
        return numUtil.LTD(x);
    }
    public double getY(){
        return numUtil.LTD(y);
    }
    public void draw(double factor){
        drawUtil.startRotation();
        drawUtil.setColor(80, 80, 80);
        drawUtil.fillCircleInterpolate(numUtil.LTD(lastX), numUtil.LTD(lastY), 20, numUtil.LTD(x), numUtil.LTD(y), factor, numUtil.LTD(lastDirection), numUtil.LTD(direction));
        drawUtil.setColor(player.getColor());
        drawUtil.fillCircleInterpolate(numUtil.LTD(lastX) + 10, numUtil.LTD(lastY) + 10,10, numUtil.LTD(x) + 10, numUtil.LTD(y)+10, factor, numUtil.LTD(lastDirection), numUtil.LTD(direction));
        drawUtil.fillRectInterpolate(numUtil.LTD(lastX), numUtil.LTD(lastY), 5, 20, numUtil.LTD(x), numUtil.LTD(y), factor, numUtil.LTD(lastDirection), numUtil.LTD(direction));
        drawUtil.resetRotation();
    }
    public void updateOnFrame(){
        for (Command command : commands){
            if (command.getInputType() == InputType.LEFT_CLICK){
                unitState = UnitState.MOVING;
                targetX = numUtil.DTL(command.getX()-20);
                targetY = numUtil.DTL(command.getY()-20);
            }
        }
        clearCommands();

        lastDirection = direction;
        lastX = x;
        lastY = y;
        switch (unitState){
            case MOVING:
                targetDirection = numUtil.DTL(Math.toDegrees(Math.atan2(targetY - y, targetX - x)));
                System.out.println(numUtil.LTD(direction));
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
                    int targetsReached = 0;
                    if (targetX > x ? x+xChange >= targetX : x+xChange <= targetX){
                        x = targetX;
                        targetsReached++;
                    } else {
                        x += xChange;
                    }
                    if (targetY > y ? y+yChange >= targetY : y+yChange <= targetY){
                        y = targetY;
                        targetsReached++;
                    } else {
                        y += yChange;
                    }
                    if (targetsReached == 2){
                        unitState = UnitState.IDLE;
                    }
                }
//                System.out.println(numUtil.longToDouble(targetDirection));
//                System.out.println("targetx:" + numUtil.longToDouble(targetX) + ":" + numUtil.longToDouble(x));
//                System.out.println("targety:" + numUtil.longToDouble(targetY) + ":" + numUtil.longToDouble(y));
                break;
            case IDLE:
                break;
        }
    }
}
