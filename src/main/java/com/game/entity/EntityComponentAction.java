package com.game.entity;

public class EntityComponentAction {
  public static final long setX = 1L << 1;
  public static final long setY = 1L << 2;
  public static final long setDirection = 1L << 3;
  public static final long select = 1L << 4;
  public static final long kill = 1L << 5;
  public static final long changeHp = 1L << 6;
  public static final long makeUnit = 1L << 7;
  public static final long setTag = 1L << 8;
  public static final long addTag = 1L << 9;
  public static final long setAbility = 1L << 10;
  public static final long addAbility = 1L << 11;
  public static final long setEffect = 1L << 12;
  public static final long removeEffect = 1L << 13;
  public static final long addEffect = 1L << 14;
}
