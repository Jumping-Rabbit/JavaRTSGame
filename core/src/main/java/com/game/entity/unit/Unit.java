package com.game.entity.unit;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.game.entity.*;
import com.game.inputHandler.InputType;
import com.game.utils.DrawUtil;
import com.game.utils.NumUtil;

import java.util.ArrayList;

public abstract class Unit extends Entity {

    protected static ArrayList<Abilities> abilities;


    abstract protected UnitStats getUnitStats();

    protected long ticksUntilAttack;

    protected UnitState unitState;
    protected long targetX;
    protected long targetY;
    protected long targetDirection;

    public void setFormationIndex(int formationIndex) {
        this.formationIndex = formationIndex;
    }

    public void changeFormationIndex(int amount) {
        formationIndex += amount;
    }

    protected Entity attackTarget;

    public int getFormationIndex() {
        return formationIndex;
    }

    protected int formationIndex = 0;


    public Unit(PlayerColor color, ModelInstance modelInstance, EntityPosition entityPosition) {
        super(color, modelInstance, entityPosition);
    }


    public long getTargetX() {
        return targetX;
    }

    public long getTargetY() {
        return targetY;
    }


    public UnitState getUnitState() {
        return unitState;
    }

    public void setUnitState(UnitState unitState) {
        this.unitState = unitState;
    }


    public void drawTarget() {
        if (unitState != UnitState.MOVING) {
            return;
        }
        DrawUtil.Game.fillCircleScaledCull(targetX - 50000, targetX - 50000, targetY - 50000, targetY - 50000, 100000, 0x00FF00FF);
    }

    @Override
    public void updateOnFrame() {
        entityPosition.tick();
        if (!commands.isEmpty()) {
            for (Command command : commands) {
                if (command.getInputType() == InputType.RIGHT_CLICK) {
                    unitState = UnitState.MOVING;

                    int ring = (int) StrictMath.sqrt(formationIndex);
                    long angle = (long) ((formationIndex * 137.507) * NumUtil.SCALER) % NumUtil.FTL(360);
                    long spreadRadius = getEntityDimension().radiusScaled * ring;
                    long offsetX = (long) (NumUtil.cos(angle) * spreadRadius);
                    long offsetY = (long) (NumUtil.sin(angle) * spreadRadius);

                    targetX = command.getX() - getEntityDimension().halfWidthScaled + offsetX;
                    targetY = command.getY() - getEntityDimension().halfWidthScaled + offsetY;
                }
            }
        } else {
            unitState = UnitState.IDLE;
        }

        switch (unitState) {
            case MOVING:
                long x = entityPosition.x;
                long y = entityPosition.y;
                long direction = entityPosition.direction;
                targetDirection = NumUtil.atan2(targetY - y, targetX - x);
                long delta = targetDirection - direction;
                long scaled180 = NumUtil.FTL(180);
                long scaled360 = NumUtil.FTL(360);
                while (delta <= -scaled180) delta += scaled360;
                while (delta > scaled180) delta -= scaled360;
//                System.out.println(delta);
                if (StrictMath.abs(delta) <= getUnitStats().turnSpeed) {
                    direction = targetDirection;
                } else {
                    if (delta > 0) entityPosition.direction += getUnitStats().turnSpeed;
                    else entityPosition.direction -= getUnitStats().turnSpeed;
                    if (direction <= -scaled180) entityPosition.direction += scaled360;
                    if (direction > scaled180) entityPosition.direction -= scaled360;
                }
                if (direction == targetDirection) {
                    long xChange = (long) (getUnitStats().speed * NumUtil.cos(direction));
                    long yChange = (long) (getUnitStats().speed * NumUtil.sin(direction));
                    if (targetX > x ? x + xChange >= targetX : x + xChange <= targetX) entityPosition.x = targetX;
                    else entityPosition.x += xChange;
                    if (targetY > y ? y + yChange >= targetY : y + yChange <= targetY) entityPosition.y = targetY;
                    else entityPosition.y += yChange;
                    long dx = x - targetX;
                    long dy = y - targetY;
                    long scaledHalfWidth = getEntityDimension().halfWidthScaled;
                    if ((dx * dx) + (dy * dy) <= (scaledHalfWidth * scaledHalfWidth)) {
                        unitState = UnitState.IDLE;
                        removeCommand();
                    }
                }
                break;
        }
    }
}
