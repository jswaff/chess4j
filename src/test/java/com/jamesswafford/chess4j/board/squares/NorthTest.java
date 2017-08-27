package com.jamesswafford.chess4j.board.squares;

import junit.framework.Assert;

import org.junit.Test;

import com.jamesswafford.chess4j.board.squares.File;
import com.jamesswafford.chess4j.board.squares.North;
import com.jamesswafford.chess4j.board.squares.Rank;
import com.jamesswafford.chess4j.board.squares.Square;


public class NorthTest {

	@Test
	public void testNext() {
		Assert.assertNull(North.getInstance().next(Square.valueOf(File.FILE_B, Rank.RANK_8)));
		
		Assert.assertEquals(North.getInstance().next(
				Square.valueOf(File.FILE_C, Rank.RANK_6)), 
				Square.valueOf(File.FILE_C, Rank.RANK_7));
		
		Assert.assertEquals(North.getInstance().next(
				Square.valueOf(File.FILE_A, Rank.RANK_3)), 
				Square.valueOf(File.FILE_A, Rank.RANK_4));
		
	}
}
