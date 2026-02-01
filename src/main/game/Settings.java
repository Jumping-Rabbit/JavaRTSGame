package main.game;

import main.CollisionUtil;
import main.DrawUtil;
import main.inputHandler.Input;
import main.inputHandler.InputHandler;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Settings extends Screen{
    private DrawUtil drawUtil;
    private int sectionIndex = 0;
    private int settingsIndex = 0;
    private String[] sections;
    private ArrayList<String> sectionSettings = new ArrayList<>();
    private double sectionWidth;
    private boolean exit = false;

    public boolean isExit() {
        if (exit){
            exit = false;
            return true;
        }
        return false;
    }

    public void resetSelections(){
        sectionIndex = 0;
    }

    public Settings(DrawUtil drawUtil) {
        this.drawUtil = drawUtil;
        JSONParser parser = new JSONParser();
        try {
            Object object = parser.parse(new FileReader("res/settings.json"));
            JSONObject settings = (JSONObject) object;
            sections = new String[settings.size()];
            for (Object section : settings.keySet()){
                JSONObject tempSectionJSONObject = (JSONObject)settings.get(section);
                sections[(int)(long)tempSectionJSONObject.get("index")] = section.toString();
            }
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public Settings(Settings settings){
        drawUtil = settings.drawUtil;
        sectionIndex = settings.sectionIndex;
        settingsIndex = settings.settingsIndex;
        sections = settings.sections;
        sectionSettings = (ArrayList<String>) settings.sectionSettings.clone();
        sectionWidth = settings.sectionWidth;
        exit = settings.exit ;
    }

    public Screen copy(){
        return new Settings(this);
    }

    public void updateOnFrame() {
        sectionWidth = (1920d/sections.length);
        for (Input input : InputHandler.getInputs()){
            switch (input.getInputType()) {
                case KEYPRESS:
                    if (Objects.equals(input.getKey(), "a") || Objects.equals(input.getKey(), "left")){
                        sectionIndex = Math.max(sectionIndex-1, 0);
                    } else if (Objects.equals(input.getKey(), "d") || Objects.equals(input.getKey(), "right")){
                        sectionIndex = Math.min(sectionIndex+1, sections.length -1);
                    } else if (Objects.equals(input.getKey(), "w")) {
                        settingsIndex = Math.min(settingsIndex+1, sectionSettings.size()-2);
                    } else if (Objects.equals(input.getKey(), "s")) {
                        settingsIndex = Math.max(settingsIndex-1, 0);
                    } else if (Objects.equals(input.getKey(), "escape")){
                        exit = true;
                    }
                    break;
                case LEFT_CLICK:
                    for (int i = 0; i < sections.length; i++) {
                        if (CollisionUtil.RectPointCollision(sectionWidth*i, 0, sectionWidth, 100, input.getX(), input.getY())) {
                            int oldIndex = sectionIndex;
                            sectionIndex = i;
                            if (sectionIndex != oldIndex){
                                settingsIndex = 0;
                            }
                        }
                    }
                    break;
                case SCROLL:
                    settingsIndex = Math.min(Math.max(sectionIndex+input.getScroll(), 0), sectionSettings.size()-2);
                    break;
            }
//            System.out.println(selectedIndex);
            if (sectionIndex == sections.length-1){
                exit = true;
            }
        }

    }

    public void draw() {

        double sectionX = 0;
        drawUtil.setColor(75, 75, 75);
        drawUtil.fillRect(0, 0, 1920, 100);

        drawUtil.setThickness(5);
        drawUtil.setColor(0, 150, 255);
        for (String section : sections){
            if (Objects.equals(section, sections[sectionIndex])){
                sectionX += sectionWidth;
                continue;
            }
//            System.out.println(section);
            drawUtil.drawRect(sectionX, 0, 1920d/sections.length, 100);
            drawUtil.drawStringFill(sectionX + sectionWidth/2, 50, section, sectionWidth, 2d/3d, 25);
            sectionX += sectionWidth;
        }

        drawUtil.setColor(0, 255, 255);
        drawUtil.drawRect((1920d/sections.length)*sectionIndex, 0, sectionWidth, 100);
        drawUtil.drawStringFill((1920d/sections.length)*sectionIndex + sectionWidth/2, 50, sections[sectionIndex], sectionWidth, 2d/3d, 25);


        JSONParser parser = new JSONParser();
        try {
            Object object = parser.parse(new FileReader("res/settings.json"));
            JSONObject settingsJSONObject = (JSONObject) object;
            JSONObject sectionSettingsJSONObject = (JSONObject) settingsJSONObject.get(sections[sectionIndex]);

            Set<Object> sectionSettingsSet = sectionSettingsJSONObject.keySet();
            sectionSettingsSet.remove("index");
            String[] settings = Arrays.stream(sectionSettingsSet.toArray()).map(Object::toString).toArray(String[]::new);
            sectionSettings = new ArrayList<>(Arrays.stream(settings).toList());
            if (settings.length == 0){
                return;
            }
            drawUtil.setColor(0, 150, 255);
            for (int i = 0; i < sectionSettings.size(); i++){
                if (i == settingsIndex) {
                    continue;
                }
                drawUtil.drawRect(20, 150+i*220, 940, 200);
                drawUtil.drawString(490, 250+i*220, settings[i], 50);
            }
            drawUtil.setColor(0, 255, 255);
            drawUtil.drawRect(20, 150+settingsIndex*220, 940, 200);
            drawUtil.drawString(490, 250+settingsIndex*220, settings[settingsIndex], 50);

        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
    }
}