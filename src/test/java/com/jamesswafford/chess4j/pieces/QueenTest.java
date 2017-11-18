package com.jamesswafford.chess4j.pieces;

import junit.framework.Assert;

import org.junit.Test;

import com.jamesswafford.chess4j.Color;
import com.jamesswafford.chess4j.pieces.Queen;


public class QueenTest {

    @Test
    public void testColor() {
        Assert.assertEquals(Color.WHITE, Queen.WHITE_QUEEN.getColor());
        Assert.assertEquals(Color.BLACK, Queen.BLACK_QUEEN.getColor());
        Assert.assertTrue(Queen.WHITE_QUEEN.isWhite());
        Assert.assertFalse(Queen.BLACK_QUEEN.isWhite());
        Assert.assertFalse(Queen.WHITE_QUEEN.isBlack());
        Assert.assertTrue(Queen.BLACK_QUEEN.isBlack());
    }

    @Test
    public void testToString() {
        Assert.assertEquals("Q", Queen.WHITE_QUEEN.toString());
        Assert.assertEquals("q", Queen.BLACK_QUEEN.toString());
    }

    @Test
    public void testGetOppositeColorPiece() {
        Assert.assertEquals(Queen.WHITE_QUEEN, Queen.BLACK_QUEEN.getOppositeColorPiece());
        Assert.assertEquals(Queen.BLACK_QUEEN, Queen.WHITE_QUEEN.getOppositeColorPiece());
    }

}
