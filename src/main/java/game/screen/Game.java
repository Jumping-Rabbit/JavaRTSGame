package game.screen;

import game.GameViewport;
import game.Replay;
import game.entity.Command;
import game.entity.Entity;
import game.entity.building.Building;
import game.entity.players;
import game.entity.unit.Unit;
import game.entity.unit.vanguard.Marine;
import game.entity.unit.vanguard.Marine;
import inputHandler.*;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
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

public class Game {
    private static final int SHIFT = 22;
    private static final int THREAD_COUNT = Math.max(1, Runtime.getRuntime().availableProcessors() - 2);
    private static final ForkJoinPool pool = new ForkJoinPool(THREAD_COUNT);
    private final ObjectArrayList<Entity> visibleEntities = new ObjectArrayList<>(1000);
    private final Long2IntMap cellHeads1;
    private final Long2IntMap cellHeads2;
    File map;
    GameState gameState = GameState.RUNNING;
    GameViewport gameViewport;//get x and y from map
    TileManager tileManager;
    ObjectArrayList<Entity> entities;
    ArrayList<Entity> selectedEntities;
    Rectangle2D selectedRectangle = null;
    long tickNum;
    long mapWidth;
    long mapHeight;
    long[] hotX;
    long[] hotY;
    long[] hotRad;
    long[] hotNIC;
    volatile boolean isSnapshot1 = true;
    boolean exit = false;

    public Game(File map) {
        var objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.readTree(new File(map.getPath() + "/map.json"));
        JsonNode playersData = root.path("playerData");
        this(map, (int) StrictMath.floor(StrictMath.random() * playersData.size()));

    }

    public Game(File map, int playerNum) {
        exit = false;
        entities = new ObjectArrayList<>(22000);
        for (int i = 0; i < 20000; i++) {
            entities.add(new Marine((int) (StrictMath.random() * 7680), (int) (StrictMath.random() * 4320), players.BLUE));
        }
        selectedEntities = new ArrayList<>();
        selectedEntities.addAll(entities);
        for (Entity entity : entities){
            entity.setIsSelected(true);
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
        cellHeads1 = new Long2IntOpenHashMap();
        cellHeads1.defaultReturnValue(-1);
        cellHeads2 = new Long2IntOpenHashMap();
        cellHeads2.defaultReturnValue(-1);
//        entitiesById = new Int2ObjectOpenHashMap<>();
        System.out.println("physics threads: " + THREAD_COUNT);
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

    public boolean isExit(){
        return exit;
    }


    private void setSpatialGrid() {
        if (isSnapshot1){
            cellHeads2.clear();
            for (int i = 0; i < entities.size(); i++) {
                Entity entity = entities.get(i);
                long key = (entity.getX() >> 22) << 32 | (entity.getY() >> 22) & 0xFFFFFFFFL;
                entity.nextInCell2 = cellHeads2.getOrDefault(key, -1);
                cellHeads2.put(key, i);
            }
        } else {
            cellHeads1.clear();
            for (int i = 0; i < entities.size(); i++) {
                Entity entity = entities.get(i);
                long key = (entity.getX() >> 22) << 32 | (entity.getY() >> 22) & 0xFFFFFFFFL;
                entity.nextInCell1 = cellHeads1.getOrDefault(key, -1);
                cellHeads1.put(key, i);
            }
        }

    }

    private void applyPush(Entity entity, long x, long y) {
//        u.changeX(dx);
//        u.changeY(dy);
        entity.changeX(x);
        entity.changeY(y);
//        System.out.println(x);
    }

    private void calculatePhysics() {
        for (int iter = 0; iter < 1; iter++) {//increase if physics acts up
            long t1 = System.nanoTime();
            hotX = new long[entities.size()];
            hotY = new long[entities.size()];
            hotRad = new long[entities.size()];
            hotNIC = new long[entities.size()];
            for (int i = 0; i < entities.size(); i++){
                Entity entity = entities.get(i);
                hotX[i] = entity.getX();
                hotY[i] = entity.getY();
                hotRad[i] = entity.getCollisionRadius();
                if (isSnapshot1){
                    hotNIC[i] = entity.nextInCell2;
                } else {
                    hotNIC[i] = entity.nextInCell1;
                }

            }
            long t2 = System.nanoTime();
            List<CollisionResult> collisions = pool.invoke(new CollisionDetectionTask(0, entities.size(), hotX, hotY, hotRad, hotNIC));
            long t3 = System.nanoTime();

            for (CollisionResult res : collisions) {
                applyPush(res.entity1, -res.moveX, -res.moveY);
                applyPush(res.entity2, res.moveX, res.moveY);
            }
            long t4 = System.nanoTime();
            if (tickNum % 2 == 0) LoggerUtil.log(PerformanceType.PHYSICS, "set hot:", (t2-t1)/1000000d, "detect:", (t3-t2)/1000000d, "resolve:", (t4-t3)/1000000d, "collisions:", collisions.size());
//            System.out.println("spatial grid: " + (t2-t1)/1000000d + "detect: " + (t3-t2)/1000000d + "resolve: " + (t4-t3)/1000000d + "collisions amount: " + collisions.size());
        }
        for (Entity entity : entities) {
            if (entity.getX() >= mapWidth) entity.setX(mapWidth-1);
            else if (entity.getX() <= 0) entity.setX(1);
            if (entity.getY() >= mapHeight) entity.setY(mapHeight-1);
            else if (entity.getY() <= 0) entity.setY(1);
        }
    }

    public void updateOnFrame() {
        long t1 = System.nanoTime();
        Replay.addTick(InputHandler.getInputs(), tickNum);
        long t2 = System.nanoTime();
        tickNum++;
        for (Input input : InputHandler.getInputs()) {
            switch (input.getInputType()) {
                case LEFT_CLICK: {
                    clearSelected();
                    long clickX = DTL(input.getX() + gameViewport.getX());
                    long clickY = DTL(input.getY() + gameViewport.getY());

                    long gridX = clickX >> SHIFT;
                    long gridY = clickY >> SHIFT;
                    int clickIdx;
                    if (isSnapshot1){
                        clickIdx = cellHeads2.get((gridX << 32) | (gridY & 0xFFFFFFFFL));
                    } else {
                        clickIdx = cellHeads1.get((gridX << 32) | (gridY & 0xFFFFFFFFL));
                    }


                    while (clickIdx != -1) {
                        Entity entity = entities.get(clickIdx);
                        double x = NumUtil.interpolate(entity.getLastX(), entity.getX(), DrawUtil.getFactor());
                        double y = NumUtil.interpolate(entity.getLastY(), entity.getY(), DrawUtil.getFactor());

                        if (CollisionUtil.PointCircleCollision(clickX, clickY, x, y, entity.getCollisionRadius())) {
                            addSelected(entity);
                        }
                        if (isSnapshot1){
                            clickIdx = entity.nextInCell2;
                        } else {
                            clickIdx = entity.nextInCell1;
                        }

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
                            int boxIdx;
                            if (isSnapshot1){
                                boxIdx = cellHeads2.get(((long) gx << 32) | (gy & 0xFFFFFFFFL));
                            } else {
                                boxIdx = cellHeads1.get(((long) gx << 32) | (gy & 0xFFFFFFFFL));
                            }

                            while (boxIdx != -1) {
                                Entity entity = entities.get(boxIdx);
                                double x = NumUtil.interpolate(entity.getX(), entity.getLastX(), DrawUtil.getFactor());
                                double y = NumUtil.interpolate(entity.getY(), entity.getLastY(), DrawUtil.getFactor());

                                if (CollisionUtil.RectCircleCollision(x, y, entity.getCollisionRadius(), rectX, rectY, rectWidth, rectHeight)) {
                                    addSelected(entity);
                                }
                                if (isSnapshot1){
                                    boxIdx = entity.nextInCell2;
                                } else {
                                    boxIdx = entity.nextInCell1;
                                }

                            }
                        }
                    }
                    break;
                }
                case RIGHT_CLICK:
                    for (Entity entity : selectedEntities) {
                        entity.clearCommands();//make shift button work
                        entity.addCommand(new Command(InputType.RIGHT_CLICK, DTL(input.getX()) + DTL(gameViewport.getX()), DTL(input.getY()) + DTL(gameViewport.getY())));
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
        long t3 = System.nanoTime();

        entities.parallelStream().forEach(Entity::updateOnFrame);
        long t4 = System.nanoTime();

//        for (Building building : buildings) {
//            building.updateOnFrame();
//        }
//        for (Unit entity : entities) {
//            entity.updateOnFrame();
//        }
        setSpatialGrid();

        long t5 = System.nanoTime();

        calculatePhysics();
        if (isSnapshot1){
            entities.parallelStream().forEach(Entity::setSnapshot2);
            isSnapshot1 = false;
        } else {
            entities.parallelStream().forEach(Entity::setSnapshot1);
            isSnapshot1 = true;
        }

        if (!InputHandler.MouseDown()) {
            selectedRectangle = null;
        }
        long t6 = System.nanoTime();
        if (tickNum % 2 == 0) LoggerUtil.log(PerformanceType.TICK, "replay:", (t2-t1)/1000000d,"input:", (t3-t2)/1000000d, "update:", (t4-t3)/1000000d, "spatial grid:", (t5-t4)/1000000d, "physics:" + (t6-t5)/1000000d);
//        System.out.println("input: " + (t2-t1)/1000000d + "update: " + (t3-t2)/1000000d + "ms  physics: " + (t4-t3)/1000000d);
    }

    public void draw() {
        DrawUtil.setGameViewport(gameViewport);

        for (Entity entity : entities){
            entity.drawTarget();
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
                int currentIndex;
                if (isSnapshot1){
                    currentIndex = cellHeads1.get(key);
                    while (currentIndex != -1) {
                        Entity entity = entities.get(currentIndex);
                        visibleEntities.add(entity);

                        currentIndex = entity.nextInCell1;
                    }
                } else {
                    currentIndex = cellHeads2.get(key);
                    while (currentIndex != -1) {
                        Entity entity = entities.get(currentIndex);
                        visibleEntities.add(entity);

                        currentIndex = entity.nextInCell2;
                    }
                }


            }
        }

        visibleEntities.sort(Entity.Y_COMPARATOR);
        for (Entity visibleEntity : visibleEntities) {
            if (visibleEntity.isSelected()) {
                visibleEntity.drawSelectedRing(isSnapshot1);
            }
            visibleEntity.draw(isSnapshot1);
            visibleEntity.drawHeathBar(isSnapshot1);
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
        Entity entity1, entity2;
        long moveX, moveY;

        CollisionResult(Entity entity1, Entity entity2, long moveX, long moveY) {
            this.entity1 = entity1;
            this.entity2 = entity2;
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
                        int currentIndex;
                        if (isSnapshot1){
                            currentIndex = cellHeads2.get(key);
                        } else {
                            currentIndex = cellHeads1.get(key);
                        }


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

                                        results.add(new CollisionResult(entities.get(i), entities.get(currentIndex), moveX, moveY));
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
