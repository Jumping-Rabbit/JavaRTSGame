package utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoggerUtil {
    private static BufferedWriter logger;
    private static ExecutorService executor;
    private static StringBuilder logBuilder;
    public static void init(){
        try {
            logger = new BufferedWriter(new FileWriter("resources/logs/log.txt", true));
            executor = Executors.newSingleThreadExecutor(runnable -> {
                Thread thread = new Thread(runnable);
                thread.setDaemon(true);
                return thread;
            });
        } catch (IOException e) {
            LoggerUtil.log(e);
        }
    }
    /**logs anything*/
    public static void log(LogType logType, Object... objects){
        executor.execute(() -> {
            try {
                StringBuilder body = new StringBuilder();
                for (Object object : objects){
                    body.append(object);
                }
                logger.write("[" + Instant.now().toString() +"] " + logType.getString() + body);
                logger.newLine();
            } catch (IOException e) {
                log(e);
            }
        });
    }
    /**logs performance*/
    public static void log(PerformanceType performanceType, Object... objects){
        executor.execute(() -> {
            try {
                StringBuilder body = new StringBuilder();
                for (Object object : objects){
                    body.append(object);
                }
                logger.write("[" + Instant.now().toString() +"] " + LogType.PERFORMANCE.getString() + performanceType.getString() + body);
                logger.newLine();
            } catch (IOException e) {
                log(e);
            }
        });
    }
    /**logs an error*/
    public static void log(Exception err){
        executor.execute(() -> {
            try {
                logBuilder = new StringBuilder(100);
                logBuilder.append(err.getMessage());
                logger.write("[" + Instant.now().toString() +"] " + LogType.ERROR.getString() + logBuilder);
                logger.newLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**logs an event*/
    public static void log(String string){
        executor.execute(() -> {
            try {
                logBuilder = new StringBuilder(100);
                logBuilder.append(Instant.now().toString());
                logBuilder.append(" ");
                logBuilder.append(string);
                logger.write("[" + Instant.now().toString() +"] " + LogType.EVENT.getString() + logBuilder);
                logger.newLine();
            } catch (IOException e) {
                log(e);
            }
        });
    }
    public static void flush() {
        try {
            logger.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
