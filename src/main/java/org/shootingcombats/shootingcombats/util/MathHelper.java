package org.shootingcombats.shootingcombats.util;

public class MathHelper {
    private MathHelper () {
        throw new AssertionError("Attempt to make instance of utility class " + getClass());
    }

    public static int clump(int value, int min, int max) {
        if (value > max) {
            return max;
        }
        if (value < min) {
            return min;
        }
        return value;
    }
}
