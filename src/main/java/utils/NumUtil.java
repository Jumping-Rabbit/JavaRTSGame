package utils;

public class NumUtil {
    public static double LTD(long num){
        return num/10000.0;
    }
    public static long DTL(double num){
        return StrictMath.round(num * 10000.0);
    }
    public static double interpolate(double start, double end, double factor){
        return end * factor + start * (1 - factor);
    }

    public static final long SCALER = 10000L;

    private static final int CACHE_SIZE = 8192;
    private static final int MASK = CACHE_SIZE - 1;


    private static final double INDEX_SCALE = (double) CACHE_SIZE / (360.0 * SCALER);
    private static final double[] SIN_CACHE = new double[CACHE_SIZE];

    static {
        long startTime = System.nanoTime();
        for (int i = 0; i < CACHE_SIZE; i++) {
            double angleInDegrees = i / INDEX_SCALE / SCALER;
            SIN_CACHE[i] = StrictMath.sin(StrictMath.toRadians(angleInDegrees));
        }
        System.out.println("sin cache: " + (System.nanoTime()-startTime)/1000000d);
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
    public static long sqrtFast(long num){
        if (num <= 0) return 0;
        long processingValue = num;// * 1000000L;
        long x = 1L << (64 - Long.numberOfLeadingZeros(processingValue) + 1) / 2;
        x = (x + processingValue / x) >> 1;
        return x;
    }


//    public static long atan2(long y, long x) {
//        if (x == 0 && y == 0) return 0;
//
//        long absX = Math.abs(x);
//        long absY = Math.abs(y);
//        boolean flip = absY > absX;
//
//        long ratio = flip ? (absX * SCALER) / absY : (absY * SCALER) / absX;
//
//        long s = (ratio * ratio) / SCALER;
//
//        long term1 = (-4650L * s) / SCALER + 15931L;
//        long term2 = (term1 * s) / SCALER - 32762L;
//        long term3 = (term2 * s) / SCALER;
//        long angle = (term3 * ratio) / SCALER + ratio;
//
//        angle = (angle * 572957L) / SCALER;
//        long scaled180 = 180 * SCALER;
//
//        if (flip) angle = (90 * SCALER) - angle;
//
//        if (x < 0) {
//            if (y >= 0) angle = scaled180 - angle;
//            else angle = -scaled180 + angle;
//        } else if (y < 0) {
//            angle = -angle;
//        }
//
//        return angle;
//    }

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
