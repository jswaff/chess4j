package com.jamesswafford.chess4j.utils;

import org.junit.Test;

import static org.junit.Assert.*;

import static com.jamesswafford.chess4j.utils.TimeUtils.*;

public class TimeUtilsTest {

    @Test
    public void testGetSearchTime() {
        assertEquals(400, getSearchTime(10000, 0));
        assertEquals(10, getSearchTime(250, 0));
        assertEquals(11910, getSearchTime(250, 12000));
        assertEquals(3, getSearchTime(0, 3));
        assertEquals(11900, getSearchTime(-2, 12000));
        assertEquals(800, getSearchTime(10000, 500));
    }
}
