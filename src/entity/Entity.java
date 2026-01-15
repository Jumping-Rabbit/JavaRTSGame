package entity;

import main.DrawUtil;

import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class Entity {
    private double x;
    private double y;
    private double hasCollision;
//    private BufferedImage image;

    public abstract void draw(DrawUtil drawUtil);
    public abstract void updateOnFrame();
}
