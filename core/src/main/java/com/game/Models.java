package com.game;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;

public enum Models {
    vanguardMarine(ModelType.UNIT),
    vanguardMUV(ModelType.UNIT),
    vanguardBarracks(ModelType.BUILDING),
    vanguardCommandCenter(ModelType.BUILDING);

    private final ModelType modelType;
    private Model model;

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

    public ModelType getModelType() {
        return modelType;
    }

    public static void setModel(Models models, Model model){
        models.model = model;
    }

    public static ModelInstance getModelInstance(Models model) {
        return new ModelInstance(model.model);//TODO:need better names
    }
}
