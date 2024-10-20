package dev.jamesswafford.chess4j.utils;

import org.junit.Assert;
import org.junit.Test;

public class TimeUtilsTest {

    @Test
    public void testGetSearchTime() {
        Assert.assertEquals(400, TimeUtils.getSearchTime(10000, 0));
        Assert.assertEquals(10, TimeUtils.getSearchTime(250, 0));
        Assert.assertEquals(11910, TimeUtils.getSearchTime(250, 12000));
        Assert.assertEquals(3, TimeUtils.getSearchTime(0, 3));
        Assert.assertEquals(11900, TimeUtils.getSearchTime(-2, 12000));
        Assert.assertEquals(800, TimeUtils.getSearchTime(10000, 500));
    }
}
