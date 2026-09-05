package com.game.settings.settingsEnums;

public interface SettingsEnums {
    String getString();

    static <T extends Enum<T> & SettingsEnums> T fromValue(Class<T> enumClass, String value) {
        if (value == null) {
            return null;
        }
        for (T constant : enumClass.getEnumConstants()) {
            if (constant.getString().equalsIgnoreCase(value)) {
                return constant;
            }
        }
        return null;
    }
}

