package utils;

public class NumUtil {
    public static final long SCALER = 10000L;
    private static final int CACHE_SIZE = 8192;
    private static final int MASK = CACHE_SIZE - 1;
    private static final double INDEX_SCALE = (double) CACHE_SIZE / (360.0 * SCALER);
    private static final double[] SIN_CACHE = new double[CACHE_SIZE];

    public static double LTD(long num) {
        return num / 10000.0;
    }

    public static long DTL(double num) {
        return StrictMath.round(num * 10000.0);
    }

    public static double unScale(double num){
        return num / 10000.0;
    }

    public static long scale(long num){
            return StrictMath.round(num * 10000.0);
    }

    public static double interpolate(double current, double last, double factor) {
        double value;
        if (current != last) {
            value = current * factor + last * (1 - factor);
        } else {
            value = current;
        }
        return value;
    }

    public static long interpolate(long current, long last, double factor) {
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
            double angleInDegrees = i / INDEX_SCALE / SCALER;
            SIN_CACHE[i] = StrictMath.sin(StrictMath.toRadians(angleInDegrees));
        }
    }

    public static double sin(long scaledDegrees) {
        int index = (int) (scaledDegrees * INDEX_SCALE);
        return SIN_CACHE[index & MASK];
    }

    public static double cos(long scaledDegrees) {
        int index = (int) ((scaledDegrees + (90 * SCALER)) * INDEX_SCALE);
        return SIN_CACHE[index & MASK];
    }

    public static long sqrt(long num) {//TODO: is this faster? testing shows slower
        return DTL(StrictMath.sqrt(LTD(num)));
    }

    public static long sqrtFast(long num) {
        if (num <= 0) return 0;
        long processingValue = num;
        long x = 1L << (64 - Long.numberOfLeadingZeros(processingValue) + 1) / 2;
        x = (x + processingValue / x) >> 1;
        return x;
    }

    public static long sqrtFastScaled(long num) {
        if (num <= 0) return 0;
        long processingValue = num * 10000L;
        long x = 1L << (64 - Long.numberOfLeadingZeros(processingValue) + 1) / 2;
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
