package com.jamesswafford.chess4j.utils;

import junit.framework.Assert;

import org.junit.Test;

import com.jamesswafford.chess4j.pieces.Bishop;
import com.jamesswafford.chess4j.pieces.Pawn;
import com.jamesswafford.chess4j.pieces.Queen;
import com.jamesswafford.chess4j.pieces.Rook;

public class PieceFactoryTest {

	@Test
	public void testGetPiece() {
		Assert.assertEquals(Pawn.WHITE_PAWN, PieceFactory.getPiece('P'));
		Assert.assertEquals(Pawn.BLACK_PAWN, PieceFactory.getPiece('p'));
		
		Assert.assertEquals(Rook.WHITE_ROOK, PieceFactory.getPiece('R'));
		Assert.assertEquals(Bishop.BLACK_BISHOP, PieceFactory.getPiece('b'));
		
		Assert.assertEquals(Queen.BLACK_QUEEN, PieceFactory.getPiece('Q',false));
	}
}
