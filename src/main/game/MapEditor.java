package main.game;

import main.CollisionUtil;
import main.DrawUtil;
import main.inputHandler.Input;
import main.inputHandler.InputHandler;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import javax.swing.*;

public class MapEditor extends Screen{
    private String directory;
    private boolean isRenaming;
    private String newName;

    private ArrayList<ArrayList<Integer>> tileMap = new ArrayList<>();
    private ArrayList<ArrayList<Integer>> heightMap = new ArrayList<>();
    private ArrayList<BufferedImage> tiles = new ArrayList<>();
    private ArrayList<File> units = new ArrayList<>();
    private ArrayList<File> buildings = new ArrayList<>();
    private ArrayList<File> objects = new ArrayList<>();
    private double tileWidth;
    private DrawUtil drawUtil;
    private int tileIndex;
    private int height = 0;

    private boolean exit = false;

    public boolean isExit(){
        if (exit){
            exit = false;
            return true;
        }
        return false;
    }

    public MapEditor(DrawUtil drawUtil) {
        this.drawUtil = drawUtil;
        tileIndex = 0;
    }

    private MapEditor(MapEditor mapEditor){
        directory = mapEditor.directory;
        isRenaming = mapEditor.isRenaming;
        newName = mapEditor.newName;

        tileMap = (ArrayList<ArrayList<Integer>>)mapEditor.tileMap.clone();
        heightMap = (ArrayList<ArrayList<Integer>>)mapEditor.heightMap.clone();
        tiles = (ArrayList<BufferedImage>)mapEditor.tiles.clone();
        units = new ArrayList<>();
        buildings = new ArrayList<>();
        objects = new ArrayList<>();
        tileWidth = mapEditor.tileIndex;
        drawUtil = mapEditor.drawUtil;
        tileIndex = mapEditor.tileIndex;
        height = mapEditor.height;
    }

    public Screen copy(){
        return new MapEditor(this);
    }

    public enum Buttons {
        RENAME_MAP(new Rectangle(1570, 20, 330, 60), "rename map"),

        OPEN(new Rectangle(1570, 100, 150, 60), "open"),
        NEW(new Rectangle(1750, 100, 150, 60), "new"),

        SAVE_AS(new Rectangle(1750, 180, 150, 60), "save as"),

        INCREASE_HEIGHT(new Rectangle(1570, 180, 50, 60), "^"),
        HEIGHT_COUNTER(new Rectangle(1620, 180, 50, 60), ""),
        DECREASE_HEIGHT(new Rectangle(1670, 180, 50, 60), "⌄"),


        IMPORT_TILE(new Rectangle(1750, 260, 150, 60), "import tile"),
        IMPORT_UNIT(new Rectangle(1570, 260, 150, 60), "import unit"),

        IMPORT_OBJECT(new Rectangle(1570, 340, 200, 60), "import object"),
        IMPORT_BUILDING(new Rectangle(1570, 420, 200, 60), "import building"),
        BACK(new Rectangle(1800, 340, 100, 140), "back");

        private final Rectangle rectangle;
        private final String name;

        Buttons(Rectangle rectangle, String name) {
            this.rectangle = rectangle;
            this.name = name;
        }

        private String getName() {
            return name;
        }

        private Rectangle getRectangle() {
            return rectangle;
        }
    }

    private void saveMap(){

    }

    private void saveMapAs(){

    }

    private void newMap(){
        LocalDateTime time = LocalDateTime.now();
        directory = "res/map/custom/" + DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss").format(time);//use -SSSSSSSSS for nanosecs
//        name = String.format("res/map/custom/%d-%d-%d-%d-%d-%d-%d", );
//        name = "res/map/custom/" + time.getYear()+"-"+time.getMonthValue()+"-"+time.getDayOfMonth()+"-"+time.getHour()+"-"+time.getMinute()+"-"+time.getSecond()+"-"+time.getNano();
        try{
            Files.createDirectories(Paths.get(directory));
            Files.createDirectories(Paths.get(directory + "/units"));
            Files.createDirectories(Paths.get(directory + "/buildings"));
            Files.createDirectories(Paths.get(directory + "/objects"));
            Files.createDirectories(Paths.get(directory + "/tiles"));
        }catch(IOException e){
            e.printStackTrace();
        }
        JSONObject map = new JSONObject();
        map.put("name", directory.substring(directory.lastIndexOf("/") + 1));

        JSONArray zero2DArray = new JSONArray();
        JSONArray zeroArray = new JSONArray();
        JSONObject playerData = new JSONObject();
        JSONArray playersData = new JSONArray();
        for (int i = 0; i < 50; i++){
            zeroArray.add(0);
        }
        for (int i = 0; i < 50; i++){
            zero2DArray.add(zeroArray);
        }
        playersData.add(playerData);
        playerData.put("x", 0);
        playerData.put("y", 0);

        JSONObject key = new JSONObject();
        key.put("0", "default.png");

        map.put("tileMap", zero2DArray);
        map.put("heightMap", zero2DArray);
        map.put("key", new JSONObject());
        map.put("playerData", playersData);
        try(FileWriter writer = new FileWriter(directory +"/map.json")){
            writer.write(map.toJSONString());
            writer.flush();
        }catch(IOException e){
            e.printStackTrace();
        }

    }

    private File[] getFile(JFileChooser chooser, String[] files) {
        int returnVal = chooser.showOpenDialog(null);
        File[] selectedDirectory;
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            selectedDirectory = chooser.getSelectedFiles();
            ArrayList<HashMap<String, Boolean>> hasFiles= new ArrayList<>(selectedDirectory.length);
            for (int i = 0; i < hasFiles.size();i++){
                hasFiles.add(new HashMap<String, Boolean>());
                for (String file : files){//populate hashMap
                    hasFiles.get(i).put(file, false);
                }
            }

            for (int i = 0 ; i < selectedDirectory.length; i++){
                for (File file : Objects.requireNonNull(selectedDirectory[i].listFiles())) {
                    if (hasFiles.get(i).containsKey(file.getName())) {
                        if (hasFiles.get(i).get(file.getName())) {//if already has image file, this means has 2, so consider as none and exit
                            hasFiles.get(i).put(file.getName(), false);
                            break;
                        }
                        hasFiles.get(i).put(file.getName(), true);
                    }
                }
            }
            boolean hasAllFiles = true;
            for (HashMap<String, Boolean> hashMap : hasFiles){
                for (Boolean hasFile : hashMap.values()){
                    if (!hasFile || !hasAllFiles){
                        hasAllFiles = false;
                        break;
                    }
                }
            }

            if (!hasAllFiles){
                chooser.cancelSelection();
                selectedDirectory = getFile(chooser, files);
            }
            return selectedDirectory;
        }
        return null;
    }
    
    private File[] getFile(JFileChooser chooser, String fileType) {
        int returnVal = chooser.showOpenDialog(null);
        File[] selectedFiles;
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            selectedFiles = chooser.getSelectedFiles();
            boolean allCorrectFileType = true;

            for (File selectedFile : Objects.requireNonNull(selectedFiles)) {
                if (!selectedFile.getName().substring(selectedFile.getName().lastIndexOf(".")).equals(fileType)) {
                    allCorrectFileType = false;
                    break;
                }
            }
            if (!allCorrectFileType){
                chooser.cancelSelection();
                selectedFiles = getFile(chooser, fileType);
            }
            return selectedFiles;
        }
        return null;
    }

    private File[] openFolder(String[] files){
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setMultiSelectionEnabled(true);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        return getFile(chooser, files);
    }

    private File[] openFile(String file){
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setMultiSelectionEnabled(true);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        return getFile(chooser, file);
    }

    private void openMap(){
        SwingUtilities.invokeLater(() -> {
            directory = openFolder(new String[]{"buildings", "map.json", "objects", "units"})[0].getPath();
        });
    }

    private void startRenameMap(){
        isRenaming = true;
    }

    private void cancelRenameMap(){
        isRenaming = false;
    }

    private void renameMap(String key){
        if (directory == null){
            return;
        }
        JSONParser parser = new JSONParser();
        try {
            Object object = parser.parse(new FileReader(directory + "/map.json"));
            JSONObject map = (JSONObject) object;
            if (Objects.equals(key, "backspace") && !String.valueOf(map.get("name")).isEmpty()){
                map.put("name", String.valueOf(map.get("name")).substring(0, String.valueOf(map.get("name")).length() - 1));
            }else if (key.equals("space")){
                map.put("name", map.get("name") + " ");
            } else if (key.length() == 1){
                map.put("name", map.get("name") + key);
            }
            try (FileWriter file = new FileWriter(directory + "/map.json")) {
                file.write(map.toJSONString());
                file.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private void importTile(){
    }

    private void importUnit(){
        SwingUtilities.invokeLater(() -> {
            for (File file : openFolder(new String[]{"image.png", "stats.json"})){
                units.add(file);
            }

        });
    }

    private void importBuilding(){
        SwingUtilities.invokeLater(() -> {
            for (File file : openFolder(new String[]{"image.png", "stats.json"})){
                buildings.add(file);
            }
        });
    }

    private void importObject(){
        SwingUtilities.invokeLater(() -> {
            for (File file : openFolder(new String[]{"image.png", "stats.json"})){
                objects.add(file);
            }
        });
    }

    private void back(){
        exit = true;
    }

    private void increaseHeight(){
        height = Math.min(height+1, 9);
    }

    private void decreaseHeight(){
        height = Math.max(height-1, 0);
    }


    public void draw() {
        drawUtil.setThickness(5);
        drawUtil.setColor(0, 150, 255);
        if (!tileMap.isEmpty()) {
            tileWidth = 16;
            for (int x = 0; x < tileMap.size(); x++) {
                for (int y = 0; y < tileMap.getFirst().size(); y++) {
                    drawUtil.fillImage(tiles.get(tileMap.get(x).get(y)), x, y, tileWidth, tileWidth);
                }
            }
        }

        int width = 16;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < width; y++) {
                if (tiles.size() - 1 < x + y * width) {
                    break;
                }
                drawUtil.fillImage(tiles.get(y + x * width), 1320 + x * width, 510 + x * width, 16, 16);
            }
        }

        for (Buttons button : Buttons.values()) {
            drawUtil.drawRect(button.getRectangle());
            if (button == Buttons.RENAME_MAP && directory != null){
                JSONParser parser = new JSONParser();
                try {
                    Object object = parser.parse(new FileReader(directory + "/map.json"));
                    JSONObject map = (JSONObject) object;
                    drawUtil.drawString(button.getRectangle().getX() + button.getRectangle().getWidth() / 2, button.getRectangle().getY() + button.getRectangle().getHeight() / 2, (String) map.get("name"), 20);
                } catch (IOException | ParseException e) {
                    throw new RuntimeException(e);
                }
                continue;
            } else if (button == Buttons.HEIGHT_COUNTER){
                drawUtil.drawString(button.getRectangle().getX() + button.getRectangle().getWidth() / 2, button.getRectangle().getY() + button.getRectangle().getHeight() / 2, String.valueOf(height), 20);
            }
            drawUtil.drawString(button.getRectangle().getX() + button.getRectangle().getWidth() / 2, button.getRectangle().getY() + button.getRectangle().getHeight() / 2, button.getName(), 20);
        }
    }

    public void updateOnFrame() {
        for (Input input : InputHandler.getInputs()) {
            switch (input.getInputType()){
                case LEFT_CLICK:
                    for (Buttons button : Buttons.values()) {
                        if (CollisionUtil.RectPointCollision(button.getRectangle(), input.getX(), input.getY())) {
                            switch (button){
                                case RENAME_MAP:
                                    if (isRenaming){
                                        cancelRenameMap();
                                    } else {
                                        startRenameMap();
                                    }
                                    break;
                                case NEW:
                                    newMap();
                                    break;
                                case OPEN:
                                    openMap();
                                    break;
                                case SAVE_AS:
                                    saveMapAs();
                                    break;
                                case IMPORT_TILE:
                                    importTile();
                                    break;
                                case IMPORT_UNIT:
                                    importUnit();
                                    break;
                                case IMPORT_BUILDING:
                                    importBuilding();
                                    break;
                                case IMPORT_OBJECT:
                                    importObject();
                                    break;
                                case BACK:
                                    back();
                                    break;
                                case INCREASE_HEIGHT:
                                    increaseHeight();
                                    break;
                                case DECREASE_HEIGHT:
                                    decreaseHeight();
                                    break;
                            }
                        }
                    }
                    break;
                case RIGHT_CLICK:
                    cancelRenameMap();
                    break;
                case KEYPRESS:
                    if (input.getKey().equals("escape")){
                        back();
                    }
                    if (isRenaming){
                        renameMap(input.getKey());
                    }
            }
        }
    }
}
