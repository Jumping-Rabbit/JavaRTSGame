package main.inputHandler;

import main.game.Viewport;

import java.awt.event.*;
import java.util.ArrayDeque;

public class InputHandler {
    private static final MouseHandler mouseHandler = new MouseHandler();
    private static final KeyHandler keyHandler = new KeyHandler();
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

    protected static void addInput(Input input){
        inputs.addLast(input);
    }

    public static ArrayDeque<Input> getInputs(){
        return inputsFinal;
    }
}

class MouseHandler extends MouseAdapter {
    private double pressedX;
    private double pressedY;
    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1){
            InputHandler.addInput(new Input(InputType.LEFT_CLICK, (e.getX() - Viewport.viewport.getXOffset())/Viewport.viewport.getScale(), (e.getY() - Viewport.viewport.getYOffset())/Viewport.viewport.getScale()));
        } else if (e.getButton() == MouseEvent.BUTTON2){
            InputHandler.addInput(new Input(InputType.MIDDLE_CLICK, (e.getX() - Viewport.viewport.getXOffset())/Viewport.viewport.getScale(), (e.getY() - Viewport.viewport.getYOffset())/Viewport.viewport.getScale()));
        } else if (e.getButton() == MouseEvent.BUTTON3){
            InputHandler.addInput(new Input(InputType.RIGHT_CLICK, (e.getX() - Viewport.viewport.getXOffset())/Viewport.viewport.getScale(), (e.getY() - Viewport.viewport.getYOffset())/Viewport.viewport.getScale()));
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        InputHandler.addInput(new Input(InputType.MOVE, (e.getX() - Viewport.viewport.getXOffset())/Viewport.viewport.getScale(), (e.getY() - Viewport.viewport.getYOffset())/Viewport.viewport.getScale()));
    }

    @Override
    public void mousePressed(MouseEvent e){
        pressedX = (e.getX() - Viewport.viewport.getXOffset())/Viewport.viewport.getScale();
        pressedY = (e.getY() - Viewport.viewport.getYOffset())/Viewport.viewport.getScale();
    }


    @Override
    public void mouseDragged(MouseEvent e) {
        InputHandler.addInput(new Input(InputType.DRAG, pressedX, pressedY, (e.getX() - Viewport.viewport.getXOffset())/Viewport.viewport.getScale(), (e.getY() - Viewport.viewport.getYOffset())/Viewport.viewport.getScale()));
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        InputHandler.addInput(new Input(InputType.SCROLL, (e.getX() - Viewport.viewport.getXOffset())/Viewport.viewport.getScale(), (e.getY() - Viewport.viewport.getYOffset())/Viewport.viewport.getScale(), e.getWheelRotation()));
    }

}


class KeyHandler implements KeyListener {
    String[] keys = {"q", "w", "e", "r", "t", "y", "u", "i", "o", "p", "a", "s", "d",
            "f", "g", "h", "j", "k", "l", "z", "x", "c", "v", "b", "n", "m",
            "1", "2", "3", "4", "5", "6", "7", "8", "9", "0",
            "shift", "control", "enter", "backspace", "space", "up", "right", "down", "left", "escape"};

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_Q) {
            InputHandler.addInput(new Input(InputType.KEYPRESS, "q"));
        }
        if (code == KeyEvent.VK_W) {
            InputHandler.addInput(new Input(InputType.KEYPRESS, "w"));
        }
        if (code == KeyEvent.VK_E) {
            InputHandler.addInput(new Input(InputType.KEYPRESS, "e"));
        }
        if (code == KeyEvent.VK_R) {
            InputHandler.addInput(new Input(InputType.KEYPRESS, "r"));
        }
        if (code == KeyEvent.VK_T) {
            InputHandler.addInput(new Input(InputType.KEYPRESS, "t"));
        }
        if (code == KeyEvent.VK_Y) {
            InputHandler.addInput(new Input(InputType.KEYPRESS, "y"));
        }
        if (code == KeyEvent.VK_U) {
            InputHandler.addInput(new Input(InputType.KEYPRESS, "u"));
        }
        if (code == KeyEvent.VK_I) {
            InputHandler.addInput(new Input(InputType.KEYPRESS, "i"));
        }
        if (code == KeyEvent.VK_O) {
            InputHandler.addInput(new Input(InputType.KEYPRESS, "o"));
        }
        if (code == KeyEvent.VK_P) {
            InputHandler.addInput(new Input(InputType.KEYPRESS, "p"));
        }
        if (code == KeyEvent.VK_A) {
            InputHandler.addInput(new Input(InputType.KEYPRESS, "a"));
        }
        if (code == KeyEvent.VK_S) {
            InputHandler.addInput(new Input(InputType.KEYPRESS, "s"));
        }
        if (code == KeyEvent.VK_D) {
            InputHandler.addInput(new Input(InputType.KEYPRESS, "d"));
        }
        if (code == KeyEvent.VK_F) {
            InputHandler.addInput(new Input(InputType.KEYPRESS, "f"));
        }
        if (code == KeyEvent.VK_G) {
            InputHandler.addInput(new Input(InputType.KEYPRESS, "g"));
        }
        if (code == KeyEvent.VK_H) {
            InputHandler.addInput(new Input(InputType.KEYPRESS, "h"));
        }
        if (code == KeyEvent.VK_J) {
            InputHandler.addInput(new Input(InputType.KEYPRESS, "j"));
        }
        if (code == KeyEvent.VK_K) {
            InputHandler.addInput(new Input(InputType.KEYPRESS, "k"));
        }
        if (code == KeyEvent.VK_L) {
            InputHandler.addInput(new Input(InputType.KEYPRESS, "l"));
        }
        if (code == KeyEvent.VK_Z) {
            InputHandler.addInput(new Input(InputType.KEYPRESS, "z"));
        }
        if (code == KeyEvent.VK_X) {
            InputHandler.addInput(new Input(InputType.KEYPRESS, "x"));
        }
        if (code == KeyEvent.VK_C) {
            InputHandler.addInput(new Input(InputType.KEYPRESS, "c"));
        }
        if (code == KeyEvent.VK_V) {
            InputHandler.addInput(new Input(InputType.KEYPRESS, "v"));
        }
        if (code == KeyEvent.VK_B) {
            InputHandler.addInput(new Input(InputType.KEYPRESS, "b"));
        }
        if (code == KeyEvent.VK_N) {
            InputHandler.addInput(new Input(InputType.KEYPRESS, "n"));
        }
        if (code == KeyEvent.VK_M) {
            InputHandler.addInput(new Input(InputType.KEYPRESS, "m"));
        }
        if (code == KeyEvent.VK_1) {
            InputHandler.addInput(new Input(InputType.KEYPRESS, "1"));
        }
        if (code == KeyEvent.VK_2) {
            InputHandler.addInput(new Input(InputType.KEYPRESS, "2"));
        }
        if (code == KeyEvent.VK_3) {
            InputHandler.addInput(new Input(InputType.KEYPRESS, "3"));
        }
        if (code == KeyEvent.VK_4) {
            InputHandler.addInput(new Input(InputType.KEYPRESS, "4"));
        }
        if (code == KeyEvent.VK_5) {
            InputHandler.addInput(new Input(InputType.KEYPRESS, "5"));
        }
        if (code == KeyEvent.VK_6) {
            InputHandler.addInput(new Input(InputType.KEYPRESS, "6"));
        }
        if (code == KeyEvent.VK_7) {
            InputHandler.addInput(new Input(InputType.KEYPRESS, "7"));
        }
        if (code == KeyEvent.VK_8) {
            InputHandler.addInput(new Input(InputType.KEYPRESS, "8"));
        }
        if (code == KeyEvent.VK_9) {
            InputHandler.addInput(new Input(InputType.KEYPRESS, "9"));
        }
        if (code == KeyEvent.VK_0) {
            InputHandler.addInput(new Input(InputType.KEYPRESS, "0"));
        }
        if (code == KeyEvent.VK_SHIFT) {
            InputHandler.addInput(new Input(InputType.KEYPRESS, "shift"));
        }
        if (code == KeyEvent.VK_CONTROL) {
            InputHandler.addInput(new Input(InputType.KEYPRESS, "control"));
        }
        if (code == KeyEvent.VK_ENTER) {
            InputHandler.addInput(new Input(InputType.KEYPRESS, "enter"));
        }
        if (code == KeyEvent.VK_SPACE) {
            InputHandler.addInput(new Input(InputType.KEYPRESS, "space"));
        }
        if (code == KeyEvent.VK_BACK_SPACE) {
            InputHandler.addInput(new Input(InputType.KEYPRESS, "backspace"));
        }
        if (code == KeyEvent.VK_UP) {
            InputHandler.addInput(new Input(InputType.KEYPRESS, "up"));
        }
        if (code == KeyEvent.VK_RIGHT) {
            InputHandler.addInput(new Input(InputType.KEYPRESS, "right"));
        }
        if (code == KeyEvent.VK_DOWN) {
            InputHandler.addInput(new Input(InputType.KEYPRESS, "down"));
        }
        if (code == KeyEvent.VK_LEFT) {
            InputHandler.addInput(new Input(InputType.KEYPRESS, "left"));
        }
        if (code == KeyEvent.VK_ESCAPE) {
            InputHandler.addInput(new Input(InputType.KEYPRESS, "escape"));
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
    }
}

