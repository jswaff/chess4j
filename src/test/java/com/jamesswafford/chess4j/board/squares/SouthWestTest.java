package com.jamesswafford.chess4j.board.squares;

import junit.framework.Assert;

import org.junit.Test;

import com.jamesswafford.chess4j.board.squares.File;
import com.jamesswafford.chess4j.board.squares.Rank;
import com.jamesswafford.chess4j.board.squares.SouthWest;
import com.jamesswafford.chess4j.board.squares.Square;


public class SouthWestTest {

    @Test
    public void testNext() {
        Assert.assertFalse(SouthWest.getInstance().next(Square.valueOf(File.FILE_A, Rank.RANK_7)).isPresent());
        Assert.assertFalse(SouthWest.getInstance().next(Square.valueOf(File.FILE_B, Rank.RANK_1)).isPresent());

        Assert.assertEquals(SouthWest.getInstance().next(
                Square.valueOf(File.FILE_C, Rank.RANK_6)).get(),
                Square.valueOf(File.FILE_B, Rank.RANK_5));

        Assert.assertEquals(SouthWest.getInstance().next(
                Square.valueOf(File.FILE_D, Rank.RANK_3)).get(),
                Square.valueOf(File.FILE_C, Rank.RANK_2));
    }

}
