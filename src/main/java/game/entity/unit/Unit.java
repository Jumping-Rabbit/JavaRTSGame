package game.entity.unit;

import game.entity.*;
import game.screen.Game;
import inputHandler.InputType;
import utils.DrawUtil;
import utils.NumUtil;

import java.util.ArrayList;
import java.util.EnumSet;

import static utils.NumUtil.LTD;

public abstract class Unit extends Entity {

    protected static ArrayList<Abilities> abilities;
    protected static EnumSet<Tags> tags;
    protected ArrayList<Effects> effects;
    protected long armor;
    protected static long maxHp;
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
    protected Entity attackTarget;
    private Long newX;
    private Long newY;

    @Override
    public EnumSet<Tags> getTags(){
        return tags;
    }
    @Override
    public long getMaxHp(){
        return maxHp;
    }

    protected Unit(int id) {
        super(id);
    }

    public Unit() {
        super();
    }


    public long getTargetX() {
        return targetX;
    }

    public long getTargetY() {
        return targetY;
    }

    public void changeX(long change) {
        if (newX == null) {
            newX = x + change;
        } else {
            newX += change;
        }
    }

    public void changeY(long change) {
        if (newY == null) {
            newY = y + change;
        } else {
            newY += change;
        }
    }

    public void changeXImmediate(long change) {
        x += change;
    }

    public void changeYImmediate(long change) {
        y += change;
    }

    public void tick() {
        if (newX != null) {
            x = newX;
            newX = null;
        }
        if (newY != null) {
            y = newY;
            newY = null;
        }
    }

    public UnitState getUnitState() {
        return unitState;
    }

    public void setUnitState(UnitState unitState) {
        this.unitState = unitState;
    }

    public void draw() {
        DrawUtil.Game.fillModelScaled(model, x, lastX, y, lastY, z, lastZ, direction, lastDirection);
    }

    @Override
    public void drawTarget(){
        if (unitState != UnitState.MOVING){
            return;
        }//TODO:cull
        DrawUtil.Game.fillCircleScaled(targetX-50000, targetX-50000, targetY-50000, targetY-50000, 100000, 0x00FF00FF);
    }

    public void updateOnFrame() {
        if (!commands.isEmpty()) {
            for (Command command : commands) {
                if (command.getInputType() == InputType.RIGHT_CLICK) {
                    unitState = UnitState.MOVING;
                    targetX = command.getX() - model.getScaledHalfWidth();
                    targetY = command.getY() - model.getScaledHalfWidth();
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
//                System.out.println(delta);
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
                    if ((dx * dx) + (dy * dy) <= (model.getScaledHalfWidth() * model.getScaledHalfWidth())) {
                        unitState = UnitState.IDLE;
                        removeCommand();
                    }
                }
                break;
        }
    }
}
