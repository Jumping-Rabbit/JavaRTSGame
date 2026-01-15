package main.game;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

public class Viewport implements ComponentListener {
    private double viewportX;
    private double viewportY;
    private final double viewportWidth;
    private final double viewportHeight;
    private double scale;
    private double xOffset;
    private double yOffset;
    public static final Viewport viewport = new Viewport();
    private Viewport() {
        viewportX = 0;
        viewportY = 0;
        viewportWidth = 1920;
        viewportHeight = 1080;
        calculateViewport(Toolkit.getDefaultToolkit().getScreenSize().getWidth(), Toolkit.getDefaultToolkit().getScreenSize().getHeight());
    }

    public void calculateViewport(double windowWidth, double windowHeight){
        double windowWidthScale = windowWidth / 1920d;
        double windowHeightScale = windowHeight / 1080d;
        scale = Math.min(windowWidthScale, windowHeightScale);
        xOffset = ((windowWidth - (viewportWidth * scale)) / 2d);
        yOffset = ((windowHeight - (viewportHeight * scale)) / 2d);
//        System.out.print(windowWidth + " ");
//        System.out.print(windowHeight + " ");
//        System.out.print(yOffset + " ");
//        System.out.print(scale + "\n");
    }

    public double getX() {
        return viewportX;
    }

    public void setX(double viewportX) {
        this.viewportX = viewportX;
    }

    public double getY() {
        return viewportY;
    }

    public void setY(double viewportY) {
        this.viewportY = viewportY;
    }

    public double getWidth() {
        return viewportWidth;
    }

    public double getHeight() {
        return viewportHeight;
    }

    public double getScale() {
        return scale;
    }

    public double getXOffset() {
        return xOffset;
    }

    public double getYOffset() {
        return yOffset;
    }

    @Override
    public void componentHidden(ComponentEvent e) {}

    @Override
    public void componentMoved(ComponentEvent e) {}

    @Override
    public void componentResized(ComponentEvent e) {
        calculateViewport(e.getComponent().getBounds().getWidth(), e.getComponent().getBounds().getHeight());
    }

    @Override
    public void componentShown(ComponentEvent e) {
        calculateViewport(e.getComponent().getBounds().getWidth(), e.getComponent().getBounds().getHeight());
    }
}
