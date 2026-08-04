package com.game.utils;

import com.badlogic.gdx.utils.Align;

public enum StringAlignment {
    TOP_LEFT(Align.top, Align.left),
    TOP_MIDDLE(Align.top, Align.center),
    TOP_RIGHT(Align.top, Align.right),

    CENTER_LEFT(Align.center, Align.left),
    CENTER_MIDDLE(Align.center, Align.center),
    CENTER_RIGHT(Align.center, Align.right),

    BOTTOM_LEFT(Align.bottom, Align.left),
    BOTTOM_MIDDLE(Align.bottom, Align.center),
    BOTTOM_RIGHT(Align.bottom, Align.right);

    private final int VerticalAlign;
    private final int HorizontalAlign;

    StringAlignment(int VerticalAlign, int HorizontalAlign) {
        this.VerticalAlign = VerticalAlign;
        this.HorizontalAlign = HorizontalAlign;
    }

    public int getVerticalAlign() {
        return VerticalAlign;
    }

    public int getHorizontalAlign() {
        return HorizontalAlign;
    }
}
