package inputHandler;

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
    NUM1("1", KeyType.NUMBER, "digit1"),
    NUM2("2", KeyType.NUMBER, "digit2"),
    NUM3("3", KeyType.NUMBER, "digit3"),
    NUM4("4", KeyType.NUMBER, "digit4"),
    NUM5("5", KeyType.NUMBER, "digit5"),
    NUM6("6", KeyType.NUMBER, "digit6"),
    NUM7("7", KeyType.NUMBER, "digit7"),
    NUM8("8", KeyType.NUMBER, "digit8"),
    NUM9("9", KeyType.NUMBER, "digit9"),
    NUM0("0", KeyType.NUMBER, "digit0"),

    CONTROL("control", KeyType.SPECIAL, "control"),
    SHIFT("shift", KeyType.SPECIAL, "shift"),
    TAB("tab", KeyType.SPECIAL, "tab"),
    ALT("alt", KeyType.SPECIAL, "alt"),
    ENTER("enter", KeyType.SPECIAL, "enter"),
    BACKSPACE("backspace", KeyType.SPECIAL, "back_space"),
    UP("up", KeyType.SPECIAL, "up"),
    DOWN("down", KeyType.SPECIAL, "down"),
    LEFT("left", KeyType.SPECIAL, "left"),
    RIGHT("right", KeyType.SPECIAL, "right"),
    ESCAPE("escape", KeyType.SPECIAL, "escape"),
    NONE("none", KeyType.SPECIAL, "none");


    private final String string;
    private final KeyType type;
    private final String keyHandlerString;
    Keys(String string, KeyType keyType, String keyHandlerString){
        this.string = string;
        type = keyType;
        this.keyHandlerString = keyHandlerString;
    }
    public String getString(){
        return string;
    }
    public String getKeyHandlerString(){
        return keyHandlerString;
    }
    public KeyType getType(){
        return type;
    }
    public Keys fromValue(String value){
        for (Keys key : Keys.values()){
            if (value.equals(key.getString())){
                return key;
            }
        }
        return null;
    }
}
