package com.jamesswafford.chess4j.board.squares;

import junit.framework.Assert;

import org.junit.Test;

import com.jamesswafford.chess4j.board.squares.East;
import com.jamesswafford.chess4j.board.squares.North;
import com.jamesswafford.chess4j.board.squares.NorthEast;
import com.jamesswafford.chess4j.board.squares.NorthWest;
import com.jamesswafford.chess4j.board.squares.South;
import com.jamesswafford.chess4j.board.squares.SouthEast;
import com.jamesswafford.chess4j.board.squares.SouthWest;
import com.jamesswafford.chess4j.board.squares.West;

public class DirectionTest {

	@Test
	public void testDirectionToNorth() {
		Square from = Square.valueOf(File.FILE_E, Rank.RANK_4);
		Square to = Square.valueOf(File.FILE_E,Rank.RANK_6);
		Assert.assertEquals(North.getInstance(),Direction.directionTo[from.value()][to.value()]);
	}
	
	@Test
	public void testDirectionToSouth() {
		Square from = Square.valueOf(File.FILE_E, Rank.RANK_6);
		Square to = Square.valueOf(File.FILE_E,Rank.RANK_4);
		Assert.assertEquals(South.getInstance(),Direction.directionTo[from.value()][to.value()]);
	}

	@Test
	public void testDirectionToEast() {
		Square from = Square.valueOf(File.FILE_E, Rank.RANK_4);
		Square to = Square.valueOf(File.FILE_H,Rank.RANK_4);
		Assert.assertEquals(East.getInstance(),Direction.directionTo[from.value()][to.value()]);
	}

	@Test
	public void testDirectionToWest() {
		Square from = Square.valueOf(File.FILE_E, Rank.RANK_4);
		Square to = Square.valueOf(File.FILE_A,Rank.RANK_4);
		Assert.assertEquals(West.getInstance(),Direction.directionTo[from.value()][to.value()]);
	}

	@Test
	public void testDirectionToNorthEast() {
		Square from = Square.valueOf(File.FILE_E, Rank.RANK_4);
		Square to = Square.valueOf(File.FILE_H,Rank.RANK_7);
		Assert.assertEquals(NorthEast.getInstance(),Direction.directionTo[from.value()][to.value()]);
	}

	@Test
	public void testDirectionToNorthWest() {
		Square from = Square.valueOf(File.FILE_E, Rank.RANK_4);
		Square to = Square.valueOf(File.FILE_C,Rank.RANK_6);
		Assert.assertEquals(NorthWest.getInstance(),Direction.directionTo[from.value()][to.value()]);
	}

	@Test
	public void testDirectionToSouthEast() {
		Square from = Square.valueOf(File.FILE_E, Rank.RANK_4);
		Square to = Square.valueOf(File.FILE_H,Rank.RANK_1);
		Assert.assertEquals(SouthEast.getInstance(),Direction.directionTo[from.value()][to.value()]);
	}

	@Test
	public void testDirectionToSouthWest() {
		Square from = Square.valueOf(File.FILE_E, Rank.RANK_4);
		Square to = Square.valueOf(File.FILE_B,Rank.RANK_1);
		Assert.assertEquals(SouthWest.getInstance(),Direction.directionTo[from.value()][to.value()]);
	}

	@Test
	public void testDirectionToNone() {
		Square from = Square.valueOf(File.FILE_E, Rank.RANK_4);
		Square to = Square.valueOf(File.FILE_A,Rank.RANK_1);
		Assert.assertNull(Direction.directionTo[from.value()][to.value()]);
	}

}
