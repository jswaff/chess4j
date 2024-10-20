package dev.jamesswafford.chess4j.pieces;

import dev.jamesswafford.chess4j.board.Color;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class KnightTest {

    @Test
    public void testColor() {
        Assert.assertEquals(Color.WHITE, Knight.WHITE_KNIGHT.getColor());
        Assert.assertEquals(Color.BLACK, Knight.BLACK_KNIGHT.getColor());
        assertTrue(Knight.WHITE_KNIGHT.isWhite());
        assertFalse(Knight.BLACK_KNIGHT.isWhite());
        assertFalse(Knight.WHITE_KNIGHT.isBlack());
        assertTrue(Knight.BLACK_KNIGHT.isBlack());
    }

    @Test
    public void testToString() {
        Assert.assertEquals("N", Knight.WHITE_KNIGHT.toString());
        Assert.assertEquals("n", Knight.BLACK_KNIGHT.toString());
    }

    @Test
    public void testGetOppositeColorPiece() {
        Assert.assertEquals(Knight.WHITE_KNIGHT, Knight.BLACK_KNIGHT.getOppositeColorPiece());
        Assert.assertEquals(Knight.BLACK_KNIGHT, Knight.WHITE_KNIGHT.getOppositeColorPiece());
    }

}
