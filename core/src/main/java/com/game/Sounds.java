package com.game;

import games.rednblack.miniaudio.MASound;
import games.rednblack.miniaudio.MiniAudio;

public enum Sounds {
    CLICK("core/resources/sounds/soundEffects/click.mp3");

    private final String location;
    private static MiniAudio miniAudio;
    private MASound sound;

    Sounds(String location) {
        this.location = location;
    }
    
    public static void loadAll(MiniAudio miniAudio) {
        Sounds.miniAudio = miniAudio;
        for (Sounds sounds : Sounds.values()){
            sounds.sound = miniAudio.createSound(sounds.location);
        }
    }


    public void play() {
        if (miniAudio != null) {
            miniAudio.playSound(this.location);
        }
    }

    public static void disposeAll() {
        miniAudio = null;
    }
    public static void setVolume(float volume){
        for (Sounds sounds : Sounds.values()){
            sounds.sound.setVolume(volume);
        }
    }
}
