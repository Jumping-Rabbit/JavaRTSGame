package com.game.utils;


import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class CollisionUtil {
    public static boolean RectPointCollision(Rectangle rec, Vector2 point) {
        return (point.x >= rec.getX() && point.x <= rec.getX() + rec.getWidth()) && (point.y >= rec.getY() && point.y <= rec.getY() + rec.getHeight());
    }

    public static boolean RectPointCollision(Rectangle rec, float x, float y) {
        return (x >= rec.getX() && x <= rec.getX() + rec.getWidth()) && (y >= rec.getY() && y <= rec.getY() + rec.getHeight());
    }

    public static boolean RectPointCollision(float x1, float y1, float width1, float height1, float x2, float y2) {
        return (x2 >= x1 && x2 <= x1 + width1) && (y2 >= y1 && y2 <= y1 + height1);
    }

    public static boolean RectRectCollision(Rectangle rec1, Rectangle rec2) {
        return RectRectCollision(rec1.getX(), rec1.getY(), rec1.getWidth(), rec1.getHeight(), rec2.getX(), rec2.getY(), rec2.getWidth(), rec2.getHeight());
    }


    public static boolean RectRectCollision(float x1, float y1, float width1, float height1, float x2, float y2, float width2, float height2) {
        boolean x1b = x2 >= x1 && x2 <= x1 + width1;
        boolean x2b = x2 + width2 >= x1 && x2 + width2 <= x1 + width1;
        boolean y1b = y2 >= y1 && y2 <= y1 + height1;
        boolean y2b = y2 + height2 >= y1 && y2 + height2 <= y1 + height1;
        return x1b && y1b || x2b && y1b || x2b && y2b || x1b && y2b;
    }

    public static boolean RectCircleCollision(float x1, float y1, float r1, Rectangle rect) {
        return RectCircleCollision(x1, y1, r1, NumUtil.FTL(rect.getX()), NumUtil.FTL(rect.getY()), NumUtil.FTL(rect.getWidth()), NumUtil.FTL(rect.getHeight()));
    }

    public static boolean RectCircleCollision(float x1, float y1, float r1, float x2, float y2, float width2, float height2) {
        float centerX = x1 + r1;
        float centerY = y1 + r1;

        float closestX = Math.max(x2, Math.min(centerX, x2 + width2));
        float closestY = Math.max(y2, Math.min(centerY, y2 + height2));

        float distanceX = centerX - closestX;
        float distanceY = centerY - closestY;

        return (distanceX * distanceX + distanceY * distanceY) < (r1 * r1);
    }

    public static boolean RectLineCollision(float x11, float y11, float x12, float y12, float x2, float y2, float width2, float height2) {
        return LineLineCollision(x11, y11, x12, y12, x2, y2, x2, y2 + height2) || //left
                LineLineCollision(x11, y11, x12, y12, x2, y2, x2 + width2, y2) || //top
                LineLineCollision(x11, y11, x12, y12, x2 + width2, y2, x2 + width2, y2 + height2) || //right
                LineLineCollision(x11, y11, x12, y12, x2, y2 + height2, x2 + width2, y2 + height2); //bottom
    }

    public static boolean LineLineCollision(float x11, float y11, float x12, float y12, float x21, float y21, float x22, float y22) {
        float denominator = (x11 - x12) * (y21 - y22) - (y11 - y12) * (x21 - x22);
        if (denominator == 0) {
            return false;
        }
        float uA = (x11 - x21) * (y21 - y22) - (y11 - y21) * (x21 - x22) / denominator;
        float uB = (x11 - x21) * (y11 - y12) - (y11 - y21) * (x11 - x12) / denominator;
        return uA >= 0 && uA <= 1 && uB >= 0 && uB <= 1;
    }

    public static boolean CircleCircleCollision(float x1, float y1, float r1, float x2, float y2, float r2) {
        float width1 = r1 + r1;
        float width2 = r2 + r2;
        if (!RectRectCollision(x1, y1, x1 + width1, y1 + width1, x2, y2, x2 + width2, y2 + width2)) {
            return false;
        }
        float dx = (x2 + r2) - (x1 + r1);
        float dy = (y2 + r2) - (y1 + r1);
        float sumRadii = r1 + r2;
        return (dx * dx + dy * dy) <= (sumRadii * sumRadii);
    }

    public static boolean PointCircleCollision(float x1, float y1, float x2, float y2, float r2) {
        float centerX = x2 + r2;
        float centerY = y2 + r2;
        float distX = x1 - centerX;
        float distY = y1 - centerY;
        float distSq = (distX * distX) + (distY * distY);
        return distSq <= (r2 * r2);
    }

    public static boolean CircleLineCollision() {
        return false;//TODO: make this work
    }
}
