package game.entity;

import inputHandler.InputType;
import utils.DrawUtil;
import utils.Models;
import utils.NumUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;

import static utils.NumUtil.LTD;
import static utils.NumUtil.unScale;

public abstract class Entity {
    public static final Comparator<Entity> Y_COMPARATOR = Comparator.comparingLong(Entity::getY);
    protected static boolean hasCollision;
    protected static ArrayList<InputType> validCommandTypes = new ArrayList<>();
    private static int idNum = 0;
    final public int id;
    public int nextInCell1 = -1;
    public int nextInCell2 = -1;
    public abstract EnumSet<Tags> getTags();
    protected abstract Models getModel();
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
    protected long armor;
    protected abstract long getMaxHp();
    protected EnumSet<Tags> tags;
    protected ArrayList<Effects> effects;
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

    public void draw(boolean isSnapshot1) {
        if (isSnapshot1){
            DrawUtil.Game.fillModelScaled(getModel(), snapshot1.x, snapshot1.lastX, snapshot1.y, snapshot1.lastY, snapshot1.z, snapshot1.lastZ, snapshot1.direction, snapshot1.lastDirection);
        } else {
            DrawUtil.Game.fillModelScaled(getModel(), snapshot2.x, snapshot2.lastX, snapshot2.y, snapshot2.lastY, snapshot2.z, snapshot2.lastZ, snapshot2.direction, snapshot2.lastDirection);
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
        if (isSnapshot1){
            DrawUtil.Game.fillCircle(LTD(snapshot1.x)-2, LTD(snapshot1.lastX) -2, LTD(snapshot1.y)-2, LTD(snapshot1.lastY)-2, getModel().getBoundingRadius()+2, 0x00FF0067);
        }else {
            DrawUtil.Game.fillCircle(LTD(snapshot2.x)-2, LTD(snapshot2.lastX) -2, LTD(snapshot2.y)-2, LTD(snapshot2.lastY) -2, getModel().getBoundingRadius()+2, 0x00FF0067);
        }
    }

    public void drawHeathBar(boolean isSnapshot1){
        if (getTags().contains(Tags.INVULNERABLE)){
            return;
        }

        long maxHp = getMaxHp();
        double scale = getCollisionDiameter()/(maxHp/20d);
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
        double tempX;
        double tempY;
        double tempLastX;
        double tempLastY;
        double tempHp;
        if (isSnapshot1){
            tempX = LTD(snapshot1.x);
            tempY = LTD(snapshot1.y);
            tempLastX = LTD(snapshot1.lastX);
            tempLastY = LTD(snapshot1.lastY);
            tempHp = LTD(snapshot1.hp);
        } else {
            tempX = LTD(snapshot2.x);
            tempY = LTD(snapshot2.y);
            tempLastX = LTD(snapshot2.lastX);
            tempLastY = LTD(snapshot2.lastY);
            tempHp = LTD(snapshot2.hp);
        }
        DrawUtil.Game.fillRect(tempX, tempLastX, tempY - LTD(getRadius())-7, tempLastY - LTD(getRadius())-7, (tempHp/20d)*scale, 5, color);
        DrawUtil.Game.strokeRect(tempX, tempLastX, tempY - LTD(getRadius())-7, tempLastY-LTD(getRadius())-7, LTD(getCollisionDiameter()), 5, 0x000000FF, 1);
        for(double i = scale; i < LTD(getCollisionDiameter()); i+=scale){
            DrawUtil.Game.fillLine(tempX + i, tempLastX + i, tempY - LTD(getRadius())-7, tempLastY - LTD(getRadius())-7, tempX + i, tempLastX + i, tempY - LTD(getRadius())-2, tempLastY - LTD(getRadius())-2, 0x000000FF);
        }

    }

    public long getRadius() {
        return getModel().getScaledHalfWidth();
    }

    public long getCollisionRadius() {
        return getModel().getBoundingRadiusScaled();
    }

    public long getDiameter() {
        return getModel().getScaledWidth();
    }

    public long getCollisionDiameter() {
        return getModel().getBoundingDiameterScaled();
    }

}
