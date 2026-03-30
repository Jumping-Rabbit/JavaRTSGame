package game.entity;

import javafx.scene.image.Image;
import utils.DrawUtil;
import inputHandler.InputType;
import utils.Models;
import utils.NumUtil;

import java.util.ArrayList;

public abstract class Entity {
    private static int idNum = 0;
    final public int id;
    public int nextInCell = -1;
    protected long x;//first 8 digit is decimal
    protected long y;
    protected long z;
    protected long lastX;
    protected long lastY;
    protected long lastZ;
    protected long direction;
    protected long lastDirection;
    protected static boolean hasCollision;
    protected DrawUtil drawUtil;
    protected static Models model;

    public Models getModel(){
        return model;
    }

    public Entity(){
        id = idNum;
        idNum++;
    }
    protected Entity(int id){
        this.id = id;
    }

    public abstract void draw();
    public abstract void updateOnFrame();
    protected ArrayList<Command> commands = new ArrayList<>();
    protected static ArrayList<InputType> validCommandTypes = new ArrayList<>();
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
        drawUtil.fillCircleInterpolate(NumUtil.LTD(x)-10, NumUtil.LTD(y)-10, NumUtil.LTD(lastX)-10, NumUtil.LTD(lastY)-10, model.getHalfWidth()+10);
    }
    public long getRadius(){
        return model.getScaledHalfWidth();
    }
    public long getCollisionRadius(){
        return model.getScaledHalfWidth()+model.getScaledHalfWidth()/2;
    }
    public long getDiameter(){
        return model.getScaledWidth();
    }
    public long getCollisionDiameter(){
        return model.getScaledWidth()+model.getScaledWidth()/2;
    }

}
