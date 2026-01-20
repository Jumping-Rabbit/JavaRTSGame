package main;

import main.game.*;
import main.inputHandler.InputHandler;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;

import static java.lang.Math.*;


public class GamePanel extends JPanel
{

//    double targetFPS = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode().getRefreshRate(); // 0 or negative number means unlimited
    double targetFPS = 0;
    enum GameStatus {
        TITLESCREEN,
        SETTINGS,
        GAME,
        MAP_EDITOR
    }
    GameStatus gameStatus = GameStatus.TITLESCREEN;


    Thread drawThread;
    Thread logicThread;

    DrawUtil drawUtil;

    Game game;
    TitleScreen titleScreen;
    MapEditor mapEditor;
    Settings settings;
    PerformanceStorage performanceStorage;

    public GamePanel() {
        Dimension d = new Dimension((int)floor((Viewport.viewport.getWidth() * Viewport.viewport.getScale()) + Viewport.viewport.getXOffset()*2), (int)floor((Viewport.viewport.getHeight() * Viewport.viewport.getScale()) + Viewport.viewport.getYOffset()*2));
//        System.out.println(d);
        this.setPreferredSize(d);
        this.setDoubleBuffered(true); // buffer for better performance
        this.addKeyListener(InputHandler.getKeyHandler());
        this.addComponentListener(Viewport.viewport);
        this.addMouseListener(InputHandler.getMouseHandler());
        this.addMouseMotionListener(InputHandler.getMouseHandler());
        this.addMouseWheelListener(InputHandler.getMouseHandler());
        this.setFocusable(true);
        drawUtil = new DrawUtil();
        titleScreen = new TitleScreen(drawUtil);
        performanceStorage = new PerformanceStorage();

    }

    public void startGameThread() {
        drawThread = new Thread(new drawThread(performanceStorage));
        logicThread = new Thread(new logicThread(performanceStorage));
        drawThread.start();
        logicThread.start();
    }


//    GameData newGame(KeyHandler keyHandler, MouseHandler mouseHandler) {
//        return new GameData(123456, keyHandler, mouseHandler);
//    }
//    @Override
//    public void run() {
//        long currentTime;
//        long lastTime = System.nanoTime();
//        double targetFrameInterval = 50000000;
//
//        while (gameThread != null) {
//            currentTime = System.nanoTime();
//            if (currentTime - lastTime >= targetFrameInterval) {
//                frames.add(currentTime);
//                calculateFPS(currentTime);
//                updateOnFrame();
//                repaint();
//                lastTime = currentTime;
//            }
//        }
//    }
    private String formatString(Double num, String format){
        DecimalFormat df = new DecimalFormat(format);
        return Objects.toString(df.format(num));
    }

    public synchronized void updateOnFrame() {
        InputHandler.tick();
        if (titleScreen.isExit()){
            switch (titleScreen.getSelectedButton()){
                case MAP_EDITOR:
                    mapEditor = new MapEditor(drawUtil); // need to delete when leaving
                    gameStatus = GameStatus.MAP_EDITOR;
                    break;
                case SETTINGS:
                    settings = new Settings(drawUtil);
                    gameStatus = GameStatus.SETTINGS;
                    break;
                case CUSTOM:
                    if (titleScreen.getSelectedFile() == null) break;
                    game = new Game(drawUtil, titleScreen.getSelectedFile());
                    gameStatus = GameStatus.GAME;
            }
            titleScreen.resetSelections();
        }
        switch (gameStatus){
            case GAME:
                game.updateOnFrame();
                break;
            case TITLESCREEN:
                titleScreen.updateOnFrame();
                break;
            case MAP_EDITOR:
                mapEditor.updateOnFrame();
                if (mapEditor.isExit()){
                    gameStatus = GameStatus.TITLESCREEN;
                }
                break;
            case SETTINGS:
                settings.updateOnFrame();
                if (settings.isExit()){
                    gameStatus = GameStatus.TITLESCREEN;
                    settings.resetSelections();
                }
                break;
        }
    }

    public synchronized void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        drawUtil.setG2(g2);
        drawUtil.fillBackground();
        switch (gameStatus){
            case GAME:
                game.draw();
                break;
            case TITLESCREEN:
                titleScreen.draw();
                break;
            case MAP_EDITOR:
                mapEditor.draw();
                break;
            case SETTINGS:
                settings.draw();
                break;
        }

        g2.setColor(Color.white);
        g2.setFont(Fonts.fpsFont);
        g2.drawString("fps:" + formatString(performanceStorage.getFPS(), "00000.00") + " tps:" + formatString(performanceStorage.getTPS(), "00"), 10, 10);
        g2.drawString( "ttu:" + formatString(performanceStorage.getTickTimeUsed(), "0000.00") + "%" + " ttu1%:" + formatString(performanceStorage.getTickTimeUsedLow(), "0000.00") + "%" + " late frames:" + performanceStorage.getLateFrames(), 10, 20);

        g2.dispose();
    }
    class logicThread implements Runnable{
        PerformanceStorage performanceStorage;
        public logicThread(PerformanceStorage performanceStorage){
            this.performanceStorage = performanceStorage;
        }
        @Override
        public void run(){
            long currentTime;
            long lastTime = System.nanoTime();
            long targetFrameInterval = 50000000;//20 tps
            long targetTime = System.nanoTime() + targetFrameInterval;
            boolean waited = true;
            while (logicThread != null) {
                currentTime = System.nanoTime();
                if (currentTime >= targetTime) {
                    updateOnFrame();
                    targetTime = currentTime + targetFrameInterval;
                    if (!waited){
                        performanceStorage.addLateFrame();
                    }else{
                        waited = false;
                    }
                    performanceStorage.addTFrame(currentTime, lastTime);
                    performanceStorage.addTickTimeUsed((double) (System.nanoTime()-currentTime)/500000); //targetFrameInterval accounted for percentantage change
                    lastTime = currentTime;
                    continue;
                }
                waited = true;
            }
        }
    }
    class drawThread implements Runnable{
        PerformanceStorage performanceStorage;
        public drawThread(PerformanceStorage performanceStorage){
            this.performanceStorage = performanceStorage;
        }
        private double targetFrameInterval;

        @Override
        public void run(){
            long currentTime;
            long lastTime = System.nanoTime();
            while (drawThread != null) {
                if (targetFPS == 0){
                    targetFrameInterval = 0;
                } else {
                    targetFrameInterval = 1000000000/targetFPS;
                }
                currentTime = System.nanoTime();
                if (currentTime - lastTime >= targetFrameInterval) {
                    repaint();
                    performanceStorage.addDFrame(currentTime, lastTime);
                    lastTime = currentTime;
                }
            }
        }
    }
}
class PerformanceStorage{
    private ArrayList<Long> tFrames = new ArrayList<>(21);
//    private ArrayList<long[]> tFramesLow = new ArrayList<>(101);
    private ArrayList<Long> dFrames = new ArrayList<>();
//    private ArrayList<long[]> dFramesLow = new ArrayList<>();
    private ArrayList<Double> tickTimeUsed = new ArrayList<>();
    public int lateFrames = 0;
    public double fps;
    public double tps;
//    public double fpsLow;
//    public double tpsLow;
    public double ttu;
    public long late;
    ExecutorService executor = Executors.newFixedThreadPool(3);//amount of function that will need to be calculated

    public synchronized void addLateFrame(){
        lateFrames++;
    };

    public synchronized void addTFrame(long currentFrame, long lastFrame){
        tFrames.add(currentFrame);
//        tFramesLow.add(new long[]{currentFrame, currentFrame-lastFrame});
        executor.submit(this::calculateFPS);
//            tFramesLow.add(frame);
    }
    public synchronized void addDFrame(long currentFrame, long lastFrame){
        dFrames.add(currentFrame);
//        dFramesLow.add(new long[]{currentFrame, currentFrame-lastFrame});
        executor.submit(this::calculateTPS);
//            dFramesLow.add(frame);
    }
    public synchronized void addTickTimeUsed(double ttu){
        tickTimeUsed.add(ttu);
        executor.submit(this::calculateTTULow);
    }
    public synchronized double getTPS(){
        return tps;
    }
    public synchronized double getFPS(){
        return fps;
    }
//    public synchronized double getTPSLow(){
//        return tpsLow;
//    }
//    public synchronized double getFPSLow(){
//        return fpsLow;
//    }
    public synchronized int getLateFrames(){
        return lateFrames;
    }
    public synchronized double getTickTimeUsed(){
        if (tickTimeUsed.isEmpty()){
            return 0;
        }
        return tickTimeUsed.getLast();
    }
    public synchronized double getTickTimeUsedLow(){
        return ttu;
    }

    private synchronized void calculateTPS() {
        tFrames.removeIf(f -> System.nanoTime() - f >= 1000000000);
        tps = tFrames.size();

//        tFramesLow.removeIf(f -> System.nanoTime() - f[0] >= 5000000000L);
//        tpsLow = 1000000000/tFramesLow.stream()
//                .map(array -> array[1])
//                .sorted(Comparator.reverseOrder())
//                .limit(tFramesLow.size()/100)
//                .mapToDouble(Long::doubleValue)
//                .average()
//                .orElse(0.0);
    }

    private synchronized void calculateFPS() {
        dFrames.removeIf(f -> System.nanoTime() - f >= 2000000000);
        fps = dFrames.size()/2f;

//        dFramesLow.removeIf(f -> System.nanoTime() - f[0] >= 5000000000L);
//        fpsLow = 1000000000/dFramesLow.stream()
//                .map(array -> array[1])
//                .sorted(Comparator.reverseOrder())
//                .limit(dFramesLow.size()/100)
//                .mapToDouble(Long::doubleValue)
//                .average()
//                .orElse(0.0);
    }

    private synchronized void calculateTTULow(){
        if (tickTimeUsed.size()>100){
            tickTimeUsed.subList(0, tickTimeUsed.size()-100).clear();
        }
        double largest = 0;
        for (Double ttu : tickTimeUsed){
            if (ttu > largest){
                largest = ttu;
            }
        }
        ttu = largest;
    }
}
