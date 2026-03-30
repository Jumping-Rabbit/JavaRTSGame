package utils;

import javafx.geometry.VPos;
import javafx.scene.text.TextAlignment;

public enum StringAlignment {
    TOP_LEFT(VPos.TOP, TextAlignment.LEFT),
    TOP_MIDDLE(VPos.TOP, TextAlignment.CENTER),
    TOP_RIGHT(VPos.TOP, TextAlignment.RIGHT),

    CENTER_LEFT(VPos.CENTER, TextAlignment.LEFT),
    CENTER_MIDDLE(VPos.CENTER, TextAlignment.CENTER),
    CENTER_RIGHT(VPos.CENTER, TextAlignment.RIGHT),

    BOTTOM_LEFT(VPos.BOTTOM, TextAlignment.LEFT),
    BOTTOM_MIDDLE(VPos.BOTTOM, TextAlignment.CENTER),
    BOTTOM_RIGHT(VPos.BOTTOM, TextAlignment.RIGHT);

    private final VPos vPos;
    private final TextAlignment textAlignment;

    StringAlignment(VPos vPos, TextAlignment textAlignment) {
        this.vPos = vPos;
        this.textAlignment = textAlignment;
    }

    public TextAlignment getTextAlignment() {
        return textAlignment;
    }

    public VPos getVPos() {
        return vPos;
    }
}