package game;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import javax.sound.sampled.Clip;
import java.io.File;
import java.net.MalformedURLException;

import java.io.File;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.StrictMath.random;


public class SoundManager{
    private static double masterVolume = 0;
    private static double bgmVolume = 0;
    private static double sfxVolume = 0;
    private static Thread soundThread;
    private static final String[] bgms = {
            "/sounds/bgm/Heavens Devils.mp3",
            "/sounds/bgm/The Golden Armada.mp3",
            "/sounds/bgm/Wings Of Liberty.mp3"
    };
    private static MediaPlayer bgmPlayer = new MediaPlayer(new Media(SoundManager.class.getResource("/sounds/bgm/Heavens Devils.mp3").toString()));

    private static void setVolume(){
        bgmPlayer.setVolume(bgmVolume*masterVolume);
        for (Sounds sounds : Sounds.values()){
            sounds.getAudioClip().setVolume(masterVolume*sfxVolume);
        }
    }
    public static void setMasterVolume(double volume){
        masterVolume = volume/100d;
        setVolume();
    }
    public static void setBGMVolume(double volume){
        bgmVolume = volume/100d;
        setVolume();
    }
    public static void setSFXVolume(double volume){
        sfxVolume = volume/100d;
        setVolume();
    }

    public static void startBGM(){
        soundThread = new Thread(SoundManager::playBGM);
        soundThread.start();
    }

    private static void playBGM(){

        int bgmNum;
        while (soundThread != null){
            bgmNum = (int)(random()*3);
            bgmPlayer = new MediaPlayer(new Media(SoundManager.class.getResource(bgms[bgmNum]).toString()));
            bgmPlayer.setVolume(bgmVolume*masterVolume);
            bgmPlayer.play();
            try {
                while (bgmPlayer.getStatus()!=MediaPlayer.Status.STOPPED){
                    Thread.sleep(50);
                }
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
