package game.entity.object;

import game.entity.Entity;
import game.entity.PlayerColor;
import game.entity.Tags;

import java.util.EnumSet;

public abstract class Object extends Entity {
    public Object(PlayerColor color) {
        super(color);
    }
    protected static EnumSet<Tags> tags;
    @Override
    public EnumSet<Tags> getTags(){
        return tags;
    }
}
