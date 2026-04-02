package utils;

import game.Fonts;
import game.GameViewport;
import game.Viewport;
import game.screen.LoadingScreen;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import tools.jackson.databind.ObjectMapper;

import java.util.EnumMap;

public class DrawUtil {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static EnumMap<Models, WritableImage[]> modelMap;
    GameViewport gameViewport;
    double factor = 0;
    private GraphicsContext gc;

    public static void init(LoadingScreen loadingScreen) {
        long startTime = System.nanoTime();
        modelMap = ModelLoaderUtil.calculateModelImages(loadingScreen);
        System.out.println("parse model time: " + (System.nanoTime() - startTime) / 1000000d);
    }

    public synchronized double getFactor() {
        return factor;
    }

    public synchronized void setFactor(double factor) {
        this.factor = StrictMath.clamp(factor, 0, 1);
        ;
    }

    public void setGameViewport(GameViewport gameViewport) {
        this.gameViewport = gameViewport;
    }

    public GraphicsContext getGC() {
        return gc;
    }

    public void setGC(GraphicsContext gc) {
        this.gc = gc;
    }

    public void startRotation(double x1, double y1, double x2, double y2, double xOffset, double yOffset, double direction1, double direction2) {
        double x;
        double y;
        double scale = Viewport.getScale();
        if (gameViewport != null) {
            x = (NumUtil.interpolate(x1, x2, factor) - gameViewport.getX() - Viewport.getX() + xOffset) * scale + Viewport.getXOffset();
            y = (NumUtil.interpolate(y1, y2, factor) - gameViewport.getY() - Viewport.getY() + yOffset) * scale + Viewport.getYOffset();
        } else {
            x = (NumUtil.interpolate(x1, x2, factor) - Viewport.getX() + xOffset) * scale + Viewport.getXOffset();
            y = (NumUtil.interpolate(y1, y2, factor) - Viewport.getY() + yOffset) * scale + Viewport.getYOffset();
        }
        rotate(x, y, NumUtil.interpolate(direction1, direction2, factor));
    }

    private void rotate(double x, double y, double rotation) {
        gc.save();
        gc.translate(x, y);
        gc.rotate(rotation);
        gc.translate(-x, -y);
    }

    public void resetRotation() {
        gc.restore();
    }


    public void setColor(int r, int g, int b) {
        gc.setFill(Color.rgb(r, g, b));
        gc.setStroke(Color.rgb(r, g, b));
    }

    public void setColor(int r, int g, int b, double transparency) {
        gc.setFill(Color.rgb(r, g, b, transparency));
        gc.setStroke(Color.rgb(r, g, b, transparency));
    }

    public void setColor(Color color) {
        gc.setFill(color);
        gc.setStroke(color);
    }

    public void setThickness(double thickness) {
        gc.setLineWidth(thickness * Viewport.getScale());
    }


    public void fillRect(double x, double y, double width, double height) {
        double scale = Viewport.getScale();
        gc.fillRect((x - Viewport.getX()) * scale + Viewport.getXOffset(), (y - Viewport.getY()) * scale + Viewport.getYOffset(), width * scale, height * scale);
    }

    public void fillRect(Rectangle2D rect) {
        fillRect(rect.getMinX(), rect.getMinY(), rect.getWidth(), rect.getHeight());
    }

    public void fillRectGame(double x, double y, double width, double height) {
        if (!CollisionUtil.RectRectCollision(gameViewport.getX(), gameViewport.getY(), gameViewport.getWidth(), gameViewport.getHeight(), x, y, width, height)) {
            return;
        }
        fillRect(x, y, width, height);
    }

    public void fillRectInterpolate(double xCurrent, double yCurrent, double xLast, double yLast, double width, double height) {
        double x = NumUtil.interpolate(xCurrent, xLast, factor);
        double y = NumUtil.interpolate(yCurrent, yLast, factor);
        fillRect(x, y, width, height);
    }

    public void fillRectInterpolateGame(double xCurrent, double yCurrent, double xLast, double yLast, double width, double height) {
        double x = NumUtil.interpolate(xCurrent, xLast, factor);
        double y = NumUtil.interpolate(yCurrent, yLast, factor);
        if (!CollisionUtil.RectRectCollision(gameViewport.getX(), gameViewport.getY(), gameViewport.getWidth(), gameViewport.getHeight(), x, y, width, height)) {
            return;
        }
        fillRect(x - gameViewport.getX(), y - gameViewport.getY(), width, height);
    }

    public void strokeRect(double x, double y, double width, double height) {
        double scale = Viewport.getScale();
        gc.strokeRect((x - Viewport.getX()) * scale + Viewport.getXOffset(), (y - Viewport.getY()) * scale + Viewport.getYOffset(), width * scale, height * scale);
    }

    public void strokeRect(Rectangle2D rect) {
        strokeRect(rect.getMinX(), rect.getMinY(), rect.getWidth(), rect.getHeight());
    }

    public void strokeRectGame(double x, double y, double width, double height) {
        if (!CollisionUtil.RectRectCollision(gameViewport.getX(), gameViewport.getY(), gameViewport.getWidth(), gameViewport.getHeight(), x, y, width, height)) {
            return;
        }
        strokeRect(x, y, width, height);
    }


    public void fillCircle(double x, double y, double radius) {
        double scale = Viewport.getScale();
        gc.fillOval((x - Viewport.getX()) * scale + Viewport.getXOffset(), (y - Viewport.getY()) * scale + Viewport.getYOffset(), radius * 2 * scale, radius * 2 * scale);
    }

    public void fillCircleGame(double x, double y, double radius) {
        if (!CollisionUtil.RectCircleCollision(x, y, radius, gameViewport.getX(), gameViewport.getY(), gameViewport.getWidth(), gameViewport.getHeight())) {
            return;
        }
        fillCircle(x - gameViewport.getX(), y - gameViewport.getY(), radius);
    }

    public void fillCircleInterpolate(double xCurrent, double yCurrent, double xLast, double yLast, double radius) {
        double x = NumUtil.interpolate(xCurrent, xLast, factor);
        double y = NumUtil.interpolate(yCurrent, yLast, factor);
        double scale = Viewport.getScale();
        fillCircle(x, y, radius);
    }

    public void fillCircleInterpolateGame(double xCurrent, double yCurrent, double xLast, double yLast, double radius) {
        double x = NumUtil.interpolate(xCurrent, xLast, factor);
        double y = NumUtil.interpolate(yCurrent, yLast, factor);
        if (!CollisionUtil.RectCircleCollision(x, y, radius, gameViewport.getX(), gameViewport.getY(), gameViewport.getWidth(), gameViewport.getHeight())) {
            return;
        }
        fillCircle(x - gameViewport.getX(), y - gameViewport.getY(), radius);
    }

    public void fillImage(Image image, double x, double y, double width, double height) {
        double scale = Viewport.getScale();
        gc.drawImage(image, ((x - Viewport.getX()) * scale + Viewport.getXOffset()), ((y - Viewport.getY()) * scale + Viewport.getYOffset()), (width * scale), (height * scale));
    }

    public void fillImageGame(Image image, double x, double y, double width, double height) {
        if (!CollisionUtil.RectRectCollision(gameViewport.getX(), gameViewport.getY(), gameViewport.getWidth(), gameViewport.getHeight(), x, y, width, height)) {
            return;
        }
        fillImage(image, x - gameViewport.getX(), y - gameViewport.getY(), width, height);
    }

    public void fillImageInterpolate(Image image, double xCurrent, double yCurrent, double xLast, double yLast, double width, double height) {
        double x = NumUtil.interpolate(xCurrent, xLast, factor);
        double y = NumUtil.interpolate(yCurrent, yLast, factor);
        double scale = Viewport.getScale();
        fillImage(image, x, y, width, height);
    }

    public void fillImageInterpolateGame(Image image, double xCurrent, double yCurrent, double xLast, double yLast, double width, double height) {
        double x = NumUtil.interpolate(xCurrent, xLast, factor);
        double y = NumUtil.interpolate(yCurrent, yLast, factor);
        if (!CollisionUtil.RectRectCollision(gameViewport.getX(), gameViewport.getY(), gameViewport.getWidth(), gameViewport.getHeight(), x, y, width, height)) {
            return;
        }
        fillImage(image, x - gameViewport.getX(), y - gameViewport.getY(), width, height);
    }

    public void drawString(double x, double y, String string, double size, Fonts font, StringAlignment alignment) {
        double scale = Viewport.getScale();
        gc.setFont(font.getFont(size * scale));
        gc.setTextAlign(alignment.getTextAlignment());
        gc.setTextBaseline(alignment.getVPos());
        gc.fillText(string, ((x - Viewport.getX()) * scale + Viewport.getXOffset()), ((y - Viewport.getY()) * scale + Viewport.getYOffset()));
    }

    public void drawStringGame(double x, double y, String string, double size, Fonts font, StringAlignment alignment) {
        double scale = Viewport.getScale();
        Text text = new Text(string);
        text.setFont(font.getFont(size * scale));
        gc.setFont(font.getFont(size * scale));
        gc.setTextAlign(alignment.getTextAlignment());
        gc.setTextBaseline(alignment.getVPos());
        if (!CollisionUtil.RectRectCollision(gameViewport.getX(), gameViewport.getY(), gameViewport.getWidth(), gameViewport.getHeight(), x - (text.getBoundsInLocal().getWidth() / 2f), y, text.getBoundsInLocal().getWidth(), text.getBoundsInLocal().getHeight())) {
            return;
        }
        drawString(x - gameViewport.getX(), y - gameViewport.getY(), string, size, font, alignment);
    }

    public void drawLine(double x1, double y1, double x2, double y2) {
        if (gameViewport != null) {
            if (!CollisionUtil.RectLineCollision(x1, y1, x2, y2, gameViewport.getX(), gameViewport.getY(), gameViewport.getWidth(), gameViewport.getHeight())) {
                return;
            }
        }
        double scale = Viewport.getScale();
        gc.strokeLine(x1 * scale + Viewport.getXOffset(), y1 * scale + Viewport.getYOffset(), x2 * scale + Viewport.getXOffset(), y2 * scale + Viewport.getYOffset());
    }

    public void drawModelGame(Models modelKey, double x, double y, double z, double direction) {
        if (!CollisionUtil.RectRectCollision(gameViewport.getX(), gameViewport.getY(), gameViewport.getWidth(), gameViewport.getHeight(), x, y, modelKey.getWidth(), modelKey.getWidth())) {
            return;
        }
//        System.out.println(StrictMath.floorMod(StrictMath.round(direction), 360));
        double scale = Viewport.getScale();
        WritableImage model = modelMap.get(modelKey)[((int) (direction * 0.044)) & 15];
        double imageSize = model.getWidth();//ModelLoaderUtil.getImageSize();
        double halfImageSize = model.getWidth() / 2;//ModelLoaderUtil.getHalfImageSize();
        double modelWidth = modelKey.getWidth();
        gc.drawImage(model, ((x - halfImageSize - Viewport.getX() + modelWidth) * scale + Viewport.getXOffset()), ((y - halfImageSize - Viewport.getY()) * scale + Viewport.getYOffset()), imageSize * scale, imageSize * scale);
    }

    public void drawModelInterpolateGame(Models modelKey, double xCurrent, double yCurrent, double zCurrent, double xLast, double yLast, double zLast, double directionCurrent, double directionLast) {
        drawModelGame(modelKey, NumUtil.interpolate(xCurrent, xLast, factor), NumUtil.interpolate(yCurrent, yLast, factor), NumUtil.interpolate(zCurrent, zLast, factor), NumUtil.interpolate(directionCurrent, directionLast, factor));
    }


    public void clearCanvas() {
        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
    }

    public void fillBackground() {
        double scale = Viewport.getScale();
        setColor(0, 0, 0);
        gc.fillRect(0, 0, StrictMath.ceil(1920 * scale + (Viewport.getXOffset() * 2)), StrictMath.ceil(1080 * scale + (Viewport.getYOffset() * 2)));
        setColor(50, 50, 50);
        fillRect(0, 0, 1920, 1080);
    }

    public void fillOffsetEdge() {
        double scale = Viewport.getScale();
        setColor(0, 0, 0);
        gc.fillRect(0, 0, 1920 * scale + (Viewport.getXOffset() * 2), Viewport.getYOffset());
        gc.fillRect(0, 0, Viewport.getXOffset(), 1080 * scale + Viewport.getYOffset() * 2);
        gc.fillRect(1920 * scale + Viewport.getXOffset(), 0, Viewport.getXOffset(), 1080 * scale + (Viewport.getYOffset() * 2));
        gc.fillRect(0, 1080 * scale + Viewport.getYOffset(), 1920 * scale + (Viewport.getXOffset() * 2), Viewport.getYOffset() * 2);
    }

}
