package game.screen;

public abstract class Screen {
    protected boolean exit = false;

    public abstract void updateOnFrame();

    public abstract void draw();

    public abstract Screen copy();

    public boolean isExit() {
        if (exit) {
            exit = false;
            return true;
        }
        return false;
    }
}
