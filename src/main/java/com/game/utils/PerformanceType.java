package com.game.utils;

public enum PerformanceType {
  GENERAL("[GENERAL] "),
  TICK("[TICK] "),
  PHYSICS("[PHYSICS] "),
  HARDWARE("[HARDWARE] ");
  private final String string;
  
  PerformanceType(String s) {
    string = s;
  }
  
  public String getString() {
    return string;
  }
}
