package utils;

import game.screen.LoadingScreen;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.EnumMap;
import java.util.concurrent.CountDownLatch;

public class ModelLoaderUtil {
    private static final double imageSize = 1024;
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
            loadingScreen.addText("Loading Models: " + modelKey);
            loadModelSize(modelKey.toString());
            modelsMap.put(modelKey, loadPng(modelKey.toString(), loadingScreen));
        }
        return modelsMap;
    }

    private static void loadModelSize(String modelName) {
        File configFile = new File("resources/models/" + modelName + "/modelConfig.json");

        JsonNode root = objectMapper.readTree(configFile);
        double width = root.path("width").asInt(0);
        double boundingOffset = root.path("boundingOffset").asInt(0);
        Models model = Models.valueOf(modelName);
        model.setWidth(width);
        model.setHalfWidth(width/2);
        model.setScaledWidth(NumUtil.DTL(width));
        model.setScaledHalfWidth(NumUtil.DTL(width / 2));
        model.setBoundingRadius(width/2*1.41*1.2);
        model.setBoundingDiameter(width*1.41*1.2);
        model.setBoundingRadiusScaled((long)(NumUtil.DTL(width/2*1.41)*1.2));
        model.setBoundingDiameterScaled((long)(NumUtil.DTL(width*1.41)*1.2));
        model.setBoundingDiff((width/2*1.41)-(width/2)*1.2);
        model.setBoundingDiffScaled((long)(NumUtil.DTL((width/2*1.41)-(width/2))*1.2));
        model.setBoundingOffset(boundingOffset*1.2);
        model.setBoundingOffsetScaled((long)(NumUtil.DTL(boundingOffset)*1.2));
    }

    private static WritableImage[] loadPng(String modelName, LoadingScreen loadingScreen) {
        WritableImage[] images = new WritableImage[16];
        for (int i = 0; i < 16; i++) {
            File pngFile = new File("resources/models/" + modelName + "/" + modelName + "_" + i + ".png");
            CountDownLatch latch = new CountDownLatch(1);
            int finalI = i;
            Platform.runLater(() -> {
                try {
                    Image src = new Image(pngFile.toURI().toString(), false);
                    images[finalI] = new WritableImage(src.getPixelReader(), (int) src.getWidth(), (int) src.getHeight());
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
        return images;
    }
}