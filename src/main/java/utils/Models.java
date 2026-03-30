package utils;

import game.SettingsManager;

public enum Models {
    vanguardMarine;

    private double width;
    private double height;
    private double halfWidth;
    private double halfHeight;
    private long scaledWidth;
    private long scaledHeight;
    private long scaledHalfWidth;
    private long scaledHalfHeight;



    public static Models fromValue(String givenName) {
        for (Models model : values()) {
            if (model.toString().equalsIgnoreCase(givenName)) {
                return model;
            }
        }
        return null;
    }

    public double getWidth(){
        return width;
    }
    public void setWidth(double width){
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getHalfWidth() {
        return halfWidth;
    }

    public void setHalfWidth(double halfWidth) {
        this.halfWidth = halfWidth;
    }

    public double getHalfHeight() {
        return halfHeight;
    }

    public void setHalfHeight(double halfHeight) {
        this.halfHeight = halfHeight;
    }

    public long getScaledWidth() {
        return scaledWidth;
    }

    public void setScaledWidth(long scaledWidth) {
        this.scaledWidth = scaledWidth;
    }

    public long getScaledHeight() {
        return scaledHeight;
    }

    public void setScaledHeight(long scaledHeight) {
        this.scaledHeight = scaledHeight;
    }

    public long getScaledHalfWidth() {
        return scaledHalfWidth;
    }

    public void setScaledHalfWidth(long scaledHalfWidth) {
        this.scaledHalfWidth = scaledHalfWidth;
    }

    public long getScaledHalfHeight() {
        return scaledHalfHeight;
    }

    public void setScaledHalfHeight(long scaledHalfHeight) {
        this.scaledHalfHeight = scaledHalfHeight;
    }
}
