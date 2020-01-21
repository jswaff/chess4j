package com.jamesswafford.chess4j.pieces;

import org.junit.Test;

import static com.jamesswafford.chess4j.board.Color.*;
import static com.jamesswafford.chess4j.pieces.Pawn.*;

import static org.junit.Assert.*;

public class PawnTest {

    @Test
    public void testColor() {
        assertEquals(WHITE, WHITE_PAWN.getColor());
        assertEquals(BLACK, BLACK_PAWN.getColor());
        assertTrue(WHITE_PAWN.isWhite());
        assertFalse(BLACK_PAWN.isWhite());
        assertFalse(WHITE_PAWN.isBlack());
        assertTrue(BLACK_PAWN.isBlack());
    }

    @Test
    public void testToString() {
        assertEquals("P", WHITE_PAWN.toString());
        assertEquals("p", BLACK_PAWN.toString());
    }

    @Test
    public void testGetOppositeColorPiece() {
        assertEquals(WHITE_PAWN, BLACK_PAWN.getOppositeColorPiece());
        assertEquals(BLACK_PAWN, WHITE_PAWN.getOppositeColorPiece());
    }

}
