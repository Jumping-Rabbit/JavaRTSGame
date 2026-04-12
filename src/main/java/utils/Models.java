package utils;

public enum Models {
    vanguardMarine;

    private double width;
    private double height;
    private double halfWidth;
    private double halfHeight;
    private long scaledWidth;
    private long scaledHeight;
    private long scaledHalfWidth;
    private long scaledHalfHeight;
    private double boundingRadius;
    private double boundingDiameter;
    private long boundingRadiusScaled;
    private long boundingDiameterScaled;
    private double boundingDiff;
    private long boundingDiffScaled;
    private double boundingOffset;
    private long boundingOffsetScaled;


    public static Models fromValue(String givenName) {
        for (Models model : values()) {
            if (model.toString().equalsIgnoreCase(givenName)) {
                return model;
            }
        }
        return null;
    }

    public double getBoundingRadius() {
        return boundingRadius;
    }

    public void setBoundingRadius(double boundingRadius) {
        this.boundingRadius = boundingRadius;
    }

    public double getBoundingDiameter() {
        return boundingDiameter;
    }

    public void setBoundingDiameter(double boundingDiameter) {
        this.boundingDiameter = boundingDiameter;
    }

    public long getBoundingRadiusScaled() {
        return boundingRadiusScaled;
    }

    public void setBoundingRadiusScaled(long boundingRadiusScaled) {
        this.boundingRadiusScaled = boundingRadiusScaled;
    }

    public long getBoundingDiameterScaled() {
        return boundingDiameterScaled;
    }

    public void setBoundingDiameterScaled(long boundingDiameterScaled) {
        this.boundingDiameterScaled = boundingDiameterScaled;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
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

    public void setHalfWidth(double halfWidth) {
        this.halfWidth = halfWidth;
    }

    public double getHalfHeight() {
        return halfHeight;
    }

    public void setHalfHeight(double halfHeight) {
        this.halfHeight = halfHeight;
    }

    public long getScaledWidth() {
        return scaledWidth;
    }

    public void setScaledWidth(long scaledWidth) {
        this.scaledWidth = scaledWidth;
    }

    public long getScaledHeight() {
        return scaledHeight;
    }

    public void setScaledHeight(long scaledHeight) {
        this.scaledHeight = scaledHeight;
    }

    public long getScaledHalfWidth() {
        return scaledHalfWidth;
    }

    public void setScaledHalfWidth(long scaledHalfWidth) {
        this.scaledHalfWidth = scaledHalfWidth;
    }

    public long getScaledHalfHeight() {
        return scaledHalfHeight;
    }

    public void setScaledHalfHeight(long scaledHalfHeight) {
        this.scaledHalfHeight = scaledHalfHeight;
    }

    public double getBoundingDiff() {
        return boundingDiff;
    }

    public void setBoundingDiff(double boundingDiff) {
        this.boundingDiff = boundingDiff;
    }

    public long getBoundingDiffScaled() {
        return boundingDiffScaled;
    }

    public void setBoundingDiffScaled(long boundingDiffScaled) {
        this.boundingDiffScaled = boundingDiffScaled;
    }

    public double getBoundingOffset() {
        return boundingOffset;
    }

    public void setBoundingOffset(double boundingOffset) {
        this.boundingOffset = boundingOffset;
    }

    public long getBoundingOffsetScaled() {
        return boundingOffsetScaled;
    }

    public void setBoundingOffsetScaled(long boundingOffsetScaled) {
        this.boundingOffsetScaled = boundingOffsetScaled;
    }
}
