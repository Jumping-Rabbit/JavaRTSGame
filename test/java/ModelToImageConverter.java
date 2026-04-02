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

    private static final boolean DEBUG_PIVOT = false;
    private static final double SCALE = 400d;
    private static final double IMAGE_SIZE = 1024;
    private static final int CAMERA_BIAS_X = -4;
    private static final int CAMERA_BIAS_Y = 0;
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static void main(String[] args) {
        Platform.startup(() -> {
        });

        Path root = Paths.get("test/resources/models");
        Path outputRoot = Paths.get("resources/models");

        System.out.println("Cleaning up old .png files...");
        clearExistingFiles(outputRoot, ".png");

        try (Stream<Path> paths = Files.walk(root)) {
            paths.filter(p -> p.toString().toLowerCase().endsWith(".obj"))
                    .toList()
                    .parallelStream()
                    .forEach(sourcePath -> {
                        File objFile = sourcePath.toFile();
                        String modelName = objFile.getName().replace(".obj", "");

                        System.out.println("Processing Model: " + modelName);

                        double[] pivot = loadPivotConfig(modelName);

                        Node model = loadMesh(objFile);
                        if (model != null) {
                            WritableImage[] images = getSnapshots(model, pivot[0], pivot[1]);
                            processModelParallel(images, modelName);
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("All conversions complete.");
        Platform.exit();
        System.exit(0);
    }

    private static double[] loadPivotConfig(String modelName) {
        File configFile = new File("resources/models/" + modelName + "/modelConfig.json");

        if (!configFile.exists()) {
            System.out.println("[" + modelName + "] No modelConfig.json found — using center pivot.");
            return new double[]{0, 0};
        }

        try {
            JsonNode root = MAPPER.readTree(configFile);
            double ox = root.path("pivotOffsetX").asDouble(0);
            double oy = root.path("pivotOffsetY").asDouble(0);
            System.out.println("[" + modelName + "] Pivot loaded: " + ox + ", " + oy);
            return new double[]{ox, oy};
        } catch (Exception e) {
            System.err.println("[" + modelName + "] Failed to read modelConfig.json: " + e.getMessage());
            return new double[]{0, 0};
        }
    }

    public static void processModelParallel(WritableImage[] images, String modelName) {
        File dir = new File("resources/models/" + modelName);
        dir.mkdirs();

        java.util.stream.IntStream.range(0, 16).parallel().forEach(i -> {
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

    private static WritableImage[] getSnapshots(Node model, double pivotOffsetX, double pivotOffsetY) {
        model.getTransforms().clear();

        model.setTranslateX(pivotOffsetX);
        model.setTranslateY(pivotOffsetY);
        model.setTranslateZ(0);

        Rotate ySpin = new Rotate(0, Rotate.Y_AXIS);
        Rotate xTilt = new Rotate(60, Rotate.X_AXIS);
        model.getTransforms().addAll(xTilt, ySpin);

        model.setScaleX(SCALE);
        model.setScaleY(SCALE);
        model.setScaleZ(SCALE);

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

        WritableImage[] images = new WritableImage[16];
        for (int i = 0; i < 16; i++) {
            final int index = i;
            ySpin.setAngle(index * 22.5);

            java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(1);
            Platform.runLater(() -> {
                try {
                    WritableImage raw = root3D.snapshot(params, new WritableImage((int) IMAGE_SIZE, (int) IMAGE_SIZE));
                    images[index] = trim(raw,
                            (int) (IMAGE_SIZE / 2) + CAMERA_BIAS_X,
                            (int) (IMAGE_SIZE / 2) + CAMERA_BIAS_Y);
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

    private static WritableImage trim(WritableImage source, int originX, int originY) {
        int w = (int) source.getWidth();
        int h = (int) source.getHeight();
        PixelReader reader = source.getPixelReader();

        int maxR = 0;
        boolean found = false;

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (((reader.getArgb(x, y) >> 24) & 0xFF) > 10) {
                    int r = Math.max(Math.abs(x - originX), Math.abs(y - originY));
                    if (r > maxR) maxR = r;
                    found = true;
                }
            }
        }

        if (!found) return source;

        int halfSize = maxR + 2;
        int squareSize = halfSize * 2;
        WritableImage output = new WritableImage(squareSize, squareSize);
        PixelWriter writer = output.getPixelWriter();

        for (int y = 0; y < squareSize; y++) {
            for (int x = 0; x < squareSize; x++) {
                int srcX = originX - halfSize + x;
                int srcY = originY - halfSize + y;
                if (srcX >= 0 && srcX < w && srcY >= 0 && srcY < h) {
                    writer.setArgb(x, y, reader.getArgb(srcX, srcY));
                }
            }
        }

        if (DEBUG_PIVOT) {
            int mid = squareSize / 2;
            for (int i = -2; i <= 2; i++) {
                for (int j = -2; j <= 2; j++) {
                    output.getPixelWriter().setArgb(mid + i, mid + j, 0xFFFF0000);
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