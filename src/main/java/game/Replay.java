package game;

import inputHandler.Input;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.ArrayList;

public class Replay {
    private static File file;
    private static ObjectMapper objectMapper = new ObjectMapper();
    private static JsonNode root;
    public static void newReplay(File map){
        LocalDateTime time = LocalDateTime.now();
        String directory = "resources/replays/" + DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss").format(time);//use -SSSSSSSSS for nanosecs
        try{
            Files.createDirectories(Paths.get(directory));
            //add map file
        }catch(IOException e){
            e.printStackTrace();
        }

        file = new File(directory + "/replay.json");
        //make the json file in the files
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, objectMapper.createObjectNode());
        root = objectMapper.readTree(file);
    }
    public static void addTick(ArrayDeque<Input> inputs, long tickNum){
        ArrayNode newTick = objectMapper.createArrayNode();
        for (Input input : inputs){
            ObjectNode node = objectMapper.createObjectNode();
            node.put("inputType", input.getInputType().toString());
            node.put("x", input.getX());
            node.put("y", input.getY());
            node.put("startX", input.getStartX());
            node.put("startY", input.getStartY());
            node.put("scroll", input.getScroll());
            node.put("shift", input.getIsShiftHeld());
            node.put("key", input.getKey().getKeyHandlerString());
            newTick.add(node);
        }

        root = ((ObjectNode) root).set(String.valueOf(tickNum), newTick);
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void openReplay(File file){
        Replay.file = file;
        root = objectMapper.readTree(file);
    }
    public static ArrayList<Input> getTick(){
        return null;
    }
}
