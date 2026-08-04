package com.game.inputHandler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector3;
import com.game.Sounds;
import com.game.utils.DrawUtil;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;

public class InputHandler implements InputProcessor {
    public static final MouseHandler mouseHandler = new MouseHandler();
    public static final KeyHandler keyHandler = new KeyHandler();
    private static ArrayDeque<Input> inputs = new ArrayDeque<>();
    private static ArrayDeque<Input> inputsFinal = new ArrayDeque<>();


    private static final Vector3 tempCords = new Vector3();

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
        for (Input input : inputsFinal) {
            if (input.getInputType() == InputType.MOVE) {
                hasMove = true;
                break;
            }
        }
        if (!hasMove) {
            inputsFinal.addLast(mouseHandler.getMoveInput());
        }
    }

    public static boolean MouseDown() {
        return mouseHandler.mouseDown();
    }

    protected static void addInput(Input input) {
        inputs.addLast(input);
    }

    public static ArrayDeque<Input> getInputs() {
        return inputsFinal;
    }

    private static boolean isShiftDown() {
        return Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.SHIFT_LEFT) ||
                Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.SHIFT_RIGHT);
    }

    private static boolean isControlDown() {
        return Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.CONTROL_LEFT) ||
                Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.CONTROL_RIGHT);
    }




    @Override
    public boolean keyDown(int keycode) {
        keyHandler.handleKeyPress(keycode);
        return true;
    }

    @Override
    public boolean keyUp(int keycode) { return false; }

    @Override
    public boolean keyTyped(char character) { return false; }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        mouseHandler.handleTouchDown(screenX, screenY, button);
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        mouseHandler.handleTouchUp(screenX, screenY, button);
        return true;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) { return false; }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        mouseHandler.handleTouchDragged(screenX, screenY);
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        mouseHandler.handleMouseMoved(screenX, screenY);
        return true;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        mouseHandler.handleScroll(amountY);
        return true;
    }

    public static class MouseHandler {
        private float pressedX;
        private float pressedY;
        private boolean isLeftDown = false;
        private boolean dragged = false;
        private volatile float mouseX;
        private volatile float mouseY;
        private volatile boolean hasMove;

        private void tick() {
            hasMove = false;
        }

        private Input getMoveInput() {
            return new Input(InputType.MOVE, mouseX, mouseY, false, false);
        }

        public boolean mouseDown() {
            return isLeftDown;
        }

        private void updateCoordinates(int screenX, int screenY) {
            if (DrawUtil.viewport != null) {
                tempCords.set(screenX, screenY, 0);
                DrawUtil.viewport.unproject(tempCords);
                this.mouseX = tempCords.x;
                this.mouseY = tempCords.y;
            } else {
                this.mouseX = screenX;
                this.mouseY = screenY;
            }
        }

        public void handleScroll(float deltaY) {
            addInput(new Input(InputType.SCROLL, mouseX, mouseY, (int) Math.copySign(1, deltaY), isShiftDown(), isControlDown()));
        }

        public void handleTouchDown(int screenX, int screenY, int button) {
            updateCoordinates(screenX, screenY);

            if (button == Buttons.LEFT) {
                isLeftDown = true;
                pressedX = mouseX;
                pressedY = mouseY;
                Sounds.CLICK.play();
            }
        }

        public void handleTouchUp(int screenX, int screenY, int button) {
            updateCoordinates(screenX, screenY);

            if (!dragged) {
                InputType type = switch (button) {
                    case Buttons.RIGHT -> InputType.RIGHT_CLICK;
                    case Buttons.MIDDLE -> InputType.MIDDLE_CLICK;
                    default -> InputType.LEFT_CLICK;
                };
                InputHandler.addInput(new Input(type, mouseX, mouseY, isShiftDown(), isControlDown()));
            }
            if (button == Buttons.LEFT) {
                isLeftDown = false;
                dragged = false;
            }
        }

        public void handleTouchDragged(int screenX, int screenY) {
            updateCoordinates(screenX, screenY);

            if (isLeftDown) {
                InputHandler.addInput(new Input(InputType.DRAG, pressedX, pressedY, mouseX, mouseY, isShiftDown(), isControlDown()));
                dragged = true;
            }
        }

        public void handleMouseMoved(int screenX, int screenY) {
            updateCoordinates(screenX, screenY);

            InputHandler.addInput(new Input(InputType.MOVE, mouseX, mouseY, isShiftDown(), isControlDown()));
            hasMove = true;
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

        public void handleKeyPress(int keycode) {
            String keyName = com.badlogic.gdx.Input.Keys.toString(keycode).toLowerCase();
            InputHandler.addInput(new Input(
                    InputType.KEYPRESS,
                    Actions.getAction(stringToKeyMap.getOrDefault(keyName, Keys.NONE)),
                    stringToKeyMap.getOrDefault(keyName, Keys.NONE),
                    isShiftDown(),
                    isControlDown()
            ));
//            System.out.println(keyName);
        }
    }
}