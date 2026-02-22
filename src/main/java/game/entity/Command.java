package game.entity;

import inputHandler.InputType;

public class Command {
    double x;
    double y;
    String button;
    InputType inputType;
    public Command (String button){
        this.button = button;
        inputType = InputType.KEYPRESS;
        x = 0;
        y = 0;
    }
    public Command (InputType inputType, double x, double y){
        this.button = "";
        this.x = x;
        this.y = y;
        this.inputType = inputType;
    }
    public String getButton(){
        return button;
    }
    public double getX(){
        return x;
    }
    public double getY(){
        return y;
    }
    public InputType getInputType(){
        return inputType;
    }
}
