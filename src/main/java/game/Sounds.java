package game;

import javafx.scene.media.AudioClip;

public enum Sounds{
    CLICK("/sounds/soundEffects/click.mp3");
    private final AudioClip audioClip;
    Sounds(String location){
        this.audioClip = new AudioClip(getClass().getResource(location).toString());
    }
    public void play(){
        audioClip.play();
    }
    public AudioClip getAudioClip(){
        return audioClip;
    }
}
