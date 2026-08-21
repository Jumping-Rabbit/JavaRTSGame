package com.game.entity.building;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.game.entity.Entity;
import com.game.entity.EntityPosition;
import com.game.entity.PlayerColor;

public abstract class Building extends Entity {

    public Building(PlayerColor color, ModelInstance modelInstance, EntityPosition entityPosition) {
        super(color, modelInstance, entityPosition);
    }

    @Override
    public void updateOnFrame() {
        entityPosition.tick();
    }

    protected long timer;
}
