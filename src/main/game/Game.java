package main.game;

import main.DrawUtil;
import main.inputHandler.InputHandler;
import tile.TileManager;

import java.awt.*;
import java.io.File;

public class Game {
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
    public Game() {
        tileManager = new TileManager(new File("test"));
    }
//
    public void updateOnFrame() {

    }
    public void draw(DrawUtil drawUtil) {
        double scale = Viewport.viewport.getScale();
    }
}
