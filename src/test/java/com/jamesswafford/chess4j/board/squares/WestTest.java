package com.jamesswafford.chess4j.board.squares;

import junit.framework.Assert;

import org.junit.Test;

import com.jamesswafford.chess4j.board.squares.File;
import com.jamesswafford.chess4j.board.squares.Rank;
import com.jamesswafford.chess4j.board.squares.Square;
import com.jamesswafford.chess4j.board.squares.West;


public class WestTest {

	@Test
	public void testNext() {
		Assert.assertNull(West.getInstance().next(Square.valueOf(File.FILE_A, Rank.RANK_8)));
		
		Assert.assertEquals(West.getInstance().next(
				Square.valueOf(File.FILE_C, Rank.RANK_6)), 
				Square.valueOf(File.FILE_B, Rank.RANK_6));
		
		Assert.assertEquals(West.getInstance().next(
				Square.valueOf(File.FILE_G, Rank.RANK_3)), 
				Square.valueOf(File.FILE_F, Rank.RANK_3));
	}

}
