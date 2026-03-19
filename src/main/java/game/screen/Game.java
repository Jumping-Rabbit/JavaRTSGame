package game.screen;

import game.GameViewport;
import game.Replay;
import game.entity.Command;
import game.entity.Entity;
import game.entity.building.Building;
import inputHandler.Keys;
import javafx.geometry.Rectangle2D;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import utils.NumUtil;
import game.entity.players;
import game.entity.unit.Unit;
import game.entity.unit.testRace1.Marine;
import utils.CollisionUtil;
import utils.DrawUtil;
import inputHandler.Input;
import inputHandler.InputHandler;
import inputHandler.InputType;
import tile.TileManager;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

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
    long tickNum;
//    Entity[] spatialPartitioning = null;//TODO: fix this and put in game init when have the map so it can be sized to the map size
    //im thinking 128 or 256, but itl only be fast if its in the 10^6 scale so idk what the numbers should be


    public Game(DrawUtil drawUtil, File map) {
        var objectMapper = new ObjectMapper();
        JsonNode root= objectMapper.readTree(new File(map.getPath() + "/map.json"));
        JsonNode playersData = root.path("playerData");
        this(drawUtil, map, (int)StrictMath.floor(StrictMath.random() * playersData.size()));

    }
    public Game(DrawUtil drawUtil, File map, int playerNum) {
        exit = false;
        units = new ArrayList<>();
        for (int i = 0; i <100; i++){
            units.add(new Marine(drawUtil, (int)(StrictMath.random()*1800), (int)(StrictMath.random()*900), players.BLUE));
        }
        buildings = new ArrayList<>();
        selectedEntities = new ArrayList<>();
        selectedEntities.addAll(units);

        tileManager = new TileManager(drawUtil, map);
        this.map = map;
        var objectMapper = new ObjectMapper();
        JsonNode root= objectMapper.readTree(new File(map.getPath() + "/map.json"));
        JsonNode playersData = root.path("playerData");
        JsonNode playerData = playersData.get(playerNum);
        gameViewport = new GameViewport(DTL(Double.parseDouble(String.valueOf(playerData.get("x")))), DTL(Double.parseDouble(String.valueOf(playerData.get("y")))));
        this.drawUtil = drawUtil;
        drawUtil.setGameViewport(gameViewport);
        tickNum = 0;
        Replay.newReplay(new File(""));
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
        for (Unit unit : game.units){
            Unit newUnit = unit.copy();
            units.add(newUnit);
            if (game.selectedEntities.contains(unit)){
                selectedEntities.add(newUnit);
            }
        }
        buildings = game.buildings;
        exit = game.exit;
        tickNum = game.tickNum;
    }

    public static double clampOutside(double value) {
        double minBound = -0.001;
        double maxBound = 0.001;

        if (value > minBound && value < maxBound) {
            return (value >= 0) ? maxBound : minBound;
        }
        return value;
    }


    private void calculatePhysics() {//TODO: optimise this wit spacial partitioning or whatever
        for (int iter = 0; iter < 8; iter++) {
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
//                    System.out.println("hi");

                    //circle check
                    if (CollisionUtil.CircleCircleCollision(x1+r1, y1+r1, r1, x2+r2, y2+r2, r2)) {
                        long dx = x2 - x1;
                        long dy = y2 - y1;
                        long distSqScaled = dx * dx + dy * dy;
//                        System.out.println("bye");

                        if (distSqScaled == 0) {
                            unit1.changeX(-1);
                            unit2.changeX(1);
                            noCollisions = false;
                            continue;
                        }
                        long distance = NumUtil.sqrtFast(distSqScaled);
                        if (distance == 0) distance = 1;

                        long overlap = (r1+r2) - distance;
//                        System.out.println("a" + overlap);

                        if (overlap > 0) {
                            long halfOverlap = overlap / 2;
                            long moveX = (dx * halfOverlap) / distance;
                            long moveY = (dy * halfOverlap) / distance;
//                            System.out.println(moveX);

                            unit1.changeX(-moveX);
                            unit1.changeY(-moveY);
                            unit2.changeX(moveX);
                            unit2.changeY(moveY);

                            noCollisions = false;
                        }
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
        Replay.addTick(InputHandler.getInputs(), tickNum);
        tickNum++;
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
                    if (input.getKey() == Keys.ESCAPE){
                        exit = true;
                    }
//                    if (input.getKey() == Keys.W){
//                        gameViewport.changeY(-10);
//                    }else if (input.getKey() == Keys.A){
//                        gameViewport.changeX(-10);
//                    } else if (input.getKey() == Keys.S){
//                        gameViewport.changeY(10);
//                    }else if (input.getKey() == Keys.D){
//                        gameViewport.changeX(10);
//                    }
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
