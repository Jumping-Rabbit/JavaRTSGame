package game;

public class GameViewport {
    private final long viewportWidth;
    private final long viewportHeight;
    private long viewportX;
    private long viewportY;
    private long scale;

    public GameViewport(long x, long y) {
        viewportX = x;
        viewportY = y;
        viewportWidth = 1920;
        viewportHeight = 1080;
        scale = 1;
    }

    public long getX() {
        return viewportX;
    }

    public void setX(long viewportX) {
        this.viewportX = viewportX;
    }

    public long getY() {
        return viewportY;
    }

    public void setY(long viewportY) {
        this.viewportY = viewportY;
    }

    public void changeX(long change) {
        viewportX += change;
    }

    public void changeY(long change) {
        viewportY += change;
    }

    public long getWidth() {
        return viewportWidth;
    }

    public long getHeight() {
        return viewportHeight;
    }

    public long getScale() {
        return scale;
    }

    public void setScale(long scale) {
        this.scale = scale;
    }


}
