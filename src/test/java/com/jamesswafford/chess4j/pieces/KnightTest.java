package com.jamesswafford.chess4j.pieces;

import junit.framework.Assert;

import org.junit.Test;

import com.jamesswafford.chess4j.Color;
import com.jamesswafford.chess4j.pieces.Knight;


public class KnightTest {

	@Test
	public void testColor() {
		Assert.assertEquals(Color.WHITE, Knight.WHITE_KNIGHT.getColor());
		Assert.assertEquals(Color.BLACK, Knight.BLACK_KNIGHT.getColor());
		Assert.assertTrue(Knight.WHITE_KNIGHT.isWhite());
		Assert.assertFalse(Knight.BLACK_KNIGHT.isWhite());
		Assert.assertFalse(Knight.WHITE_KNIGHT.isBlack());
		Assert.assertTrue(Knight.BLACK_KNIGHT.isBlack());
	}
	
	@Test
	public void testToString() {
		Assert.assertEquals("N", Knight.WHITE_KNIGHT.toString());
		Assert.assertEquals("n", Knight.BLACK_KNIGHT.toString());
	}

	@Test
	public void testGetOppositeColorPiece() {
		Assert.assertEquals(Knight.WHITE_KNIGHT, Knight.BLACK_KNIGHT.getOppositeColorPiece());
		Assert.assertEquals(Knight.BLACK_KNIGHT, Knight.WHITE_KNIGHT.getOppositeColorPiece());
	}
	
}
