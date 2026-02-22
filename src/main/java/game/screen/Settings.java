package game.screen;

import game.Fonts;
import game.SettingsManager;
import javafx.geometry.Rectangle2D;
import utils.CollisionUtil;
import utils.DrawUtil;
import inputHandler.Input;
import inputHandler.InputHandler;

import java.util.Objects;

public class Settings extends Screen{
    public enum Buttons {
        GRAPHICS(new Rectangle2D(0, 0, 860, 100), "graphics"),
        AUDIO(new Rectangle2D(860, 0, 860, 100), "audio"),
        EXIT(new Rectangle2D(1720, 0, 200, 100), "exit");

        private final Rectangle2D rectangle;
        private final String name;
        Buttons(Rectangle2D rectangle, String name){
            this.rectangle = rectangle;
            this.name = name;
        }
        private String getName(){
            return name;
        }
        private Rectangle2D getRectangle(){
            return rectangle;
        }
    }
    public enum AudioButtons {
        MASTER_VOLUME(new Rectangle2D(50, 150, 1000, 100), "master volume", SettingsManager.Settings.MASTER_VOLUME, "masterVolume"),
        BGM_VOLUME(new Rectangle2D(50, 300, 1000, 100), "bgm volume", SettingsManager.Settings.BGM_VOLUME, "BGMVolume"),
        SFX_VOLUME(new Rectangle2D(50, 450, 1000, 100), "bgm volume", SettingsManager.Settings.BGM_VOLUME, "SFXVolume");

        private final Rectangle2D rectangle;
        private final String name;
        private final SettingsManager.Settings setting;
        private final String id;
        AudioButtons(Rectangle2D rectangle, String name, SettingsManager.Settings setting, String id){
            this.rectangle = rectangle;
            this.name = name;
            this.setting = setting;
            this.id = id;
        }
        private String getName(){
            return name;
        }
        private Rectangle2D getRectangle(){
            return rectangle;
        }
        private SettingsManager.Settings getSetting(){
            return setting;
        }
        private String getId(){
            return id;
        }
    }
    public enum GraphicsButtons {
        GRAPHICS_QUALITY(new Rectangle2D(50, 150, 1000, 100), "graphics quality", SettingsManager.Settings.GRAPHICS_QUALITY, "graphicsQuality"),
        MONITOR_NUM(new Rectangle2D(50, 300, 1000, 100), "monitor num", SettingsManager.Settings.MONITOR_NUM, "monitorNum"),
        TARGET_FPS(new Rectangle2D(50, 450, 1000, 100), "target fps", SettingsManager.Settings.TARGET_FPS, "targetFPS"),
        ANTIALIASING(new Rectangle2D(50, 600, 1000, 100), "antialiasing", SettingsManager.Settings.ANTIALIASING, "antialiasing"),
        DISPLAY_MODE(new Rectangle2D(50, 750, 1000, 100), "display mode", SettingsManager.Settings.DISPLAY_MODES, "displayMode");

        private final Rectangle2D rectangle;
        private final String name;
        private final SettingsManager.Settings setting;
        private final String id;
        GraphicsButtons(Rectangle2D rectangle, String name, SettingsManager.Settings setting, String id){
            this.rectangle = rectangle;
            this.name = name;
            this.setting = setting;
            this.id = id;
        }
        private SettingsManager.Settings getSetting(){
            return setting;
        }
        private String getName(){
            return name;
        }
        private Rectangle2D getRectangle(){
            return rectangle;
        }
        private String getId(){
            return id;
        }
    }
    private DrawUtil drawUtil;
    private boolean isEditing = false;
    private Buttons currentSection = Buttons.GRAPHICS;
    private GraphicsButtons currentGraphicsSetting = GraphicsButtons.GRAPHICS_QUALITY;
    private AudioButtons currentAudioSetting = AudioButtons.MASTER_VOLUME;
    private boolean exit = false;
    private SettingsManager settingsManager;

    public boolean isExit() {
        if (exit){
            exit = false;
            return true;
        }
        return false;
    }

    public void resetSelections(){
        isEditing = false;
        currentSection = Buttons.GRAPHICS;
        currentGraphicsSetting = GraphicsButtons.GRAPHICS_QUALITY;
        currentAudioSetting = AudioButtons.MASTER_VOLUME;
    }

    public Settings(DrawUtil drawUtil, SettingsManager settingsManager) {
        this.drawUtil = drawUtil;
        this.settingsManager = settingsManager;
    }

    public Settings(Settings settings){
        drawUtil = settings.drawUtil;
        isEditing = settings.isEditing;
        currentSection = settings.currentSection;
        currentGraphicsSetting = settings.currentGraphicsSetting;
        currentAudioSetting = settings.currentAudioSetting;
        exit = settings.exit;
        settingsManager = settings.settingsManager;
    }

    public Screen copy(){
        return new Settings(this);
    }

    private boolean isDouble(String string){
        try{
            Double.parseDouble(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public void updateOnFrame() {
        for (Input input : InputHandler.getInputs()){
            switch (input.getInputType()) {
                case KEYPRESS:
                    if (Objects.equals(input.getKey(), "a") || Objects.equals(input.getKey(), "left")){
                        if (!isEditing) {
                            Buttons oldButton = currentSection;
                            currentSection = Buttons.values()[(currentSection.ordinal() - 1) >= 0 ? (currentSection.ordinal() - 1) : (Buttons.values().length - 1)];
                            if (currentSection != oldButton) {
                                currentGraphicsSetting = GraphicsButtons.GRAPHICS_QUALITY;
                                currentAudioSetting = AudioButtons.MASTER_VOLUME;
                            }
                            break;
                        }
                    } else if (Objects.equals(input.getKey(), "d") || Objects.equals(input.getKey(), "right")){
                        if(!isEditing) {
                            Buttons oldButton = currentSection;
                            currentSection = Buttons.values()[(currentSection.ordinal() + 1) % Buttons.values().length];
                            if (currentSection != oldButton) {
                                currentGraphicsSetting = GraphicsButtons.GRAPHICS_QUALITY;
                                currentAudioSetting = AudioButtons.MASTER_VOLUME;
                            }
                        }
                    } else if (Objects.equals(input.getKey(), "w")) {
                        if(!isEditing) {
                            switch (currentSection) {
                                case GRAPHICS:
                                    currentGraphicsSetting = GraphicsButtons.values()[(currentGraphicsSetting.ordinal() - 1) >= 0 ? (currentGraphicsSetting.ordinal() - 1) : (GraphicsButtons.values().length - 1)];
                                    break;
                                case AUDIO:
                                    currentAudioSetting = AudioButtons.values()[(currentAudioSetting.ordinal() - 1) >= 0 ? (currentAudioSetting.ordinal() - 1) : (AudioButtons.values().length - 1)];
                                    break;
                            }
                        }
                    } else if (Objects.equals(input.getKey(), "s")) {
                        if(!isEditing) {
                            switch (currentSection) {
                                case GRAPHICS:
                                    currentGraphicsSetting = GraphicsButtons.values()[(currentGraphicsSetting.ordinal() + 1) % GraphicsButtons.values().length];
                                    break;
                                case AUDIO:
                                    currentAudioSetting = AudioButtons.values()[(currentAudioSetting.ordinal() + 1) % AudioButtons.values().length];
                                    break;
                            }
                        }
                    } else if (Objects.equals(input.getKey(), "escape")){
                        if (isEditing){
                            isEditing = false;
                            break;
                        }
                        exit = true;
                    } else if (Objects.equals(input.getKey(), "enter")){
                        if (currentSection == Buttons.EXIT){
                            exit = true;
                            break;
                        }
                        isEditing = !isEditing;
                    }
                    if (isEditing){
                        String currentSetting = switch (currentSection) {
                            case GRAPHICS -> currentGraphicsSetting.getId();
                            case AUDIO -> currentAudioSetting.getId();
                            default -> "";
                        };
                        switch(SettingsManager.Settings.fromValue(currentSetting).getSettingType()){
                            case INTEGER:
                                if (Objects.equals(input.getKey(), "backspace") && !settingsManager.getSettingStringValue(currentSetting).isEmpty()){
                                    settingsManager.setSetting(currentSetting, !settingsManager.getSettingStringValue(currentSetting).substring(0, String.valueOf(settingsManager.getSettingStringValue(currentSetting)).length() - 1).isEmpty() ? settingsManager.getSettingStringValue(currentSetting).substring(0, String.valueOf(settingsManager.getSettingStringValue(currentSetting)).length() - 1) : "0");
                                } else if (isDouble(input.getKey())){
                                    settingsManager.setSetting(currentSetting,settingsManager.getSettingStringValue(currentSetting) + input.getKey());
                                }
                                break;
                            case BOOLEAN:
                                if (Objects.equals(input.getKey(), "w")||Objects.equals(input.getKey(), "a")||Objects.equals(input.getKey(), "s")||Objects.equals(input.getKey(), "d")){
                                    settingsManager.setSetting(currentSetting, settingsManager.getSettingStringValue(currentSetting).equals("true") ? "false" : "true");
                                }
                                break;
                            case STRING:
                                //TODO: handle the string cases with the enums
                                break;
                        }

                    }
                    break;
                case LEFT_CLICK:
                    for (Buttons button : Buttons.values()){
                        if (CollisionUtil.RectPointCollision(button.getRectangle(), input.getX(), input.getY())){
                            if (currentSection == Buttons.EXIT && button == Buttons.EXIT){
                                exit = true;
                                break;
                            }
                            currentSection = button;
                            break;
                        }
                    }
                    switch(currentSection){
                        case AUDIO:
                            for (AudioButtons button : AudioButtons.values()){
                                if (CollisionUtil.RectPointCollision(button.getRectangle(), input.getX(), input.getY())){
                                    currentAudioSetting = button;
                                    break;
                                }
                            }
                            break;
                        case GRAPHICS:
                            for (GraphicsButtons button : GraphicsButtons.values()){
                                if (CollisionUtil.RectPointCollision(button.getRectangle(), input.getX(), input.getY())){
                                    currentGraphicsSetting = button;
                                    break;
                                }
                            }
                            break;
                    }
                    break;
                case SCROLL:
                    switch (currentSection) {
                        case GRAPHICS:
                            currentGraphicsSetting = GraphicsButtons.values()[((currentGraphicsSetting.ordinal() + input.getScroll()) >= 0 ? (currentGraphicsSetting.ordinal() + input.getScroll()) : (GraphicsButtons.values().length + (currentGraphicsSetting.ordinal() + input.getScroll()))) % GraphicsButtons.values().length];
                            break;
                        case AUDIO:
                            currentAudioSetting = AudioButtons.values()[((currentAudioSetting.ordinal() + input.getScroll()) >= 0 ? (currentAudioSetting.ordinal() + input.getScroll()) : (AudioButtons.values().length + (currentAudioSetting.ordinal() + input.getScroll()))) % AudioButtons.values().length];
                            break;
                    }
            }
        }

    }

    public void draw() {
        drawUtil.setColor(75, 75, 75);
        drawUtil.fillRect(0, 0, 1920, 100);
        drawUtil.setThickness(5);
        for (Buttons button : Buttons.values()){
            if (button == currentSection){
                drawUtil.setColor(0, 255, 255);
            } else{
                drawUtil.setColor(0, 150, 255);
            }
            drawUtil.drawRect(button.getRectangle());
            drawUtil.drawString(button.getRectangle().getMinX() + button.getRectangle().getWidth()/2, 50, button.getName(), 20, Fonts.DEFAULT);
        }
        switch(currentSection){
            case GRAPHICS:
                for (GraphicsButtons button : GraphicsButtons.values()){
                    if (button == currentGraphicsSetting){
                        drawUtil.setColor(0, 255, 255);
                    } else{
                        drawUtil.setColor(0, 150, 255);
                    }
                    drawUtil.drawRect(button.getRectangle());
                    drawUtil.drawString(button.getRectangle().getMinX() + button.getRectangle().getWidth()/5, button.getRectangle().getMinY() + button.getRectangle().getHeight()/2, button.getName(), 20, Fonts.DEFAULT);
                    drawUtil.drawString(button.getRectangle().getMinX() + (button.getRectangle().getWidth()/5)*4, button.getRectangle().getMinY() + button.getRectangle().getHeight()/2, settingsManager.getSettingStringValue(button.getSetting().getId()), 20, Fonts.DEFAULT);
                }
                break;
            case AUDIO:
                for (AudioButtons button : AudioButtons.values()){
                    if (button == currentAudioSetting){
                        drawUtil.setColor(0, 255, 255);
                    } else{
                        drawUtil.setColor(0, 150, 255);
                    }
                    drawUtil.drawRect(button.getRectangle());
                    drawUtil.drawString(button.getRectangle().getMinX() + button.getRectangle().getWidth()/5, button.getRectangle().getMinY() + button.getRectangle().getHeight()/2, button.getName(), 20, Fonts.DEFAULT);
                    drawUtil.drawString(button.getRectangle().getMinX() + (button.getRectangle().getWidth()/5)*4, button.getRectangle().getMinY() + button.getRectangle().getHeight()/2, settingsManager.getSettingStringValue(button.getSetting().getId()), 20, Fonts.DEFAULT);
                }
                break;
        }
    }
}