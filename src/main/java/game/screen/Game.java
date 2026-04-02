package game.screen;

import game.GameViewport;
import game.Replay;
import game.entity.Command;
import game.entity.Entity;
import game.entity.building.Building;
import game.entity.players;
import game.entity.unit.Unit;
import game.entity.unit.vanguard.Marine;
import inputHandler.Input;
import inputHandler.InputHandler;
import inputHandler.InputType;
import inputHandler.Keys;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import javafx.geometry.Rectangle2D;
import tile.TileManager;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import utils.CollisionUtil;
import utils.DrawUtil;
import utils.NumUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

import static utils.NumUtil.DTL;

public class Game extends Screen {
    private static final int SHIFT = 22;
    private static final int THREAD_COUNT = Math.max(1, Runtime.getRuntime().availableProcessors() - 2);
    private static final ForkJoinPool pool = new ForkJoinPool(THREAD_COUNT);
    private final ArrayList<Unit> visibleUnits = new ArrayList<>(1000);
    private final Long2IntMap cellHeads;
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

    public Game(DrawUtil drawUtil, File map) {
        var objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.readTree(new File(map.getPath() + "/map.json"));
        JsonNode playersData = root.path("playerData");
        this(drawUtil, map, (int) StrictMath.floor(StrictMath.random() * playersData.size()));

    }

    public Game(DrawUtil drawUtil, File map, int playerNum) {
        exit = false;
        units = new ArrayList<>();
        for (int i = 0; i < 20000; i++) {
            units.add(new Marine(drawUtil, (int) (StrictMath.random() * 19200), (int) (StrictMath.random() * 10800), players.BLUE));
        }
        buildings = new ArrayList<>();
        selectedEntities = new ArrayList<>();
        selectedEntities.addAll(units);

        tileManager = new TileManager(drawUtil, map);
        this.map = map;
        var objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.readTree(new File(map.getPath() + "/map.json"));
        JsonNode playersData = root.path("playerData");
        JsonNode playerData = playersData.get(playerNum);
        gameViewport = new GameViewport(DTL(Double.parseDouble(String.valueOf(playerData.get("x")))), DTL(Double.parseDouble(String.valueOf(playerData.get("y")))));
        this.drawUtil = drawUtil;
        drawUtil.setGameViewport(gameViewport);
        tickNum = 0;
        Replay.newReplay(new File(""));
        cellHeads = new Long2IntOpenHashMap();
        cellHeads.defaultReturnValue(-1);
//        unitsById = new Int2ObjectOpenHashMap<>();
    }
    private Game(Game game) {
        selectedRectangle = game.selectedRectangle;
        map = game.map;
        drawUtil = game.drawUtil;
        gameState = game.gameState;
        gameViewport = game.gameViewport;
        tileManager = game.tileManager;
        selectedEntities = new ArrayList<>();
        units = new ArrayList<>();
        Int2ObjectOpenHashMap<Unit> idToNewUnit = new Int2ObjectOpenHashMap<>(game.units.size());

        units = new ArrayList<>(game.units.size());
        for (Unit unit : game.units) {
            Unit copiedUnit = (Unit) unit.copy();
            idToNewUnit.put(unit.id, copiedUnit);
            units.add(copiedUnit);
        }

        selectedEntities = new ArrayList<>(game.selectedEntities.size());
        for (Entity entity : game.selectedEntities) {
            Unit copiedUnit = idToNewUnit.get(entity.id);
            if (copiedUnit != null) {
                selectedEntities.add(copiedUnit);
            }
        }


        buildings = game.buildings;
        exit = game.exit;
        tickNum = game.tickNum;
        cellHeads = new Long2IntOpenHashMap();
        cellHeads.defaultReturnValue(-1);
    }

    public Screen copy() {
        return new Game(this);
    }

    private void setSpatialGrid() {
        cellHeads.clear();
        for (int i = 0; i < units.size(); i++) {
            Unit u = units.get(i);
            long key = (u.getX() >> 22) << 32 | (u.getY() >> 22) & 0xFFFFFFFFL;
            u.nextInCell = cellHeads.getOrDefault(key, -1);
            cellHeads.put(key, i);
        }
    }

    private void applyPush(Unit u, long dx, long dy) {
//        u.changeX(dx);
//        u.changeY(dy);
        u.changeXImmediate(dx);
        u.changeYImmediate(dy);
    }

    private void calculatePhysics() {


        for (int iter = 0; iter < 1; iter++) {
//            long t1 = System.nanoTime();
            setSpatialGrid();
//            long t2 = System.nanoTime();

            List<CollisionResult> collisions = pool.invoke(new CollisionDetectionTask(0, units.size()));
//            long t3 = System.nanoTime();

            for (CollisionResult res : collisions) {
                applyPush(res.u1, -res.moveX, -res.moveY);
                applyPush(res.u2, res.moveX, res.moveY);
            }
//            long t4 = System.nanoTime();

//            System.out.println("spatial grid: " + (t2-t1)/1_000_000 + "detect: " + (t3-t2)/1_000_000 + "ms  resolve: " + (t4-t3)/1_000_000 + "collisions amount: " + collisions.size());
        }

//        for (Unit unit : units) {
//            unit.tick();
//        }
    }

    public void updateOnFrame() {
        Replay.addTick(InputHandler.getInputs(), tickNum);
        tickNum++;
        for (Input input : InputHandler.getInputs()) {
            switch (input.getInputType()) {
                case LEFT_CLICK:
                    selectedEntities.clear();
                    for (Unit unit : units) {
                        if (CollisionUtil.PointCircleCollision(DTL(input.getX()) + gameViewport.getX(), DTL(input.getY()) + gameViewport.getY(), NumUtil.interpolate(unit.getLastX(), unit.getX(), drawUtil.getFactor()) + unit.getRadius(), NumUtil.interpolate(unit.getLastY(), unit.getY(), drawUtil.getFactor()) + unit.getRadius(), unit.getRadius())) {
                            selectedEntities.clear();
                            selectedEntities.add(unit);
                        }
                    }
                    break;
                case DRAG:
                    selectedEntities.clear();
                    selectedRectangle = new Rectangle2D(StrictMath.min(input.getX(), input.getStartX()), StrictMath.min(input.getY(), input.getStartY()), StrictMath.abs(input.getX() - input.getStartX()), StrictMath.abs(input.getY() - input.getStartY()));
                    for (Unit unit : units) {
                        if (CollisionUtil.RectCircleCollision(NumUtil.interpolate(unit.getX(), unit.getLastX(), drawUtil.getFactor()) + unit.getRadius(), NumUtil.interpolate(unit.getY(), unit.getLastY(), drawUtil.getFactor()) + unit.getRadius(), unit.getRadius(), selectedRectangle)) {
                            selectedEntities.add(unit);
                        }
                    }

                    break;
                case RIGHT_CLICK:
                    for (Entity entity : selectedEntities) {
                        entity.clearCommands();//make shift button work
                        entity.addCommand(new Command(InputType.RIGHT_CLICK, DTL(input.getX()) + gameViewport.getX(), DTL(input.getY()) + gameViewport.getY()));
                    }
                    break;
                case KEYPRESS:
                    if (input.getKey() == Keys.ESCAPE) {
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

        for (Building building : buildings) {
            building.updateOnFrame();
        }
//        for (Unit unit : units) {
//            unit.updateOnFrame();
//        }
        units.parallelStream().forEach(Unit::updateOnFrame);

        calculatePhysics();
        if (!InputHandler.MouseDown()) {
            selectedRectangle = null;
        }
    }

    public void draw() {
        drawUtil.setGameViewport(gameViewport);
        for (Entity selected : selectedEntities) {
            selected.drawSelectedRing();
        }

        visibleUnits.clear();
        for (Unit unit : units) {
            if (CollisionUtil.RectRectCollision(0, 0, 19200000, 10800000, unit.getX(), unit.getY(), unit.getDiameter(), unit.getModel().getHeight())) {
                visibleUnits.add(unit);
            }
        }
        visibleUnits.sort(Entity.Y_COMPARATOR);
        for (int i = 0; i < visibleUnits.size(); i++) {
            visibleUnits.get(i).draw();
        }

        for (Building building : buildings) {
            building.draw();
        }
        if (selectedRectangle != null) {
            drawUtil.setColor(0, 255, 0, 0.25);
            drawUtil.fillRect(selectedRectangle);
        }
    }

    public enum GameState {
        RUNNING,
        PAUSED,
        MENU
    }

    private static class CollisionResult {
        Unit u1, u2;
        long moveX, moveY;

        CollisionResult(Unit u1, Unit u2, long moveX, long moveY) {
            this.u1 = u1;
            this.u2 = u2;
            this.moveX = moveX;
            this.moveY = moveY;
        }
    }

    private class CollisionDetectionTask extends RecursiveTask<List<CollisionResult>> {
        private static final int THRESHOLD = 512;
        private final int start, end;

        CollisionDetectionTask(int start, int end) {
            this.start = start;
            this.end = end;
        }

        @Override
        protected List<CollisionResult> compute() {
            if (end - start <= THRESHOLD) {
                return detectSequentially();
            }

            int mid = (start + end) / 2;
            CollisionDetectionTask left = new CollisionDetectionTask(start, mid);
            CollisionDetectionTask right = new CollisionDetectionTask(mid, end);
            left.fork();

            List<CollisionResult> results = right.compute();
            results.addAll(left.join());
            return results;
        }

        private List<CollisionResult> detectSequentially() {
            List<CollisionResult> results = new ArrayList<>();
            for (int i = start; i < end; i++) {
                Unit unit1 = units.get(i);

                long r1 = unit1.getCollisionRadius();
                long x1 = unit1.getX();
                long y1 = unit1.getY();
                long gx = unit1.getX() >> SHIFT;
                long gy = unit1.getY() >> SHIFT;

                for (long nx = gx - 1; nx <= gx + 1; nx++) {
                    for (long ny = gy - 1; ny <= gy + 1; ny++) {
                        long key = (nx << 32) | (ny & 0xFFFFFFFFL);
                        int currentIndex = cellHeads.get(key);

                        while (currentIndex != -1) {
                            Unit unit2 = units.get(currentIndex);

                            if (unit1.id < unit2.id) {
                                long r2 = unit2.getCollisionRadius();
                                long rSum = r1 + r2;
                                long dx = unit2.getX() - x1;
                                if (dx == 0) dx = 1;
                                long dy = unit2.getY() - y1;
                                if (dy == 0) dy = 1;

                                long distSq = dx * dx + dy * dy;
                                long rSumSq = rSum * rSum;

                                if (distSq < rSumSq) {
                                    long distance = StrictMath.max(NumUtil.sqrtFast(distSq), 1);

                                    long overlap = rSum - distance;
                                    long pushAmount = (long) (overlap * 0.5);

                                    long moveX = (dx * pushAmount) / distance;
                                    long moveY = (dy * pushAmount) / distance;
                                    results.add(new CollisionResult(unit1, unit2, moveX, moveY));
                                }
                            }
                            currentIndex = unit2.nextInCell;
                        }
                    }
                }
            }
            return results;
        }
    }
}
