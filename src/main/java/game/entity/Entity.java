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
        drawUtil.fillCircleInterpolateGame(NumUtil.LTD(x) - 2, NumUtil.LTD(y) - 2, NumUtil.LTD(lastX) - 2, NumUtil.LTD(lastY) - 2, model.getHalfWidth() + 2);
    }

    public long getRadius() {
        return model.getScaledHalfWidth();
    }

    public long getCollisionRadius() {
        return (long) (model.getScaledHalfWidth() * 1.5);
    }

    public long getDiameter() {
        return model.getScaledWidth();
    }

    public long getCollisionDiameter() {
        return (long) (model.getScaledWidth() * 1.5);
    }

}
