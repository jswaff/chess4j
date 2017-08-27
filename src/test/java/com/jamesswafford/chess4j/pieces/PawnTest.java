package com.jamesswafford.chess4j.pieces;


import org.junit.Test;

import com.jamesswafford.chess4j.Color;
import com.jamesswafford.chess4j.pieces.Pawn;


import junit.framework.Assert;

public class PawnTest {

	@Test
	public void testColor() {
		Assert.assertEquals(Color.WHITE, Pawn.WHITE_PAWN.getColor());
		Assert.assertEquals(Color.BLACK, Pawn.BLACK_PAWN.getColor());
		Assert.assertTrue(Pawn.WHITE_PAWN.isWhite());
		Assert.assertFalse(Pawn.BLACK_PAWN.isWhite());
		Assert.assertFalse(Pawn.WHITE_PAWN.isBlack());
		Assert.assertTrue(Pawn.BLACK_PAWN.isBlack());
	}
	
	@Test
	public void testToString() {
		Assert.assertEquals("P", Pawn.WHITE_PAWN.toString());
		Assert.assertEquals("p", Pawn.BLACK_PAWN.toString());
	}
	
	@Test
	public void testGetOppositeColorPiece() {
		Assert.assertEquals(Pawn.WHITE_PAWN, Pawn.BLACK_PAWN.getOppositeColorPiece());
		Assert.assertEquals(Pawn.BLACK_PAWN, Pawn.WHITE_PAWN.getOppositeColorPiece());
	}

}
