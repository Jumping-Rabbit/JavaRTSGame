package com.game.entity;

import com.game.utils.NumUtil;

public class Snapshot {
    public long x;
    public long y;
    public long z;
    public long lastX;
    public long lastY;
    public long lastZ;
    public long direction;
    public long lastDirection;
    public boolean isSelected = false;
    public long hp;
    public long lerpY;

    public void set(Entity entity) {
        x = entity.entityPosition.x;
        y = entity.entityPosition.y;
        z = entity.entityPosition.z;
        lastX = entity.entityPosition.lastX;
        lastY = entity.entityPosition.lastY;
        lastZ = entity.entityPosition.lastZ;
        direction = entity.entityPosition.direction;
        lastDirection = entity.entityPosition.lastDirection;
        isSelected = entity.isSelected;
        hp = entity.hp;
    }
    public long getYLerp(){
        return lerpY;
    }
    public void setYLerp(float factor) {
        lerpY = NumUtil.interpolate(y, lastY, factor);
    }
}
