package utils;

import game.screen.LoadingScreen;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.*;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import org.fxyz3d.importers.obj.ObjImporter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumMap;
import java.util.stream.Stream;

public class ModelLoaderUtil {
    private static double scale = 2.0;
    private static double imageSize = 1024;
    private static double halfImageSize = imageSize/2;
    public static double getImageSize(){
        return imageSize;
    }
    public static double getHalfImageSize(){
        return halfImageSize;
    }
    static EnumMap<Models, WritableImage[]> calculateModelImages(LoadingScreen loadingScreen){
        EnumMap<Models, WritableImage[]> modelsMap = new EnumMap<>(Models.class);
        Path root = Paths.get("resources/models");
        try (Stream<Path> paths = Files.walk(root)) {
            paths.filter(p -> p.toString().toLowerCase().endsWith(".obj"))
            .forEach(sourcePath -> {
                File file = new File(sourcePath.toUri());
                Node model = loadMesh(file);
                Models modelKey = Models.fromValue(file.getName().replace(".obj", ""));
                loadingScreen.addText("loading: " + modelKey);
                modelsMap.put(modelKey, getSnapshots(model, loadingScreen));
                setModelSizes(model, modelKey);
//                loadingScreen.increment();
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        return modelsMap;
    }
    private static Node loadMesh(File file) {
        try {
            ObjImporter importer = new ObjImporter();
            return importer.load(file.toURI().toURL()).getRoot();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void setModelSizes(Node model, Models modelKey){
//        model.setScaleX(scale);
//        model.setScaleY(scale);
//        model.setScaleZ(scale);
        model.getTransforms().clear();
        Bounds bounds = model.getBoundsInLocal();
        double halfWidth = StrictMath.min(StrictMath.abs(bounds.getMinX()-bounds.getCenterX()), StrictMath.abs(bounds.getMaxX()-bounds.getCenterX()));
        double halfHeight = StrictMath.min(StrictMath.abs(bounds.getMinY()-bounds.getCenterY()), StrictMath.abs(bounds.getMaxY()-bounds.getCenterY()));
        modelKey.setWidth(halfWidth*2);
        modelKey.setHeight(halfHeight*2);
        modelKey.setHalfWidth(halfWidth);
        modelKey.setHalfHeight(halfHeight);
        modelKey.setScaledWidth(NumUtil.DTL(halfWidth*2));
        modelKey.setScaledHeight(NumUtil.DTL(halfHeight*2));
        modelKey.setScaledHalfWidth(NumUtil.DTL(halfWidth));
        modelKey.setScaledHalfHeight(NumUtil.DTL(halfHeight));
    }
    private static WritableImage[] getSnapshots(Node model, LoadingScreen loadingScreen) {
        model.setScaleX(scale*2);
        model.setScaleY(scale*2);
        model.setScaleZ(scale*2);
        Rotate xTilt = new Rotate(35, Rotate.X_AXIS);
        Rotate ySpin = new Rotate(0, Rotate.Y_AXIS);
        model.getTransforms().addAll(xTilt, ySpin);
        PointLight light = new PointLight(Color.WHITE);
        light.setTranslateZ(-1000);
        light.setTranslateY(-1000);
        Group root3D = new Group(model, new AmbientLight(Color.color(0.5, 0.5, 0.5)), light);
        SnapshotParameters params = new SnapshotParameters();
        params.setCamera(new ParallelCamera());
        params.setFill(Color.TRANSPARENT);
//        params.setFill(Color.WHITE);
        params.setDepthBuffer(true);
        params.setViewport(new Rectangle2D(-imageSize/2, -imageSize/2, imageSize, imageSize));

        WritableImage[] images = new WritableImage[36];

        for (int i = 0; i < 36; i++) {
            final int index = i;
            ySpin.setAngle(index * 10);

            // We use a latch to force the background thread to wait for the UI snapshot
            java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(1);

            Platform.runLater(() -> {
                try {
                    images[index] = root3D.snapshot(params, null);
                    loadingScreen.increment();
                    loadingScreen.draw();
                } finally {
                    latch.countDown();
                }
            });

            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return images;
    }
}
