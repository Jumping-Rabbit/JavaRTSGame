package game.entity;

import javafx.scene.image.Image;
import utils.DrawUtil;
import inputHandler.InputType;
import utils.numUtil;

import java.util.ArrayList;

public abstract class Entity {
    protected long x;//first 8 digit is decimal
    protected long y;
    protected long lastX;
    protected long lastY;
    protected long direction;
    protected long lastDirection;
    protected boolean hasCollision;
    protected DrawUtil drawUtil;
    protected double radius;
    protected double collisionRadius;
    protected Image image;

    public abstract void draw();
    public abstract void updateOnFrame();
    protected ArrayList<Command> commands = new ArrayList<>();
    protected ArrayList<InputType> validCommandTypes = new ArrayList<>();
    public void clearCommands(){
        commands.clear();
    }
    public void removeCommand(){
        if (!commands.isEmpty()){
            commands.removeFirst();
        }
    }
    public void addCommand(Command command){
        if (validCommandTypes.contains(command.getInputType())){
            commands.add(command);
        }
    }
    public void drawSelectedRing(){
        drawUtil.setColor(0, 255, 0, 0.2);
        drawUtil.fillCircleInterpolate(numUtil.LTD(lastX)-5, numUtil.LTD(lastY)-5, radius+5, numUtil.LTD(x)-5, numUtil.LTD(y)-5);
    }
    public double getRadius(){
        return radius;
    }
    public double getCollisionRadius(){
        return collisionRadius;
    }

}
