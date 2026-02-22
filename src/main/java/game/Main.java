package game;

import inputHandler.InputHandler;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.io.IOException;


public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        GamePanel gamePanel = new GamePanel();
        StackPane root = new StackPane();
        gamePanel.widthProperty().bind(root.widthProperty());
        gamePanel.heightProperty().bind(root.heightProperty());
        root.getChildren().add(gamePanel);
        Scene scene = new Scene(root);

        scene.setOnKeyPressed(event -> InputHandler.getKeyHandler().handleKeyPress(event));

        scene.setOnMousePressed(e -> InputHandler.getMouseHandler().handleMouse(e));
        scene.setOnMouseReleased(e -> InputHandler.getMouseHandler().handleMouse(e));
        scene.setOnMouseDragged(e -> InputHandler.getMouseHandler().handleMouse(e));

        stage.setTitle("java game");
        stage.setScene(scene);
        stage.show();
        gamePanel.startGameThread();
        stage.setOnCloseRequest(event -> {
            Platform.exit(); // Shuts down the JavaFX application
            System.exit(0);  // Terminates the entire JVM
        });

    }

    public static void main(String[] args) {
        System.setProperty("prism.vsync", "false");
        System.setProperty("javafx.animation.fullspeed", "true");
        System.setProperty("glass.win.uiScale", "1.0");
        launch();
    }

    public static void setFullscreen(int moniterNum){

    }

    public static void setWindowed(){

    }

    public static void setWindowedBorderless(int moniterNum){

    }

}