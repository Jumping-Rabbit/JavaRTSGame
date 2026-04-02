package inputHandler;

import game.Sounds;
import game.Viewport;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;

public class InputHandler {
    public static final MouseHandler mouseHandler = new MouseHandler();
    public static final KeyHandler keyHandler = new KeyHandler();
    private static ArrayDeque<Input> inputs = new ArrayDeque<>();
    private static ArrayDeque<Input> inputsFinal = new ArrayDeque<>();


    public static KeyHandler getKeyHandler() {
        return keyHandler;
    }

    public static MouseHandler getMouseHandler() {
        return mouseHandler;
    }

    public static void tick() {
        inputsFinal = inputs;
        inputs = new ArrayDeque<>();
    }

    ;

    public static boolean MouseDown() {
        return mouseHandler.mouseDown();
    }

    protected static void addInput(Input input) {
        inputs.addLast(input);
    }

    public static ArrayDeque<Input> getInputs() {
        return inputsFinal;
    }

    public static class MouseHandler {
        private double pressedX;
        private double pressedY;
        private boolean isLeftDown = false;
        private boolean dragged = false;

        public boolean mouseDown() {
            return isLeftDown;
        }

        public void handleScroll(ScrollEvent e) {
            double mouseX = ((e.getX() - Viewport.getXOffset()) / Viewport.getScale());
            double mouseY = ((e.getY() - Viewport.getYOffset()) / Viewport.getScale());
            addInput(new Input(InputType.SCROLL, mouseX, mouseY, (int) Math.copySign(1, e.getDeltaY())));
        }

        public void handleMouse(MouseEvent e) {
            double mouseX = ((e.getX() - Viewport.getXOffset()) / Viewport.getScale());
            double mouseY = ((e.getY() - Viewport.getYOffset()) / Viewport.getScale());

            if (e.getEventType() == javafx.scene.input.MouseEvent.MOUSE_PRESSED) {
                if (e.getButton() == javafx.scene.input.MouseButton.PRIMARY) {
                    isLeftDown = true;
                    pressedX = mouseX;
                    pressedY = mouseY;
                    Sounds.CLICK.play();
                }
            } else if (e.getEventType() == javafx.scene.input.MouseEvent.MOUSE_RELEASED) {

                if (!dragged) {
                    InputType type = switch (e.getButton()) {
                        case SECONDARY -> InputType.RIGHT_CLICK;
                        case MIDDLE -> InputType.MIDDLE_CLICK;
                        default -> InputType.LEFT_CLICK;
                    };
                    InputHandler.addInput(new Input(type, mouseX, mouseY));
                }
                if (e.getButton() == javafx.scene.input.MouseButton.PRIMARY) {
                    isLeftDown = false;
                    dragged = false;
                }
                ;
            } else if (e.getEventType() == javafx.scene.input.MouseEvent.MOUSE_DRAGGED) {
                if (isLeftDown) {
                    InputHandler.addInput(new Input(InputType.DRAG, pressedX, pressedY, mouseX, mouseY));
                    dragged = true;
                }
            }
        }
    }


    public static class KeyHandler {
        private final static Map<String, Keys> stringToKeyMap = new HashMap<>();
        String[] keys = {"q", "w", "e", "r", "t", "y", "u", "i", "o", "p", "a", "s", "d",
                "f", "g", "h", "j", "k", "l", "z", "x", "c", "v", "b", "n", "m",
                "1", "2", "3", "4", "5", "6", "7", "8", "9", "0",
                "shift", "control", "enter", "backspace", "space", "up", "right", "down", "left", "escape"};

        public static void init() {
            long startTime = System.nanoTime();
            for (Keys key : Keys.values()) {
                stringToKeyMap.put(key.getKeyHandlerString(), key);
            }
            System.out.println("keyHandler map time: " + (System.nanoTime() - startTime) / 1000000d);
        }


        public void handleKeyPress(javafx.scene.input.KeyEvent e) {
            InputHandler.addInput(new Input(InputType.KEYPRESS, stringToKeyMap.getOrDefault(e.getCode().name().toLowerCase(), Keys.NONE), e.isShiftDown()));
        }
    }
}



