package utils;

import javax.sound.midi.SysexMessage;

import static utils.NumUtil.DTL;

public enum Models {
    vanguardMarine(ModelType.UNIT),
    vanguardBarracks(ModelType.BUILDING);

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
    private ModelType modelType;

    Models(ModelType modelType) {
        this.modelType = modelType;
    }

    public enum ModelType{
        UNIT,
        BUILDING;
    }

    public static int getBuildingAmount(){
        int count = 0;
        for (Models model : values()){
            if (model.modelType == ModelType.BUILDING){
                count++;
            }
        }
        System.out.println("b" + count);
        return count;
    }
    public static int getUnitAmount(){
        int count = 0;
        for (Models model : values()){
            if (model.modelType == ModelType.UNIT){
                count++;
            }
        }
        System.out.println("u" +count);
        return count;
    }

    public static Models fromValue(String givenName) {
        for (Models model : values()) {
            if (model.toString().equalsIgnoreCase(givenName)) {
                return model;
            }
        }
        return null;
    }

    public void set(double width, double height, double radius, ModelType modelType){
        this.width = width;
        halfWidth = width/2;
        scaledWidth = DTL(width);
        scaledHalfWidth = DTL(width/2);
        this.height = height;
        halfHeight = height/2;
        scaledHeight = DTL(height);
        scaledHalfHeight = DTL(height/2);
        this.boundingRadius = radius;
        boundingDiameter = radius*2;
        boundingRadiusScaled = DTL(radius);
        boundingDiameterScaled = DTL(radius*2);
        this.modelType = modelType;
    }

    public ModelType getModelType(){
        return modelType;
    }


    public double getBoundingRadius() {
        return boundingRadius;
    }

    public double getBoundingDiameter() {
        return boundingDiameter;
    }

    public long getBoundingRadiusScaled() {
        return boundingRadiusScaled;
    }

    public long getBoundingDiameterScaled() {
        return boundingDiameterScaled;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public double getHalfWidth() {
        return halfWidth;
    }

    public double getHalfHeight() {
        return halfHeight;
    }

    public long getScaledWidth() {
        return scaledWidth;
    }

    public long getScaledHeight() {
        return scaledHeight;
    }

    public long getScaledHalfWidth() {
        return scaledHalfWidth;
    }

    public long getScaledHalfHeight() {
        return scaledHalfHeight;
    }
}
