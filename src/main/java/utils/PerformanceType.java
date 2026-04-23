package utils;

public enum PerformanceType {
    GENERAL("[GENERAL] "),
    TICK("[TICK] "),
    PHYSICS("[PHYSICS] "),
    HARDWARE("[HARDWARE] ");
    private String string;
    PerformanceType(String s) {
        string = s;
    }

    public String getString() {
        return string;
    }
}
