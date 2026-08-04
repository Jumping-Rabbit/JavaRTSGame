package com.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.game.entity.Init;
import com.game.inputHandler.Actions;
import com.game.inputHandler.Input;
import com.game.inputHandler.InputHandler;
import com.game.gameWindow.*;
import com.game.utils.*;
import org.reflections.Reflections;
import oshi.ffm.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.Sensors;

import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;


public class GameCore extends Game {

    //    float targetFPS = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode().getRefreshRate(); // 0 or negative number means unlimited
    float targetFPS = 0;
    volatile AtomicLong lastTickTime = new AtomicLong(System.nanoTime());
    private GameStatus gameStatus = GameStatus.START_LOADING;

//    private FPSLogger fpsLogger;

    private InputHandler inputHandler;


    private Thread logicThread;


    private GameScreen gameScreen;
    private GameWindow tickScreen;
    private LoadingScreen loadingScreen;

    private SettingsManager settingsManager;

    private TitleScreen titleScreen;
    private MapEditorScreen mapEditorScreen;
    private SettingsScreen settingsScreen;
    private PerformanceStorage performanceStorage;
    private HardwarePerformance hardwarePerformance;


    public GameCore() {
//        System.out.println(NumUtil.sqrtFastScaled(90000));
//        for (long i = -NumUtil.FTL(100); i < NumUtil.FTL(100); i+= 10000){
//            for (long j = -NumUtil.FTL(100); j < NumUtil.FTL(100); j+= 10000){
//                System.out.println(StrictMath.toDegrees(StrictMath.atan2(NumUtil.FTL(i), NumUtil.FTL(j))) + ":" + NumUtil.LTF(NumUtil.atan2(NumUtil.FTL(i), NumUtil.FTL(j))));
//            }
//        }
//        long startTime = System.currentTimeMillis();
//        long total = 0;
//        for (int x = 0; x < 10000;x++){
//            for (int y = 0; y < 10000;y++) {
//                total += NumUtil.atan2(NumUtil.FTL(x), NumUtil.FTL(y));
//            }
//        }
//        System.out.println(System.currentTimeMillis()-startTime + ":" + total);
//        total = 0;
//        startTime = System.currentTimeMillis();
//        for (int x = 0; x < 10000;x++){
//            for (int y = 0; y < 10000;y++) {
//                total += StrictMath.toDegrees(StrictMath.atan2(NumUtil.FTL(x), NumUtil.FTL(y)));
//            }
//        }
//        System.out.println(System.currentTimeMillis()-startTime + ":" + total);
//        System.out.println("atan: " + NumUtil.atan2(10000000, 10000000));

//
//
//        startTime = System.currentTimeMillis();
//        total = 0;
//        for (int i = 0; i < 100000000;i++){
//            total += NumUtil.sin(i);
//        }
//        System.out.println("sin" + (System.currentTimeMillis()-startTime) + ":" + total);
//        total = 0;
//        startTime = System.currentTimeMillis();
//        for (int i = 0; i < 100000000;i++){
//            total += NumUtil.FTL(StrictMath.sin(StrictMath.toRadians(i)));
//        }
//        System.out.println(System.currentTimeMillis()-startTime + ":" + total);
////        System.out.println(NumUtil.sin(90));
////        System.out.println(NumUtil.FTL(StrictMath.sin(StrictMath.toRadians(90))));
//
//
//        long startTime = System.currentTimeMillis();
//        long total = 0;
//        for (int i = 0; i < NumUtil.FTL(1000);i++){
//            total += NumUtil.sqrt(i);
//        }
//        System.out.println((System.currentTimeMillis()-startTime) + ":" + total);
//        startTime = System.currentTimeMillis();
//        total = 0;
//        for (int i = 0; i < NumUtil.FTL(1000);i++){
//            total += NumUtil.sqrtCached(i);
//        }
//        System.out.println(System.currentTimeMillis()-startTime + ":" + total);
//        startTime = System.currentTimeMillis();
//        total = 0;
//        for (int i = 0; i < NumUtil.FTL(1000);i++){
//            total += NumUtil.sqrtCached2(i);
//        }
//        System.out.println(System.currentTimeMillis()-startTime + ":" + total);
//        System.out.println(NumUtil.sqrtFast(NumUtil.FTL(200)));
//
//        total = 0;
//        startTime = System.currentTimeMillis();
//        for (int i = 0; i < 100000000;i++){
//            total += NumUtil.FTL(i);
//        }
//        System.out.println(System.currentTimeMillis()-startTime + ":" + total);
//        for (long i  = -NumUtil.FTL(100); i < NumUtil.FTL(100); i+= 10000){
//            for (long j  = -NumUtil.FTL(100); j < NumUtil.FTL(100); j+= 10000){
//                System.out.println(NumUtil.LTF(NumUtil.FTL((StrictMath.toDegrees(StrictMath.atan2(NumUtil.FTL(i), NumUtil.FTL(j)))))) + ":" + NumUtil.LTF(NumUtil.atan2(i, j)));
//            }
//        }
        settingsManager = new SettingsManager();
        titleScreen = new TitleScreen();
        performanceStorage = new PerformanceStorage();
        hardwarePerformance = new HardwarePerformance();
        inputHandler = new InputHandler();
//        fpsLogger = new FPSLogger(2000);
    }

    private synchronized GameStatus getGameStatus() {
        return gameStatus;
    }

    private synchronized void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
        switch (gameStatus){
            case TITLESCREEN -> setScreen(titleScreen);
            case SETTINGS -> setScreen(settingsScreen);
            case GAME -> setScreen(gameScreen);
            case MAP_EDITOR -> setScreen(mapEditorScreen);
            default -> setScreen(loadingScreen);
        }
    }

    private synchronized GameScreen getGame() {
        return gameScreen;
    }

    private synchronized void setGame(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
    }

    private synchronized TitleScreen getTitleScreen() {
        return titleScreen;
    }

    private synchronized void setTitleScreen(TitleScreen titleScreen) {
        this.titleScreen = titleScreen;
    }

    private synchronized MapEditorScreen getMapEditor() {
        return mapEditorScreen;
    }

    private synchronized void setMapEditor(MapEditorScreen mapEditorScreen) {
        this.mapEditorScreen = mapEditorScreen;
    }

    private synchronized SettingsScreen getSettings() {
        return settingsScreen;
    }

    private synchronized void setSettings(SettingsScreen settingsScreen) {
        this.settingsScreen = settingsScreen;
    }

    public void startGameThread() {

    }

    private String formatString(float num, String format) {
        DecimalFormat df = new DecimalFormat(format);
        return Objects.toString(df.format(num));
    }

    public void updateOnFrame() {
        long startTime = System.nanoTime();
        InputHandler.tick();
        switch (gameStatus) {
            case GAME_LOADING:
                if (gameScreen.isLoadingFinished()) {
                    setGameStatus(GameStatus.GAME);
                }
                break;
            case GAME:
                gameScreen.updateOnFrame();
                if (gameScreen.isExit()) {
                    setTitleScreen(new TitleScreen());
                    setGameStatus(GameStatus.TITLESCREEN);
                    LoggerUtil.log("open title screen");
                }
                break;
            case TITLESCREEN:
                tickScreen = getTitleScreen().copy();
                tickScreen.updateOnFrame();
                if (tickScreen.isExit()) {
                    switch (((TitleScreen) tickScreen).getSelectedButton()) {
                        case MAP_EDITOR:
                            setMapEditor(new MapEditorScreen());
                            setGameStatus(GameStatus.MAP_EDITOR);
                            LoggerUtil.log("open mapEditor");
                            break;
                        case SETTINGS:
                            setSettings(settingsScreen = new SettingsScreen(settingsManager));
                            setGameStatus(GameStatus.SETTINGS);
                            LoggerUtil.log("open settings");
                            break;
                        case CUSTOM:
                            if (titleScreen.getSelectedFile() == null) break;
                            setGame(new GameScreen(getTitleScreen().getSelectedFile()));
                            setGameStatus(GameStatus.GAME_LOADING);
                            LoggerUtil.log("open custom");
                            break;
                    }
                    getTitleScreen().resetSelections();
                } else {
                    setTitleScreen((TitleScreen) tickScreen);
                }
                break;
            case MAP_EDITOR:
                tickScreen = getMapEditor().copy();
                tickScreen.updateOnFrame();
                setMapEditor((MapEditorScreen) tickScreen);
                if (getMapEditor().isExit()) {
                    setGameStatus(GameStatus.TITLESCREEN);
                    LoggerUtil.log("open title screen");
                }
                break;
            case SETTINGS:
                tickScreen = getSettings().copy();
                tickScreen.updateOnFrame();
                setSettings((SettingsScreen) tickScreen);
                if (getSettings().isExit()) {
                    setGameStatus(GameStatus.TITLESCREEN);
                    LoggerUtil.log("open title screen");
                }
                break;
        }
        if (getGameStatus() != GameStatus.START_LOADING) {
            for (Input input : InputHandler.getInputs()) {
                if (input.getAction() == Actions.CHANGE_BGM) {
                    SoundManager.newBGM();
                }
            }
        }
        lastTickTime.set(startTime);
    }




    enum GameStatus {
        TITLESCREEN,
        SETTINGS,
        GAME,
        MAP_EDITOR,
        START_LOADING,
        GAME_LOADING
    }


    @Override
    public void create() {
        DrawUtil.create();
        Reflections reflections = new Reflections("game.entity");
        Set<Class<?>> childClasses = reflections.getTypesAnnotatedWith(Init.class);
        logicThread = new Thread(new logicThread(performanceStorage));
        loadingScreen = new LoadingScreen(6 + (Models.getUnitAmount() * 17) + Models.getBuildingAmount() * 2 + childClasses.size());

        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
            LoggerUtil.log(e);
        }
        Thread loader = new Thread(() -> {
            long initTime = System.nanoTime();
            loadingScreen.addText("init Logger");
            long startTime = System.nanoTime();
            LoggerUtil.init();
            System.out.println("logger time: " + (System.nanoTime() - startTime) / 1000000000d);
            loadingScreen.increment();

            for (Class<?> clazz : childClasses) {
                try {
                    loadingScreen.addText("init " + clazz.getSimpleName());
                    startTime = System.nanoTime();
                    Method staticMethod = clazz.getDeclaredMethod("init");
                    staticMethod.invoke(null);
                    System.out.println(clazz.getSimpleName() + " time: " + (System.nanoTime() - startTime) / 1000000000d);
                    loadingScreen.increment();
                } catch (Exception e) {
                    LoggerUtil.log(e);
                }
            }

            loadingScreen.addText("init Sounds");
            startTime = System.nanoTime();
            SoundManager.init();
            System.out.println("sound time: " + (System.nanoTime() - startTime) / 1000000000d);
            loadingScreen.increment();
            loadingScreen.addText("init NumUtil");
            startTime = System.nanoTime();
            NumUtil.init();
            System.out.println("numUtil time: " + (System.nanoTime() - startTime) / 1000000000d);
            loadingScreen.increment();
            loadingScreen.addText("init keyHandler");
            startTime = System.nanoTime();
            InputHandler.KeyHandler.init();
            Gdx.input.setInputProcessor(inputHandler);
            System.out.println("keyHandler time: " + (System.nanoTime() - startTime) / 1000000000d);
            loadingScreen.increment();
            loadingScreen.addText("init actions");
            startTime = System.nanoTime();
            Actions.init();
            System.out.println("actions time: " + (System.nanoTime() - startTime) / 1000000000d);
            loadingScreen.increment();
            loadingScreen.addText("init draw util");
            startTime = System.nanoTime();
            DrawUtil.init(loadingScreen);
            System.out.println("DrawUtil time: " + (System.nanoTime() - startTime) / 1000000000d);
            loadingScreen.increment();
            loadingScreen.addText("init settings");
            startTime = System.nanoTime();
            SettingsManager.getSettings();
            System.out.println("Settings time: " + (System.nanoTime() - startTime) / 1000000000d);
            loadingScreen.increment();
            float initTotalTime = (System.nanoTime() - initTime) / 1000000000f;
            loadingScreen.addText("done   time: " + initTotalTime);
            LoggerUtil.log("startup time: " + initTotalTime);
            SystemInfo si = new SystemInfo();
            LoggerUtil.log(LogType.EVENT, "os:", si.getOperatingSystem(), "cpu:", si.getHardware().getProcessor().getProcessorIdentifier().getName(), "gpu:", si.getHardware().getGraphicsCards().getFirst().getName(), "ram:", si.getHardware().getMemory().getTotal());
            System.out.println("start   time: " + initTotalTime);
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                LoggerUtil.log(e);
            }
            setGameStatus(GameStatus.TITLESCREEN);
            SoundManager.startBGM();
        });
        loader.start();
//        try {
//            loader.join();
//        } catch (InterruptedException e) {
//
//            LoggerUtil.log(e);
//
//        }

        logicThread.start();
    }

    @Override
    public void resize(int width, int height) {
        DrawUtil.updateViewport(width, height);
    }

    @Override
    public void render() {
        long startTime = System.nanoTime();
        DrawUtil.startRender2D();
        DrawUtil.setFactor((System.nanoTime() - lastTickTime.get()) / 50000000f);
//        System.out.println(DrawUtil.getFactor());
//        DrawUtil.setGC(gc);
        DrawUtil.clearCanvas();
        DrawUtil.fillBackground();
//        System.out.println("h");

        switch (getGameStatus()) {
            case GAME, GAME_LOADING:
                gameScreen.draw();
                break;
            case TITLESCREEN:
                getTitleScreen().draw();
                break;
            case MAP_EDITOR:
                getMapEditor().draw();
                break;
            case SETTINGS:
                getSettings().draw();
                break;
            case START_LOADING:
                loadingScreen.draw();
                break;
        }
        DrawUtil.fillOffsetEdge();

        DrawUtil.fillText("fps:" + formatString(Gdx.graphics.getFramesPerSecond(), "00000.00") + " tps:" + formatString(performanceStorage.getTPS(), "00"), 5, 1070, Fonts.DEFAULT, 10, StringAlignment.TOP_LEFT, 0xFFFFFFFF);
        DrawUtil.fillText("fps1%:" + formatString(performanceStorage.getPeakDT(), "0000.00") + " fps0.1%:" + formatString(performanceStorage.getPeakPeakDT(), "0000.00"), 5, 1060, Fonts.DEFAULT, 10, StringAlignment.TOP_LEFT, 0xFFFFFFFF);
        DrawUtil.fillText("ttu:" + formatString(performanceStorage.getTickTimeUsed(), "0000.00") + "%" + " ttu1%:" + formatString(performanceStorage.getTickTimeUsedLow(), "0000.00") + "%" + " late frames:" + performanceStorage.getLateFrames(), 5, 1050, Fonts.DEFAULT, 10, StringAlignment.TOP_LEFT, 0xFFFFFFFF);

        DrawUtil.fillText(hardwarePerformance.getCpuStats(), 1915, 1070, Fonts.DEFAULT, 10, StringAlignment.TOP_RIGHT, 0xFFFFFFFF);
        DrawUtil.fillText(hardwarePerformance.getRamStats(), 1915, 1060, Fonts.DEFAULT, 10, StringAlignment.TOP_RIGHT, 0xFFFFFFFF);
        DrawUtil.fillText("BGM: " + SoundManager.getBgmName(), 1915, 1050, Fonts.DEFAULT, 10, StringAlignment.TOP_RIGHT, 0xFFFFFFFF);
        DrawUtil.stopRender();
//        fpsLogger.log();
        performanceStorage.addDrawTimeUsed(startTime);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        LoggerUtil.log("close program");
        LoggerUtil.flush();
        Replay.flush();
        Gdx.app.exit();
        System.exit(0);
    }


    class logicThread implements Runnable {
        PerformanceStorage performanceStorage;

        public logicThread(PerformanceStorage performanceStorage) {
            this.performanceStorage = performanceStorage;
        }

        @Override
        public void run() {
            long targetFrameInterval = 50_000_000L; // 20 TPS
            long targetTime = System.nanoTime() + targetFrameInterval;
            while (logicThread != null) {
                long currentTime = System.nanoTime();
                if (currentTime >= targetTime) {
                    long tickStartTarget = targetTime;
                    targetTime += targetFrameInterval;
                    if (currentTime > targetTime + targetFrameInterval * 5) {
                        targetTime = currentTime;
                    }
                    if (currentTime > tickStartTarget + targetFrameInterval) {
                        performanceStorage.addLateFrame();
                    }

                    updateOnFrame();
                    performanceStorage.addTFrame();
                    performanceStorage.addTickTimeUsed(currentTime);
                    hardwarePerformance.tick();
                }
            }
        }
    }

}


class PerformanceStorage {
    private final AtomicInteger dFrameCount = new AtomicInteger(0);
    private final AtomicInteger tFrameCount = new AtomicInteger(0);
    private final float[] ttuHistory = new float[100];
    private final float[] dtHistory = new float[1000];
    private final AtomicInteger lateFrames = new AtomicInteger(0);
    private float currentFPS = 0;
    private float currentTPS = 0;
    private long lastFPSUpdate = System.nanoTime();
    private long lastTPSUpdate = System.nanoTime();
    private volatile float currentTTU = 0;
    private volatile float peakTTU = 0;
    private int ttuIndex = 0;
    private int ttuCount = 0;
    private int dtIndex = 0;
    private int dtCount = 0;
    private volatile float peakDT = 0;
    private volatile float peakPeakDT = 0;
    private int loggerCooldown = 20;

    public void addTFrame() {
        tFrameCount.incrementAndGet();
        long now = System.nanoTime();
        long delta = now - lastTPSUpdate;
        if (delta >= 500000000L) {
            currentTPS = (tFrameCount.getAndSet(0) / (delta / 500000000f)) * 2;
            lastTPSUpdate = now;
        }
        loggerCooldown--;
        if (loggerCooldown <= 0) {
            LoggerUtil.log(LogType.PERFORMANCE, "fps:", getFPS(), "dt1%:", getPeakDT(), "dt0.1%:", getPeakPeakDT(), "tps:", getTPS(), "ttu:", getTickTimeUsed(), "ttu1%:", getTickTimeUsedLow(), "late frames:", getLateFrames());
            loggerCooldown = 40;
        }
    }

    public float getFPS() {
        return currentFPS;
    }

    public float getTPS() {
        return currentTPS;
    }

    public void addTickTimeUsed(long startNano) {
        long duration = System.nanoTime() - startNano;
        float percent = (duration / 50000000f) * 100f;
        this.currentTTU = percent;
        synchronized (ttuHistory) {
            ttuHistory[ttuIndex] = percent;
            ttuIndex = (ttuIndex + 1) % 100;
            if (ttuCount < 100) ttuCount++;
            float max = 0;
            for (int i = 0; i < ttuCount; i++) {
                if (ttuHistory[i] > max) max = ttuHistory[i];
            }
            this.peakTTU = max;
        }

        synchronized (dtHistory) {
            if (dtCount > 0) {
                float[] sortedCopy = new float[dtCount];
                System.arraycopy(dtHistory, 0, sortedCopy, 0, dtCount);
                java.util.Arrays.sort(sortedCopy);

                int limit1 = Math.max(1, (int) Math.round(dtCount / 100.0));
                float sum1 = 0;
                for (int i = dtCount - 1; i >= dtCount - limit1; i--) {
                    sum1 += sortedCopy[i];
                }
                this.peakDT = sum1 / limit1;

                int limit01 = Math.max(1, (int) Math.round(dtCount / 1000.0));
                float sum01 = 0;
                for (int i = dtCount - 1; i >= dtCount - limit01; i--) {
                    sum01 += sortedCopy[i];
                }
                this.peakPeakDT = sum01 / limit01;
            }
        }
    }

    public float getTickTimeUsed() {
        return currentTTU;
    }

    public float getTickTimeUsedLow() {
        return peakTTU;
    }

    public void addDrawTimeUsed(long startNano) {
        float dt = (float) (System.nanoTime() - startNano);
        synchronized (dtHistory) {
            dtHistory[dtIndex] = dt;
            dtIndex = (dtIndex + 1) % 1000;
            if (dtCount < 1000) dtCount++;
        }
    }

    public float getPeakDT() {
        return 1_000_000_000f / peakDT;
    }

    public float getPeakPeakDT() {
        return 1_000_000_000f / peakPeakDT;
    }

    public void addLateFrame() {
        lateFrames.incrementAndGet();
    }

    public int getLateFrames() {
        return lateFrames.get();
    }
}

class HardwarePerformance {
    SystemInfo si = new SystemInfo();
    HardwareAbstractionLayer hal = si.getHardware();
    Sensors sensors = hal.getSensors();
    CentralProcessor cpu = hal.getProcessor();


    private float cpuLoad;
    private float cpuTemp;
    private String cpuStats = "";
    private String ramStats = "";

    public synchronized String getCpuStats() {
        return cpuStats;
    }

    public synchronized String getRamStats() {
        return ramStats;
    }

    private String cpuName;
    private long[] prevTicks = cpu.getSystemCpuLoadTicks();

    private float ramTotal;
    private float ramUnused;
    private float ramUsed;

    public HardwarePerformance() {
        cpuName = cpu.getProcessorIdentifier().getName();
    }

    private long lastStatsUpdate = System.nanoTime();

    public synchronized void tick() {
        long now = System.nanoTime();
        long delta = now - lastStatsUpdate;

        if (delta >= 4000000000L) {
            ramTotal = (float) (hal.getMemory().getTotal() / 1073741824d);
            ramUnused = (float) (hal.getMemory().getAvailable() / 1073741824d);
            ramUsed = (float) (si.getOperatingSystem().getProcess(si.getOperatingSystem().getProcessId()).getResidentMemory() / 1073741824d);
            cpuLoad = (float) cpu.getSystemCpuLoadBetweenTicks(prevTicks);
            prevTicks = cpu.getSystemCpuLoadTicks();
            cpuTemp = (float) sensors.getCpuTemperature();
            lastStatsUpdate = now;
            LoggerUtil.log(PerformanceType.HARDWARE, "ram total:", ramTotal, "ram unused:", ramUnused, "ram used:", ramUsed, "cpu load:", cpuLoad, "cpuTemp:", cpuTemp);
            cpuStats = "cpu load:" + String.format("%.2f", cpuLoad * 100) + " cpuTemp:" + String.format("%.2f", cpuTemp);
            ramStats = "used:" + String.format("%.2f", ramUsed) + " ram unused:" + String.format("%.2f", ramUnused);
        }

    }



}