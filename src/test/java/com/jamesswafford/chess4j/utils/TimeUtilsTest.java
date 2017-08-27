package com.jamesswafford.chess4j.utils;

import org.junit.Test;

import junit.framework.Assert;

public class TimeUtilsTest {

	@Test
	public void testGetSearchTime() {
		Assert.assertEquals(4, TimeUtils.getSearchTime(100, 0));
		Assert.assertEquals(3, TimeUtils.getSearchTime(99, 0));
		Assert.assertEquals(1, TimeUtils.getSearchTime(25, 0));
		Assert.assertEquals(0, TimeUtils.getSearchTime(24, 0));
		Assert.assertEquals(3, TimeUtils.getSearchTime(0, 3));
		Assert.assertEquals(3, TimeUtils.getSearchTime(-1, 3));
		Assert.assertEquals(2, TimeUtils.getSearchTime(-25, 2));
	}
}
