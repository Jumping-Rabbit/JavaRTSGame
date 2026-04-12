package game.entity;

import inputHandler.InputType;
import utils.DrawUtil;
import utils.Models;
import utils.NumUtil;

import java.util.ArrayList;
import java.util.Comparator;

public abstract class Entity {
    public static final Comparator<Entity> Y_COMPARATOR = (e1, e2) -> Long.compare(e1.getY(), e2.getY());
    protected static boolean hasCollision;
    protected static Models model;
    protected static ArrayList<InputType> validCommandTypes = new ArrayList<>();
    private static int idNum = 0;
    final public int id;
    public int nextInCell = -1;
    protected long x;//first 4 digit is decimal
    protected long y;
    protected long z;
    protected long lastX;
    protected long lastY;
    protected long lastZ;
    protected long direction;
    protected long lastDirection;
    protected DrawUtil drawUtil;
    protected ArrayList<Command> commands = new ArrayList<>();
    protected boolean isSelected = false;

    public void setIsSelected(boolean isSelected){
        this.isSelected = isSelected;
    }

    public boolean isSelected(){
        return isSelected;
    }

    public Entity() {
        id = idNum;
        idNum++;
    }

    protected Entity(int id) {
        this.id = id;
    }

    public abstract Entity copy();

    public long getX() {
        return x;
    }

    public long getY() {
        return y;
    }

    public long getZ() {
        return z;
    }

    public long getLastX() {
        return lastX;
    }

    public long getLastY() {
        return lastY;
    }

    public long getLastZ() {
        return lastY;
    }

    public void setX(long x){
        this.x = x;
    }

    public void setY(long y){
        this.y = y;
    }

    public void setZ(long z){
        this.z = z;
    }

    public Models getModel() {
        return model;
    }

    public abstract void draw();

    public abstract void updateOnFrame();

    public void clearCommands() {
        commands.clear();
    }

    public void removeCommand() {
        if (!commands.isEmpty()) {
            commands.removeFirst();
        }
    }

    public void addCommand(Command command) {
        if (validCommandTypes.contains(command.getInputType())) {
            commands.add(command);
        }
    }

    public void drawSelectedRing() {
        drawUtil.setColor(0, 255, 0, 0.2);
        double offset = model.getBoundingDiff()+model.getBoundingOffset();
        drawUtil.fillCircleInterpolateGame(NumUtil.LTD(x) + offset-2, NumUtil.LTD(y) - offset-2, NumUtil.LTD(lastX) + offset-2, NumUtil.LTD(lastY) - offset-2, model.getBoundingRadius()+2);
    }

    public long getRadius() {
        return model.getScaledHalfWidth();
    }

    public long getCollisionRadius() {
        return model.getBoundingRadiusScaled();
    }

    public long getDiameter() {
        return model.getScaledWidth();
    }

    public long getCollisionDiameter() {
        return model.getBoundingDiameterScaled();
    }

}
