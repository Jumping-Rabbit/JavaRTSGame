module game {
    requires javafx.controls;
    requires json.simple;
    requires javafx.media;
    requires javafx.graphics;
    requires java.desktop;
    opens game to javafx.graphics;
}