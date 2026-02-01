package entity;

import main.DrawUtil;
import main.inputHandler.InputType;

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

    public abstract void draw();
    public abstract void updateOnFrame();
    protected ArrayList<Command> commands = new ArrayList<>();
    protected ArrayList<InputType> validCommandTypes = new ArrayList<>();
    protected void clearCommands(){
        commands.clear();
    }
    public void addCommand(Command command){
        if (validCommandTypes.contains(command.getInputType())){
            commands.add(command);
        }
    }
    public void drawSelectedRing(){
        drawUtil.setColor(0, 255, 0, 25);
        drawUtil.fillCircleInterpolate(numUtil.LTD(lastX)-5, numUtil.LTD(lastY)-5, radius+5, numUtil.LTD(x)-5, numUtil.LTD(y)-5, numUtil.LTD(lastDirection), numUtil.LTD(direction));
    }

}
