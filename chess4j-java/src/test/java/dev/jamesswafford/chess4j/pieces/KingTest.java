package dev.jamesswafford.chess4j.pieces;

import dev.jamesswafford.chess4j.board.Color;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class KingTest {

    @Test
    public void testColor() {
        Assert.assertEquals(Color.WHITE, King.WHITE_KING.getColor());
        Assert.assertEquals(Color.BLACK, King.BLACK_KING.getColor());
        assertTrue(King.WHITE_KING.isWhite());
        assertFalse(King.BLACK_KING.isWhite());
        assertFalse(King.WHITE_KING.isBlack());
        assertTrue(King.BLACK_KING.isBlack());
    }

    @Test
    public void testToString() {
        Assert.assertEquals("K", King.WHITE_KING.toString());
        Assert.assertEquals("k", King.BLACK_KING.toString());
    }

    @Test
    public void testGetOppositeColorPiece() {
        Assert.assertEquals(King.WHITE_KING, King.BLACK_KING.getOppositeColorPiece());
        Assert.assertEquals(King.BLACK_KING, King.WHITE_KING.getOppositeColorPiece());
    }

}
