package inputHandler;

import game.Sounds;
import game.Viewport;
import javafx.scene.input.MouseButton;
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
        boolean hasMove = false;
        for (Input input : inputsFinal){
            if (input.getInputType() == InputType.MOVE){
                hasMove = true;
                break;
            }
        }
        if (!hasMove){
            inputsFinal.addLast(mouseHandler.getMoveInput());
        }
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
        private volatile double mouseX;
        private volatile double mouseY;
        private volatile boolean hasMove;

        private void tick(){
            hasMove = false;
        }

        private Input getMoveInput(){
            return new Input(InputType.MOVE, mouseX, mouseY, false);
        }

        public boolean mouseDown() {
            return isLeftDown;
        }

        public void handleScroll(ScrollEvent e) {
            double mouseX = ((e.getX() - Viewport.getXOffset()) / Viewport.getScale());
            double mouseY = ((e.getY() - Viewport.getYOffset()) / Viewport.getScale());
            addInput(new Input(InputType.SCROLL, mouseX, mouseY, (int) Math.copySign(1, e.getDeltaY()), e.isShiftDown()));
        }

        public void handleMouse(MouseEvent e) {
            double mouseX = ((e.getX() - Viewport.getXOffset()) / Viewport.getScale());
            double mouseY = ((e.getY() - Viewport.getYOffset()) / Viewport.getScale());
            this.mouseX = mouseX;
            this.mouseY = mouseY;

            if (e.getEventType() == MouseEvent.MOUSE_PRESSED) {
                if (e.getButton() == MouseButton.PRIMARY) {
                    isLeftDown = true;
                    pressedX = mouseX;
                    pressedY = mouseY;
                    Sounds.CLICK.play();
                }
            } else if (e.getEventType() == MouseEvent.MOUSE_RELEASED) {

                if (!dragged) {
                    InputType type = switch (e.getButton()) {
                        case SECONDARY -> InputType.RIGHT_CLICK;
                        case MIDDLE -> InputType.MIDDLE_CLICK;
                        default -> InputType.LEFT_CLICK;
                    };
                    InputHandler.addInput(new Input(type, mouseX, mouseY, e.isShiftDown()));
                }
                if (e.getButton() == MouseButton.PRIMARY) {
                    isLeftDown = false;
                    dragged = false;
                }
                ;
            } else if (e.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                if (isLeftDown) {
                    InputHandler.addInput(new Input(InputType.DRAG, pressedX, pressedY, mouseX, mouseY, e.isShiftDown()));
                    dragged = true;
                }
            } else if (e.getEventType() == MouseEvent.MOUSE_MOVED){
                InputHandler.addInput(new Input(InputType.MOVE, mouseX, mouseY, e.isShiftDown()));
                hasMove = true;
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
            for (Keys key : Keys.values()) {
                stringToKeyMap.put(key.getKeyHandlerString(), key);
            }
        }


        public void handleKeyPress(javafx.scene.input.KeyEvent e) {
            InputHandler.addInput(new Input(InputType.KEYPRESS, Actions.getAction(stringToKeyMap.getOrDefault(e.getCode().name().toLowerCase(), Keys.NONE)), stringToKeyMap.getOrDefault(e.getCode().name().toLowerCase(), Keys.NONE), e.isShiftDown()));
        }
    }
}



