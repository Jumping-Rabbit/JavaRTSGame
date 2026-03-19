import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class AudioConverter {
    public static void main(String[] args) {
        Path sourceRootDir = Paths.get("src/test/resources/bgm/geometry dash/game");
        Path targetRootDir = Paths.get("src/main/resources/sounds/bgm/geometry dash/game");

        try (Stream<Path> paths = Files.walk(sourceRootDir)) {
            paths.filter(Files::isRegularFile)
                    // Check specifically for the double extension
                    .filter(p -> p.toString().toLowerCase().endsWith(".mp3.mpeg"))
                    .forEach(sourcePath -> {
                        Path relativePath = sourceRootDir.relativize(sourcePath);
                        String fileName = relativePath.toString();

                        // Remove the last 9 characters (".mp3.mpeg")
                        // and append ".m4a"
                        String baseName = fileName.substring(0, fileName.length() - 9);
                        Path targetFile = targetRootDir.resolve(baseName + ".m4a");

                        if (targetFile.getParent() != null) {
                            targetFile.getParent().toFile().mkdirs();
                        }

                        convertToM4A(sourcePath.toString(), targetFile.toString());
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void convertToM4A(String inputPath, String outputPath) {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "ffmpeg-8.0.1-essentials_build/ffmpeg-8.0.1-essentials_build/bin/ffmpeg.exe",
                    "-i", inputPath,
                    "-vn", "-sn",
                    "-map_metadata", "-1",
                    "-c:a", "aac",
                    "-b:a", "192k",
                    "-y",
                    outputPath
            );

            int exitCode = pb.inheritIO().start().waitFor();
            if (exitCode == 0) {
                System.out.println("Converted: " + outputPath);
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Failed to convert: " + inputPath);
            Thread.currentThread().interrupt();
        }
    }
}