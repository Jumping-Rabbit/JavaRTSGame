package com.game.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.game.Fonts;
import com.game.Models;
import com.game.entity.InitInternalIncrements;
import com.game.gameWindow.LoadingScreen;
import space.earlygrey.shapedrawer.ShapeDrawer;

import java.util.EnumMap;

import static com.game.utils.NumUtil.LTF;

@InitInternalIncrements()
public class DrawUtil {
    //TODO: use pixmap
    private static EnumMap<Models, Model> baseModelMap;
//    private static EnumMap<ModelInstance, WritableImage> modelImageMap;



    private enum RenderBatch{
        TWO_D,
        THREE_D,
        NONE
    }
    private static volatile float factor = 0;
    private static int color;
    private static int imageResolution = 64;
    private static float scale = 1f;

    private static PolygonSpriteBatch batch2D;//use for drawing images
    private static ModelBatch batch3D;//use for drawing ModelInstance
    private static ShapeDrawer shapeDrawer;


    private static PerspectiveCamera gameCamera;
    private static OrthographicCamera camera;
    public static Viewport viewport;

    private static RenderBatch renderBatch;

    private static PixmapPacker pixmapPacker;

    private static Texture whitePixelTexture;
    private static TextureRegion whitePixelRegion;

    public static void startRender2D(){
        if (renderBatch == RenderBatch.TWO_D) {
            return;
        } else if (renderBatch == RenderBatch.THREE_D){
            batch3D.end();
        }
        viewport.apply();
        batch2D.setProjectionMatrix(camera.combined);

        batch2D.begin();
        renderBatch = RenderBatch.TWO_D;

    }
    public static void startRender3D(){
        if (renderBatch == RenderBatch.THREE_D){
            return;
        } else if (renderBatch == RenderBatch.TWO_D){
            batch2D.end();
        }
        viewport.apply();

        batch3D.begin(gameCamera);
        renderBatch = RenderBatch.THREE_D;
    }

    public static void stopRender(){
        switch (renderBatch){
            case TWO_D -> batch2D.end();
            case THREE_D -> batch3D.end();
        }
        renderBatch = RenderBatch.NONE;
    }

    public static void updateViewport(int width, int height){
        if (viewport == null) return;
        viewport.update(width, height, true);
        scale = camera.zoom;
        Fonts.clear();
    }

    public static void setImageResolution(int imageResolution) {
        DrawUtil.imageResolution = imageResolution;
    }

    public static void create(){
        batch2D = new PolygonSpriteBatch();
        batch3D = new ModelBatch();

        camera = new OrthographicCamera();
        viewport = new FitViewport(1920, 1080, camera);
        gameCamera = new PerspectiveCamera(60, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        float targetX = 100f;
        float targetZ = 100f;
        float height = 50f;
        float distanceBack = height / (float) Math.tan(Math.toRadians(56));
        gameCamera.position.set(targetX, height, targetZ + distanceBack);
        gameCamera.lookAt(targetX, 0f, targetZ);
        gameCamera.up.set(0, 1, 0);

        //setup shapeDrawer
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        whitePixelTexture = new Texture(pixmap);
        pixmap.dispose();
        whitePixelRegion = new TextureRegion(whitePixelTexture);
        shapeDrawer = new ShapeDrawer(batch2D, whitePixelRegion);

        renderBatch = RenderBatch.NONE;
        pixmapPacker = new PixmapPacker(2048, 2048, Pixmap.Format.RGBA8888, 2, false);//make this dynamic resize
    }


    public static void init(LoadingScreen loadingScreen) {
//        loadBaseImages(loadingScreen);
        ModelLoaderUtil.loadModels(loadingScreen);

    }

    public static void loadBaseModels(LoadingScreen loadingScreen){

    }



//    public static void loadBaseImages(LoadingScreen loadingScreen) {
//        modelMap = ModelLoaderUtil.calculateModelImages(loadingScreen, imageResolution);
//        modelImageMap = ModelLoaderUtil.calculateModelImage(loadingScreen, imageResolution);
//    }
//
//    public static void loadColoredImages(LoadingScreen loadingScreen, PlayerColor... players) {
//        makeHealthModelImages(loadingScreen, players);
//        makeColoredModelImages(loadingScreen, players);
//        makeScaledColoredModelImages(loadingScreen, players);
//    }
//
//
//    public static void makeHealthModelImages(LoadingScreen loadingScreen, PlayerColor... players) {
//        playersColoredModelImageMap = new Object2ObjectOpenHashMap<>();
//        for (PlayerColor player : players) {
//            EnumMap<ModelInstance, WritableImage[]> playerColoredModelImageMap = new EnumMap<>(ModelInstance.class);
//            for (ModelInstance model : ModelInstance.values()) {
//                WritableImage[] coloredImage = new WritableImage[5];
//                WritableImage source = modelImageMap.get(model);
//                PixelReader reader = source.getPixelReader();
//                for (int i = 0; i < 5; i++) {
//                    coloredImage[i] = new WritableImage(reader, (int) source.getWidth(), (int) source.getHeight());
//                    PixelWriter imageWriter = coloredImage[i].getPixelWriter();
//                    for (int x = 0; x < (int) source.getWidth(); x++) {
//                        for (int y = 0; y < (int) source.getHeight(); y++) {
//                            Color color = reader.getColor(x, y);
//                            float newHue = switch (i) {
//                                case 0 -> 120;
//                                case 1 -> 89.88;
//                                case 2 -> 60;
//                                case 3 -> 30.12;
//                                default -> 0;
//                            };
//
//                            Color newColor = Color.hsb(newHue, color.getSaturation(), color.getBrightness(), color.getOpacity());
//                            imageWriter.setColor(x, y, newColor);
//                        }
//                    }
//                    loadingScreen.increment();
//                }
//                playerColoredModelImageMap.put(model, coloredImage);
//            }
//            playersColoredModelImageMap.put(player, playerColoredModelImageMap);
//        }
//    }
//
//    public static void makeScaledColoredModelImages(LoadingScreen loadingScreen, PlayerColor... players) {
//        playersModelImageMap = new Object2ObjectOpenHashMap<>();
//        for (PlayerColor player : players) {
//            EnumMap<ModelInstance, WritableImage[]> playerModelImageMap = new EnumMap<>(ModelInstance.class);
//            for (ModelInstance model : ModelInstance.values()) {
//                WritableImage[] image = new WritableImage[5];
//                WritableImage source = modelImageMap.get(model);
//                PixelReader reader = source.getPixelReader();
//                for (int i = 0; i < 5; i++) {
//                    image[i] = new WritableImage(reader, (int) source.getWidth(), (int) source.getHeight());
//                    PixelWriter imageWriter = image[i].getPixelWriter();
//                    for (int x = 0; x < (int) source.getWidth(); x++) {
//                        for (int y = 0; y < (int) source.getHeight(); y++) {
//                            Color color = reader.getColor(x, y);
//                            Color newColor = Color.hsb(player.getHue(), color.getSaturation(), color.getBrightness(), color.getOpacity());
//                            imageWriter.setColor(x, y, newColor);
//                        }
//                    }
//                    loadingScreen.increment();
//                }
//                playerModelImageMap.put(model, image);
//            }
//            playersModelImageMap.put(player, playerModelImageMap);
//        }
//    }
//
//    public static void makeColoredModelImages(LoadingScreen loadingScreen, PlayerColor... players) {
//        playersModelMap = new Object2ObjectOpenHashMap<>();
//        for (PlayerColor player : players) {
//            System.out.println(player.toString());
//            EnumMap<ModelInstance, WritableImage[]> playerModelMap = new EnumMap<>(ModelInstance.class);
//            for (ModelInstance sourcesKey : modelMap.keySet()) {
//                WritableImage[] newSources = new WritableImage[modelMap.get(sourcesKey).length];
//                int count = 0;
//                for (WritableImage source : modelMap.get(sourcesKey)) {
//                    PixelReader reader = source.getPixelReader();
//                    WritableImage image = new WritableImage(reader, (int) source.getWidth(), (int) source.getHeight());
//                    PixelWriter imageWriter = image.getPixelWriter();
//                    for (int x = 0; x < (int) source.getWidth(); x++) {
//                        for (int y = 0; y < (int) source.getHeight(); y++) {
//                            Color oldColor = reader.getColor(x, y);
//                            Color newColor = Color.hsb(player.getHue(), oldColor.getSaturation(), oldColor.getBrightness(), oldColor.getOpacity());
//                            imageWriter.setColor(x, y, newColor);
//                        }
//                    }
//                    newSources[count] = image;
//                    count++;
//                    loadingScreen.increment();
//                }
//                playerModelMap.put(sourcesKey, newSources);
//            }
//            playersModelMap.put(player, playerModelMap);
//        }
//    }




    public static synchronized float getFactor() {
        return factor;
    }

    public static synchronized void setFactor(float factor) {
        DrawUtil.factor = StrictMath.clamp(factor, 0, 1);
    }

    public static void setColor(int hex) {
        if (color == hex) {
            return;
        }
        Color newColor = Colors.fromHex(hex);
        color = hex;
        shapeDrawer.setColor(newColor);
    }
    

    /**
     * draws a filled rectangle, scaled inputs
     */
    public static void fillRectScaled(long xScaled, long yScaled, long widthScaled, long heightScaled, int color) {
        setColor(color);
        shapeDrawer.filledRectangle(LTF(xScaled), LTF(yScaled), LTF(widthScaled) * scale, LTF(heightScaled) * scale);
    }

    /**
     * draws a rectangle outline, scaled inputs
     */
    public static void strokeRectScaled(long xScaled, long yScaled, long widthScaled, long heightScaled, int color, float strokeWidth) {
        setColor(color);
        shapeDrawer.rectangle(LTF(xScaled), LTF(yScaled), LTF(widthScaled) * scale, LTF(heightScaled) * scale, strokeWidth*scale);
    }

    /**
     * draws a filled rounded rectangle, scaled inputs
     */
    public static void fillRoundedRectScaled(long xScaled, long yScaled, long widthScaled, long heightScaled, float arc, int color) {
//        setColor(color);
//        shapeDrawer.fillRoundRect(LTF(xScaled), LTF(yScaled), LTF(widthScaled) * scale, LTF(heightScaled) * scale, arc * scale, arc * scale);
    }

    /**
     * draws a rounded rectangle outline, scaled inputs
     */
    public static void strokeRoundedRectScaled(long xScaled, long yScaled, long widthScaled, long heightScaled, float arc, int color, float strokeWidth) {
//        setColor(color);
//        shapeDrawer.fillRoundRect(LTF(xScaled), LTF(yScaled), LTF(widthScaled) * scale, LTF(heightScaled) * scale, arc * scale, arc * scale);
    }

    /**
     * draws a filled circle, scaled inputs
     */
    public static void fillCircleScaled(long xScaled, long yScaled, long radiusScaled, int color) {
        setColor(color);
        shapeDrawer.filledCircle(LTF(xScaled), LTF(yScaled), LTF(radiusScaled) * 2 * scale);
    }

    /**
     * draws a circle outline, scaled inputs
     */
    public static void strokeCircleScaled(long xScaled, long yScaled, long radiusScaled, int color, float strokeWidth) {
        setColor(color);
        shapeDrawer.circle(LTF(xScaled), LTF(yScaled), LTF(radiusScaled) * 2 * scale, strokeWidth*scale);
    }

    /**
     * draws a filled oval, scaled inputs
     */
    public static void fillOvalScaled(long xScaled, long yScaled, long widthScaled, long heightScaled, int color) {
        setColor(color);
        shapeDrawer.filledEllipse(LTF(xScaled), LTF(yScaled), LTF(widthScaled) * scale, LTF(heightScaled) * scale);
    }

    /**
     * draws a oval outline, scaled inputs
     */
    public static void strokeOvalScaled(long xScaled, long yScaled, long widthScaled, long heightScaled, int color, float strokeWidth) {
        setColor(color);
        shapeDrawer.ellipse(LTF(xScaled), LTF(yScaled), LTF(widthScaled) * scale, LTF(heightScaled) * scale, 0, strokeWidth*scale);
    }

    /**
     * draws a filled line, scaled inputs
     */
    public static void fillLineScaled(long startXScaled, long startYScaled, long endXScaled, long endYScaled, int color, float thickness) {
        setColor(color);
        shapeDrawer.line(LTF(startXScaled), LTF(startYScaled), LTF(endXScaled), LTF(endYScaled));
    }

    /**
     * draws a filled line, scaled inputs
     */
    public static void fillLineDottedScaled(long startXScaled, long startYScaled, long endXScaled, long endYScaled, int color, float thickness, float dotLength, float spaceLength) {
        setColor(color);

        float startX = LTF(startXScaled);
        float startY = LTF(startYScaled);
        float endX = LTF(endXScaled);
        float endY = LTF(endYScaled);

        float dx = endX - startX;
        float dy = endY - startY;

        float totalLength = (float) Math.sqrt(dx * dx + dy * dy);
        if (totalLength == 0) return;


        float dirX = dx / totalLength;
        float dirY = dy / totalLength;

        float currentLength = 0;

        while (currentLength < totalLength) {
            float endSegmentLength = currentLength + dotLength;

            if (endSegmentLength > totalLength) {
                endSegmentLength = totalLength;
            }

            float segStartX = startX + dirX * currentLength;
            float segStartY = startY + dirY * currentLength;
            float segEndX = startX + dirX * endSegmentLength;
            float segEndY = startY + dirY * endSegmentLength;

            shapeDrawer.line(segStartX, segStartY, segEndX, segEndY, thickness);
            currentLength += dotLength + spaceLength;
        }
    }

    /**
     * draws a filled text, scaled inputs
     */
    public static void fillTextScaled(String text, long xScaled, long yScaled, Fonts font, float size, StringAlignment alignment) {
        int finalSize = Math.round(size * scale);
        BitmapFont gdxFont = font.getFont(finalSize);

        float targetX = LTF(xScaled);
        float targetY = LTF(yScaled);

        GlyphLayout layout = new GlyphLayout(gdxFont, text);
        int vAlign = alignment.getVerticalAlign();

        if (vAlign == Align.center) {
            targetY += (layout.height / 2f);
        } else if (vAlign == Align.bottom) {
            targetY += layout.height;
        }

        gdxFont.draw(batch2D, text, targetX, targetY, 0, alignment.getHorizontalAlign(), false);
    }

    /**
     * draws a image, scaled inputs
     */
    public static void fillImageScaled(Image image, long xScaled, long yScaled, long widthScaled, long heightScaled) {
//        gc.drawImage(image, LTF(xScaled), LTF(yScaled), (LTF(widthScaled) * scale), (LTF(heightScaled) * scale));
    }

    /**
     * draws a model image, scaled inputs
     */
    public static void fillModelImageScaled(ModelInstance model, long xScaled, long yScaled, long directionScaled) {
//        WritableImage model = playersModelMap.get(color).get(model)[(int) (StrictMath.floorMod((int) (LTF(directionScaled) + 11.25), 360) / 22.5)];
//        gc.drawImage(model, LTF(xScaled - 128 + model.getHalfWidth()), LTF(yScaled - 128 + model.getHalfHeight()), 256 * scale, 256 * scale);
    }

    /**
     * draws a model, scaled inputs
     */
    public static void fillModelScaled(ModelInstance model, long xScaled, long yScaled, long zScaled, long directionScaled) {
        model.transform.setToTranslation(LTF(xScaled), LTF(yScaled), LTF(zScaled));
        model.transform.setToRotation(Vector3.Y, LTF(directionScaled));
//        batch3D.render(model);
        // drawImage(model, LTF(xScaled + model.getHalfWidth()) - model.getWidth() / 2, LTF(yScaled + model.getHalfHeight()) - model.getHeight() / 2);
    }


    /**
     * draws a filled rectangle, scaled inputs
     */
    public static void fillRect(float x, float y, float width, float height, int color) {
        setColor(color);
        shapeDrawer.filledRectangle(x, y, width * scale, height * scale);
    }

    /**
     * draws a filled rectangle, scaled inputs
     */
    public static void fillRect(Rectangle rect, int color) {
        setColor(color);
        shapeDrawer.filledRectangle(rect.getX(), rect.getY(), rect.getWidth() * scale, rect.getHeight() * scale);
    }

    /**
     * draws a rectangle outline, scaled inputs
     */
    public static void strokeRect(float x, float y, float width, float height, int color, float strokeWidth) {
        setColor(color);
        shapeDrawer.rectangle(x, y, width * scale, height * scale, strokeWidth*scale);
    }

    /**
     * draws a rectangle outline, scaled inputs
     */
    public static void strokeRect(Rectangle rect, int color, float strokeWidth) {
        setColor(color);
        shapeDrawer.rectangle(rect.getX(), rect.getY(), rect.getWidth() * scale, rect.getHeight() * scale, strokeWidth*scale);
    }

    /**
     * draws a filled rounded rectangle, scaled inputs
     */
    public static void fillRoundedRect(float x, float y, float width, float height, float arc, int color) {
//        setColor(color);
//        shapeDrawer.fillRoundRect(x, y, width * scale, height * scale, arc * scale, arc * scale);
    }

    /**
     * draws a rounded rectangle outline, scaled inputs
     */
    public static void strokeRoundedRect(float x, float y, float width, float height, float arc, int color, float strokeWidth) {
//        setColor(color);
//        shapeDrawer.RoundRect(x, y, width * scale, height * scale, arc * scale, arc * scale);
    }

    /**
     * draws a filled circle, scaled inputs
     */
    public static void fillCircle(float x, float y, float radius, int color) {
        setColor(color);
        shapeDrawer.filledEllipse(x, y, radius * scale, radius * scale);
    }

    /**
     * draws a circle outline, scaled inputs
     */
    public static void strokeCircle(float x, float y, float radius, int color, float strokeWidth) {
        setColor(color);
        shapeDrawer.circle(x, y, radius * 2 * scale, strokeWidth*scale);
    }

    /**
     * draws a filled oval, scaled inputs
     */
    public static void fillOval(float x, float y, float width, float height, int color) {
        setColor(color);
        shapeDrawer.filledEllipse(x, y, width * scale, height * scale);
    }

    /**
     * draws a oval outline, scaled inputs
     */
    public static void strokeOval(float x, float y, float width, float height, int color, float strokeWidth) {
        setColor(color);
        shapeDrawer.ellipse(x, y, width * scale, height * scale, strokeWidth*scale);
    }

    /**
     * draws a filled line, scaled inputs
     */
    public static void fillLine(float startX, float startY, float endX, float endY, int color, float strokeWidth) {
        setColor(color);
        shapeDrawer.line(startX, startY, endX, endY, strokeWidth*scale);
    }

    /**
     * draws a filled line, scaled inputs
     */
    public static void fillLineDotted(float startX, float startY, float endX, float endY, int color, float strokeWidth, float dotLength, float spaceLength) {
        setColor(color);

        float dx = endX - startX;
        float dy = endY - startY;

        float totalLength = (float) Math.sqrt(dx * dx + dy * dy);
        if (totalLength == 0) return;


        float dirX = dx / totalLength;
        float dirY = dy / totalLength;

        float currentLength = 0;

        while (currentLength < totalLength) {
            float endSegmentLength = currentLength + dotLength;

            if (endSegmentLength > totalLength) {
                endSegmentLength = totalLength;
            }

            float segStartX = startX + dirX * currentLength;
            float segStartY = startY + dirY * currentLength;
            float segEndX = startX + dirX * endSegmentLength;
            float segEndY = startY + dirY * endSegmentLength;

            shapeDrawer.line(segStartX, segStartY, segEndX, segEndY, strokeWidth*scale);
            currentLength += dotLength + spaceLength;
        }
    }

    /**
     * draws a filled text
     */
    public static void fillText(String text, float x, float y, Fonts font, float size, StringAlignment alignment, int color) {
        setColor(color);
        int finalSize = Math.round(size * scale);
        BitmapFont gdxFont = font.getFont(finalSize);


        GlyphLayout layout = new GlyphLayout(gdxFont, text);
        int vAlign = alignment.getVerticalAlign();

        if (vAlign == Align.center) {
            y += (layout.height / 2f);
        } else if (vAlign == Align.bottom) {
            y += layout.height;
        }

        gdxFont.draw(batch2D, text, x, y, 0, alignment.getHorizontalAlign(), false);
    }

    /**
     * draws a image
     */
    public static void fillImage(Image image, float x, float y, float width, float height) {
//        gc.drawImage(image, x, y, (width * scale), (height * scale));
    }

    /**
     * draws a model
     */
    public static void fillModel(ModelInstance model, float x, float y, float z, float direction) {
        model.transform.setToTranslation(x, y, z);
        model.transform.setToRotation(Vector3.Y, direction);
        batch3D.render(model);
    }


    /**
     * shorthand for linear iterpolate, scaled inputs
     */
    private static long lerp(long current, long last) {
        return NumUtil.interpolate(current, last, factor);
    }

    /**
     * shorthand for linear iterpolate
     */
    private static float lerp(float current, float last) {
        return NumUtil.interpolate(current, last, factor);
    }

    public static class Lerp {
        /**
         * draws a filled rectangle, scaled inputs
         */
        public static void fillRectScaled(long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long widthScaled, long heightScaled, int color) {
            DrawUtil.fillRectScaled(lerp(xCurrentScaled, xLastScaled), lerp(yCurrentScaled, yLastScaled), widthScaled, heightScaled, color);
        }

        /**
         * draws a rectangle outline, scaled inputs
         */
        public static void strokeRectScaled(long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long widthScaled, long heightScaled, int color, float strokeWidth) {
            DrawUtil.strokeRectScaled(lerp(xCurrentScaled, xLastScaled), lerp(yCurrentScaled, yLastScaled), widthScaled, heightScaled, color, strokeWidth*scale);
        }

        /**
         * draws a filled rounded rectangle, scaled inputs
         */
        public static void fillRoundedRectScaled(long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long widthScaled, long heightScaled, float arc, int color) {
            DrawUtil.fillRoundedRectScaled(lerp(xCurrentScaled, xLastScaled), lerp(yCurrentScaled, yLastScaled), widthScaled, heightScaled, arc, color);
        }

        /**
         * draws a rounded rectangle outline, scaled inputs
         */
        public static void strokeRoundedRectScaled(long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long widthScaled, long heightScaled, float arc, int color, float strokeWidth) {
            DrawUtil.strokeRoundedRectScaled(lerp(xCurrentScaled, xLastScaled), lerp(yCurrentScaled, yLastScaled), widthScaled, heightScaled, arc, color, strokeWidth*scale);
        }

        /**
         * draws a filled circle, scaled inputs
         */
        public static void fillCircleScaled(long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long radiusScaled, int color) {
            DrawUtil.fillCircleScaled(lerp(xCurrentScaled, xLastScaled), lerp(yCurrentScaled, yLastScaled), radiusScaled, color);
        }

        /**
         * draws a circle outline, scaled inputs
         */
        public static void strokeCircleScaled(long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long radiusScaled, int color, float strokeWidth) {
            DrawUtil.strokeCircleScaled(lerp(xCurrentScaled, xLastScaled), lerp(yCurrentScaled, yLastScaled), radiusScaled, color, strokeWidth*scale);
        }

        /**
         * draws a filled oval, scaled inputs
         */
        public static void fillOvalScaled(long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long widthScaled, long heightScaled, int color) {
            DrawUtil.fillOvalScaled(lerp(xCurrentScaled, xLastScaled), lerp(yCurrentScaled, yLastScaled), widthScaled, heightScaled, color);
        }

        /**
         * draws a oval outline, scaled inputs
         */
        public static void strokeOvalScaled(long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long widthScaled, long heightScaled, int color, float strokeWidth) {
            DrawUtil.strokeOvalScaled(lerp(xCurrentScaled, xLastScaled), lerp(yCurrentScaled, yLastScaled), widthScaled, heightScaled, color, strokeWidth*scale);
        }

        /**
         * draws a filled line, scaled inputs
         */
        public static void fillLineScaled(long startXCurrentScaled, long startXLastScaled, long startYCurrentScaled, long startYLastScaled, long endXCurrentScaled, long endXLastScaled, long endYCurrentScaled, long endYLastScaled, int color, float thickness) {
            DrawUtil.fillLineScaled(lerp(startXCurrentScaled, startXLastScaled), lerp(startYCurrentScaled, startYLastScaled), lerp(endXCurrentScaled, endXLastScaled), lerp(endYCurrentScaled, endYLastScaled), color, thickness);
        }

        /**
         * draws a filled line, scaled inputs
         */
        public static void fillLineDottedScaled(long startXCurrentScaled, long startXLastScaled, long startYCurrentScaled, long startYLastScaled, long endXCurrentScaled, long endXLastScaled, long endYCurrentScaled, long endYLastScaled, int color, float thickness, float dotLength, float spaceLength) {
            DrawUtil.fillLineDottedScaled(lerp(startXCurrentScaled, startXLastScaled), lerp(startYCurrentScaled, startYLastScaled), lerp(endXCurrentScaled, endXLastScaled), lerp(endYCurrentScaled, endYLastScaled), color, thickness, dotLength, spaceLength);
        }

        /**
         * draws a filled text, scaled inputs
         */
        public static void fillTextScaled(String text, long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, Fonts font, float size, StringAlignment alignment) {
            DrawUtil.fillTextScaled(text, lerp(xCurrentScaled, xLastScaled), lerp(yCurrentScaled, yLastScaled), font, size, alignment);
        }

        /**
         * draws a image, scaled inputs
         */
        public static void fillImageScaled(Image image, long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long width, long height) {
            DrawUtil.fillImageScaled(image, lerp(xCurrentScaled, xLastScaled), lerp(yCurrentScaled, yLastScaled), width, height);
        }

        /**
         * draws a model, scaled inputs
         */
        public static void fillModelScaled(ModelInstance model, long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long zCurrentScaled, long zLastScaled, long directionCurrentScaled, long directionLastScaled) {
            DrawUtil.fillModelScaled(model, lerp(xCurrentScaled, xLastScaled), lerp(yCurrentScaled, yLastScaled), lerp(zCurrentScaled, zLastScaled), lerp(directionCurrentScaled, directionLastScaled));
        }


        /**
         * draws a filled rectangle
         */
        public static void fillRect(float xCurrent, float xLast, float yCurrent, float yLast, float width, float height, int color) {
            DrawUtil.fillRect(lerp(xCurrent, xLast), lerp(yCurrent, yLast), width, height, color);
        }

        /**
         * draws a rectangle outline
         */
        public static void strokeRect(float xCurrent, float xLast, float yCurrent, float yLast, float width, float height, int color, float strokeWidth) {
            DrawUtil.strokeRect(lerp(xCurrent, xLast), lerp(yCurrent, yLast), width, height, color, strokeWidth*scale);
        }

        /**
         * draws a filled rounded rectangle
         */
        public static void fillRoundedRect(float xCurrent, float xLast, float yCurrent, float yLast, float width, float height, float arc, int color) {
            DrawUtil.fillRoundedRect(lerp(xCurrent, xLast), lerp(yCurrent, yLast), width, height, arc, color);
        }

        /**
         * draws a rounded rectangle outline
         */
        public static void strokeRoundedRect(float xCurrent, float xLast, float yCurrent, float yLast, float width, float height, float arc, int color, float strokeWidth) {
            DrawUtil.strokeRoundedRect(lerp(xCurrent, xLast), lerp(yCurrent, yLast), width, height, arc, color, strokeWidth*scale);
        }

        /**
         * draws a filled circle
         */
        public static void fillCircle(float xCurrent, float xLast, float yCurrent, float yLast, float radius, int color) {
            DrawUtil.fillCircle(lerp(xCurrent, xLast), lerp(yCurrent, yLast), radius, color);
        }

        /**
         * draws a circle outline
         */
        public static void strokeCircle(float xCurrent, float xLast, float yCurrent, float yLast, float radius, int color, float strokeWidth) {
            DrawUtil.strokeCircle(lerp(xCurrent, xLast), lerp(yCurrent, yLast), radius, color, strokeWidth*scale);
        }

        /**
         * draws a filled oval
         */
        public static void fillOval(float xCurrent, float xLast, float yCurrent, float yLast, float width, float height, int color) {
            DrawUtil.fillOval(lerp(xCurrent, xLast), lerp(yCurrent, yLast), width, height, color);
        }

        /**
         * draws a oval outline
         */
        public static void strokeOval(float xCurrent, float xLast, float yCurrent, float yLast, float width, float height, int color, float strokeWidth) {
            DrawUtil.strokeOval(lerp(xCurrent, xLast), lerp(yCurrent, yLast), width, height, color, strokeWidth*scale);
        }

        /**
         * draws a filled line
         */
        public static void fillLine(float startXCurrent, float startXLast, float startYCurrent, float startYLast, float endXCurrent, float endXLast, float endYCurrent, float endYLast, int color, float thickness) {
            DrawUtil.fillLine(lerp(startXCurrent, startXLast), lerp(startYCurrent, startYLast), lerp(endXCurrent, endXLast), lerp(endYCurrent, endYLast), color, thickness);
        }

        /**
         * draws a filled line
         */
        public static void fillLineDotted(float startXCurrent, float startXLast, float startYCurrent, float startYLast, float endXCurrent, float endXLast, float endYCurrent, float endYLast, int color, float thickness, float dotLength, float spaceLength) {
            DrawUtil.fillLineDotted(lerp(startXCurrent, startXLast), lerp(startYCurrent, startYLast), lerp(endXCurrent, endXLast), lerp(endYCurrent, endYLast), color, thickness, dotLength, spaceLength);
        }

        /**
         * draws a filled text
         */
        public static void fillText(String text, float xCurrent, float xLast, float yCurrent, float yLast, Fonts font, float size, StringAlignment alignment, int color) {
            DrawUtil.fillText(text, lerp(xCurrent, xLast), lerp(yCurrent, yLast), font, size, alignment, color);
        }

        /**
         * draws a image
         */
        public static void fillImage(Image image, float xCurrent, float xLast, float yCurrent, float yLast, float width, float height) {
            DrawUtil.fillImage(image, lerp(xCurrent, xLast), lerp(yCurrent, yLast), width, height);
        }

        /**
         * draws a model
         */
        public static void fillModel(ModelInstance model, float xCurrent, float xLast, float yCurrent, float yLast, float zCurrent, float zLast, float directionCurrent, float directionLast) {
            DrawUtil.fillModel(model, lerp(xCurrent, xLast), lerp(yCurrent, yLast), lerp(zCurrent, zLast), lerp(directionCurrent, directionLast));
        }
    }

    public static class Game {
        /**
         * returns true if needs to be culled
         */
        private static boolean cull(long x, long y, long width, long height) {
//            return CollisionUtil.RectangleRectCollision(x, y, width, height, );//use scaled gameViewport coords, return true if needs to be culled
            return true;
        }

        /**
         * returns true if needs to be culled
         */
        private static boolean cull(float x, float y, float width, float height) {
            return true;
        }

        /**
         * draws a filled rectangle, scaled inputs
         */
        public static void fillRectScaled(long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long widthScaled, long heightScaled, int color) {
            DrawUtil.fillRectScaled(lerp(xCurrentScaled, xLastScaled), lerp(yCurrentScaled, yLastScaled), widthScaled, heightScaled, color);
        }

        /**
         * draws a rectangle outline, scaled inputs
         */
        public static void strokeRectScaled(long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long widthScaled, long heightScaled, int color, float strokeWidth) {
            DrawUtil.strokeRectScaled(lerp(xCurrentScaled, xLastScaled), lerp(yCurrentScaled, yLastScaled), widthScaled, heightScaled, color, strokeWidth*scale);
        }

        /**
         * draws a filled rounded rectangle, scaled inputs
         */
        public static void fillRoundedRectScaled(long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long widthScaled, long heightScaled, float arc, int color) {
            DrawUtil.fillRoundedRectScaled(lerp(xCurrentScaled, xLastScaled), lerp(yCurrentScaled, yLastScaled), widthScaled, heightScaled, arc, color);
        }

        /**
         * draws a rounded rectangle outline, scaled inputs
         */
        public static void strokeRoundedRectScaled(long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long widthScaled, long heightScaled, float arc, int color, float strokeWidth) {
            DrawUtil.strokeRoundedRectScaled(lerp(xCurrentScaled, xLastScaled), lerp(yCurrentScaled, yLastScaled), widthScaled, heightScaled, arc, color, strokeWidth*scale);
        }

        /**
         * draws a filled circle, scaled inputs
         */
        public static void fillCircleScaled(long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long radiusScaled, int color) {
            DrawUtil.fillCircleScaled(lerp(xCurrentScaled, xLastScaled), lerp(yCurrentScaled, yLastScaled), radiusScaled, color);
        }

        /**
         * draws a circle outline, scaled inputs
         */
        public static void strokeCircleScaled(long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long radiusScaled, int color, float strokeWidth) {
            DrawUtil.strokeCircleScaled(lerp(xCurrentScaled, xLastScaled), lerp(yCurrentScaled, yLastScaled), radiusScaled, color, strokeWidth*scale);
        }

        /**
         * draws a filled oval, scaled inputs
         */
        public static void fillOvalScaled(long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long widthScaled, long heightScaled, int color) {
            DrawUtil.fillOvalScaled(lerp(xCurrentScaled, xLastScaled), lerp(yCurrentScaled, yLastScaled), widthScaled, heightScaled, color);
        }

        /**
         * draws a oval outline, scaled inputs
         */
        public static void strokeOvalScaled(long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long widthScaled, long heightScaled, int color, float strokeWidth) {
            DrawUtil.strokeOvalScaled(lerp(xCurrentScaled, xLastScaled), lerp(yCurrentScaled, yLastScaled), widthScaled, heightScaled, color, strokeWidth*scale);
        }

        /**
         * draws a filled line, scaled inputs
         */
        public static void fillLineScaled(long startXCurrentScaled, long startXLastScaled, long startYCurrentScaled, long startYLastScaled, long endXCurrentScaled, long endXLastScaled, long endYCurrentScaled, long endYLastScaled, int color, float thickness) {
            DrawUtil.fillLineScaled(lerp(startXCurrentScaled, startXLastScaled), lerp(startYCurrentScaled, startYLastScaled), lerp(endXCurrentScaled, endXLastScaled), lerp(endYCurrentScaled, endYLastScaled), color, thickness);
        }

        /**
         * draws a filled line, scaled inputs
         */
        public static void fillLineDottedScaled(long startXCurrentScaled, long startXLastScaled, long startYCurrentScaled, long startYLastScaled, long endXCurrentScaled, long endXLastScaled, long endYCurrentScaled, long endYLastScaled, int color, float thickness, float dotLength, float spaceLength) {
            DrawUtil.fillLineDottedScaled(lerp(startXCurrentScaled, startXLastScaled), lerp(startYCurrentScaled, startYLastScaled), lerp(endXCurrentScaled, endXLastScaled), lerp(endYCurrentScaled, endYLastScaled), color, thickness, dotLength, spaceLength);
        }

        /**
         * draws a filled text, scaled inputs
         */
        public static void fillTextScaled(String text, long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, Fonts font, float size, StringAlignment alignment) {
            DrawUtil.fillTextScaled(text, lerp(xCurrentScaled, xLastScaled), lerp(yCurrentScaled, yLastScaled), font, size, alignment);
        }

        /**
         * draws a image, scaled inputs
         */
        public static void fillImageScaled(Image image, long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long width, long height) {
            DrawUtil.fillImageScaled(image, lerp(xCurrentScaled, xLastScaled), lerp(yCurrentScaled, yLastScaled), width, height);
        }

        /**
         * draws a model, scaled inputs
         */
        public static void fillModelScaled(ModelInstance model, long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long zCurrentScaled, long zLastScaled, long directionCurrentScaled, long directionLastScaled) {
            DrawUtil.fillModelScaled(model, lerp(xCurrentScaled, xLastScaled), lerp(yCurrentScaled, yLastScaled), lerp(zCurrentScaled, zLastScaled), lerp(directionCurrentScaled, directionLastScaled));
        }


        /**
         * draws a filled rectangle
         */
        public static void fillRect(float xCurrent, float xLast, float yCurrent, float yLast, float width, float height, int color) {
            DrawUtil.fillRect(lerp(xCurrent, xLast), lerp(yCurrent, yLast), width, height, color);
        }

        /**
         * draws a rectangle outline
         */
        public static void strokeRect(float xCurrent, float xLast, float yCurrent, float yLast, float width, float height, int color, float strokeWidth) {
            DrawUtil.strokeRect(lerp(xCurrent, xLast), lerp(yCurrent, yLast), width, height, color, strokeWidth*scale);
        }

        /**
         * draws a filled rounded rectangle
         */
        public static void fillRoundedRect(float xCurrent, float xLast, float yCurrent, float yLast, float width, float height, float arc, int color) {
            DrawUtil.fillRoundedRect(lerp(xCurrent, xLast), lerp(yCurrent, yLast), width, height, arc, color);
        }

        /**
         * draws a rounded rectangle outline
         */
        public static void strokeRoundedRect(float xCurrent, float xLast, float yCurrent, float yLast, float width, float height, float arc, int color, float strokeWidth) {
            DrawUtil.strokeRoundedRect(lerp(xCurrent, xLast), lerp(yCurrent, yLast), width, height, arc, color, strokeWidth*scale);
        }

        /**
         * draws a filled circle
         */
        public static void fillCircle(float xCurrent, float xLast, float yCurrent, float yLast, float radius, int color) {
            DrawUtil.fillCircle(lerp(xCurrent, xLast), lerp(yCurrent, yLast), radius, color);
        }

        /**
         * draws a circle outline
         */
        public static void strokeCircle(float xCurrent, float xLast, float yCurrent, float yLast, float radius, int color, float strokeWidth) {
            DrawUtil.strokeCircle(lerp(xCurrent, xLast), lerp(yCurrent, yLast), radius, color, strokeWidth*scale);
        }

        /**
         * draws a filled oval
         */
        public static void fillOval(float xCurrent, float xLast, float yCurrent, float yLast, float width, float height, int color) {
            DrawUtil.fillOval(lerp(xCurrent, xLast), lerp(yCurrent, yLast), width, height, color);
        }

        /**
         * draws a oval outline
         */
        public static void strokeOval(float xCurrent, float xLast, float yCurrent, float yLast, float width, float height, int color, float strokeWidth) {
            DrawUtil.strokeOval(lerp(xCurrent, xLast), lerp(yCurrent, yLast), width, height, color, strokeWidth*scale);
        }

        /**
         * draws a filled line
         */
        public static void fillLine(float startXCurrent, float startXLast, float startYCurrent, float startYLast, float endXCurrent, float endXLast, float endYCurrent, float endYLast, int color, float thickness) {
            DrawUtil.fillLine(lerp(startXCurrent, startXLast), lerp(startYCurrent, startYLast), lerp(endXCurrent, endXLast), lerp(endYCurrent, endYLast), color, thickness);
        }

        /**
         * draws a filled line
         */
        public static void fillLineDotted(float startXCurrent, float startXLast, float startYCurrent, float startYLast, float endXCurrent, float endXLast, float endYCurrent, float endYLast, int color, float thickness, float dotLength, float spaceLength) {
            DrawUtil.fillLineDotted(lerp(startXCurrent, startXLast), lerp(startYCurrent, startYLast), lerp(endXCurrent, endXLast), lerp(endYCurrent, endYLast), color, thickness, dotLength, spaceLength);
        }

        /**
         * draws a filled text
         */
        public static void fillText(String text, float xCurrent, float xLast, float yCurrent, float yLast, Fonts font, float size, StringAlignment alignment, int color) {
            DrawUtil.fillText(text, lerp(xCurrent, xLast), lerp(yCurrent, yLast), font, size, alignment, color);
        }

        /**
         * draws a image
         */
        public static void fillImage(Image image, float xCurrent, float xLast, float yCurrent, float yLast, float width, float height) {
            DrawUtil.fillImage(image, lerp(xCurrent, xLast), lerp(yCurrent, yLast), width, height);
        }

        /**
         * draws a model
         */
        public static void fillModel(ModelInstance model, float xCurrent, float xLast, float yCurrent, float yLast, float zCurrent, float zLast, float directionCurrent, float directionLast) {
            DrawUtil.fillModel(model, lerp(xCurrent, xLast), lerp(yCurrent, yLast), lerp(zCurrent, zLast), lerp(directionCurrent, directionLast));
        }


        /**
         * draws a filled rectangle, scaled inputs, returns false if culled
         */
        public static boolean fillRectScaledCull(long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long widthScaled, long heightScaled, int color) {
            long x = lerp(xCurrentScaled, xLastScaled);
            long y = lerp(yCurrentScaled, yLastScaled);
            if (cull(x, y, widthScaled, heightScaled)) return false;
            DrawUtil.fillRectScaled(x, y, widthScaled, heightScaled, color);
            return true;
        }

        /**
         * draws a rectangle outline, scaled inputs, returns false if culled
         */
        public static boolean strokeRectScaledCull(long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long widthScaled, long heightScaled, int color, float strokeWidth) {
            long x = lerp(xCurrentScaled, xLastScaled);
            long y = lerp(yCurrentScaled, yLastScaled);
            if (cull(x, y, widthScaled, heightScaled)) return false;
            DrawUtil.strokeRectScaled(x, y, widthScaled, heightScaled, color, strokeWidth*scale);
            return true;
        }

        /**
         * draws a filled rounded rectangle, scaled inputs, returns false if culled
         */
        public static boolean fillRoundedRectScaledCull(long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long widthScaled, long heightScaled, float arc, int color) {
            long x = lerp(xCurrentScaled, xLastScaled);
            long y = lerp(yCurrentScaled, yLastScaled);
            if (cull(x, y, widthScaled, heightScaled)) return false;
            DrawUtil.fillRoundedRectScaled(x, y, widthScaled, heightScaled, arc, color);
            return true;
        }

        /**
         * draws a rounded rectangle outline, scaled inputs, returns false if culled
         */
        public static boolean strokeRoundedRectScaledCull(long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long widthScaled, long heightScaled, float arc, int color, float strokeWidth) {
            long x = lerp(xCurrentScaled, xLastScaled);
            long y = lerp(yCurrentScaled, yLastScaled);
            if (cull(x, y, widthScaled, heightScaled)) return false;
            DrawUtil.strokeRoundedRectScaled(x, y, widthScaled, heightScaled, arc, color, strokeWidth*scale);
            return true;
        }

        /**
         * draws a filled circle, scaled inputs, returns false if culled
         */
        public static boolean fillCircleScaledCull(long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long radiusScaled, int color) {
            long x = lerp(xCurrentScaled, xLastScaled);
            long y = lerp(yCurrentScaled, yLastScaled);
            if (cull(x, y, radiusScaled * 2, radiusScaled * 2)) return false;
            DrawUtil.fillCircleScaled(x, y, radiusScaled, color);
            return true;
        }

        /**
         * draws a circle outline, scaled inputs, returns false if culled
         */
        public static boolean strokeCircleScaledCull(long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long radiusScaled, int color, float strokeWidth) {
            long x = lerp(xCurrentScaled, xLastScaled);
            long y = lerp(yCurrentScaled, yLastScaled);
            if (cull(x, y, radiusScaled * 2, radiusScaled * 2)) return false;
            DrawUtil.strokeCircleScaled(x, y, radiusScaled, color, strokeWidth*scale);
            return true;
        }

        /**
         * draws a filled oval, scaled inputs, returns false if culled
         */
        public static boolean fillOvalScaledCull(long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long widthScaled, long heightScaled, int color) {
            long x = lerp(xCurrentScaled, xLastScaled);
            long y = lerp(yCurrentScaled, yLastScaled);
            if (cull(x, y, widthScaled, heightScaled)) return false;
            DrawUtil.fillOvalScaled(x, y, widthScaled, heightScaled, color);
            return true;
        }

        /**
         * draws a oval outline, scaled inputs, returns false if culled
         */
        public static boolean strokeOvalScaledCull(long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long widthScaled, long heightScaled, int color, float strokeWidth) {
            long x = lerp(xCurrentScaled, xLastScaled);
            long y = lerp(yCurrentScaled, yLastScaled);
            if (cull(x, y, widthScaled, heightScaled)) return false;
            DrawUtil.strokeOvalScaled(x, y, widthScaled, heightScaled, color, strokeWidth*scale);
            return true;
        }

        /**
         * draws a filled line, scaled inputs, returns false if culled
         */
        public static boolean fillLineScaledCull(long startXCurrentScaled, long startXLastScaled, long startYCurrentScaled, long startYLastScaled, long endXCurrentScaled, long endXLastScaled, long endYCurrentScaled, long endYLastScaled, int color, float thickness) {
            long startX = lerp(startXCurrentScaled, startXLastScaled);
            long startY = lerp(startYCurrentScaled, startYLastScaled);
            long endX = lerp(endXCurrentScaled, endXLastScaled);
            long endY = lerp(endYCurrentScaled, endYLastScaled);
            if (cull(startX, startY, endX - startX, endY - startX)) return false;
            DrawUtil.fillLineScaled(startX, startY, endX, endY, color, thickness);
            return true;
        }

        /**
         * draws a filled line, scaled inputs, returns false if culled
         */
        public static boolean fillLineDottedScaledCull(long startXCurrentScaled, long startXLastScaled, long startYCurrentScaled, long startYLastScaled, long endXCurrentScaled, long endXLastScaled, long endYCurrentScaled, long endYLastScaled, int color, float thickness, float dotLength, float spaceLength) {
            long startX = lerp(startXCurrentScaled, startXLastScaled);
            long startY = lerp(startYCurrentScaled, startYLastScaled);
            long endX = lerp(endXCurrentScaled, endXLastScaled);
            long endY = lerp(endYCurrentScaled, endYLastScaled);
            if (cull(startX, startY, endX - startX, endY - startX)) return false;
            DrawUtil.fillLineDottedScaled(startX, startY, endX, endY, color, thickness, dotLength, spaceLength);
            return true;
        }

        /**
         * draws a filled text, scaled inputs, returns false if culled
         */
        public static boolean fillTextScaledCull(String text, long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, Fonts font, float size, StringAlignment alignment) {
            long x = lerp(xCurrentScaled, xLastScaled);
            long y = lerp(yCurrentScaled, yLastScaled);
//            if (cull(x, y, widthScaled, heightScaled)) return;//TODO:fix this
            DrawUtil.fillTextScaled(text, x, y, font, size, alignment);
            return true;
        }

        /**
         * draws a image, scaled inputs, returns false if culled
         */
        public static boolean fillImageScaledCull(Image image, long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long widthScaled, long heightScaled) {
            long x = lerp(xCurrentScaled, xLastScaled);
            long y = lerp(yCurrentScaled, yLastScaled);
            if (cull(x, y, widthScaled, heightScaled)) return false;
            DrawUtil.fillImageScaled(image, x, y, widthScaled, heightScaled);
            return true;
        }

        /**
         * draws a model, scaled inputs, returns false if culled
         */
        public static boolean fillModelScaledCull(ModelInstance model, long xCurrentScaled, long xLastScaled, long yCurrentScaled, long yLastScaled, long zCurrentScaled, long zLastScaled, long directionCurrentScaled, long directionLastScaled) {
            long x = lerp(xCurrentScaled, xLastScaled);
            long y = lerp(yCurrentScaled, yLastScaled);//do something with z here
//            if (cull(x, y, widthScaled, heightScaled)) return;//TODO:fix this
            DrawUtil.fillModelScaled(model, x, y, lerp(zCurrentScaled, zLastScaled), lerp(directionCurrentScaled, directionLastScaled));
            return true;
        }


        /**
         * draws a filled rectangle, returns false if culled
         */
        public static boolean fillRectCull(float xCurrent, float xLast, float yCurrent, float yLast, float width, float height, int color) {
            float x = lerp(xCurrent, xLast);
            float y = lerp(yCurrent, yLast);
            if (cull(x, y, width, height)) return false;
            DrawUtil.fillRect(x, y, width, height, color);
            return true;
        }

        /**
         * draws a rectangle outline, returns false if culled
         */
        public static boolean strokeRectCull(float xCurrent, float xLast, float yCurrent, float yLast, float width, float height, int color, float strokeWidth) {
            float x = lerp(xCurrent, xLast);
            float y = lerp(yCurrent, yLast);
            if (cull(x, y, width, height)) return false;
            DrawUtil.strokeRect(x, y, width, height, color, strokeWidth*scale);
            return true;
        }

        /**
         * draws a filled rounded rectangle, returns false if culled
         */
        public static boolean fillRoundedRectCull(float xCurrent, float xLast, float yCurrent, float yLast, float width, float height, float arc, int color) {
            float x = lerp(xCurrent, xLast);
            float y = lerp(yCurrent, yLast);
            if (cull(x, y, width, height)) return false;
            DrawUtil.fillRoundedRect(x, y, width, height, arc, color);
            return true;
        }

        /**
         * draws a rounded rectangle outline, returns false if culled
         */
        public static boolean strokeRoundedRectCull(float xCurrent, float xLast, float yCurrent, float yLast, float width, float height, float arc, int color, float strokeWidth) {
            float x = lerp(xCurrent, xLast);
            float y = lerp(yCurrent, yLast);
            if (cull(x, y, width, height)) return false;
            DrawUtil.strokeRoundedRect(x, y, width, height, arc, color, strokeWidth*scale);
            return true;
        }

        /**
         * draws a filled circle, returns false if culled
         */
        public static boolean fillCircleCull(float xCurrent, float xLast, float yCurrent, float yLast, float radius, int color) {
            float x = lerp(xCurrent, xLast);
            float y = lerp(yCurrent, yLast);
            if (cull(x, y, radius * 2, radius * 2)) return false;
            DrawUtil.fillCircle(x, y, radius, color);
            return true;
        }

        /**
         * draws a circle outline, returns false if culled
         */
        public static boolean strokeCircleCull(float xCurrent, float xLast, float yCurrent, float yLast, float radius, int color, float strokeWidth) {
            float x = lerp(xCurrent, xLast);
            float y = lerp(yCurrent, yLast);
            if (cull(x, y, radius * 2, radius * 2)) return false;
            DrawUtil.strokeCircle(x, y, radius, color, strokeWidth*scale);
            return true;
        }

        /**
         * draws a filled oval, returns false if culled
         */
        public static boolean fillOvalCull(float xCurrent, float xLast, float yCurrent, float yLast, float width, float height, int color) {
            float x = lerp(xCurrent, xLast);
            float y = lerp(yCurrent, yLast);
            if (cull(x, y, width, height)) return false;
            DrawUtil.fillOval(x, y, width, height, color);
            return true;
        }

        /**
         * draws a oval outline, returns false if culled
         */
        public static boolean strokeOvalCull(float xCurrent, float xLast, float yCurrent, float yLast, float width, float height, int color, float strokeWidth) {
            float x = lerp(xCurrent, xLast);
            float y = lerp(yCurrent, yLast);
            if (cull(x, y, width, height)) return false;
            DrawUtil.strokeOval(x, y, width, height, color, strokeWidth*scale);
            return true;
        }

        /**
         * draws a filled line, returns false if culled
         */
        public static boolean fillLineCull(float startXCurrent, float startXLast, float startYCurrent, float startYLast, float endXCurrent, float endXLast, float endYCurrent, float endYLast, int color, float thickness) {
            float startX = lerp(startXCurrent, startXLast);
            float startY = lerp(startYCurrent, startYLast);
            float endX = lerp(endXCurrent, endXLast);
            float endY = lerp(endYCurrent, endYLast);
            if (cull(startX, startY, endX - startX, endY - startX)) return false;
            DrawUtil.fillLine(startX, startY, endX, endY, color, thickness);
            return true;
        }

        /**
         * draws a filled line, returns false if culled
         */
        public static boolean fillLineDottedCull(float startXCurrent, float startXLast, float startYCurrent, float startYLast, float endXCurrent, float endXLast, float endYCurrent, float endYLast, int color, float thickness, float dotLength, float spaceLength) {
            float startX = lerp(startXCurrent, startXLast);
            float startY = lerp(startYCurrent, startYLast);
            float endX = lerp(endXCurrent, endXLast);
            float endY = lerp(endYCurrent, endYLast);
            if (cull(startX, startY, endX - startX, endY - startX)) return false;
            DrawUtil.fillLineDotted(startX, startY, endX, endY, color, thickness, dotLength, spaceLength);
            return true;
        }

        /**
         * draws a filled text, returns false if culled
         */
        public static boolean fillTextCull(String text, float xCurrent, float xLast, float yCurrent, float yLast, Fonts font, float size, StringAlignment alignment, int color) {
            float x = lerp(xCurrent, xLast);
            float y = lerp(yCurrent, yLast);
//            if (cull(x, y, width, height)) return;//TODO: fix
            DrawUtil.fillText(text, x, y, font, size, alignment, color);
            return true;
        }

        /**
         * draws a image, returns false if culled
         */
        public static boolean fillImageCull(Image image, float xCurrent, float xLast, float yCurrent, float yLast, float width, float height) {
            float x = lerp(xCurrent, xLast);
            float y = lerp(yCurrent, yLast);
            if (cull(x, y, width, height)) return false;
            DrawUtil.fillImage(image, x, y, width, height);
            return true;
        }

        /**
         * draws a model, returns false if culled
         */
        public static boolean fillModelCull(ModelInstance model, float xCurrent, float xLast, float yCurrent, float yLast, float zCurrent, float zLast, float directionCurrent, float directionLast) {
//            float x = lerp(xCurrent, xLast);
//            float y = lerp(yCurrent, yLast);
////            if (cull(x, y, width, height)) return;//TODO: fix
//            DrawUtil.fillModel(model, color, x, y, lerp(zCurrent, zLast), lerp(directionCurrent, directionLast));
            return true;
        }

    }

    public static void clearCanvas() {
        ScreenUtils.clear(Color.CLEAR);
    }

    public static void fillBackground() {
//        setColor(0x000000FF);
//        gc.fillRect(0, 0, 1920 * scale + (Viewport.getXOffset() * 2) + 1, 1080 * scale + (Viewport.getYOffset() * 2) + 1);
        fillRect(0, 0, 1920, 1080, 0x323232FF);
    }

    public static void fillOffsetEdge() {
//        setColor(0x000000FF);
//        gc.fillRect(0, 0, 1920 * scale + (Viewport.getXOffset() * 2), Viewport.getYOffset());
//        gc.fillRect(0, 0, Viewport.getXOffset(), 1080 * scale + Viewport.getYOffset() * 2);
//        gc.fillRect(1920 * scale + Viewport.getXOffset(), 0, Viewport.getXOffset(), 1080 * scale + (Viewport.getYOffset() * 2));
//        gc.fillRect(0, 1080 * scale + Viewport.getYOffset(), 1920 * scale + (Viewport.getXOffset() * 2), Viewport.getYOffset() * 2);
    }
}