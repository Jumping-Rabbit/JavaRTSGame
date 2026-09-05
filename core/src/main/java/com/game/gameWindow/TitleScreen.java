package com.game.gameWindow;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.math.Rectangle;
import com.game.Fonts;
import com.game.inputHandler.Actions;
import com.game.inputHandler.Input;
import com.game.inputHandler.InputHandler;
import com.game.utils.CollisionUtil;
import com.game.utils.DrawUtil;
import com.game.utils.StringAlignment;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class TitleScreen extends GameWindow implements Screen {
    private Rectangle exitButton = new Rectangle(710, 460, 500, 100);
    private Buttons selectedButton = Buttons.HOME;
    private ArrayList<File> customMaps = new ArrayList<>();
    private ArrayList<File> replays = new ArrayList<>();
    private ArrayList<String> customMapsName = new ArrayList<>();
    private ArrayList<String> replaysName = new ArrayList<>();
    private int selectedIndex = 0;
    private boolean closing = false;


    public TitleScreen() {
    }

    private TitleScreen(TitleScreen titleScreen) {//copy constructor
        selectedButton = titleScreen.selectedButton;
        customMaps = new ArrayList<>(Arrays.asList(new File("core/resources/map/custom").listFiles()));
        replays = new ArrayList<>(Arrays.asList(new File("core/resources/replays").listFiles()));
        selectedIndex = titleScreen.selectedIndex;
        exit = titleScreen.exit;
        closing = titleScreen.closing;
    }

    public Buttons getSelectedButton() {
        return selectedButton;
    }

    public File getSelectedFile() {
        return switch (selectedButton) {
            case CUSTOM -> !customMaps.isEmpty() ? customMaps.get(selectedIndex) : null;
            case REPLAYS -> !replays.isEmpty() ? replays.get(selectedIndex) : null;
            default -> null;
        };
    }

    public void resetSelections() {
        selectedButton = Buttons.HOME;
    }

    public GameWindow copy() {
        return new TitleScreen(this);
    }

    public void updateOnFrame() {
        for (Input input : InputHandler.getInputs()) {
            switch (input.getInputType()) {
                case KEYPRESS:
                    if (input.getAction() == Actions.LEFT) {
                        Buttons oldButton = selectedButton;
                        selectedButton = Buttons.values()[(selectedButton.ordinal() - 1) >= 0 ? (selectedButton.ordinal() - 1) : (Buttons.values().length - 1)];
                        if (selectedButton != oldButton) {
                            selectedIndex = 0;
                        }
                    } else if (input.getAction() == Actions.RIGHT) {
                        Buttons oldButton = selectedButton;
                        selectedButton = Buttons.values()[(selectedButton.ordinal() + 1) % Buttons.values().length];
                        if (selectedButton != oldButton) {
                            selectedIndex = 0;
                        }
                    } else if (input.getAction() == Actions.UP) {
                        selectedIndex--;
                        if (selectedIndex < 0) {
                            selectedIndex = 0;
                        }
                    } else if (input.getAction() == Actions.DOWN) {
                        selectedIndex++;
                        switch (selectedButton) {
                            case CUSTOM:
                                if (selectedIndex > (customMaps.size() - 1)) {
                                    selectedIndex = customMaps.size() - 1;
                                }
                                break;
                            case REPLAYS:
                                if (selectedIndex > (replays.size() - 1)) {
                                    selectedIndex = replays.size() - 1;
                                }
                                break;
                        }
                    } else if (input.getAction() == Actions.CONFIRM) {
                        switch (selectedButton) {
                            case CUSTOM, REPLAYS:
                                exit = true;
                                break;
                        }
                    } else if (input.getAction() == Actions.BACK) {
                        closing = !closing;
                    }
                    break;
                case LEFT_CLICK:
                    if (closing) {
                        if (CollisionUtil.RectPointCollision(exitButton, input.getX(), input.getY())) {
                            Gdx.app.exit();
                        } else {
                            closing = false;
                        }
                        return;
                    }
                    for (Buttons button : Buttons.values()) {
                        if (CollisionUtil.RectPointCollision(button.getRectangle(), input.getX(), input.getY())) {
                            Buttons oldButton = selectedButton;
                            selectedButton = button;
                            if (selectedButton != oldButton) {
                                selectedIndex = 0;
                            }
                            switch (button) {
                                case MAP_EDITOR, SETTINGS:
                                    exit = true;
                                    break;
                            }
                        }
                    }

                    break;
                case SCROLL:
                    switch (selectedButton) {
                        case CUSTOM:
                            if (input.getY() > 930 && input.getY() < 30 && input.getX() > 50 && input.getX() < 1050) {
                                selectedIndex += input.getScroll();
                                if (selectedIndex < 0) {
                                    selectedIndex = 0;
                                } else if (selectedIndex > (customMaps.size() - 1)) {
                                    selectedIndex = customMaps.size() - 1;
                                }
                            }
                            break;
                        case REPLAYS:
                            if (input.getY() > 930 && input.getY() < 30 && input.getX() > 50 && input.getX() < 1050) {
                                selectedIndex += input.getScroll();
                                if (selectedIndex < 0) {
                                    selectedIndex = 0;
                                } else if (selectedIndex > (replays.size() - 1)) {
                                    selectedIndex = replays.size() - 1;
                                }
                            }
                            break;
                    }
            }
//            System.out.println(selectedIndex);
        }
        if (selectedButton == Buttons.CUSTOM) {
            customMaps = new ArrayList<>(Arrays.asList((new File("core/resources/map/custom").listFiles())));
            customMapsName = new ArrayList<>();
            for (File customMap : customMaps) {
                var objectMapper = new ObjectMapper();
                JsonNode map = objectMapper.readTree(new File(customMap.getPath() + "/map.json"));
                customMapsName.add(map.get("name").stringValue());
            }

        }
        if (selectedButton == Buttons.REPLAYS) {
            replays = new ArrayList<>(Arrays.asList((new File("core/resources/replays").listFiles())));
            replaysName = new ArrayList<>();
//            for (File replay : replays) {
//                JSONParser parser = new JSONParser();
//                try {
//                    Object object = parser.parse(new FileReader(replay.getPath() + "/map.json"));
//                    JSONObject map = (JSONObject) object;
//                    replaysName.add((String) map.get("name"));
//                } catch (IOException | ParseException e) {
//                    throw new RuntimeException(e);
//                }
//            }
        }
        switch (selectedButton) {
            case MAP_EDITOR, SETTINGS:
                exit = true;
                break;
            default:
                break;
        }
    }

    public void draw() {
//        DrawUtil.fillRect(0, 0, 1920, 100, 0x4B4B4BFF);

//        DrawUtil.setThickness(5);
        for (Buttons button : Buttons.values()) {
            if (button == selectedButton) {
                continue;
            }
            DrawUtil.strokeRect(button.getRectangle(), 0x0096FFFF, 5);
            DrawUtil.fillText(button.getName(), button.getRectangle().getX() + button.getRectangle().getWidth() / 2, 1030, Fonts.DEFAULT, 40, StringAlignment.CENTER_MIDDLE, 0xFFFFFFFF);
        }

        DrawUtil.strokeRect(selectedButton.getRectangle(), 0x00FFFFFF, 5);
        DrawUtil.fillText(selectedButton.getName(), selectedButton.getRectangle().getX() + selectedButton.getRectangle().getWidth() / 2, 1030, Fonts.DEFAULT, 40, StringAlignment.CENTER_MIDDLE, 0xFFFFFFFF);

        switch (selectedButton) {
            case HOME:
                drawHome();
                break;
            case CAMPAIGN:
                drawCampaign();
                break;
            case CUSTOM:
                drawCustom();
                break;
            case MAP_EDITOR:
                drawMapEditor();
                break;
            case REPLAYS:
                drawReplays();
                break;
        }
        if (closing) {
            DrawUtil.fillRect(0, 0, 1920, 1080, 0x00000054);
            DrawUtil.fillRect(620, 420, 680, 300, 0x646464FF);
            DrawUtil.fillRect(exitButton, 0x969696FF);
            DrawUtil.fillText("close program?", 960, 630, Fonts.DEFAULT, 50, StringAlignment.CENTER_MIDDLE, 0xFFFFFFFF);
            DrawUtil.fillText("close", 960, 510, Fonts.DEFAULT, 50, StringAlignment.CENTER_MIDDLE, 0xFFFFFFFF);
        }
    }

    private void drawHome() {

    }

    private void drawCampaign() {

    }

    private void drawCustom() {
        if (customMaps.isEmpty()) {
            return;
        }
        int start;
        if (selectedIndex - 4 < 0) {
            start = 0;
        } else if (selectedIndex > customMaps.size() - 5) {
            start = customMaps.size() - 9;
        } else {
            start = selectedIndex - 4;
        }
        for (int i = start; i < StrictMath.min(start + 9, customMaps.size()); i++) {//prevent error when custom maps size is less than 9
            DrawUtil.fillRect(50, 830-(i - start) * 100, 1000, 80, 0x000000FF);
            if (i == selectedIndex) {
                DrawUtil.strokeRect(50, 830-(i - start) * 100, 1000, 80, 0x00FFFFFF, 5);
                if (!customMaps.isEmpty()) {
                    DrawUtil.fillText(customMapsName.get(i), 525, 880-(i - start) * 100, Fonts.DEFAULT, 40, StringAlignment.CENTER_MIDDLE, 0x00FFFFFF);
                }
            } else {
                DrawUtil.strokeRect(50, 830-(i - start) * 100, 1000, 80, 0x0096FFFF, 5);
                if (!customMaps.isEmpty()) {
                    DrawUtil.fillText(customMapsName.get(i), 525, 880-(i - start) * 100, Fonts.DEFAULT, 40, StringAlignment.CENTER_MIDDLE, 0x0096FFFF);
                }
            }

        }
    }

    private void drawMapEditor() {

    }

    private void drawReplays() {
        if (replays.isEmpty()) {
            return;
        }
        int start;
        if (selectedIndex - 4 < 0) {
            start = 0;
        } else if (selectedIndex > replays.size() - 5) {
            start = replays.size() - 9;
        } else {
            start = selectedIndex - 4;
        }
        for (int i = start; i < start + 9; i++) {
            DrawUtil.fillRect(50, 930-(i - start) * 100, 1000, 80, 0x000000FF);
            if (i == selectedIndex) {
                DrawUtil.strokeRect(50, 930-(i - start) * 100, 1000, 80, 0x00FFFFFF, 5);
                DrawUtil.fillText(replaysName.get(i), 525, 880-(i - start) * 100, Fonts.DEFAULT, 40, StringAlignment.CENTER_MIDDLE, 0x00FFFFFF);
            } else {
                DrawUtil.strokeRect(50, 930-(i - start) * 100, 1000, 80, 0x0096FFFF, 5);
                DrawUtil.fillText(replaysName.get(i), 525, 880-(i - start) * 100, Fonts.DEFAULT, 40, StringAlignment.CENTER_MIDDLE, 0x0096FFFF);
            }


        }
    }

    public enum Buttons {
        HOME(new Rectangle(0, 980, 200, 100), "home"),
        CAMPAIGN(new Rectangle(200, 980, 400, 100), "campaign"),
        CUSTOM(new Rectangle(600, 980, 400, 100), "custom"),
        MAP_EDITOR(new Rectangle(1000, 980, 400, 100), "map maker"),
        REPLAYS(new Rectangle(1400, 980, 260, 100), "replays"),
        SETTINGS(new Rectangle(1660, 980, 260, 100), "settings");

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

    @Override
    public void show() {

    }

    @Override
    public void render(float v) {

    }

    @Override
    public void resize(int i, int i1) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
