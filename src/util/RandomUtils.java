package util;

import java.util.Random;

public class RandomUtils {

    private static Random random = new Random();

    public static int nextInt(int min, int max) {
        return random.nextInt((max - min) + 1) + min;
    }

    public static double nextDouble() {
        return random.nextDouble();
    }

    public static double nextDouble(double min, double max) {
        return min + (max - min) * random.nextDouble();
    }
}
