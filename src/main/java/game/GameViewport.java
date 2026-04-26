package game;

import inputHandler.Input;
import inputHandler.InputHandler;

import java.util.List;

//public class GameViewport {
//    private final long viewportWidth;
//    private final long viewportHeight;
//    private volatile boolean isSnapshot1;
//    private long viewportX1;
//    private long viewportY1;
//    private long viewportLastX1;
//    private long viewportLastY1;
//    private long viewportX2;
//    private long viewportY2;
//    private long viewportLastX2;
//    private long viewportLastY2;
//    private long scale;
//    private long[] frozenPos;
//
//    public GameViewport(long x, long y) {
//        viewportX1 = x;
//        viewportY1 = y;
//        viewportLastX1 = x;
//        viewportLastY1 = y;
//        viewportX2 = x;
//        viewportY2 = y;
//        viewportLastX2 = x;
//        viewportLastY2 = y;
//        isSnapshot1 = true;
//        viewportWidth = 1920;
//        viewportHeight = 1080;
//        scale = 1;
//    }
//
//    public void updateOnFrame(){
//        if (isSnapshot1){
//            viewportLastX2 = viewportX2;
//            viewportLastY2 = viewportY2;
//        } else {
//            viewportLastX1 = viewportX1;
//            viewportLastY1 = viewportY1;
//        }
//
//        for (Input input : InputHandler.getInputs()) {
//            if(isSnapshot1){
//                switch (input.getAction()){
//                    case UP:
//                        viewportY2-= 30;
//                        break;
//                    case DOWN:
//                        viewportY2+= 30;
//                        break;
//                    case LEFT:
//                        viewportX2-=30;
//                        break;
//                    case RIGHT:
//                        viewportX2+=30;
//                        break;
//                }
//            } else {
//                switch (input.getAction()){
//                    case UP:
//                        viewportY1-= 30;
//                        break;
//                    case DOWN:
//                        viewportY1+= 30;
//                        break;
//                    case LEFT:
//                        viewportX1-=30;
//                        break;
//                    case RIGHT:
//                        viewportX1+=30;
//                        break;
//                }
//            }
//
//        }
//        if (isSnapshot1){
//            isSnapshot1 = false;
//        } else {
//            isSnapshot1 = true;
//        }
//    }
//
//    public void freeze(){
//        if (isSnapshot1){
//            frozenPos = new long[]{
//                    viewportX1,
//                    viewportY1,
//                    viewportLastX1,
//                    viewportLastY1
//            };
//        } else {
//            frozenPos = new long[]{
//                    viewportX2,
//                    viewportY2,
//                    viewportLastX2,
//                    viewportLastY2
//            };
//        }
//
//    }
//    public long getX() {
//        if (frozenPos == null) return 0;
//        return frozenPos[0];
//    }
//    public long getLastX() {
//        if (frozenPos == null) return 0;
//        return frozenPos[2];
//    }
//
//
//    public long getY() {
//        if (frozenPos == null) return 0;
//        return frozenPos[1];
//    }
//    public long getLastY() {
//        if (frozenPos == null) return 0;
//        return frozenPos[3];
//    }
//
//    public long getWidth() {
//        return viewportWidth;
//    }
//
//    public long getHeight() {
//        return viewportHeight;
//    }
//
//    public long getScale() {
//        return scale;
//    }
//
//    public void setScale(long scale) {
//        this.scale = scale;
//    }
//
//
//}
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