package com.game.utils;

public enum LogType {
    PERFORMANCE("[PERFORMANCE] "),
    ERROR("[ERROR] "),
    EVENT("[EVENT] ");
    private String string;

    LogType(String s) {
        string = s;
    }

    public String getString() {
        return string;
    }
}
