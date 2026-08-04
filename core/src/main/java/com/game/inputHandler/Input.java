package com.game.inputHandler;


public class Input {


    private final InputType inputType;
    private final float startX;
    private final float startY;
    private final float x;
    private final float y;
    private final Actions action;
    private final Keys key;
    private final int scrollAmount;
    private boolean isShiftHeld;
    private boolean isControlHeld;

    public Input(InputType inputType, float x, float y, boolean isShiftHeld, boolean isControlHeld) {//mouse click
        this.inputType = inputType;
        this.x = x;
        this.y = y;
        startX = 0;
        startY = 0;
        action = Actions.NONE;
        key = Keys.NONE;
        scrollAmount = 0;
        this.isShiftHeld = isShiftHeld;
        this.isControlHeld = isControlHeld;
    }

    public Input(InputType inputType, float startX, float startY, float x, float y, boolean isShiftHeld, boolean isControlHeld) {//drag
        this.inputType = inputType;
        this.startX = startX;
        this.startY = startY;
        this.x = x;
        this.y = y;
        action = Actions.NONE;
        key = Keys.NONE;
        scrollAmount = 0;
        this.isShiftHeld = isShiftHeld;
        this.isControlHeld = isControlHeld;
    }

    public Input(InputType inputType, Actions action, Keys key, boolean isShiftHeld, boolean isControlHeld) {//type
        this.inputType = inputType;
        this.action = action;
        this.key = key;
        startX = 0;
        startY = 0;
        x = 0;
        y = 0;
        scrollAmount = 0;
        this.isShiftHeld = isShiftHeld;
        this.isControlHeld = isControlHeld;
    }

    public Input(InputType inputType, float x, float y, int scrollAmount, boolean isShiftHeld, boolean isControlHeld) {
        this.inputType = inputType;
        action = Actions.NONE;
        key = Keys.NONE;
        startX = 0;
        startY = 0;
        this.x = x;
        this.y = y;
        this.scrollAmount = scrollAmount;
        this.isShiftHeld = isShiftHeld;
        this.isControlHeld = isControlHeld;
    }

    public float getStartX() {
        return startX;
    }

    public float getStartY() {
        return startY;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public Actions getAction() {
        return action;
    }

    public Keys getKey() {
        return key;
    }

    public int getScroll() {
        return scrollAmount;
    }

    public InputType getInputType() {
        return inputType;
    }

    public boolean isShiftHeld() {
        return isShiftHeld;
    }

    public boolean isControlHeld() {
        return isControlHeld;
    }
}
