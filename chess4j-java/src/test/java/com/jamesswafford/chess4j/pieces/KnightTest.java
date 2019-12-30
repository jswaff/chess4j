package com.jamesswafford.chess4j.pieces;

import org.junit.Test;

import static com.jamesswafford.chess4j.Color.*;
import static com.jamesswafford.chess4j.pieces.Knight.*;

import static org.junit.Assert.*;

public class KnightTest {

    @Test
    public void testColor() {
        assertEquals(WHITE, WHITE_KNIGHT.getColor());
        assertEquals(BLACK, BLACK_KNIGHT.getColor());
        assertTrue(WHITE_KNIGHT.isWhite());
        assertFalse(BLACK_KNIGHT.isWhite());
        assertFalse(WHITE_KNIGHT.isBlack());
        assertTrue(BLACK_KNIGHT.isBlack());
    }

    @Test
    public void testToString() {
        assertEquals("N", WHITE_KNIGHT.toString());
        assertEquals("n", BLACK_KNIGHT.toString());
    }

    @Test
    public void testGetOppositeColorPiece() {
        assertEquals(WHITE_KNIGHT, BLACK_KNIGHT.getOppositeColorPiece());
        assertEquals(BLACK_KNIGHT, WHITE_KNIGHT.getOppositeColorPiece());
    }

}
