package com.jamesswafford.chess4j.pieces;

import org.junit.Test;

import static com.jamesswafford.chess4j.board.Color.*;
import static com.jamesswafford.chess4j.pieces.Queen.*;

import static org.junit.Assert.*;

public class QueenTest {

    @Test
    public void testColor() {
        assertEquals(WHITE, WHITE_QUEEN.getColor());
        assertEquals(BLACK, BLACK_QUEEN.getColor());
        assertTrue(WHITE_QUEEN.isWhite());
        assertFalse(BLACK_QUEEN.isWhite());
        assertFalse(WHITE_QUEEN.isBlack());
        assertTrue(BLACK_QUEEN.isBlack());
    }

    @Test
    public void testToString() {
        assertEquals("Q", WHITE_QUEEN.toString());
        assertEquals("q", BLACK_QUEEN.toString());
    }

    @Test
    public void testGetOppositeColorPiece() {
        assertEquals(WHITE_QUEEN, BLACK_QUEEN.getOppositeColorPiece());
        assertEquals(BLACK_QUEEN, WHITE_QUEEN.getOppositeColorPiece());
    }
}
