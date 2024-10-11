package dev.jamesswafford.chess4j.pieces;

import org.junit.Test;

import static dev.jamesswafford.chess4j.board.Color.*;
import static dev.jamesswafford.chess4j.pieces.Rook.*;

import static org.junit.Assert.*;

public class RookTest {

    @Test
    public void testColor() {
        assertEquals(WHITE, WHITE_ROOK.getColor());
        assertEquals(BLACK, BLACK_ROOK.getColor());
        assertTrue(WHITE_ROOK.isWhite());
        assertFalse(BLACK_ROOK.isWhite());
        assertFalse(WHITE_ROOK.isBlack());
        assertTrue(BLACK_ROOK.isBlack());
    }

    @Test
    public void testToString() {
        assertEquals("R", WHITE_ROOK.toString());
        assertEquals("r", BLACK_ROOK.toString());
    }

    @Test
    public void testGetOppositeColorPiece() {
        assertEquals(WHITE_ROOK, BLACK_ROOK.getOppositeColorPiece());
        assertEquals(BLACK_ROOK, WHITE_ROOK.getOppositeColorPiece());
    }

}
