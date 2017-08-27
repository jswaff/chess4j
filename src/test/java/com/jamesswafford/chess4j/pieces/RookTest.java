package com.jamesswafford.chess4j.pieces;

import junit.framework.Assert;

import org.junit.Test;

import com.jamesswafford.chess4j.Color;
import com.jamesswafford.chess4j.pieces.Rook;


public class RookTest {

	@Test
	public void testColor() {
		Assert.assertEquals(Color.WHITE, Rook.WHITE_ROOK.getColor());
		Assert.assertEquals(Color.BLACK, Rook.BLACK_ROOK.getColor());
		Assert.assertTrue(Rook.WHITE_ROOK.isWhite());
		Assert.assertFalse(Rook.BLACK_ROOK.isWhite());
		Assert.assertFalse(Rook.WHITE_ROOK.isBlack());
		Assert.assertTrue(Rook.BLACK_ROOK.isBlack());
	}
	
	@Test
	public void testToString() {
		Assert.assertEquals("R", Rook.WHITE_ROOK.toString());
		Assert.assertEquals("r", Rook.BLACK_ROOK.toString());
	}

	@Test
	public void testGetOppositeColorPiece() {
		Assert.assertEquals(Rook.WHITE_ROOK, Rook.BLACK_ROOK.getOppositeColorPiece());
		Assert.assertEquals(Rook.BLACK_ROOK, Rook.WHITE_ROOK.getOppositeColorPiece());
	}

}
