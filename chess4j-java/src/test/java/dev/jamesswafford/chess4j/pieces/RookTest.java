package dev.jamesswafford.chess4j.pieces;

import dev.jamesswafford.chess4j.board.Color;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class RookTest {

    @Test
    public void testColor() {
        Assert.assertEquals(Color.WHITE, Rook.WHITE_ROOK.getColor());
        Assert.assertEquals(Color.BLACK, Rook.BLACK_ROOK.getColor());
        assertTrue(Rook.WHITE_ROOK.isWhite());
        assertFalse(Rook.BLACK_ROOK.isWhite());
        assertFalse(Rook.WHITE_ROOK.isBlack());
        assertTrue(Rook.BLACK_ROOK.isBlack());
    }

    @Test
    public void testToString() {
        Assert.assertEquals("R", Rook.WHITE_ROOK.toString());
        Assert.assertEquals("r", Rook.BLACK_ROOK.toString());
    }

    @Test
    public void testGetOppositeColorPiece() {
        Assert.assertEquals(Rook.WHITE_ROOK, Rook.BLACK_ROOK.getOppositeColorPiece());
        Assert.assertEquals(Rook.BLACK_ROOK, Rook.WHITE_ROOK.getOppositeColorPiece());
    }

}
