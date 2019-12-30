package com.jamesswafford.chess4j.pieces;

import org.junit.Test;

import static com.jamesswafford.chess4j.Color.*;
import static com.jamesswafford.chess4j.pieces.Bishop.*;

import static org.junit.Assert.*;


public class BishopTest {

    @Test
    public void testColor() {
        assertEquals(WHITE, WHITE_BISHOP.getColor());
        assertEquals(BLACK, BLACK_BISHOP.getColor());
        assertTrue(WHITE_BISHOP.isWhite());
        assertFalse(BLACK_BISHOP.isWhite());
        assertFalse(WHITE_BISHOP.isBlack());
        assertTrue(BLACK_BISHOP.isBlack());
    }

    @Test
    public void testToString() {
        assertEquals("B", WHITE_BISHOP.toString());
        assertEquals("b", BLACK_BISHOP.toString());
    }

    @Test
    public void testGetOppositeColorPiece() {
        assertEquals(WHITE_BISHOP, BLACK_BISHOP.getOppositeColorPiece());
        assertEquals(BLACK_BISHOP, WHITE_BISHOP.getOppositeColorPiece());
    }
}
