package main;

import main.game.GameViewport;
import main.game.Viewport;

import java.awt.*;
import java.awt.image.BufferedImage;

public class DrawUtil{
    private Graphics2D g2;
    GameViewport gameViewport;
    boolean rotated = false;
    double x;
    double y;
    double rotation;
    boolean startRotate = false;

    public void setGameViewport(GameViewport gameViewport) {
        this.gameViewport = gameViewport;
    }

    public void disableGameViewport(){
        gameViewport = null;
    }

    public void setG2(Graphics2D g2){
        this.g2 = g2;
    }

    public Graphics2D getG2(){
        return g2;
    }

    public void startRotation(){
        startRotate = true;
    }

    private void rotate(double x, double y, double rotation){
        if (!startRotate){
            return;
        }
        if (rotated){
            g2.rotate(-Math.toRadians(rotation), (x-Viewport.viewport.getX())*Viewport.viewport.getScale() + Viewport.viewport.getXOffset(), (y-Viewport.viewport.getY())*Viewport.viewport.getScale() + Viewport.viewport.getYOffset());//TODO: account for the offset and scale and stuff
        }
        g2.rotate(Math.toRadians(rotation), (x-Viewport.viewport.getX())*Viewport.viewport.getScale() + Viewport.viewport.getXOffset(), (y-Viewport.viewport.getY())*Viewport.viewport.getScale() + Viewport.viewport.getYOffset());
        rotated = true;
        this.x = x;
        this.y = y;
        this.rotation = rotation;
        startRotate = false;
    }
    public void resetRotation(){
        if (rotated){
            g2.rotate(-Math.toRadians(rotation), (x-Viewport.viewport.getX())*Viewport.viewport.getScale() + Viewport.viewport.getXOffset(), (y-Viewport.viewport.getY())*Viewport.viewport.getScale() + Viewport.viewport.getYOffset());
            rotated = false;
        }
    }

    public void setColor(int r, int g, int b){
        g2.setColor(new Color(r, g, b));
    }
    public void setColor(int r, int g, int b, int transparency){
        g2.setColor(new Color(r, g, b, transparency));
    }
    public void setColor(Color color) {
        g2.setColor(color);
    }

    public void fillRect(double x, double y, double width, double height) {
        if (gameViewport != null){
             if (!CollisionUtil.RectRectCollision(gameViewport.getX(), gameViewport.getY(), gameViewport.getWidth(), gameViewport.getHeight(), x, y, width, height)){
                 return;
             }
        }
        double scale = Viewport.viewport.getScale();
        g2.fillRect((int)Math.round((x-Viewport.viewport.getX())*scale + Viewport.viewport.getXOffset()), (int)Math.round((y-Viewport.viewport.getY())*scale + Viewport.viewport.getYOffset()), (int)Math.round(width*scale), (int)Math.round(height*scale));
    }

    public void fillRectInterpolate(double x1, double y1, double width, double height, double x2, double y2,  double factor, double direction1, double direction2) {
        factor = Math.clamp(factor, 0, 1);
        double x = x2 * factor + x1 * (1 - factor);
        double y = y2 * factor + y1 * (1 - factor);
        if (gameViewport != null){
            if (!CollisionUtil.RectRectCollision(gameViewport.getX(), gameViewport.getY(), gameViewport.getWidth(), gameViewport.getHeight(), x, y, width, height)){
                return;
            }
        }
        double scale = Viewport.viewport.getScale();
        rotate(x + width/2, y + height/2, direction2 * factor + direction1 * (1 - factor));
        g2.fillRect((int)Math.round((x-Viewport.viewport.getX())*scale + Viewport.viewport.getXOffset()), (int)Math.round((y-Viewport.viewport.getY())*scale + Viewport.viewport.getYOffset()), (int)Math.round(width*scale), (int)Math.round(height*scale));
//        resetRotation();
    }

    public void drawRect(double x, double y, double width, double height) {
        if (gameViewport != null){
            if (!CollisionUtil.RectRectCollision(gameViewport.getX(), gameViewport.getY(), gameViewport.getWidth(), gameViewport.getHeight(), x, y, width, height)){
                return;
            }
        }
        double scale = Viewport.viewport.getScale();
        g2.drawRect((int)Math.round((x-Viewport.viewport.getX())*scale + Viewport.viewport.getXOffset()), (int)Math.round((y-Viewport.viewport.getY())*scale + Viewport.viewport.getYOffset()), (int)Math.round(width*scale), (int)Math.round(height*scale));
    }

    public void drawRect(Rectangle rectangle) {
        if (gameViewport != null){
            if (!CollisionUtil.RectRectCollision(gameViewport.getX(), gameViewport.getY(), gameViewport.getWidth(), gameViewport.getHeight(), rectangle.x, rectangle.y, rectangle.width, rectangle.height)){
                return;
            }
        }
        double scale = Viewport.viewport.getScale();
        g2.drawRect((int)Math.round((rectangle.x-Viewport.viewport.getX())*scale + Viewport.viewport.getXOffset()), (int)Math.round((rectangle.y-Viewport.viewport.getY())*scale + Viewport.viewport.getYOffset()), (int)Math.round(rectangle.width*scale), (int)Math.round(rectangle.height*scale));
    }


    public void fillCircle(double x, double y, double radius) {
        if (gameViewport != null){
            if (!CollisionUtil.RectCircleCollision(x, y, radius, gameViewport.getX(), gameViewport.getY(), gameViewport.getWidth(), gameViewport.getHeight())){
                return;
            }
        }
        double scale = Viewport.viewport.getScale();
        g2.fillOval((int)Math.round((x-Viewport.viewport.getX())*scale + Viewport.viewport.getXOffset()), (int)Math.round((y-Viewport.viewport.getY())*scale + Viewport.viewport.getYOffset()), (int)Math.round(radius*2*scale), (int)Math.round(radius*2*scale));
    }
    public void fillCircleInterpolate(double x1, double y1, double radius, double x2, double y2, double factor, double direction1, double direction2) {
        factor = Math.clamp(factor, 0, 1);
        double x = x2 * factor + x1 * (1 - factor);
        double y = y2 * factor + y1 * (1 - factor);
        if (gameViewport != null){
            if (!CollisionUtil.RectCircleCollision(x, y, radius, gameViewport.getX(), gameViewport.getY(), gameViewport.getWidth(), gameViewport.getHeight())){
                return;
            }
        }
        double scale = Viewport.viewport.getScale();
        rotate(x + radius, y + radius, direction2 * factor + direction1 * (1 - factor));
        g2.fillOval((int)Math.round((x - Viewport.viewport.getX())*scale + Viewport.viewport.getXOffset()), (int)Math.round((y-Viewport.viewport.getY())*scale + Viewport.viewport.getYOffset()), (int)Math.round(radius*2*scale), (int)Math.round(radius*2*scale));
//        resetRotation();
    }

    public void fillImage(BufferedImage image, double x, double y, double width, double height) {
        if (gameViewport != null){
            if (!CollisionUtil.RectRectCollision(gameViewport.getX(), gameViewport.getY(), gameViewport.getWidth(), gameViewport.getHeight(), x, y, width, height)){
                return;
            }
        }
        double scale = Viewport.viewport.getScale();
        g2.drawImage(image, (int)Math.round((x-Viewport.viewport.getX())*scale + Viewport.viewport.getXOffset()), (int)Math.round((y-Viewport.viewport.getY())*scale + Viewport.viewport.getYOffset()), (int)Math.round(width*scale), (int)Math.round(height*scale), null);
    }

    public void drawString(double x, double y, String string, int size){
        double scale = Viewport.viewport.getScale();
        Font testFont = new Font("Monospaced", Font.PLAIN, size);
        if (gameViewport != null){
            if (!CollisionUtil.RectRectCollision(gameViewport.getX(), gameViewport.getY(), gameViewport.getWidth(), gameViewport.getHeight(), x-(g2.getFontMetrics(testFont).stringWidth(string) / 2f), y, g2.getFontMetrics(testFont).stringWidth(string), testFont.getSize())){
                return;
            }
        }
        Font font = new Font("Monospaced", Font.PLAIN, (int)Math.round(size*scale));
        g2.setFont(font);
        g2.drawString(string, (int)Math.round((x * scale + Viewport.viewport.getXOffset())- (g2.getFontMetrics(font).stringWidth(string) / 2f)), (int)Math.round(y * scale + Viewport.viewport.getYOffset()));
    }
    public void drawStringFill(double x, double y, String string, double width, double fill, double max){
        double scale = Viewport.viewport.getScale();
        Font testFont = new Font("Monospaced", Font.PLAIN, (int)Math.min(width*fill, max));
        if (gameViewport != null){
            if (!CollisionUtil.RectRectCollision(gameViewport.getX(), gameViewport.getY(), gameViewport.getWidth(), gameViewport.getHeight(), x-(g2.getFontMetrics(testFont).stringWidth(string) / 2f), y, g2.getFontMetrics(testFont).stringWidth(string), testFont.getSize())){
                return;
            }
        }
        Font font = new Font("Monospaced", Font.PLAIN, (int)Math.round(Math.min(width*fill, max)*scale));
        g2.setFont(font);
        g2.drawString(string, (int)Math.round((x * scale + Viewport.viewport.getXOffset())- (g2.getFontMetrics(font).stringWidth(string) / 2f)), (int)Math.round(y * scale + Viewport.viewport.getYOffset()));
    }

    public void drawLine(double x1, double y1, double x2, double y2){
        if (gameViewport != null){
            if (!CollisionUtil.RectLineCollision(x1, y1, x2, y2, gameViewport.getX(), gameViewport.getY(), gameViewport.getWidth(), gameViewport.getHeight())){
                return;
            }
        }
        double scale = Viewport.viewport.getScale();
        g2.drawLine((int)Math.round(x1*scale + Viewport.viewport.getXOffset()), (int)Math.round(y1*scale + Viewport.viewport.getYOffset()), (int)Math.round(x2*scale + Viewport.viewport.getXOffset()), (int)Math.round(y2*scale + Viewport.viewport.getYOffset()));
    }

    public void fillBackground(){
        double scale = Viewport.viewport.getScale();
        g2.setColor(new Color(0, 0, 0));
        g2.fillRect(0, 0, (int)Math.ceil(1920*scale + (Viewport.viewport.getXOffset()*2)), (int)Math.ceil(1080*scale + (Viewport.viewport.getYOffset()*2)));

//        setThickness(2);
//        setColor(100, 100, 100);
//        g2.drawLine((int)Math.round(Viewport.viewport.getXOffset()-1), 0, (int)Math.round(Viewport.viewport.getXOffset()-1), (int)Math.round(1080+Viewport.viewport.getYOffset()*2d));
//        g2.drawLine((int)Math.round(Viewport.viewport.getXOffset() + 1920*scale+1), 0, (int)Math.round(Viewport.viewport.getXOffset() + 1920*scale+1), (int)Math.round(1080+Viewport.viewport.getYOffset()*2d));
//
//        g2.drawLine(0, (int)Math.round(Viewport.viewport.getYOffset() - 1), (int)Math.round(1920+Viewport.viewport.getXOffset()*2d), (int)Math.round(Viewport.viewport.getYOffset() - 1));
//        g2.drawLine(0, (int)Math.round(Viewport.viewport.getYOffset() + 1080*scale + 1), (int)Math.round(1920+Viewport.viewport.getXOffset()*2d), (int)Math.round(Viewport.viewport.getYOffset() + 1080*scale + 1));

        g2.setColor(new Color(50, 50, 50));
        fillRect(0, 0, 1920, 1080);

    }

    public void setThickness(float thickness){
        g2.setStroke(new BasicStroke((float)(thickness*Viewport.viewport.getScale())));
    }
}
