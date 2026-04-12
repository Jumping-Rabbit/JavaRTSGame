package utils;

import com.sun.javafx.tk.quantum.PerformanceLogger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoggerUtil {
    private static BufferedWriter performanceLogger;
    private static BufferedWriter errorLogger;
    private static BufferedWriter eventLogger;
    private static ExecutorService executor;
    private static StringBuilder logBuilder;
    public static void init(){
        try {
            performanceLogger = new BufferedWriter(new FileWriter("resources/logs/performanceLog.txt"));
            errorLogger = new BufferedWriter(new FileWriter("resources/logs/errorLog.txt"));
            eventLogger = new BufferedWriter(new FileWriter("resources/logs/eventLog.txt"));
            executor = Executors.newSingleThreadExecutor(runnable -> {
                Thread thread = new Thread(runnable);
                thread.setDaemon(true);
                return thread;
            });
        } catch (IOException e) {
            LoggerUtil.logError(e);
        }
    }
    public static void logPerformance(double fps, double fps1, double fps01, double tps, double ttu, double ttu1, int lateFrames){
        executor.execute(() -> {
            try {
                logBuilder = new StringBuilder(100);
                logBuilder.append(Instant.now().toString());
                logBuilder.append(" fps:");
                logBuilder.append(fps);
                logBuilder.append(" fps1:");
                logBuilder.append(fps1);
                logBuilder.append(" fps01:");
                logBuilder.append(fps01);
                logBuilder.append(" tps:");
                logBuilder.append(tps);
                logBuilder.append(" ttu:");
                logBuilder.append(ttu);
                logBuilder.append(" ttu1:");
                logBuilder.append(ttu1);
                logBuilder.append(" lf:");
                logBuilder.append(lateFrames);
                performanceLogger.write(logBuilder.toString());
                performanceLogger.newLine();
            } catch (IOException e) {
                logError(e);
            }
        });
    }
    public static void logError(Exception err){
        executor.execute(() -> {
            try {
                logBuilder = new StringBuilder(100);
                logBuilder.append(Instant.now().toString());
                logBuilder.append(" ");
                logBuilder.append(err.getMessage());
                errorLogger.write(logBuilder.toString());
                errorLogger.newLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
    public static void logEvent(String string){
        executor.execute(() -> {
            try {
                logBuilder = new StringBuilder(100);
                logBuilder.append(Instant.now().toString());
                logBuilder.append(" ");
                logBuilder.append(string);
                eventLogger.write(logBuilder.toString());
                eventLogger.newLine();
            } catch (IOException e) {
                logError(e);
            }
        });
    }
    public static void flush() {
        try {
            performanceLogger.flush();
            errorLogger.flush();
            eventLogger.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
