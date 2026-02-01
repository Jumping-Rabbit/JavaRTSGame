package main.game;

import entity.Command;
import entity.Entity;
import entity.building.Building;
import entity.players;
import entity.unit.Unit;
import entity.unit.testRace1.Marine;
import main.DrawUtil;
import main.inputHandler.Input;
import main.inputHandler.InputHandler;
import main.inputHandler.InputType;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import tile.TileManager;

import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Game extends Screen{
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
    ArrayList<Unit> units;
    ArrayList<Building> buildings;
    ArrayList<Entity> selectedEntities;
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
        units = new ArrayList<>();
        units.add(new Marine(drawUtil, 100, 100, players.BLUE));//temp for testing
        buildings = new ArrayList<>();
        selectedEntities = new ArrayList<>();
        selectedEntities.add(units.getFirst());

        tileManager = new TileManager(drawUtil, map);
        this.map = map;
        JSONParser parser = new JSONParser();
        try {
            Object object = parser.parse(new FileReader(map.getPath() + "/map.json"));
            JSONObject mapJSON = (JSONObject) object;
            JSONArray playersData = (JSONArray) mapJSON.get("playerData");
            JSONObject playerData = (JSONObject) playersData.get(playerNum);
            gameViewport = new GameViewport(Double.parseDouble(String.valueOf(playerData.get("x"))), Double.parseDouble(String.valueOf(playerData.get("y"))));
            System.out.println(gameViewport.getX());
            System.out.println(gameViewport.getY());
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }

        this.drawUtil = drawUtil;
        drawUtil.setGameViewport(gameViewport);
    }

    public Screen copy(){
        return new Game(this);
    }

    private Game(Game game){
        map = game.map;
        drawUtil = game.drawUtil;
        gameState = game.gameState;
        gameViewport = game.gameViewport;
        tileManager = game.tileManager;
        selectedEntities = new ArrayList<>();
        units = new ArrayList<>();
        for (Unit unit : game.units){
            Unit newUnit = unit.copy();
            units.add(newUnit);
            if (game.selectedEntities.contains(unit)){
                selectedEntities.add(newUnit);
            }
        }
        buildings = game.buildings;
    }

    public void updateOnFrame() {
        for (Input input : InputHandler.getInputs()){
            switch (input.getInputType()) {
                case LEFT_CLICK:
                    for (Entity entity : selectedEntities){
                        entity.addCommand(new Command(InputType.LEFT_CLICK, input.getX(), input.getY()));
                    }
                    break;
            }
        }
        for (Unit unit : units){
            unit.updateOnFrame();
        }
        for (Building building : buildings){
            building.updateOnFrame();
        }
    }

    public void draw() {
        for (Entity selected : selectedEntities){
            selected.drawSelectedRing();
        }
        for (Unit unit : units){
            unit.draw();
        }
        for (Building building : buildings){
            building.draw();
        }

    }
}
