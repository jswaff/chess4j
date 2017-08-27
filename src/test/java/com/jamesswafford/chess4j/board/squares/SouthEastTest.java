package com.jamesswafford.chess4j.board.squares;

import junit.framework.Assert;

import org.junit.Test;

import com.jamesswafford.chess4j.board.squares.File;
import com.jamesswafford.chess4j.board.squares.Rank;
import com.jamesswafford.chess4j.board.squares.SouthEast;
import com.jamesswafford.chess4j.board.squares.Square;


public class SouthEastTest {

	@Test
	public void testNext() {
		Assert.assertNull(SouthEast.getInstance().next(Square.valueOf(File.FILE_H, Rank.RANK_7)));
		Assert.assertNull(SouthEast.getInstance().next(Square.valueOf(File.FILE_B, Rank.RANK_1)));
		
		Assert.assertEquals(SouthEast.getInstance().next(
				Square.valueOf(File.FILE_C, Rank.RANK_6)), 
				Square.valueOf(File.FILE_D, Rank.RANK_5));
		
		Assert.assertEquals(SouthEast.getInstance().next(
				Square.valueOf(File.FILE_A, Rank.RANK_3)), 
				Square.valueOf(File.FILE_B, Rank.RANK_2));
	}
	
}
