package com.game;

import com.game.draw.DrawManager;
import com.game.input.Actions;
import com.game.input.Input;
import com.game.input.InputHandler;
import com.game.screens.*;
import com.game.settings.SettingsManager;
import com.game.sound.SoundManager;
import com.game.utils.LogType;
import com.game.utils.LoggerUtil;
import com.game.utils.PerformanceType;
import com.game.vulkan.VulkanManager;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.reflections.Reflections;
import oshi.ffm.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.Sensors;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;


public class GameCore /*extends Game */ {
  
  volatile AtomicLong lastTickTime = new AtomicLong(System.nanoTime());
  private GameStatus gameStatus = GameStatus.START_LOADING;
  
  private final InputHandler inputHandler;
  
  VulkanManager vulkanManager;
  
  private Thread logicThread;
  private Thread drawThread;
  
  
  private GameScreen gameScreen;
  private GameWindow tickScreen;
  private LoadingScreen loadingScreen;
  
  private final SettingsManager settingsManager;
  
  private TitleScreen titleScreen;
  private MapEditorScreen mapEditorScreen;
  private SettingsScreen settingsScreen;
  private final PerformanceStorage performanceStorage;
  private final HardwarePerformance hardwarePerformance;
  
  
  public GameCore() {
    settingsManager = new SettingsManager();
    titleScreen = new TitleScreen();
    performanceStorage = new PerformanceStorage();
    hardwarePerformance = new HardwarePerformance();
    inputHandler = new InputHandler();
//        System.out.println(NumUtil.LTF(NumUtil.sqrtFastScaled(NumUtil.FTL(48291753))));//should be 6949 ish
  }
  
  private synchronized GameStatus getGameStatus() {
    return gameStatus;
  }
  
  private synchronized void setGameStatus(GameStatus gameStatus) {
    this.gameStatus = gameStatus;
        /*switch (gameStatus){
            case TITLESCREEN -> setScreen(titleScreen);
            case SETTINGS -> setScreen(settingsScreen);
            case GAME -> setScreen(gameScreen);
            case MAP_EDITOR -> setScreen(mapEditorScreen);
            default -> setScreen(loadingScreen);
        }*/
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
  
  //    TODO:make a cache so it dosent keep making them
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
                    /*switch (((TitleScreen) tickScreen).getSelectedButton()) {
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
                    }*/
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
  
  
  private void init(Consumer<LoadingScreen> function, String name, LoadingScreen loadingScreen) {
    long startTime = System.nanoTime();
    loadingScreen.addText("init " + name);
    function.accept(loadingScreen);
    System.out.println(name + " time: " + (System.nanoTime() - startTime) / 1000000d + "\n");
  }
  
  private void init(Method method, String name, LoadingScreen loadingScreen) {
    long startTime = System.nanoTime();
    loadingScreen.addText("init " + name);
    try {
      method.invoke(null, loadingScreen);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    System.out.println(name + " time: " + (System.nanoTime() - startTime) / 1000000d);
  }
  
  private ObjectArrayList<Set<Class<?>>> findInitClasses() {
    Reflections reflections = new Reflections("com.game");
    ObjectArrayList<Set<Class<?>>> initClasses = new ObjectArrayList<>();
    Set<Class<?>> foundClasses = reflections.getTypesAnnotatedWith(Init.class);
    for (Class<?> clazz : foundClasses) {
      int stage = clazz.getAnnotation(Init.class).stage();
      while (initClasses.size() <= stage) {
        initClasses.add(new HashSet<>());
      }
      initClasses.get(stage).add(clazz);
    }
    return initClasses;
    
  }
  
  private int getTotalLoadingIncrements(ObjectArrayList<Set<Class<?>>> initClasses) {
    int total = 0;
    for (Set<Class<?>> stage : initClasses) {
      for (Class<?> clazz : stage) {
        try {
          Method method = clazz.getMethod("getIncrements");
          total += (int) method.invoke(null);
        } catch (InvocationTargetException | IllegalAccessException e) {
          throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
          total += 1;
        }
        
      }
    }
    return total;
  }
  
  
  public void init() {
    DrawManager.create();
    vulkanManager = new VulkanManager(loadingScreen);
    System.out.println("init Vulkan");
    DrawManager.setWindow(vulkanManager.getWindowHandle());
    logicThread = new Thread(new logicThread(performanceStorage));
    drawThread = new Thread(new drawThread(performanceStorage));
    
    ObjectArrayList<Set<Class<?>>> initClasses = findInitClasses();

//        int modelNum;
//        try (Stream<Path> stream = Files.list(Paths.get("resources/models"))) {
//            long fileCount = stream
//                    .filter(Files::isRegularFile)
//                    .count();
//
//            modelNum = (int)fileCount;
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//        System.out.println("model Count: " + modelNum);
    
    loadingScreen = new LoadingScreen(getTotalLoadingIncrements(initClasses));
    
    try {
      Thread.sleep(500);
    } catch (InterruptedException e) {
      LoggerUtil.log(e);
    }
    
    Thread loader = new Thread(() -> {
      
      for (int i = 0; i < initClasses.size(); i++) {
        System.out.println("Stage " + i + " contains: " + initClasses.get(i).size() + " classes.");
      }
      System.out.println();
      long initTime = System.nanoTime();
      SystemInfo si = new SystemInfo();
      init(LoggerUtil::init, "loggerUtil", loadingScreen);
      ExecutorService executor = Executors.newFixedThreadPool(Math.max(si.getHardware().getProcessor().getPhysicalProcessorCount() - 1, 1));
      for (int stage = 0; stage < initClasses.size(); stage++) {
        Set<Class<?>> classes = initClasses.get(stage);
        if (classes.isEmpty()) continue;
        long stageTime = System.nanoTime();
        System.out.println("Stage " + stage + " init starting");
        CountDownLatch latch = new CountDownLatch(classes.size());
        for (Class<?> clazz : classes) {
          executor.submit(() -> {
            try {
              Method method = clazz.getDeclaredMethod("init", LoadingScreen.class);
              init(method, clazz.getSimpleName(), loadingScreen);
            } catch (Exception e) {
              LoggerUtil.log(e);
            } finally {
              latch.countDown();
            }
          });
        }
        
        try {
          latch.await();
        } catch (InterruptedException e) {
          LoggerUtil.log(e);
        }
        System.out.println("Stage " + stage + " init in: " + (System.nanoTime() - stageTime) / 1000000d + "\n");
        
      }
      executor.shutdown();
      float initTotalTime = (System.nanoTime() - initTime) / 1000000f;
      loadingScreen.addText("done   time: " + initTotalTime);
      LoggerUtil.log("startup time: " + initTotalTime);
      
      LoggerUtil.log(LogType.EVENT, "os:", si.getOperatingSystem(), "cpu:", si.getHardware().getProcessor().getProcessorIdentifier().getName(), "gpu:", si.getHardware().getGraphicsCards().getFirst().getName(), "ram:", si.getHardware().getMemory().getTotal());
      System.out.println("total init time: " + initTotalTime);
      try {
        Thread.sleep(500);
      } catch (InterruptedException e) {
        LoggerUtil.log(e);
      }
      setGameStatus(GameStatus.TITLESCREEN);
      SoundManager.startBGM();
      logicThread.start();
    });
    loader.start();
    
    drawThread.run();

//        try {
//            loader.join();
//        } catch (InterruptedException e) {
//
//            LoggerUtil.log(e);
//
//        }
    
    try {
      drawThread.join();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

//        if (DrawUtil.getDevice() != null) {
//            VK14.vkDeviceWaitIdle(DrawUtil.getDevice());
//        }
    
    vulkanManager.cleanup();
  }
  
  public void resize(int width, int height) {
    DrawManager.updateViewport(width, height);
  }
  
  public void render() {
    long startTime = System.nanoTime();
    DrawManager.render();

//        VK14.vkWaitForFences(vkDevice, inFlightFences[currentFrame], true, Long.MAX_VALUE);

//        VK14.vkResetFences(vkDevice, inFlightFences[currentFrame]);
//        int imageIndex = acquireNextImage();

//        recordCommandBuffer(imageIndex);TODO: replace with drawing in drawUtil and use Vk14 instead of 10;
//         VkCommandBufferBeginInfo beginInfo = VkCommandBufferBeginInfo.calloc()
//        .sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO);
//
//    VK14.vkBeginCommandBuffer(commandBuffer, beginInfo);
//
//    VkRenderingAttachmentInfo colorAttachment = VkRenderingAttachmentInfo.calloc()
//        .sType(VK_STRUCTURE_TYPE_RENDERING_ATTACHMENT_INFO)
//        .imageView(getSwapchainImageView(imageIndex))
//        .imageLayout(VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL)
//        .loadOp(VK_ATTACHMENT_LOAD_OP_CLEAR)
//        .storeOp(VK_ATTACHMENT_STORE_OP_STORE);
//
//    VkRenderingInfo renderingInfo = VkRenderingInfo.calloc()
//        .sType(VK_STRUCTURE_TYPE_RENDERING_INFO)
//        .renderArea(rect -> rect.offset(o -> o.x(0).y(0)).extent(e -> e.width(800).height(600)))
//        .layerCount(1)
//        .pColorAttachments(VkRenderingAttachmentInfo.create(colorAttachment.address(), 1));
//
//    vkCmdBeginRendering(commandBuffer, renderingInfo);
//
//    // draw command
//

//    vkCmdEndRendering(commandBuffer);
//
//    VK14.vkEndCommandBuffer(commandBuffer);


//        VkSubmitInfo submitInfo = VkSubmitInfo.calloc();
//        VK14.vkQueueSubmit(graphicsQueue, submitInfo, inFlightFences[currentFrame]);
//        KHRSwapchain.vkQueuePresentKHR(presentQueue, presentInfo);
//        currentFrame = (currentFrame + 1) % MAX_FRAMES_IN_FLIGHT;
//        DrawUtil.startRender2D();
//        DrawUtil.setFactor((System.nanoTime() - lastTickTime.get()) / 50000000f);
//        System.out.println(DrawUtil.getFactor());
//        DrawUtil.setGC(gc);
//        DrawUtil.clearCanvas();
//        DrawUtil.fillBackground();
//
//        switch (getGameStatus()) {
//            case GAME, GAME_LOADING:
//                gameScreen.draw();
//                break;
//            case TITLESCREEN:
//                getTitleScreen().draw();
//                break;
//            case MAP_EDITOR:
//                getMapEditor().draw();
//                break;
//            case SETTINGS:
//                getSettings().draw();
//                break;
//            case START_LOADING:
//                loadingScreen.draw();
//                break;
//        }
//        DrawUtil.fillOffsetEdge();

//        DrawUtil.fillText("fps:" + formatString(Gdx.graphics.getFramesPerSecond(), "00000.00") + " tps:" + formatString(performanceStorage.getTPS(), "00"), 5, 1070, Fonts.DEFAULT, 10, StringAlignment.TOP_LEFT, 0xFFFFFFFF);
//        DrawUtil.fillText("fps1%:" + formatString(performanceStorage.getPeakDT(), "0000.00") + " fps0.1%:" + formatString(performanceStorage.getPeakPeakDT(), "0000.00"), 5, 1060, Fonts.DEFAULT, 10, StringAlignment.TOP_LEFT, 0xFFFFFFFF);
//        DrawUtil.fillText("ttu:" + formatString(performanceStorage.getTickTimeUsed(), "0000.00") + "%" + " ttu1%:" + formatString(performanceStorage.getTickTimeUsedLow(), "0000.00") + "%" + " late frames:" + performanceStorage.getLateFrames(), 5, 1050, Fonts.DEFAULT, 10, StringAlignment.TOP_LEFT, 0xFFFFFFFF);
//
//        DrawUtil.fillText(hardwarePerformance.getCpuStats(), 1915, 1070, Fonts.DEFAULT, 10, StringAlignment.TOP_RIGHT, 0xFFFFFFFF);
//        DrawUtil.fillText(hardwarePerformance.getRamStats(), 1915, 1060, Fonts.DEFAULT, 10, StringAlignment.TOP_RIGHT, 0xFFFFFFFF);
//        DrawUtil.fillText("BGM: " + SoundManager.getBgmName(), 1915, 1050, Fonts.DEFAULT, 10, StringAlignment.TOP_RIGHT, 0xFFFFFFFF);
//        DrawUtil.stopRender();
//        fpsLogger.log();
    performanceStorage.addDrawTimeUsed(startTime);
  }
  
  
  public void dispose() {
    LoggerUtil.log("close program");
    LoggerUtil.flush();
    ReplayScreen.flush();
//        Gdx.app.exit();
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
      while (!vulkanManager.shouldClose()) {
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
  
  class drawThread implements Runnable {
    PerformanceStorage performanceStorage;
    
    
    public drawThread(PerformanceStorage performanceStorage) {
      this.performanceStorage = performanceStorage;
    }
    
    @Override
    public void run() {
      long targetTime = System.nanoTime();
      int targetFPS = 720;
      long currentTime;
      long targetFrameInterval = 0;
      while (!vulkanManager.shouldClose()) {
//                targetFPS = settingsManager.getTargetFPS();
        currentTime = System.nanoTime();
        if (targetFPS > 0) {
          targetFrameInterval = 1000000000 / targetFPS;
        }
        if (targetFPS <= 0 || currentTime >= targetTime) {
          performanceStorage.addDrawTimeUsed(currentTime);
          targetTime += targetFrameInterval;
          vulkanManager.pollEvents();
          render();
          performanceStorage.addDFrame();
        }
      }
    }
  }
}


class PerformanceStorage {
  private final AtomicInteger dFrameCount = new AtomicInteger(0);
  private final AtomicInteger tFrameCount = new AtomicInteger(0);
  private final double[] ttuHistory = new double[100];
  private final double[] dtHistory = new double[1000];
  private final AtomicInteger lateFrames = new AtomicInteger(0);
  private double currentFPS = 0;
  private double currentTPS = 0;
  private long lastFPSUpdate = System.nanoTime();
  private long lastTPSUpdate = System.nanoTime();
  private volatile double currentTTU = 0;
  private volatile double peakTTU = 0;
  private int ttuIndex = 0;
  private int ttuCount = 0;
  private int dtIndex = 0;
  private int dtCount = 0;
  private volatile double peakDT = 0;
  private volatile double peakPeakDT = 0;
  private int loggerCooldown = 20;
  
  public void addDFrame() {
    dFrameCount.incrementAndGet();
    long now = System.nanoTime();
    long delta = now - lastFPSUpdate;
    
    if (delta >= 500000000L) {
      this.currentFPS = (dFrameCount.getAndSet(0) / (delta / 500000000d)) * 2;
      lastFPSUpdate = now;
    }
  }
  
  public void addTFrame() {
    tFrameCount.incrementAndGet();
    long now = System.nanoTime();
    long delta = now - lastTPSUpdate;
    if (delta >= 500000000L) {
      currentTPS = (tFrameCount.getAndSet(0) / (delta / 500000000d)) * 2;
      lastTPSUpdate = now;
    }
    loggerCooldown--;
    if (loggerCooldown <= 0) {
      LoggerUtil.log(LogType.PERFORMANCE, "fps:", getFPS(), "dt1%:", getPeakDT(), "dt0.1%:", getPeakPeakDT(), "tps:", getTPS(), "ttu:", getTickTimeUsed(), "ttu1%:", getTickTimeUsedLow(), "late frames:", getLateFrames());
      loggerCooldown = 40;
    }
  }
  
  public double getFPS() {
    return currentFPS;
  }
  
  public double getTPS() {
    return currentTPS;
  }
  
  public void addTickTimeUsed(long startNano) {
    long duration = System.nanoTime() - startNano;
    double percent = (duration / 50000000.0) * 100.0;
    this.currentTTU = percent;
    synchronized (ttuHistory) {
      ttuHistory[ttuIndex] = percent;
      ttuIndex = (ttuIndex + 1) % 100;
      if (ttuCount < 100) ttuCount++;
      double max = 0;
      for (int i = 0; i < ttuCount; i++) {
        if (ttuHistory[i] > max) max = ttuHistory[i];
      }
      this.peakTTU = max;
    }
    
    synchronized (dtHistory) {
      if (dtCount > 0) {
        double[] sortedCopy = new double[dtCount];
        System.arraycopy(dtHistory, 0, sortedCopy, 0, dtCount);
        java.util.Arrays.sort(sortedCopy);
        
        int limit1 = Math.max(1, (int) Math.round(dtCount / 100.0));
        double sum1 = 0;
        for (int i = dtCount - 1; i >= dtCount - limit1; i--) {
          sum1 += sortedCopy[i];
        }
        this.peakDT = sum1 / limit1;
        
        int limit01 = Math.max(1, (int) Math.round(dtCount / 1000.0));
        double sum01 = 0;
        for (int i = dtCount - 1; i >= dtCount - limit01; i--) {
          sum01 += sortedCopy[i];
        }
        this.peakPeakDT = sum01 / limit01;
      }
    }
  }
  
  public double getTickTimeUsed() {
    return currentTTU;
  }
  
  public double getTickTimeUsedLow() {
    return peakTTU;
  }
  
  public void addDrawTimeUsed(long startNano) {
    double dt = (double) (System.nanoTime() - startNano);
    synchronized (dtHistory) {
      dtHistory[dtIndex] = dt;
      dtIndex = (dtIndex + 1) % 1000;
      if (dtCount < 1000) dtCount++;
    }
  }
  
  public double getPeakDT() {
    return 1_000_000_000.0 / peakDT;
  }
  
  public double getPeakPeakDT() {
    return 1_000_000_000.0 / peakPeakDT;
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
  
  private final String cpuName;
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