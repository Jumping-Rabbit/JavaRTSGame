package com.game.entity.building;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.game.entity.Entity;
import com.game.entity.PlayerColor;
import com.game.entity.Tags;

import java.util.EnumSet;

public abstract class Building extends Entity {

    public Building(PlayerColor color, ModelInstance modelInstance) {
        super(color, modelInstance);
    }

    @Override
    public EnumSet<Tags> getTags() {
        return tags;
    }

    @Override
    public void updateOnFrame() {
        lastX = x;
        lastY = y;
        lastZ = z;
    }

    protected long timer;

    protected Building(int id) {
        super(id);
    }
}
