package com.game.utils;

import com.game.gameWindow.LoadingScreen;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPOutputStream;

public class LoggerUtil {
    private static BufferedWriter errorLogger;
    private static GZIPOutputStream logger;
    private static ExecutorService executor;

    public static void init(LoadingScreen loadingScreen) {

        try {
            errorLogger = new BufferedWriter(new FileWriter("core/resources/logs/errorLog.txt", true));
            logger = new GZIPOutputStream(new FileOutputStream("core/resources/logs/log.gz", true));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        executor = Executors.newSingleThreadExecutor(runnable -> {
            Thread thread = new Thread(runnable);
            thread.setDaemon(true);
            return thread;
        });
        loadingScreen.increment();
    }

    /**
     * logs anything
     */
    public static void log(LogType logType, Object... objects) {
        executor.execute(() -> {
            try {
                StringBuilder body = new StringBuilder();
                for (Object object : objects) {
                    body.append(object);
                }
                logger.write(("[" + Instant.now().toString() + "] " + logType.getString() + body + "\n").getBytes());
            } catch (IOException e) {
                log(e);
            }
        });
    }

    /**
     * logs performance
     */
    public static void log(PerformanceType performanceType, Object... objects) {
//        System.out.println("p");
        executor.execute(() -> {
            try {
                StringBuilder body = new StringBuilder();
                for (Object object : objects) {
                    body.append(object);
                }
                logger.write(("[" + Instant.now().toString() + "] " + LogType.PERFORMANCE.getString() + performanceType.getString() + body + "\n").getBytes());
            } catch (IOException e) {
                log(e);
            }
        });
    }

    /**
     * logs an error
     */
    public static void log(Exception err) {
        executor.execute(() -> {
            try {
                errorLogger.write("[" + Instant.now().toString() + "] " + LogType.ERROR.getString() + err.getMessage() + "\n");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        throwError(err);
    }

    /**
     * logs an event
     */
    public static void log(String string) {
        executor.execute(() -> {
            try {
                StringBuilder logBuilder = new StringBuilder(100);
                logBuilder.append(Instant.now().toString());
                logBuilder.append(" ");
                logBuilder.append(string);
                logger.write(("[" + Instant.now().toString() + "] " + LogType.EVENT.getString() + logBuilder +"\n").getBytes());
            } catch (IOException e) {
                log(e);
            }
        });
    }

    public static void flush() {
        System.out.println("flush logger");
        try {
            logger.flush();
            errorLogger.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        executor.shutdown();
        try {
            executor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        try {
            errorLogger.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void throwError(Exception e){
        throw new RuntimeException(e);
    }
}
