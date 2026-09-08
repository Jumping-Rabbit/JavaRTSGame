package com.game.settings;

public enum Settings {
    TARGET_FPS(SettingTypes.INTEGER, "targetFPS", SettingSections.GRAPHICS),
    MONITOR_NUM(SettingTypes.INTEGER, "monitorNum", SettingSections.GRAPHICS),
    DISPLAY_MODES(SettingTypes.ENUM, "displayMode", SettingSections.GRAPHICS),
    MASTER_VOLUME(SettingTypes.INTEGER, "masterVolume", SettingSections.AUDIO),
    BGM_VOLUME(SettingTypes.INTEGER, "BGMVolume", SettingSections.AUDIO),
    SFX_VOLUME(SettingTypes.INTEGER, "SFXVolume", SettingSections.AUDIO),
    ANTIALIASING(SettingTypes.BOOLEAN, "antialiasing", SettingSections.GRAPHICS),
    GRAPHICS_QUALITY(SettingTypes.ENUM, "graphicsQuality", SettingSections.GRAPHICS);

    private final SettingTypes type;
    private final String id;
    private final SettingSections settingSections;

    Settings(SettingTypes type, String id, SettingSections settingSections) {
        this.type = type;
        this.id = id;
        this.settingSections = settingSections;
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

    public SettingSections getSettingSections(){
        return settingSections;
    }
}
