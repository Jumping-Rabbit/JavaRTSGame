package entity.unit;

import entity.Effects;
import entity.Entity;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public abstract class Unit extends Entity {
    private long hp;
    private int armor;
    private int speed;
    private int turnSpeed;
    private long direction;
    private long x;
    private long y;
    ArrayList<Effects> effects;
    private BufferedImage image;

}
