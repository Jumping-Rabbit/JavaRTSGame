package com.game.utils;

import com.game.entity.Init;

@Init
public class NumUtil {
    public static final long SCALER = 10000L;
    private static final int CACHE_SIZE = 8192;
    private static final int MASK = CACHE_SIZE - 1;
    private static final float INDEX_SCALE = (float) CACHE_SIZE / (360f * SCALER);
    private static final float[] SIN_CACHE = new float[CACHE_SIZE];

    public static float LTF(long num) {
        return num / 10000f;
    }

    public static long FTL(float num) {
        return StrictMath.round(num * 10000.0);
    }

    public static float unScale(float num) {
        return num / 10000f;
    }

    public static long scale(long num) {
        return StrictMath.round(num * 10000f);
    }

    public static float interpolate(float current, float last, float factor) {
        float value;
        if (current != last) {
            value = current * factor + last * (1 - factor);
        } else {
            value = current;
        }
        return value;
    }

    public static long interpolate(long current, long last, float factor) {
        long value;
        if (current != last) {
            value = (long) (current * factor + last * (1 - factor));
        } else {
            value = current;
        }
        return value;
    }

    public static void init() {
        for (int i = 0; i < CACHE_SIZE; i++) {
            float angleInDegrees = i / INDEX_SCALE / SCALER;
            SIN_CACHE[i] = (float) StrictMath.sin(StrictMath.toRadians(angleInDegrees));
        }
    }

    public static float sin(long scaledDegrees) {
        int index = (int) (scaledDegrees * INDEX_SCALE);
        return SIN_CACHE[index & MASK];
    }

    public static float cos(long scaledDegrees) {
        int index = (int) ((scaledDegrees + (90 * SCALER)) * INDEX_SCALE);
        return SIN_CACHE[index & MASK];
    }

    public static long sqrt(long num) {//TODO: is this faster? testing shows slower
        return NumUtil.FTL((float) StrictMath.sqrt(LTF(num)));
    }

    public static long sqrtFast(long num) {
        if (num <= 0) return 0;
        long processingValue = num;
        long x = 1L << (64 - Long.numberOfLeadingZeros(processingValue) + 1) / 2;
        x = (x + processingValue / x) >> 1;
        x = (x + processingValue / x) >> 1;
        x = (x + processingValue / x) >> 1;
        return x;
    }

    public static long sqrtFastScaled(long num) {
        if (num <= 0) return 0;
        long processingValue = num * 10000L;
        long x = 1L << (64 - Long.numberOfLeadingZeros(processingValue) + 1) / 2;
        x = (x + processingValue / x) >> 1;
        x = (x + processingValue / x) >> 1;
        x = (x + processingValue / x) >> 1;
        return x;
    }

    public static long atan2(long y, long x) {
        if (x == 0 && y == 0) return 0;

        long absX = x < 0 ? -x : x;
        long absY = y < 0 ? -y : y;

        boolean flip = absY > absX;
        long ratio = flip ? (absX * SCALER) / absY : (absY * SCALER) / absX;

        long r3 = (ratio * ratio) / SCALER;
        r3 = (r3 * ratio) / SCALER;

        long angle = (572958L * ratio) - (122958L * r3);
        angle = angle / SCALER;
        if (flip) angle = 900000L - angle;

        if (x < 0) {
            if (y >= 0) angle = 1800000L - angle;
            else angle = -1800000L + angle;
        } else if (y < 0) {
            angle = -angle;
        }

        return angle;
    }
}
