package com.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import java.util.HashMap;
import java.util.Map;

public enum Fonts {
    DEFAULT("core/resources/fonts/SpaceMono-Regular.ttf");

    private final FreeTypeFontGenerator generator;
    private final FreeTypeFontParameter parameter;
    private final Map<Integer, BitmapFont> fontCache = new HashMap<>();

    Fonts(String filePath) {
        this.generator = new FreeTypeFontGenerator(Gdx.files.internal(filePath));
        parameter = new FreeTypeFontParameter();
        parameter.magFilter = Texture.TextureFilter.Linear;
        parameter.minFilter = Texture.TextureFilter.Linear;
    }

    public BitmapFont getFont(int size) {
        if (fontCache.containsKey(size)) {
            return fontCache.get(size);
        }
        size = Math.max(1, Math.min(size, 256));

        parameter.size = size;
        BitmapFont newFont = generator.generateFont(parameter);

        fontCache.put(size, newFont);
        return newFont;
    }

    public void dispose() {
        generator.dispose();
        for (BitmapFont font : fontCache.values()) {
            font.dispose();
        }
        fontCache.clear();
    }
    public static void clear(){
        for (Fonts font : Fonts.values()){
            font.fontCache.clear();
        }
    }
}
