package entity;

public class numUtil {
    public static double LTD(long num){
        return num/100000000.0;
    }
    public static long DTL(double num){
        return Math.round(num * 100000000.0);
    }
}
