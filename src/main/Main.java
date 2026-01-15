package main;

import main.game.Viewport;
import main.inputHandler.InputHandler;

import java.awt.*;

import javax.swing.*;

public class Main {
    private static JFrame window = new JFrame();
    public static void main(String[] args) {
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        window.setResizable(false);
//        window.setUndecorated(true);
        window.setTitle("java rts game");

        GamePanel gamePanel = new GamePanel();
        window.add(gamePanel);

        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);
        gamePanel.startGameThread();
    }
    public static Component getWindow(){
        return window;
    }
    public static void setFullscreen(int moniterNum){
        window.dispose();
        window.setResizable(false);
        window.setUndecorated(true);
        GraphicsDevice display = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[moniterNum];
        display.setFullScreenWindow(window);
        window.setVisible(true);
    }

    public static void setWindowed(){
        window.dispose();
        window.setResizable(true);
        window.setUndecorated(false);
        window.setVisible(true);
    }

    public static void setWindowedBorderless(int moniterNum){
        window.dispose();
        window.setResizable(false);
        window.setUndecorated(true);
        GraphicsConfiguration display = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[moniterNum].getDefaultConfiguration();
        Rectangle bounds = display.getBounds();
        window.setBounds(bounds.x, bounds.y, bounds.width, bounds.height);
        window.setVisible(true);
    }

}