package dev.jamesswafford.chess4j.pieces;

import dev.jamesswafford.chess4j.board.Color;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class QueenTest {

    @Test
    public void testColor() {
        Assert.assertEquals(Color.WHITE, Queen.WHITE_QUEEN.getColor());
        Assert.assertEquals(Color.BLACK, Queen.BLACK_QUEEN.getColor());
        assertTrue(Queen.WHITE_QUEEN.isWhite());
        assertFalse(Queen.BLACK_QUEEN.isWhite());
        assertFalse(Queen.WHITE_QUEEN.isBlack());
        assertTrue(Queen.BLACK_QUEEN.isBlack());
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
