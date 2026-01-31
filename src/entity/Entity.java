package entity;

import main.DrawUtil;
import main.inputHandler.InputType;

import java.util.ArrayList;

public abstract class Entity {
    protected long x;//first 8 digit is decimal
    protected long y;
    protected boolean hasCollision;
    protected DrawUtil drawUtil;

    public abstract void draw(double factor);
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
}
