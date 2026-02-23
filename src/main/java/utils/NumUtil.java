package utils;

public class NumUtil {
    public static double LTD(long num){
        return num/1000000.0;
    }
    public static long DTL(double num){
        return StrictMath.round(num * 1000000.0);
    }
    public static double interpolate(double start, double end, double factor){
        return end * factor + start * (1 - factor);
    }

    private static final int CACHE_SIZE = 4096;
    private static final int MASK = CACHE_SIZE - 1;

    private static final double SCALE = CACHE_SIZE / 360.0;
    private static final double[] SIN_CACHE = new double[CACHE_SIZE];

    static {
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < CACHE_SIZE; i++) {
            double angle = i / SCALE;
            SIN_CACHE[i] = StrictMath.sin(StrictMath.toRadians(angle));
        }
        System.out.println("trig cache time: " + (System.currentTimeMillis()-startTime));
    }

    public static double sin(double degrees) {
        int index = (int) (degrees * SCALE);
        return SIN_CACHE[index & MASK];
    }

    public static double cos(double degrees) {
        int index = (int) ((degrees + 90.0) * SCALE);
        return SIN_CACHE[index & MASK];
    }
    public static long sqrt(long n) {
        if (n <= 0) return 0;
        long x = n;
        long y = (x + 1) / 2;
        while (y < x) {
            x = y;
            y = (x + n / x) / 2;
        }
        return x;
    }
    public static double atan2(double y, double x) {
        if (x == 0 && y == 0) return 0;
        double absX = Math.abs(x);
        double absY = Math.abs(y);
        boolean flip = absY > absX;
        double ratio = flip ? absX / absY : absY / absX;
        double s = ratio * ratio;
        double angle = ((-0.0464964749 * s + 0.15931422) * s - 0.327622764) * s * ratio + ratio;
        angle *= 57.29577951308232;
        if (flip) angle = 90.0 - angle;
        if (x < 0) angle = 180.0 - angle;
        if (y < 0) angle = -angle;
        return angle;
    }
}
