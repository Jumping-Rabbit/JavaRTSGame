package com.game;

import com.game.utils.LoggerUtil;
import games.rednblack.miniaudio.MiniAudio;
import games.rednblack.miniaudio.MASound;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;

import static java.lang.Math.random;

public class SoundManager {
    private static MiniAudio miniAudio;
    private static MASound bgmPlayer = null;

    private static int bgmNum;
    private static String bgmName;
    private static float masterVolume = 1.0f; // Scale: 0.0f to 1.0f
    private static float bgmVolume = 1.0f;
    private static float sfxVolume = 1.0f;

    private static final ArrayList<String> bgms = new ArrayList<>();

    public static void init() {
        miniAudio = new MiniAudio();
        Sounds.loadAll(miniAudio);

        Path root = Paths.get("core/resources/sounds/bgm");
        try (Stream<Path> paths = Files.walk(root)) {
            paths.filter(Files::isRegularFile)
                    .forEach(sourcePath -> {
                        String pathString = sourcePath.toAbsolutePath().toString();
                        bgms.add(pathString);
                        if (pathString.contains("geometry dash")) {
                            for (int i = 0; i < 3; i++) {
                                bgms.add(pathString);
                            }
                        }
                    });
        } catch (IOException e) {
            LoggerUtil.log(e);
        }
    }

    private static void updateBGMVolume() {
        if (bgmPlayer != null) {
            bgmPlayer.setVolume(bgmVolume * masterVolume);
        }
    }

    public static void setMasterVolume(float volume) {
        masterVolume = volume / 100f;
        updateBGMVolume();
        updateSFXVolume();
    }

    public static void setBGMVolume(float volume) {
        bgmVolume = volume / 100f;
        updateBGMVolume();
    }

    public static void setSFXVolume(float volume) {
        sfxVolume = volume / 100f;
        updateSFXVolume();
    }

    private static void updateSFXVolume() {
        Sounds.setVolume(masterVolume * sfxVolume);
    }

    public static void startBGM() {
        if (bgms.isEmpty()) return;
        playNextBGM();
    }

    public static void newBGM() {
        playNextBGM();
    }

    public static String getBgmName() {
        return bgmName;
    }

    private static void playNextBGM() {
        if (bgmPlayer != null) {
            bgmPlayer.stop();
            bgmPlayer.dispose();
        }

        bgmNum = (int) (random() * bgms.size());
        String currentPath = bgms.get(bgmNum);
//        bgmName = Paths.get(currentPath).getFileName().toString().replaceFirst("[.][^.]+$", "");
        bgmName = Paths.get(currentPath).getFileName().toString().replaceAll(".mp3|.mpeg", "");

        try {
            bgmPlayer = miniAudio.createSound(currentPath);
            bgmPlayer.setVolume(bgmVolume * masterVolume);

            bgmPlayer.play();
        } catch (Exception e) {
            LoggerUtil.log(e);
        }
    }

    public static void dispose() {
        if (bgmPlayer != null) {
            bgmPlayer.dispose();
        }
        if (miniAudio != null) {
            miniAudio.dispose();
        }
    }
}
