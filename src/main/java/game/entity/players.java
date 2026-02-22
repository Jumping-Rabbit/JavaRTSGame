package game.entity;

import java.awt.*;

public enum players {
    RED(new Color(255, 0, 0)),
    BLUE(new Color(0, 0, 255));
    Color color;

    players(Color color) {
        this.color = color;
    }

    public Color getColor(){
        return color;
    }


}
