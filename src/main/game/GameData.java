package main.game;

import main.inputHandler.InputHandler;
import tile.TileManager;

import java.awt.*;
import java.util.Random;

public class GameData {
    public enum GameState {
        RUNNING,
        PAUSED,
        MENU
    }
    GameState gameState = GameState.MENU;
//    public GameState getGameState() {
//        return gameState;
//    }
    TileManager tileManager;
    public GameData(int seed, InputHandler inputHandler) {
        Random random = new Random(seed);
//        tileManager = new TileManager(this);
    }
//
    public void updateOnFrame() {

    }
    public void draw(Graphics2D g2) {
        double scale = Viewport.viewport.getScale();
    }
}
