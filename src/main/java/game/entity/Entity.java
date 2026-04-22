package game.entity;

import inputHandler.InputType;
import utils.DrawUtil;
import utils.Models;
import utils.NumUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;

import static utils.NumUtil.LTD;

public abstract class Entity {
    public static final Comparator<Entity> Y_COMPARATOR = (e1, e2) -> Long.compare(e1.getY(), e2.getY());
    protected static boolean hasCollision;
    protected static Models model;
    protected static ArrayList<InputType> validCommandTypes = new ArrayList<>();
    private static int idNum = 0;
    final public int id;
    public int nextInCell = -1;
    public abstract EnumSet<Tags> getTags();
    public abstract long getMaxHp();
    protected long x;//first 4 digit is decimal
    protected long y;
    protected long z;
    protected long lastX;
    protected long lastY;
    protected long lastZ;
    protected long direction;
    protected long lastDirection;
    protected ArrayList<Command> commands = new ArrayList<>();
    protected boolean isSelected = false;
    protected long hp;


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
        double offset = model.getBoundingDiff()+model.getBoundingOffset();
        DrawUtil.Game.fillCircle(LTD(x) + offset-2, LTD(lastX) + offset-2, LTD(y) - offset-2, LTD(lastY) - offset-2, model.getBoundingRadius()+2, 0x00FF0067);
    }

    public void drawHeathBar(){
        if (getTags().contains(Tags.INVULNERABLE) || hp == 0){
            return;
        }
        double offset = model.getBoundingDiff()+model.getBoundingOffset();
        double scale = getCollisionDiameter()/(getMaxHp()/20d);
        long maxHp = getMaxHp();
//        System.out.println(getCollisionDiameter() + ":" + getMaxHp() + ":" + scale + ":" + ((double)getCollisionDiameter())/((double)getMaxHp()));

        int color;
        if (hp > maxHp*0.8){
            color = 0x00FF00FF;
        } else if (hp > maxHp*0.6){
            color = 0x80FF00FF;
        }else if (hp > maxHp*0.4){
            color = 0xFFFF00FF;
        }else if (hp > maxHp*0.2){
            color = 0xFF8000FF;
        } else {
            color = 0xFF0000FF;
        }
        DrawUtil.Game.fillRect(LTD(getX()) + offset, LTD(getLastX()) + offset, LTD(getY()-getRadius())-7, LTD(getLastY()-getRadius())-7, (LTD(hp)/20d)*scale, 5, color);
//        System.out.println((hp/20d)*scale +":"+scale);
        DrawUtil.Game.strokeRect(LTD(getX()) + offset, LTD(getLastX()) + offset, LTD(getY()-getRadius())-7, LTD(getLastY()-getRadius())-7, LTD(getCollisionDiameter()), 5, 0x000000FF, 1);
        for(double i = scale; i < LTD(getCollisionDiameter()); i+=scale){
            DrawUtil.Game.fillLine(LTD(getX()) + i + offset, LTD(getLastX()) + i + offset, LTD(getY()-getRadius())-7, LTD(getLastY()-getRadius())-7, LTD(getX()) + i + offset, LTD(getLastX()) + i + offset, LTD(getY()-getRadius())-2, LTD(getLastY()-getRadius())-2, 0x000000FF);
        }
    }

    public abstract void drawTarget();

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
