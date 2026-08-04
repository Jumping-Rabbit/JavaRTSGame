package com.game.lwjgl3; // 1. Put it in the sub-package so it's clean!

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
//        configuration.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate + 1);
        configuration.setWindowedMode(800, 500);
        configuration.setForegroundFPS(240);
        configuration.setIdleFPS(240);
        configuration.useVsync(false);
        return configuration;
    }
}
