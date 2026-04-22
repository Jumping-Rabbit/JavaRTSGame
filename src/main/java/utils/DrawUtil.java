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
import tools.jackson.databind.ObjectMapper;

import java.util.EnumMap;

import static utils.NumUtil.LTD;

public class DrawUtil {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static EnumMap<Models, WritableImage[]> modelMap;
    static GameViewport gameViewport;
    private static double factor = 0;
    private static GraphicsContext gc;
    private static int color;

    public static void init(LoadingScreen loadingScreen) {
        modelMap = ModelLoaderUtil.calculateModelImages(loadingScreen);
    }

    public static synchronized double getFactor() {
        return factor;
    }

    public static synchronized void setFactor(double factor) {
        DrawUtil.factor = StrictMath.clamp(factor, 0, 1);
    }

    public static void setGameViewport(GameViewport gameViewport) {
        DrawUtil.gameViewport = gameViewport;
    }

    public static GraphicsContext getGC() {
        return gc;
    }

    public static void setGC(GraphicsContext gc) {
        DrawUtil.gc = gc;
    }

    public static void startRotation(double x1, double y1, double x2, double y2, double xOffset, double yOffset, double direction1, double direction2) {
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

    private static void rotate(double x, double y, double rotation) {
        gc.save();
        gc.translate(x, y);
        gc.rotate(rotation);
        gc.translate(-x, -y);
    }

    public static void resetRotation() {
        gc.restore();
    }


    public static void setColor(int hex) {

        if (color == hex){
            return;
        }
        Color newColor = Colors.fromHex(hex);
        color = hex;
        gc.setFill(newColor);
        gc.setStroke(newColor);
    }

    /**sets the thickness when calling stroke*/
    public static void setThickness(double thickness) {
        gc.setLineWidth(thickness * Viewport.getScale());
    }


    /** projects the x coordinate to screen position*/
    private static double projectX(double x){
        return (x - Viewport.getX()) * Viewport.getScale() + Viewport.getXOffset();
    }
    /** projects the y coordinate to screen position*/
    private static double projectY(double y){
        return (y - Viewport.getY()) * Viewport.getScale() + Viewport.getYOffset();
    }

    /**draws a filled rectangle, scaled inputs*/
    public static void fillRectScaled(long xScaled, long yScaled, long widthScaled, long heightScaled, int color){
        double scale = Viewport.getScale();
        setColor(color);
        gc.fillRect(projectX(LTD(xScaled)), projectY(LTD(yScaled)), LTD(widthScaled)*scale, LTD(heightScaled)*scale);
    }
    /**draws a rectangle outline, scaled inputs*/
    public static void strokeRectScaled(long xScaled, long yScaled, long widthScaled, long heightScaled, int color, double strokeWidth){
        double scale = Viewport.getScale();
        setColor(color);
        setThickness(strokeWidth);
        gc.strokeRect(projectX(LTD(xScaled)), projectY(LTD(yScaled)), LTD(widthScaled)*scale, LTD(heightScaled)*scale);
    }
    /**draws a filled rounded rectangle, scaled inputs*/
    public static void fillRoundedRectScaled(long xScaled, long yScaled, long widthScaled, long heightScaled, double arc, int color){
        double scale = Viewport.getScale();
        setColor(color);
        gc.fillRoundRect(projectX(LTD(xScaled)), projectY(LTD(yScaled)), LTD(widthScaled)*scale, LTD(heightScaled)*scale, arc*scale, arc*scale);
    }
    /**draws a rounded rectangle outline, scaled inputs*/
    public static void strokeRoundedRectScaled(long xScaled, long yScaled, long widthScaled, long heightScaled, double arc, int color, double strokeWidth){
        double scale = Viewport.getScale();
        setColor(color);
        setThickness(strokeWidth);
        gc.fillRoundRect(projectX(LTD(xScaled)), projectY(LTD(yScaled)), LTD(widthScaled)*scale, LTD(heightScaled)*scale, arc*scale, arc*scale);
    }
    /**draws a filled circle, scaled inputs*/
    public static void fillCircleScaled(long xScaled, long yScaled, long radiusScaled, int color){
        double scale = Viewport.getScale();
        setColor(color);
        gc.fillOval(projectX(LTD(xScaled)), projectY(LTD(yScaled)), LTD(radiusScaled)*2*scale, LTD(radiusScaled)*2*scale);
    }
    /**draws a circle outline, scaled inputs*/
    public static void strokeCircleScaled(long xScaled, long yScaled, long radiusScaled, int color, double strokeWidth){
        double scale = Viewport.getScale();
        setColor(color);
        setThickness(strokeWidth);
        gc.strokeOval(projectX(LTD(xScaled)), projectY(LTD(yScaled)), LTD(radiusScaled)*2*scale, LTD(radiusScaled)*2*scale);
    }
    /**draws a filled oval, scaled inputs*/
    public static void fillOvalScaled(long xScaled, long yScaled, long widthScaled, long heightScaled, int color){
        double scale = Viewport.getScale();
        setColor(color);
        gc.fillOval(projectX(LTD(xScaled)), projectY(LTD(yScaled)), LTD(widthScaled)*scale, LTD(heightScaled)*scale);
    }
    /**draws a oval outline, scaled inputs*/
    public static void strokeOvalScaled(long xScaled, long yScaled, long widthScaled, long heightScaled, int color, double strokeWidth){
        double scale = Viewport.getScale();
        setColor(color);
        setThickness(strokeWidth);
        gc.strokeOval(projectX(LTD(xScaled)), projectY(LTD(yScaled)), LTD(widthScaled)*scale, LTD(heightScaled)*scale);
    }
    /**draws a filled line, scaled inputs*/
    public static void fillLineScaled(long startXScaled, long startYScaled, long endXScaled, long endYScaled, int color){
        double scale = Viewport.getScale();
        setColor(color);
        gc.strokeLine(projectX(LTD(startXScaled)), projectY(LTD(startYScaled)), projectX(LTD(endXScaled)), projectY(LTD(endYScaled)));
    }
    /**draws a filled text, scaled inputs*/
    public static void fillTextScaled(String text, long xScaled, long yScaled, Fonts font, double size, StringAlignment alignment){
        double scale = Viewport.getScale();
        gc.setFont(font.getFont(size * scale));
        gc.setTextAlign(alignment.getTextAlignment());
        gc.setTextBaseline(alignment.getVPos());
        gc.fillText(text, projectX(LTD(xScaled)), projectY(LTD(yScaled)));
    }
    /**draws a image, scaled inputs*/
    public static void fillImageScaled(Image image, long xScaled, long yScaled, long widthScaled, long heightScaled){
        double scale = Viewport.getScale();
        gc.drawImage(image, projectX(LTD(xScaled)), projectY(LTD(yScaled)), (LTD(widthScaled) * scale), (LTD(heightScaled) * scale));
    }
    /**draws a model, scaled inputs*/
    public static void fillModelScaled(Models modelKey, long xScaled, long yScaled, long zScaled, long directionScaled){
        double scale = Viewport.getScale();
        WritableImage model = modelMap.get(modelKey)[(int) (StrictMath.floorMod((int) (LTD(directionScaled)+11.25), 360)/22.5)];
        double imageSize = model.getWidth();
        double halfImageSize = model.getWidth() / 2;
        double modelWidth = modelKey.getWidth();
        gc.drawImage(model, projectX(LTD(xScaled) - halfImageSize + modelWidth), projectY(LTD(yScaled) - halfImageSize), imageSize * scale, imageSize * scale);
    }


    /**draws a filled rectangle, scaled inputs*/
    public static void fillRect(double x, double y, double width, double height, int color){
        double scale = Viewport.getScale();
        setColor(color);
        gc.fillRect(projectX(x), projectY(y), width*scale, height*scale);
    }
    /**draws a filled rectangle, scaled inputs*/
    public static void fillRect(Rectangle2D rect, int color){
        double scale = Viewport.getScale();
        setColor(color);
        gc.fillRect(projectX(rect.getMinX()), projectY(rect.getMinY()), rect.getWidth()*scale, rect.getHeight()*scale);
    }
    /**draws a rectangle outline, scaled inputs*/
    public static void strokeRect(double x, double y, double width, double height, int color, double strokeWidth){
        double scale = Viewport.getScale();
        setColor(color);
        setThickness(strokeWidth);
        gc.strokeRect(projectX(x), projectY(y), width*scale, height*scale);
    }
    /**draws a rectangle outline, scaled inputs*/
    public static void strokeRect(Rectangle2D rect, int color, double strokeWidth){
        double scale = Viewport.getScale();
        setColor(color);
        setThickness(strokeWidth);
        gc.strokeRect(projectX(rect.getMinX()), projectY(rect.getMinY()), rect.getWidth()*scale, rect.getHeight()*scale);
    }
    /**draws a filled rounded rectangle, scaled inputs*/
    public static void fillRoundedRect(double x, double y, double width, double height, double arc, int color){
        double scale = Viewport.getScale();
        setColor(color);
        gc.fillRoundRect(projectX(x), projectY(y), width*scale, height*scale, arc*scale, arc*scale);
    }

    /**draws a rounded rectangle outline, scaled inputs*/
    public static void strokeRoundedRect(double x, double y, double width, double height, double arc, int color, double strokeWidth){
        double scale = Viewport.getScale();
        setColor(color);
        setThickness(strokeWidth);
        gc.strokeRoundRect(projectX(x), projectY(y), width*scale, height*scale, arc*scale, arc*scale);
    }

    /**draws a filled circle, scaled inputs*/
    public static void fillCircle(double x, double y, double radius, int color){
        double scale = Viewport.getScale();
        setColor(color);
        gc.fillOval(projectX(x), projectY(y), radius*2*scale, radius*2*scale);
    }

    /**draws a circle outline, scaled inputs*/
    public static void strokeCircle(double x, double y, double radius, int color, double strokeWidth){
        double scale = Viewport.getScale();
        setColor(color);
        setThickness(strokeWidth);
        gc.strokeOval(projectX(x), projectY(y), radius*2*scale, radius*2*scale);
    }

    /**draws a filled oval, scaled inputs*/
    public static void fillOval(double x, double y, double width, double height, int color){
        double scale = Viewport.getScale();
        setColor(color);
        gc.fillOval(projectX(x), projectY(y), width*scale, height*scale);
    }

    /**draws a oval outline, scaled inputs*/
    public static void strokeOval(double x, double y, double width, double height, int color, double strokeWidth){
        double scale = Viewport.getScale();
        setColor(color);
        setThickness(strokeWidth);
        gc.strokeOval(projectX(x), projectY(y), width*scale, height*scale);
    }

    /**draws a filled line, scaled inputs*/
    public static void fillLine(double startX, double startY, double endX, double endY, int color){
        double scale = Viewport.getScale();
        setColor(color);
        gc.strokeLine(projectX(startX), projectY(startY), projectX(endX), projectY(endY));
    }

    /**draws a filled text*/
    public static void fillText(String text, double x, double y, Fonts font, double size, StringAlignment alignment, int color){
        double scale = Viewport.getScale();
        setColor(color);
        gc.setFont(font.getFont(size * scale));
        gc.setTextAlign(alignment.getTextAlignment());
        gc.setTextBaseline(alignment.getVPos());
        gc.fillText(text, projectX(x), projectY(y));
    }

    /**draws a image*/
    public static void fillImage(Image image, double x, double y, double width, double height){
        double scale = Viewport.getScale();
        gc.drawImage(image, projectX(x), projectY(y), (width * scale), (height * scale));
    }

    /**draws a model*/
    public static void fillModel(Models modelKey, double x, double y, double z, double direction){
        double scale = Viewport.getScale();
        WritableImage model = modelMap.get(modelKey)[(int) (StrictMath.floorMod((int) (direction+11.25), 360)/22.5)];
        double imageSize = model.getWidth();
        double halfImageSize = model.getWidth() / 2;
        double modelWidth = modelKey.getWidth();
        gc.drawImage(model, projectX(x - halfImageSize + modelWidth), projectY(y - halfImageSize), imageSize * scale, imageSize * scale);
    }


    /** shorthand for linear iterpolate, scaled inputs*/
    private static long lerp(long current, long last){
        return NumUtil.interpolate(current, last, factor);
    }
    /** shorthand for linear iterpolate*/
    private static double lerp(double current, double last){
        return NumUtil.interpolate(current, last, factor);
    }

    public static class Lerp{
        /**draws a filled rectangle, scaled inputs*/
        public static void fillRectScaled(long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long widthScaled, long heightScaled, int color){
            DrawUtil.fillRectScaled(lerp(xCurrentScaled, xLastScaled), lerp(yCurrentScaled, yLastScaled), widthScaled, heightScaled, color);
        }
        /**draws a rectangle outline, scaled inputs*/
        public static void strokeRectScaled(long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long widthScaled, long heightScaled, int color, double strokeWidth){
            DrawUtil.strokeRectScaled(lerp(xCurrentScaled, xLastScaled), lerp(yCurrentScaled, yLastScaled), widthScaled, heightScaled, color, strokeWidth);
        }
        /**draws a filled rounded rectangle, scaled inputs*/
        public static void fillRoundedRectScaled(long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long widthScaled, long heightScaled, double arc, int color){
            DrawUtil.fillRoundedRectScaled(lerp(xCurrentScaled, xLastScaled), lerp(yCurrentScaled, yLastScaled), widthScaled, heightScaled, arc, color);
        }
        /**draws a rounded rectangle outline, scaled inputs*/
        public static void strokeRoundedRectScaled(long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long widthScaled, long heightScaled, double arc, int color, double strokeWidth){
            DrawUtil.strokeRoundedRectScaled(lerp(xCurrentScaled, xLastScaled), lerp(yCurrentScaled, yLastScaled), widthScaled, heightScaled, arc, color, strokeWidth);
        }
        /**draws a filled circle, scaled inputs*/
        public static void fillCircleScaled(long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long radiusScaled, int color){
            DrawUtil.fillCircleScaled(lerp(xCurrentScaled, xLastScaled), lerp(yCurrentScaled, yLastScaled), radiusScaled, color);
        }
        /**draws a circle outline, scaled inputs*/
        public static void strokeCircleScaled(long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long radiusScaled, int color, double strokeWidth){
            DrawUtil.strokeCircleScaled(lerp(xCurrentScaled, xLastScaled), lerp(yCurrentScaled, yLastScaled), radiusScaled, color, strokeWidth);
        }
        /**draws a filled oval, scaled inputs*/
        public static void fillOvalScaled(long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long widthScaled, long heightScaled, int color){
            DrawUtil.fillOvalScaled(lerp(xCurrentScaled, xLastScaled), lerp(yCurrentScaled, yLastScaled), widthScaled, heightScaled, color);
        }
        /**draws a oval outline, scaled inputs*/
        public static void strokeOvalScaled(long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long widthScaled, long heightScaled, int color, double strokeWidth){
            DrawUtil.strokeOvalScaled(lerp(xCurrentScaled, xLastScaled), lerp(yCurrentScaled, yLastScaled), widthScaled, heightScaled, color, strokeWidth);
        }
        /**draws a filled line, scaled inputs*/
        public static void fillLineScaled(long startXCurrentScaled, long startXLastScaled, long startYCurrentScaled, long startYLastScaled, long endXCurrentScaled, long endXLastScaled, long endYCurrentScaled, long endYLastScaled, int color){
            DrawUtil.fillLineScaled(lerp(startXCurrentScaled, startXLastScaled), lerp(startYCurrentScaled, startYLastScaled), lerp(endXCurrentScaled, endXLastScaled), lerp(endYCurrentScaled, endYLastScaled), color);
        }
        /**draws a filled text, scaled inputs*/
        public static void fillTextScaled(String text, long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, Fonts font, double size, StringAlignment alignment){
            DrawUtil.fillTextScaled(text, lerp(xCurrentScaled, xLastScaled), lerp(yCurrentScaled, yLastScaled), font, size, alignment);
        }
        /**draws a image, scaled inputs*/
        public static void fillImageScaled(Image image, long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long width, long height){
            DrawUtil.fillImageScaled(image, lerp(xCurrentScaled, xLastScaled), lerp(yCurrentScaled, yLastScaled), width, height);
        }
        /**draws a model, scaled inputs*/
        public static void fillModelScaled(Models modelKey, long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long zCurrentScaled, long zLastScaled, long directionCurrentScaled, long directionLastScaled){
            DrawUtil.fillModelScaled(modelKey, lerp(xCurrentScaled, xLastScaled), lerp(yCurrentScaled, yLastScaled), lerp(zCurrentScaled, zLastScaled), lerp(directionCurrentScaled, directionLastScaled));
        }


        /**draws a filled rectangle*/
        public static void fillRect(double xCurrent, double xLast, double yCurrent, double yLast, double width, double height, int color){
            DrawUtil.fillRect(lerp(xCurrent, xLast), lerp(yCurrent, yLast), width, height, color);
        }
        /**draws a rectangle outline*/
        public static void strokeRect(double xCurrent, double xLast, double yCurrent, double yLast, double width, double height, int color, double strokeWidth){
            DrawUtil.strokeRect(lerp(xCurrent, xLast), lerp(yCurrent, yLast), width, height, color, strokeWidth);
        }
        /**draws a filled rounded rectangle*/
        public static void fillRoundedRect(double xCurrent, double xLast, double yCurrent, double yLast, double width, double height, double arc, int color){
            DrawUtil.fillRoundedRect(lerp(xCurrent, xLast), lerp(yCurrent, yLast), width, height, arc, color);
        }
        /**draws a rounded rectangle outline*/
        public static void strokeRoundedRect(double xCurrent, double xLast, double yCurrent, double yLast, double width, double height, double arc, int color, double strokeWidth){
            DrawUtil.strokeRoundedRect(lerp(xCurrent, xLast), lerp(yCurrent, yLast), width, height, arc, color, strokeWidth);
        }
        /**draws a filled circle*/
        public static void fillCircle(double xCurrent, double xLast, double yCurrent, double yLast, double radius, int color){
            DrawUtil.fillCircle(lerp(xCurrent, xLast), lerp(yCurrent, yLast), radius, color);
        }
        /**draws a circle outline*/
        public static void strokeCircle(double xCurrent, double xLast, double yCurrent, double yLast, double radius, int color, double strokeWidth){
            DrawUtil.strokeCircle(lerp(xCurrent, xLast), lerp(yCurrent, yLast), radius, color, strokeWidth);
        }
        /**draws a filled oval*/
        public static void fillOval(double xCurrent, double xLast, double yCurrent, double yLast, double width, double height, int color){
            DrawUtil.fillOval(lerp(xCurrent, xLast), lerp(yCurrent, yLast), width, height, color);
        }
        /**draws a oval outline*/
        public static void strokeOval(double xCurrent, double xLast, double yCurrent, double yLast, double width, double height, int color, double strokeWidth){
            DrawUtil.strokeOval(lerp(xCurrent, xLast), lerp(yCurrent, yLast), width, height, color, strokeWidth);
        }
        /**draws a filled line*/
        public static void fillLine(double startXCurrent, double startXLast, double startYCurrent, double startYLast, double endXCurrent, double endXLast, double endYCurrent, double endYLast, int color){
            DrawUtil.fillLine(lerp(startXCurrent, startXLast), lerp(startYCurrent, startYLast), lerp(endXCurrent, endXLast), lerp(endYCurrent, endYLast), color);
        }
        /**draws a filled text*/
        public static void fillText(String text, double xCurrent, double xLast, double yCurrent, double yLast, Fonts font, double size, StringAlignment alignment, int color){
            DrawUtil.fillText(text, lerp(xCurrent, xLast), lerp(yCurrent, yLast), font, size, alignment, color);
        }
        /**draws a image*/
        public static void fillImage(Image image, double xCurrent, double xLast, double yCurrent, double yLast, double width, double height){
            DrawUtil.fillImage(image, lerp(xCurrent, xLast), lerp(yCurrent, yLast), width, height);
        }
        /**draws a model*/
        public static void fillModel(Models modelKey, double xCurrent, double xLast, double yCurrent, double yLast, double zCurrent, double zLast, double directionCurrent, double directionLast){
            DrawUtil.fillModel(modelKey, lerp(xCurrent, xLast), lerp(yCurrent, yLast), lerp(zCurrent, zLast), lerp(directionCurrent, directionLast));
        }
    }

    public static class Game{
        /** puts the offset from gameViewport*/
        private static long putXGO (long x){
            return x - gameViewport.getX();
        }
        /** puts the offset from gameViewport*/
        private static long putYGO (long y){
            return y - gameViewport.getY();
        }
        /** puts the offset from gameViewport*/
        private static double putXGO (double x){
            return x - gameViewport.getX();
        }
        /** puts the offset from gameViewport*/
        private static double putYGO (double y){
            return y - gameViewport.getY();
        }

        /**returns true if needs to be culled*/
        private static boolean cull(long x, long y, long width, long height){
//            return CollisionUtil.RectRectCollision(x, y, width, height, );//use scaled gameViewport coords, return true if needs to be culled
            return true;
        }

        /**returns true if needs to be culled*/
        private static boolean cull(double x, double y, double width, double height){
            return true;
        }

        /**draws a filled rectangle, scaled inputs*/
        public static void fillRectScaled(long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long widthScaled, long heightScaled, int color){
            DrawUtil.fillRectScaled(putXGO(lerp(xCurrentScaled, xLastScaled)), putYGO(lerp(yCurrentScaled, yLastScaled)), widthScaled, heightScaled, color);
        }
        /**draws a rectangle outline, scaled inputs*/
        public static void strokeRectScaled(long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long widthScaled, long heightScaled, int color, double strokeWidth){
            DrawUtil.strokeRectScaled(putXGO(lerp(xCurrentScaled, xLastScaled)), putYGO(lerp(yCurrentScaled, yLastScaled)), widthScaled, heightScaled, color, strokeWidth);
        }
        /**draws a filled rounded rectangle, scaled inputs*/
        public static void fillRoundedRectScaled(long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long widthScaled, long heightScaled, double arc, int color){
            DrawUtil.fillRoundedRectScaled(putXGO(lerp(xCurrentScaled, xLastScaled)), putYGO(lerp(yCurrentScaled, yLastScaled)), widthScaled, heightScaled, arc, color);
        }
        /**draws a rounded rectangle outline, scaled inputs*/
        public static void strokeRoundedRectScaled(long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long widthScaled, long heightScaled, double arc, int color, double strokeWidth){
            DrawUtil.strokeRoundedRectScaled(putXGO(lerp(xCurrentScaled, xLastScaled)), putYGO(lerp(yCurrentScaled, yLastScaled)), widthScaled, heightScaled, arc, color, strokeWidth);
        }
        /**draws a filled circle, scaled inputs*/
        public static void fillCircleScaled(long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long radiusScaled, int color){
            DrawUtil.fillCircleScaled(putXGO(lerp(xCurrentScaled, xLastScaled)), putYGO(lerp(yCurrentScaled, yLastScaled)), radiusScaled, color);
        }
        /**draws a circle outline, scaled inputs*/
        public static void strokeCircleScaled(long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long radiusScaled, int color, double strokeWidth){
            DrawUtil.strokeCircleScaled(putXGO(lerp(xCurrentScaled, xLastScaled)), putYGO(lerp(yCurrentScaled, yLastScaled)), radiusScaled, color, strokeWidth);
        }
        /**draws a filled oval, scaled inputs*/
        public static void fillOvalScaled(long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long widthScaled, long heightScaled, int color){
            DrawUtil.fillOvalScaled(putXGO(lerp(xCurrentScaled, xLastScaled)), putYGO(lerp(yCurrentScaled, yLastScaled)), widthScaled, heightScaled, color);
        }
        /**draws a oval outline, scaled inputs*/
        public static void strokeOvalScaled(long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long widthScaled, long heightScaled, int color, double strokeWidth){
            DrawUtil.strokeOvalScaled(putXGO(lerp(xCurrentScaled, xLastScaled)), putYGO(lerp(yCurrentScaled, yLastScaled)), widthScaled, heightScaled, color, strokeWidth);
        }
        /**draws a filled line, scaled inputs*/
        public static void fillLineScaled(long startXCurrentScaled, long startXLastScaled, long startYCurrentScaled, long startYLastScaled, long endXCurrentScaled, long endXLastScaled, long endYCurrentScaled, long endYLastScaled, int color){
            DrawUtil.fillLineScaled(putXGO(lerp(startXCurrentScaled, startXLastScaled)), putYGO(lerp(startYCurrentScaled, startYLastScaled)), putXGO(lerp(endXCurrentScaled, endXLastScaled)), putYGO(lerp(endYCurrentScaled, endYLastScaled)), color);
        }
        /**draws a filled text, scaled inputs*/
        public static void fillTextScaled(String text, long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, Fonts font, double size, StringAlignment alignment){
            DrawUtil.fillTextScaled(text, putXGO(lerp(xCurrentScaled, xLastScaled)), putYGO(lerp(yCurrentScaled, yLastScaled)), font, size, alignment);
        }
        /**draws a image, scaled inputs*/
        public static void fillImageScaled(Image image, long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long width, long height){
            DrawUtil.fillImageScaled(image, putXGO(lerp(xCurrentScaled, xLastScaled)), putYGO(lerp(yCurrentScaled, yLastScaled)), width, height);
        }
        /**draws a model, scaled inputs*/
        public static void fillModelScaled(Models modelKey, long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long zCurrentScaled, long zLastScaled, long directionCurrentScaled, long directionLastScaled){
            DrawUtil.fillModelScaled(modelKey, putXGO(lerp(xCurrentScaled, xLastScaled)), putYGO(lerp(yCurrentScaled, yLastScaled)), lerp(zCurrentScaled, zLastScaled), lerp(directionCurrentScaled, directionLastScaled));
        }


        /**draws a filled rectangle*/
        public static void fillRect(double xCurrent, double xLast, double yCurrent, double yLast, double width, double height, int color){
            DrawUtil.fillRect(putXGO(lerp(xCurrent, xLast)), putYGO(lerp(yCurrent, yLast)), width, height, color);
        }
        /**draws a rectangle outline*/
        public static void strokeRect(double xCurrent, double xLast, double yCurrent, double yLast, double width, double height, int color, double strokeWidth){
            DrawUtil.strokeRect(putXGO(lerp(xCurrent, xLast)), putYGO(lerp(yCurrent, yLast)), width, height, color, strokeWidth);
        }
        /**draws a filled rounded rectangle*/
        public static void fillRoundedRect(double xCurrent, double xLast, double yCurrent, double yLast, double width, double height, double arc, int color){
            DrawUtil.fillRoundedRect(putXGO(lerp(xCurrent, xLast)), putYGO(lerp(yCurrent, yLast)), width, height, arc, color);
        }
        /**draws a rounded rectangle outline*/
        public static void strokeRoundedRect(double xCurrent, double xLast, double yCurrent, double yLast, double width, double height, double arc, int color, double strokeWidth){
            DrawUtil.strokeRoundedRect(putXGO(lerp(xCurrent, xLast)), putYGO(lerp(yCurrent, yLast)), width, height, arc, color, strokeWidth);
        }
        /**draws a filled circle*/
        public static void fillCircle(double xCurrent, double xLast, double yCurrent, double yLast, double radius, int color){
            DrawUtil.fillCircle(putXGO(lerp(xCurrent, xLast)), putYGO(lerp(yCurrent, yLast)), radius, color);
        }
        /**draws a circle outline*/
        public static void strokeCircle(double xCurrent, double xLast, double yCurrent, double yLast, double radius, int color, double strokeWidth){
            DrawUtil.strokeCircle(putXGO(lerp(xCurrent, xLast)), putYGO(lerp(yCurrent, yLast)), radius, color, strokeWidth);
        }
        /**draws a filled oval*/
        public static void fillOval(double xCurrent, double xLast, double yCurrent, double yLast, double width, double height, int color){
            DrawUtil.fillOval(putXGO(lerp(xCurrent, xLast)), putYGO(lerp(yCurrent, yLast)), width, height, color);
        }
        /**draws a oval outline*/
        public static void strokeOval(double xCurrent, double xLast, double yCurrent, double yLast, double width, double height, int color, double strokeWidth){
            DrawUtil.strokeOval(putXGO(lerp(xCurrent, xLast)), putYGO(lerp(yCurrent, yLast)), width, height, color, strokeWidth);
        }
        /**draws a filled line*/
        public static void fillLine(double startXCurrent, double startXLast, double startYCurrent, double startYLast, double endXCurrent, double endXLast, double endYCurrent, double endYLast, int color){
            DrawUtil.fillLine(putXGO(lerp(startXCurrent, startXLast)), putYGO(lerp(startYCurrent, startYLast)), putXGO(lerp(endXCurrent, endXLast)), putYGO(lerp(endYCurrent, endYLast)), color);
        }
        /**draws a filled text*/
        public static void fillText(String text, double xCurrent, double xLast, double yCurrent, double yLast, Fonts font, double size, StringAlignment alignment, int color){
            DrawUtil.fillText(text, putXGO(lerp(xCurrent, xLast)), putYGO(lerp(yCurrent, yLast)), font, size, alignment, color);
        }
        /**draws a image*/
        public static void fillImage(Image image, double xCurrent, double xLast, double yCurrent, double yLast, double width, double height){
            DrawUtil.fillImage(image, putXGO(lerp(xCurrent, xLast)), putYGO(lerp(yCurrent, yLast)), width, height);
        }
        /**draws a model*/
        public static void fillModel(Models modelKey, double xCurrent, double xLast, double yCurrent, double yLast, double zCurrent, double zLast, double directionCurrent, double directionLast){
            DrawUtil.fillModel(modelKey, putXGO(lerp(xCurrent, xLast)), putYGO(lerp(yCurrent, yLast)), lerp(zCurrent, zLast), lerp(directionCurrent, directionLast));
        }


        /**draws a filled rectangle, scaled inputs, returns false if culled*/
        public static boolean fillRectScaledCull(long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long widthScaled, long heightScaled, int color){
            long x = lerp(xCurrentScaled, xLastScaled);
            long y = lerp(yCurrentScaled, yLastScaled);
            if (cull(x, y, widthScaled, heightScaled)) return false;
            DrawUtil.fillRectScaled(putXGO(x), putYGO(y), widthScaled, heightScaled, color);
            return true;
        }
        /**draws a rectangle outline, scaled inputs, returns false if culled*/
        public static boolean strokeRectScaledCull(long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long widthScaled, long heightScaled, int color, double strokeWidth){
            long x = lerp(xCurrentScaled, xLastScaled);
            long y = lerp(yCurrentScaled, yLastScaled);
            if (cull(x, y, widthScaled, heightScaled)) return false;
            DrawUtil.strokeRectScaled(putXGO(x), putYGO(y), widthScaled, heightScaled, color, strokeWidth);
            return true;
        }
        /**draws a filled rounded rectangle, scaled inputs, returns false if culled*/
        public static boolean fillRoundedRectScaledCull(long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long widthScaled, long heightScaled, double arc, int color){
            long x = lerp(xCurrentScaled, xLastScaled);
            long y = lerp(yCurrentScaled, yLastScaled);
            if (cull(x, y, widthScaled, heightScaled)) return false;
            DrawUtil.fillRoundedRectScaled(putXGO(x), putYGO(y), widthScaled, heightScaled, arc, color);
            return true;
        }
        /**draws a rounded rectangle outline, scaled inputs, returns false if culled*/
        public static boolean strokeRoundedRectScaledCull(long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long widthScaled, long heightScaled, double arc, int color, double strokeWidth){
            long x = lerp(xCurrentScaled, xLastScaled);
            long y = lerp(yCurrentScaled, yLastScaled);
            if (cull(x, y, widthScaled, heightScaled)) return false;
            DrawUtil.strokeRoundedRectScaled(putXGO(x), putYGO(y), widthScaled, heightScaled, arc, color, strokeWidth);
            return true;
        }
        /**draws a filled circle, scaled inputs, returns false if culled*/
        public static boolean fillCircleScaledCull(long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long radiusScaled, int color){
            long x = lerp(xCurrentScaled, xLastScaled);
            long y = lerp(yCurrentScaled, yLastScaled);
            if (cull(x, y, radiusScaled*2, radiusScaled*2)) return false;
            DrawUtil.fillCircleScaled(putXGO(x), putYGO(y), radiusScaled, color);
            return true;
        }
        /**draws a circle outline, scaled inputs, returns false if culled*/
        public static boolean strokeCircleScaledCull(long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long radiusScaled, int color, double strokeWidth){
            long x = lerp(xCurrentScaled, xLastScaled);
            long y = lerp(yCurrentScaled, yLastScaled);
            if (cull(x, y, radiusScaled*2, radiusScaled*2)) return false;
            DrawUtil.strokeCircleScaled(putXGO(x), putYGO(y), radiusScaled, color, strokeWidth);
            return true;
        }
        /**draws a filled oval, scaled inputs, returns false if culled*/
        public static boolean fillOvalScaledCull(long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long widthScaled, long heightScaled, int color){
            long x = lerp(xCurrentScaled, xLastScaled);
            long y = lerp(yCurrentScaled, yLastScaled);
            if (cull(x, y, widthScaled, heightScaled)) return false;
            DrawUtil.fillOvalScaled(putXGO(x), putYGO(y), widthScaled, heightScaled, color);
            return true;
        }
        /**draws a oval outline, scaled inputs, returns false if culled*/
        public static boolean strokeOvalScaledCull(long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long widthScaled, long heightScaled, int color, double strokeWidth){
            long x = lerp(xCurrentScaled, xLastScaled);
            long y = lerp(yCurrentScaled, yLastScaled);
            if (cull(x, y, widthScaled, heightScaled)) return false;
            DrawUtil.strokeOvalScaled(putXGO(x), putYGO(y), widthScaled, heightScaled, color, strokeWidth);
            return true;
        }
        /**draws a filled line, scaled inputs, returns false if culled*/
        public static boolean fillLineScaledCull(long startXCurrentScaled, long startXLastScaled, long startYCurrentScaled, long startYLastScaled, long endXCurrentScaled, long endXLastScaled, long endYCurrentScaled, long endYLastScaled, int color){
            long startX = lerp(startXCurrentScaled, startXLastScaled);
            long startY = lerp(startYCurrentScaled, startYLastScaled);
            long endX = lerp(endXCurrentScaled, endXLastScaled);
            long endY = lerp(endYCurrentScaled, endYLastScaled);
            if (cull(startX, startY, endX-startX, endY-startX)) return false;
            DrawUtil.fillLineScaled(putXGO(startX), putYGO(startY), putXGO(endX), putYGO(endY), color);
            return true;
        }
        /**draws a filled text, scaled inputs, returns false if culled*/
        public static boolean fillTextScaledCull(String text, long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, Fonts font, double size, StringAlignment alignment){
            long x = lerp(xCurrentScaled, xLastScaled);
            long y = lerp(yCurrentScaled, yLastScaled);
//            if (cull(x, y, widthScaled, heightScaled)) return;//TODO:fix this
            DrawUtil.fillTextScaled(text, putXGO(x), putYGO(y), font, size, alignment);
            return true;
        }
        /**draws a image, scaled inputs, returns false if culled*/
        public static boolean fillImageScaledCull(Image image, long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long widthScaled, long heightScaled){
            long x = lerp(xCurrentScaled, xLastScaled);
            long y = lerp(yCurrentScaled, yLastScaled);
            if (cull(x, y, widthScaled, heightScaled)) return false;
            DrawUtil.fillImageScaled(image, putXGO(x), putYGO(y), widthScaled, heightScaled);
            return true;
        }
        /**draws a model, scaled inputs, returns false if culled*/
        public static boolean fillModelScaledCull(Models modelKey, long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long zCurrentScaled, long zLastScaled, long directionCurrentScaled, long directionLastScaled){
            long x = lerp(xCurrentScaled, xLastScaled);
            long y = lerp(yCurrentScaled, yLastScaled);//do something with z here
//            if (cull(x, y, widthScaled, heightScaled)) return;//TODO:fix this
            DrawUtil.fillModelScaled(modelKey, putXGO(x), putYGO(y), lerp(zCurrentScaled, zLastScaled), lerp(directionCurrentScaled, directionLastScaled));
            return true;
        }


        /**draws a filled rectangle, returns false if culled*/
        public static boolean fillRectCull(double xCurrent, double xLast, double yCurrent, double yLast, double width, double height, int color){
            double x = lerp(xCurrent, xLast);
            double y = lerp(yCurrent, yLast);
            if (cull(x, y, width, height)) return false;
            DrawUtil.fillRect(putXGO(x), putYGO(y), width, height, color);
            return true;
        }
        /**draws a rectangle outline, returns false if culled*/
        public static boolean strokeRectCull(double xCurrent, double xLast, double yCurrent, double yLast, double width, double height, int color, double strokeWidth){
            double x = lerp(xCurrent, xLast);
            double y = lerp(yCurrent, yLast);
            if (cull(x, y, width, height)) return false;
            DrawUtil.strokeRect(putXGO(x), putYGO(y), width, height, color, strokeWidth);
            return true;
        }
        /**draws a filled rounded rectangle, returns false if culled*/
        public static boolean fillRoundedRectCull(double xCurrent, double xLast, double yCurrent, double yLast, double width, double height, double arc, int color){
            double x = lerp(xCurrent, xLast);
            double y = lerp(yCurrent, yLast);
            if (cull(x, y, width, height)) return false;
            DrawUtil.fillRoundedRect(putXGO(x), putYGO(y), width, height, arc, color);
            return true;
        }
        /**draws a rounded rectangle outline, returns false if culled*/
        public static boolean strokeRoundedRectCull(double xCurrent, double xLast, double yCurrent, double yLast, double width, double height, double arc, int color, double strokeWidth){
            double x = lerp(xCurrent, xLast);
            double y = lerp(yCurrent, yLast);
            if (cull(x, y, width, height)) return false;
            DrawUtil.strokeRoundedRect(putXGO(x), putYGO(y), width, height, arc, color, strokeWidth);
            return true;
        }
        /**draws a filled circle, returns false if culled*/
        public static boolean fillCircleCull(double xCurrent, double xLast, double yCurrent, double yLast, double radius, int color){
            double x = lerp(xCurrent, xLast);
            double y = lerp(yCurrent, yLast);
            if (cull(x, y, radius*2, radius*2)) return false;
            DrawUtil.fillCircle(putXGO(x), putYGO(y), radius, color);
            return true;
        }
        /**draws a circle outline, returns false if culled*/
        public static boolean strokeCircleCull(double xCurrent, double xLast, double yCurrent, double yLast, double radius, int color, double strokeWidth){
            double x = lerp(xCurrent, xLast);
            double y = lerp(yCurrent, yLast);
            if (cull(x, y, radius*2, radius*2)) return false;
            DrawUtil.strokeCircle(putXGO(x), putYGO(y), radius, color, strokeWidth);
            return true;
        }
        /**draws a filled oval, returns false if culled*/
        public static boolean fillOvalCull(double xCurrent, double xLast, double yCurrent, double yLast, double width, double height, int color){
            double x = lerp(xCurrent, xLast);
            double y = lerp(yCurrent, yLast);
            if (cull(x, y, width, height)) return false;
            DrawUtil.fillOval(putXGO(x), putYGO(y), width, height, color);
            return true;
        }
        /**draws a oval outline, returns false if culled*/
        public static boolean strokeOvalCull(double xCurrent, double xLast, double yCurrent, double yLast, double width, double height, int color, double strokeWidth){
            double x = lerp(xCurrent, xLast);
            double y = lerp(yCurrent, yLast);
            if (cull(x, y, width, height)) return false;
            DrawUtil.strokeOval(putXGO(x), putYGO(y), width, height, color, strokeWidth);
            return true;
        }
        /**draws a filled line, returns false if culled*/
        public static boolean fillLineCull(double startXCurrent, double startXLast, double startYCurrent, double startYLast, double endXCurrent, double endXLast, double endYCurrent, double endYLast, int color){
            double startX = lerp(startXCurrent, startXLast);
            double startY = lerp(startYCurrent, startYLast);
            double endX = lerp(endXCurrent, endXLast);
            double endY = lerp(endYCurrent, endYLast);
            if (cull(startX, startY, endX-startX, endY-startX)) return false;
            DrawUtil.fillLine(putXGO(startX), putYGO(startY), putXGO(endX), putYGO(endY), color);
            return true;
        }
        /**draws a filled text, returns false if culled*/
        public static boolean fillTextCull(String text, double xCurrent, double xLast, double yCurrent, double yLast, Fonts font, double size, StringAlignment alignment, int color){
            double x = lerp(xCurrent, xLast);
            double y = lerp(yCurrent, yLast);
//            if (cull(x, y, width, height)) return;//TODO: fix
            DrawUtil.fillText(text, putXGO(x), putYGO(y), font, size, alignment, color);
            return true;
        }
        /**draws a image, returns false if culled*/
        public static boolean fillImageCull(Image image, double xCurrent, double xLast, double yCurrent, double yLast, double width, double height){
            double x = lerp(xCurrent, xLast);
            double y = lerp(yCurrent, yLast);
            if (cull(x, y, width, height)) return false;
            DrawUtil.fillImage(image, putXGO(x), putYGO(y), width, height);
            return true;
        }
        /**draws a model, returns false if culled*/
        public static boolean fillModelCull(Models modelKey, double xCurrent, double xLast, double yCurrent, double yLast, double zCurrent, double zLast, double directionCurrent, double directionLast){
            double x = lerp(xCurrent, xLast);
            double y = lerp(yCurrent, yLast);
//            if (cull(x, y, width, height)) return;//TODO: fix
            DrawUtil.fillModel(modelKey, putXGO(x), putYGO(y), lerp(zCurrent, zLast), lerp(directionCurrent, directionLast));
            return true;
        }

    }

    public static void clearCanvas() {
        gc.clearRect(0, 0, gc.getCanvas().getWidth()+1, gc.getCanvas().getHeight()+1);
    }

    public static void fillBackground() {
        double scale = Viewport.getScale();
        setColor(0x000000FF);
        gc.fillRect(0, 0, 1920 * scale + (Viewport.getXOffset() * 2)+1, 1080 * scale + (Viewport.getYOffset() * 2)+1);
        fillRect(0, 0, 1920, 1080, 0x323232FF);
    }

    public static void fillOffsetEdge() {
        double scale = Viewport.getScale();
        setColor(0x000000FF);
        gc.fillRect(0, 0, 1920 * scale + (Viewport.getXOffset() * 2), Viewport.getYOffset());
        gc.fillRect(0, 0, Viewport.getXOffset(), 1080 * scale + Viewport.getYOffset() * 2);
        gc.fillRect(1920 * scale + Viewport.getXOffset(), 0, Viewport.getXOffset(), 1080 * scale + (Viewport.getYOffset() * 2));
        gc.fillRect(0, 1080 * scale + Viewport.getYOffset(), 1920 * scale + (Viewport.getXOffset() * 2), Viewport.getYOffset() * 2);
    }

}
