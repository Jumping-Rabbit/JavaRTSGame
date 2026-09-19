package com.game.settings;

public class IntSetting implements Setting<Integer> {
    private final String name;
    private int value;
    private int min;
    private int max;
    private boolean hasMin;
    private boolean hasMax;

    public IntSetting(String name, Integer min, Integer max){
        this.name = name;
        hasMin = min != null;
        hasMax = max != null;
        this.min = hasMin ? min : Integer.MIN_VALUE;
        this.max = hasMax ? max : Integer.MAX_VALUE;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Integer getValue() {
        return value;
    }

    @Override
    public void setValue(Integer value) {
        this.value = StrictMath.clamp(value, min, max);
    }

    @Override
    public void nextValue() {
        value ++;
        if (hasMax && value > max) {
            value = hasMin ? min : max;
        }
    }

    @Override
    public void previousValue() {
        value --;
        if (hasMin && value < min) {
            value = hasMax ? max : min;
        }
    }
}
