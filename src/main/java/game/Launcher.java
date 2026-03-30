package game;

import game.entity.unit.testRace1.Marine;
import inputHandler.InputHandler;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import utils.DrawUtil;
import utils.NumUtil;

import java.io.IOException;


public class Launcher extends Application {
    private static Stage stage;
    private static Scene scene;


    @Override
    public void start(Stage stage){
        Launcher.stage = stage;
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

        Launcher.stage.setTitle("java game");
        Launcher.stage.setScene(scene);
        Launcher.stage.show();

        Launcher.stage.setOnCloseRequest(event -> {
            close();
        });
        stage.toFront();
        stage.requestFocus();
        gamePanel.startGameThread();

    }

    public static void close(){
        Platform.exit();
        System.exit(0);
    }

    public static void setFullscreen(int moniterNum){
        Platform.runLater(() -> {
            stage.hide();
            stage = new Stage();
            stage.setFullScreen(true);
            stage.setTitle("java game");
            stage.setScene(scene);
            Launcher.stage.setOnCloseRequest(event -> {
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
            Launcher.stage.setOnCloseRequest(event -> {
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
            Launcher.stage.setOnCloseRequest(event -> {
                close();
            });
            stage.show();
            stage.toFront();
            stage.requestFocus();
            }
        );
    }
}