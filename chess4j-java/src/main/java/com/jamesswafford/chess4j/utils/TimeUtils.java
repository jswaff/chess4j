package com.jamesswafford.chess4j.utils;

public class TimeUtils {

    public static int getSearchTime(int remainingTime, int increment) {
        if (remainingTime < 0) {
            remainingTime = 0;
        }
        return (remainingTime / 25) + increment;
    }
}
