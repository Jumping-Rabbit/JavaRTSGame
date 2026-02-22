package game.screen;

public abstract class Screen {
    public abstract void updateOnFrame();
    public abstract void draw();
    public abstract Screen copy();
}
