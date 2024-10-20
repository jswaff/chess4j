package dev.jamesswafford.chess4j.pieces;

import dev.jamesswafford.chess4j.board.Color;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;


public class BishopTest {

    @Test
    public void testColor() {
        Assert.assertEquals(Color.WHITE, Bishop.WHITE_BISHOP.getColor());
        Assert.assertEquals(Color.BLACK, Bishop.BLACK_BISHOP.getColor());
        assertTrue(Bishop.WHITE_BISHOP.isWhite());
        assertFalse(Bishop.BLACK_BISHOP.isWhite());
        assertFalse(Bishop.WHITE_BISHOP.isBlack());
        assertTrue(Bishop.BLACK_BISHOP.isBlack());
    }

    @Test
    public void testToString() {
        Assert.assertEquals("B", Bishop.WHITE_BISHOP.toString());
        Assert.assertEquals("b", Bishop.BLACK_BISHOP.toString());
    }

    @Test
    public void testGetOppositeColorPiece() {
        Assert.assertEquals(Bishop.WHITE_BISHOP, Bishop.BLACK_BISHOP.getOppositeColorPiece());
        Assert.assertEquals(Bishop.BLACK_BISHOP, Bishop.WHITE_BISHOP.getOppositeColorPiece());
    }
}
