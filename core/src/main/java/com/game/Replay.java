package com.game;

import com.game.inputHandler.Input;
import com.game.utils.LoggerUtil;
import org.apache.commons.io.FileUtils;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Replay {
    private static File file;
    private static ObjectMapper objectMapper = new ObjectMapper();
    private static ExecutorService executor;
    private static PrintWriter writer;

    public static void newReplay(File map) {
        //            thread.setDaemon(true);
        LocalDateTime time = LocalDateTime.now();
        String directory = "core/resources/replays/" + DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss").format(time);//use -SSSSSSSSS for nanosecs
        try {
            Files.createDirectories(Paths.get(directory));
            //add map file
        } catch (IOException e) {
            LoggerUtil.log(e);
        }

        file = new File(directory + "/replay.jsonl");
        try {
            FileUtils.copyDirectory(map, new File(directory));
        } catch (IOException e) {
            LoggerUtil.log(e);
        }
        executor = Executors.newSingleThreadExecutor(Thread::new);
        try {
            writer = new PrintWriter(new BufferedWriter(new FileWriter(directory + "/replay.jsonl")));
        } catch (IOException e) {
            LoggerUtil.log(e);
        }


//        objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, objectMapper.createObjectNode());
//        root = objectMapper.readTree(file);
    }

    public static void addTick(ArrayDeque<Input> inputs, long tickNum) {
        if (executor == null || executor.isShutdown() || executor.isTerminated()) {
            return;
        }
        ArrayNode newTick = objectMapper.createArrayNode();
        for (Input input : inputs) {
            ObjectNode node = objectMapper.createObjectNode();
            node.put("inputType", input.getInputType().toString());
            node.put("x", input.getX());
            node.put("y", input.getY());
            node.put("startX", input.getStartX());
            node.put("startY", input.getStartY());
            node.put("scroll", input.getScroll());
            node.put("shift", input.isShiftHeld());
            node.put("control", input.isControlHeld());
            node.put("key", input.getKey().getKeyHandlerString());
            node.put("action", input.getAction().name());
            newTick.add(node);
        }

//        root = ((ObjectNode) root).set(String.valueOf(tickNum), newTick);
        executor.execute(() -> {
            try {
                writer.println(objectMapper.writeValueAsString(newTick));
            } catch (Exception e) {
                LoggerUtil.log(e);
            }
        });
    }

    public static void openReplay(File file) {
        Replay.file = file;
//        root = objectMapper.readTree(file); change to jsonL
    }

    public static ArrayList<Input> getTick() {
        return null;//change to jsonL
    }

    public static void flush() {
        if (executor == null) return;
        executor.shutdown();
        writer.flush();
        writer.close();
    }
}
