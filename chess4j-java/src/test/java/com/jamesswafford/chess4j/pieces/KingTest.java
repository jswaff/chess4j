package com.jamesswafford.chess4j.pieces;

import org.junit.Test;

import static com.jamesswafford.chess4j.board.Color.*;
import static com.jamesswafford.chess4j.pieces.King.*;

import static org.junit.Assert.*;

public class KingTest {

    @Test
    public void testColor() {
        assertEquals(WHITE, WHITE_KING.getColor());
        assertEquals(BLACK, BLACK_KING.getColor());
        assertTrue(WHITE_KING.isWhite());
        assertFalse(BLACK_KING.isWhite());
        assertFalse(WHITE_KING.isBlack());
        assertTrue(BLACK_KING.isBlack());
    }

    @Test
    public void testToString() {
        assertEquals("K", WHITE_KING.toString());
        assertEquals("k", BLACK_KING.toString());
    }

    @Test
    public void testGetOppositeColorPiece() {
        assertEquals(WHITE_KING, BLACK_KING.getOppositeColorPiece());
        assertEquals(BLACK_KING, WHITE_KING.getOppositeColorPiece());
    }

}
