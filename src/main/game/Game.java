package main.game;

import main.DrawUtil;
import main.inputHandler.InputHandler;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import tile.TileManager;

import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Game {
    public enum GameState {
        RUNNING,
        PAUSED,
        MENU
    }
    File map;
    DrawUtil drawUtil;
    GameState gameState = GameState.RUNNING;
    GameViewport gameViewport;//get x and y from map
    TileManager tileManager;
    public Game(DrawUtil drawUtil, File map) {
        JSONParser parser = new JSONParser();
        Object object;
        try {
            object = parser.parse(new FileReader(map.getPath() + "/map.json"));
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
        JSONObject mapJSON = (JSONObject) object;
        JSONArray playersData = (JSONArray) mapJSON.get("playerData");
        this(drawUtil, map, (int)Math.floor(Math.random() * playersData.size()));
    }
    public Game(DrawUtil drawUtil, File map, int playerNum) {
        tileManager = new TileManager(new File("test"));
        this.map = map;
        JSONParser parser = new JSONParser();
        try {
            Object object = parser.parse(new FileReader(map.getPath() + "/map.json"));
            JSONObject mapJSON = (JSONObject) object;
            JSONArray playersData = (JSONArray) mapJSON.get("playerData");
            JSONObject playerData = (JSONObject) playersData.get(playerNum);
            gameViewport = new GameViewport(Double.parseDouble(String.valueOf(playerData.get("x"))), Double.parseDouble(String.valueOf(playerData.get("y"))));
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }

        this.drawUtil = drawUtil;
        drawUtil.setGameViewport(gameViewport);
    }
//
    public void updateOnFrame() {

    }
    public void draw() {

    }
}
