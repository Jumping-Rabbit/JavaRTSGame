package inputHandler;


public class Input {


    private final InputType inputType;
    private final double startX;
    private final double startY;
    private final double x;
    private final double y;
    private final Actions action;
    private final Keys key;
    private final int scrollAmount;
    private boolean isShiftHeld;

    public Input(InputType inputType, double x, double y, boolean isShiftHeld) {//mouse click
        this.inputType = inputType;
        this.x = x;
        this.y = y;
        startX = 0;
        startY = 0;
        action = Actions.NONE;
        key = Keys.NONE;
        scrollAmount = 0;
        this.isShiftHeld = isShiftHeld;
    }

    public Input(InputType inputType, double startX, double startY, double x, double y, boolean isShiftHeld) {//drag
        this.inputType = inputType;
        this.startX = startX;
        this.startY = startY;
        this.x = x;
        this.y = y;
        action = Actions.NONE;
        key = Keys.NONE;
        scrollAmount = 0;
        this.isShiftHeld = isShiftHeld;
    }

    public Input(InputType inputType, Actions action, Keys key, boolean isShiftHeld) {//type
        this.inputType = inputType;
        this.action = action;
        this.key = key;
        startX = 0;
        startY = 0;
        x = 0;
        y = 0;
        scrollAmount = 0;
        this.isShiftHeld = isShiftHeld;
    }

    public Input(InputType inputType, double x, double y, int scrollAmount, boolean isShiftHeld) {
        this.inputType = inputType;
        action = Actions.NONE;
        key = Keys.NONE;
        startX = 0;
        startY = 0;
        this.x = x;
        this.y = y;
        this.scrollAmount = scrollAmount;
        this.isShiftHeld = isShiftHeld;
    }

    public double getStartX() {
        return startX;
    }

    public double getStartY() {
        return startY;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public Actions getAction() {
        return action;
    }

    public Keys getKey(){
        return key;
    }

    public int getScroll() {
        return scrollAmount;
    }

    public InputType getInputType() {
        return inputType;
    }

    public boolean getIsShiftHeld() {
        return isShiftHeld;
    }
}
