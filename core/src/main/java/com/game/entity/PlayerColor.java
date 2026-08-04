package com.game.entity;

import java.awt.*;

public enum PlayerColor {
    RED(new Color(255, 0, 0), 0),
    BLUE(new Color(0, 0, 255), 240),
    NONE(new Color(0, 0, 0), 0);
    private final Color color;
    private final float hue;

    PlayerColor(Color color, float hue) {
        this.color = color;
        this.hue = hue;
    }

    public Color getColor() {
        return color;
    }

    public float getHue() {
        return hue;
    }

    public static PlayerColor fromValue(String value) {
        for (PlayerColor playerColor : PlayerColor.values()) {
            if (value.equalsIgnoreCase(playerColor.name())) {
                return playerColor;
            }
        }
        return null;
    }
}
