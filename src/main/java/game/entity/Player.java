
/*package main.game.entity;

import main.Assets;
import main.KeyHandler;
import main.game.Viewport;

import java.awt.*;

public class Player extends Entity{
    KeyHandler keyHandler;
    private final double speed;
    private final double sprintSpeed;
    private long lastWPressTime, lastAPressTime,lastSPressTime, lastDPressTime = System.nanoTime();
    private boolean lastWState, lastAState,lastSState, lastDState = false;
    public Player(double x, double y, EntityDirection entityDirection, KeyHandler keyHandler) {
        this.keyHandler = keyHandler;
        this.x = x;
        this.y = y;
        this.entityDirection = entityDirection;
        speed = 64;
        sprintSpeed = 96;
        imageNorth = Assets.ImageAsset.PLAYER_NORTH.getAsset();
        imageEast = Assets.ImageAsset.PLAYER_EAST.getAsset();
        imageSouth = Assets.ImageAsset.PLAYER_SOUTH.getAsset();
        imageWest = Assets.ImageAsset.PLAYER_WEST.getAsset();
    }
    @Override
    public void updateOnFrame(double timePassed) {
        if (keyHandler.shiftPressed) {
            if (keyHandler.wPressed) {
                y -= timePassed*sprintSpeed;
            }
            if (keyHandler.sPressed) {
                y += timePassed*sprintSpeed;
            }
            if (keyHandler.aPressed) {
                x -= timePassed*sprintSpeed;
            }
            if (keyHandler.dPressed) {
                x += timePassed*sprintSpeed;
            }
        } else {
            if (keyHandler.wPressed) {
                y -= timePassed*speed;
            }
            if (keyHandler.sPressed) {
                y += timePassed*speed;
            }
            if (keyHandler.aPressed) {
                x -= timePassed*speed;
            }
            if (keyHandler.dPressed) {
                x += timePassed*speed;
            }
        }
        if (!lastWState && keyHandler.wPressed) {
            lastWPressTime = System.nanoTime();
        }
        if (!lastAState && keyHandler.aPressed) {
            lastAPressTime = System.nanoTime();
        }
        if (!lastSState && keyHandler.sPressed) {
            lastSPressTime = System.nanoTime();
        }
        if (!lastDState && keyHandler.dPressed) {
            lastDPressTime = System.nanoTime();
        }

        if (x - (Viewport.viewport.getWidth() / 2d) > 0) {
            Viewport.viewport.setX(x - (Viewport.viewport.getWidth() / 2d));
        } else {
            Viewport.viewport.setX(0);
        }
        if (y - (Viewport.viewport.getHeight() / 2d) > 0) {
            Viewport.viewport.setY(y - (Viewport.viewport.getHeight() / 2d));
        } else {
            Viewport.viewport.setY(0);
        }
        if (x < -5) {
            x = -5;
        }
        if (y < -5) {
            y = -5;
        }
        long shortestTime = System.nanoTime();
        if (keyHandler.wPressed && lastWPressTime < shortestTime) {
            shortestTime = lastWPressTime;
            entityDirection = EntityDirection.NORTH;
        }
        if (keyHandler.aPressed && lastAPressTime < shortestTime) {
            shortestTime = lastAPressTime;
            entityDirection = EntityDirection.WEST;
        }
        if (keyHandler.sPressed && lastSPressTime < shortestTime) {
            shortestTime = lastSPressTime;
            entityDirection = EntityDirection.SOUTH;
        }
        if (keyHandler.dPressed && lastDPressTime < shortestTime) {
            //dosent need shortest time
            entityDirection = EntityDirection.EAST;
        }
        shortestTime = System.nanoTime();
        if ((keyHandler.wPressed && keyHandler.sPressed) && (keyHandler.aPressed || keyHandler.dPressed)) {
            if (keyHandler.aPressed && lastAPressTime < shortestTime) {
                shortestTime = lastAPressTime;
                entityDirection = EntityDirection.WEST;
            }
            if (keyHandler.dPressed && lastDPressTime < shortestTime) {
                entityDirection = EntityDirection.EAST;
            }
        }
        if ((keyHandler.aPressed && keyHandler.dPressed) && (keyHandler.wPressed || keyHandler.sPressed)) {
            if (keyHandler.wPressed && lastWPressTime < shortestTime) {
                shortestTime = lastWPressTime;
                entityDirection = EntityDirection.NORTH;
            }
            if (keyHandler.sPressed && lastSPressTime < shortestTime) {
                entityDirection = EntityDirection.SOUTH;
            }
        }

        lastWState = keyHandler.wPressed;
        lastAState = keyHandler.aPressed;
        lastSState = keyHandler.sPressed;
        lastDState = keyHandler.dPressed;
    }
    @Override
    public void draw(Graphics2D g2) {
        double scale = Viewport.viewport.getScale();
        double viewportX = Viewport.viewport.getX();
        double viewportY = Viewport.viewport.getY();
        switch (entityDirection) {
            case EntityDirection.NORTH:
                g2.drawImage(imageNorth, (int)((x-viewportX)*scale), (int)((y-viewportY)*scale), (int)(32*scale), (int)(32*scale), null);
                break;
            case EntityDirection.EAST:
                g2.drawImage(imageEast, (int)((x-viewportX)*scale), (int)((y-viewportY)*scale), (int)(32*scale), (int)(32*scale), null);
                break;
            case EntityDirection.SOUTH:
                g2.drawImage(imageSouth, (int)((x-viewportX)*scale), (int)((y-viewportY)*scale), (int)(32*scale), (int)(32*scale), null);
                break;
            case EntityDirection.WEST:
                g2.drawImage(imageWest, (int)((x-viewportX)*scale), (int)((y-viewportY)*scale), (int)(32*scale), (int)(32*scale), null);
                break;
        }

    }
}
*/