package com.game.settings.settingsEnums;

public enum DisplayModes implements SettingsEnums{
    WINDOWED("windowed"),
    WINDOWED_FULLSCREEN("windowed fullscreen"),
    FULLSCREEN("fullscreen");

    private final String string;

    DisplayModes(String string) {
        this.string = string;
    }

    @Override
    public String getString() {
        return string;
    }

    public static DisplayModes fromValue(String value) {
        return SettingsEnums.fromValue(DisplayModes.class, value);
    }
}
