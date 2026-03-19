package game;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import java.io.*;

public class SettingsManager{
    public enum SettingTypes{
        INTEGER,
        BOOLEAN,
        STRING
    }
    public enum Settings{
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
        public SettingTypes getSettingType(){
            return type;
        }
        public String getId(){
            return id;
        }
        public static Settings fromValue(String givenName) {
            for (Settings setting : values()) {
                if (setting.id.equalsIgnoreCase(givenName)) {
                    return setting;
                }
            }
            return null;
        }
    }


    private final int minTargetFPS = 1;
    private int targetFPS = 1;
    private final Object targetFPSLock = new Object();


    private final int minMonitorNum = 0;
    private int monitorNum = 0;
    private final Object monitorNumLock = new Object();


    public enum DisplayModes{
        WINDOWED("windowed"),
        WINDOWED_FULLSCREEN("windowedFullscreen"),
        FULLSCREEN("fullscreen");

        private final String string;
        DisplayModes(String string){
            this.string = string;
        }
        public String getString(){
            return string;
        }
        public static DisplayModes fromValue(String givenName) {
            for (DisplayModes displayMode : values()) {
                if (displayMode.string.equalsIgnoreCase(givenName)) {
                    return displayMode;
                }
            }
            return null;
        }
    }
    private DisplayModes displayMode = DisplayModes.WINDOWED_FULLSCREEN;
    private final Object displayModeLock = new Object();


    private final int minMasterVolume = 0;
    private final int maxMasterVolume = 100;
    private int masterVolume = 0;
    private final Object masterVolumeLock = new Object();

    private final int minBGMVolume = 0;
    private final int maxBGMVolume = 100;
    private int BGMVolume = 0;
    private final Object BGMVolumeLock = new Object();

    private final int minSFXVolume = 0;
    private final int maxSFXVolume = 100;
    private int SFXVolume = 0;
    private final Object SFXVolumeLock = new Object();

    private boolean antialiasing = true;
    private final Object antialiasingLock = new Object();

    public enum GraphicsQuality{
        LOW("low"),
        MEDIUM("medium"),
        HIGH("high");

        private final String string;
        GraphicsQuality(String string){
            this.string = string;
        }
        public String getString(){
            return string;
        }
        public static GraphicsQuality fromValue(String givenName) {
            for (GraphicsQuality graphicsQuality : values()) {
                if (graphicsQuality.string.equalsIgnoreCase(givenName)) {
                    return graphicsQuality;
                }
            }
            return null;
        }
    }
    private GraphicsQuality graphicsQuality = GraphicsQuality.HIGH;
    private final Object graphicsQualityLock = new Object();




    private void writeSettings(String directory, String key, Object value){
        var objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.readTree(new File("resources/settings.json"));
        JsonNode setting = root.path(directory);
        ObjectNode objectNode = (ObjectNode) setting;
        switch(value.getClass().getSimpleName()){
            case "String" -> objectNode.put(key, value.toString());
            case "Integer" -> objectNode.put(key, Integer.parseInt(value.toString()));
            case "Boolean" -> objectNode.put(key, Boolean.parseBoolean(value.toString()));
        }
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File("resources/settings.json"), root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getSettings(){
        var objectMapper = new ObjectMapper();
        JsonNode root  = objectMapper.readTree(new File("resources/settings.json"));
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

    public String getSettingStringValue(String id){
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
    public void setSetting(String id, String set){
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


    public void setTargetFPS(int targetFPS){
        targetFPS = StrictMath.max(targetFPS, minTargetFPS);
        if (targetFPS < minTargetFPS){
            return;
        }
        synchronized (targetFPSLock){
            this.targetFPS = targetFPS;
            writeSettings("graphics", "targetFPS", targetFPS);
        }
    }
    public int getTargetFPS(){
        synchronized (targetFPSLock) {
            return targetFPS;
        }
    }

    public void setMonitorNum(int monitorNum){
        monitorNum = StrictMath.max(monitorNum, minMonitorNum);
        synchronized (monitorNumLock){
            this.monitorNum = monitorNum;
            writeSettings("graphics", "monitorNum", monitorNum);
            synchronized (displayModeLock) {
                switch (displayMode) {
                    case WINDOWED:
                        Main.setWindowed();
                        break;
                    case WINDOWED_FULLSCREEN:
                        Main.setWindowedBorderless(this.monitorNum);
                        break;
                    case FULLSCREEN:
                        Main.setFullscreen(monitorNum);
                        break;
                }
            }
        }
    }
    public int getMonitorNum(){
        synchronized (monitorNumLock) {
            return monitorNum;
        }
    }

    public void setDisplayMode(DisplayModes displayMode){
        synchronized (displayModeLock){
            if(this.displayMode == displayMode) {
                return;
            }
            this.displayMode = displayMode;
            writeSettings("graphics", "displayMode", displayMode.string);
            switch (displayMode){
                case WINDOWED:
                    Main.setWindowed();
                    break;
                case WINDOWED_FULLSCREEN:
                    synchronized (monitorNumLock){
                        Main.setWindowedBorderless(this.monitorNum);
                    }
                    break;
                case FULLSCREEN:
                    synchronized (monitorNumLock){
                        Main.setFullscreen(monitorNum);
                    }
                    break;
            }
        }
    }
    public DisplayModes getDisplayMode(){
        synchronized (displayModeLock) {
            return displayMode;
        }
    }

    public void setMasterVolume(int volume){
        volume = StrictMath.clamp(volume, minMasterVolume, maxMasterVolume);
        synchronized (masterVolumeLock){
            masterVolume = volume;
            writeSettings("audio", "masterVolume", volume);
            SoundManager.setMasterVolume(volume);
        }
    }
    public int getMasterVolume(){
        synchronized (masterVolumeLock) {
            return masterVolume;
        }
    }

    public void setBGMVolume(int volume){
        volume = StrictMath.clamp(volume, minBGMVolume, maxBGMVolume);
        synchronized (BGMVolumeLock){
            BGMVolume = volume;
            writeSettings("audio", "BGMVolume", volume);
            SoundManager.setBGMVolume(volume);
        }
    }
    public int getBGMVolume(){
        synchronized (BGMVolumeLock) {
            return BGMVolume;
        }
    }

    public void setSFXVolume(int volume){
        volume = StrictMath.clamp(volume, minSFXVolume, maxSFXVolume);
        synchronized (SFXVolumeLock){
            SFXVolume = volume;
            writeSettings("audio", "SFXVolume", volume);
            SoundManager.setSFXVolume(volume);
        }
    }
    public int getSFXVolume(){
        synchronized (SFXVolumeLock) {
            return SFXVolume;
        }

    }


    public boolean getAntialiasing() {
        synchronized (antialiasingLock){
            return antialiasing;
        }
    }
    public void setAntialiasing(boolean antialiasing) {
        synchronized (antialiasingLock){
            this.antialiasing = antialiasing;
            writeSettings("graphics", "antialiasing", antialiasing);
        }
    }

    public void setGraphicsQuality(GraphicsQuality graphicsQuality){
        synchronized (graphicsQualityLock){
            this.graphicsQuality = graphicsQuality;
            writeSettings("graphics", "graphicsQuality", graphicsQuality.string);
        }
    }
    public GraphicsQuality getGraphicsQuality(){
        synchronized (graphicsQualityLock) {
            return graphicsQuality;
        }
    }

}
