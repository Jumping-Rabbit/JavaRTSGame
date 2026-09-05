package com.game.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.game.GameCore;

public class Lwjgl3Launcher {

    public static void main(String[] args) {
        new Lwjgl3Application(new GameCore(), getDefaultConfiguration());
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("javaGame");
        configuration.setWindowedMode(800, 500);
        configuration.setForegroundFPS(540);
        configuration.setIdleFPS(540);
        configuration.useVsync(false);
        configuration.setInitialVisible(true);
        return configuration;
    }
}
