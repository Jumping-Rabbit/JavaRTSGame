module game {
    requires javafx.controls;
    requires javafx.media;
    requires javafx.graphics;
    requires java.desktop;
    requires tools.jackson.databind;
    opens game to javafx.graphics;
}