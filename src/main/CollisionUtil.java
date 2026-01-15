package main;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class CollisionUtil {
    public static boolean RectPointCollision(Rectangle rec, Point point){
        return (point.x >= rec.x && point.x <= rec.x + rec.width) && (point.y >= rec.y && point.y <= rec.y + rec.height);
    }
    public static boolean RectPointCollision(Rectangle2D.Double rec, Point2D.Double point){
        return (point.x >= rec.x && point.x <= rec.x + rec.width) && (point.y >= rec.y && point.y <= rec.y + rec.height);
    }
    public static boolean RectPointCollision(Rectangle rec, double x, double y){
        return (x >= rec.x && x <= rec.x + rec.width) && (y >= rec.y && y <= rec.y + rec.height);
    }
    public static boolean RectPointCollision(double x1, double y1, double width1, double height1, double x2, double y2){
        return (x2 >= x1 && x2 <= x1 + width1) && (y2 >= y1 && y2 <= y1 + height1);
    }
}
