package game;

import javafx.application.Application;

public class Main {
    public static void main(String[] args) {//this class is just here to make the javafx native warnings shut up
        System.setProperty("prism.order", "d3d,es2,sw");//try to use the best one

//        System.setProperty("prism.verbose", "true");//for testing
        //        System.setProperty("prism.poolstats", "true");

        System.setProperty("prism.vsync", "false");
        System.setProperty("prism.multisample", "false");
        System.setProperty("javafx.animation.fullspeed", "true");
        System.setProperty("prism.allowhidpi", "false");
        System.setProperty("prism.lcdtext", "false");
        System.setProperty("prism.cacheshapes", "true");
        System.setProperty("prism.maxvram", "4G");
        System.setProperty("prism.dirtyopts", "true");

        System.setProperty("glass.gtk.uiScale", "1.0");
        System.setProperty("glass.win.uiScale", "1.0");
        System.setProperty("glass.macosx.uiScale", "1.0");

//        PrintStream originalErr = System.err;
//        Systebm.setErr(new PrintStream(OutputStream.nullOutputStream()));
//        try {
        Application.launch(Launcher.class, args);
//        } finally {
//            System.setErr(originalErr);
//        }
    }
}