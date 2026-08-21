package com.game.entity.object;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.game.entity.Entity;
import com.game.entity.EntityPosition;
import com.game.entity.PlayerColor;
import com.game.entity.Tags;

import java.util.EnumSet;

public abstract class Object extends Entity {
    public Object(PlayerColor color, ModelInstance modelInstance) {
        super(color, modelInstance, new EntityPosition(0, 0, 0, 0));
    }

    protected static EnumSet<Tags> tags;

    @Override
    public EnumSet<Tags> getTags() {
        return tags;
    }
}
