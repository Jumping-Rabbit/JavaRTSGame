package entity.unit;

import entity.Effects;
import entity.Entity;
import entity.players;

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
    protected Entity attckTarget;

    public abstract Unit copy();
}
