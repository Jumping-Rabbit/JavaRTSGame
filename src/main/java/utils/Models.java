package utils;

public enum Models {
    vanguardMarine;

    private static double width;
    private static double height;
    private static double halfWidth;
    private static double halfHeight;
    private static long scaledWidth;
    private static long scaledHeight;
    private static long scaledHalfWidth;
    private static long scaledHalfHeight;


    public static Models fromValue(String givenName) {
        for (Models model : values()) {
            if (model.toString().equalsIgnoreCase(givenName)) {
                return model;
            }
        }
        return null;
    }

    public double getWidth() {
        return width;
    }

    public static void setWidth(double width) {
        Models.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getHalfWidth() {
        return halfWidth;
    }

    public static void setHalfWidth(double halfWidth) {
        Models.halfWidth = halfWidth;
    }

    public double getHalfHeight() {
        return halfHeight;
    }

    public void setHalfHeight(double halfHeight) {
        Models.halfHeight = halfHeight;
    }

    public long getScaledWidth() {
        return scaledWidth;
    }

    public static void setScaledWidth(long scaledWidth) {
        Models.scaledWidth = scaledWidth;
    }

    public long getScaledHeight() {
        return scaledHeight;
    }

    public void setScaledHeight(long scaledHeight) {
        Models.scaledHeight = scaledHeight;
    }

    public long getScaledHalfWidth() {
        return scaledHalfWidth;
    }

    public static void setScaledHalfWidth(long scaledHalfWidth) {
        Models.scaledHalfWidth = scaledHalfWidth;
    }

    public long getScaledHalfHeight() {
        return scaledHalfHeight;
    }

    public void setScaledHalfHeight(long scaledHalfHeight) {
        Models.scaledHalfHeight = scaledHalfHeight;
    }
}
