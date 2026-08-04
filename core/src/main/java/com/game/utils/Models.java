package com.game.utils;

import com.badlogic.gdx.graphics.g3d.ModelInstance;

public enum Models {
    vanguardMarine(ModelType.UNIT),
    vanguardMUV(ModelType.UNIT),
    vanguardBarracks(ModelType.BUILDING),
    vanguardCommandCenter(ModelType.BUILDING);

    private ModelType modelType;

    Models(ModelType modelType) {
        this.modelType = modelType;
    }

    public enum ModelType {
        UNIT,
        BUILDING;
    }

    public static int getBuildingAmount() {
        int count = 0;
        for (Models model : values()) {
            if (model.modelType == ModelType.BUILDING) {
                count++;
            }
        }
        return count;
    }

    public static int getUnitAmount() {
        int count = 0;
        for (Models model : values()) {
            if (model.modelType == ModelType.UNIT) {
                count++;
            }
        }
        return count;
    }

    public static Models fromValue(String givenName) {
        for (Models model : values()) {
            if (model.toString().equalsIgnoreCase(givenName)) {
                return model;
            }
        }
        return null;
    }

    public void set(float width, float height, float radius, ModelType modelType, ModelInstance modelInstance) {

        this.modelType = modelType;
    }

    public ModelType getModelType() {
        return modelType;
    }


    public float getBoundingRadius() {
        return boundingRadius;
    }

    public float getBoundingDiameter() {
        return boundingDiameter;
    }

    public long getBoundingRadiusScaled() {
        return boundingRadiusScaled;
    }

    public long getBoundingDiameterScaled() {
        return boundingDiameterScaled;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public float getHalfWidth() {
        return halfWidth;
    }

    public float getHalfHeight() {
        return halfHeight;
    }

    public long getScaledWidth() {
        return scaledWidth;
    }

    public long getScaledHeight() {
        return scaledHeight;
    }

    public long getScaledHalfWidth() {
        return scaledHalfWidth;
    }

    public long getScaledHalfHeight() {
        return scaledHalfHeight;
    }
}
