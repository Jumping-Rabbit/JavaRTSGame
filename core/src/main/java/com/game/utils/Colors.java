package com.game.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.IntMap;

public class Colors {
    private static final IntMap<Color> cache = new IntMap<>();

    public static Color fromHex(int hex) {
        Color color = cache.get(hex);
        if (color == null) {
            color = new Color();
            Color.rgba8888ToColor(color, hex);
            cache.put(hex, color);
        }
        return color;
    }
}
