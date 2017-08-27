package com.jamesswafford.chess4j.pieces;

import junit.framework.Assert;

import org.junit.Test;

import com.jamesswafford.chess4j.Color;
import com.jamesswafford.chess4j.pieces.King;


public class KingTest {

	@Test
	public void testColor() {
		Assert.assertEquals(Color.WHITE, King.WHITE_KING.getColor());
		Assert.assertEquals(Color.BLACK, King.BLACK_KING.getColor());
		Assert.assertTrue(King.WHITE_KING.isWhite());
		Assert.assertFalse(King.BLACK_KING.isWhite());
		Assert.assertFalse(King.WHITE_KING.isBlack());
		Assert.assertTrue(King.BLACK_KING.isBlack());
	}
	
	@Test
	public void testToString() {
		Assert.assertEquals("K", King.WHITE_KING.toString());
		Assert.assertEquals("k", King.BLACK_KING.toString());
	}

	@Test
	public void testGetOppositeColorPiece() {
		Assert.assertEquals(King.WHITE_KING, King.BLACK_KING.getOppositeColorPiece());
		Assert.assertEquals(King.BLACK_KING, King.WHITE_KING.getOppositeColorPiece());
	}

}
