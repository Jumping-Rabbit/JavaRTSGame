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
    public static final Comparator<Entity> Y_COMPARATOR = Comparator.comparingLong(Entity::getY);
    protected static boolean hasCollision;
    protected static Models model;
    protected static ArrayList<InputType> validCommandTypes = new ArrayList<>();
    private static int idNum = 0;
    final public int id;
    public int nextInCell1 = -1;
    public int nextInCell2 = -1;
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
    protected boolean isSelected = false;
    protected long hp;
    protected Snapshot snapshot1;
    protected Snapshot snapshot2;
    protected ArrayList<Command> commands = new ArrayList<>();


    public Snapshot getSnapshot(boolean isSnapshot1) {
        if (isSnapshot1){
            return snapshot1;
        } else {
            return snapshot2;
        }
    }
    public void setSnapshot1(){
        snapshot1.set(this);
    }
    public void setSnapshot2(){
        snapshot2.set(this);
    }

    public void setIsSelected(boolean isSelected){
        this.isSelected = isSelected;
    }

    public boolean isSelected(){
        return isSelected;
    }

    public Entity() {
        id = idNum;
        idNum++;
        snapshot1 = new Snapshot();
        snapshot2 = new Snapshot();
    }

    protected Entity(int id) {
        this.id = id;
    }

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

    public void changeX(long x){
        this.x += x;
    }

    public void changeY(long y){
        this.y += y;
    }

    public void changeZ(long z){
        this.z += z;
    }

    public Models getModel() {
        return model;
    }

    public void draw(boolean isSnapshot1) {
        if (isSnapshot1){
            DrawUtil.Game.fillModelScaled(model, snapshot1.x, snapshot1.lastX, snapshot1.y, snapshot1.lastY, snapshot1.z, snapshot1.lastZ, snapshot1.direction, snapshot1.lastDirection);
        } else {
            DrawUtil.Game.fillModelScaled(model, snapshot2.x, snapshot2.lastX, snapshot2.y, snapshot2.lastY, snapshot2.z, snapshot2.lastZ, snapshot2.direction, snapshot2.lastDirection);
        }

    }

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

    public void drawSelectedRing(boolean isSnapshot1) {
        double offset = model.getBoundingDiff()+model.getBoundingOffset();
        if (isSnapshot1){
            DrawUtil.Game.fillCircle(LTD(snapshot1.x) + offset-2, LTD(snapshot1.lastX) + offset-2, LTD(snapshot1.y) - offset-2, LTD(snapshot1.lastY) - offset-2, model.getBoundingRadius()+2, 0x00FF0067);
        }else {
            DrawUtil.Game.fillCircle(LTD(snapshot2.x) + offset-2, LTD(snapshot2.lastX) + offset-2, LTD(snapshot2.y) - offset-2, LTD(snapshot2.lastY) - offset-2, model.getBoundingRadius()+2, 0x00FF0067);
        }
    }

    public void drawHeathBar(boolean isSnapshot1){
        if (getTags().contains(Tags.INVULNERABLE)){
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
        if (isSnapshot1){
            DrawUtil.Game.fillRect(LTD(snapshot1.x) + offset, LTD(snapshot1.lastX) + offset, LTD(snapshot1.y-getRadius())-7, LTD(snapshot1.lastY-getRadius())-7, (LTD(snapshot1.hp)/20d)*scale, 5, color);
            DrawUtil.Game.strokeRect(LTD(snapshot1.x) + offset, LTD(snapshot1.lastX) + offset, LTD(snapshot1.y-getRadius())-7, LTD(snapshot1.lastY-getRadius())-7, LTD(getCollisionDiameter()), 5, 0x000000FF, 1);
            for(double i = scale; i < LTD(getCollisionDiameter()); i+=scale){
                DrawUtil.Game.fillLine(LTD(snapshot1.x) + i + offset, LTD(snapshot1.lastX) + i + offset, LTD(snapshot1.y-getRadius())-7, LTD(snapshot1.lastY-getRadius())-7, LTD(snapshot1.x) + i + offset, LTD(snapshot1.lastX) + i + offset, LTD(snapshot1.y-getRadius())-2, LTD(snapshot1.lastY-getRadius())-2, 0x000000FF);
            }
        } else {
            DrawUtil.Game.fillRect(LTD(snapshot2.x) + offset, LTD(snapshot2.lastX) + offset, LTD(snapshot2.y-getRadius())-7, LTD(snapshot2.lastY-getRadius())-7, (LTD(snapshot2.hp)/20d)*scale, 5, color);
            DrawUtil.Game.strokeRect(LTD(snapshot2.x) + offset, LTD(snapshot2.lastX) + offset, LTD(snapshot2.y-getRadius())-7, LTD(snapshot2.lastY-getRadius())-7, LTD(getCollisionDiameter()), 5, 0x000000FF, 1);
            for(double i = scale; i < LTD(getCollisionDiameter()); i+=scale){
                DrawUtil.Game.fillLine(LTD(snapshot2.x) + i + offset, LTD(snapshot2.lastX) + i + offset, LTD(snapshot2.y-getRadius())-7, LTD(snapshot2.lastY-getRadius())-7, LTD(snapshot2.x) + i + offset, LTD(snapshot2.lastX) + i + offset, LTD(snapshot2.y-getRadius())-2, LTD(snapshot2.lastY-getRadius())-2, 0x000000FF);
            }
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
