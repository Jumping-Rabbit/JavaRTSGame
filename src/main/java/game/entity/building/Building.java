package game.entity.building;

import game.entity.Entity;
import game.entity.Tags;

import java.util.EnumSet;

public abstract class Building extends Entity {
    protected static EnumSet<Tags> tags;

    public Building() {
        super();
    }
    @Override
    public EnumSet<Tags> getTags(){
        return tags;
    }
}
