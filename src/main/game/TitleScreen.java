package main.game;

import main.CollisionUtil;
import main.DrawUtil;
import main.Fonts;
import main.Main;
import main.inputHandler.Input;
import main.inputHandler.InputHandler;
import main.inputHandler.InputType;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import static main.inputHandler.InputType.*;

public class TitleScreen {
    public enum Buttons {
        HOME(new Rectangle(0, 0, 200, 100), "home"),
        CAMPAIGN(new Rectangle(200, 0, 400, 100), "campaign"),
        CUSTOM(new Rectangle(600, 0, 400, 100), "custom"),
        MAP_EDITOR(new Rectangle(1000, 0, 400, 100), "map maker"),
        REPLAYS(new Rectangle(1400, 0, 260, 100), "replays"),
        SETTINGS(new Rectangle(1660, 0, 260, 100), "settings");

        private final Rectangle  rectangle;
        private final String name;
        Buttons(Rectangle rectangle, String name){
            this.rectangle = rectangle;
            this.name = name;
        }
        private String getName(){
            return name;
        }
        private Rectangle getRectangle(){
            return rectangle;
        }
    }
    private Buttons selectedButton = Buttons.HOME;

    private ArrayList<File> customMaps = new ArrayList<>(Arrays.asList(Objects.requireNonNull(new File("res/map/custom").listFiles())));
    private ArrayList<File> replays = new ArrayList<>(Arrays.asList(Objects.requireNonNull(new File("res/replay").listFiles())));
    private int selectedIndex = 0;
    private boolean exit = false;
    public boolean isExit() {
        if (exit){
            exit = false;
            return true;
        }
        return false;
    }
    public Buttons getSelectedButton(){
        return selectedButton;
    }

    public void resetSelections(){
        selectedButton = Buttons.HOME;
    }

    private DrawUtil drawUtil;

    public TitleScreen(DrawUtil drawUtil){
        this.drawUtil = drawUtil;
    }

    public void updateOnFrame() {
        for (Input input : InputHandler.getInputs()){
            switch (input.getInputType()) {
                case KEYPRESS:
                    if (Objects.equals(input.getKey(), "a") || Objects.equals(input.getKey(), "left")){
                        Buttons oldButton = selectedButton;
                        selectedButton = Buttons.values()[(selectedButton.ordinal() - 1) >= 0 ? (selectedButton.ordinal() - 1) : (Buttons.values().length - 1)];
                        if (selectedButton != oldButton){
                            selectedIndex = 0;
                        }
                    } else if (Objects.equals(input.getKey(), "d") || Objects.equals(input.getKey(), "right")){
                        Buttons oldButton = selectedButton;
                        selectedButton = Buttons.values()[(selectedButton.ordinal() + 1) % Buttons.values().length];
                        if (selectedButton != oldButton){
                            selectedIndex = 0;
                        }
                    }else if (Objects.equals(input.getKey(), "w")) {
                        selectedIndex--;
                        if (selectedIndex < 0){
                            selectedIndex = 0;
                        }
                    } else if (Objects.equals(input.getKey(), "s")) {
                        selectedIndex++;
                        switch (selectedButton){
                            case CUSTOM:
                                if (selectedIndex > (customMaps.size() - 1)){
                                    selectedIndex = customMaps.size() - 1;
                                }
                                break;
                            case REPLAYS:
                                if (selectedIndex > (replays.size() - 1)){
                                    selectedIndex = replays.size() - 1;
                                }
                                break;
                        }
                }
                    break;
                case LEFT_CLICK:
                    for (Buttons button : Buttons.values()) {
                        if (CollisionUtil.RectPointCollision(button.getRectangle(), input.getX(), input.getY())) {
                            Buttons oldButton = selectedButton;
                            selectedButton = button;
                            if (selectedButton != oldButton){
                                selectedIndex = 0;
                            }
                            switch (button){
                                case MAP_EDITOR:
                                    exit = true;
                                    break;
                                case SETTINGS:
                                    exit = true;
                                    break;
                            }
                        }
                    }
                    break;
                case SCROLL:
                    switch (selectedButton) {
                        case CUSTOM:
                            if (input.getY() > 150 && input.getY() < 1050 && input.getX() > 50 && input.getX() < 1050){
                                selectedIndex += input.getScroll();
                                if (selectedIndex < 0){
                                    selectedIndex = 0;
                                } else if (selectedIndex > (customMaps.size() - 1)){
                                    selectedIndex = customMaps.size() - 1;
                                }
                            }
                            break;
                        case REPLAYS:
                            if (input.getY() > 150 && input.getY() < 1050 && input.getX() > 50 && input.getX() < 1050){
                                selectedIndex += input.getScroll();
                                if (selectedIndex < 0){
                                    selectedIndex = 0;
                                } else if (selectedIndex > (replays.size() - 1)){
                                    selectedIndex = replays.size() - 1;
                                }
                            }
                            break;
                    }
            }
//            System.out.println(selectedIndex);
        }
        if (selectedButton == Buttons.CUSTOM){
            customMaps = new ArrayList<>(Arrays.asList(Objects.requireNonNull(new File("res/map/custom").listFiles())));
        }
        if (selectedButton == Buttons.REPLAYS){
            replays = new ArrayList<>(Arrays.asList(Objects.requireNonNull(new File("res/replay").listFiles())));
        }
        switch (selectedButton){
            case MAP_EDITOR:
                exit = true;
                break;
            case SETTINGS:
                exit = true;
                break;
            default:
                break;
        }
    }


    public void draw() {
        drawUtil.setColor(75, 75, 75);
        drawUtil.fillRect(0, 0, 1920, 100);

        drawUtil.setThickness(5);
        drawUtil.setColor(0, 150, 255);
        for (Buttons button : Buttons.values()){
            if (button == selectedButton){
                continue;
            }
            drawUtil.drawRect(button.getRectangle());
            drawUtil.drawString(button.getRectangle().getX() + button.getRectangle().getWidth()/2, 50, button.getName(), 20);
        }

        drawUtil.setColor(0, 255, 255);
        drawUtil.drawRect(selectedButton.getRectangle());
        drawUtil.drawString(selectedButton.getRectangle().getX() + selectedButton.getRectangle().getWidth()/2, 50, selectedButton.getName(), 20);

        switch (selectedButton){
            case HOME:
                drawHome(drawUtil);
                break;
            case CAMPAIGN:
                drawCampaign(drawUtil);
                break;
            case CUSTOM:
                drawCustom(drawUtil);
                break;
            case MAP_EDITOR:
                drawMapEditor(drawUtil);
                break;
            case REPLAYS:
                drawReplays(drawUtil);
                break;

        }

    }
    private void drawHome(DrawUtil drawUtil){

    }
    private void drawCampaign(DrawUtil drawUtil){

    }
    private void drawCustom(DrawUtil drawUtil){
        int start;
        if (selectedIndex - 4 < 0) {
            start = 0;
        }else if (selectedIndex > customMaps.size()-5){
            start = customMaps.size()-9;
        }else {
            start = selectedIndex - 4;
        }
        for (int i = start; i < start + 9; i++){
            drawUtil.setColor(0, 0, 0);
            drawUtil.fillRect(50, (i - start)*100 + 150, 1000, 80);
            if (i == selectedIndex){
                drawUtil.setColor(0, 255, 255);
            } else {
                drawUtil.setColor(0, 150, 255);
            }
            drawUtil.drawRect(50,(i - start)*100 + 150, 1000, 80);
            JSONParser parser = new JSONParser();
            try {
                Object object = parser.parse(new FileReader(customMaps.get(i).getPath() + "/map.json"));
                JSONObject map = (JSONObject) object;
                drawUtil.drawString(525, (i - start)*100 + 200, (String) map.get("name"), 50);
            } catch (IOException | ParseException e) {
                throw new RuntimeException(e);
            }
        }
    }
    private void drawMapEditor(DrawUtil drawUtil){

    }
    private void drawReplays(DrawUtil drawUtil){
        int start;
        if (selectedIndex - 4 < 0) {
            start = 0;
        }else if (selectedIndex > customMaps.size()-5){
            start = customMaps.size()-9;
        }else {
            start = selectedIndex - 4;
        }
        for (int i = start; i < start + 9; i++){
            drawUtil.setColor(0, 0, 0);
            drawUtil.fillRect(50, (i - start)*100 + 150, 1000, 80);
            if (i == selectedIndex){
                drawUtil.setColor(0, 255, 255);
            } else {
                drawUtil.setColor(0, 150, 255);
            }
            drawUtil.drawRect(50,(i - start)*100 + 150, 1000, 80);
            JSONParser parser = new JSONParser();
            try {
                Object object = parser.parse(new FileReader(customMaps.get(i).getPath() + "/map.json"));
                JSONObject map = (JSONObject) object;
                drawUtil.drawString(525, (i - start)*100 + 200, (String) map.get("name"), 50);
            } catch (IOException | ParseException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
