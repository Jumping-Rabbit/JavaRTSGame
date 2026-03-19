package game;

import inputHandler.InputHandler;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;


public class Main extends Application {
    private static Stage stage;
    private static Scene scene;
    @Override
    public void start(Stage stage) throws IOException {
        Main.stage = stage;
        GamePanel gamePanel = new GamePanel();
        StackPane root = new StackPane();
        gamePanel.widthProperty().bind(root.widthProperty());
        gamePanel.heightProperty().bind(root.heightProperty());
        root.getChildren().add(gamePanel);
        scene = new Scene(root);

        scene.setOnKeyPressed(event -> InputHandler.getKeyHandler().handleKeyPress(event));

        scene.setOnMousePressed(e -> InputHandler.getMouseHandler().handleMouse(e));
        scene.setOnMouseReleased(e -> InputHandler.getMouseHandler().handleMouse(e));
        scene.setOnMouseDragged(e -> InputHandler.getMouseHandler().handleMouse(e));
        scene.setOnScroll(e -> InputHandler.getMouseHandler().handleScroll(e));

        Main.stage.setTitle("java game");
        Main.stage.setScene(scene);
        Main.stage.show();
        gamePanel.startGameThread();
        Main.stage.setOnCloseRequest(event -> {
            close();
        });

    }

    public static void close(){
        Platform.exit();
        System.exit(0);
    }

    public static void main(String[] args) {
        System.setProperty("prism.vsync", "false");
        System.setProperty("javafx.animation.fullspeed", "true");
        System.setProperty("glass.win.uiScale", "1.0");
        launch();
    }

    public static void setFullscreen(int moniterNum){
        Platform.runLater(() -> {
            stage.hide();
            stage = new Stage();
            stage.setFullScreen(true);
            stage.setTitle("java game");
            stage.setScene(scene);
            Main.stage.setOnCloseRequest(event -> {
                close();
            });
            stage.show();
            stage.toFront();
            stage.requestFocus();
            }
        );

    }

    public static void setWindowed(){
        Platform.runLater(() -> {
            stage.hide();
            stage = new Stage();
            stage.initStyle(StageStyle.DECORATED);
            stage.setFullScreen(false);
            stage.setMaximized(true);
            stage.setTitle("java game");
            stage.setScene(scene);
            Main.stage.setOnCloseRequest(event -> {
                close();
            });
            stage.show();
            stage.toFront();
            stage.requestFocus();
            }
        );
    }

    public static void setWindowedBorderless(int moniterNum){
        Platform.runLater(() -> {
            stage.hide();
            stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setFullScreen(false);
            stage.setMaximized(true);
            stage.setTitle("java game");
            stage.setScene(scene);
            Main.stage.setOnCloseRequest(event -> {
                close();
            });
            stage.show();
            stage.toFront();
            stage.requestFocus();
            }
        );
    }
}