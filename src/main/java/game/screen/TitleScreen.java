package game.screen;


import game.Fonts;
import game.Launcher;
import inputHandler.Input;
import inputHandler.InputHandler;
import inputHandler.Keys;
import javafx.geometry.Rectangle2D;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import utils.CollisionUtil;
import utils.DrawUtil;
import utils.StringAlignment;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class TitleScreen extends Screen {
    private Rectangle2D exitButton = new Rectangle2D(760, 520, 400, 100);
    private Buttons selectedButton = Buttons.HOME;
    private ArrayList<File> customMaps = new ArrayList<>();
    private ArrayList<File> replays = new ArrayList<>();
    private ArrayList<String> customMapsName = new ArrayList<>();
    private ArrayList<String> replaysName = new ArrayList<>();
    private int selectedIndex = 0;
    private boolean closing = false;
    private DrawUtil drawUtil;

    public TitleScreen(DrawUtil drawUtil) {
        this.drawUtil = drawUtil;
    }

    private TitleScreen(TitleScreen titleScreen) {//copy constructor
        selectedButton = titleScreen.selectedButton;
        customMaps = new ArrayList<>(Arrays.asList(new File("resources/map/custom").listFiles()));
        replays = new ArrayList<>(Arrays.asList(new File("resources/replays").listFiles()));
        selectedIndex = titleScreen.selectedIndex;
        exit = titleScreen.exit;
        drawUtil = titleScreen.drawUtil;
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

    public Screen copy() {
        return new TitleScreen(this);
    }

    public void updateOnFrame() {
        for (Input input : InputHandler.getInputs()) {
            switch (input.getInputType()) {
                case KEYPRESS:
                    if (input.getKey() == Keys.A || input.getKey() == Keys.LEFT) {
                        Buttons oldButton = selectedButton;
                        selectedButton = Buttons.values()[(selectedButton.ordinal() - 1) >= 0 ? (selectedButton.ordinal() - 1) : (Buttons.values().length - 1)];
                        if (selectedButton != oldButton) {
                            selectedIndex = 0;
                        }
                    } else if (input.getKey() == Keys.D || input.getKey() == Keys.RIGHT) {
                        Buttons oldButton = selectedButton;
                        selectedButton = Buttons.values()[(selectedButton.ordinal() + 1) % Buttons.values().length];
                        if (selectedButton != oldButton) {
                            selectedIndex = 0;
                        }
                    } else if (input.getKey() == Keys.W) {
                        selectedIndex--;
                        if (selectedIndex < 0) {
                            selectedIndex = 0;
                        }
                    } else if (input.getKey() == Keys.S) {
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
                    } else if (input.getKey() == Keys.ENTER) {
                        switch (selectedButton) {
                            case CUSTOM, REPLAYS:
                                exit = true;
                                break;
                        }
                    } else if (input.getKey() == Keys.ESCAPE) {
                        closing = !closing;
                    }
                    break;
                case LEFT_CLICK:
                    if (closing) {
                        if (CollisionUtil.RectPointCollision(exitButton, input.getX(), input.getY())) {
                            Launcher.close();
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
                            if (input.getY() > 150 && input.getY() < 1050 && input.getX() > 50 && input.getX() < 1050) {
                                selectedIndex += input.getScroll();
                                if (selectedIndex < 0) {
                                    selectedIndex = 0;
                                } else if (selectedIndex > (customMaps.size() - 1)) {
                                    selectedIndex = customMaps.size() - 1;
                                }
                            }
                            break;
                        case REPLAYS:
                            if (input.getY() > 150 && input.getY() < 1050 && input.getX() > 50 && input.getX() < 1050) {
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
            customMaps = new ArrayList<>(Arrays.asList((new File("resources/map/custom").listFiles())));
            customMapsName = new ArrayList<>();
            for (File customMap : customMaps) {
                var objectMapper = new ObjectMapper();
                JsonNode map = objectMapper.readTree(new File(customMap.getPath() + "/map.json"));
                customMapsName.add(map.get("name").stringValue());
            }

        }
        if (selectedButton == Buttons.REPLAYS) {
            replays = new ArrayList<>(Arrays.asList((new File("resources/replays").listFiles())));
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
        drawUtil.setColor(75, 75, 75);
        drawUtil.fillRect(0, 0, 1920, 100);

        drawUtil.setThickness(5);
        drawUtil.setColor(0, 150, 255);
        for (Buttons button : Buttons.values()) {
            if (button == selectedButton) {
                continue;
            }
            drawUtil.strokeRect(button.getRectangle());
            drawUtil.drawString(button.getRectangle().getMinX() + button.getRectangle().getWidth() / 2, 50, button.getName(), 40, Fonts.DEFAULT, StringAlignment.CENTER_MIDDLE);
        }

        drawUtil.setColor(0, 255, 255);
        drawUtil.strokeRect(selectedButton.getRectangle());
        drawUtil.drawString(selectedButton.getRectangle().getMinX() + selectedButton.getRectangle().getWidth() / 2, 50, selectedButton.getName(), 40, Fonts.DEFAULT, StringAlignment.CENTER_MIDDLE);

        switch (selectedButton) {
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
        if (closing) {
            drawUtil.setColor(0, 0, 0, 0.33);
            drawUtil.fillRect(0, 0, 1920, 1080);
            drawUtil.setColor(100, 100, 100);
            drawUtil.fillRect(620, 390, 680, 300);
            drawUtil.setColor(150, 150, 150);
            drawUtil.fillRect(exitButton);
            drawUtil.setColor(255, 255, 255);
            drawUtil.drawString(960, 450, "close program?", 50, Fonts.DEFAULT, StringAlignment.CENTER_MIDDLE);
            drawUtil.drawString(960, 570, "close", 50, Fonts.DEFAULT, StringAlignment.CENTER_MIDDLE);
        }
    }

    private void drawHome(DrawUtil drawUtil) {

    }

    private void drawCampaign(DrawUtil drawUtil) {

    }

    private void drawCustom(DrawUtil drawUtil) {
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
            drawUtil.setColor(0, 0, 0);
            drawUtil.fillRect(50, (i - start) * 100 + 150, 1000, 80);
            if (i == selectedIndex) {
                drawUtil.setColor(0, 255, 255);
            } else {
                drawUtil.setColor(0, 150, 255);
            }
            drawUtil.strokeRect(50, (i - start) * 100 + 150, 1000, 80);
            if (!customMaps.isEmpty()) {
                drawUtil.drawString(525, (i - start) * 100 + 200, customMapsName.get(i), 40, Fonts.DEFAULT, StringAlignment.CENTER_MIDDLE);
            }
        }
    }

    private void drawMapEditor(DrawUtil drawUtil) {

    }

    private void drawReplays(DrawUtil drawUtil) {
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
            drawUtil.setColor(0, 0, 0);
            drawUtil.fillRect(50, (i - start) * 100 + 150, 1000, 80);
            if (i == selectedIndex) {
                drawUtil.setColor(0, 255, 255);
            } else {
                drawUtil.setColor(0, 150, 255);
            }
            drawUtil.strokeRect(50, (i - start) * 100 + 150, 1000, 80);
            drawUtil.drawString(525, (i - start) * 100 + 200, replaysName.get(i), 40, Fonts.DEFAULT, StringAlignment.CENTER_MIDDLE);

        }
    }

    public enum Buttons {
        HOME(new Rectangle2D(0, 0, 200, 100), "home"),
        CAMPAIGN(new Rectangle2D(200, 0, 400, 100), "campaign"),
        CUSTOM(new Rectangle2D(600, 0, 400, 100), "custom"),
        MAP_EDITOR(new Rectangle2D(1000, 0, 400, 100), "map maker"),
        REPLAYS(new Rectangle2D(1400, 0, 260, 100), "replays"),
        SETTINGS(new Rectangle2D(1660, 0, 260, 100), "settings");

        private final Rectangle2D rectangle;
        private final String name;

        Buttons(Rectangle2D rectangle, String name) {
            this.rectangle = rectangle;
            this.name = name;
        }

        private String getName() {
            return name;
        }

        private Rectangle2D getRectangle() {
            return rectangle;
        }
    }
}
