package main.inputHandler;


public class Input {


    private final InputType inputType;
    private final double startX;
    private final double startY;
    private final double x;
    private final double y;
    private final String key;
    private final int scrollAmount;

    public Input(InputType inputType, double x, double y){
        this.inputType = inputType;
        this.x = x;
        this.y = y;
        startX = 0;
        startY = 0;
        key = "";
        scrollAmount = 0;
    }
    public Input(InputType inputType, double startX, double startY, double x, double y){
        this.inputType = inputType;
        this.startX = startX;
        this.startY = startY;
        this.x = x;
        this.y = y;
        key = "";
        scrollAmount = 0;
    }

    public Input(InputType inputType, String key){
        this.inputType = inputType;
        this.key = key;
        startX = 0;
        startY = 0;
        x = 0;
        y = 0;
        scrollAmount = 0;
    }
    public Input(InputType inputType, double x, double y, int scrollAmount){
        this.inputType = inputType;
        key = "";
        startX = 0;
        startY = 0;
        this.x = x;
        this.y = y;
        this.scrollAmount = scrollAmount;
    }

    public double getStartX(){
        return startX;
    }

    public double getStartY() {
        return startY;
    }

    public double getX(){
        return x;
    }

    public double getY() {
        return y;
    }

    public String getKey() {
        return key;
    }

    public int getScroll() {
        return scrollAmount;
    }

    public InputType getInputType() {
        return inputType;
    }
}
