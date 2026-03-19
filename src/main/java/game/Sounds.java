package game;

import javafx.scene.media.AudioClip;

import java.io.File;

public enum Sounds{
    CLICK("resources/sounds/soundEffects/click.mp3");
    private final AudioClip audioClip;
    Sounds(String location){
        this.audioClip = new AudioClip(new File(location).toURI().toString());
    }
    public void play(){
        audioClip.play();
    }
    public AudioClip getAudioClip(){
        return audioClip;
    }
}
