package utils;

import game.Fonts;
import game.GameViewport;
import game.Viewport;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;


public class DrawUtil{
    private GraphicsContext gc;
    GameViewport gameViewport;
    double factor = 0;

    public synchronized void setFactor(double factor){
        this.factor = factor;
    }
    public synchronized double getFactor(){
        return factor;
    }

    public void setGameViewport(GameViewport gameViewport) {
        this.gameViewport = gameViewport;
    }

    public void disableGameViewport(){
        gameViewport = null;
    }

    public void setGC(GraphicsContext gc){
        this.gc = gc;
    }

    public GraphicsContext getGC(){
        return gc;
    }

    public void startRotation(double x1, double y1, double x2, double y2, double xOffset, double yOffset, double direction1, double direction2){
        factor = StrictMath.clamp(factor, 0, 1);
        double x;
        double y;
        double scale = Viewport.getScale();
        if (gameViewport != null){
            x = (NumUtil.interpolate(x1, x2, factor)-gameViewport.getX() - Viewport.getX() + xOffset)*scale + Viewport.getXOffset();
            y = (NumUtil.interpolate(y1, y2, factor)-gameViewport.getY() - Viewport.getY() + yOffset)*scale + Viewport.getYOffset();
        } else {
            x = (NumUtil.interpolate(x1, x2, factor) - Viewport.getX() + xOffset)*scale + Viewport.getXOffset();
            y = (NumUtil.interpolate(y1, y2, factor) - Viewport.getY() + yOffset)*scale + Viewport.getYOffset();
         }
        rotate(x, y, NumUtil.interpolate(direction1, direction2, factor));
    }

    private void rotate(double x, double y, double rotation){
        gc.save();
        gc.translate(x, y);
        gc.rotate(rotation);
        gc.translate(-x, -y);

    }
    public void resetRotation(){
        gc.restore();
    }

    public void setColor(int r, int g, int b){
        gc.setFill(Color.rgb(r, g, b));
        gc.setStroke(Color.rgb(r, g, b));
    }
    public void setColor(int r, int g, int b, double transparency){
        gc.setFill(Color.rgb(r, g, b, transparency));
        gc.setStroke(Color.rgb(r, g, b, transparency));
    }
    public void setColor(Color color) {
        gc.setFill(color);
        gc.setStroke(color);
    }

    public void fillRect(double x, double y, double width, double height) {
        if (gameViewport != null){
             if (!CollisionUtil.RectRectCollision(gameViewport.getX(), gameViewport.getY(), gameViewport.getWidth(), gameViewport.getHeight(), x, y, width, height)){
                 return;
             }
        }
        double scale = Viewport.getScale();
        gc.fillRect((int)StrictMath.round((x-Viewport.getX())*scale + Viewport.getXOffset()), (int)StrictMath.round((y-Viewport.getY())*scale + Viewport.getYOffset()), (int)StrictMath.round(width*scale), (int)StrictMath.round(height*scale));
    }

    public void fillRectInterpolate(double x1, double y1, double width, double height, double x2, double y2, double direction1, double direction2) {
        factor = StrictMath.clamp(factor, 0, 1);
        double x = x2 * factor + x1 * (1 - factor);
        double y = y2 * factor + y1 * (1 - factor);
        double scale = Viewport.getScale();
        if (gameViewport != null){
            if (!CollisionUtil.RectRectCollision(gameViewport.getX(), gameViewport.getY(), gameViewport.getWidth(), gameViewport.getHeight(), x, y, width, height)){
                return;
            }
            gc.fillRect((int)StrictMath.round(((x-gameViewport.getX())-Viewport.getX())*scale + Viewport.getXOffset()), (int)StrictMath.round(((y-gameViewport.getY())-Viewport.getY())*scale + Viewport.getYOffset()), (int)StrictMath.round(width*scale), (int)StrictMath.round(height*scale));
            return;
        }
//        rotate(x + width/2, y + height/2, direction2 * factor + direction1 * (1 - factor));
        gc.fillRect((int)StrictMath.round((x-Viewport.getX())*scale + Viewport.getXOffset()), (int)StrictMath.round((y-Viewport.getY())*scale + Viewport.getYOffset()), (int)StrictMath.round(width*scale), (int)StrictMath.round(height*scale));
//        resetRotation();
    }

    public void fillRoundRectInterpolate(double x1, double y1, double width, double height, double edge, double x2, double y2, double direction1, double direction2) {
        factor = StrictMath.clamp(factor, 0, 1);
        double x = x2 * factor + x1 * (1 - factor);
        double y = y2 * factor + y1 * (1 - factor);
        double scale = Viewport.getScale();
        if (gameViewport != null){
            if (!CollisionUtil.RectRectCollision(gameViewport.getX(), gameViewport.getY(), gameViewport.getWidth(), gameViewport.getHeight(), x, y, width, height)){
                return;
            }
            gc.fillRoundRect((int)StrictMath.round((x-gameViewport.getX()-Viewport.getX())*scale + Viewport.getXOffset()), (int)StrictMath.round((y-gameViewport.getY()-Viewport.getY())*scale + Viewport.getYOffset()), (int)StrictMath.round(width*scale), (int)StrictMath.round(height*scale), (int)StrictMath.round(edge*scale), (int)StrictMath.round(edge*scale));
            return;
        }

//        rotate(x + width/2, y + height/2, direction2 * factor + direction1 * (1 - factor));
        gc.fillRoundRect((int)StrictMath.round((x-Viewport.getX())*scale + Viewport.getXOffset()), (int)StrictMath.round((y-Viewport.getY())*scale + Viewport.getYOffset()), (int)StrictMath.round(width*scale), (int)StrictMath.round(height*scale), (int)StrictMath.round(edge*scale), (int)StrictMath.round(edge*scale));
//        resetRotation();
    }


    public void drawRect(double x, double y, double width, double height) {
        if (gameViewport != null){
            if (!CollisionUtil.RectRectCollision(gameViewport.getX(), gameViewport.getY(), gameViewport.getWidth(), gameViewport.getHeight(), x, y, width, height)){
                return;
            }
        }
        double scale = Viewport.getScale();
        gc.strokeRect((int)StrictMath.round((x-Viewport.getX())*scale + Viewport.getXOffset()), (int)StrictMath.round((y-Viewport.getY())*scale + Viewport.getYOffset()), (int)StrictMath.round(width*scale), (int)StrictMath.round(height*scale));
    }

    public void drawRect(Rectangle2D rectangle) {
        drawRect(rectangle.getMinX(), rectangle.getMinY(), rectangle.getWidth(), rectangle.getHeight());
    }


    public void fillRect(Rectangle2D rectangle) {
        if (gameViewport != null){
            if (!CollisionUtil.RectRectCollision(gameViewport.getX(), gameViewport.getY(), gameViewport.getWidth(), gameViewport.getHeight(), rectangle.getMinX(), rectangle.getMinY(), rectangle.getWidth(), rectangle.getHeight())){
                return;
            }
        }
        double scale = Viewport.getScale();
        gc.fillRect((int)StrictMath.round((rectangle.getMinX()-Viewport.getX())*scale + Viewport.getXOffset()), (int)StrictMath.round((rectangle.getMinY()-Viewport.getY())*scale + Viewport.getYOffset()), (int)StrictMath.round(rectangle.getWidth()*scale), (int)StrictMath.round(rectangle.getHeight()*scale));
    }


    public void fillCircle(double x, double y, double radius) {
        if (gameViewport != null){
            if (!CollisionUtil.RectCircleCollision(x, y, radius, gameViewport.getX(), gameViewport.getY(), gameViewport.getWidth(), gameViewport.getHeight())){
                return;
            }
        }
        double scale = Viewport.getScale();
        gc.fillOval((int)StrictMath.round((x-Viewport.getX())*scale + Viewport.getXOffset()), (int)StrictMath.round((y-Viewport.getY())*scale + Viewport.getYOffset()), (int)StrictMath.round(radius*2*scale), (int)StrictMath.round(radius*2*scale));
    }
    public void fillCircleInterpolate(double x1, double y1, double radius, double x2, double y2) {
        factor = StrictMath.clamp(factor, 0, 1);
        double x = x2 * factor + x1 * (1 - factor);
        double y = y2 * factor + y1 * (1 - factor);
        double scale = Viewport.getScale();

        if (gameViewport != null){
            if (!CollisionUtil.RectCircleCollision(x, y, radius, gameViewport.getX(), gameViewport.getY(), gameViewport.getWidth(), gameViewport.getHeight())){
                return;
            }
            gc.fillOval((x-gameViewport.getX() - Viewport.getX())*scale + Viewport.getXOffset(), (y-gameViewport.getY()-Viewport.getY())*scale + Viewport.getYOffset(), (radius*2*scale), (radius*2*scale));
            return;
        }
//        rotate(x + radius, y + radius, direction2 * factor + direction1 * (1 - factor));
        gc.fillOval(((x - Viewport.getX())*scale + Viewport.getXOffset()), ((y-Viewport.getY())*scale + Viewport.getYOffset()), (radius*2*scale), (radius*2*scale));
//        resetRotation();
    }

    public void fillImage(Image image, double x, double y, double width, double height) {
        if (gameViewport != null){
            if (!CollisionUtil.RectRectCollision(gameViewport.getX(), gameViewport.getY(), gameViewport.getWidth(), gameViewport.getHeight(), x, y, width, height)){
                return;
            }
        }
        double scale = Viewport.getScale();
        gc.drawImage(image, (x-Viewport.getX()*scale + Viewport.getXOffset()), (y-Viewport.getY()*scale + Viewport.getYOffset()), (width*scale), (height*scale));
    }
    public void fillImageInterpolate(Image image, double x1, double y1, double width, double height, double x2, double y2) {
        factor = StrictMath.clamp(factor, 0, 1);
        double x = x2 * factor + x1 * (1 - factor);
        double y = y2 * factor + y1 * (1 - factor);
        double scale = Viewport.getScale();
        if (gameViewport != null){
            if (!CollisionUtil.RectRectCollision(gameViewport.getX(), gameViewport.getY(), gameViewport.getWidth(), gameViewport.getHeight(), x, y, width, height)){
                return;
            }
            gc.drawImage(image, (x-gameViewport.getX() - Viewport.getX())*scale + Viewport.getXOffset(), (y-gameViewport.getY() - Viewport.getY())*scale + Viewport.getYOffset(), scale*width, scale*height);
            return;
        }
        gc.drawImage(image, ((x - Viewport.getX())*scale + Viewport.getXOffset()), ((y - Viewport.getY())*scale + Viewport.getYOffset()), (width*scale), (height*scale));
    }

    public void drawString(double x, double y, String string, double size, Fonts font){
        double scale = Viewport.getScale();
        Text text = new Text(string);
        text.setFont(font.getFont(size));
        gc.setFont(font.getFont(size));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        if (gameViewport != null){
            if (!CollisionUtil.RectRectCollision(gameViewport.getX(), gameViewport.getY(), gameViewport.getWidth(), gameViewport.getHeight(), x-(text.getBoundsInLocal().getWidth() / 2f), y, text.getBoundsInLocal().getWidth(), text.getBoundsInLocal().getHeight())){
                return;
            }
            gc.fillText(string, (x-gameViewport.getX() - Viewport.getX())*scale + Viewport.getXOffset(), (y-gameViewport.getY() - Viewport.getY())*scale + Viewport.getYOffset());
            return;
        }
        gc.fillText(string, ((x - Viewport.getX())*scale + Viewport.getXOffset()), ((y - Viewport.getY())*scale + Viewport.getYOffset()));
    }
//    public void drawStringFill(double x, double y, String string, double width, double fill, double max){
//        double scale = Viewport.getScale();
//        Font testFont = new Font("Monospaced", Font.PLAIN, (int)StrictMath.min(width*fill, max));
//        if (gameViewport != null){
//            if (!CollisionUtil.RectRectCollision(gameViewport.getX(), gameViewport.getY(), gameViewport.getWidth(), gameViewport.getHeight(), x-(gc.getFontMetrics(testFont).stringWidth(string) / 2f), y, gc.getFontMetrics(testFont).stringWidth(string), testFont.getSize())){
//                return;
//            }
//        }
//        Font font = new Font("Monospaced", Font.PLAIN, (int)StrictMath.round(StrictMath.min(width*fill, max)*scale));
//        gc.setFont(font);
//        gc.drawString(string, (int)StrictMath.round((x * scale + Viewport.getXOffset())- (gc.getFontMetrics(font).stringWidth(string) / 2f)), (int)StrictMath.round(y * scale + Viewport.getYOffset()));
//    }

    public void drawLine(double x1, double y1, double x2, double y2){
        if (gameViewport != null){
            if (!CollisionUtil.RectLineCollision(x1, y1, x2, y2, gameViewport.getX(), gameViewport.getY(), gameViewport.getWidth(), gameViewport.getHeight())){
                return;
            }
        }
        double scale = Viewport.getScale();
        gc.strokeLine(x1*scale + Viewport.getXOffset(), y1*scale + Viewport.getYOffset(), x2*scale + Viewport.getXOffset(), y2*scale + Viewport.getYOffset());
    }

    public void fillBackground(){
        double scale = Viewport.getScale();
        setColor(0, 0, 0);
        gc.fillRect(0, 0, StrictMath.ceil(1920*scale + (Viewport.getXOffset()*2)), StrictMath.ceil(1080*scale + (Viewport.getYOffset()*2)));
        setColor(50, 50, 50);
        fillRect(0, 0, 1920, 1080);
    }

    public void fillOffsetEdge(){
        double scale = Viewport.getScale();
        setColor(0, 0, 0);
        gc.fillRect(0, 0, 1920*scale + (Viewport.getXOffset()*2), Viewport.getYOffset());
        gc.fillRect(0, 0, Viewport.getXOffset(), 1080*scale + Viewport.getYOffset()*2);
        gc.fillRect(1920*scale + Viewport.getXOffset(), 0, Viewport.getXOffset(), 1080*scale + (Viewport.getYOffset()*2));
        gc.fillRect(0, 1080*scale + Viewport.getYOffset(), 1920*scale + (Viewport.getXOffset()*2), Viewport.getYOffset()*2);
    }

    public void setThickness(double thickness){
        gc.setLineWidth(thickness);
    }
}
