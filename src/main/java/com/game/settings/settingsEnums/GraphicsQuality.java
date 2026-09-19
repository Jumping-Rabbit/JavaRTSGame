package com.game.settings.settingsEnums;

public enum GraphicsQuality implements SettingsEnums {
    LOW("low"),
    MEDIUM("medium"),
    HIGH("high");

    private final String string;

    GraphicsQuality(String string) {
        this.string = string;
    }

    @Override
    public String getString() {
        return string;
    }

    public static GraphicsQuality fromValue(String value) {
        return SettingsEnums.fromValue(GraphicsQuality.class, value);
    }
}
