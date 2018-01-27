package com.jamesswafford.chess4j.board.squares;

import junit.framework.Assert;

import org.junit.Test;

import com.jamesswafford.chess4j.board.squares.File;
import com.jamesswafford.chess4j.board.squares.Rank;
import com.jamesswafford.chess4j.board.squares.South;
import com.jamesswafford.chess4j.board.squares.Square;


public class SouthTest {

    @Test
    public void testNext() {
        Assert.assertFalse(South.getInstance().next(Square.valueOf(File.FILE_B, Rank.RANK_1)).isPresent());

        Assert.assertEquals(South.getInstance().next(
                Square.valueOf(File.FILE_C, Rank.RANK_6)).get(),
                Square.valueOf(File.FILE_C, Rank.RANK_5));

        Assert.assertEquals(South.getInstance().next(
                Square.valueOf(File.FILE_A, Rank.RANK_3)).get(),
                Square.valueOf(File.FILE_A, Rank.RANK_2));

    }

}
