package game.entity.building;

import game.entity.Entity;
import game.entity.Tags;
import game.entity.players;

import java.util.EnumSet;

public abstract class Building extends Entity {

    public Building() {
        super();
    }
    @Override
    public EnumSet<Tags> getTags(){
        return tags;
    }
    @Override
    public void updateOnFrame(){
        lastX = x;
        lastY = y;
        lastZ = z;
    }
    protected long timer;
    protected players player;
    protected Building(int id) {
        super(id);
    }
}
