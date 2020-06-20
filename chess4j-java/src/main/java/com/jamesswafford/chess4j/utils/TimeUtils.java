package com.jamesswafford.chess4j.utils;

public class TimeUtils {

    public static int getSearchTime(int remainingTimeMs, int incrementMs) {
        if (remainingTimeMs < 0) {
            remainingTimeMs = 0;
        }
        // if we have an increment, keep a small margin to avoid time losses
        if (incrementMs > 100) {
            incrementMs -= 100;
        } else if (incrementMs > 50) {
            incrementMs /= 2;
        }

        return (remainingTimeMs / 25) + incrementMs;
    }
}
