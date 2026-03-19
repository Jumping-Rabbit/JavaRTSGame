package game.screen;

public abstract class Screen {
    public abstract void updateOnFrame();
    public abstract void draw();
    public abstract Screen copy();
    protected boolean exit = false;
    public boolean isExit(){
        if (exit){
            exit = false;
            return true;
        }
        return false;
    }
}
