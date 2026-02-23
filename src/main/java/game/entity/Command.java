package game.entity;

import inputHandler.InputType;

public class Command {
    long x;
    long y;
    String button;
    InputType inputType;
    public Command (String button){
        this.button = button;
        inputType = InputType.KEYPRESS;
        x = 0;
        y = 0;
    }
    public Command (InputType inputType, long x, long y){
        this.button = "";
        this.x = x;
        this.y = y;
        this.inputType = inputType;
    }
    public String getButton(){
        return button;
    }
    public long getX(){
        return x;
    }
    public long getY(){
        return y;
    }
    public InputType getInputType(){
        return inputType;
    }
}
