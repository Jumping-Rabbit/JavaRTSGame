package inputHandler;

import game.Sounds;
import game.Viewport;
import utils.NumUtil;

import java.util.ArrayDeque;

public class InputHandler {
    public static final MouseHandler mouseHandler = new MouseHandler();
    public static final KeyHandler keyHandler = new KeyHandler();
    private static ArrayDeque<Input> inputs = new ArrayDeque<>();
    private static ArrayDeque<Input> inputsFinal = new ArrayDeque<>();


    public static KeyHandler getKeyHandler(){
        return keyHandler;
    }
    public static MouseHandler getMouseHandler(){
        return mouseHandler;
    }
    public static void tick(){
        inputsFinal = inputs;
        inputs = new ArrayDeque<>();
    };

    public static boolean MouseDown(){
        return mouseHandler.mouseDown();
    }

    protected static void addInput(Input input){
        inputs.addLast(input);
    }

    public static ArrayDeque<Input> getInputs(){
        return inputsFinal;
    }
    public static class MouseHandler {
        private double pressedX;
        private double pressedY;
        private boolean isLeftDown = false;
        private boolean dragged = false;

        public boolean mouseDown() { return isLeftDown; }

        public void handleMouse(javafx.scene.input.MouseEvent e) {
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

                if (!dragged){
                    InputType type = switch(e.getButton()) {
                        case SECONDARY -> InputType.RIGHT_CLICK;
                        case MIDDLE -> InputType.MIDDLE_CLICK;
                        default -> InputType.LEFT_CLICK;
                    };
                    InputHandler.addInput(new Input(type, mouseX, mouseY));
                }
                if (e.getButton() == javafx.scene.input.MouseButton.PRIMARY) {isLeftDown = false; dragged = false;};
            } else if (e.getEventType() == javafx.scene.input.MouseEvent.MOUSE_DRAGGED) {
                if (isLeftDown) {
                    InputHandler.addInput(new Input(InputType.DRAG, pressedX, pressedY, mouseX, mouseY));
                    dragged = true;
                }
            }
        }
//    private double pressedX;
//    private double pressedY;
//    private static boolean isLeftDown = false;
//
//    protected boolean mouseDown(){
//        return isLeftDown;
//    }
//
//    @Override
//    public void mouseClicked(MouseEvent e){
//        if (e.getButton() == MouseEvent.BUTTON1){
//            InputHandler.addInput(new Input(InputType.LEFT_CLICK, (e.getX() - Viewport.viewport.getXOffset())/Viewport.viewport.getScale(), (e.getY() - Viewport.viewport.getYOffset())/Viewport.viewport.getScale()));
//        }
//    }
//
//    @Override
//    public void mouseMoved(MouseEvent e) {
//        InputHandler.addInput(new Input(InputType.MOVE, (e.getX() - Viewport.viewport.getXOffset())/Viewport.viewport.getScale(), (e.getY() - Viewport.viewport.getYOffset())/Viewport.viewport.getScale()));
//    }
//
//    @Override
//    public void mousePressed(MouseEvent e){
//        if (e.getButton() == MouseEvent.BUTTON1){
//            isLeftDown = true;
//        }
//
//        pressedX = (e.getX() - Viewport.viewport.getXOffset())/Viewport.viewport.getScale();
//        pressedY = (e.getY() - Viewport.viewport.getYOffset())/Viewport.viewport.getScale();
//    }
//
//    @Override
//    public void mouseReleased(MouseEvent e){
//        if (e.getButton() == MouseEvent.BUTTON1) {
//            isLeftDown = false;
//        }
//        if (e.getButton() == MouseEvent.BUTTON2){
//            InputHandler.addInput(new Input(InputType.MIDDLE_CLICK, (e.getX() - Viewport.viewport.getXOffset())/Viewport.viewport.getScale(), (e.getY() - Viewport.viewport.getYOffset())/Viewport.viewport.getScale()));
//        } else if (e.getButton() == MouseEvent.BUTTON3){
//            InputHandler.addInput(new Input(InputType.RIGHT_CLICK, (e.getX() - Viewport.viewport.getXOffset())/Viewport.viewport.getScale(), (e.getY() - Viewport.viewport.getYOffset())/Viewport.viewport.getScale()));
//        }
//    }
//
//
//    @Override
//    public void mouseDragged(MouseEvent e) {
//        if (isLeftDown){
//            InputHandler.addInput(new Input(InputType.DRAG, pressedX, pressedY, (e.getX() - Viewport.viewport.getXOffset())/Viewport.viewport.getScale(), (e.getY() - Viewport.viewport.getYOffset())/Viewport.viewport.getScale()));
//        }
//    }
//
//    @Override
//    public void mouseWheelMoved(MouseWheelEvent e) {
//        InputHandler.addInput(new Input(InputType.SCROLL, (e.getX() - Viewport.viewport.getXOffset())/Viewport.viewport.getScale(), (e.getY() - Viewport.viewport.getYOffset())/Viewport.viewport.getScale(), e.getWheelRotation()));
//    }


    }


    public static class KeyHandler{
        String[] keys = {"q", "w", "e", "r", "t", "y", "u", "i", "o", "p", "a", "s", "d",
                "f", "g", "h", "j", "k", "l", "z", "x", "c", "v", "b", "n", "m",
                "1", "2", "3", "4", "5", "6", "7", "8", "9", "0",
                "shift", "control", "enter", "backspace", "space", "up", "right", "down", "left", "escape"};


//    private static final Map<Integer, String> SPECIAL_KEYS = Map.ofEntries(
//            Map.entry(KeyEvent.VK_SHIFT, "shift"),
//            Map.entry(KeyEvent.VK_CONTROL, "control"),
//            Map.entry(KeyEvent.VK_ENTER, "enter"),
//            Map.entry(KeyEvent.VK_SPACE, "space"),
//            Map.entry(KeyEvent.VK_BACK_SPACE, "backspace"),
//            Map.entry(KeyEvent.VK_UP, "up"),
//            Map.entry(KeyEvent.VK_DOWN, "down"),
//            Map.entry(KeyEvent.VK_LEFT, "left"),
//            Map.entry(KeyEvent.VK_RIGHT, "right"),
//            Map.entry(KeyEvent.VK_ESCAPE, "escape")
//    );

        public void handleKeyPress(javafx.scene.input.KeyEvent e) {
            String keyName = e.getCode().name().toLowerCase();
            if (keyName.equals("back_space")) keyName = "backspace";

            InputHandler.addInput(new Input(InputType.KEYPRESS, keyName));
        }
    }
}



