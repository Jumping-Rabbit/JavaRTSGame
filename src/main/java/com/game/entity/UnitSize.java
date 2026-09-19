package com.game.entity;

import com.game.utils.NumUtil;

public class UnitSize {
    private final float width;
    private final float height;
    private final float halfWidth;
    private final float halfHeight;
    private final long scaledWidth;
    private final long scaledHeight;
    private final long scaledHalfWidth;
    private final long scaledHalfHeight;
    private final float boundingRadius;
    private final float boundingDiameter;
    private final long boundingRadiusScaled;
    private final long boundingDiameterScaled;

    public UnitSize(float width, float height, float radius){
        this.width = width;
        halfWidth = width / 2;
        scaledWidth = NumUtil.FTL(width);
        scaledHalfWidth = NumUtil.FTL(width / 2);
        this.height = height;
        halfHeight = height / 2;
        scaledHeight = NumUtil.FTL(height);
        scaledHalfHeight = NumUtil.FTL(height / 2);
        this.boundingRadius = radius;
        boundingDiameter = radius * 2;
        boundingRadiusScaled = NumUtil.FTL(radius);
        boundingDiameterScaled = NumUtil.FTL(radius * 2);
    }

    public long getBoundingDiameterScaled() {
        return boundingDiameterScaled;
    }

    public long getBoundingRadiusScaled() {
        return boundingRadiusScaled;
    }

    public float getBoundingDiameter() {
        return boundingDiameter;
    }

    public float getBoundingRadius() {
        return boundingRadius;
    }

    public long getScaledHalfHeight() {
        return scaledHalfHeight;
    }

    public long getScaledHalfWidth() {
        return scaledHalfWidth;
    }

    public long getScaledHeight() {
        return scaledHeight;
    }

    public long getScaledWidth() {
        return scaledWidth;
    }

    public float getHalfHeight() {
        return halfHeight;
    }

    public float getHalfWidth() {
        return halfWidth;
    }

    public float getHeight() {
        return height;
    }

    public float getWidth() {
        return width;
    }

}
