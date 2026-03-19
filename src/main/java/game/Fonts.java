package game;

import javafx.scene.text.Font;

import java.io.File;

public enum Fonts{
    DEFAULT(Font.loadFont(new File("resources/fonts/SpaceMono-Regular.ttf").toURI().toString(), 12));
    private final Font font;
    Fonts(Font font){
        this.font = font;
    }
    public Font getFont(double size){
        return Font.font(font.getFamily(), size);
    }
}
