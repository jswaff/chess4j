package com.jamesswafford.chess4j.eval;

import static org.junit.Assert.*;

import org.junit.Test;

import com.jamesswafford.chess4j.board.Board;

import static com.jamesswafford.chess4j.board.squares.Square.*;
import static com.jamesswafford.chess4j.eval.BishopUtils.*;

public class BishopUtilsTest {
    
    @Test
    public void testTrappedBishopInitialPos() {
        Board board = new Board();

        assertFalse(isTrapped(board, C1, true));
        assertFalse(isTrapped(board, F1, true));
        assertFalse(isTrapped(board, C8, false));
        assertFalse(isTrapped(board, F8, false));
    }

    @Test
    public void testTrappedBishopH2() {
        assertTrue(isTrapped(new Board("8/pp2k1p1/4pp2/1P6/7p/P3P1P1/5PKb/2B5 w - - 0 1"), H2, false));
        assertFalse(isTrapped(new Board("8/pp2k1p1/4pp2/1P6/7p/P3P3/5PKb/2B5 w - - 0 1"), H2, false));
        assertFalse(isTrapped(new Board("8/pp2k1p1/4pp2/1P6/7p/P3P1P1/5PKB/2B5 w - - 0 1"), H2, true));
    }

    @Test
    public void testTrappedBishopA2() {
        assertTrue(isTrapped(new Board("rn1qkbnr/pppppppp/8/8/8/1P6/b1PPPPPP/2KR1BNR b - - 0 1"), A2, false));
        assertFalse(isTrapped(new Board("rn1qkbnr/pppppppp/8/8/8/1P6/b2PPPPP/2KR1BNR b - - 0 1"), A2, false));
    }

    @Test
    public void testTrappedBishopA7() {
        assertTrue(isTrapped(new Board("rn1qkbnr/B1pppppp/1p6/8/8/1P6/b2PPPPP/2KR1BNR b - - 0 1"), A7, true));
    }

    @Test
    public void testTrappedBishopH7() {
        assertFalse(isTrapped(new Board("rnbq1rk1/pppn1ppB/4p3/3pP3/1b1P4/2N2N2/PPP2PPP/R1BQK2R w - - 0 1"), H7, true));
        assertTrue(isTrapped(new Board("rnbq1rk1/pppn1p1B/4p1p1/3pP3/1b1P4/2N2N2/PPP2PPP/R1BQK2R w - - 0 1"), H7, true));
    }
}
