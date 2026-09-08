package com.darahz.dmod.helpers;

import java.util.Random;

public final class NumberHelper {

    private static final Random RANDOM = new Random();

    /** Inclusive on both ends. Accepts min == max instead of throwing. */
    public static int getRandomNumberInRange(int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("max must not be less than min");
        }
        if (min == max) {
            return min;
        }
        return RANDOM.nextInt((max - min) + 1) + min;
    }

    private NumberHelper() {}
}
