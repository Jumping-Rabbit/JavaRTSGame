package game.entity.unit;

import game.entity.*;
import inputHandler.InputType;
import utils.NumUtil;

import java.util.ArrayList;
import java.util.EnumSet;

public abstract class Unit extends Entity {

    protected ArrayList<Effects> effects;
    protected static ArrayList<Abilities> abilities;
    protected static EnumSet<Tags> tags;
    protected long hp;
    protected long maxHp;
    protected long armor;
    protected long speed;
    protected long turnSpeed;
    protected long damage;
    protected long attackSpeed;//in ticks
    protected long ticksUntilAttack;
    protected players player;

    protected UnitState unitState;
    protected long targetX;
    protected long targetY;
    protected long targetDirection;
    private Long newX;
    private Long newY;
    protected Entity attackTarget;
    public long getX(){
        return x;
    }
    public long getY(){
        return y;
    }
    public long getTargetX(){
        return targetX;
    }
    public long getTargetY(){
        return targetY;
    }
    public void changeX(long change){
        if (newX == null){
            newX = x+change;
        } else {
            newX += change;
        }
    }
    public void changeY(long change){
        if (newY == null){
            newY = y+change;
        } else {
            newY += change;
        }
    }
    public void tick(){
        if (newX != null){
            x = newX;
        }
        if (newY != null){
            y = newY;
        }
    }
    public long getLastX(){
        return lastX;
    }
    public long getLastY(){
        return lastY;
    }
    public UnitState getUnitState(){
        return unitState;
    }
    public void setUnitState(UnitState unitState){
        this.unitState = unitState;
    }
    public abstract Unit copy();
    public void draw(){
        drawUtil.startRotation(NumUtil.LTD(lastX), NumUtil.LTD(lastY), NumUtil.LTD(x), NumUtil.LTD(y), NumUtil.LTD(radius), NumUtil.LTD(radius), NumUtil.LTD(lastDirection), NumUtil.LTD(direction));
        drawUtil.fillImageInterpolate(image, NumUtil.LTD(lastX), NumUtil.LTD(lastY), NumUtil.LTD(radius)*2, NumUtil.LTD(radius)*2, NumUtil.LTD(x), NumUtil.LTD(y));
        drawUtil.resetRotation();
    }
    public void updateOnFrame() {
//        /*
        if (!commands.isEmpty()) {
            for (Command command : commands) {
                if (command.getInputType() == InputType.RIGHT_CLICK) {
                    unitState = UnitState.MOVING;
                    targetX = command.getX() - radius;
                    targetY = command.getY() - radius;
                    break;
                }
            }
        } else {
            unitState = UnitState.IDLE;
        }

        lastDirection = direction;
        lastX = x;
        lastY = y;
        switch (unitState) {
            case MOVING:
                targetDirection = NumUtil.atan2(targetY - y, targetX - x);
                long delta = targetDirection - direction;
                long scaled180 = NumUtil.DTL(180);
                long scaled360 = NumUtil.DTL(360);
                while (delta <= -scaled180) delta += scaled360;
                while (delta > scaled180) delta -= scaled360;
                if (StrictMath.abs(delta) <= turnSpeed) {
                    direction = targetDirection;
                } else {
                    if (delta > 0) direction += turnSpeed;
                    else direction -= turnSpeed;
                    if (direction <= -scaled180) direction += scaled360;
                    if (direction > scaled180) direction -= scaled360;
                }
                if (direction == targetDirection) {
                    long xChange = (long) (speed * NumUtil.cos(direction));
                    long yChange = (long) (speed * NumUtil.sin(direction));
                    if (targetX > x ? x + xChange >= targetX : x + xChange <= targetX) x = targetX;
                    else x += xChange;
                    if (targetY > y ? y + yChange >= targetY : y + yChange <= targetY) y = targetY;
                    else y += yChange;
                    long dx = x - targetX;
                    long dy = y - targetY;
                    if ((dx * dx) + (dy * dy) <= (radius * radius)) {
                        unitState = UnitState.IDLE;
                        removeCommand();
                    }
                }
                break;
        }
    }
}
