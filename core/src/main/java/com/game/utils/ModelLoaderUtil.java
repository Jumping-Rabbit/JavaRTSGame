package com.game.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.Model;
import com.game.Models;
import com.game.gameWindow.LoadingScreen;
import net.mgsx.gltf.loaders.glb.GLBLoader;
import net.mgsx.gltf.scene3d.scene.SceneAsset;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Stream;

public class ModelLoaderUtil {

    public static void loadModels(LoadingScreen loadingScreen){
        Path root = Paths.get("core/resources/models");

        try (Stream<Path> paths = Files.list(root)) {
            paths.filter(Files::isRegularFile)
                .forEach(sourcePath -> {
//                    System.out.println(sourcePath.toFile().getName().split("\\.")[0]);
                    Models modelKey = Models.fromValue(sourcePath.toFile().getName().split("\\.")[0]);
//                    System.out.println(modelKey);

                    FileHandle fileHandle = Gdx.files.local(sourcePath.toString());

                    CountDownLatch latch = new CountDownLatch(1);
                    Gdx.app.postRunnable(() -> {
                        try {
                            SceneAsset sceneAsset = new GLBLoader().load(fileHandle);
                            Model model = sceneAsset.scene.model;
                            Models.setModel(modelKey, model);
                        } catch (Exception e) {
                            LoggerUtil.log(e);
                            latch.countDown();
                        } finally {
                            latch.countDown();
                        }
                    });
                    try {
                        latch.await();
                    } catch (InterruptedException e) {
                        LoggerUtil.log(e);
                    }
                    loadingScreen.increment();
                });
        } catch (IOException e) {
            LoggerUtil.log(e);
        }
    }



//    private static final float imageSize = 512;
//    private static final float halfImageSize = imageSize / 2;
//
//    private static final ObjectMapper objectMapper = new ObjectMapper();
//
//    public static float getImageSize() {
//        return imageSize;
//    }
//
//    public static float getHalfImageSize() {
//        return halfImageSize;
//    }
//
//    static EnumMap<Models, WritableImage[]> calculateModelImages(LoadingScreen loadingScreen, int resolution) {
//        EnumMap<Models, WritableImage[]> modelsMap = new EnumMap<>(Models.class);
//        for (Models modelKey : Models.values()) {
//            loadingScreen.addText("Loading Model: " + modelKey);
//            System.out.println("Loading Model: " + modelKey);
//            modelsMap.put(modelKey, loadPngs(modelKey.toString(), loadingScreen, modelKey.getModelType(), resolution));
//        }
//        return modelsMap;
//    }
//
//    static EnumMap<Models, WritableImage> calculateModelImage(LoadingScreen loadingScreen, int resolution) {
//        EnumMap<Models, WritableImage> modelsMap = new EnumMap<>(Models.class);
//        for (Models modelKey : Models.values()) {
//            loadingScreen.addText("Loading Image: " + modelKey);
//            modelsMap.put(modelKey, loadImage(modelKey.toString(), loadingScreen, resolution));
//        }
//        return modelsMap;
//    }
//
//    private static WritableImage[] loadPngs(String modelName, LoadingScreen loadingScreen, Models.ModelType modelType, int resolution) {
//        if (modelType == Models.ModelType.UNIT) {
//            WritableImage[] images = new WritableImage[16];
//            for (int i = 0; i < 16; i++) {
//                loadPng(i, modelName, images, loadingScreen, resolution);
//            }
//            return images;
//        } else {
//            WritableImage[] images = new WritableImage[1];
//            loadPng(0, modelName, images, loadingScreen, resolution);
//            return images;
//        }
//    }
//
//    private static void loadPng(int i, String modelName, WritableImage[] images, LoadingScreen loadingScreen, int resolution) {
//        File pngFile = new File("core/resources/models/" + modelName + "/" + roundResolution(resolution) + "/" + modelName + "_" + i + ".png");
//        CountDownLatch latch = new CountDownLatch(1);
//        Platform.runLater(() -> {
//            try {
//                Image src = new Image(pngFile.toURI().toString(), false);
//                images[i] = new WritableImage(src.getPixelReader(), (int) src.getWidth(), (int) src.getHeight());
//                loadingScreen.increment();
//                loadingScreen.draw();
//            } catch (Exception e) {
//                LoggerUtil.log(e);
//            } finally {
//                latch.countDown();
//            }
//        });
//        try {
//            latch.await();
//        } catch (InterruptedException e) {
//            LoggerUtil.log(e);
//        }
//    }
//
//    private static WritableImage loadImage(String modelName, LoadingScreen loadingScreen, int resolution) {
//
//        File pngFile = new File("core/resources/models/" + modelName + "/" + roundResolution(resolution) + "/" + modelName + "Image.png");
//        CountDownLatch latch = new CountDownLatch(1);
//        final WritableImage[] image = new WritableImage[1];
//        Platform.runLater(() -> {
//            try {
//                Image src = new Image(pngFile.toURI().toString(), false);
//                image[0] = scaleImage(new WritableImage(src.getPixelReader(), (int) src.getWidth(), (int) src.getHeight()), resolution);
//                loadingScreen.increment();
//                loadingScreen.draw();
//            } finally {
//                latch.countDown();
//            }
//        });
//        try {
//            latch.await();
//        } catch (InterruptedException e) {
//            LoggerUtil.log(e);
//        }
//        return image[0];
//    }
//
//    private static int roundResolution(int resolution) {
//        if (resolution <= 64) return 64;
//        return 256;
//    }
//
//    public static WritableImage scaleImage(Image source, int imageSize) {
//        ImageView imageView = new ImageView(source);
//        imageView.setPreserveRatio(true);
//        imageView.setSmooth(false);
//        imageView.setFitWidth(imageSize);
//        imageView.setFitHeight(imageSize);
//        return imageView.snapshot(null, null);
//    }
////    public static WritableImage scaleImage(Image source, int imageSize) {
////        final WritableImage[] output = new WritableImage[1];
////        CountDownLatch latch = new CountDownLatch(1);
////        Platform.runLater(() -> {
////            try {
////                ImageView imageView = new ImageView(source);
////                imageView.setPreserveRatio(true);
////                imageView.setSmooth(false);
////                imageView.setFitWidth(imageSize);
////                imageView.setFitHeight(imageSize);
////                output[0] = imageView.snapshot(null, null);
////            } catch (Exception e) {
////                LoggerUtil.log(e);
////                throw new RuntimeException(e);
////            }finally {
////                latch.countDown();
////            }
////        });
////        try {
////            latch.await();
////        } catch (InterruptedException e) {
////            LoggerUtil.log(e);
////        }
////        return output[0];
////    }
}