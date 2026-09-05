package com.game.entity;


import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.game.inputHandler.InputType;
import com.game.utils.DrawUtil;
import com.game.Models;
import com.game.utils.NumUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;

public abstract class Entity {
    public static final Comparator<Entity> Y_SNAP_1_COMPARATOR = Comparator.comparingLong(entity -> entity.snapshot1.getYLerp());
    public static final Comparator<Entity> Y_SNAP_2_COMPARATOR = Comparator.comparingLong(entity -> entity.snapshot2.getYLerp());
    protected static boolean hasCollision;
    protected static ArrayList<InputType> validCommandTypes = new ArrayList<>();
    private static int idNum = 0;
    final public int id;

    public ModelInstance modelInstance;

    public int nextInCell1 = -1;
    public int nextInCell2 = -1;

    public abstract Models getModel();
    
    protected EntityPosition entityPosition;
    protected PlayerColor playerColor;


    protected boolean isSelected = false;
    protected long hp;

    protected Snapshot snapshot1;
    protected Snapshot snapshot2;
    protected ArrayList<Command> commands = new ArrayList<>();


    protected abstract EntityStats getEntityStats();

    protected abstract EntityDimension getEntityDimension();

    public abstract EnumSet<Tags> getTags();

    protected ArrayList<Effects> effects;

    public Snapshot getSnapshot(boolean isSnapshot1) {
        if (isSnapshot1) {
            return snapshot1;
        } else {
            return snapshot2;
        }
    }

    public void setSnapshot1() {
        snapshot1.set(this);
    }

    public void setSnapshot2() {
        snapshot2.set(this);
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public Entity(PlayerColor playerColor, ModelInstance modelInstance, EntityPosition entityPosition) {
        id = idNum;
        idNum++;
        snapshot1 = new Snapshot();
        snapshot2 = new Snapshot();
        this.playerColor = playerColor;
        this.modelInstance = modelInstance;
        this.entityPosition = entityPosition;
    }



    public void draw(boolean isSnapshot1) {
        if (isSnapshot1) {
            DrawUtil.Game.fillModelScaled(modelInstance, snapshot1.x, snapshot1.lastX, snapshot1.y, snapshot1.lastY, snapshot1.z, snapshot1.lastZ, snapshot1.direction, snapshot1.lastDirection);
        } else {
            DrawUtil.Game.fillModelScaled(modelInstance, snapshot2.x, snapshot2.lastX, snapshot2.y, snapshot2.lastY, snapshot2.z, snapshot2.lastZ, snapshot2.direction, snapshot2.lastDirection);
        }
    }

    public abstract void updateOnFrame();

    public void clearCommands() {
        commands.clear();
    }

    public void removeCommand() {
        if (!commands.isEmpty()) {
            commands.removeFirst();
        }
    }

    public void addCommand(Command command) {
        if (validCommandTypes.contains(command.getInputType())) {
            commands.add(command);
        }
    }

    public void drawSelectedRing(boolean isSnapshot1) {
        if (isSnapshot1) {
            DrawUtil.Game.fillCircle(NumUtil.LTF(snapshot1.x), NumUtil.LTF(snapshot1.lastX), NumUtil.LTF(snapshot1.y), NumUtil.LTF(snapshot1.lastY), getEntityDimension().radius, 0x00FF0067);
//            DrawUtil.Game.fillCircle(NumUtil.LTF(snapshot1.x), NumUtil.LTF(snapshot1.lastX), NumUtil.LTF(snapshot1.y), NumUtil.LTF(snapshot1.lastY), 1, 0xFF0000FF);
        } else {
            DrawUtil.Game.fillCircle(NumUtil.LTF(snapshot2.x), NumUtil.LTF(snapshot2.lastX), NumUtil.LTF(snapshot2.y), NumUtil.LTF(snapshot2.lastY), getEntityDimension().radius, 0x00FF0067);
//            DrawUtil.Game.fillCircle(NumUtil.LTF(snapshot2.x), NumUtil.LTF(snapshot2.lastX), NumUtil.LTF(snapshot2.y), NumUtil.LTF(snapshot2.lastY), 1, 0xFF0000FF);
        }
    }

    public void drawHeathBar(boolean isSnapshot1) {
        if (getTags().contains(Tags.INVULNERABLE)) {
            return;
        }

        long maxHp = getEntityStats().maxHp;
        float scale = getEntityDimension().diameter / (NumUtil.LTF(maxHp) / 100f); //the width of each tick
//        System.out.println(getCollisionDiameter() + ":" + getMaxHp() + ":" + scale + ":" + ((float)getCollisionDiameter())/((float)getMaxHp()));

        int color;
        if (hp > maxHp * 0.8) {
            color = 0x00FF00FF;
        } else if (hp > maxHp * 0.6) {
            color = 0x80FF00FF;
        } else if (hp > maxHp * 0.4) {
            color = 0xFFFF00FF;
        } else if (hp > maxHp * 0.2) {
            color = 0xFF8000FF;
        } else {
            color = 0xFF0000FF;
        }
        float tempX;
        float tempY;
        float tempLastX;
        float tempLastY;
        float tempHp;
        if (isSnapshot1) {
            tempX = NumUtil.LTF(snapshot1.x);
            tempY = NumUtil.LTF(snapshot1.y);
            tempLastX = NumUtil.LTF(snapshot1.lastX);
            tempLastY = NumUtil.LTF(snapshot1.lastY);
            tempHp = NumUtil.LTF(snapshot1.hp);
        } else {
            tempX = NumUtil.LTF(snapshot2.x);
            tempY = NumUtil.LTF(snapshot2.y);
            tempLastX = NumUtil.LTF(snapshot2.lastX);
            tempLastY = NumUtil.LTF(snapshot2.lastY);
            tempHp = NumUtil.LTF(snapshot2.hp);
        }
        float radius = getEntityDimension().radius;
        DrawUtil.Game.fillRect(tempX-radius, tempLastX-radius, tempY - radius - 7, tempLastY - radius - 7, (tempHp / 100f) * scale, 6, color);
        DrawUtil.Game.strokeRect(tempX-radius, tempLastX-radius, tempY - radius - 7, tempLastY - radius - 7, getEntityDimension().diameter, 6, 0x000000FF, 1);
        if (scale > getEntityDimension().diameter) {
            for (float i = scale / 4; i < getEntityDimension().diameter; i += scale / 4) {
                DrawUtil.Game.fillLineDotted(tempX + i-radius, tempLastX + i-radius, tempY - radius - 7, tempLastY - radius - 7, tempX + i-radius, tempLastX + i-radius, tempY - radius - 1, tempLastY - radius - 1, 0x000000FF, 1, 2, 2);
            }
        } else {
            for (float i = scale; i < getEntityDimension().diameter; i += scale) {
                DrawUtil.Game.fillLine(tempX + i-radius, tempLastX + i-radius, tempY - radius - 7, tempLastY - radius - 7, tempX + i-radius, tempLastX + i-radius, tempY - radius - 1, tempLastY - radius - 1, 0x000000FF, 1);
            }
        }


    }

    public float getRadius() {
        return getEntityDimension().radius;
    }
    public long getRadiusScaled() {
        return getEntityDimension().radiusScaled;
    }

    public long getX() {
        return entityPosition.x;
    }

    public long getY() {
        return entityPosition.y;
    }

    public long getZ() {
        return entityPosition.z;
    }

    public long getLastX() {
        return entityPosition.lastX;
    }

    public long getLastY() {
        return entityPosition.lastY;
    }

    public long getLastZ() {
        return entityPosition.lastY;
    }

    public void setX(long x) {
        entityPosition.x = x;
    }

    public void setY(long y) {
        entityPosition.y = y;
    }

    public void setZ(long z) {
        entityPosition.z = z;
    }

    public void changeX(long x) {
        entityPosition.x += x;
    }

    public void changeY(long y) {
        entityPosition.y += y;
    }

    public void changeZ(long z) {
        entityPosition.z += z;
    }

    public void setLastX(long lastX) {
        entityPosition.lastX = lastX;
    }

    public void setLastY(long lastY) {
        entityPosition.lastY = lastY;
    }

    public void setLastZ(long lastZ) {
        entityPosition.lastZ = lastZ;
    }
}


