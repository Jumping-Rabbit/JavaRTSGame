package com.game.settings;

public enum Settings {
    TARGET_FPS(SettingTypes.INTEGER, "targetFPS"),
    MONITOR_NUM(SettingTypes.INTEGER, "monitorNum"),
    DISPLAY_MODES(SettingTypes.ENUM, "displayMode"),
    MASTER_VOLUME(SettingTypes.INTEGER, "masterVolume"),
    BGM_VOLUME(SettingTypes.INTEGER, "BGMVolume"),
    SFX_VOLUME(SettingTypes.INTEGER, "SFXVolume"),
    ANTIALIASING(SettingTypes.BOOLEAN, "antialiasing"),
    GRAPHICS_QUALITY(SettingTypes.ENUM, "graphicsQuality");

    private final SettingTypes type;
    private final String id;

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
