package game;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import javax.sound.sampled.Clip;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import static java.lang.StrictMath.random;


public class SoundManager{
    private static int bgmNum;
    private static String bgmName;

    private static double masterVolume = 0;
    private static double bgmVolume = 0;
    private static double sfxVolume = 0;
    private static Thread soundThread;
    private static ArrayList<String> bgms = new ArrayList<>();
    private static MediaPlayer bgmPlayer = null;
    private static AtomicBoolean newBGM = new AtomicBoolean(false);
    static{
        Path root = Paths.get("resources/sounds/bgm");
        try (Stream<Path> paths = Files.walk(root)) {
            paths.filter(Files::isRegularFile)
                    .forEach(sourcePath -> {
                        String pathString = sourcePath.toAbsolutePath().toString();
                        bgms.add(pathString);
                        if (pathString.contains("geometry dash")) {
                            for (int i = 0; i < 4; i++) {
                                bgms.add(pathString);
                            }
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static void setVolume(){
        if (bgmPlayer != null){
            bgmPlayer.setVolume(bgmVolume*masterVolume);
        }
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

    public static void newBGM(){
        newBGM.set(true);
    }

    public static String getBgmName(){
        return bgmName;
    }

    private static void playBGM(){

        while (soundThread != null){
            newBGM.set(false);
            bgmNum = (int)(random()*bgms.size());
            bgmPlayer = new MediaPlayer(new Media(new File(bgms.get(bgmNum)).toURI().toString()));
            bgmPlayer.setVolume(bgmVolume*masterVolume);
            bgmPlayer.play();
            bgmName = Paths.get(bgms.get(bgmNum)).getFileName().toString().replaceFirst("[.][^.]+$", "");
            try {
                for (int i = 0; i < 10; i++){
                    if (newBGM.get()){
                        break;
                    }
                    Thread.sleep(50);
                }
                while (bgmPlayer.getStatus()!=MediaPlayer.Status.HALTED){
                    Thread.sleep(100);
                    if (newBGM.get()){
                        bgmPlayer.stop();
                        break;
                    }
                }
                if (!newBGM.get()){
                    Thread.sleep(3000);
                }

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
