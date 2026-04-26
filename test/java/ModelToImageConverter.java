import game.entity.Entity;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Rectangle2D;
import javafx.scene.*;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import org.fxyz3d.importers.obj.ObjImporter;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class ModelToImageConverter {

    enum entityType{
        UNIT,
        BUILDING;
    }
    private static final boolean DEBUG_PIVOT = false;
    private static final double IMAGE_SIZE = 512;
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static void main(String[] args) {
        Platform.startup(() -> {
        });
        Path outputRoot = Paths.get("resources/models");
        System.out.println("Cleaning up old .png files...");
        clearExistingFiles(outputRoot, ".png");
        process("test/resources/models/unit", entityType.UNIT);
        process("test/resources/models/building", entityType.BUILDING);
        System.out.println("All conversions complete.");
        Platform.exit();
        System.exit(0);
    }
    public static void process(String dir, entityType type){
        Path root = Paths.get(dir);


        try (Stream<Path> paths = Files.walk(root)) {
            paths.filter(p -> p.toString().toLowerCase().endsWith(".obj"))
                    .toList()
                    .parallelStream()
                    .forEach(sourcePath -> {
                        File objFile = sourcePath.toFile();
                        String modelName = objFile.getName().replace(".obj", "");

                        System.out.println("Processing Model: " + modelName);

                        Node model = loadMesh(objFile);
                        if (model != null) {
                            WritableImage[] images = getSnapshots(model, type);
                            processModelParallel(images, modelName);
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void processModelParallel(WritableImage[] images, String modelName) {
        File dir = new File("resources/models/" + modelName);
        dir.mkdirs();

        java.util.stream.IntStream.range(0, images.length).parallel().forEach(i -> {
            File outputPng = new File(dir, modelName + "_" + i + ".png");
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(images[i], null), "png", outputPng);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private static Node loadMesh(File file) {
        ObjImporter importer = new ObjImporter();
        try {
            return importer.load(file.toURI().toURL()).getRoot();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static WritableImage[] getSnapshots(Node model, entityType type) {
        model.getTransforms().clear();

        Rotate ySpin = new Rotate(0, Rotate.Y_AXIS);
        Rotate xTilt = new Rotate(45, Rotate.X_AXIS);
        model.getTransforms().addAll(xTilt, ySpin);

        AmbientLight ambient = new AmbientLight(Color.WHITE);
        Group root3D = new Group(model, ambient);

        ParallelCamera camera = new ParallelCamera();
        camera.setNearClip(0.1);
        camera.setFarClip(100000.0);

        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        params.setCamera(camera);
        params.setDepthBuffer(true);
        params.setViewport(new Rectangle2D(-IMAGE_SIZE / 2, -IMAGE_SIZE / 2, IMAGE_SIZE, IMAGE_SIZE));
        if (type == entityType.UNIT){
            WritableImage[] images = new WritableImage[16];
            for (int i = 0; i < 16; i++) {
                final int index = i;
                ySpin.setAngle(index * 22.5);

                java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(1);
                Platform.runLater(() -> {
                    try {
                        WritableImage raw = root3D.snapshot(params, new WritableImage((int) IMAGE_SIZE, (int) IMAGE_SIZE));
                        images[index] = raw;//trim(raw, (int) (IMAGE_SIZE / 2), (int) (IMAGE_SIZE / 2));
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
                    WritableImage raw = root3D.snapshot(params, new WritableImage((int) IMAGE_SIZE, (int) IMAGE_SIZE));
                    images[0] = raw;//trim(raw, (int) (IMAGE_SIZE / 2), (int) (IMAGE_SIZE / 2));
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