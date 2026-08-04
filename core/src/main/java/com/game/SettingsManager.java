package com.game;

import com.game.utils.LoggerUtil;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import java.io.File;

public class SettingsManager {
    private static final int minTargetFPS = 0;
    private static final Object targetFPSLock = new Object();
    private static final int minMonitorNum = 0;
    private static final Object monitorNumLock = new Object();
    private static final Object displayModeLock = new Object();
    private static final int minMasterVolume = 0;
    private static final int maxMasterVolume = 100;
    private static final Object masterVolumeLock = new Object();
    private static final int minBGMVolume = 0;
    private static final int maxBGMVolume = 100;
    private static final Object BGMVolumeLock = new Object();
    private static final int minSFXVolume = 0;
    private static final int maxSFXVolume = 100;
    private static final Object SFXVolumeLock = new Object();
    private static final Object antialiasingLock = new Object();
    private static final Object graphicsQualityLock = new Object();
    private static int targetFPS = 1;
    private static int monitorNum = 0;
    private static DisplayModes displayMode = DisplayModes.WINDOWED_FULLSCREEN;
    private static int masterVolume = 0;
    private static int BGMVolume = 0;
    private static int SFXVolume = 0;
    private static boolean antialiasing = true;
    private static GraphicsQuality graphicsQuality = GraphicsQuality.HIGH;

    private static void writeSettings(String directory, String key, Object value) {
        var objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.readTree(new File("core/resources/settings.json"));
        JsonNode setting = root.path(directory);
        ObjectNode objectNode = (ObjectNode) setting;
        switch (value.getClass().getSimpleName()) {
            case "String" -> objectNode.put(key, value.toString());
            case "Integer" -> objectNode.put(key, Integer.parseInt(value.toString()));
            case "Boolean" -> objectNode.put(key, Boolean.parseBoolean(value.toString()));
        }
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File("core/resources/settings.json"), root);
        } catch (Exception e) {
            LoggerUtil.log(e);
        }
    }

    public static void getSettings() {
        var objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.readTree(new File("core/resources/settings.json"));
        JsonNode graphics = root.get("graphics");
        JsonNode audio = root.get("audio");
        setMonitorNum(graphics.get("monitorNum").asInt());
        setDisplayMode(DisplayModes.fromValue(String.valueOf(graphics.get("displayMode").asString())));
        setGraphicsQuality(GraphicsQuality.fromValue(String.valueOf(graphics.get("graphicsQuality").asString())));
        setAntialiasing(graphics.get("antialiasing").asBoolean());
        setTargetFPS(graphics.get("targetFPS").asInt());
        setBGMVolume(audio.get("BGMVolume").asInt());
        setMasterVolume(audio.get("masterVolume").asInt());
        setSFXVolume(audio.get("SFXVolume").asInt());
    }

    public String getSettingStringValue(String id) {
        return switch (id) {
            case "targetFPS" -> String.valueOf(getTargetFPS());
            case "monitorNum" -> String.valueOf(getMonitorNum());
            case "displayMode" -> getDisplayMode().getString();
            case "masterVolume" -> String.valueOf(getMasterVolume());
            case "BGMVolume" -> String.valueOf(getBGMVolume());
            case "SFXVolume" -> String.valueOf(getSFXVolume());
            case "antialiasing" -> String.valueOf(getAntialiasing());
            case "graphicsQuality" -> getGraphicsQuality().getString();
            default -> "";
        };
    }

    public void setSetting(String id, String set) {
        switch (id) {
            case "targetFPS" -> setTargetFPS(Integer.parseInt(set));
            case "monitorNum" -> setMonitorNum(Integer.parseInt(set));
            case "displayMode" -> setDisplayMode(DisplayModes.fromValue(set));
            case "masterVolume" -> setMasterVolume(Integer.parseInt(set));
            case "BGMVolume" -> setBGMVolume(Integer.parseInt(set));
            case "SFXVolume" -> setSFXVolume(Integer.parseInt(set));
            case "antialiasing" -> setAntialiasing(Boolean.parseBoolean(set));
            case "graphicsQuality" -> setGraphicsQuality(GraphicsQuality.fromValue(set));
        }
    }

    public int getTargetFPS() {
        synchronized (targetFPSLock) {
            return targetFPS;
        }
    }

    public static void setTargetFPS(int targetFPS) {
        targetFPS = StrictMath.max(targetFPS, minTargetFPS);
        if (targetFPS < minTargetFPS) {
            return;
        }
        synchronized (targetFPSLock) {
            SettingsManager.targetFPS = targetFPS;
            writeSettings("graphics", "targetFPS", targetFPS);
        }
    }

    public int getMonitorNum() {
        synchronized (monitorNumLock) {
            return monitorNum;
        }
    }

    public static void setMonitorNum(int monitorNum) {
        monitorNum = StrictMath.max(monitorNum, minMonitorNum);
        synchronized (monitorNumLock) {
            SettingsManager.monitorNum = monitorNum;
            writeSettings("graphics", "monitorNum", monitorNum);
            synchronized (displayModeLock) {
                switch (displayMode) {
                    case WINDOWED:
//                        Lwjgl3Launcher.setWindowed();
                        break;
                    case WINDOWED_FULLSCREEN:
//                        Lwjgl3Launcher.setWindowedBorderless(SettingsManager.monitorNum);
                        break;
                    case FULLSCREEN:
//                        Lwjgl3Launcher.setFullscreen(monitorNum);
                        break;
                }
            }
        }
    }

    public DisplayModes getDisplayMode() {
        synchronized (displayModeLock) {
            return displayMode;
        }
    }

    public static void setDisplayMode(DisplayModes displayMode) {
        synchronized (displayModeLock) {
            if (SettingsManager.displayMode == displayMode) {
                return;
            }
            SettingsManager.displayMode = displayMode;
            writeSettings("graphics", "displayMode", displayMode.string);
            switch (displayMode) {
                case WINDOWED:
//                    Lwjgl3Launcher.setWindowed();
                    break;
                case WINDOWED_FULLSCREEN:
                    synchronized (monitorNumLock) {
//                        Lwjgl3Launcher.setWindowedBorderless(SettingsManager.monitorNum);
                    }
                    break;
                case FULLSCREEN:
                    synchronized (monitorNumLock) {
//                        Lwjgl3Launcher.setFullscreen(monitorNum);
                    }
                    break;
            }
        }
    }

    public int getMasterVolume() {
        synchronized (masterVolumeLock) {
            return masterVolume;
        }
    }

    public static void setMasterVolume(int volume) {
        volume = StrictMath.clamp(volume, minMasterVolume, maxMasterVolume);
        synchronized (masterVolumeLock) {
            masterVolume = volume;
            writeSettings("audio", "masterVolume", volume);
            SoundManager.setMasterVolume(volume);
        }
    }

    public int getBGMVolume() {
        synchronized (BGMVolumeLock) {
            return BGMVolume;
        }
    }

    public static void setBGMVolume(int volume) {
        volume = StrictMath.clamp(volume, minBGMVolume, maxBGMVolume);
        synchronized (BGMVolumeLock) {
            BGMVolume = volume;
            writeSettings("audio", "BGMVolume", volume);
            SoundManager.setBGMVolume(volume);
        }
    }

    public int getSFXVolume() {
        synchronized (SFXVolumeLock) {
            return SFXVolume;
        }

    }

    public static void setSFXVolume(int volume) {
        volume = StrictMath.clamp(volume, minSFXVolume, maxSFXVolume);
        synchronized (SFXVolumeLock) {
            SFXVolume = volume;
            writeSettings("audio", "SFXVolume", volume);
            SoundManager.setSFXVolume(volume);
        }
    }

    public boolean getAntialiasing() {
        synchronized (antialiasingLock) {
            return antialiasing;
        }
    }

    public static void setAntialiasing(boolean antialiasing) {
        synchronized (antialiasingLock) {
            SettingsManager.antialiasing = antialiasing;
            writeSettings("graphics", "antialiasing", antialiasing);
        }
    }

    public GraphicsQuality getGraphicsQuality() {
        synchronized (graphicsQualityLock) {
            return graphicsQuality;
        }
    }

    public static void setGraphicsQuality(GraphicsQuality graphicsQuality) {
        synchronized (graphicsQualityLock) {
            SettingsManager.graphicsQuality = graphicsQuality;
            writeSettings("graphics", "graphicsQuality", graphicsQuality.string);
        }
    }


    public enum SettingTypes {
        INTEGER,
        BOOLEAN,
        STRING
    }

    public enum Settings {
        TARGET_FPS(SettingTypes.INTEGER, "targetFPS"),
        MONITOR_NUM(SettingTypes.INTEGER, "monitorNum"),
        DISPLAY_MODES(SettingTypes.STRING, "displayMode"),
        MASTER_VOLUME(SettingTypes.INTEGER, "masterVolume"),
        BGM_VOLUME(SettingTypes.INTEGER, "BGMVolume"),
        SFX_VOLUME(SettingTypes.INTEGER, "SFXVolume"),
        ANTIALIASING(SettingTypes.BOOLEAN, "antialiasing"),
        GRAPHICS_QUALITY(SettingTypes.STRING, "graphicsQuality");

        private final SettingTypes type;
        private final String id;

//        public abstract String getStringValue();

        Settings(SettingTypes type, String id) {
            this.type = type;
            this.id = id;
        }

        public static Settings fromValue(String givenName) {
            for (Settings setting : values()) {
                if (setting.id.equalsIgnoreCase(givenName)) {
                    return setting;
                }
            }
            return null;
        }

        public SettingTypes getSettingType() {
            return type;
        }

        public String getId() {
            return id;
        }
    }

    public enum DisplayModes {
        WINDOWED("windowed"),
        WINDOWED_FULLSCREEN("windowedFullscreen"),
        FULLSCREEN("fullscreen");

        private final String string;

        DisplayModes(String string) {
            this.string = string;
        }

        public static DisplayModes fromValue(String givenName) {
            for (DisplayModes displayMode : values()) {
                if (displayMode.string.equalsIgnoreCase(givenName)) {
                    return displayMode;
                }
            }
            return null;
        }

        public String getString() {
            return string;
        }
    }

    public enum GraphicsQuality {
        LOW("low"),
        MEDIUM("medium"),
        HIGH("high");

        private final String string;

        GraphicsQuality(String string) {
            this.string = string;
        }

        public static GraphicsQuality fromValue(String givenName) {
            for (GraphicsQuality graphicsQuality : values()) {
                if (graphicsQuality.string.equalsIgnoreCase(givenName)) {
                    return graphicsQuality;
                }
            }
            return null;
        }

        public String getString() {
            return string;
        }
    }

}
