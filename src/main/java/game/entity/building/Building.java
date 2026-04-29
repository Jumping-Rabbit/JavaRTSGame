package game.entity.building;

import game.entity.Entity;
import game.entity.Tags;
import game.entity.PlayerColor;

import java.util.EnumSet;

public abstract class Building extends Entity {

    public Building(PlayerColor color) {
        super(color);
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
    protected Building(int id) {
        super(id);
    }
}
