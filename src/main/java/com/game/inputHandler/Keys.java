package com.game.inputHandler;

public enum Keys {
    Q("q", KeyType.LETTER, "q"),
    W("w", KeyType.LETTER, "w"),
    E("e", KeyType.LETTER, "e"),
    R("r", KeyType.LETTER, "r"),
    T("t", KeyType.LETTER, "t"),
    Y("y", KeyType.LETTER, "y"),
    U("u", KeyType.LETTER, "u"),
    I("i", KeyType.LETTER, "i"),
    O("o", KeyType.LETTER, "o"),
    P("p", KeyType.LETTER, "p"),
    A("a", KeyType.LETTER, "a"),
    S("s", KeyType.LETTER, "s"),
    D("d", KeyType.LETTER, "d"),
    F("f", KeyType.LETTER, "f"),
    G("g", KeyType.LETTER, "g"),
    H("h", KeyType.LETTER, "h"),
    J("j", KeyType.LETTER, "j"),
    K("k", KeyType.LETTER, "k"),
    L("l", KeyType.LETTER, "l"),
    Z("z", KeyType.LETTER, "z"),
    X("x", KeyType.LETTER, "x"),
    C("c", KeyType.LETTER, "c"),
    V("v", KeyType.LETTER, "v"),
    B("b", KeyType.LETTER, "b"),
    N("n", KeyType.LETTER, "n"),
    M("m", KeyType.LETTER, "m"),
    SPACE("space", KeyType.LETTER, "space"),

    GRAVE("`", KeyType.LETTER, "`"),
    MINUS("-", KeyType.LETTER, "-"),
    EQUALS("=", KeyType.LETTER, "="),
    LEFT_BRACKET("[", KeyType.LETTER, "["),
    RIGHT_BRACKET("]", KeyType.LETTER, "]"),
    BACKSLASH("\\", KeyType.LETTER, "\\"),
    SEMICOLON(";", KeyType.LETTER, ";"),
    QUOTES("'", KeyType.LETTER, "'"),
    COMMA(",", KeyType.LETTER, ","),
    PERIOD(".", KeyType.LETTER, "."),
    SLASH("/", KeyType.LETTER, "/"),

    NUM1("1", KeyType.NUMBER, "1"),
    NUM2("2", KeyType.NUMBER, "2"),
    NUM3("3", KeyType.NUMBER, "3"),
    NUM4("4", KeyType.NUMBER, "4"),
    NUM5("5", KeyType.NUMBER, "5"),
    NUM6("6", KeyType.NUMBER, "6"),
    NUM7("7", KeyType.NUMBER, "7"),
    NUM8("8", KeyType.NUMBER, "8"),
    NUM9("9", KeyType.NUMBER, "9"),
    NUM0("0", KeyType.NUMBER, "0"),

    NUMPAD1("numpad 1", KeyType.NUMBER, "numpad 1"),
    NUMPAD2("numpad 2", KeyType.NUMBER, "numpad 2"),
    NUMPAD3("numpad 3", KeyType.NUMBER, "numpad 3"),
    NUMPAD4("numpad 4", KeyType.NUMBER, "numpad 4"),
    NUMPAD5("numpad 5", KeyType.NUMBER, "numpad 5"),
    NUMPAD6("numpad 6", KeyType.NUMBER, "numpad 6"),
    NUMPAD7("numpad 7", KeyType.NUMBER, "numpad 7"),
    NUMPAD8("numpad 8", KeyType.NUMBER, "numpad 8"),
    NUMPAD9("numpad 9", KeyType.NUMBER, "numpad 9"),
    NUMPAD0("numpad 0", KeyType.NUMBER, "numpad 0"),

    FUNCTION1("f1", KeyType.FUNCTION, "f1"),
    FUNCTION2("f2", KeyType.FUNCTION, "f2"),
    FUNCTION3("f3", KeyType.FUNCTION, "f3"),
    FUNCTION4("f4", KeyType.FUNCTION, "f4"),
    FUNCTION5("f5", KeyType.FUNCTION, "f5"),
    FUNCTION6("f6", KeyType.FUNCTION, "f6"),
    FUNCTION7("f7", KeyType.FUNCTION, "f7"),
    FUNCTION8("f8", KeyType.FUNCTION, "f8"),
    FUNCTION9("f9", KeyType.FUNCTION, "f9"),
    FUNCTION10("f10", KeyType.FUNCTION, "f10"),
    FUNCTION11("f11", KeyType.FUNCTION, "f11"),
    FUNCTION12("f12", KeyType.FUNCTION, "f12"),

    LEFT_CONTROL("left control", KeyType.SPECIAL, "l-ctrl"),
    LEFT_SHIFT("left shift", KeyType.SPECIAL, "l-shift"),
    RIGHT_CONTROL("right control", KeyType.SPECIAL, "r-ctrl"),
    RIGHT_SHIFT("right shift", KeyType.SPECIAL, "r-shift"),
    CAPS_LOCK("caps lock", KeyType.SPECIAL, "caps lock"),
    TAB("tab", KeyType.SPECIAL, "tab"),
    LEFT_ALT("left alt", KeyType.SPECIAL, "l-alt"),
    RIGHT_ALT("right alt", KeyType.SPECIAL, "r-alt"),
    ENTER("enter", KeyType.SPECIAL, "enter"),
    BACKSPACE("backspace", KeyType.SPECIAL, "delete"),
    DELETE("delete", KeyType.SPECIAL, "forward delete"),
    UP("up", KeyType.SPECIAL, "up"),
    DOWN("down", KeyType.SPECIAL, "down"),
    LEFT("left", KeyType.SPECIAL, "left"),
    RIGHT("right", KeyType.SPECIAL, "right"),
    ESCAPE("escape", KeyType.SPECIAL, "escape"),
    NONE("none", KeyType.SPECIAL, "none");


    private final String string;
    private final KeyType type;
    private final String keyHandlerString;

    Keys(String string, KeyType keyType, String keyHandlerString) {
        this.string = string;
        type = keyType;
        this.keyHandlerString = keyHandlerString;
    }

    public String getString() {
        return string;
    }

    public String getKeyHandlerString() {
        return keyHandlerString;
    }

    public KeyType getType() {
        return type;
    }

    public static Keys fromValue(String value) {
        for (Keys key : Keys.values()) {
            if (value.equalsIgnoreCase(key.string)) {
                return key;
            }
        }
        return null;
    }
}
