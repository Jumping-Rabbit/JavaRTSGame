package com.game.gameWindow;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.math.Rectangle;
import com.game.Fonts;
import com.game.Replay;
import com.game.entity.Command;
import com.game.entity.Entity;
import com.game.entity.PlayerColor;
import com.game.entity.Tags;
import com.game.entity.unit.Unit;
import com.game.inputHandler.Input;
import com.game.inputHandler.InputHandler;
import com.game.inputHandler.InputType;
import com.game.tile.TileManager;
import com.game.utils.*;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;


public class GameScreen implements Screen{
    private static final int SHIFT = 22;
    private static final int THREAD_COUNT = Math.max(1, Runtime.getRuntime().availableProcessors() - 2);
    private static final ForkJoinPool pool = new ForkJoinPool(THREAD_COUNT);
    private final ObjectArrayList<Entity> visibleEntities = new ObjectArrayList<>(12000);
    private final Long2IntMap cellHeads1;
    private final Long2IntMap cellHeads2;
    File map;
    GameState gameState = GameState.RUNNING;
    TileManager tileManager;
    ObjectArrayList<Entity> entities;
    ObjectArrayList<Entity> selectedEntities;
    Rectangle selectedRectangle = null;
    long tickNum;
    long mapWidth;
    long mapHeight;
    long[] hotX;
    long[] hotY;
    long[] hotRad;
    long[] hotNIC;
    volatile boolean isSnapshot1 = true;
    boolean exit = false;
    boolean isLoadingFinished = false;
    LoadingScreen loadingScreen;
    PlayerColor[] playerColors;
    Int2ObjectOpenHashMap<ObjectArrayList<Entity>> controlGroups = new Int2ObjectOpenHashMap<>();

    public GameScreen(File map) {
        var objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.readTree(new File(map.getPath() + "/map.json"));
        JsonNode playersData = root.path("playerData");
        this(map, 0);//(int) StrictMath.floor(StrictMath.random() * playersData.size()));

    }

    public GameScreen(File map, int playerNum) {
        for (int i = 1; i <= 20; i++) {
            controlGroups.put(i, new ObjectArrayList<>());
        }
        exit = false;
        entities = new ObjectArrayList<>(12000);
//        for (int i = 0; i < 2500; i++) {
//            entities.add(new VanguardMarine((int) (StrictMath.random() * 7680), (int) (StrictMath.random() * 4320), PlayerColor.BLUE));
//        }
//        for (int i = 0; i < 2500; i++) {
//            entities.add(new VanguardMUV((int) (StrictMath.random() * 7680), (int) (StrictMath.random() * 4320), PlayerColor.RED));
//        }
//        for (int i = 0; i < 100; i++) {
//            entities.add(new VanguardBarracks((int) (StrictMath.random() * 7680), (int) (StrictMath.random() * 4320), PlayerColor.BLUE));
//        }
//        for (int i = 0; i < 100; i++) {
//            entities.add(new VanguardCommandCenter((int) (StrictMath.random() * 7680), (int) (StrictMath.random() * 4320), PlayerColor.RED));
//        }
        selectedEntities = new ObjectArrayList<>();
        selectedEntities.addAll(entities);
        for (Entity entity : entities) {
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
//        DrawUtil.setGameViewport(gameViewport);
        tickNum = 0;
        Replay.newReplay(map);
        cellHeads1 = new Long2IntOpenHashMap();
        cellHeads1.defaultReturnValue(-1);
        cellHeads2 = new Long2IntOpenHashMap();
        cellHeads2.defaultReturnValue(-1);
//        entitiesById = new Int2ObjectOpenHashMap<>();
        int playerColorsNum = playersData.asArray().size();
        playerColors = new PlayerColor[playerColorsNum];
        for (int i = 0; i < playerColorsNum; i++) {
            playerColors[i] = PlayerColor.fromValue(playersData.get(i).get("color").asString());
        }
        System.out.println("physics threads: " + THREAD_COUNT);
        load();


    }

    private void load() {
        loadingScreen = new LoadingScreen(Models.values().length * 5 + Models.getUnitAmount() * 32 + Models.getBuildingAmount() * 2);
        Thread loader = new Thread(() -> {
            try {
                Thread.sleep(125);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            long totalTime = System.nanoTime();
            loadingScreen.addText("loading Icons");
            long startTime = System.nanoTime();

//            DrawUtil.loadColoredImages(loadingScreen, playerColors);
            System.out.println("load model images time:" + (System.nanoTime() - startTime) / 1000000000d);
            System.out.println("total time:" + (System.nanoTime() - totalTime) / 1000000000d);
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            isLoadingFinished = true;
        });
        loader.start();

    }

    public boolean isLoadingFinished() {
        return isLoadingFinished;
    }


    private void clearSelected() {
        for (Entity entity : selectedEntities) {
            entity.setIsSelected(false);
        }
        selectedEntities.clear();
    }

    private void addSelected(Entity entity) {
        selectedEntities.add(entity);
        entity.setIsSelected(true);
    }

    public boolean isExit() {
        return exit;
    }


    private void setSpatialGrid() {
        if (isSnapshot1) {
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
        if (entity.getTags().contains(Tags.UNMOVABLE)) return;
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
            for (int i = 0; i < entities.size(); i++) {
                Entity entity = entities.get(i);
                hotX[i] = entity.getX();
                hotY[i] = entity.getY();
                hotRad[i] = entity.getRadiusScaled();
                if (isSnapshot1) {
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
            if (tickNum % 2 == 0)
                LoggerUtil.log(PerformanceType.PHYSICS, "set hot:", (t2 - t1) / 1000000d, "detect:", (t3 - t2) / 1000000d, "resolve:", (t4 - t3) / 1000000d, "collisions:", collisions.size());
//            System.out.println("spatial grid: " + (t2-t1)/1000000d + "detect: " + (t3-t2)/1000000d + "resolve: " + (t4-t3)/1000000d + "collisions amount: " + collisions.size());
        }
        for (Entity entity : entities) {
            if (entity.getX() >= mapWidth) entity.setX(mapWidth - 1);
            else if (entity.getX() <= 0) entity.setX(1);
            if (entity.getY() >= mapHeight) entity.setY(mapHeight - 1);
            else if (entity.getY() <= 0) entity.setY(1);
        }
    }

    public void updateOnFrame() {
        long t1 = System.nanoTime();
        Replay.addTick(InputHandler.getInputs(), tickNum);
        tickNum++;

        long t2 = System.nanoTime();

        boolean isControlHeld = false;
        for (Input input : InputHandler.getInputs()) {
            switch (input.getInputType()) {
                case LEFT_CLICK: {
                    clearSelected();
                    long clickX = NumUtil.FTL(input.getX());// + gameViewport.getX());
                    long clickY = NumUtil.FTL(input.getY());// + gameViewport.getY());

                    long gridX = clickX >> SHIFT;
                    long gridY = clickY >> SHIFT;
                    int clickIdx;
                    if (isSnapshot1) {
                        clickIdx = cellHeads2.get((gridX << 32) | (gridY & 0xFFFFFFFFL));
                    } else {
                        clickIdx = cellHeads1.get((gridX << 32) | (gridY & 0xFFFFFFFFL));
                    }


                    while (clickIdx != -1) {
                        Entity entity = entities.get(clickIdx);
                        float x = NumUtil.interpolate(entity.getLastX(), entity.getX(), DrawUtil.getFactor());
                        float y = NumUtil.interpolate(entity.getLastY(), entity.getY(), DrawUtil.getFactor());

                        if (CollisionUtil.PointCircleCollision(clickX, clickY, x, y, entity.getRadius())) {
                            addSelected(entity);
                        }
                        if (isSnapshot1) {
                            clickIdx = entity.nextInCell2;
                        } else {
                            clickIdx = entity.nextInCell1;
                        }

                    }
                    break;
                }

                case DRAG: {
                    clearSelected();
                    selectedRectangle = new Rectangle(
                            StrictMath.min(input.getX(), input.getStartX()),
                            StrictMath.min(input.getY(), input.getStartY()),
                            StrictMath.abs(input.getX() - input.getStartX()),
                            StrictMath.abs(input.getY() - input.getStartY())
                    );

                    float rectX = NumUtil.FTL(selectedRectangle.getX());// + gameViewport.getX());
                    float rectY = NumUtil.FTL(selectedRectangle.getY());// + gameViewport.getY());
                    float rectWidth = NumUtil.FTL(selectedRectangle.getWidth());
                    float rectHeight = NumUtil.FTL(selectedRectangle.getHeight());

                    int startX = (int) ((long) rectX >> SHIFT);
                    int endX = (int) ((long) (rectX + rectWidth) >> SHIFT);
                    int startY = (int) ((long) rectY >> SHIFT);
                    int endY = (int) ((long) (rectY + rectHeight) >> SHIFT);

                    for (int gx = startX; gx <= endX; gx++) {
                        for (int gy = startY; gy <= endY; gy++) {
                            int boxIdx;
                            if (isSnapshot1) {
                                boxIdx = cellHeads2.get(((long) gx << 32) | (gy & 0xFFFFFFFFL));
                            } else {
                                boxIdx = cellHeads1.get(((long) gx << 32) | (gy & 0xFFFFFFFFL));
                            }

                            while (boxIdx != -1) {
                                Entity entity = entities.get(boxIdx);
                                float x = NumUtil.interpolate(entity.getX(), entity.getLastX(), DrawUtil.getFactor());
                                float y = NumUtil.interpolate(entity.getY(), entity.getLastY(), DrawUtil.getFactor());

                                if (CollisionUtil.RectCircleCollision(x, y, entity.getRadius(), rectX, rectY, rectWidth, rectHeight)) {
                                    addSelected(entity);
                                }
                                if (isSnapshot1) {
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
                    long commandX = NumUtil.FTL(input.getX());// + NumUtil.FTL(gameViewport.getX());
                    long commandY = NumUtil.FTL(input.getY());// + NumUtil.FTL(gameViewport.getY());
                    for (int i = 0; i < selectedEntities.size(); i++) {
                        selectedEntities.get(i).addCommand(new Command(InputType.RIGHT_CLICK, commandX, commandY));
                    }
                    break;
                case KEYPRESS:
                    switch (input.getAction()) {
                        case BACK:
                            exit = true;
                            break;
                        case SET_CONTROL_GROUP_1:
                            controlGroups.put(1, selectedEntities);
                            break;
                        case SET_CONTROL_GROUP_2:
                            controlGroups.put(2, selectedEntities);
                            break;
                        case SET_CONTROL_GROUP_3:
                            controlGroups.put(3, selectedEntities);
                            break;
                        case SET_CONTROL_GROUP_4:
                            controlGroups.put(4, selectedEntities);
                            break;
                        case SET_CONTROL_GROUP_5:
                            controlGroups.put(5, selectedEntities);
                            break;
                        case SET_CONTROL_GROUP_6:
                            controlGroups.put(6, selectedEntities);
                            break;
                        case SET_CONTROL_GROUP_7:
                            controlGroups.put(7, selectedEntities);
                            break;
                        case SET_CONTROL_GROUP_8:
                            controlGroups.put(8, selectedEntities);
                            break;
                        case SET_CONTROL_GROUP_9:
                            controlGroups.put(9, selectedEntities);
                            break;
                        case SET_CONTROL_GROUP_10:
                            controlGroups.put(10, selectedEntities);
                            break;
                        case SET_CONTROL_GROUP_11:
                            controlGroups.put(11, selectedEntities);
                            break;
                        case SET_CONTROL_GROUP_12:
                            controlGroups.put(12, selectedEntities);
                            break;
                        case SET_CONTROL_GROUP_13:
                            controlGroups.put(13, selectedEntities);
                            break;
                        case SET_CONTROL_GROUP_14:
                            controlGroups.put(14, selectedEntities);
                            break;
                        case SET_CONTROL_GROUP_15:
                            controlGroups.put(15, selectedEntities);
                            break;
                        case SET_CONTROL_GROUP_16:
                            controlGroups.put(16, selectedEntities);
                            break;
                        case SET_CONTROL_GROUP_17:
                            controlGroups.put(17, selectedEntities);
                            break;
                        case SET_CONTROL_GROUP_18:
                            controlGroups.put(18, selectedEntities);
                            break;
                        case SET_CONTROL_GROUP_19:
                            controlGroups.put(19, selectedEntities);
                            break;
                        case SET_CONTROL_GROUP_20:
                            controlGroups.put(20, selectedEntities);
                            break;
                        case GET_CONTROL_GROUP_1:
                            selectedEntities = controlGroups.get(1);
                            break;
                        case GET_CONTROL_GROUP_2:
                            selectedEntities = controlGroups.get(2);
                            break;
                        case GET_CONTROL_GROUP_3:
                            selectedEntities = controlGroups.get(3);
                            break;
                        case GET_CONTROL_GROUP_4:
                            selectedEntities = controlGroups.get(4);
                            break;
                        case GET_CONTROL_GROUP_5:
                            selectedEntities = controlGroups.get(5);
                            break;
                        case GET_CONTROL_GROUP_6:
                            selectedEntities = controlGroups.get(6);
                            break;
                        case GET_CONTROL_GROUP_7:
                            selectedEntities = controlGroups.get(7);
                            break;
                        case GET_CONTROL_GROUP_8:
                            selectedEntities = controlGroups.get(8);
                            break;
                        case GET_CONTROL_GROUP_9:
                            selectedEntities = controlGroups.get(9);
                            break;
                        case GET_CONTROL_GROUP_10:
                            selectedEntities = controlGroups.get(10);
                            break;
                        case GET_CONTROL_GROUP_11:
                            selectedEntities = controlGroups.get(11);
                            break;
                        case GET_CONTROL_GROUP_12:
                            selectedEntities = controlGroups.get(12);
                            break;
                        case GET_CONTROL_GROUP_13:
                            selectedEntities = controlGroups.get(13);
                            break;
                        case GET_CONTROL_GROUP_14:
                            selectedEntities = controlGroups.get(14);
                            break;
                        case GET_CONTROL_GROUP_15:
                            selectedEntities = controlGroups.get(15);
                            break;
                        case GET_CONTROL_GROUP_16:
                            selectedEntities = controlGroups.get(16);
                            break;
                        case GET_CONTROL_GROUP_17:
                            selectedEntities = controlGroups.get(17);
                            break;
                        case GET_CONTROL_GROUP_18:
                            selectedEntities = controlGroups.get(18);
                            break;
                        case GET_CONTROL_GROUP_19:
                            selectedEntities = controlGroups.get(19);
                            break;
                        case GET_CONTROL_GROUP_20:
                            selectedEntities = controlGroups.get(20);
                            break;
                        case ADD_CONTROL_GROUP_1:
                            controlGroups.get(1).addAll(selectedEntities);
                            break;
                        case ADD_CONTROL_GROUP_2:
                            controlGroups.get(2).addAll(selectedEntities);
                            break;
                        case ADD_CONTROL_GROUP_3:
                            controlGroups.get(3).addAll(selectedEntities);
                            break;
                        case ADD_CONTROL_GROUP_4:
                            controlGroups.get(4).addAll(selectedEntities);
                            break;
                        case ADD_CONTROL_GROUP_5:
                            controlGroups.get(5).addAll(selectedEntities);
                            break;
                        case ADD_CONTROL_GROUP_6:
                            controlGroups.get(6).addAll(selectedEntities);
                            break;
                        case ADD_CONTROL_GROUP_7:
                            controlGroups.get(7).addAll(selectedEntities);
                            break;
                        case ADD_CONTROL_GROUP_8:
                            controlGroups.get(8).addAll(selectedEntities);
                            break;
                        case ADD_CONTROL_GROUP_9:
                            controlGroups.get(9).addAll(selectedEntities);
                            break;
                        case ADD_CONTROL_GROUP_10:
                            controlGroups.get(10).addAll(selectedEntities);
                            break;
                        case ADD_CONTROL_GROUP_11:
                            controlGroups.get(11).addAll(selectedEntities);
                            break;
                        case ADD_CONTROL_GROUP_12:
                            controlGroups.get(12).addAll(selectedEntities);
                            break;
                        case ADD_CONTROL_GROUP_13:
                            controlGroups.get(13).addAll(selectedEntities);
                            break;
                        case ADD_CONTROL_GROUP_14:
                            controlGroups.get(14).addAll(selectedEntities);
                            break;
                        case ADD_CONTROL_GROUP_15:
                            controlGroups.get(15).addAll(selectedEntities);
                            break;
                        case ADD_CONTROL_GROUP_16:
                            controlGroups.get(16).addAll(selectedEntities);
                            break;
                        case ADD_CONTROL_GROUP_17:
                            controlGroups.get(17).addAll(selectedEntities);
                            break;
                        case ADD_CONTROL_GROUP_18:
                            controlGroups.get(18).addAll(selectedEntities);
                            break;
                        case ADD_CONTROL_GROUP_19:
                            controlGroups.get(19).addAll(selectedEntities);
                            break;
                        case ADD_CONTROL_GROUP_20:
                            controlGroups.get(20).addAll(selectedEntities);
                            break;
                        default:
                            break;
                    }

                    break;
            }
        }
        long t3 = System.nanoTime();

        entities.parallelStream().forEach(Entity::updateOnFrame);
        setSpatialGrid();
        calculatePhysics();
        if (isSnapshot1) {
            entities.parallelStream().forEach(Entity::setSnapshot2);
            isSnapshot1 = false;
        } else {
            entities.parallelStream().forEach(Entity::setSnapshot1);
            isSnapshot1 = true;
        }


        if (!InputHandler.MouseDown()) {
            selectedRectangle = null;
        }
        long t4 = System.nanoTime();

//        for (Building building : buildings) {
//            building.updateOnFrame();
//        }
//        for (Unit entity : entities) {
//            entity.updateOnFrame();
//        }


        long t5 = System.nanoTime();
//        gameViewport.updateOnFrame();


        long t6 = System.nanoTime();
        if (tickNum % 2 == 0)
            LoggerUtil.log(PerformanceType.TICK, "replay:", (t2 - t1) / 1000000d, "input:", (t3 - t2) / 1000000d, "physics:", (t4 - t3) / 1000000d, "spatial grid:", (t5 - t4) / 1000000d, "update:" + (t6 - t5) / 1000000d);
//        System.out.println("input: " + (t2-t1)/1000000d + "update: " + (t3-t2)/1000000d + "ms  physics: " + (t4-t3)/1000000d);
    }

    public void drawHud() {
        DrawUtil.fillRect(0, 0, 1920, 260, 0x505050FF);
        DrawUtil.fillLine(260, 200, 1570, 200, 0x000000FF, 4);
        DrawUtil.fillLine(0, 260, 1920, 260, 0x000000FF, 4);
        DrawUtil.fillLine(260, 260, 260, 0, 0x000000FF, 4);
        DrawUtil.fillLine(1570, 260, 1570, 0, 0x000000FF, 4);
        for (int i = 0; i < 20; i += 1) {
            DrawUtil.fillLine(260 + i * 65.5f, 200, 260 + i * 65.5f, 260, 0x000000FF, 4);
            DrawUtil.fillRect(262 + i * 65.5f + 15, 190, 30, 20, 0x505050FF);
            DrawUtil.fillText(String.valueOf(i + 1), 260 + i * 65.5f + 32.75f, 200, Fonts.DEFAULT, 20, StringAlignment.CENTER_MIDDLE, 0xFFFFFFFF);
        }
        int counter = 0;
        if (selectedEntities.size() > 20 * 3) {//col * row, more than can compress
            Object2IntOpenHashMap<Models> ModelAmount = new Object2IntOpenHashMap<>();
            for (Entity entity : selectedEntities) {
                ModelAmount.put(entity.getModel(), ModelAmount.getOrDefault(entity.getModel(), 0));
            }
//            for (Models model : ModelAmount.keySet()) {
//                DrawUtil.strokeRect(265 + counter * 65, 120 - ((counter / 10) % 10) * 60, 65, 60, 0x0096FFFF, 4);
//                counter++;
//            }
            for (int i = 0; i < 28; i++) {
                DrawUtil.strokeRect(290 + (counter%14) * 90, (float)(100 - (counter / 14) * 90), 75, 75, 0x0096FFFF, 4);//float is there to supress warning
                counter++;
            }
            //compressed
        } else {

            //uncompressed
        }


    }

    public void draw() {
        DrawUtil.startRender2D();
        if (!isLoadingFinished) {
            loadingScreen.draw();
            return;
        }
        boolean isSnapshot1Draw = isSnapshot1;

        for (Entity entity : entities) {
            if (entity instanceof Unit) {
                ((Unit) entity).drawTarget();
            }
        }

        visibleEntities.clear();
        long viewX = 0;//NumUtil.FTL(gameViewport.getX());
        long viewY = 0;//NumUtil.FTL(gameViewport.getY());
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
                if (isSnapshot1Draw) {
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
                visibleEntity.drawSelectedRing(isSnapshot1Draw);
            }
        }
        DrawUtil.startRender3D();
        for (Entity visibleEntity : visibleEntities) {

//            visibleEntity.draw(isSnapshot1Draw);
        }
        DrawUtil.startRender2D();
        for (Entity visibleEntity : visibleEntities) {
            visibleEntity.drawHeathBar(isSnapshot1Draw);
        }

        if (selectedRectangle != null) {
            DrawUtil.fillRect(selectedRectangle, 0x00FF0040);
        }
        drawHud();
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

        CollisionDetectionTask(int start, int end, long[] xArr, long[] yArr, long[] rArr, long[] nArr) {
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
                long cx1 = xArr[i] + rArr[i];
                long cy1 = yArr[i] + rArr[i];
                long r1 = rArr[i];

                long gx = xArr[i] >> SHIFT;
                long gy = yArr[i] >> SHIFT;

                for (long nx = gx - 1; nx <= gx + 1; nx++) {
                    for (long ny = gy - 1; ny <= gy + 1; ny++) {
                        long key = (nx << 32) | (ny & 0xFFFFFFFFL);
                        int currentIndex;
                        if (isSnapshot1) {
                            currentIndex = cellHeads2.get(key);
                        } else {
                            currentIndex = cellHeads1.get(key);
                        }

                        while (currentIndex != -1) {
                            if (i < currentIndex) {
                                long cx2 = xArr[currentIndex] + rArr[currentIndex];
                                long cy2 = yArr[currentIndex] + rArr[currentIndex];
                                long rSum = r1 + rArr[currentIndex];
                                long dx = cx2 - cx1;
                                long dy = cy2 - cy1;

                                if (StrictMath.abs(dx) < rSum && StrictMath.abs(dy) < rSum) {
                                    long distSq = dx * dx + dy * dy;
                                    long rSumSq = rSum * rSum;

                                    if (distSq < rSumSq && distSq > 0) {
                                        long distance = NumUtil.sqrtFast(distSq);
                                        if (distance == 0) distance = 1;

                                        long overlap = rSum - distance;
                                        long pushAmount = (long) (overlap * 0.5);

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

    @Override
    public void show() {

    }

    @Override
    public void render(float v) {

    }

    @Override
    public void resize(int i, int i1) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
