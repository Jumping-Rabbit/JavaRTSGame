package game.screen;

import game.Fonts;
import game.SettingsManager;
import inputHandler.*;
import javafx.geometry.Rectangle2D;
import utils.CollisionUtil;
import utils.DrawUtil;
import utils.StringAlignment;

public class Settings extends Screen {
    private boolean isEditing = false;
    private Buttons currentSection = Buttons.GRAPHICS;
    private GraphicsButtons currentGraphicsSetting = GraphicsButtons.GRAPHICS_QUALITY;
    private AudioButtons currentAudioSetting = AudioButtons.MASTER_VOLUME;
    private SettingsManager settingsManager;

    public Settings(SettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }

    public Settings(Settings settings) {
        isEditing = settings.isEditing;
        currentSection = settings.currentSection;
        currentGraphicsSetting = settings.currentGraphicsSetting;
        currentAudioSetting = settings.currentAudioSetting;
        exit = settings.exit;
        settingsManager = settings.settingsManager;
    }

    public void resetSelections() {
        isEditing = false;
        currentSection = Buttons.GRAPHICS;
        currentGraphicsSetting = GraphicsButtons.GRAPHICS_QUALITY;
        currentAudioSetting = AudioButtons.MASTER_VOLUME;
    }

    public Screen copy() {
        return new Settings(this);
    }

    private boolean isInt(String string) {
        try {
            Integer.parseInt(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public void updateOnFrame() {
        for (Input input : InputHandler.getInputs()) {
            switch (input.getInputType()) {
                case KEYPRESS:
                    if (isEditing) {
                        if (input.getAction() == Actions.BACK) {
                            isEditing = false;
                            break;
                        } else if (input.getAction() == Actions.CONFIRM) {
                            isEditing = false;
                            break;
                        }
                        String currentSetting = switch (currentSection) {
                            case GRAPHICS -> currentGraphicsSetting.getId();
                            case AUDIO -> currentAudioSetting.getId();
                            default -> "";
                        };
                        switch (SettingsManager.Settings.fromValue(currentSetting).getSettingType()) {
                            case INTEGER:
                                if (input.getKey() == Keys.BACKSPACE && !settingsManager.getSettingStringValue(currentSetting).isEmpty()) {
                                    settingsManager.setSetting(currentSetting, !settingsManager.getSettingStringValue(currentSetting).substring(0, String.valueOf(settingsManager.getSettingStringValue(currentSetting)).length() - 1).isEmpty() ? settingsManager.getSettingStringValue(currentSetting).substring(0, String.valueOf(settingsManager.getSettingStringValue(currentSetting)).length() - 1) : "0");
                                } else if (input.getKey().getType() == KeyType.NUMBER) {
                                    settingsManager.setSetting(currentSetting, settingsManager.getSettingStringValue(currentSetting) + input.getKey().getString());
                                }
                                break;
                            case BOOLEAN:
                                if (input.getAction() == Actions.UP || input.getAction() == Actions.DOWN || input.getAction() == Actions.LEFT || input.getAction() == Actions.RIGHT) {
                                    settingsManager.setSetting(currentSetting, settingsManager.getSettingStringValue(currentSetting).equals("true") ? "false" : "true");
                                }
                                break;
                            case STRING:
                                if (input.getAction() == Actions.UP || input.getAction() == Actions.DOWN || input.getAction() == Actions.LEFT || input.getAction() == Actions.RIGHT) {
                                    switch (currentSetting) {
                                        case "graphicsQuality":
                                            settingsManager.setSetting(currentSetting, SettingsManager.GraphicsQuality.values()[(SettingsManager.GraphicsQuality.fromValue(settingsManager.getSettingStringValue(currentSetting)).ordinal() + 1) % SettingsManager.GraphicsQuality.values().length].getString());
                                            break;
                                        case "displayMode":
                                            settingsManager.setSetting(currentSetting, SettingsManager.DisplayModes.values()[(SettingsManager.DisplayModes.fromValue(settingsManager.getSettingStringValue(currentSetting)).ordinal() + 1) % SettingsManager.DisplayModes.values().length].getString());
                                            break;
                                    }

                                }
                                break;
                        }
                        break;
                    }
                    if (input.getAction() == Actions.LEFT) {
                        Buttons oldButton = currentSection;
                        currentSection = Buttons.values()[(currentSection.ordinal() - 1) >= 0 ? (currentSection.ordinal() - 1) : (Buttons.values().length - 1)];
                        if (currentSection != oldButton) {
                            currentGraphicsSetting = GraphicsButtons.GRAPHICS_QUALITY;
                            currentAudioSetting = AudioButtons.MASTER_VOLUME;
                        }
                        break;
                    } else if (input.getAction() == Actions.RIGHT) {
                        Buttons oldButton = currentSection;
                        currentSection = Buttons.values()[(currentSection.ordinal() + 1) % Buttons.values().length];
                        if (currentSection != oldButton) {
                            currentGraphicsSetting = GraphicsButtons.GRAPHICS_QUALITY;
                            currentAudioSetting = AudioButtons.MASTER_VOLUME;
                        }
                    } else if (input.getAction() == Actions.UP) {
                        switch (currentSection) {
                            case GRAPHICS:
                                currentGraphicsSetting = GraphicsButtons.values()[(currentGraphicsSetting.ordinal() - 1) >= 0 ? (currentGraphicsSetting.ordinal() - 1) : (GraphicsButtons.values().length - 1)];
                                break;
                            case AUDIO:
                                currentAudioSetting = AudioButtons.values()[(currentAudioSetting.ordinal() - 1) >= 0 ? (currentAudioSetting.ordinal() - 1) : (AudioButtons.values().length - 1)];
                                break;
                        }
                    } else if (input.getAction() == Actions.DOWN) {
                        switch (currentSection) {
                            case GRAPHICS:
                                currentGraphicsSetting = GraphicsButtons.values()[(currentGraphicsSetting.ordinal() + 1) % GraphicsButtons.values().length];
                                break;
                            case AUDIO:
                                currentAudioSetting = AudioButtons.values()[(currentAudioSetting.ordinal() + 1) % AudioButtons.values().length];
                                break;
                        }
                    } else if (input.getAction() == Actions.BACK) {
                        exit = true;
                    } else if (input.getAction() == Actions.CONFIRM) {
                        if (currentSection == Buttons.EXIT) {
                            exit = true;
                            break;
                        }
                        isEditing = true;
                    }

                    break;
                case LEFT_CLICK:
                    for (Buttons button : Buttons.values()) {
                        if (CollisionUtil.RectPointCollision(button.getRectangle(), input.getX(), input.getY())) {
                            if (currentSection == Buttons.EXIT && button == Buttons.EXIT) {
                                exit = true;
                                break;
                            }
                            currentSection = button;
                            break;
                        }
                    }
                    boolean pressedButton = false;
                    switch (currentSection) {
                        case AUDIO:
                            AudioButtons lastAudioButton = currentAudioSetting;
                            for (AudioButtons button : AudioButtons.values()) {
                                if (CollisionUtil.RectPointCollision(button.getRectangle(), input.getX(), input.getY())) {
                                    currentAudioSetting = button;
                                    pressedButton = true;
                                    break;
                                }
                            }
                            if (pressedButton && lastAudioButton == currentAudioSetting) {
                                isEditing = !isEditing;
                            }
                            break;
                        case GRAPHICS:
                            GraphicsButtons lastGraphicsButton = currentGraphicsSetting;
                            for (GraphicsButtons button : GraphicsButtons.values()) {
                                if (CollisionUtil.RectPointCollision(button.getRectangle(), input.getX(), input.getY())) {
                                    currentGraphicsSetting = button;
                                    pressedButton = true;
                                    break;
                                }
                            }
                            if (pressedButton && lastGraphicsButton == currentGraphicsSetting) {
                                isEditing = !isEditing;
                            }
                            break;
                    }
                    if (isEditing && !pressedButton) {
                        isEditing = false;
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
        DrawUtil.setColor(0x4B4B4BFF);
        DrawUtil.fillRect(0, 0, 1920, 100, 0x4B4B4BFF);
        DrawUtil.setThickness(5);
        for (Buttons button : Buttons.values()) {
            if (button == currentSection) {
                DrawUtil.strokeRect(button.getRectangle(), 0x00FFFFFF, 5);
                DrawUtil.fillText(button.getName(), button.getRectangle().getMinX() + button.getRectangle().getWidth() / 2, 50, Fonts.DEFAULT, 40, StringAlignment.CENTER_MIDDLE, 0x00FFFFFF);
            } else {
                DrawUtil.strokeRect(button.getRectangle(), 0x0096FFFF, 5);
                DrawUtil.fillText(button.getName(), button.getRectangle().getMinX() + button.getRectangle().getWidth() / 2, 50, Fonts.DEFAULT, 40, StringAlignment.CENTER_MIDDLE, 0x0096FFFF);
            }

        }
        switch (currentSection) {
            case GRAPHICS:
                for (GraphicsButtons button : GraphicsButtons.values()) {
                    if (button == currentGraphicsSetting) {
                        DrawUtil.strokeRect(button.getRectangle(), 0x00FFFFFF, 5);
                        DrawUtil.fillText(button.getName(), button.getRectangle().getMinX() + button.getRectangle().getWidth() / 5, button.getRectangle().getMinY() + button.getRectangle().getHeight() / 2, Fonts.DEFAULT, 40, StringAlignment.CENTER_MIDDLE, 0x00FFFFFF);
                        DrawUtil.fillText(settingsManager.getSettingStringValue(button.getSetting().getId()), button.getRectangle().getMinX() + (button.getRectangle().getWidth() / 5) * 4, button.getRectangle().getMinY() + button.getRectangle().getHeight() / 2, Fonts.DEFAULT, 40, StringAlignment.CENTER_MIDDLE, 0x00FFFFFF);
                    } else {
                        DrawUtil.strokeRect(button.getRectangle(), 0x0096FFFF, 5);
                        DrawUtil.fillText(button.getName(), button.getRectangle().getMinX() + button.getRectangle().getWidth() / 5, button.getRectangle().getMinY() + button.getRectangle().getHeight() / 2, Fonts.DEFAULT, 40, StringAlignment.CENTER_MIDDLE, 0x0096FFFF);
                        DrawUtil.fillText(settingsManager.getSettingStringValue(button.getSetting().getId()), button.getRectangle().getMinX() + (button.getRectangle().getWidth() / 5) * 4, button.getRectangle().getMinY() + button.getRectangle().getHeight() / 2, Fonts.DEFAULT, 40, StringAlignment.CENTER_MIDDLE, 0x0096FFFF);
                    }

                }
                break;
            case AUDIO:
                for (AudioButtons button : AudioButtons.values()) {
                    if (button == currentAudioSetting) {
                        DrawUtil.strokeRect(button.getRectangle(), 0x00FFFFFF, 5);
                        DrawUtil.fillText(button.getName(), button.getRectangle().getMinX() + button.getRectangle().getWidth() / 5, button.getRectangle().getMinY() + button.getRectangle().getHeight() / 2, Fonts.DEFAULT, 40, StringAlignment.CENTER_MIDDLE, 0x00FFFFFF);
                        DrawUtil.fillText(settingsManager.getSettingStringValue(button.getSetting().getId()), button.getRectangle().getMinX() + (button.getRectangle().getWidth() / 5) * 4, button.getRectangle().getMinY() + button.getRectangle().getHeight() / 2, Fonts.DEFAULT, 40, StringAlignment.CENTER_MIDDLE, 0x00FFFFFF);
                    } else {
                        DrawUtil.strokeRect(button.getRectangle(), 0x0096FFFF, 5);
                        DrawUtil.fillText(button.getName(), button.getRectangle().getMinX() + button.getRectangle().getWidth() / 5, button.getRectangle().getMinY() + button.getRectangle().getHeight() / 2, Fonts.DEFAULT, 40, StringAlignment.CENTER_MIDDLE, 0x0096FFFF);
                        DrawUtil.fillText(settingsManager.getSettingStringValue(button.getSetting().getId()), button.getRectangle().getMinX() + (button.getRectangle().getWidth() / 5) * 4, button.getRectangle().getMinY() + button.getRectangle().getHeight() / 2, Fonts.DEFAULT, 40, StringAlignment.CENTER_MIDDLE, 0x0096FFFF);
                    }

                }
                break;
        }
        if (isEditing) {
            DrawUtil.fillRect(0, 0, 1920, 1080, 0x00000054);
            switch (currentSection) {
                case GRAPHICS:
                    DrawUtil.fillRect(currentGraphicsSetting.getRectangle(), 0x323232FF);
                    DrawUtil.strokeRect(currentGraphicsSetting.getRectangle(), 0x00FFFFFF, 5);
                    DrawUtil.fillText(currentGraphicsSetting.getName(), currentGraphicsSetting.getRectangle().getMinX() + currentGraphicsSetting.getRectangle().getWidth() / 5, currentGraphicsSetting.getRectangle().getMinY() + currentGraphicsSetting.getRectangle().getHeight() / 2, Fonts.DEFAULT, 40, StringAlignment.CENTER_MIDDLE, 0x00FFFFFF);
                    DrawUtil.fillText(settingsManager.getSettingStringValue(currentGraphicsSetting.getSetting().getId()), currentGraphicsSetting.getRectangle().getMinX() + (currentGraphicsSetting.getRectangle().getWidth() / 5) * 4, currentGraphicsSetting.getRectangle().getMinY() + currentGraphicsSetting.getRectangle().getHeight() / 2, Fonts.DEFAULT, 40, StringAlignment.CENTER_MIDDLE, 0x00FFFFFF);
                    break;
                case AUDIO:
                    DrawUtil.fillRect(currentAudioSetting.getRectangle(), 0x323232FF);
                    DrawUtil.strokeRect(currentAudioSetting.getRectangle(), 0x00FFFFFF, 5);
                    DrawUtil.fillText(currentAudioSetting.getName(), currentAudioSetting.getRectangle().getMinX() + currentAudioSetting.getRectangle().getWidth() / 5, currentAudioSetting.getRectangle().getMinY() + currentAudioSetting.getRectangle().getHeight() / 2, Fonts.DEFAULT, 40, StringAlignment.CENTER_MIDDLE, 0x00FFFFFF);
                    DrawUtil.fillText(settingsManager.getSettingStringValue(currentAudioSetting.getSetting().getId()), currentAudioSetting.getRectangle().getMinX() + (currentAudioSetting.getRectangle().getWidth() / 5) * 4, currentAudioSetting.getRectangle().getMinY() + currentAudioSetting.getRectangle().getHeight() / 2, Fonts.DEFAULT, 40, StringAlignment.CENTER_MIDDLE, 0x00FFFFFF);
                    break;
            }
        }
    }

    public enum Buttons {
        GRAPHICS(new Rectangle2D(0, 0, 860, 100), "graphics"),
        AUDIO(new Rectangle2D(860, 0, 860, 100), "audio"),
        EXIT(new Rectangle2D(1720, 0, 200, 100), "exit");

        private final Rectangle2D rectangle;
        private final String name;

        Buttons(Rectangle2D rectangle, String name) {
            this.rectangle = rectangle;
            this.name = name;
        }

        private String getName() {
            return name;
        }

        private Rectangle2D getRectangle() {
            return rectangle;
        }
    }

    public enum AudioButtons {
        MASTER_VOLUME(new Rectangle2D(50, 150, 1000, 100), "master volume", SettingsManager.Settings.MASTER_VOLUME, "masterVolume"),
        BGM_VOLUME(new Rectangle2D(50, 300, 1000, 100), "bgm volume", SettingsManager.Settings.BGM_VOLUME, "BGMVolume"),
        SFX_VOLUME(new Rectangle2D(50, 450, 1000, 100), "sfx volume", SettingsManager.Settings.SFX_VOLUME, "SFXVolume");

        private final Rectangle2D rectangle;
        private final String name;
        private final SettingsManager.Settings setting;
        private final String id;

        AudioButtons(Rectangle2D rectangle, String name, SettingsManager.Settings setting, String id) {
            this.rectangle = rectangle;
            this.name = name;
            this.setting = setting;
            this.id = id;
        }

        private String getName() {
            return name;
        }

        private Rectangle2D getRectangle() {
            return rectangle;
        }

        private SettingsManager.Settings getSetting() {
            return setting;
        }

        private String getId() {
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

        GraphicsButtons(Rectangle2D rectangle, String name, SettingsManager.Settings setting, String id) {
            this.rectangle = rectangle;
            this.name = name;
            this.setting = setting;
            this.id = id;
        }

        private SettingsManager.Settings getSetting() {
            return setting;
        }

        private String getName() {
            return name;
        }

        private Rectangle2D getRectangle() {
            return rectangle;
        }

        private String getId() {
            return id;
        }
    }
}