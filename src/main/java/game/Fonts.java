package game;

import javafx.scene.text.Font;

public enum Fonts{
    DEFAULT(Font.loadFont(Fonts.class.getResourceAsStream("/fonts/SpaceMono-Regular.ttf"), 12));
    private final Font font;
    Fonts(Font font){
        this.font = font;
    }
    public Font getFont(double size){
        return Font.font(font.getFamily(), size);
    }
}
