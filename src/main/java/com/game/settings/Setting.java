package com.game.settings;

public interface Setting<T> {
    String getName();
    T getValue();
    void setValue(T value);
    void nextValue();
    void previousValue();
}
