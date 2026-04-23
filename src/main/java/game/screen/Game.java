package game.screen;

import game.GameViewport;
import game.Replay;
import game.entity.Command;
import game.entity.Entity;
import game.entity.building.Building;
import game.entity.players;
import game.entity.unit.Unit;
import game.entity.unit.vanguard.Marine;
import inputHandler.*;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import javafx.geometry.Rectangle2D;
import tile.TileManager;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import utils.*;

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
    private final ArrayList<Entity> visibleEntities = new ArrayList<>(1000);
    private final Long2IntMap cellHeads;
    File map;
    GameState gameState = GameState.RUNNING;
    GameViewport gameViewport;//get x and y from map
    TileManager tileManager;
    ArrayList<Unit> units;
    ArrayList<Building> buildings;
    ArrayList<Entity> selectedEntities;
    Rectangle2D selectedRectangle = null;
    long tickNum;
    long mapWidth;
    long mapHeight;
    long[] hotX;
    long[] hotY;
    long[] hotRad;
    long[] hotNIC;

    public Game(File map) {
        var objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.readTree(new File(map.getPath() + "/map.json"));
        JsonNode playersData = root.path("playerData");
        this(map, (int) StrictMath.floor(StrictMath.random() * playersData.size()));

    }

    public Game(File map, int playerNum) {
        exit = false;
        units = new ArrayList<>();
        for (int i = 0; i < 20000; i++) {
            units.add(new Marine((int) (StrictMath.random() * 7680), (int) (StrictMath.random() * 4320), players.BLUE));
        }
        buildings = new ArrayList<>();
        selectedEntities = new ArrayList<>();
        selectedEntities.addAll(units);
        for (Unit unit : units){
            unit.setIsSelected(true);
        }

        tileManager = new TileManager(map);
        this.map = map;
        var objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.readTree(new File(map.getPath() + "/map.json"));
        JsonNode playersData = root.path("playerData");
        JsonNode playerData = playersData.get(playerNum);
        mapWidth = root.get("width").asLong();
        mapHeight = root.get("height").asLong();
        gameViewport = new GameViewport(DTL(Double.parseDouble(String.valueOf(playerData.get("x")))), DTL(Double.parseDouble(String.valueOf(playerData.get("y")))));
        DrawUtil.setGameViewport(gameViewport);
        tickNum = 0;
        Replay.newReplay(map);
        cellHeads = new Long2IntOpenHashMap();
        cellHeads.defaultReturnValue(-1);
//        unitsById = new Int2ObjectOpenHashMap<>();
        System.out.println("physics threads: " + THREAD_COUNT);
    }

    private Game(Game game) {
        selectedRectangle = game.selectedRectangle;
        map = game.map;
        gameState = game.gameState;
        gameViewport = game.gameViewport;
        tileManager = game.tileManager;
        selectedEntities = new ArrayList<>();
        units = new ArrayList<>();
        mapWidth = game.mapWidth;
        mapHeight = game.mapHeight;
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
                copiedUnit.setIsSelected(true);
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

    private void clearSelected(){
        for (Entity entity : selectedEntities){
            entity.setIsSelected(false);
        }
        selectedEntities.clear();
    }

    private void addSelected(Entity entity){
        selectedEntities.add(entity);
        entity.setIsSelected(true);
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
            long t1 = System.nanoTime();
            hotX = new long[units.size()];
            hotY = new long[units.size()];
            hotRad = new long[units.size()];
            hotNIC = new long[units.size()];
            for (int i = 0; i < units.size(); i++){
                Unit unit = units.get(i);
                hotX[i] = unit.getX();
                hotY[i] = unit.getY();
                hotRad[i] = unit.getCollisionRadius();
                hotNIC[i] = unit.nextInCell;
            }
            long t2 = System.nanoTime();
            List<CollisionResult> collisions = pool.invoke(new CollisionDetectionTask(0, units.size(), hotX, hotY, hotRad, hotNIC));
            long t3 = System.nanoTime();

            for (CollisionResult res : collisions) {
                applyPush(res.u1, -res.moveX, -res.moveY);
                applyPush(res.u2, res.moveX, res.moveY);
            }
            long t4 = System.nanoTime();
            if (tickNum % 2 == 0) LoggerUtil.log(PerformanceType.PHYSICS, "set hot:", (t2-t1)/1000000d, "detect:", (t3-t2)/1000000d, "resolve:", (t4-t3)/1000000d, "collisions:", collisions.size());
//            System.out.println("spatial grid: " + (t2-t1)/1000000d + "detect: " + (t3-t2)/1000000d + "resolve: " + (t4-t3)/1000000d + "collisions amount: " + collisions.size());
        }
        for (Unit unit : units) {
            if (unit.getX() >= mapWidth) unit.setX(mapWidth-1);
            else if (unit.getX() <= 0) unit.setX(1);
            if (unit.getY() >= mapHeight) unit.setY(mapHeight-1);
            else if (unit.getY() <= 0) unit.setY(1);
        }
    }

    public void updateOnFrame() {
        long t1 = System.nanoTime();
        Replay.addTick(InputHandler.getInputs(), tickNum);
        long t2 = System.nanoTime();
        setSpatialGrid();
        long t3 = System.nanoTime();
        tickNum++;
        for (Input input : InputHandler.getInputs()) {
            switch (input.getInputType()) {
                case LEFT_CLICK: {
                    clearSelected();
                    long clickX = DTL(input.getX() + gameViewport.getX());
                    long clickY = DTL(input.getY() + gameViewport.getY());

                    long gridX = clickX >> SHIFT;
                    long gridY = clickY >> SHIFT;
                    int clickIdx = cellHeads.get((gridX << 32) | (gridY & 0xFFFFFFFFL));

                    while (clickIdx != -1) {
                        Unit unit = units.get(clickIdx);
                        double x = NumUtil.interpolate(unit.getLastX(), unit.getX(), DrawUtil.getFactor());
                        double y = NumUtil.interpolate(unit.getLastY(), unit.getY(), DrawUtil.getFactor());

                        if (CollisionUtil.PointCircleCollision(clickX, clickY, x, y, unit.getCollisionRadius())) {
                            addSelected(unit);
                        }
                        clickIdx = unit.nextInCell;
                    }
                    break;
                }

                case DRAG: {
                    clearSelected();
                    selectedRectangle = new Rectangle2D(
                            StrictMath.min(input.getX(), input.getStartX()),
                            StrictMath.min(input.getY(), input.getStartY()),
                            StrictMath.abs(input.getX() - input.getStartX()),
                            StrictMath.abs(input.getY() - input.getStartY())
                    );

                    double rectX = DTL(selectedRectangle.getMinX() + gameViewport.getX());
                    double rectY = DTL(selectedRectangle.getMinY() + gameViewport.getY());
                    double rectWidth = DTL(selectedRectangle.getWidth());
                    double rectHeight = DTL(selectedRectangle.getHeight());

                    int startX = (int) ((long)rectX >> SHIFT);
                    int endX   = (int) ((long)(rectX + rectWidth) >> SHIFT);
                    int startY = (int) ((long)rectY >> SHIFT);
                    int endY   = (int) ((long)(rectY+rectHeight) >> SHIFT);

                    for (int gx = startX; gx <= endX; gx++) {
                        for (int gy = startY; gy <= endY; gy++) {
                            int boxIdx = cellHeads.get(((long) gx << 32) | (gy & 0xFFFFFFFFL));

                            while (boxIdx != -1) {
                                Unit unit = units.get(boxIdx);
                                double x = NumUtil.interpolate(unit.getX(), unit.getLastX(), DrawUtil.getFactor());
                                double y = NumUtil.interpolate(unit.getY(), unit.getLastY(), DrawUtil.getFactor());

                                if (CollisionUtil.RectCircleCollision(x, y, unit.getCollisionRadius(), rectX, rectY, rectWidth, rectHeight)) {
                                    addSelected(unit);
                                }
                                boxIdx = unit.nextInCell;
                            }
                        }
                    }
                    break;
                }
                case RIGHT_CLICK:
                    for (Entity entity : selectedEntities) {
                        entity.clearCommands();//make shift button work
                        entity.addCommand(new Command(InputType.RIGHT_CLICK, DTL(input.getX()) + gameViewport.getX(), DTL(input.getY()) + gameViewport.getY()));
                    }
                    break;
                case KEYPRESS:
                    if (input.getAction() == Actions.BACK) {
                        exit = true;
                    }
                    if (input.getAction() == Actions.UP){
                        gameViewport.changeY(-10);
                    }else if (input.getAction() == Actions.LEFT){
                        gameViewport.changeX(-10);
                    } else if (input.getAction() == Actions.DOWN){
                        gameViewport.changeY(10);
                    }else if (input.getAction() == Actions.RIGHT){
                        gameViewport.changeX(10);
                    }
                    break;
            }
        }
        long t4 = System.nanoTime();

//        for (Building building : buildings) {
//            building.updateOnFrame();
//        }
//        for (Unit unit : units) {
//            unit.updateOnFrame();
//        }
        buildings.parallelStream().forEach(Building::updateOnFrame);
        units.parallelStream().forEach(Unit::updateOnFrame);
        long t5 = System.nanoTime();

        calculatePhysics();
        if (!InputHandler.MouseDown()) {
            selectedRectangle = null;
        }
        long t6 = System.nanoTime();
        if (tickNum % 2 == 0) LoggerUtil.log(PerformanceType.TICK, "replay:", (t2-t1)/1000000d,"spatial grid:", (t3-t2)/1000000d, "input:", (t4-t3)/1000000d, "update:", (t5-t4)/1000000d, "physics:" + (t6-t5)/1000000d);
//        System.out.println("input: " + (t2-t1)/1000000d + "update: " + (t3-t2)/1000000d + "ms  physics: " + (t4-t3)/1000000d);
    }

    public void draw() {
        DrawUtil.setGameViewport(gameViewport);

        for (Unit unit : units){
            unit.drawTarget();
        }

        visibleEntities.clear();
        long viewX = NumUtil.DTL(gameViewport.getX());
        long viewY = NumUtil.DTL(gameViewport.getY());
        long viewWidth = 19200000;
        long viewHeight = 10800000;

        int minGridX = (int) (viewX >> SHIFT);
        int maxGridX = (int) ((viewX + viewWidth) >> SHIFT);
        int minGridY = (int) (viewY >> SHIFT);
        int maxGridY = (int) ((viewY + viewHeight) >> SHIFT);

        for (int gx = minGridX; gx <= maxGridX; gx++) {
            for (int gy = minGridY; gy <= maxGridY; gy++) {
                long key = ((long) gx << 32) | (gy & 0xFFFFFFFFL);
                int currentIndex = cellHeads.get(key);

                while (currentIndex != -1) {
                    Unit unit = units.get(currentIndex);
                    visibleEntities.add(unit);

                    currentIndex = unit.nextInCell;
                }
            }
        }

        for (Building building : buildings) {
            if (CollisionUtil.RectRectCollision(0, 0, viewWidth, viewHeight,
                    building.getX() - viewX, building.getY() - viewY,
                    building.getDiameter(), building.getModel().getHeight())) {
                visibleEntities.add(building);
            }
        }

        visibleEntities.sort(Entity.Y_COMPARATOR);
        for (int i = 0; i < visibleEntities.size(); i++) {
            if (visibleEntities.get(i).isSelected()) {
                visibleEntities.get(i).drawSelectedRing();
            }
            visibleEntities.get(i).draw();
            visibleEntities.get(i).drawHeathBar();

        }

        if (selectedRectangle != null) {
            DrawUtil.fillRect(selectedRectangle, 0x00FF0040);
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
        private static final int THRESHOLD = 2048;
        private final int start, end;
        private final long[] xArr, yArr, rArr, nArr;

        CollisionDetectionTask(int start, int end, long[] xArr, long[] yArr, long[] rArr, long [] nArr) {
            this.start = start;
            this.end = end;
            this.xArr = xArr;
            this.yArr = yArr;
            this.rArr = rArr;
            this.nArr = nArr;
        }

        @Override
        protected List<CollisionResult> compute() {
            if (end - start <= THRESHOLD) {
                return detectSequentially();
            }

            int mid = (start + end) / 2;
            CollisionDetectionTask left = new CollisionDetectionTask(start, mid, xArr, yArr, rArr, nArr);
            CollisionDetectionTask right = new CollisionDetectionTask(mid, end, xArr, yArr, rArr, nArr);
            left.fork();

            List<CollisionResult> results = right.compute();
            results.addAll(left.join());
            return results;
        }

        private List<CollisionResult> detectSequentially() {
            List<CollisionResult> results = new ArrayList<>();
            for (int i = start; i < end; i++) {
                long x1 = xArr[i];
                long y1 = yArr[i];
                long r1 = rArr[i];

                long gx = x1 >> SHIFT;
                long gy = y1 >> SHIFT;

                for (long nx = gx - 1; nx <= gx + 1; nx++) {
                    for (long ny = gy - 1; ny <= gy + 1; ny++) {
                        long key = (nx << 32) | (ny & 0xFFFFFFFFL);
                        int currentIndex = cellHeads.get(key);

                        while (currentIndex != -1) {
                            if (i < currentIndex) {
                                long rSum = r1 + rArr[currentIndex];
                                long dx = xArr[currentIndex] - x1;
                                long dy = yArr[currentIndex] - y1;

                                if (StrictMath.abs(dx) < rSum && StrictMath.abs(dy) < rSum) {

                                    long distSq = dx * dx + dy * dy;
                                    long rSumSq = rSum * rSum;

                                    if (distSq < rSumSq && distSq > 0) {
                                        long distance = NumUtil.sqrtFast(distSq);
                                        if (distance == 0) distance = 1;

                                        long overlap = rSum - distance;
                                        long pushAmount = (long) (overlap * 0.75);

                                        long moveX = (dx * pushAmount) / distance;
                                        long moveY = (dy * pushAmount) / distance;

                                        results.add(new CollisionResult(units.get(i), units.get(currentIndex), moveX, moveY));
                                    }
                                }
                            }
                            currentIndex = (int) nArr[currentIndex];
                        }
                    }
                }
            }
            return results;
        }
    }
}
