package com.jamesswafford.chess4j.board.squares;

import junit.framework.Assert;

import org.junit.Test;

import com.jamesswafford.chess4j.board.squares.File;
import com.jamesswafford.chess4j.board.squares.NorthWest;
import com.jamesswafford.chess4j.board.squares.Rank;
import com.jamesswafford.chess4j.board.squares.Square;


public class NorthWestTest {

    @Test
    public void testNext() {
        Assert.assertFalse(NorthWest.getInstance().next(Square.valueOf(File.FILE_A, Rank.RANK_7)).isPresent());
        Assert.assertFalse(NorthWest.getInstance().next(Square.valueOf(File.FILE_B, Rank.RANK_8)).isPresent());

        Assert.assertEquals(NorthWest.getInstance().next(
                Square.valueOf(File.FILE_C, Rank.RANK_6)).get(),
                Square.valueOf(File.FILE_B, Rank.RANK_7));

        Assert.assertEquals(NorthWest.getInstance().next(
                Square.valueOf(File.FILE_D, Rank.RANK_3)).get(),
                Square.valueOf(File.FILE_C, Rank.RANK_4));
    }

}
