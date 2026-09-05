package com.game.settings;

public class BoolSetting implements Setting<Boolean> {
    private final String name;
    private boolean value;

    public BoolSetting(String name){
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Boolean getValue() {
        return value;
    }

    @Override
    public void setValue(Boolean value) {
        this.value = value;
    }

    @Override
    public void nextValue() {
        value = !value;
    }

    @Override
    public void previousValue() {
        value = !value;
    }
}
