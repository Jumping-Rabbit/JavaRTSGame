package com.game.entity.unit;

/**
 * @param attackSpeed in ticks
 */
public record UnitStats(long speed, long turnSpeed, long damage, long attackSpeed) {
}
