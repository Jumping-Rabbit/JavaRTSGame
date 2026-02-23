package game.entity;

import javafx.scene.image.Image;
import utils.DrawUtil;
import inputHandler.InputType;
import utils.NumUtil;

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
    protected long radius;
    protected long collisionRadius;
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
        drawUtil.fillCircleInterpolate(NumUtil.LTD(lastX)-5, NumUtil.LTD(lastY)-5, NumUtil.LTD(collisionRadius), NumUtil.LTD(x)-5, NumUtil.LTD(y)-5);
    }
    public long getRadius(){
        return radius;
    }
    public long getCollisionRadius(){
        return collisionRadius;
    }

}
