package utils;


import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;

public class CollisionUtil {
    public static boolean RectPointCollision(Rectangle2D rec, Point2D point){
        return (point.getX() >= rec.getMinX() && point.getX() <= rec.getMinX() + rec.getWidth()) && (point.getY() >= rec.getMinY() && point.getY() <= rec.getMinY() + rec.getHeight());
    }
    public static boolean RectPointCollision(Rectangle2D rec, double x, double y){
        return (x >= rec.getMinX() && x <= rec.getMinX() + rec.getWidth()) && (y >= rec.getMinY() && y <= rec.getMinY() + rec.getHeight());
    }
    public static boolean RectPointCollision(double x1, double y1, double width1, double height1, double x2, double y2){
        return (x2 >= x1 && x2 <= x1 + width1) && (y2 >= y1 && y2 <= y1 + height1);
    }
    public static boolean RectRectCollision(Rectangle2D rec1, Rectangle2D rec2){
        return RectRectCollision(rec1.getMinX(), rec1.getMinY(), rec1.getWidth(), rec1.getHeight(), rec2.getMinX(), rec2.getMinY(), rec2.getWidth(), rec2.getHeight());
    }



    public static boolean RectRectCollision(double x1, double y1, double width1, double height1, double x2, double y2, double width2, double height2){
        boolean x1b = x2 >= x1 && x2 <= x1 + width1;
        boolean x2b = x2 + width2 >= x1 && x2 + width2 <= x1 + width1;
        boolean y1b = y2 >= y1 && y2 <= y1 + height1;
        boolean y2b = y2 + height2 >= y1 && y2 + height2 <= y1 + height1;
        return x1b && y1b || x2b && y1b || x2b && y2b || x1b && y2b;
    }
    public static boolean RectCircleCollision(double x1, double y1, double r1, double x2, double y2, double width2, double height2){
        var distX = Math.abs((x1+r1) - x2-width2/2);
        var distY = Math.abs((y1+r1) - y2-height2/2);

        if (distX > (width2/2 + r1)) { return false; }
        if (distY > (height2/2 + r1)) { return false; }

        if (distX <= (width2/2)) { return true; }
        if (distY <= (height2/2)) { return true; }

        var dx=distX-width2/2;
        var dy=distY-height2/2;
        return (dx*dx+dy*dy<=(r1*r1));
    }
    public static boolean RectLineCollision(double x11, double y11, double x12, double y12, double x2, double y2, double width2, double height2){
        return LineLineCollision(x11, y11, x12, y12, x2, y2, x2, y2+height2) || //left
                LineLineCollision(x11, y11, x12, y12, x2, y2, x2+width2, y2) || //top
                LineLineCollision(x11, y11, x12, y12, x2+width2, y2, x2+width2, y2+height2) || //right
                LineLineCollision(x11, y11, x12, y12, x2, y2+height2, x2+width2, y2+height2); //bottom
    }

    public static boolean LineLineCollision(double x11, double y11, double x12, double y12, double x21, double y21, double x22, double y22){
        double denominator = (x11 - x12) * (y21 - y22) - (y11 - y12) * (x21 - x22);
        if (denominator == 0) {
            return false;
        }
        double uA = (x11 - x21) * (y21 - y22) - (y11 - y21) * (x21 - x22) / denominator;
        double uB = (x11 - x21) * (y11 - y12) - (y11 - y21) * (x11 - x12) / denominator;
        return uA >= 0 && uA <= 1 && uB >= 0 && uB <= 1;
    }
    public static boolean CircleCircleCollision(double x1,double y1,double r1,double x2,double y2,double r2){
        double width1 = r1+r1;
        double width2 = r2+r2;
        if (!RectRectCollision(x1, y1, x1+width1, y1+width1, x2, y2, x2+width2, y2+width2)){
            return false;
        }
        double dx = (x2+r2) - (x1+r1);
        double dy = (y2+r2) - (y1+r1);
        double sumRadii = r1 + r2;
        return (dx * dx + dy * dy) <= (sumRadii * sumRadii);
    }
    public static boolean PointCircleCollision(double x1, double y1, double x2, double y2, double r2){
        double distX = x1 - x2;
        double distY = y1 - y2;
        double distSq = (distX * distX) + (distY * distY);//bounding box check not good becuase calculating the
        return distSq <= r2*r2;//bounding box is about as expensive as just checking it without it
    }
}
