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
        Assert.assertFalse(SouthEast.getInstance().next(Square.valueOf(File.FILE_H, Rank.RANK_7)).isPresent());
        Assert.assertFalse(SouthEast.getInstance().next(Square.valueOf(File.FILE_B, Rank.RANK_1)).isPresent());

        Assert.assertEquals(SouthEast.getInstance().next(
                Square.valueOf(File.FILE_C, Rank.RANK_6)).get(),
                Square.valueOf(File.FILE_D, Rank.RANK_5));

        Assert.assertEquals(SouthEast.getInstance().next(
                Square.valueOf(File.FILE_A, Rank.RANK_3)).get(),
                Square.valueOf(File.FILE_B, Rank.RANK_2));
    }

}
