import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Rectangle2D;
import javafx.scene.*;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import org.fxyz3d.importers.obj.ObjImporter;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class ModelToImageConverter {

    enum EntityType {
        UNIT,
        BUILDING;
    }

    public static void main(String[] args) {
        Platform.startup(() -> {
        });
        Path outputRoot = Paths.get("resources/models");
        System.out.println("Cleaning up old .png files...");
        clearExistingFiles(outputRoot, ".png");
        processDir("test/resources/models/unit", EntityType.UNIT);
        processDir("test/resources/models/building", EntityType.BUILDING);
        System.out.println("All conversions complete.");
        Platform.exit();
        System.exit(0);
    }
    public static void processDir(String dir, EntityType entityType){
        process(dir, entityType, 64);
        process(dir, entityType, 256);
    }
    public static void process(String dir, EntityType type, int resolution){
        Path root = Paths.get(dir);
        try (Stream<Path> paths = Files.walk(root)) {
            paths.filter(p -> p.toString().toLowerCase().endsWith(".obj"))
                    .toList()
                    .parallelStream()
                    .forEach(sourcePath -> {
                        File objFile = sourcePath.toFile();
                        String modelName = objFile.getName().replace(".obj", "");

                        System.out.println("Processing Model: " + modelName + " : " + resolution);

                        Node model = loadMesh(objFile);
                        if (model != null) {
                            WritableImage[] images = getSnapshots(model, type, resolution);
                            processModelParallel(images, modelName, resolution);
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void processModelParallel(WritableImage[] images, String modelName, int resolution) {
        File dir = new File("resources/models/" + modelName);
        dir.mkdirs();
        File resDir = new File("resources/models/" + modelName +"/"+resolution);
        resDir.mkdirs();
        images = trim(images);
        WritableImage[] finalImages = images;
        java.util.stream.IntStream.range(0, images.length).parallel().forEach(i -> {
            File outputPng = new File(resDir, modelName + "_" + i + ".png");
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(finalImages[i], null), "png", outputPng);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        File outputPng = new File(resDir, modelName + "Image.png");
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(images[0], null), "png", outputPng);
        } catch (IOException e) {
            e.printStackTrace();
        }

//        File outputPng = new File(resDir, modelName + "Image" + ".png");
//        try {
//            ImageIO.write(SwingFXUtils.fromFXImage(trim(images[0]), null), "png", outputPng);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    private static Node loadMesh(File file) {
        ObjImporter importer = new ObjImporter();
        try {
            return importer.load(file.toURI().toURL()).getRoot();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static WritableImage[] getSnapshots(Node model, EntityType type, double resolution) {
        model.getTransforms().clear();

        Rotate ySpin = new Rotate(0, Rotate.Y_AXIS);
        Rotate xTilt = new Rotate(45, Rotate.X_AXIS);
        Scale scale;
        if (resolution == 256){
            scale = new Scale(2, 2, 2);
        } else {
            scale = new Scale(0.5, 0.5, 0.5);
        }
        model.getTransforms().addAll(xTilt, ySpin, scale);

        AmbientLight ambient = new AmbientLight(Color.TRANSPARENT);
        Group root3D = new Group(model, ambient);

        ParallelCamera camera = new ParallelCamera();
        camera.setNearClip(0.001);
        camera.setFarClip(100000.0);

        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        params.setCamera(camera);
        params.setDepthBuffer(true);
        params.setViewport(new Rectangle2D(-resolution / 2, -resolution / 2, resolution, resolution));
        if (type == EntityType.UNIT){
            WritableImage[] images = new WritableImage[16];
            for (int i = 0; i < 16; i++) {
                final int index = i;
                ySpin.setAngle(index * 22.5);

                java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(1);
                Platform.runLater(() -> {
                    try {
                        WritableImage raw = root3D.snapshot(params, new WritableImage((int) resolution, (int) resolution));
                        images[index] = raw;//trim(raw, (int) (resolution / 2), (int) (resolution / 2));
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
        } else {
            WritableImage[] images = new WritableImage[1];
            java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(1);
            Platform.runLater(() -> {
                try {
                    WritableImage raw = root3D.snapshot(params, new WritableImage((int) resolution, (int) resolution));
                    images[0] = raw;//trim(raw, (int) (resolution / 2), (int) (resolution / 2));
                } finally {
                    latch.countDown();
                }
            });
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return images;
        }

    }
    private static boolean checkRect(WritableImage image, int layer){
        PixelReader reader = image.getPixelReader();
        for (int x = layer; x < (int)image.getWidth() - layer; x++){
            if (((reader.getArgb(x, layer) >> 24) & 0xFF) > 0) return true;
        }
        for (int x = layer; x < (int)image.getWidth() - layer; x++){
            if (((reader.getArgb(x, (int)image.getHeight()-layer-1) >> 24) & 0xFF) > 0) return true;
        }
        for (int y = layer; y < (int)image.getHeight() - layer; y++){
            if (((reader.getArgb(layer, y) >> 24) & 0xFF) > 0) return true;
        }
        for (int y = layer; y < (int)image.getHeight() - layer; y++){
            if (((reader.getArgb((int)image.getWidth()-layer-1, y) >> 24) & 0xFF) > 0) return true;
        }
        return false;
    }

    private static WritableImage[] trim(WritableImage[] sources) {
        int w = (int) sources[0].getWidth();
        int h = (int) sources[0].getHeight();
        int size = 0;

        for (WritableImage image : sources){
            for (int i = 1; i < image.getWidth()/2; i++){
                if (checkRect(image, i)) {
                    if ((int)(image.getWidth())-(i-1)*2 > size){
                        size = (int)(image.getWidth())-(i-1)*2;
                    }
                    break;
                }
            }
        }
        int offset = (w-size)/2;
        WritableImage[] output = new WritableImage[sources.length];
        for (int i = 0; i < sources.length; i++){
            output[i] = new WritableImage(size, size);
            PixelReader reader = sources[i].getPixelReader();
            PixelWriter writer = output[i].getPixelWriter();
            for (int x = 0; x < size; x++){
                for (int y = 0; y < size; y++){
                    writer.setArgb(x, y, reader.getArgb(x+offset, y+offset));
                }
            }
        }
        return output;
    }

    private static void clearExistingFiles(Path outputPath, String extension) {
        if (!Files.exists(outputPath)) return;
        try (Stream<Path> stream = Files.walk(outputPath)) {
            stream.filter(Files::isRegularFile)
                    .filter(p -> p.toString().toLowerCase().endsWith(extension))
                    .forEach(p -> {
                        try {
                            Files.delete(p);
                        } catch (IOException e) {
                            System.err.println("Could not delete: " + p + " - " + e.getMessage());
                        }
                    });
        } catch (IOException e) {
            System.err.println("Error while cleaning output directory: " + e.getMessage());
        }
    }
}