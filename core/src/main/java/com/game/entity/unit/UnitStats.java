package com.game.entity.unit;

public class UnitStats {
    public final long speed;
    public final long turnSpeed;
    public final long damage;
    public final long attackSpeed;//in ticks
    public UnitStats (long speed, long turnSpeed, long damage, long attackSpeed){
        this.speed = speed;
        this.turnSpeed = turnSpeed;
        this.damage = damage;
        this.attackSpeed = attackSpeed;
    }
}
