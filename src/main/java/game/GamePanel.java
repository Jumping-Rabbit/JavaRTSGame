package game;

import game.screen.*;
import inputHandler.InputHandler;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.TextAlignment;
import utils.DrawUtil;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Math.*;


public class GamePanel extends Canvas {

//    double targetFPS = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode().getRefreshRate(); // 0 or negative number means unlimited
    double targetFPS = 0;
    enum GameStatus {
        TITLESCREEN,
        SETTINGS,
        GAME,
        MAP_EDITOR
    }
    private GameStatus gameStatus = GameStatus.TITLESCREEN;


    private Thread drawThread;
    private Thread logicThread;
    private Thread soundThread;

    private DrawUtil drawUtil;

    private Game game;
    private Screen tickScreen;

    private SettingsManager settingsManager;

    private TitleScreen titleScreen;
    private MapEditor mapEditor;
    private Settings settings;
    private PerformanceStorage performanceStorage;
    volatile double lastTickTime = System.currentTimeMillis();//make atomic

    private synchronized void setGameStatus(GameStatus gameStatus){
        this.gameStatus = gameStatus;
    }
    private synchronized GameStatus getGameStatus(){
        return gameStatus;
    }

    private synchronized void setGame(Game game){
        this.game = game;
    }
    private synchronized Game getGame(){
        return game;
    }

    private synchronized TitleScreen getTitleScreen() {
        return titleScreen;
    }
    private synchronized void setTitleScreen(TitleScreen titleScreen) {
        this.titleScreen = titleScreen;
    }

    private synchronized MapEditor getMapEditor() {
        return mapEditor;
    }
    private synchronized void setMapEditor(MapEditor mapEditor) {
        this.mapEditor = mapEditor;
    }

    private synchronized Settings getSettings() {
        return settings;
    }
    private synchronized void setSettings(Settings settings) {
        this.settings = settings;
    }


    public GamePanel() {
        super(0, 0);

        // Bind canvas size to GamePanel size

        settingsManager = new SettingsManager();
        settingsManager.getSettings();
        drawUtil = new DrawUtil();
        titleScreen = new TitleScreen(drawUtil);
        performanceStorage = new PerformanceStorage();
    }

    public void startGameThread() {
        drawThread = new Thread(new drawThread(performanceStorage));
        logicThread = new Thread(new logicThread(performanceStorage));
        drawThread.start();
        logicThread.start();
        SoundManager.startBGM();
    }

    private String formatString(Double num, String format){
        DecimalFormat df = new DecimalFormat(format);
        return Objects.toString(df.format(num));
    }

    public void updateOnFrame() {
        Viewport.calculateViewport(this.getWidth(), this.getHeight());
        InputHandler.tick();
        switch (gameStatus){
            case GAME:
                tickScreen = getGame().copy();
                tickScreen.updateOnFrame();
                setGame((Game)tickScreen);
                break;
            case TITLESCREEN:
                tickScreen = getTitleScreen().copy();
                tickScreen.updateOnFrame();
                setTitleScreen((TitleScreen)tickScreen);
                if (getTitleScreen().isExit()){
                    switch (getTitleScreen().getSelectedButton()){
                        case MAP_EDITOR:
                            setMapEditor(new MapEditor(drawUtil));
                            setGameStatus(GameStatus.MAP_EDITOR);
                            break;
                        case SETTINGS:
                            setSettings(settings = new Settings(drawUtil, settingsManager));
                            setGameStatus(GameStatus.SETTINGS);
                            break;
                        case CUSTOM:
                            if (titleScreen.getSelectedFile() == null) break;
                            setGame(new Game(drawUtil, getTitleScreen().getSelectedFile()));
                            setGameStatus(GameStatus.GAME);
                            break;
                    }
                    getTitleScreen().resetSelections();
                }

                break;
            case MAP_EDITOR:
                tickScreen = getMapEditor().copy();
                tickScreen.updateOnFrame();
                setMapEditor((MapEditor)tickScreen);
                if (getMapEditor().isExit()){
                    setGameStatus(GameStatus.TITLESCREEN);
                }
                break;
            case SETTINGS:
                tickScreen = getSettings().copy();
                tickScreen.updateOnFrame();
                setSettings((Settings) tickScreen);
                if (getSettings().isExit()){
                    setGameStatus(GameStatus.TITLESCREEN);
                }
                break;
        }
        lastTickTime = System.currentTimeMillis();
    }

    public void draw() {
        GraphicsContext gc = this.getGraphicsContext2D();
        gc.setImageSmoothing(false);
        gc.setFontSmoothingType(FontSmoothingType.LCD);;
        drawUtil.setFactor((System.currentTimeMillis()-lastTickTime)/50d);
        drawUtil.setGC(gc);
        gc.clearRect(0, 0, this.getWidth(), this.getHeight());
        drawUtil.fillBackground();
        switch (getGameStatus()){
            case GAME:
                getGame().draw();
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
        }
        drawUtil.fillOffsetEdge();

        gc.setFill(Color.WHITE);
        gc.setFont(Fonts.DEFAULT.getFont(10));
        gc.setTextAlign(TextAlignment.LEFT);
        gc.setTextBaseline(VPos.TOP);
        gc.fillText("fps:" + formatString(performanceStorage.getFPS(), "00000.00") + " tps:" + formatString(performanceStorage.getTPS(), "00"), 10, 10);
        gc.fillText( "ttu:" + formatString(performanceStorage.getTickTimeUsed(), "0000.00") + "%" + " ttu1%:" + formatString(performanceStorage.getTickTimeUsedLow(), "0000.00") + "%" + " late frames:" + performanceStorage.getLateFrames(), 10, 20);
    }
    class logicThread implements Runnable{
        PerformanceStorage performanceStorage;
        public logicThread(PerformanceStorage performanceStorage){
            this.performanceStorage = performanceStorage;
        }
        @Override
        public void run(){
            long currentTime;
            long targetFrameInterval = 50000000;//20 tps
            long targetTime = System.nanoTime() + targetFrameInterval;
            boolean waited = true;
            while (logicThread != null) {
                currentTime = System.nanoTime();
                if (currentTime >= targetTime) {
                    targetTime += targetFrameInterval;
                    updateOnFrame();
                    if (!waited){
                        performanceStorage.addLateFrame();
                    }else{
                        waited = false;
                    }
                    performanceStorage.addTFrame();
                    performanceStorage.addTickTimeUsed(currentTime); //targetFrameInterval accounted for percentantage change
                    continue;
                }
                waited = true;
            }
        }
    }
    class drawThread implements Runnable {
        PerformanceStorage performanceStorage;
        private final AtomicBoolean isRendering = new AtomicBoolean(false);

        public drawThread(PerformanceStorage performanceStorage) {
            this.performanceStorage = performanceStorage;
        }

        @Override
        public void run() {
            long targetTime = System.nanoTime();

            while (drawThread != null) {
                targetFPS = settingsManager.getTargetFPS();
                long currentTime = System.nanoTime();
                long targetFrameInterval;
                if (targetFPS <= 0) {
                    targetFrameInterval = 0;
                } else {
                    targetFrameInterval = (long) (1000000000 / targetFPS);
                }

                if (!isRendering.get()) {
                    if (targetFPS <= 0 || currentTime >= targetTime) {
                        targetTime = currentTime + targetFrameInterval;
                        isRendering.set(true);
                        javafx.application.Platform.runLater(() -> {
                            try {
                                draw();
                                performanceStorage.addDFrame();
                            } finally {
                                isRendering.set(false);
                            }
                        });
                    }
                }

//                if (targetFPS <= 0) {
//                    Thread.yield();
//                }
            }
        }
    }

}
class PerformanceStorage {

    private final AtomicInteger dFrameCount = new AtomicInteger(0);
    private final AtomicInteger tFrameCount = new AtomicInteger(0);

    private double currentFPS = 0;
    private double currentTPS = 0;

    private long lastFPSUpdate = System.nanoTime();
    private long lastTPSUpdate = System.nanoTime();

    public void addDFrame() {
        dFrameCount.incrementAndGet();
        long now = System.nanoTime();
        long delta = now - lastFPSUpdate;

        if (delta >= 250000000L) {
//            System.out.println(dFrameCount.get()*4);
            this.currentFPS = (dFrameCount.getAndSet(0) / (delta/250000000d))*4;
            lastFPSUpdate = now;
        }
    }

    public void addTFrame() {
        tFrameCount.incrementAndGet();
        long now = System.nanoTime();
        long delta = now - lastTPSUpdate;
        if (delta >= 250000000L) {
            currentTPS = (tFrameCount.getAndSet(0) / (delta/250000000d))*4;
            lastTPSUpdate = now;
        }
    }

    public double getFPS() { return currentFPS; }
    public double getTPS() { return currentTPS; }
    private volatile double currentTTU = 0;
    private volatile double peakTTU = 0;
    private final List<Double> ttuHistory = new ArrayList<>();

    public void addTickTimeUsed(long startNano) {
        long duration = System.nanoTime() - startNano;
        double percent = (duration / 50000000.0) * 100.0;
        this.currentTTU = percent;
        synchronized(ttuHistory) {
            ttuHistory.add(percent);
            if (ttuHistory.size() > 100) {
                ttuHistory.remove(0);
            }
            double max = 0;
            for (Double d : ttuHistory) {
                if (d > max) max = d;
            }
            this.peakTTU = max;
        }
    }

    public double getTickTimeUsed() { return currentTTU; }
    public double getTickTimeUsedLow() { return peakTTU; }

    private final AtomicInteger lateFrames = new AtomicInteger(0);

    public void addLateFrame() {
        lateFrames.incrementAndGet();
    }

    public int getLateFrames() {
        return lateFrames.get();
    }
}
