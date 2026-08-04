package com.game.utils;

public class ModelLoaderUtil {
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