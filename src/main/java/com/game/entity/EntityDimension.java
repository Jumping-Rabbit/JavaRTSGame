package com.game.entity;

import com.game.utils.NumUtil;

public class EntityDimension {
    public final long widthScaled;
    public final long heightScaled;
    public final long halfWidthScaled;
    public final long halfHeightScaled;
    public final long radiusScaled;
    public final long diameterScaled;
    public final float width;
    public final float height;
    public final float halfWidth;
    public final float halfHeight;
    public final float radius;
    public final float diameter;
    public EntityDimension(float width, float height, float radius){
        widthScaled = NumUtil.FTL(width);
        heightScaled = NumUtil.FTL(height);
        halfWidthScaled = NumUtil.FTL(width/2);
        halfHeightScaled = NumUtil.FTL(height/2);
        radiusScaled = NumUtil.FTL(radius);
        diameterScaled = NumUtil.FTL(radius*2);
        this.width = width;
        this.height = height;
        halfWidth = width/2;
        halfHeight = height;
        this.radius = radius;
        diameter = radius*2;

    }
}
