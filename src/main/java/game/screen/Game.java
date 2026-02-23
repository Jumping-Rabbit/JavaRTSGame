package game.screen;

import game.GameViewport;
import game.entity.Command;
import game.entity.Entity;
import game.entity.building.Building;
import game.entity.unit.UnitState;
import javafx.geometry.Rectangle2D;
import utils.NumUtil;
import game.entity.players;
import game.entity.unit.Unit;
import game.entity.unit.testRace1.Marine;
import utils.CollisionUtil;
import utils.DrawUtil;
import inputHandler.Input;
import inputHandler.InputHandler;
import inputHandler.InputType;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import tile.TileManager;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.*;
import static utils.NumUtil.DTL;

public class Game extends Screen {
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
    Rectangle2D selectedRectangle = null;
    ExecutorService updateThreadPool = Executors.newFixedThreadPool(4);
    ExecutorCompletionService<Void> updateThreadPoolCompletion = new ExecutorCompletionService<>(updateThreadPool);

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
        this(drawUtil, map, (int)StrictMath.floor(StrictMath.random() * playersData.size()));

    }
    public Game(DrawUtil drawUtil, File map, int playerNum) {
        units = new ArrayList<>();
        for (int i = 0; i <500; i++){
            units.add(new Marine(drawUtil, (int)(StrictMath.random()*1800), (int)(StrictMath.random()*900), players.BLUE));
        }
        buildings = new ArrayList<>();
        selectedEntities = new ArrayList<>();
        selectedEntities.addAll(units);

        tileManager = new TileManager(drawUtil, map);
        this.map = map;
        JSONParser parser = new JSONParser();
        try {
            Object object = parser.parse(new FileReader(map.getPath() + "/map.json"));
            JSONObject mapJSON = (JSONObject) object;
            JSONArray playersData = (JSONArray) mapJSON.get("playerData");
            JSONObject playerData = (JSONObject) playersData.get(playerNum);
            gameViewport = new GameViewport(DTL(Double.parseDouble(String.valueOf(playerData.get("x")))), DTL(Double.parseDouble(String.valueOf(playerData.get("y")))));
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
        selectedRectangle = game.selectedRectangle;
        map = game.map;
        drawUtil = game.drawUtil;
        gameState = game.gameState;
        gameViewport = game.gameViewport;
        tileManager = game.tileManager;
        selectedEntities = new ArrayList<>();
        units = new ArrayList<>();
        updateThreadPoolCompletion = game.updateThreadPoolCompletion;
        updateThreadPool = game.updateThreadPool;
        for (Unit unit : game.units){
            Unit newUnit = unit.copy();
            units.add(newUnit);
            if (game.selectedEntities.contains(unit)){
                selectedEntities.add(newUnit);
            }
        }
        buildings = game.buildings;
    }

    public static double clampOutside(double value) {
        double minBound = -0.001;
        double maxBound = 0.001;

        if (value > minBound && value < maxBound) {
            return (value >= 0) ? maxBound : minBound;
        }
        return value;
    }


    private void calculatePhysics() {//TODO: optimise this
        for (int iter = 0; iter < 10; iter++) {
            boolean noCollisions = true;

            for (int i = 0; i < units.size(); i++){
                Unit unit1 = units.get(i);
                long r1 = unit1.getCollisionRadius();
                long x1 = unit1.getX();
                long y1 = unit1.getY();

                for (int j = i + 1; j < units.size(); j++) {
                    Unit unit2 = units.get(j);
                    long r2 = unit2.getCollisionRadius();
                    long x2 = unit2.getX();
                    long y2 = unit2.getY();
                    //bounding box check
                    if (!CollisionUtil.RectRectCollision(x1, y1, r1+r1, r1+r1, x2, y2, r2+r2, r2+r2)) {
                        continue;
                    }

                    //circle check
                    if (CollisionUtil.CircleCircleCollision(x1+r1, y1+r1, r1, x2+r2, y2+r2, r2)) {
                        long dx = x2 - x1;
                        long dy = y2 - y1;
                        long distSqScaled = (dx * dx) + (dy * dy);

                        if (distSqScaled == 0) {
                            unit1.changeX(-1);
                            unit2.changeX(1);
                            noCollisions = false;
                            continue;
                        }

                        long distance = NumUtil.sqrt(distSqScaled);
                        if (distance == 0) distance = 1;

                        long overlap = (r1+r2) - distance;

                        if (overlap > 0) {
                            long halfOverlap = overlap / 2;
                            long moveX = (dx * halfOverlap) / distance;
                            long moveY = (dy * halfOverlap) / distance;

                            unit1.changeX(-moveX);
                            unit1.changeY(-moveY);
                            unit2.changeX(moveX);
                            unit2.changeY(moveY);

                            noCollisions = false;
                        }

                        // 3. Same-target logic
//                        if (unit1.getTargetX() == unit2.getTargetX() && unit1.getTargetY() == unit2.getTargetY()) {
//                            if (unit1.getUnitState() == UnitState.MOVING && unit2.getUnitState() == UnitState.IDLE) {
//                                long tdx1 = unit1.getTargetX() - x1;
//                                long tdy1 = unit1.getTargetY() - y1;
//                                long tdx2 = unit2.getTargetX() - x2;
//                                long tdy2 = unit2.getTargetY() - y2;
//
//                                if ((tdx1 * tdx1 + tdy1 * tdy1) <= (tdx2 * tdx2 + tdy2 * tdy2) + unit2.getCollisionRadius()) {
//                                    unit1.removeCommand();
//                                }
//                            }
//                        }
                    }
                }
            }
            for (Unit unit : units){
                unit.tick();
            }
            if (noCollisions) return;
        }

    }


    public void updateOnFrame() {
        for (Input input : InputHandler.getInputs()){
            switch (input.getInputType()) {
                case LEFT_CLICK:
                    selectedEntities.clear();
                    for (Unit unit : units){
                        if(CollisionUtil.PointCircleCollision(DTL(input.getX()) + gameViewport.getX(), DTL(input.getY()) + gameViewport.getY(), NumUtil.interpolate(unit.getLastX(), unit.getX(), drawUtil.getFactor())+unit.getRadius(), NumUtil.interpolate(unit.getLastY(), unit.getY(), drawUtil.getFactor())+unit.getRadius(), unit.getRadius())){
                            selectedEntities.clear();
                            selectedEntities.add(unit);
                        }
                    }
                    break;
                case DRAG:
                    selectedEntities.clear();
                    for (Unit unit : units){
                        if(CollisionUtil.RectCircleCollision(NumUtil.interpolate(unit.getLastX(), unit.getX(), drawUtil.getFactor())+unit.getRadius(), NumUtil.interpolate(unit.getLastY(), unit.getY(), drawUtil.getFactor())+unit.getRadius(), unit.getRadius(), DTL(StrictMath.min(input.getX(), input.getStartX()))+ gameViewport.getX(), DTL(StrictMath.min(input.getY(), input.getStartY()))+ gameViewport.getY(), DTL(StrictMath.abs(input.getX()-input.getStartX())), DTL(StrictMath.abs(input.getX()-input.getStartX())))){
                            selectedEntities.add(unit);
                        }
                    }
                    selectedRectangle = new Rectangle2D(StrictMath.min(input.getX(), input.getStartX()), StrictMath.min(input.getY(), input.getStartY()), StrictMath.abs(input.getX()-input.getStartX()), StrictMath.abs(input.getY()-input.getStartY()));
                    break;
                case RIGHT_CLICK:
                    for (Entity entity : selectedEntities){
                        entity.clearCommands();//make shift button work
                        entity.addCommand(new Command(InputType.RIGHT_CLICK, DTL(input.getX()) + gameViewport.getX(), DTL(input.getY()) + gameViewport.getY()));
                    }
                    break;
                case KEYPRESS:
                    if (Objects.equals(input.getKey(), "w")){
                        gameViewport.changeY(-10);
                    }else if (Objects.equals(input.getKey(), "a")){
                        gameViewport.changeX(-10);
                    } else if (Objects.equals(input.getKey(), "s")){
                        gameViewport.changeY(10);
                    }else if (Objects.equals(input.getKey(), "d")){
                        gameViewport.changeX(10);
                    }
                    break;
            }
        }

        for (Building building : buildings){
            building.updateOnFrame();
        }
        for (Unit unit : units){
            unit.updateOnFrame();
        }

        calculatePhysics();
        if (!InputHandler.MouseDown()){
            selectedRectangle = null;
        }
    }

    public void draw() {
        drawUtil.setGameViewport(gameViewport);
        for (Entity selected : selectedEntities){
            selected.drawSelectedRing();
        }
        for (Unit unit : units){
            unit.draw();
//            if (unit.getUnitState() == UnitState.IDLE){
//                unit.drawSelectedRing();
//            }
        }
        for (Building building : buildings){
            building.draw();
        }
        if (selectedRectangle != null){
            drawUtil.setColor(0, 255, 0, 0.25);
            drawUtil.fillRect(selectedRectangle);
        }
        drawUtil.disableGameViewport();
    }
}
