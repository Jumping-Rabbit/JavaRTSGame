package utils;

import game.screen.LoadingScreen;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.EnumMap;
import java.util.concurrent.CountDownLatch;

public class ModelLoaderUtil {
    private static final double imageSize = 512;
    private static final double halfImageSize = imageSize / 2;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static double getImageSize() {
        return imageSize;
    }

    public static double getHalfImageSize() {
        return halfImageSize;
    }

    static EnumMap<Models, WritableImage[]> calculateModelImages(LoadingScreen loadingScreen) {
        EnumMap<Models, WritableImage[]> modelsMap = new EnumMap<>(Models.class);
        for (Models modelKey : Models.values()) {
            loadingScreen.addText("Loading Model: " + modelKey);
            modelsMap.put(modelKey, loadPngs(modelKey.toString(), loadingScreen, modelKey.getModelType()));
        }
        return modelsMap;
    }
    static EnumMap<Models, WritableImage> calculateModelPicture(LoadingScreen loadingScreen) {
        EnumMap<Models, WritableImage> modelsMap = new EnumMap<>(Models.class);
        for (Models modelKey : Models.values()) {
            loadingScreen.addText("Loading Picture: " + modelKey);
            modelsMap.put(modelKey, loadPicture(modelKey.toString(), loadingScreen));
        }
        return modelsMap;
    }

    private static WritableImage[] loadPngs(String modelName, LoadingScreen loadingScreen, Models.ModelType modelType) {
        if (modelType == Models.ModelType.UNIT){
            WritableImage[] images = new WritableImage[16];
            for (int i = 0; i < 16; i++) {
                loadPng(i, modelName, images, loadingScreen);
            }
            return images;
        } else {
            WritableImage[] images = new WritableImage[1];
            loadPng(0, modelName, images, loadingScreen);
            return images;
        }
    }
    private static void loadPng(int i, String modelName, WritableImage[] images, LoadingScreen loadingScreen) {
        File pngFile = new File("resources/models/" + modelName + "/" + modelName + "_" + i + ".png");
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                Image src = new Image(pngFile.toURI().toString(), false);
                images[i] = new WritableImage(src.getPixelReader(), (int) src.getWidth(), (int) src.getHeight());
                loadingScreen.increment();
                loadingScreen.draw();
            } finally {
                latch.countDown();
            }
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            LoggerUtil.log(e);
        }
    }
    private static WritableImage loadPicture(String modelName, LoadingScreen loadingScreen){
        File pngFile = new File("resources/pictures/" + modelName + ".png");
        CountDownLatch latch = new CountDownLatch(1);
        final WritableImage[] image = new WritableImage[1];
        Platform.runLater(() -> {
            try {
                Image src = new Image(pngFile.toURI().toString(), false);
                image[0] = new WritableImage(src.getPixelReader(), (int) src.getWidth(), (int) src.getHeight());
                loadingScreen.increment();
                loadingScreen.draw();
            } finally {
                latch.countDown();
            }
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            LoggerUtil.log(e);
        }
        return image[0];
    }
}