package game.entity.unit;

import game.entity.Effects;
import game.entity.Entity;
import utils.NumUtil;
import game.entity.players;

import java.util.ArrayList;

public abstract class Unit extends Entity {
    protected long hp;
    protected long armor;
    protected long speed;
    protected long turnSpeed;
    protected long damage;
    protected long attackSpeed;//in ticks
    protected long ticksUntilAttack;
    protected players player;
    protected ArrayList<Effects> effects;
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
}
