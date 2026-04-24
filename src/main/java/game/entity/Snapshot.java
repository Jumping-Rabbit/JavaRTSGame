package game.entity;

public class Snapshot {
    public long x;
    public long y;
    public long z;
    public long lastX;
    public long lastY;
    public long lastZ;
    public long direction;
    public long lastDirection;
    public boolean isSelected = false;
    public long hp;
    
    public void set(Entity entity) {
        x = entity.x;
        y = entity.y;
        z = entity.z;
        lastX = entity.lastX;
        lastY = entity.lastY;
        lastZ = entity.lastZ;
        direction = entity.direction;
        lastDirection = entity.lastDirection;
        isSelected = entity.isSelected;
        hp = entity.hp;
    }
}
