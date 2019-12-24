package com.jamesswafford.chess4j.utils;

import org.junit.Test;

import static org.junit.Assert.*;

import static com.jamesswafford.chess4j.utils.TimeUtils.*;

public class TimeUtilsTest {

    @Test
    public void testGetSearchTime() {
        assertEquals(4, getSearchTime(100, 0));
        assertEquals(3, getSearchTime(99, 0));
        assertEquals(1, getSearchTime(25, 0));
        assertEquals(0, getSearchTime(24, 0));
        assertEquals(3, getSearchTime(0, 3));
        assertEquals(3, getSearchTime(-1, 3));
        assertEquals(2, getSearchTime(-25, 2));
    }
}
