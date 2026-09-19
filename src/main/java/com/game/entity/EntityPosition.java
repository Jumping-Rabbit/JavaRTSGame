package com.game.entity;

public class EntityPosition {
    public long x;
    public long y;
    public long z;
    public long lastX;
    public long lastY;
    public long lastZ;
    public long direction;
    public long lastDirection;
    public void tick(){
        lastX = x;
        lastY = y;
        lastZ = z;
        lastDirection = direction;
    }
    public EntityPosition(long x, long y, long z, long direction){
        this.x = x;
        this.lastX = x;
        this.y = y;
        this.lastY = y;
        this.z = z;
        this.lastZ = z;
        this.direction = direction;
        this.lastDirection = direction;
    }
}
