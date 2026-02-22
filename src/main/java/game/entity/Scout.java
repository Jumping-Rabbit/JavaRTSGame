
/*
package main.game.entity;

import main.game.Viewport;

import java.awt.*;
import java.util.HashMap;

import static java.lang.Math.round;

public class Scout extends Trainers {
    int hp;
    int maxHp;
    int speed;
    int armour;
    int maxArmour;
    public Scout(double scoutX, double scoutY) {
        HashMap<String, Integer> inventory = new HashMap<>();
        inventory.put("iron", 0);
        inventory.put("limestone", 0);
        inventory.put("titanium", 0);
        inventory.put("tungsten", 0);
        HashMap<String, Integer> maxInventory = new HashMap<>();
        inventory.put("iron", 50);
        inventory.put("limestone", 50);
        inventory.put("titanium", 40);
        inventory.put("tungsten", 20);
        x = scoutX;
        y = scoutY;
        hp = 100;
        maxHp = 100;
        armour = 4;
        maxArmour = 4;
        speed = 20;
    }

    @Override
    public void updateOnFrame(double timePassed) {
    }
    @Override
    public void draw(Graphics2D g2) {
        double scale = Viewport.viewport.getScale();
        g2.setColor(Color.gray);
        g2.fillOval(
                (int) round(((x - Viewport.viewport.getX()) * scale) + Viewport.viewport.getXOffset()),
                (int) round(((y - Viewport.viewport.getY()) * scale) + Viewport.viewport.getYOffset()),
                (int)round(40 * scale),
                (int)round(40 * scale));
    }
}

 */