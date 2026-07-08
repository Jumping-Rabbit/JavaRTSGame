
package utils;

import game.Fonts;
import game.GameViewport;
import game.Viewport;
import game.entity.PlayerColor;
import game.screen.LoadingScreen;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.*;
import javafx.scene.paint.Color;
import tools.jackson.databind.ObjectMapper;

import java.util.EnumMap;

import static utils.NumUtil.LTD;

public class DrawUtil {
    private static EnumMap<Models, WritableImage[]> modelMap;
    private static EnumMap<Models, WritableImage> modelImageMap;
    private static Object2ObjectOpenHashMap<PlayerColor, EnumMap<Models, WritableImage[]>> playersModelImageMap;
    private static Object2ObjectOpenHashMap<PlayerColor, EnumMap<Models, WritableImage[]>> playersColoredModelImageMap;
    private static Object2ObjectOpenHashMap<PlayerColor, EnumMap<Models, WritableImage[]>> playersModelMap;

    static GameViewport gameViewport;
    private static volatile double factor = 0;
    private static GraphicsContext gc;
    private static int color;
    private static int width;
    private static int height;

    public static void setImageResolution(int imageResolution) {
        DrawUtil.imageResolution = imageResolution;
    }

    public static void setHeight(int height) {
        DrawUtil.height = height;
    }

    public static void setWidth(int width) {
        DrawUtil.width = width;
    }

    private static int imageResolution = 64;
    

    public static void startDraw(){
        gameViewport.freeze();
    }

    public static void init(LoadingScreen loadingScreen) {
        loadBaseImages(loadingScreen);
    }

    public static void loadBaseImages(LoadingScreen loadingScreen){
        modelMap = ModelLoaderUtil.calculateModelImages(loadingScreen, imageResolution);
        modelImageMap = ModelLoaderUtil.calculateModelImage(loadingScreen, imageResolution);
    }

    public static void loadColoredImages(LoadingScreen loadingScreen, PlayerColor... players){
        makeHealthModelImages(loadingScreen, players);
        makeColoredModelImages(loadingScreen, players);
        makeScaledColoredModelImages(loadingScreen, players);
    }



    public static void makeHealthModelImages(LoadingScreen loadingScreen, PlayerColor... players){
        playersColoredModelImageMap = new Object2ObjectOpenHashMap<>();
        for (PlayerColor player : players) {
            EnumMap<Models, WritableImage[]> playerColoredModelImageMap = new EnumMap<>(Models.class);
            for (Models model : Models.values()) {
                WritableImage[] coloredImage = new WritableImage[5];
                WritableImage source = modelImageMap.get(model);
                PixelReader reader = source.getPixelReader();
                for (int i = 0; i < 5; i++) {
                    coloredImage[i] = new WritableImage(reader, (int) source.getWidth(), (int) source.getHeight());
                    PixelWriter imageWriter = coloredImage[i].getPixelWriter();
                    for (int x = 0; x < (int) source.getWidth(); x++) {
                        for (int y = 0; y < (int) source.getHeight(); y++) {
                            Color color = reader.getColor(x, y);
                            double newHue = switch (i) {
                                case 0 -> 120;
                                case 1 -> 89.88;
                                case 2 -> 60;
                                case 3 -> 30.12;
                                default -> 0;
                            };

                            Color newColor = Color.hsb(newHue, color.getSaturation(), color.getBrightness(), color.getOpacity());
                            imageWriter.setColor(x, y, newColor);
                        }
                    }
                    loadingScreen.increment();
                }
                playerColoredModelImageMap.put(model, coloredImage);
            }
            playersColoredModelImageMap.put(player, playerColoredModelImageMap);
        }
    }
    public static void makeScaledColoredModelImages(LoadingScreen loadingScreen, PlayerColor... players){
        playersModelImageMap = new Object2ObjectOpenHashMap<>();
        for (PlayerColor player : players) {
            EnumMap<Models, WritableImage[]> playerModelImageMap = new EnumMap<>(Models.class);
            for (Models model : Models.values()) {
                WritableImage[] image = new WritableImage[5];
                WritableImage source = modelImageMap.get(model);
                PixelReader reader = source.getPixelReader();
                for (int i = 0; i < 5; i++) {
                    image[i] = new WritableImage(reader, (int) source.getWidth(), (int) source.getHeight());
                    PixelWriter imageWriter = image[i].getPixelWriter();
                    for (int x = 0; x < (int) source.getWidth(); x++) {
                        for (int y = 0; y < (int) source.getHeight(); y++) {
                            Color color = reader.getColor(x, y);
                            Color newColor = Color.hsb(player.getHue(), color.getSaturation(), color.getBrightness(), color.getOpacity());
                            imageWriter.setColor(x, y, newColor);
                        }
                    }
                    loadingScreen.increment();
                }
                playerModelImageMap.put(model, image);
            }
            playersModelImageMap.put(player, playerModelImageMap);
        }
    }

    public static void makeColoredModelImages(LoadingScreen loadingScreen, PlayerColor... players){
        playersModelMap = new Object2ObjectOpenHashMap<>();
        for (PlayerColor player : players){
            System.out.println(player.toString());
            EnumMap<Models, WritableImage[]> playerModelMap = new EnumMap<>(Models.class);
            for (Models sourcesKey : modelMap.keySet()){
                WritableImage[] newSources = new WritableImage[modelMap.get(sourcesKey).length];
                int count = 0;
                for (WritableImage source : modelMap.get(sourcesKey)){
                    PixelReader reader = source.getPixelReader();
                    WritableImage image = new WritableImage(reader, (int)source.getWidth(), (int)source.getHeight());
                    PixelWriter imageWriter = image.getPixelWriter();
                    for (int x = 0; x < (int)source.getWidth(); x++){
                        for (int y = 0; y < (int)source.getHeight(); y++){
                            Color oldColor = reader.getColor(x, y);
                            Color newColor = Color.hsb(player.getHue(), oldColor.getSaturation(), oldColor.getBrightness(), oldColor.getOpacity());
                            imageWriter.setColor(x, y, newColor);
                        }
                    }
                    newSources[count] = image;
                    count++;
                    loadingScreen.increment();
                }
                playerModelMap.put(sourcesKey, newSources);
            }
            playersModelMap.put(player, playerModelMap);
        }
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
    public static void fillLineScaled(long startXScaled, long startYScaled, long endXScaled, long endYScaled, int color, double thickness){
        double scale = Viewport.getScale();
        setColor(color);
        setThickness(thickness);
        gc.strokeLine(projectX(LTD(startXScaled)), projectY(LTD(startYScaled)), projectX(LTD(endXScaled)), projectY(LTD(endYScaled)));
    }
    /**draws a filled line, scaled inputs*/
    public static void fillLineDottedScaled(long startXScaled, long startYScaled, long endXScaled, long endYScaled, int color, double thickness, double dotLength, double spaceLength){
        double scale = Viewport.getScale();
        setColor(color);
        setThickness(thickness);
        gc.setLineDashes(dotLength, spaceLength);
        gc.strokeLine(projectX(LTD(startXScaled)), projectY(LTD(startYScaled)), projectX(LTD(endXScaled)), projectY(LTD(endYScaled)));
        gc.setLineDashes(null);
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
    /**draws a model image, scaled inputs*/
    public static void fillModelImageScaled(Models modelKey, PlayerColor color, long xScaled, long yScaled, long directionScaled){
        double scale = Viewport.getScale();
        WritableImage model = playersModelMap.get(color).get(modelKey)[(int) (StrictMath.floorMod((int) (LTD(directionScaled)+11.25), 360)/22.5)];
        gc.drawImage(model, projectX(LTD(xScaled) - 128 + modelKey.getHalfWidth()), projectY(LTD(yScaled) - 128 + modelKey.getHalfHeight()), 256 * scale, 256 * scale);
    }
    /**draws a model, scaled inputs*/
    public static void fillModelScaled(Models modelKey, PlayerColor color, long xScaled, long yScaled, long zScaled, long directionScaled){
        double scale = Viewport.getScale();
        WritableImage model = playersModelMap.get(color).get(modelKey)[(int) (StrictMath.floorMod((int) (LTD(directionScaled)+11.25), 360)/22.5)];
        gc.drawImage(model, projectX(LTD(xScaled) + modelKey.getHalfWidth())-model.getWidth()/2, projectY(LTD(yScaled) + modelKey.getHalfHeight())-model.getHeight()/2);
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
    public static void fillLine(double startX, double startY, double endX, double endY, int color, double strokeWidth){
        double scale = Viewport.getScale();
        setColor(color);
        setThickness(strokeWidth);
        gc.strokeLine(projectX(startX), projectY(startY), projectX(endX), projectY(endY));
    }

    /**draws a filled line, scaled inputs*/
    public static void fillLineDotted(double startX, double startY, double endX, double endY, int color, double strokeWidth, double dotLength, double spaceLength){
        double scale = Viewport.getScale();
        setColor(color);
        setThickness(strokeWidth);
        gc.setLineDashes(dotLength, spaceLength);
        gc.strokeLine(projectX(startX), projectY(startY), projectX(endX), projectY(endY));
        gc.setLineDashes(null);
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
    public static void fillModel(Models modelKey, PlayerColor color, double x, double y, double z, double direction){
        double scale = Viewport.getScale();
        WritableImage model = playersModelMap.get(color).get(modelKey)[(int) (StrictMath.floorMod((int) (direction+11.25), 360)/22.5)];
        gc.drawImage(model, projectX(x), projectY(y+ modelKey.getHalfHeight()));
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
        public static void fillLineScaled(long startXCurrentScaled, long startXLastScaled, long startYCurrentScaled, long startYLastScaled, long endXCurrentScaled, long endXLastScaled, long endYCurrentScaled, long endYLastScaled, int color, double thickness){
            DrawUtil.fillLineScaled(lerp(startXCurrentScaled, startXLastScaled), lerp(startYCurrentScaled, startYLastScaled), lerp(endXCurrentScaled, endXLastScaled), lerp(endYCurrentScaled, endYLastScaled), color, thickness);
        }
        /**draws a filled line, scaled inputs*/
        public static void fillLineDottedScaled(long startXCurrentScaled, long startXLastScaled, long startYCurrentScaled, long startYLastScaled, long endXCurrentScaled, long endXLastScaled, long endYCurrentScaled, long endYLastScaled, int color, double thickness , double dotLength, double spaceLength){
            DrawUtil.fillLineDottedScaled(lerp(startXCurrentScaled, startXLastScaled), lerp(startYCurrentScaled, startYLastScaled), lerp(endXCurrentScaled, endXLastScaled), lerp(endYCurrentScaled, endYLastScaled), color, thickness ,dotLength, spaceLength);
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
        public static void fillModelScaled(Models modelKey, PlayerColor color, long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long zCurrentScaled, long zLastScaled, long directionCurrentScaled, long directionLastScaled){
            DrawUtil.fillModelScaled(modelKey, color, lerp(xCurrentScaled, xLastScaled), lerp(yCurrentScaled, yLastScaled), lerp(zCurrentScaled, zLastScaled), lerp(directionCurrentScaled, directionLastScaled));
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
        public static void fillLine(double startXCurrent, double startXLast, double startYCurrent, double startYLast, double endXCurrent, double endXLast, double endYCurrent, double endYLast, int color, double thickness){
            DrawUtil.fillLine(lerp(startXCurrent, startXLast), lerp(startYCurrent, startYLast), lerp(endXCurrent, endXLast), lerp(endYCurrent, endYLast), color, thickness);
        }
        /**draws a filled line*/
        public static void fillLineDotted(double startXCurrent, double startXLast, double startYCurrent, double startYLast, double endXCurrent, double endXLast, double endYCurrent, double endYLast, int color, double thickness, double dotLength, double spaceLength){
            DrawUtil.fillLineDotted(lerp(startXCurrent, startXLast), lerp(startYCurrent, startYLast), lerp(endXCurrent, endXLast), lerp(endYCurrent, endYLast), color, thickness, dotLength, spaceLength);
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
        public static void fillModel(Models modelKey, PlayerColor color, double xCurrent, double xLast, double yCurrent, double yLast, double zCurrent, double zLast, double directionCurrent, double directionLast){
            DrawUtil.fillModel(modelKey, color, lerp(xCurrent, xLast), lerp(yCurrent, yLast), lerp(zCurrent, zLast), lerp(directionCurrent, directionLast));
        }
    }

    public static class Game{
        /** puts the offset from gameViewport*/
        private static long putXGO (long x){
            return x - NumUtil.DTL(lerp(gameViewport.getX(), gameViewport.getLastX()));
        }
        /** puts the offset from gameViewport*/
        private static long putYGO (long y){
            return y - NumUtil.DTL(lerp(gameViewport.getY(), gameViewport.getLastY()));
        }
        /** puts the offset from gameViewport*/
        private static double putXGO (double x){
            return x - lerp(gameViewport.getX(), gameViewport.getLastX());
        }
        /** puts the offset from gameViewport*/
        private static double putYGO (double y){
            return y - lerp(gameViewport.getY(), gameViewport.getLastY());
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
        public static void fillLineScaled(long startXCurrentScaled, long startXLastScaled, long startYCurrentScaled, long startYLastScaled, long endXCurrentScaled, long endXLastScaled, long endYCurrentScaled, long endYLastScaled, int color, double thickness){
            DrawUtil.fillLineScaled(putXGO(lerp(startXCurrentScaled, startXLastScaled)), putYGO(lerp(startYCurrentScaled, startYLastScaled)), putXGO(lerp(endXCurrentScaled, endXLastScaled)), putYGO(lerp(endYCurrentScaled, endYLastScaled)), color, thickness);
        }
        /**draws a filled line, scaled inputs*/
        public static void fillLineDottedScaled(long startXCurrentScaled, long startXLastScaled, long startYCurrentScaled, long startYLastScaled, long endXCurrentScaled, long endXLastScaled, long endYCurrentScaled, long endYLastScaled, int color, double thickness, double dotLength, double spaceLength){
            DrawUtil.fillLineDottedScaled(putXGO(lerp(startXCurrentScaled, startXLastScaled)), putYGO(lerp(startYCurrentScaled, startYLastScaled)), putXGO(lerp(endXCurrentScaled, endXLastScaled)), putYGO(lerp(endYCurrentScaled, endYLastScaled)), color, thickness, dotLength, spaceLength);
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
        public static void fillModelScaled(Models modelKey, PlayerColor color, long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long zCurrentScaled, long zLastScaled, long directionCurrentScaled, long directionLastScaled){
            DrawUtil.fillModelScaled(modelKey, color, putXGO(lerp(xCurrentScaled, xLastScaled)), putYGO(lerp(yCurrentScaled, yLastScaled)), lerp(zCurrentScaled, zLastScaled), lerp(directionCurrentScaled, directionLastScaled));
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
        public static void fillLine(double startXCurrent, double startXLast, double startYCurrent, double startYLast, double endXCurrent, double endXLast, double endYCurrent, double endYLast, int color, double thickness){
            DrawUtil.fillLine(putXGO(lerp(startXCurrent, startXLast)), putYGO(lerp(startYCurrent, startYLast)), putXGO(lerp(endXCurrent, endXLast)), putYGO(lerp(endYCurrent, endYLast)), color, thickness);
        }
        /**draws a filled line*/
        public static void fillLineDotted(double startXCurrent, double startXLast, double startYCurrent, double startYLast, double endXCurrent, double endXLast, double endYCurrent, double endYLast, int color, double thickness, double dotLength, double spaceLength){
            DrawUtil.fillLineDotted(putXGO(lerp(startXCurrent, startXLast)), putYGO(lerp(startYCurrent, startYLast)), putXGO(lerp(endXCurrent, endXLast)), putYGO(lerp(endYCurrent, endYLast)), color, thickness, dotLength, spaceLength);
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
        public static void fillModel(Models modelKey, PlayerColor color, double xCurrent, double xLast, double yCurrent, double yLast, double zCurrent, double zLast, double directionCurrent, double directionLast){
            DrawUtil.fillModel(modelKey, color, putXGO(lerp(xCurrent, xLast)), putYGO(lerp(yCurrent, yLast)), lerp(zCurrent, zLast), lerp(directionCurrent, directionLast));
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
        public static boolean fillLineScaledCull(long startXCurrentScaled, long startXLastScaled, long startYCurrentScaled, long startYLastScaled, long endXCurrentScaled, long endXLastScaled, long endYCurrentScaled, long endYLastScaled, int color, double thickness){
            long startX = lerp(startXCurrentScaled, startXLastScaled);
            long startY = lerp(startYCurrentScaled, startYLastScaled);
            long endX = lerp(endXCurrentScaled, endXLastScaled);
            long endY = lerp(endYCurrentScaled, endYLastScaled);
            if (cull(startX, startY, endX-startX, endY-startX)) return false;
            DrawUtil.fillLineScaled(putXGO(startX), putYGO(startY), putXGO(endX), putYGO(endY), color, thickness);
            return true;
        }
        /**draws a filled line, scaled inputs, returns false if culled*/
        public static boolean fillLineDottedScaledCull(long startXCurrentScaled, long startXLastScaled, long startYCurrentScaled, long startYLastScaled, long endXCurrentScaled, long endXLastScaled, long endYCurrentScaled, long endYLastScaled, int color, double thickness, double dotLength, double spaceLength){
            long startX = lerp(startXCurrentScaled, startXLastScaled);
            long startY = lerp(startYCurrentScaled, startYLastScaled);
            long endX = lerp(endXCurrentScaled, endXLastScaled);
            long endY = lerp(endYCurrentScaled, endYLastScaled);
            if (cull(startX, startY, endX-startX, endY-startX)) return false;
            DrawUtil.fillLineDottedScaled(putXGO(startX), putYGO(startY), putXGO(endX), putYGO(endY), color, thickness, dotLength, spaceLength);
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
        public static boolean fillModelScaledCull(Models modelKey, PlayerColor color, long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long zCurrentScaled, long zLastScaled, long directionCurrentScaled, long directionLastScaled){
            long x = lerp(xCurrentScaled, xLastScaled);
            long y = lerp(yCurrentScaled, yLastScaled);//do something with z here
//            if (cull(x, y, widthScaled, heightScaled)) return;//TODO:fix this
            DrawUtil.fillModelScaled(modelKey, color, putXGO(x), putYGO(y), lerp(zCurrentScaled, zLastScaled), lerp(directionCurrentScaled, directionLastScaled));
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
        public static boolean fillLineCull(double startXCurrent, double startXLast, double startYCurrent, double startYLast, double endXCurrent, double endXLast, double endYCurrent, double endYLast, int color, double thickness){
            double startX = lerp(startXCurrent, startXLast);
            double startY = lerp(startYCurrent, startYLast);
            double endX = lerp(endXCurrent, endXLast);
            double endY = lerp(endYCurrent, endYLast);
            if (cull(startX, startY, endX-startX, endY-startX)) return false;
            DrawUtil.fillLine(putXGO(startX), putYGO(startY), putXGO(endX), putYGO(endY), color, thickness);
            return true;
        }
        /**draws a filled line, returns false if culled*/
        public static boolean fillLineDottedCull(double startXCurrent, double startXLast, double startYCurrent, double startYLast, double endXCurrent, double endXLast, double endYCurrent, double endYLast, int color, double thickness, double dotLength, double spaceLength){
            double startX = lerp(startXCurrent, startXLast);
            double startY = lerp(startYCurrent, startYLast);
            double endX = lerp(endXCurrent, endXLast);
            double endY = lerp(endYCurrent, endYLast);
            if (cull(startX, startY, endX-startX, endY-startX)) return false;
            DrawUtil.fillLineDotted(putXGO(startX), putYGO(startY), putXGO(endX), putYGO(endY), color, thickness, dotLength, spaceLength);
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
        public static boolean fillModelCull(Models modelKey, PlayerColor color, double xCurrent, double xLast, double yCurrent, double yLast, double zCurrent, double zLast, double directionCurrent, double directionLast){
            double x = lerp(xCurrent, xLast);
            double y = lerp(yCurrent, yLast);
//            if (cull(x, y, width, height)) return;//TODO: fix
            DrawUtil.fillModel(modelKey, color, putXGO(x), putYGO(y), lerp(zCurrent, zLast), lerp(directionCurrent, directionLast));
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
//package utils;
//
//import game.Fonts;
//import game.GameViewport;
//import game.Viewport;
//import game.entity.PlayerColor;
//import game.screen.LoadingScreen;
//import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
//import javafx.geometry.Rectangle2D;
//import javafx.geometry.VPos;
//import javafx.scene.canvas.Canvas;
//import javafx.scene.canvas.GraphicsContext;
//import javafx.scene.image.*;
//        import javafx.scene.paint.Color;
//import javafx.scene.text.TextAlignment;
//import tools.jackson.databind.ObjectMapper;
//
//import java.nio.IntBuffer;
//import java.util.Arrays;
//import java.util.EnumMap;
//import java.util.HashMap;
//import java.util.HashSet;
//
//import static utils.NumUtil.LTD;
//
//public class DrawUtil {
//
//    private static final ObjectMapper mapper = new ObjectMapper();
//
//    // ── model maps ────────────────────────────────────────────────────────────
//    private static EnumMap<Models, WritableImage[]> modelMap;
//    private static EnumMap<Models, WritableImage>   modelImageMap;
//    private static Object2ObjectOpenHashMap<PlayerColor, EnumMap<Models, WritableImage[]>> playersModelImageMap;
//    private static Object2ObjectOpenHashMap<PlayerColor, EnumMap<Models, WritableImage[]>> playersColoredModelImageMap;
//    private static Object2ObjectOpenHashMap<PlayerColor, EnumMap<Models, WritableImage[]>> playersModelMap;
//
//    /** player → model → frame → ARGB-premultiplied pixels, extracted once after colorUnits() */
//    private static Object2ObjectOpenHashMap<PlayerColor, EnumMap<Models, int[][]>> playersModelPixelsMap;
//    private static EnumMap<Models, Integer> modelFrameWidthMap;
//    private static EnumMap<Models, Integer> modelFrameHeightMap;
//
//    static GameViewport gameViewport;
//    private static volatile double factor = 0;
//    /** Used only by endDraw() to push the back-image to screen. */
//    private static GraphicsContext gc;
//
//    // ── back-buffer ───────────────────────────────────────────────────────────
//    /** All drawing writes here. Format: ARGB premultiplied (IntArgbPre), row-major. */
//    private static int[]                  backPixels;
//    private static PixelBuffer<IntBuffer> backBuffer;
//    private static WritableImage          backImage;
//    private static int backWidth;
//    private static int backHeight;
//
//    // ── text cache ────────────────────────────────────────────────────────────
//    /**
//     * Pre-rendered character images.
//     * Key encodes (fontOrdinal | pixelSize << 8 | codePoint << 24).
//     * Entry layout: [imageWidth, imageHeight, baselineY, pixels...]
//     * Pixels are white on transparent so blitText() can tint to any color.
//     */
//    private static final HashMap<Long, int[]> textCache     = new HashMap<>();
//    /**
//     * Monospaced cell width per (fontOrdinal | pixelSize << 8).
//     * All characters at a given font+size share this advance width.
//     */
//    private static final HashMap<Long, Integer> textCellWidthMap = new HashMap<>();
//
//    private static final int TEXT_PAD       = 4;
//    private static final int TEXT_CHAR_START = 32;   // space
//    private static final int TEXT_CHAR_END   = 126;  // ~
//    private static final int TEXT_SIZE_MIN   = 6;    // smallest game-unit size to pre-render
//    private static final int TEXT_SIZE_MAX   = 96;   // largest game-unit size to pre-render
//
//    /** Offscreen canvas used only during initText() — nulled out afterwards. */
//    private static Canvas          textCanvas;
//    private static GraphicsContext textGC;
//
//    // ── color cache ───────────────────────────────────────────────────────────
//    /** Last 0xRRGGBBAA value passed in; cached to avoid recomputing toPre(). */
//    private static int lastRawColor = 0;
//    /** Premultiplied ARGB of lastRawColor. */
//    private static int lastPreColor = 0;
//
//    // =========================================================================
//    //  Initialisation
//    // =========================================================================
//
//    public static void init(LoadingScreen loadingScreen) {
//        modelMap        = ModelLoaderUtil.calculateModelImages(loadingScreen);
//        modelImageMap = ModelLoaderUtil.calculateModelImage(loadingScreen);
//    }
//
//    /**
//     * Sets up the pixel back-buffer and pre-renders the full text cache.
//     * Call on the JavaFX Application Thread after Viewport scale is configured.
//     *
//     * @param width  canvas pixel width
//     * @param height canvas pixel height
//     */
//    public static void initCanvas(int width, int height, LoadingScreen loadingScreen) {
//        backWidth  = width;
//        backHeight = height;
//        backPixels = new int[width * height];
//        backBuffer = new PixelBuffer<>(width, height, IntBuffer.wrap(backPixels),
//                PixelFormat.getIntArgbPreInstance());
//        backImage  = new WritableImage(backBuffer);
//
//        textCanvas = new Canvas(512, 512);
//        textGC     = textCanvas.getGraphicsContext2D();
//
//        initText(loadingScreen);
//
//        textCanvas = null;
//        textGC     = null;
//    }
//
//    /**
//     * Pre-renders every printable ASCII character for every font at every
//     * integer game-unit size in [TEXT_SIZE_MIN, TEXT_SIZE_MAX].
//     * Duplicate pixel-sizes (from rounding) are skipped.
//     * Called internally by initCanvas().
//     */
//    private static void initText(LoadingScreen loadingScreen) {
//        double scale = Viewport.getScale();
//
//        for (Fonts font : Fonts.values()) {
//            HashSet<Integer> renderedPixelSizes = new HashSet<>();
//
//            for (int size = TEXT_SIZE_MIN; size <= TEXT_SIZE_MAX; size++) {
//                int pixelSize = Math.max(1, (int) Math.round(size * scale));
//                if (!renderedPixelSizes.add(pixelSize)) {
//                    continue;
//                }
//
//                // First pass — measure every character to find the monospaced cell width.
//                int cellWidth = 0;
//                for (int cp = TEXT_CHAR_START; cp <= TEXT_CHAR_END; cp++) {
//                    javafx.scene.text.Text measurer = new javafx.scene.text.Text(String.valueOf((char) cp));
//                    measurer.setFont(font.getFont(pixelSize));
//                    int charWidth = (int) Math.ceil(measurer.getLayoutBounds().getWidth()) + TEXT_PAD * 2;
//                    if (charWidth > cellWidth) {
//                        cellWidth = charWidth;
//                    }
//                }
//                textCellWidthMap.put(textCellKey(font.ordinal(), pixelSize), cellWidth);
//
//                // Second pass — render and store each character.
//                for (int cp = TEXT_CHAR_START; cp <= TEXT_CHAR_END; cp++) {
//                    renderTextEntry(font, pixelSize, cp);
//                    loadingScreen.increment();
//                }
//            }
//        }
//    }
//
//    /** Renders a single character to the offscreen canvas and stores its pixels in textCache. */
//    private static void renderTextEntry(Fonts font, int pixelSize, int codePoint) {
//        long key = textKey(font.ordinal(), pixelSize, codePoint);
//        if (textCache.containsKey(key)) {
//            return;
//        }
//
//        String character = new String(Character.toChars(codePoint));
//        javafx.scene.text.Text measurer = new javafx.scene.text.Text(character);
//        measurer.setFont(font.getFont(pixelSize));
//        javafx.geometry.Bounds bounds = measurer.getLayoutBounds();
//
//        int imageWidth  = (int) Math.ceil(bounds.getWidth())  + TEXT_PAD * 2;
//        int imageHeight = (int) Math.ceil(bounds.getHeight()) + TEXT_PAD * 2;
//        int baselineY   = (int) Math.ceil(-bounds.getMinY())  + TEXT_PAD;
//
//        if (imageWidth > textCanvas.getWidth() || imageHeight > textCanvas.getHeight()) {
//            textCanvas.setWidth(Math.max(imageWidth,  (int) textCanvas.getWidth()));
//            textCanvas.setHeight(Math.max(imageHeight, (int) textCanvas.getHeight()));
//        }
//
//        textGC.clearRect(0, 0, textCanvas.getWidth(), textCanvas.getHeight());
//        textGC.setFill(Color.WHITE);
//        textGC.setFont(font.getFont(pixelSize));
//        textGC.setTextAlign(TextAlignment.LEFT);
//        textGC.setTextBaseline(VPos.BASELINE);
//        textGC.fillText(character, TEXT_PAD, baselineY);
//
//        WritableImage snapshot = new WritableImage(imageWidth, imageHeight);
//        textCanvas.snapshot(null, snapshot);
//
//        int[] pixels = new int[imageWidth * imageHeight];
//        snapshot.getPixelReader().getPixels(0, 0, imageWidth, imageHeight,
//                PixelFormat.getIntArgbPreInstance(), pixels, 0, imageWidth);
//
//        int[] entry = new int[3 + pixels.length];
//        entry[0] = imageWidth;
//        entry[1] = imageHeight;
//        entry[2] = baselineY;
//        System.arraycopy(pixels, 0, entry, 3, pixels.length);
//        textCache.put(key, entry);
//    }
//
//    /**
//     * Call once after colorUnits() to extract int[][] pixel arrays from the
//     * colored WritableImages. After this, fillModel() never touches a PixelReader.
//     */
//    public static void buildModelPixelArrays() {
//        playersModelPixelsMap = new Object2ObjectOpenHashMap<>();
//        modelFrameWidthMap    = new EnumMap<>(Models.class);
//        modelFrameHeightMap   = new EnumMap<>(Models.class);
//        PixelFormat<IntBuffer> format = PixelFormat.getIntArgbPreInstance();
//
//        for (var playerEntry : playersModelMap.entrySet()) {
//            EnumMap<Models, int[][]> modelPixelsMap = new EnumMap<>(Models.class);
//            for (var modelEntry : playerEntry.getValue().entrySet()) {
//                WritableImage[] frames = modelEntry.getValue();
//                int frameWidth  = (int) frames[0].getWidth();
//                int frameHeight = (int) frames[0].getHeight();
//                modelFrameWidthMap.putIfAbsent(modelEntry.getKey(),  frameWidth);
//                modelFrameHeightMap.putIfAbsent(modelEntry.getKey(), frameHeight);
//
//                int[][] pixelFrames = new int[frames.length][];
//                for (int f = 0; f < frames.length; f++) {
//                    pixelFrames[f] = new int[frameWidth * frameHeight];
//                    frames[f].getPixelReader().getPixels(0, 0, frameWidth, frameHeight,
//                            format, pixelFrames[f], 0, frameWidth);
//                }
//                modelPixelsMap.put(modelEntry.getKey(), pixelFrames);
//            }
//            playersModelPixelsMap.put(playerEntry.getKey(), modelPixelsMap);
//        }
//    }
//
//    // =========================================================================
//    //  Frame lifecycle
//    // =========================================================================
//
//    public static void startDraw() {
//        gameViewport.freeze();
//        Arrays.fill(backPixels, 0);
//    }
//
//    /**
//     * Flushes the back-buffer to the canvas.
//     * This is the only GC call per frame.
//     * Must be called on the JavaFX Application Thread.
//     */
//    public static void endDraw() {
//        backBuffer.updateBuffer(b -> new Rectangle2D(0, 0, backWidth, backHeight));
//        gc.drawImage(backImage, 0, 0);
//    }
//
//    // =========================================================================
//    //  Color helpers
//    //  Input format:  0xRRGGBBAA straight alpha (same as original code)
//    //  Internal format: 0xAARRGGBB premultiplied (IntArgbPre)
//    // =========================================================================
//
//    /**converts 0xRRGGBBAA straight-alpha to 0xAARRGGBB premultiplied, result cached*/
//    private static int toPre(int rrggbbaa) {
//        if (rrggbbaa == lastRawColor) {
//            return lastPreColor;
//        }
//        lastRawColor = rrggbbaa;
//        int r = (rrggbbaa >>> 24) & 0xFF;
//        int g = (rrggbbaa >>> 16) & 0xFF;
//        int b = (rrggbbaa >>>  8) & 0xFF;
//        int a =  rrggbbaa         & 0xFF;
//        lastPreColor = switch (a) {
//            case   0 -> 0;
//            case 255 -> (0xFF << 24) | (r << 16) | (g << 8) | b;
//            default  -> (a << 24) | ((r * a / 255) << 16) | ((g * a / 255) << 8) | (b * a / 255);
//        };
//        return lastPreColor;
//    }
//
//    /**sets the current color, kept for API compatibility*/
//    public static void setColor(int color) {
//        toPre(color);
//    }
//
//    /**no-op, kept for API compatibility*/
//    public static void setThickness(double thickness) {}
//
//    // =========================================================================
//    //  Pixel compositor
//    // =========================================================================
//
//    /**premultiplied src-over composite of pre onto backPixels[index]*/
//    private static void plot(int index, int pre) {
//        int sourceAlpha = (pre >>> 24) & 0xFF;
//        if (sourceAlpha == 0) {
//            return;
//        }
//        if (sourceAlpha == 255) {
//            backPixels[index] = pre;
//            return;
//        }
//        int destination = backPixels[index];
//        int inverse     = 255 - sourceAlpha;
//        backPixels[index] =
//                (( sourceAlpha + ((destination >>> 24 & 0xFF) * inverse + 127) / 255) << 24) |
//                        (((pre >> 16 & 0xFF) + ((destination >> 16 & 0xFF) * inverse + 127) / 255) << 16) |
//                        (((pre >>  8 & 0xFF) + ((destination >>  8 & 0xFF) * inverse + 127) / 255) <<  8) |
//                        (( pre      & 0xFF) + (( destination      & 0xFF) * inverse + 127) / 255);
//    }
//
//    /**fills horizontal span [x0, x1) on row y*/
//    private static void hspan(int y, int x0, int x1, int pre) {
//        if (y < 0 || y >= backHeight) {
//            return;
//        }
//        x0 = Math.max(x0, 0);
//        x1 = Math.min(x1, backWidth);
//        if (x0 >= x1) {
//            return;
//        }
//        int base        = y * backWidth;
//        int sourceAlpha = (pre >>> 24) & 0xFF;
//        if (sourceAlpha == 255) {
//            Arrays.fill(backPixels, base + x0, base + x1, pre);
//        } else {
//            for (int x = x0; x < x1; x++) {
//                plot(base + x, pre);
//            }
//        }
//    }
//
//    // =========================================================================
//    //  Projection helpers
//    // =========================================================================
//
//    /**projects the x coordinate to screen position*/
//    private static double projectX(double x) {
//        return (x - Viewport.getX()) * Viewport.getScale() + Viewport.getXOffset();
//    }
//
//    /**projects the y coordinate to screen position*/
//    private static double projectY(double y) {
//        return (y - Viewport.getY()) * Viewport.getScale() + Viewport.getYOffset();
//    }
//
//    // =========================================================================
//    //  Software rasterizers
//    // =========================================================================
//
//    /**rasterizes a filled axis-aligned rectangle*/
//    private static void softFillRect(int x0, int y0, int x1, int y1, int pre) {
//        y0 = Math.max(y0, 0);
//        y1 = Math.min(y1, backHeight);
//        for (int y = y0; y < y1; y++) {
//            hspan(y, x0, x1, pre);
//        }
//    }
//
//    /**rasterizes a stroked axis-aligned rectangle*/
//    private static void softStrokeRect(int x0, int y0, int x1, int y1, int thickness, int pre) {
//        softFillRect(x0,              y0,              x1,              y0 + thickness, pre);
//        softFillRect(x0,              y1 - thickness,  x1,              y1,             pre);
//        softFillRect(x0,              y0 + thickness,  x0 + thickness,  y1 - thickness, pre);
//        softFillRect(x1 - thickness,  y0 + thickness,  x1,              y1 - thickness, pre);
//    }
//
//    /**rasterizes a filled rounded rectangle using per-scanline circle formula for corners*/
//    private static void softFillRoundedRect(int x, int y, int width, int height, int arc, int pre) {
//        arc = Math.min(arc, Math.min(width, height) / 2);
//        long arcSquared = (long) arc * arc;
//        int topBand    = y + arc;
//        int bottomBand = y + height - arc;
//        int yEnd       = Math.min(y + height, backHeight);
//        for (int py = Math.max(y, 0); py < yEnd; py++) {
//            int x0, x1;
//            if (py < topBand) {
//                long dy = topBand - py;
//                int dx = (int) Math.sqrt(arcSquared - dy * dy);
//                x0 = x + arc - dx;
//                x1 = x + width - arc + dx;
//            } else if (py >= bottomBand) {
//                long dy = py - bottomBand + 1;
//                if (dy > arc) {
//                    continue;
//                }
//                int dx = (int) Math.sqrt(arcSquared - dy * dy);
//                x0 = x + arc - dx;
//                x1 = x + width - arc + dx;
//            } else {
//                x0 = x;
//                x1 = x + width;
//            }
//            hspan(py, x0, x1, pre);
//        }
//    }
//
//    /**rasterizes a stroked rounded rectangle by subtracting an inner rounded rect per scanline*/
//    private static void softStrokeRoundedRect(int x, int y, int width, int height, int arc, int thickness, int pre) {
//        arc = Math.min(arc, Math.min(width, height) / 2);
//        long arcSquared = (long) arc * arc;
//        int topBand    = y + arc;
//        int bottomBand = y + height - arc;
//
//        int innerX    = x + thickness;
//        int innerY    = y + thickness;
//        int innerW    = width  - 2 * thickness;
//        int innerH    = height - 2 * thickness;
//        int innerArc  = Math.max(arc - thickness, 0);
//        long innerArcSquared = (long) innerArc * innerArc;
//        int innerTopBand    = innerY + innerArc;
//        int innerBottomBand = innerY + innerH - innerArc;
//
//        int yEnd = Math.min(y + height, backHeight);
//        for (int py = Math.max(y, 0); py < yEnd; py++) {
//            int outerX0, outerX1;
//            if (py < topBand) {
//                long dy = topBand - py;
//                int dx = (int) Math.sqrt(arcSquared - dy * dy);
//                outerX0 = x + arc - dx;
//                outerX1 = x + width - arc + dx;
//            } else if (py >= bottomBand) {
//                long dy = py - bottomBand + 1;
//                if (dy > arc) {
//                    continue;
//                }
//                int dx = (int) Math.sqrt(arcSquared - dy * dy);
//                outerX0 = x + arc - dx;
//                outerX1 = x + width - arc + dx;
//            } else {
//                outerX0 = x;
//                outerX1 = x + width;
//            }
//
//            int innerX0, innerX1;
//            if (innerW <= 0 || innerH <= 0 || py < innerY || py >= innerY + innerH) {
//                innerX0 = outerX1;
//                innerX1 = outerX1;
//            } else if (py < innerTopBand) {
//                long dy = innerTopBand - py;
//                if (dy > innerArc) {
//                    innerX0 = innerX;
//                    innerX1 = innerX + innerW;
//                } else {
//                    int dx = (int) Math.sqrt(innerArcSquared - dy * dy);
//                    innerX0 = innerX + innerArc - dx;
//                    innerX1 = innerX + innerW - innerArc + dx;
//                }
//            } else if (py >= innerBottomBand) {
//                long dy = py - innerBottomBand + 1;
//                if (dy > innerArc) {
//                    innerX0 = innerX;
//                    innerX1 = innerX + innerW;
//                } else {
//                    int dx = (int) Math.sqrt(innerArcSquared - dy * dy);
//                    innerX0 = innerX + innerArc - dx;
//                    innerX1 = innerX + innerW - innerArc + dx;
//                }
//            } else {
//                innerX0 = innerX;
//                innerX1 = innerX + innerW;
//            }
//
//            hspan(py, outerX0, innerX0, pre);
//            hspan(py, innerX1, outerX1, pre);
//        }
//    }
//
//    /**rasterizes a filled ellipse*/
//    private static void softFillOval(int centerX, int centerY, int radiusX, int radiusY, int pre) {
//        if (radiusX <= 0 || radiusY <= 0) {
//            return;
//        }
//        long rx2 = (long) radiusX * radiusX;
//        long ry2 = (long) radiusY * radiusY;
//        int yEnd = Math.min(centerY + radiusY + 1, backHeight);
//        for (int py = Math.max(centerY - radiusY, 0); py < yEnd; py++) {
//            long dy  = py - centerY;
//            long dx2 = rx2 * (ry2 - dy * dy) / ry2;
//            if (dx2 < 0) {
//                continue;
//            }
//            int dx = (int) Math.sqrt(dx2);
//            hspan(py, centerX - dx, centerX + dx + 1, pre);
//        }
//    }
//
//    /**rasterizes a stroked ellipse by subtracting an inner ellipse per scanline*/
//    private static void softStrokeOval(int centerX, int centerY, int radiusX, int radiusY, int thickness, int pre) {
//        if (radiusX <= 0 || radiusY <= 0) {
//            return;
//        }
//        int innerRX = Math.max(radiusX - thickness, 0);
//        int innerRY = Math.max(radiusY - thickness, 0);
//        long rx2  = (long) radiusX * radiusX;
//        long ry2  = (long) radiusY * radiusY;
//        long irx2 = (long) innerRX * innerRX;
//        long iry2 = (long) innerRY * innerRY;
//        int yEnd  = Math.min(centerY + radiusY + 1, backHeight);
//        for (int py = Math.max(centerY - radiusY, 0); py < yEnd; py++) {
//            long dy   = py - centerY;
//            long odx2 = rx2 * (ry2 - dy * dy) / ry2;
//            if (odx2 < 0) {
//                continue;
//            }
//            int odx = (int) Math.sqrt(odx2);
//            if (innerRY == 0 || Math.abs(dy) > innerRY) {
//                hspan(py, centerX - odx, centerX + odx + 1, pre);
//            } else {
//                long idx2 = irx2 * (iry2 - dy * dy) / iry2;
//                if (idx2 < 0) {
//                    hspan(py, centerX - odx, centerX + odx + 1, pre);
//                } else {
//                    int idx = (int) Math.sqrt(idx2);
//                    hspan(py, centerX - odx, centerX - idx, pre);
//                    hspan(py, centerX + idx + 1, centerX + odx + 1, pre);
//                }
//            }
//        }
//    }
//
//    /**rasterizes a thick line as a filled parallelogram; falls back to Bresenham for thin lines*/
//    private static void softLine(int x1, int y1, int x2, int y2, double thickness, int pre) {
//        if (thickness <= 1.0) {
//            bresenham(x1, y1, x2, y2, pre);
//            return;
//        }
//        double dx  = x2 - x1;
//        double dy  = y2 - y1;
//        double len = Math.sqrt(dx * dx + dy * dy);
//        if (len < 0.5) {
//            int radius = (int) (thickness * 0.5);
//            softFillOval(x1, y1, radius, radius, pre);
//            return;
//        }
//        double normalX = -dy / len * thickness * 0.5;
//        double normalY =  dx / len * thickness * 0.5;
//        fillConvexQuad(
//                (int) (x1 + normalX), (int) (y1 + normalY),
//                (int) (x1 - normalX), (int) (y1 - normalY),
//                (int) (x2 - normalX), (int) (y2 - normalY),
//                (int) (x2 + normalX), (int) (y2 + normalY),
//                pre);
//    }
//
//    /**rasterizes a dashed thick line as sequential softLine segments*/
//    private static void softLineDotted(int x1, int y1, int x2, int y2,
//                                       double thickness, int pre, double dotLength, double spaceLength) {
//        double dx     = x2 - x1;
//        double dy     = y2 - y1;
//        double length = Math.sqrt(dx * dx + dy * dy);
//        if (length < 0.5) {
//            return;
//        }
//        double stepX  = dx / length;
//        double stepY  = dy / length;
//        double stride = dotLength + spaceLength;
//        for (double d = 0; d < length; d += stride) {
//            double segEnd = Math.min(d + dotLength, length);
//            softLine(
//                    (int) (x1 + stepX * d),      (int) (y1 + stepY * d),
//                    (int) (x1 + stepX * segEnd), (int) (y1 + stepY * segEnd),
//                    thickness, pre);
//        }
//    }
//
//    /**rasterizes text by blitting pre-rendered monospaced character images from the text cache*/
//    private static void softText(String text, int sx, int sy,
//                                 Fonts font, double size, StringAlignment alignment, int color) {
//        int pre       = toPre(color);
//        int pixelSize = Math.max(1, (int) Math.round(size * Viewport.getScale()));
//
//        // Look up monospaced cell width for this font + size.
//        long cellKey    = textCellKey(font.ordinal(), pixelSize);
//        int  cellWidth  = textCellWidthMap.getOrDefault(cellKey, pixelSize);
//
//        // Use 'A' as the reference character for line metrics.
//        int[] refEntry  = textCache.get(textKey(font.ordinal(), pixelSize, 'A'));
//        if (refEntry == null) {
//            return;
//        }
//        int ascent     = refEntry[2];       // pixels from top of image to baseline
//        int lineHeight = refEntry[1];       // total image height
//
//        // Adjust the baseline y for the requested vertical alignment.
//        int baselineY = sy;
//        VPos vpos     = alignment.getVPos();
//        if (vpos == VPos.TOP) {
//            baselineY = sy + ascent;
//        } else if (vpos == VPos.BOTTOM) {
//            baselineY = sy - (lineHeight - ascent);
//        } else if (vpos == VPos.CENTER) {
//            baselineY = sy - lineHeight / 2 + ascent;
//        }
//        // VPos.BASELINE: baselineY = sy (no change)
//
//        // Compute total string width for horizontal alignment.
//        int totalWidth = text.length() * cellWidth;
//        int startX     = sx;
//        TextAlignment textAlign = alignment.getTextAlignment();
//        if (textAlign == TextAlignment.CENTER) {
//            startX = sx - totalWidth / 2;
//        } else if (textAlign == TextAlignment.RIGHT) {
//            startX = sx - totalWidth;
//        }
//
//        // Blit each character, centered within its cell.
//        int cursor = startX;
//        for (int i = 0; i < text.length(); ) {
//            int    codePoint = text.codePointAt(i);
//            int[]  entry     = textCache.get(textKey(font.ordinal(), pixelSize, codePoint));
//            if (entry != null) {
//                int cellOffsetX = (cellWidth - entry[0]) / 2;
//                blitText(entry, cursor + cellOffsetX, baselineY - entry[2], pre);
//            }
//            cursor += cellWidth;
//            i += Character.charCount(codePoint);
//        }
//    }
//
//    /**blits a pre-rendered text entry, tinting its white pixels to the given premultiplied color*/
//    private static void blitText(int[] entry, int dx, int dy, int pre) {
//        int imageWidth  = entry[0];
//        int imageHeight = entry[1];
//        int tintA = (pre >>> 24) & 0xFF;
//        int tintR = (pre >> 16)  & 0xFF;
//        int tintG = (pre >>  8)  & 0xFF;
//        int tintB =  pre         & 0xFF;
//
//        int x0 = Math.max(dx, 0);
//        int y0 = Math.max(dy, 0);
//        int x1 = Math.min(dx + imageWidth,  backWidth);
//        int y1 = Math.min(dy + imageHeight, backHeight);
//        if (x0 >= x1 || y0 >= y1) {
//            return;
//        }
//
//        for (int py = y0; py < y1; py++) {
//            int srcRow = (py - dy) * imageWidth;
//            int dstRow = py * backWidth;
//            for (int px = x0; px < x1; px++) {
//                int glyph  = entry[3 + srcRow + (px - dx)];
//                int glyphA = (glyph >>> 24) & 0xFF;
//                if (glyphA == 0) {
//                    continue;
//                }
//                // Glyph was rendered white, so tint by multiplying each channel.
//                int outA = tintA * glyphA / 255;
//                int outR = tintR * glyphA / 255;
//                int outG = tintG * glyphA / 255;
//                int outB = tintB * glyphA / 255;
//                plot(dstRow + px, (outA << 24) | (outR << 16) | (outG << 8) | outB);
//            }
//        }
//    }
//
//    /**nearest-neighbour scaled blit with premultiplied src-over compositing*/
//    private static void blit(int[] src, int sourceWidth, int sourceHeight,
//                             int dx, int dy, int destWidth, int destHeight) {
//        if (destWidth <= 0 || destHeight <= 0) {
//            return;
//        }
//        int x0 = Math.max(dx, 0);
//        int y0 = Math.max(dy, 0);
//        int x1 = Math.min(dx + destWidth,  backWidth);
//        int y1 = Math.min(dy + destHeight, backHeight);
//        if (x0 >= x1 || y0 >= y1) {
//            return;
//        }
//        int stepX  = (sourceWidth  << 16) / destWidth;
//        int stepY  = (sourceHeight << 16) / destHeight;
//        int baseY  = (y0 - dy) * stepY;
//        for (int py = y0; py < y1; py++, baseY += stepY) {
//            int srcRow = (baseY >> 16) * sourceWidth;
//            int dstRow = py * backWidth;
//            int baseX  = (x0 - dx) * stepX;
//            for (int px = x0; px < x1; px++, baseX += stepX) {
//                plot(dstRow + px, src[srcRow + (baseX >> 16)]);
//            }
//        }
//    }
//
//    /**scanline fills a convex quad; used by thick line rasterization*/
//    private static void fillConvexQuad(int x0, int y0, int x1, int y1,
//                                       int x2, int y2, int x3, int y3, int pre) {
//        int[] xs = {x0, x1, x2, x3};
//        int[] ys = {y0, y1, y2, y3};
//        int minY = Math.max(Math.min(Math.min(y0, y1), Math.min(y2, y3)), 0);
//        int maxY = Math.min(Math.max(Math.max(y0, y1), Math.max(y2, y3)), backHeight - 1);
//        for (int py = minY; py <= maxY; py++) {
//            int xMin = Integer.MAX_VALUE;
//            int xMax = Integer.MIN_VALUE;
//            for (int i = 0; i < 4; i++) {
//                int j  = (i + 1) & 3;
//                int ya = ys[i];
//                int yb = ys[j];
//                if ((ya <= py && py < yb) || (yb <= py && py < ya)) {
//                    int x = xs[i] + (py - ya) * (xs[j] - xs[i]) / (yb - ya);
//                    if (x < xMin) xMin = x;
//                    if (x > xMax) xMax = x;
//                }
//            }
//            if (xMin <= xMax) {
//                hspan(py, xMin, xMax + 1, pre);
//            }
//        }
//    }
//
//    /**Bresenham thin-line rasterizer*/
//    private static void bresenham(int x0, int y0, int x1, int y1, int pre) {
//        int dx  =  Math.abs(x1 - x0);
//        int dy  = -Math.abs(y1 - y0);
//        int sx  = x0 < x1 ? 1 : -1;
//        int sy  = y0 < y1 ? 1 : -1;
//        int err = dx + dy;
//        while (true) {
//            if (x0 >= 0 && x0 < backWidth && y0 >= 0 && y0 < backHeight) {
//                plot(y0 * backWidth + x0, pre);
//            }
//            if (x0 == x1 && y0 == y1) {
//                break;
//            }
//            int e2 = 2 * err;
//            if (e2 >= dy) { err += dy; x0 += sx; }
//            if (e2 <= dx) { err += dx; y0 += sy; }
//        }
//    }
//
//    /**maps a degree direction to a 16-frame sprite index*/
//    private static int directionIndex(double direction) {
//        return (int) (StrictMath.floorMod((int) (direction + 11.25), 360) / 22.5);
//    }
//
//    // =========================================================================
//    //  Text cache key helpers
//    // =========================================================================
//
//    private static long textKey(int fontOrdinal, int pixelSize, int codePoint) {
//        return ((long) codePoint << 24) | ((long) pixelSize << 8) | (long) fontOrdinal;
//    }
//
//    private static long textCellKey(int fontOrdinal, int pixelSize) {
//        return ((long) pixelSize << 8) | (long) fontOrdinal;
//    }
//
//    // =========================================================================
//    //  Canvas utilities
//    // =========================================================================
//
//    public static void clearCanvas() {
//        Arrays.fill(backPixels, 0);
//    }
//
//    public static void fillBackground() {
//        double scale = Viewport.getScale();
//        int xOffset  = (int) Viewport.getXOffset();
//        int yOffset  = (int) Viewport.getYOffset();
//        softFillRect(0, 0, backWidth, backHeight, 0xFF000000);
//        softFillRect(xOffset, yOffset,
//                xOffset + (int) (1920 * scale),
//                yOffset + (int) (1080 * scale), 0xFF323232);
//    }
//
//    public static void fillOffsetEdge() {
//        double scale = Viewport.getScale();
//        int xOffset  = (int) Viewport.getXOffset();
//        int yOffset  = (int) Viewport.getYOffset();
//        int gameW    = (int) (1920 * scale);
//        int gameH    = (int) (1080 * scale);
//        softFillRect(0,              0,            gameW + xOffset * 2, yOffset,          0xFF000000);
//        softFillRect(0,              0,            xOffset,              gameH + yOffset * 2, 0xFF000000);
//        softFillRect(gameW + xOffset, 0,           xOffset,              gameH + yOffset * 2, 0xFF000000);
//        softFillRect(0,              gameH + yOffset, gameW + xOffset * 2, yOffset * 2,        0xFF000000);
//    }
//
//    // =========================================================================
//    //  Rotation — no-op in pixel-buffer path; models use direction parameter
//    // =========================================================================
//
//    public static void startRotation(double x1, double y1, double x2, double y2,
//                                     double xOffset, double yOffset,
//                                     double direction1, double direction2) {}
//
//    public static void resetRotation() {}
//
//    // =========================================================================
//    //  Getters / setters
//    // =========================================================================
//
//    public static synchronized double getFactor()              { return factor; }
//    public static synchronized void   setFactor(double f)     { DrawUtil.factor = StrictMath.clamp(f, 0, 1); }
//    public static void setGameViewport(GameViewport gameViewport) { DrawUtil.gameViewport = gameViewport; }
//    public static GraphicsContext getGC()                      { return gc; }
//    public static void setGC(GraphicsContext gc)               { DrawUtil.gc = gc; }
//
//    // =========================================================================
//    //  Public draw API — scaled (long) variants
//    // =========================================================================
//
//    /**draws a filled rectangle, scaled inputs*/
//    public static void fillRectScaled(long xScaled, long yScaled, long widthScaled, long heightScaled, int color) {
//        double scale = Viewport.getScale();
//        int pre      = toPre(color);
//        int px       = (int) projectX(LTD(xScaled));
//        int py       = (int) projectY(LTD(yScaled));
//        softFillRect(px, py, px + (int) (LTD(widthScaled) * scale), py + (int) (LTD(heightScaled) * scale), pre);
//    }
//
//    /**draws a rectangle outline, scaled inputs*/
//    public static void strokeRectScaled(long xScaled, long yScaled, long widthScaled, long heightScaled, int color, double strokeWidth) {
//        double scale = Viewport.getScale();
//        int pre      = toPre(color);
//        int thickness = Math.max(1, (int) (strokeWidth * scale));
//        int px       = (int) projectX(LTD(xScaled));
//        int py       = (int) projectY(LTD(yScaled));
//        softStrokeRect(px, py, px + (int) (LTD(widthScaled) * scale), py + (int) (LTD(heightScaled) * scale), thickness, pre);
//    }
//
//    /**draws a filled rounded rectangle, scaled inputs*/
//    public static void fillRoundedRectScaled(long xScaled, long yScaled, long widthScaled, long heightScaled, double arc, int color) {
//        double scale = Viewport.getScale();
//        int pre      = toPre(color);
//        softFillRoundedRect((int) projectX(LTD(xScaled)), (int) projectY(LTD(yScaled)),
//                (int) (LTD(widthScaled) * scale), (int) (LTD(heightScaled) * scale),
//                (int) (arc * scale), pre);
//    }
//
//    /**draws a rounded rectangle outline, scaled inputs*/
//    public static void strokeRoundedRectScaled(long xScaled, long yScaled, long widthScaled, long heightScaled, double arc, int color, double strokeWidth) {
//        double scale  = Viewport.getScale();
//        int pre       = toPre(color);
//        int thickness = Math.max(1, (int) (strokeWidth * scale));
//        softStrokeRoundedRect((int) projectX(LTD(xScaled)), (int) projectY(LTD(yScaled)),
//                (int) (LTD(widthScaled) * scale), (int) (LTD(heightScaled) * scale),
//                (int) (arc * scale), thickness, pre);
//    }
//
//    /**draws a filled circle, scaled inputs*/
//    public static void fillCircleScaled(long xScaled, long yScaled, long radiusScaled, int color) {
//        double scale = Viewport.getScale();
//        int pre      = toPre(color);
//        int radius   = (int) (LTD(radiusScaled) * scale);
//        softFillOval((int) projectX(LTD(xScaled)) + radius, (int) projectY(LTD(yScaled)) + radius,
//                radius, radius, pre);
//    }
//
//    /**draws a circle outline, scaled inputs*/
//    public static void strokeCircleScaled(long xScaled, long yScaled, long radiusScaled, int color, double strokeWidth) {
//        double scale  = Viewport.getScale();
//        int pre       = toPre(color);
//        int radius    = (int) (LTD(radiusScaled) * scale);
//        int thickness = Math.max(1, (int) (strokeWidth * scale));
//        softStrokeOval((int) projectX(LTD(xScaled)) + radius, (int) projectY(LTD(yScaled)) + radius,
//                radius, radius, thickness, pre);
//    }
//
//    /**draws a filled oval, scaled inputs*/
//    public static void fillOvalScaled(long xScaled, long yScaled, long widthScaled, long heightScaled, int color) {
//        double scale = Viewport.getScale();
//        int pre      = toPre(color);
//        int rx       = (int) (LTD(widthScaled)  * scale / 2);
//        int ry       = (int) (LTD(heightScaled) * scale / 2);
//        softFillOval((int) projectX(LTD(xScaled)) + rx, (int) projectY(LTD(yScaled)) + ry, rx, ry, pre);
//    }
//
//    /**draws an oval outline, scaled inputs*/
//    public static void strokeOvalScaled(long xScaled, long yScaled, long widthScaled, long heightScaled, int color, double strokeWidth) {
//        double scale  = Viewport.getScale();
//        int pre       = toPre(color);
//        int rx        = (int) (LTD(widthScaled)  * scale / 2);
//        int ry        = (int) (LTD(heightScaled) * scale / 2);
//        int thickness = Math.max(1, (int) (strokeWidth * scale));
//        softStrokeOval((int) projectX(LTD(xScaled)) + rx, (int) projectY(LTD(yScaled)) + ry, rx, ry, thickness, pre);
//    }
//
//    /**draws a line, scaled inputs*/
//    public static void fillLineScaled(long startXScaled, long startYScaled, long endXScaled, long endYScaled, int color, double thickness) {
//        double scale = Viewport.getScale();
//        int pre      = toPre(color);
//        softLine((int) projectX(LTD(startXScaled)), (int) projectY(LTD(startYScaled)),
//                (int) projectX(LTD(endXScaled)),   (int) projectY(LTD(endYScaled)),
//                thickness * scale, pre);
//    }
//
//    /**draws a dotted line, scaled inputs*/
//    public static void fillLineDottedScaled(long startXScaled, long startYScaled, long endXScaled, long endYScaled, int color, double thickness, double dotLength, double spaceLength) {
//        double scale = Viewport.getScale();
//        int pre      = toPre(color);
//        softLineDotted((int) projectX(LTD(startXScaled)), (int) projectY(LTD(startYScaled)),
//                (int) projectX(LTD(endXScaled)),   (int) projectY(LTD(endYScaled)),
//                thickness * scale, pre, dotLength, spaceLength);
//    }
//
//    /**draws filled text, scaled inputs*/
//    public static void fillTextScaled(String text, long xScaled, long yScaled, Fonts font, double size, StringAlignment alignment) {
//        softText(text, (int) projectX(LTD(xScaled)), (int) projectY(LTD(yScaled)), font, size, alignment, lastRawColor);
//    }
//
//    /**draws an image, scaled inputs*/
//    public static void fillImageScaled(Image image, long xScaled, long yScaled, long widthScaled, long heightScaled) {
//        double scale  = Viewport.getScale();
//        int srcWidth  = (int) image.getWidth();
//        int srcHeight = (int) image.getHeight();
//        int[] pixels  = new int[srcWidth * srcHeight];
//        image.getPixelReader().getPixels(0, 0, srcWidth, srcHeight,
//                PixelFormat.getIntArgbPreInstance(), pixels, 0, srcWidth);
//        blit(pixels, srcWidth, srcHeight,
//                (int) projectX(LTD(xScaled)), (int) projectY(LTD(yScaled)),
//                (int) (LTD(widthScaled) * scale), (int) (LTD(heightScaled) * scale));
//    }
//
//    /**draws a model image, scaled inputs*/
//    public static void fillModelImageScaled(Models modelKey, PlayerColor color, long xScaled, long yScaled, long directionScaled) {
//        double scale  = Viewport.getScale();
//        int[]  src    = playersModelPixelsMap.get(color).get(modelKey)[directionIndex(LTD(directionScaled))];
//        int frameWidth  = modelFrameWidthMap.get(modelKey);
//        int frameHeight = modelFrameHeightMap.get(modelKey);
//        blit(src, frameWidth, frameHeight,
//                (int) projectX(LTD(xScaled) - 128 + modelKey.getHalfWidth()),
//                (int) projectY(LTD(yScaled) - 128 + modelKey.getHalfHeight()),
//                (int) (256 * scale), (int) (256 * scale));
//    }
//
//    /**draws a model, scaled inputs*/
//    public static void fillModelScaled(Models modelKey, PlayerColor color, long xScaled, long yScaled, long zScaled, long directionScaled) {
//        fillModelImageScaled(modelKey, color, xScaled, yScaled, directionScaled);
//    }
//
//    // =========================================================================
//    //  Public draw API — unscaled (double) variants
//    // =========================================================================
//
//    /**draws a filled rectangle*/
//    public static void fillRect(double x, double y, double width, double height, int color) {
//        double scale = Viewport.getScale();
//        int pre      = toPre(color);
//        int px       = (int) projectX(x);
//        int py       = (int) projectY(y);
//        softFillRect(px, py, px + (int) (width * scale), py + (int) (height * scale), pre);
//    }
//
//    /**draws a filled rectangle*/
//    public static void fillRect(Rectangle2D rect, int color) {
//        fillRect(rect.getMinX(), rect.getMinY(), rect.getWidth(), rect.getHeight(), color);
//    }
//
//    /**draws a rectangle outline*/
//    public static void strokeRect(double x, double y, double width, double height, int color, double strokeWidth) {
//        double scale  = Viewport.getScale();
//        int pre       = toPre(color);
//        int thickness = Math.max(1, (int) (strokeWidth * scale));
//        int px        = (int) projectX(x);
//        int py        = (int) projectY(y);
//        softStrokeRect(px, py, px + (int) (width * scale), py + (int) (height * scale), thickness, pre);
//    }
//
//    /**draws a rectangle outline*/
//    public static void strokeRect(Rectangle2D rect, int color, double strokeWidth) {
//        strokeRect(rect.getMinX(), rect.getMinY(), rect.getWidth(), rect.getHeight(), color, strokeWidth);
//    }
//
//    /**draws a filled rounded rectangle*/
//    public static void fillRoundedRect(double x, double y, double width, double height, double arc, int color) {
//        double scale = Viewport.getScale();
//        int pre      = toPre(color);
//        softFillRoundedRect((int) projectX(x), (int) projectY(y),
//                (int) (width * scale), (int) (height * scale), (int) (arc * scale), pre);
//    }
//
//    /**draws a rounded rectangle outline*/
//    public static void strokeRoundedRect(double x, double y, double width, double height, double arc, int color, double strokeWidth) {
//        double scale  = Viewport.getScale();
//        int pre       = toPre(color);
//        int thickness = Math.max(1, (int) (strokeWidth * scale));
//        softStrokeRoundedRect((int) projectX(x), (int) projectY(y),
//                (int) (width * scale), (int) (height * scale), (int) (arc * scale), thickness, pre);
//    }
//
//    /**draws a filled circle*/
//    public static void fillCircle(double x, double y, double radius, int color) {
//        double scale = Viewport.getScale();
//        int pre      = toPre(color);
//        int r        = (int) (radius * scale);
//        softFillOval((int) projectX(x) + r, (int) projectY(y) + r, r, r, pre);
//    }
//
//    /**draws a circle outline*/
//    public static void strokeCircle(double x, double y, double radius, int color, double strokeWidth) {
//        double scale  = Viewport.getScale();
//        int pre       = toPre(color);
//        int r         = (int) (radius * scale);
//        int thickness = Math.max(1, (int) (strokeWidth * scale));
//        softStrokeOval((int) projectX(x) + r, (int) projectY(y) + r, r, r, thickness, pre);
//    }
//
//    /**draws a filled oval*/
//    public static void fillOval(double x, double y, double width, double height, int color) {
//        double scale = Viewport.getScale();
//        int pre      = toPre(color);
//        int rx       = (int) (width  * scale / 2);
//        int ry       = (int) (height * scale / 2);
//        softFillOval((int) projectX(x) + rx, (int) projectY(y) + ry, rx, ry, pre);
//    }
//
//    /**draws an oval outline*/
//    public static void strokeOval(double x, double y, double width, double height, int color, double strokeWidth) {
//        double scale  = Viewport.getScale();
//        int pre       = toPre(color);
//        int rx        = (int) (width  * scale / 2);
//        int ry        = (int) (height * scale / 2);
//        int thickness = Math.max(1, (int) (strokeWidth * scale));
//        softStrokeOval((int) projectX(x) + rx, (int) projectY(y) + ry, rx, ry, thickness, pre);
//    }
//
//    /**draws a line*/
//    public static void fillLine(double startX, double startY, double endX, double endY, int color, double strokeWidth) {
//        double scale = Viewport.getScale();
//        int pre      = toPre(color);
//        softLine((int) projectX(startX), (int) projectY(startY),
//                (int) projectX(endX),   (int) projectY(endY),
//                strokeWidth * scale, pre);
//    }
//
//    /**draws a dotted line*/
//    public static void fillLineDotted(double startX, double startY, double endX, double endY, int color, double strokeWidth, double dotLength, double spaceLength) {
//        double scale = Viewport.getScale();
//        int pre      = toPre(color);
//        softLineDotted((int) projectX(startX), (int) projectY(startY),
//                (int) projectX(endX),   (int) projectY(endY),
//                strokeWidth * scale, pre, dotLength, spaceLength);
//    }
//
//    /**draws filled text*/
//    public static void fillText(String text, double x, double y, Fonts font, double size, StringAlignment alignment, int color) {
//        softText(text, (int) projectX(x), (int) projectY(y), font, size, alignment, color);
//    }
//
//    /**draws an image*/
//    public static void fillImage(Image image, double x, double y, double width, double height) {
//        double scale  = Viewport.getScale();
//        int srcWidth  = (int) image.getWidth();
//        int srcHeight = (int) image.getHeight();
//        int[] pixels  = new int[srcWidth * srcHeight];
//        image.getPixelReader().getPixels(0, 0, srcWidth, srcHeight,
//                PixelFormat.getIntArgbPreInstance(), pixels, 0, srcWidth);
//        blit(pixels, srcWidth, srcHeight,
//                (int) projectX(x), (int) projectY(y),
//                (int) (width * scale), (int) (height * scale));
//    }
//
//    /**draws a model*/
//    public static void fillModel(Models modelKey, PlayerColor color, double x, double y, double z, double direction) {
//        double scale    = Viewport.getScale();
//        int[]  src      = playersModelPixelsMap.get(color).get(modelKey)[directionIndex(direction)];
//        int frameWidth  = modelFrameWidthMap.get(modelKey);
//        int frameHeight = modelFrameHeightMap.get(modelKey);
//        blit(src, frameWidth, frameHeight,
//                (int) projectX(x - 128 + modelKey.getHalfWidth()),
//                (int) projectY(y - 128 + modelKey.getHalfHeight()),
//                (int) (256 * scale), (int) (256 * scale));
//    }
//
//    // =========================================================================
//    //  Interpolation shorthands
//    // =========================================================================
//
//    /**shorthand for linear interpolate, scaled inputs*/
//    private static long lerp(long current, long last) {
//        return NumUtil.interpolate(current, last, factor);
//    }
//
//    /**shorthand for linear interpolate*/
//    private static double lerp(double current, double last) {
//        return NumUtil.interpolate(current, last, factor);
//    }
//
//    // =========================================================================
//    //  Lerp — unchanged from original, delegates to public methods above
//    // =========================================================================
//
//    public static class Lerp {
//        /**draws a filled rectangle, scaled inputs*/
//        public static void fillRectScaled(long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long widthScaled, long heightScaled, int color){DrawUtil.fillRectScaled(lerp(xCurrentScaled, xLastScaled), lerp(yCurrentScaled, yLastScaled), widthScaled, heightScaled, color);}
//        /**draws a rectangle outline, scaled inputs*/
//        public static void strokeRectScaled(long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long widthScaled, long heightScaled, int color, double strokeWidth){DrawUtil.strokeRectScaled(lerp(xCurrentScaled, xLastScaled), lerp(yCurrentScaled, yLastScaled), widthScaled, heightScaled, color, strokeWidth);}
//        /**draws a filled rounded rectangle, scaled inputs*/
//        public static void fillRoundedRectScaled(long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long widthScaled, long heightScaled, double arc, int color){DrawUtil.fillRoundedRectScaled(lerp(xCurrentScaled, xLastScaled), lerp(yCurrentScaled, yLastScaled), widthScaled, heightScaled, arc, color);}
//        /**draws a rounded rectangle outline, scaled inputs*/
//        public static void strokeRoundedRectScaled(long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long widthScaled, long heightScaled, double arc, int color, double strokeWidth){DrawUtil.strokeRoundedRectScaled(lerp(xCurrentScaled, xLastScaled), lerp(yCurrentScaled, yLastScaled), widthScaled, heightScaled, arc, color, strokeWidth);}
//        /**draws a filled circle, scaled inputs*/
//        public static void fillCircleScaled(long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long radiusScaled, int color){DrawUtil.fillCircleScaled(lerp(xCurrentScaled, xLastScaled), lerp(yCurrentScaled, yLastScaled), radiusScaled, color);}
//        /**draws a circle outline, scaled inputs*/
//        public static void strokeCircleScaled(long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long radiusScaled, int color, double strokeWidth){DrawUtil.strokeCircleScaled(lerp(xCurrentScaled, xLastScaled), lerp(yCurrentScaled, yLastScaled), radiusScaled, color, strokeWidth);}
//        /**draws a filled oval, scaled inputs*/
//        public static void fillOvalScaled(long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long widthScaled, long heightScaled, int color){DrawUtil.fillOvalScaled(lerp(xCurrentScaled, xLastScaled), lerp(yCurrentScaled, yLastScaled), widthScaled, heightScaled, color);}
//        /**draws an oval outline, scaled inputs*/
//        public static void strokeOvalScaled(long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long widthScaled, long heightScaled, int color, double strokeWidth){DrawUtil.strokeOvalScaled(lerp(xCurrentScaled, xLastScaled), lerp(yCurrentScaled, yLastScaled), widthScaled, heightScaled, color, strokeWidth);}
//        /**draws a line, scaled inputs*/
//        public static void fillLineScaled(long startXCurrentScaled, long startXLastScaled, long startYCurrentScaled, long startYLastScaled, long endXCurrentScaled, long endXLastScaled, long endYCurrentScaled, long endYLastScaled, int color, double thickness){DrawUtil.fillLineScaled(lerp(startXCurrentScaled, startXLastScaled), lerp(startYCurrentScaled, startYLastScaled), lerp(endXCurrentScaled, endXLastScaled), lerp(endYCurrentScaled, endYLastScaled), color, thickness);}
//        /**draws a dotted line, scaled inputs*/
//        public static void fillLineDottedScaled(long startXCurrentScaled, long startXLastScaled, long startYCurrentScaled, long startYLastScaled, long endXCurrentScaled, long endXLastScaled, long endYCurrentScaled, long endYLastScaled, int color, double thickness, double dotLength, double spaceLength){DrawUtil.fillLineDottedScaled(lerp(startXCurrentScaled, startXLastScaled), lerp(startYCurrentScaled, startYLastScaled), lerp(endXCurrentScaled, endXLastScaled), lerp(endYCurrentScaled, endYLastScaled), color, thickness, dotLength, spaceLength);}
//        /**draws filled text, scaled inputs*/
//        public static void fillTextScaled(String text, long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, Fonts font, double size, StringAlignment alignment){DrawUtil.fillTextScaled(text, lerp(xCurrentScaled, xLastScaled), lerp(yCurrentScaled, yLastScaled), font, size, alignment);}
//        /**draws an image, scaled inputs*/
//        public static void fillImageScaled(Image image, long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long widthScaled, long heightScaled){DrawUtil.fillImageScaled(image, lerp(xCurrentScaled, xLastScaled), lerp(yCurrentScaled, yLastScaled), widthScaled, heightScaled);}
//        /**draws a model, scaled inputs*/
//        public static void fillModelScaled(Models modelKey, PlayerColor color, long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long zCurrentScaled, long zLastScaled, long directionCurrentScaled, long directionLastScaled){DrawUtil.fillModelScaled(modelKey, color, lerp(xCurrentScaled, xLastScaled), lerp(yCurrentScaled, yLastScaled), lerp(zCurrentScaled, zLastScaled), lerp(directionCurrentScaled, directionLastScaled));}
//        /**draws a filled rectangle*/
//        public static void fillRect(double xCurrent, double xLast, double yCurrent, double yLast, double width, double height, int color){DrawUtil.fillRect(lerp(xCurrent, xLast), lerp(yCurrent, yLast), width, height, color);}
//        /**draws a rectangle outline*/
//        public static void strokeRect(double xCurrent, double xLast, double yCurrent, double yLast, double width, double height, int color, double strokeWidth){DrawUtil.strokeRect(lerp(xCurrent, xLast), lerp(yCurrent, yLast), width, height, color, strokeWidth);}
//        /**draws a filled rounded rectangle*/
//        public static void fillRoundedRect(double xCurrent, double xLast, double yCurrent, double yLast, double width, double height, double arc, int color){DrawUtil.fillRoundedRect(lerp(xCurrent, xLast), lerp(yCurrent, yLast), width, height, arc, color);}
//        /**draws a rounded rectangle outline*/
//        public static void strokeRoundedRect(double xCurrent, double xLast, double yCurrent, double yLast, double width, double height, double arc, int color, double strokeWidth){DrawUtil.strokeRoundedRect(lerp(xCurrent, xLast), lerp(yCurrent, yLast), width, height, arc, color, strokeWidth);}
//        /**draws a filled circle*/
//        public static void fillCircle(double xCurrent, double xLast, double yCurrent, double yLast, double radius, int color){DrawUtil.fillCircle(lerp(xCurrent, xLast), lerp(yCurrent, yLast), radius, color);}
//        /**draws a circle outline*/
//        public static void strokeCircle(double xCurrent, double xLast, double yCurrent, double yLast, double radius, int color, double strokeWidth){DrawUtil.strokeCircle(lerp(xCurrent, xLast), lerp(yCurrent, yLast), radius, color, strokeWidth);}
//        /**draws a filled oval*/
//        public static void fillOval(double xCurrent, double xLast, double yCurrent, double yLast, double width, double height, int color){DrawUtil.fillOval(lerp(xCurrent, xLast), lerp(yCurrent, yLast), width, height, color);}
//        /**draws an oval outline*/
//        public static void strokeOval(double xCurrent, double xLast, double yCurrent, double yLast, double width, double height, int color, double strokeWidth){DrawUtil.strokeOval(lerp(xCurrent, xLast), lerp(yCurrent, yLast), width, height, color, strokeWidth);}
//        /**draws a line*/
//        public static void fillLine(double startXCurrent, double startXLast, double startYCurrent, double startYLast, double endXCurrent, double endXLast, double endYCurrent, double endYLast, int color, double thickness){DrawUtil.fillLine(lerp(startXCurrent, startXLast), lerp(startYCurrent, startYLast), lerp(endXCurrent, endXLast), lerp(endYCurrent, endYLast), color, thickness);}
//        /**draws a dotted line*/
//        public static void fillLineDotted(double startXCurrent, double startXLast, double startYCurrent, double startYLast, double endXCurrent, double endXLast, double endYCurrent, double endYLast, int color, double thickness, double dotLength, double spaceLength){DrawUtil.fillLineDotted(lerp(startXCurrent, startXLast), lerp(startYCurrent, startYLast), lerp(endXCurrent, endXLast), lerp(endYCurrent, endYLast), color, thickness, dotLength, spaceLength);}
//        /**draws filled text*/
//        public static void fillText(String text, double xCurrent, double xLast, double yCurrent, double yLast, Fonts font, double size, StringAlignment alignment, int color){DrawUtil.fillText(text, lerp(xCurrent, xLast), lerp(yCurrent, yLast), font, size, alignment, color);}
//        /**draws an image*/
//        public static void fillImage(Image image, double xCurrent, double xLast, double yCurrent, double yLast, double width, double height){DrawUtil.fillImage(image, lerp(xCurrent, xLast), lerp(yCurrent, yLast), width, height);}
//        /**draws a model*/
//        public static void fillModel(Models modelKey, PlayerColor color, double xCurrent, double xLast, double yCurrent, double yLast, double zCurrent, double zLast, double directionCurrent, double directionLast){DrawUtil.fillModel(modelKey, color, lerp(xCurrent, xLast), lerp(yCurrent, yLast), lerp(zCurrent, zLast), lerp(directionCurrent, directionLast));}
//    }
//
//    // =========================================================================
//    //  Game — unchanged from original, delegates to public methods above
//    // =========================================================================
//
//    public static class Game {
//        /**puts the offset from gameViewport*/
//        private static long putXGO(long x){return x - NumUtil.DTL(lerp(gameViewport.getX(), gameViewport.getLastX()));}
//        /**puts the offset from gameViewport*/
//        private static long putYGO(long y){return y - NumUtil.DTL(lerp(gameViewport.getY(), gameViewport.getLastY()));}
//        /**puts the offset from gameViewport*/
//        private static double putXGO(double x){return x - lerp(gameViewport.getX(), gameViewport.getLastX());}
//        /**puts the offset from gameViewport*/
//        private static double putYGO(double y){return y - lerp(gameViewport.getY(), gameViewport.getLastY());}
//        /**returns true if needs to be culled*/
//        private static boolean cull(long x, long y, long width, long height){return false;}
//        /**returns true if needs to be culled*/
//        private static boolean cull(double x, double y, double width, double height){return false;}
//
//        /**draws a filled rectangle, scaled inputs*/
//        public static void fillRectScaled(long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long widthScaled, long heightScaled, int color){DrawUtil.fillRectScaled(putXGO(lerp(xCurrentScaled, xLastScaled)), putYGO(lerp(yCurrentScaled, yLastScaled)), widthScaled, heightScaled, color);}
//        /**draws a rectangle outline, scaled inputs*/
//        public static void strokeRectScaled(long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long widthScaled, long heightScaled, int color, double strokeWidth){DrawUtil.strokeRectScaled(putXGO(lerp(xCurrentScaled, xLastScaled)), putYGO(lerp(yCurrentScaled, yLastScaled)), widthScaled, heightScaled, color, strokeWidth);}
//        /**draws a filled rounded rectangle, scaled inputs*/
//        public static void fillRoundedRectScaled(long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long widthScaled, long heightScaled, double arc, int color){DrawUtil.fillRoundedRectScaled(putXGO(lerp(xCurrentScaled, xLastScaled)), putYGO(lerp(yCurrentScaled, yLastScaled)), widthScaled, heightScaled, arc, color);}
//        /**draws a rounded rectangle outline, scaled inputs*/
//        public static void strokeRoundedRectScaled(long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long widthScaled, long heightScaled, double arc, int color, double strokeWidth){DrawUtil.strokeRoundedRectScaled(putXGO(lerp(xCurrentScaled, xLastScaled)), putYGO(lerp(yCurrentScaled, yLastScaled)), widthScaled, heightScaled, arc, color, strokeWidth);}
//        /**draws a filled circle, scaled inputs*/
//        public static void fillCircleScaled(long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long radiusScaled, int color){DrawUtil.fillCircleScaled(putXGO(lerp(xCurrentScaled, xLastScaled)), putYGO(lerp(yCurrentScaled, yLastScaled)), radiusScaled, color);}
//        /**draws a circle outline, scaled inputs*/
//        public static void strokeCircleScaled(long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long radiusScaled, int color, double strokeWidth){DrawUtil.strokeCircleScaled(putXGO(lerp(xCurrentScaled, xLastScaled)), putYGO(lerp(yCurrentScaled, yLastScaled)), radiusScaled, color, strokeWidth);}
//        /**draws a filled oval, scaled inputs*/
//        public static void fillOvalScaled(long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long widthScaled, long heightScaled, int color){DrawUtil.fillOvalScaled(putXGO(lerp(xCurrentScaled, xLastScaled)), putYGO(lerp(yCurrentScaled, yLastScaled)), widthScaled, heightScaled, color);}
//        /**draws an oval outline, scaled inputs*/
//        public static void strokeOvalScaled(long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long widthScaled, long heightScaled, int color, double strokeWidth){DrawUtil.strokeOvalScaled(putXGO(lerp(xCurrentScaled, xLastScaled)), putYGO(lerp(yCurrentScaled, yLastScaled)), widthScaled, heightScaled, color, strokeWidth);}
//        /**draws a line, scaled inputs*/
//        public static void fillLineScaled(long startXCurrentScaled, long startXLastScaled, long startYCurrentScaled, long startYLastScaled, long endXCurrentScaled, long endXLastScaled, long endYCurrentScaled, long endYLastScaled, int color, double thickness){DrawUtil.fillLineScaled(putXGO(lerp(startXCurrentScaled, startXLastScaled)), putYGO(lerp(startYCurrentScaled, startYLastScaled)), putXGO(lerp(endXCurrentScaled, endXLastScaled)), putYGO(lerp(endYCurrentScaled, endYLastScaled)), color, thickness);}
//        /**draws a dotted line, scaled inputs*/
//        public static void fillLineDottedScaled(long startXCurrentScaled, long startXLastScaled, long startYCurrentScaled, long startYLastScaled, long endXCurrentScaled, long endXLastScaled, long endYCurrentScaled, long endYLastScaled, int color, double thickness, double dotLength, double spaceLength){DrawUtil.fillLineDottedScaled(putXGO(lerp(startXCurrentScaled, startXLastScaled)), putYGO(lerp(startYCurrentScaled, startYLastScaled)), putXGO(lerp(endXCurrentScaled, endXLastScaled)), putYGO(lerp(endYCurrentScaled, endYLastScaled)), color, thickness, dotLength, spaceLength);}
//        /**draws filled text, scaled inputs*/
//        public static void fillTextScaled(String text, long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, Fonts font, double size, StringAlignment alignment){DrawUtil.fillTextScaled(text, putXGO(lerp(xCurrentScaled, xLastScaled)), putYGO(lerp(yCurrentScaled, yLastScaled)), font, size, alignment);}
//        /**draws an image, scaled inputs*/
//        public static void fillImageScaled(Image image, long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long widthScaled, long heightScaled){DrawUtil.fillImageScaled(image, putXGO(lerp(xCurrentScaled, xLastScaled)), putYGO(lerp(yCurrentScaled, yLastScaled)), widthScaled, heightScaled);}
//        /**draws a model, scaled inputs*/
//        public static void fillModelScaled(Models modelKey, PlayerColor color, long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long zCurrentScaled, long zLastScaled, long directionCurrentScaled, long directionLastScaled){DrawUtil.fillModelScaled(modelKey, color, putXGO(lerp(xCurrentScaled, xLastScaled)), putYGO(lerp(yCurrentScaled, yLastScaled)), lerp(zCurrentScaled, zLastScaled), lerp(directionCurrentScaled, directionLastScaled));}
//        /**draws a filled rectangle*/
//        public static void fillRect(double xCurrent, double xLast, double yCurrent, double yLast, double width, double height, int color){DrawUtil.fillRect(putXGO(lerp(xCurrent, xLast)), putYGO(lerp(yCurrent, yLast)), width, height, color);}
//        /**draws a rectangle outline*/
//        public static void strokeRect(double xCurrent, double xLast, double yCurrent, double yLast, double width, double height, int color, double strokeWidth){DrawUtil.strokeRect(putXGO(lerp(xCurrent, xLast)), putYGO(lerp(yCurrent, yLast)), width, height, color, strokeWidth);}
//        /**draws a filled rounded rectangle*/
//        public static void fillRoundedRect(double xCurrent, double xLast, double yCurrent, double yLast, double width, double height, double arc, int color){DrawUtil.fillRoundedRect(putXGO(lerp(xCurrent, xLast)), putYGO(lerp(yCurrent, yLast)), width, height, arc, color);}
//        /**draws a rounded rectangle outline*/
//        public static void strokeRoundedRect(double xCurrent, double xLast, double yCurrent, double yLast, double width, double height, double arc, int color, double strokeWidth){DrawUtil.strokeRoundedRect(putXGO(lerp(xCurrent, xLast)), putYGO(lerp(yCurrent, yLast)), width, height, arc, color, strokeWidth);}
//        /**draws a filled circle*/
//        public static void fillCircle(double xCurrent, double xLast, double yCurrent, double yLast, double radius, int color){DrawUtil.fillCircle(putXGO(lerp(xCurrent, xLast)), putYGO(lerp(yCurrent, yLast)), radius, color);}
//        /**draws a circle outline*/
//        public static void strokeCircle(double xCurrent, double xLast, double yCurrent, double yLast, double radius, int color, double strokeWidth){DrawUtil.strokeCircle(putXGO(lerp(xCurrent, xLast)), putYGO(lerp(yCurrent, yLast)), radius, color, strokeWidth);}
//        /**draws a filled oval*/
//        public static void fillOval(double xCurrent, double xLast, double yCurrent, double yLast, double width, double height, int color){DrawUtil.fillOval(putXGO(lerp(xCurrent, xLast)), putYGO(lerp(yCurrent, yLast)), width, height, color);}
//        /**draws an oval outline*/
//        public static void strokeOval(double xCurrent, double xLast, double yCurrent, double yLast, double width, double height, int color, double strokeWidth){DrawUtil.strokeOval(putXGO(lerp(xCurrent, xLast)), putYGO(lerp(yCurrent, yLast)), width, height, color, strokeWidth);}
//        /**draws a line*/
//        public static void fillLine(double startXCurrent, double startXLast, double startYCurrent, double startYLast, double endXCurrent, double endXLast, double endYCurrent, double endYLast, int color, double thickness){DrawUtil.fillLine(putXGO(lerp(startXCurrent, startXLast)), putYGO(lerp(startYCurrent, startYLast)), putXGO(lerp(endXCurrent, endXLast)), putYGO(lerp(endYCurrent, endYLast)), color, thickness);}
//        /**draws a dotted line*/
//        public static void fillLineDotted(double startXCurrent, double startXLast, double startYCurrent, double startYLast, double endXCurrent, double endXLast, double endYCurrent, double endYLast, int color, double thickness, double dotLength, double spaceLength){DrawUtil.fillLineDotted(putXGO(lerp(startXCurrent, startXLast)), putYGO(lerp(startYCurrent, startYLast)), putXGO(lerp(endXCurrent, endXLast)), putYGO(lerp(endYCurrent, endYLast)), color, thickness, dotLength, spaceLength);}
//        /**draws filled text*/
//        public static void fillText(String text, double xCurrent, double xLast, double yCurrent, double yLast, Fonts font, double size, StringAlignment alignment, int color){DrawUtil.fillText(text, putXGO(lerp(xCurrent, xLast)), putYGO(lerp(yCurrent, yLast)), font, size, alignment, color);}
//        /**draws an image*/
//        public static void fillImage(Image image, double xCurrent, double xLast, double yCurrent, double yLast, double width, double height){DrawUtil.fillImage(image, putXGO(lerp(xCurrent, xLast)), putYGO(lerp(yCurrent, yLast)), width, height);}
//        /**draws a model*/
//        public static void fillModel(Models modelKey, PlayerColor color, double xCurrent, double xLast, double yCurrent, double yLast, double zCurrent, double zLast, double directionCurrent, double directionLast){DrawUtil.fillModel(modelKey, color, putXGO(lerp(xCurrent, xLast)), putYGO(lerp(yCurrent, yLast)), lerp(zCurrent, zLast), lerp(directionCurrent, directionLast));}
//
//        /**draws a filled rectangle, scaled inputs, returns false if culled*/
//        public static boolean fillRectScaledCull(long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long widthScaled, long heightScaled, int color){long x = lerp(xCurrentScaled, xLastScaled); long y = lerp(yCurrentScaled, yLastScaled); if(cull(x, y, widthScaled, heightScaled)) return false; DrawUtil.fillRectScaled(putXGO(x), putYGO(y), widthScaled, heightScaled, color); return true;}
//        /**draws a rectangle outline, scaled inputs, returns false if culled*/
//        public static boolean strokeRectScaledCull(long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long widthScaled, long heightScaled, int color, double strokeWidth){long x = lerp(xCurrentScaled, xLastScaled); long y = lerp(yCurrentScaled, yLastScaled); if(cull(x, y, widthScaled, heightScaled)) return false; DrawUtil.strokeRectScaled(putXGO(x), putYGO(y), widthScaled, heightScaled, color, strokeWidth); return true;}
//        /**draws a filled rounded rectangle, scaled inputs, returns false if culled*/
//        public static boolean fillRoundedRectScaledCull(long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long widthScaled, long heightScaled, double arc, int color){long x = lerp(xCurrentScaled, xLastScaled); long y = lerp(yCurrentScaled, yLastScaled); if(cull(x, y, widthScaled, heightScaled)) return false; DrawUtil.fillRoundedRectScaled(putXGO(x), putYGO(y), widthScaled, heightScaled, arc, color); return true;}
//        /**draws a rounded rectangle outline, scaled inputs, returns false if culled*/
//        public static boolean strokeRoundedRectScaledCull(long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long widthScaled, long heightScaled, double arc, int color, double strokeWidth){long x = lerp(xCurrentScaled, xLastScaled); long y = lerp(yCurrentScaled, yLastScaled); if(cull(x, y, widthScaled, heightScaled)) return false; DrawUtil.strokeRoundedRectScaled(putXGO(x), putYGO(y), widthScaled, heightScaled, arc, color, strokeWidth); return true;}
//        /**draws a filled circle, scaled inputs, returns false if culled*/
//        public static boolean fillCircleScaledCull(long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long radiusScaled, int color){long x = lerp(xCurrentScaled, xLastScaled); long y = lerp(yCurrentScaled, yLastScaled); if(cull(x, y, radiusScaled*2, radiusScaled*2)) return false; DrawUtil.fillCircleScaled(putXGO(x), putYGO(y), radiusScaled, color); return true;}
//        /**draws a circle outline, scaled inputs, returns false if culled*/
//        public static boolean strokeCircleScaledCull(long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long radiusScaled, int color, double strokeWidth){long x = lerp(xCurrentScaled, xLastScaled); long y = lerp(yCurrentScaled, yLastScaled); if(cull(x, y, radiusScaled*2, radiusScaled*2)) return false; DrawUtil.strokeCircleScaled(putXGO(x), putYGO(y), radiusScaled, color, strokeWidth); return true;}
//        /**draws a filled oval, scaled inputs, returns false if culled*/
//        public static boolean fillOvalScaledCull(long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long widthScaled, long heightScaled, int color){long x = lerp(xCurrentScaled, xLastScaled); long y = lerp(yCurrentScaled, yLastScaled); if(cull(x, y, widthScaled, heightScaled)) return false; DrawUtil.fillOvalScaled(putXGO(x), putYGO(y), widthScaled, heightScaled, color); return true;}
//        /**draws an oval outline, scaled inputs, returns false if culled*/
//        public static boolean strokeOvalScaledCull(long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long widthScaled, long heightScaled, int color, double strokeWidth){long x = lerp(xCurrentScaled, xLastScaled); long y = lerp(yCurrentScaled, yLastScaled); if(cull(x, y, widthScaled, heightScaled)) return false; DrawUtil.strokeOvalScaled(putXGO(x), putYGO(y), widthScaled, heightScaled, color, strokeWidth); return true;}
//        /**draws a line, scaled inputs, returns false if culled*/
//        public static boolean fillLineScaledCull(long startXCurrentScaled, long startXLastScaled, long startYCurrentScaled, long startYLastScaled, long endXCurrentScaled, long endXLastScaled, long endYCurrentScaled, long endYLastScaled, int color, double thickness){long startX = lerp(startXCurrentScaled, startXLastScaled); long startY = lerp(startYCurrentScaled, startYLastScaled); long endX = lerp(endXCurrentScaled, endXLastScaled); long endY = lerp(endYCurrentScaled, endYLastScaled); if(cull(startX, startY, endX-startX, endY-startX)) return false; DrawUtil.fillLineScaled(putXGO(startX), putYGO(startY), putXGO(endX), putYGO(endY), color, thickness); return true;}
//        /**draws a dotted line, scaled inputs, returns false if culled*/
//        public static boolean fillLineDottedScaledCull(long startXCurrentScaled, long startXLastScaled, long startYCurrentScaled, long startYLastScaled, long endXCurrentScaled, long endXLastScaled, long endYCurrentScaled, long endYLastScaled, int color, double thickness, double dotLength, double spaceLength){long startX = lerp(startXCurrentScaled, startXLastScaled); long startY = lerp(startYCurrentScaled, startYLastScaled); long endX = lerp(endXCurrentScaled, endXLastScaled); long endY = lerp(endYCurrentScaled, endYLastScaled); if(cull(startX, startY, endX-startX, endY-startX)) return false; DrawUtil.fillLineDottedScaled(putXGO(startX), putYGO(startY), putXGO(endX), putYGO(endY), color, thickness, dotLength, spaceLength); return true;}
//        /**draws filled text, scaled inputs, returns false if culled*/
//        public static boolean fillTextScaledCull(String text, long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, Fonts font, double size, StringAlignment alignment){DrawUtil.fillTextScaled(text, putXGO(lerp(xCurrentScaled, xLastScaled)), putYGO(lerp(yCurrentScaled, yLastScaled)), font, size, alignment); return true;}
//        /**draws an image, scaled inputs, returns false if culled*/
//        public static boolean fillImageScaledCull(Image image, long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long widthScaled, long heightScaled){long x = lerp(xCurrentScaled, xLastScaled); long y = lerp(yCurrentScaled, yLastScaled); if(cull(x, y, widthScaled, heightScaled)) return false; DrawUtil.fillImageScaled(image, putXGO(x), putYGO(y), widthScaled, heightScaled); return true;}
//        /**draws a model, scaled inputs, returns false if culled*/
//        public static boolean fillModelScaledCull(Models modelKey, PlayerColor color, long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long zCurrentScaled, long zLastScaled, long directionCurrentScaled, long directionLastScaled){long x = lerp(xCurrentScaled, xLastScaled); long y = lerp(yCurrentScaled, yLastScaled); DrawUtil.fillModelScaled(modelKey, color, putXGO(x), putYGO(y), lerp(zCurrentScaled, zLastScaled), lerp(directionCurrentScaled, directionLastScaled)); return true;}
//        /**draws a filled rectangle, returns false if culled*/
//        public static boolean fillRectCull(double xCurrent, double xLast, double yCurrent, double yLast, double width, double height, int color){double x = lerp(xCurrent, xLast); double y = lerp(yCurrent, yLast); if(cull(x, y, width, height)) return false; DrawUtil.fillRect(putXGO(x), putYGO(y), width, height, color); return true;}
//        /**draws a rectangle outline, returns false if culled*/
//        public static boolean strokeRectCull(double xCurrent, double xLast, double yCurrent, double yLast, double width, double height, int color, double strokeWidth){double x = lerp(xCurrent, xLast); double y = lerp(yCurrent, yLast); if(cull(x, y, width, height)) return false; DrawUtil.strokeRect(putXGO(x), putYGO(y), width, height, color, strokeWidth); return true;}
//        /**draws a filled rounded rectangle, returns false if culled*/
//        public static boolean fillRoundedRectCull(double xCurrent, double xLast, double yCurrent, double yLast, double width, double height, double arc, int color){double x = lerp(xCurrent, xLast); double y = lerp(yCurrent, yLast); if(cull(x, y, width, height)) return false; DrawUtil.fillRoundedRect(putXGO(x), putYGO(y), width, height, arc, color); return true;}
//        /**draws a rounded rectangle outline, returns false if culled*/
//        public static boolean strokeRoundedRectCull(double xCurrent, double xLast, double yCurrent, double yLast, double width, double height, double arc, int color, double strokeWidth){double x = lerp(xCurrent, xLast); double y = lerp(yCurrent, yLast); if(cull(x, y, width, height)) return false; DrawUtil.strokeRoundedRect(putXGO(x), putYGO(y), width, height, arc, color, strokeWidth); return true;}
//        /**draws a filled circle, returns false if culled*/
//        public static boolean fillCircleCull(double xCurrent, double xLast, double yCurrent, double yLast, double radius, int color){double x = lerp(xCurrent, xLast); double y = lerp(yCurrent, yLast); if(cull(x, y, radius*2, radius*2)) return false; DrawUtil.fillCircle(putXGO(x), putYGO(y), radius, color); return true;}
//        /**draws a circle outline, returns false if culled*/
//        public static boolean strokeCircleCull(double xCurrent, double xLast, double yCurrent, double yLast, double radius, int color, double strokeWidth){double x = lerp(xCurrent, xLast); double y = lerp(yCurrent, yLast); if(cull(x, y, radius*2, radius*2)) return false; DrawUtil.strokeCircle(putXGO(x), putYGO(y), radius, color, strokeWidth); return true;}
//        /**draws a filled oval, returns false if culled*/
//        public static boolean fillOvalCull(double xCurrent, double xLast, double yCurrent, double yLast, double width, double height, int color){double x = lerp(xCurrent, xLast); double y = lerp(yCurrent, yLast); if(cull(x, y, width, height)) return false; DrawUtil.fillOval(putXGO(x), putYGO(y), width, height, color); return true;}
//        /**draws an oval outline, returns false if culled*/
//        public static boolean strokeOvalCull(double xCurrent, double xLast, double yCurrent, double yLast, double width, double height, int color, double strokeWidth){double x = lerp(xCurrent, xLast); double y = lerp(yCurrent, yLast); if(cull(x, y, width, height)) return false; DrawUtil.strokeOval(putXGO(x), putYGO(y), width, height, color, strokeWidth); return true;}
//        /**draws a line, returns false if culled*/
//        public static boolean fillLineCull(double startXCurrent, double startXLast, double startYCurrent, double startYLast, double endXCurrent, double endXLast, double endYCurrent, double endYLast, int color, double thickness){double startX = lerp(startXCurrent, startXLast); double startY = lerp(startYCurrent, startYLast); double endX = lerp(endXCurrent, endXLast); double endY = lerp(endYCurrent, endYLast); if(cull(startX, startY, endX-startX, endY-startX)) return false; DrawUtil.fillLine(putXGO(startX), putYGO(startY), putXGO(endX), putYGO(endY), color, thickness); return true;}
//        /**draws a dotted line, returns false if culled*/
//        public static boolean fillLineDottedCull(double startXCurrent, double startXLast, double startYCurrent, double startYLast, double endXCurrent, double endXLast, double endYCurrent, double endYLast, int color, double thickness, double dotLength, double spaceLength){double startX = lerp(startXCurrent, startXLast); double startY = lerp(startYCurrent, startYLast); double endX = lerp(endXCurrent, endXLast); double endY = lerp(endYCurrent, endYLast); if(cull(startX, startY, endX-startX, endY-startX)) return false; DrawUtil.fillLineDotted(putXGO(startX), putYGO(startY), putXGO(endX), putYGO(endY), color, thickness, dotLength, spaceLength); return true;}
//        /**draws filled text, returns false if culled*/
//        public static boolean fillTextCull(String text, double xCurrent, double xLast, double yCurrent, double yLast, Fonts font, double size, StringAlignment alignment, int color){DrawUtil.fillText(text, putXGO(lerp(xCurrent, xLast)), putYGO(lerp(yCurrent, yLast)), font, size, alignment, color); return true;}
//        /**draws an image, returns false if culled*/
//        public static boolean fillImageCull(Image image, double xCurrent, double xLast, double yCurrent, double yLast, double width, double height){double x = lerp(xCurrent, xLast); double y = lerp(yCurrent, yLast); if(cull(x, y, width, height)) return false; DrawUtil.fillImage(image, putXGO(x), putYGO(y), width, height); return true;}
//        /**draws a model, returns false if culled*/
//        public static boolean fillModelCull(Models modelKey, PlayerColor color, double xCurrent, double xLast, double yCurrent, double yLast, double zCurrent, double zLast, double directionCurrent, double directionLast){double x = lerp(xCurrent, xLast); double y = lerp(yCurrent, yLast); DrawUtil.fillModel(modelKey, color, putXGO(x), putYGO(y), lerp(zCurrent, zLast), lerp(directionCurrent, directionLast)); return true;}
//    }
//
//    // ── coloring methods — unchanged from original ────────────────────────────
//    public static void loadColoredModelImages(LoadingScreen loadingScreen, PlayerColor... players) { /* original impl */ }
//    public static void loadModelImages(LoadingScreen loadingScreen, PlayerColor... players)        { /* original impl */ }
//    public static void colorUnits(LoadingScreen loadingScreen, PlayerColor... players)               { /* original impl */ }
//}