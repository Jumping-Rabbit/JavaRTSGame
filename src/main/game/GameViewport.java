package main.game;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class GameViewport {
    private double viewportX;
    private double viewportY;
    private final double viewportWidth;
    private final double viewportHeight;
    private double scale;
    public GameViewport(double x, double y) {
        viewportX = x;
        viewportY = y;
        viewportWidth = 1920;
        viewportHeight = 1080;
        scale = 1;
    }

    public double getX() {
        return viewportX;
    }

    public void setX(double viewportX) {
        this.viewportX = viewportX;
    }

    public double getY() {
        return viewportY;
    }

    public void setY(double viewportY) {
        this.viewportY = viewportY;
    }

    public double getWidth() {
        return viewportWidth;
    }

    public double getHeight() {
        return viewportHeight;
    }

    public double getScale() {
        return scale;
    }

    public void setScale(double scale){
        this.scale = scale;
    }
}
