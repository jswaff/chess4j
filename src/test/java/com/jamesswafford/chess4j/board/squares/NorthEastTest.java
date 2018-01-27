package com.jamesswafford.chess4j.board.squares;

import junit.framework.Assert;

import org.junit.Test;

import com.jamesswafford.chess4j.board.squares.File;
import com.jamesswafford.chess4j.board.squares.NorthEast;
import com.jamesswafford.chess4j.board.squares.Rank;
import com.jamesswafford.chess4j.board.squares.Square;


public class NorthEastTest {

    @Test
    public void testNext() {
        Assert.assertFalse(NorthEast.getInstance().next(Square.valueOf(File.FILE_B, Rank.RANK_8)).isPresent());

        Assert.assertEquals(NorthEast.getInstance().next(
                Square.valueOf(File.FILE_C, Rank.RANK_6)).get(),
                Square.valueOf(File.FILE_D, Rank.RANK_7));

        Assert.assertEquals(NorthEast.getInstance().next(
                Square.valueOf(File.FILE_A, Rank.RANK_3)).get(),
                Square.valueOf(File.FILE_B, Rank.RANK_4));
    }
}
