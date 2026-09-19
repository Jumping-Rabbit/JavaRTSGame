package com.game.gameWindow;

public abstract class GameWindow {
    protected boolean exit = false;

    public abstract void updateOnFrame();

    public abstract void draw();

    public abstract GameWindow copy();

    public boolean isExit() {
        if (exit) {
            exit = false;
            return true;
        }
        return false;
    }
}
