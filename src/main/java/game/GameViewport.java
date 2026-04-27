package game;

import inputHandler.Input;
import inputHandler.InputHandler;
import inputHandler.InputType;

public class GameViewport {
    private record ViewportSnapshot(long x, long y, long lastX, long lastY) {}

    private volatile ViewportSnapshot snapshot;
    private volatile ViewportSnapshot freezeSnapshot;
    private long currentX, currentY, lastX, lastY;

    public GameViewport(long x, long y) {
        currentX = x; currentY = y;
        lastX = x; lastY = y;
        snapshot = new ViewportSnapshot(x, y, x, y);
    }
    public void freeze(){
        freezeSnapshot = snapshot;
    }

    public void updateOnFrame() {
        lastX = currentX;
        lastY = currentY;
        for (Input input : InputHandler.getInputs()) {
            if (input.getInputType() == InputType.MOVE){
                if (input.getX() > 1870){
                    currentX += 15;
                } else if (input.getX() < 50){
                    currentX -= 15;
                } else if (input.getY() > 1030){
                    currentY += 15;
                } else if (input.getY() < 50){
                    currentY -=15;
                }
                if (input.getX() > 1910){
                    currentX += 30;
                } else if (input.getX() < 10){
                    currentX -= 30;
                } else if (input.getY() > 1070){
                    currentY += 30;
                } else if (input.getY() < 10){
                    currentY -=30;
                }
            }
            switch (input.getAction()) {
                case UP: currentY -= 30; break;
                case DOWN: currentY += 30; break;
                case LEFT: currentX -= 30; break;
                case RIGHT: currentX += 30; break;
            }

        }
        // single volatile write = atomic publish
        snapshot = new ViewportSnapshot(currentX, currentY, lastX, lastY);
    }

    public long getX() { return freezeSnapshot.x(); }
    public long getLastX() { return freezeSnapshot.lastX(); }
    public long getY() { return freezeSnapshot.y(); }
    public long getLastY() { return freezeSnapshot.lastY(); }
}