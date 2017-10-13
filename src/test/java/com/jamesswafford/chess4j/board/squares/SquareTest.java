package com.jamesswafford.chess4j.board.squares;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.jamesswafford.chess4j.board.squares.File;
import com.jamesswafford.chess4j.board.squares.Rank;
import com.jamesswafford.chess4j.board.squares.Square;

import junit.framework.Assert;

public class SquareTest {

	@Test
	public void getValue() {
		Assert.assertEquals(0, Square.valueOf(File.FILE_A, Rank.RANK_8).value());
		Assert.assertEquals(7, Square.valueOf(File.FILE_H, Rank.RANK_8).value());
		Assert.assertEquals(56, Square.valueOf(File.FILE_A, Rank.RANK_1).value());
		Assert.assertEquals(63, Square.valueOf(File.FILE_H, Rank.RANK_1).value());
		Assert.assertEquals(42, Square.valueOf(File.FILE_C, Rank.RANK_3).value());
		Assert.assertEquals(36, Square.valueOf(File.FILE_E, Rank.RANK_4).value());
	}
	
	@Test
	public void testToString() {
		Assert.assertEquals("a8", Square.valueOf(File.FILE_A, Rank.RANK_8).toString());
		Assert.assertEquals("h8", Square.valueOf(File.FILE_H, Rank.RANK_8).toString());
		Assert.assertEquals("a1", Square.valueOf(File.FILE_A, Rank.RANK_1).toString());
		Assert.assertEquals("h1", Square.valueOf(File.FILE_H, Rank.RANK_1).toString());
		Assert.assertEquals("c3", Square.valueOf(File.FILE_C, Rank.RANK_3).toString());
		Assert.assertEquals("e4", Square.valueOf(File.FILE_E, Rank.RANK_4).toString());
	}
	
	@Test
	public void testFileSquares() {
		Assert.assertTrue(Square.fileSquares(File.FILE_B).contains(Square.valueOf(File.FILE_B, Rank.RANK_2)));
		Assert.assertFalse(Square.fileSquares(File.FILE_B).contains(Square.valueOf(File.FILE_C, Rank.RANK_2)));
	}
	
	@Test
	public void testRankSquares() {
		Assert.assertTrue(Square.rankSquares(Rank.RANK_2).contains(Square.valueOf(File.FILE_B, Rank.RANK_2)));
		Assert.assertFalse(Square.rankSquares(Rank.RANK_2).contains(Square.valueOf(File.FILE_B, Rank.RANK_3)));
	}

	@Test
	public void testFlipVertical() {
		Assert.assertEquals(Square.valueOf(File.FILE_A, Rank.RANK_8), Square.valueOf(File.FILE_A, Rank.RANK_1).flipVertical());
		Assert.assertEquals(Square.valueOf(File.FILE_E, Rank.RANK_2), Square.valueOf(File.FILE_E, Rank.RANK_7).flipVertical());
		Assert.assertEquals(Square.valueOf(File.FILE_C, Rank.RANK_6), Square.valueOf(File.FILE_C, Rank.RANK_3).flipVertical());
		Assert.assertEquals(Square.valueOf(File.FILE_B, Rank.RANK_5), Square.valueOf(File.FILE_B, Rank.RANK_4).flipVertical());
		Assert.assertEquals(Square.valueOf(File.FILE_H, Rank.RANK_7), Square.valueOf(File.FILE_H, Rank.RANK_2).flipVertical());
	}
	
	@Test
	public void testFlipHorizontal() {
		Assert.assertEquals(Square.valueOf(File.FILE_A, Rank.RANK_8), Square.valueOf(File.FILE_H, Rank.RANK_8).flipHorizontal());
		Assert.assertEquals(Square.valueOf(File.FILE_E, Rank.RANK_2), Square.valueOf(File.FILE_D, Rank.RANK_2).flipHorizontal());
		Assert.assertEquals(Square.valueOf(File.FILE_C, Rank.RANK_6), Square.valueOf(File.FILE_F, Rank.RANK_6).flipHorizontal());
		Assert.assertEquals(Square.valueOf(File.FILE_B, Rank.RANK_5), Square.valueOf(File.FILE_G, Rank.RANK_5).flipHorizontal());
		Assert.assertEquals(Square.valueOf(File.FILE_H, Rank.RANK_7), Square.valueOf(File.FILE_A, Rank.RANK_7).flipHorizontal());
	}
	
	@Test
	public void testIsLightSquare() {
		Assert.assertTrue(Square.valueOf(File.FILE_A, Rank.RANK_8).isLight());
		Assert.assertFalse(Square.valueOf(File.FILE_B, Rank.RANK_8).isLight());
		Assert.assertTrue(Square.valueOf(File.FILE_C, Rank.RANK_8).isLight());
		Assert.assertFalse(Square.valueOf(File.FILE_A, Rank.RANK_7).isLight());
		Assert.assertTrue(Square.valueOf(File.FILE_B, Rank.RANK_7).isLight());
		Assert.assertFalse(Square.valueOf(File.FILE_C, Rank.RANK_7).isLight());
		Assert.assertTrue(Square.valueOf(File.FILE_A, Rank.RANK_6).isLight());
		Assert.assertFalse(Square.valueOf(File.FILE_B, Rank.RANK_6).isLight());
		Assert.assertTrue(Square.valueOf(File.FILE_C, Rank.RANK_6).isLight());
	}
	
	@Test
	public void testValueOf() {
		Assert.assertEquals(Square.valueOf(File.FILE_A, Rank.RANK_8), Square.valueOf(0));
		Assert.assertEquals(Square.valueOf(File.FILE_H, Rank.RANK_8), Square.valueOf(7));
		Assert.assertEquals(Square.valueOf(File.FILE_A, Rank.RANK_1), Square.valueOf(56));
		Assert.assertEquals(Square.valueOf(File.FILE_H, Rank.RANK_1), Square.valueOf(63));
		Assert.assertEquals(Square.valueOf(File.FILE_C, Rank.RANK_3), Square.valueOf(42));
		Assert.assertEquals(Square.valueOf(File.FILE_E, Rank.RANK_4), Square.valueOf(36));
	}
	
	@Test
	public void testHashCodes() {
		Set<Integer> hashCodes = new HashSet<Integer>();
		
		List<Square> squares = Square.allSquares();
		Assert.assertEquals(64, squares.size());
		
		for (Square sq : squares) {
			hashCodes.add(sq.hashCode());
		}
		
		Assert.assertEquals(64, hashCodes.size());
	}

	@Test
	public void testFileDistance() {
		Assert.assertEquals(1,Square.valueOf(File.FILE_A,Rank.RANK_1).fileDistance(Square.valueOf(File.FILE_B,Rank.RANK_1)));
		Assert.assertEquals(7,Square.valueOf(File.FILE_A,Rank.RANK_1).fileDistance(Square.valueOf(File.FILE_H,Rank.RANK_8)));
		Assert.assertEquals(7,Square.valueOf(File.FILE_A,Rank.RANK_1).fileDistance(Square.valueOf(File.FILE_H,Rank.RANK_4)));
		Assert.assertEquals(1,Square.valueOf(File.FILE_B,Rank.RANK_3).fileDistance(Square.valueOf(File.FILE_C,Rank.RANK_7)));
		Assert.assertEquals(7,Square.valueOf(File.FILE_H,Rank.RANK_2).fileDistance(Square.valueOf(File.FILE_A,Rank.RANK_3)));
		Assert.assertEquals(4,Square.valueOf(File.FILE_F,Rank.RANK_3).fileDistance(Square.valueOf(File.FILE_B,Rank.RANK_8)));
		Assert.assertEquals(0,Square.valueOf(File.FILE_E,Rank.RANK_1).fileDistance(Square.valueOf(File.FILE_E,Rank.RANK_2)));
	}

	@Test
	public void testRankDistance() {
		Assert.assertEquals(0,Square.valueOf(File.FILE_A,Rank.RANK_1).rankDistance(Square.valueOf(File.FILE_B,Rank.RANK_1)));
		Assert.assertEquals(3,Square.valueOf(File.FILE_A,Rank.RANK_1).rankDistance(Square.valueOf(File.FILE_C,Rank.RANK_4)));
		Assert.assertEquals(1,Square.valueOf(File.FILE_H,Rank.RANK_8).rankDistance(Square.valueOf(File.FILE_A,Rank.RANK_7)));
		Assert.assertEquals(7,Square.valueOf(File.FILE_G,Rank.RANK_8).rankDistance(Square.valueOf(File.FILE_B,Rank.RANK_1)));
	}


	@Test
	public void testDistance() {
		Assert.assertEquals(0,Square.valueOf(File.FILE_E,Rank.RANK_4).distance(Square.valueOf(File.FILE_E,Rank.RANK_4)));
		Assert.assertEquals(7,Square.valueOf(File.FILE_A,Rank.RANK_1).distance(Square.valueOf(File.FILE_A,Rank.RANK_8)));
		Assert.assertEquals(3,Square.valueOf(File.FILE_H,Rank.RANK_1).distance(Square.valueOf(File.FILE_E,Rank.RANK_4)));
		Assert.assertEquals(4,Square.valueOf(File.FILE_H,Rank.RANK_8).distance(Square.valueOf(File.FILE_E,Rank.RANK_4)));
	}
}
