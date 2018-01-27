package com.jamesswafford.chess4j.board.squares;

import junit.framework.Assert;

import org.junit.Test;

import com.jamesswafford.chess4j.board.squares.East;
import com.jamesswafford.chess4j.board.squares.File;
import com.jamesswafford.chess4j.board.squares.Rank;
import com.jamesswafford.chess4j.board.squares.Square;


public class EastTest {

    @Test
    public void testNext() {
        Assert.assertFalse(East.getInstance().next(Square.valueOf(File.FILE_H, Rank.RANK_8)).isPresent());

        Assert.assertEquals(East.getInstance().next(
                Square.valueOf(File.FILE_C, Rank.RANK_6)).get(),
                Square.valueOf(File.FILE_D, Rank.RANK_6));

        Assert.assertEquals(East.getInstance().next(
                Square.valueOf(File.FILE_A, Rank.RANK_3)).get(),
                Square.valueOf(File.FILE_B, Rank.RANK_3));
    }

}
