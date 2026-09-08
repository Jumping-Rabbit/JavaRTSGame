package com.game.settings;

import com.badlogic.gdx.Gdx;

public class SettingsApplicator {
    protected static void setFPS(int value){
        Gdx.graphics.setForegroundFPS(value);
    }
    protected static void setMonitorNum(int value){
        //TODO: make sure i actually have a monitor there, and then make it do somethings
//        Lwjgl3Monitor targetMonitor = (Lwjgl3Monitor) monitors[1];
//        Lwjgl3Window window = ((Lwjgl3Graphics) Gdx.graphics).getWindow();
//        window.setPosition(targetMonitor.virtualX, targetMonitor.virtualY);
    }
    protected static void setDisplayModes(EnumSetting value){
//        switch ((DisplayModes)((EnumSetting)settingMap.get(SettingSections.GRAPHICS).get(Settings.DISPLAY_MODES)).getValue()){
//            case WINDOWED -> {
//                Gdx.graphics.setWindowedMode(0, 0);
//            }
//            case WINDOWED_FULLSCREEN -> {
//                Gdx.graphics.setUndecorated(true);
//                Gdx.graphics.
//            }
//            case FULLSCREEN -> {
//
//            }
//        }
    }
    protected static void setBGMVolume (){

    }
    protected static void setSFXVolume (){

    }
    protected static void setAntialiasing (){

    }
    protected static void setGraphicsQuality (){

    }
}
