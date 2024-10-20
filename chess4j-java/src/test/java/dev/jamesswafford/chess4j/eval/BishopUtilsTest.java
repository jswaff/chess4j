package dev.jamesswafford.chess4j.eval;

import static org.junit.Assert.*;

import dev.jamesswafford.chess4j.board.Board;
import dev.jamesswafford.chess4j.board.squares.Square;
import org.junit.Test;

public class BishopUtilsTest {
    
    @Test
    public void testTrappedBishopInitialPos() {
        Board board = new Board();

        assertFalse(BishopUtils.isTrapped(board, Square.C1, true));
        assertFalse(BishopUtils.isTrapped(board, Square.F1, true));
        assertFalse(BishopUtils.isTrapped(board, Square.C8, false));
        assertFalse(BishopUtils.isTrapped(board, Square.F8, false));
    }

    @Test
    public void testTrappedBishopH2() {
        assertTrue(BishopUtils.isTrapped(new Board("8/pp2k1p1/4pp2/1P6/7p/P3P1P1/5PKb/2B5 w - - 0 1"), Square.H2, false));
        assertFalse(BishopUtils.isTrapped(new Board("8/pp2k1p1/4pp2/1P6/7p/P3P3/5PKb/2B5 w - - 0 1"), Square.H2, false));
        assertFalse(BishopUtils.isTrapped(new Board("8/pp2k1p1/4pp2/1P6/7p/P3P1P1/5PKB/2B5 w - - 0 1"), Square.H2, true));
    }

    @Test
    public void testTrappedBishopA2() {
        assertTrue(BishopUtils.isTrapped(new Board("rn1qkbnr/pppppppp/8/8/8/1P6/b1PPPPPP/2KR1BNR b - - 0 1"), Square.A2, false));
        assertFalse(BishopUtils.isTrapped(new Board("rn1qkbnr/pppppppp/8/8/8/1P6/b2PPPPP/2KR1BNR b - - 0 1"), Square.A2, false));
    }

    @Test
    public void testTrappedBishopA7() {
        assertTrue(BishopUtils.isTrapped(new Board("rn1qkbnr/B1pppppp/1p6/8/8/1P6/b2PPPPP/2KR1BNR b - - 0 1"), Square.A7, true));
    }

    @Test
    public void testTrappedBishopH7() {
        assertFalse(BishopUtils.isTrapped(new Board("rnbq1rk1/pppn1ppB/4p3/3pP3/1b1P4/2N2N2/PPP2PPP/R1BQK2R w - - 0 1"), Square.H7, true));
        assertTrue(BishopUtils.isTrapped(new Board("rnbq1rk1/pppn1p1B/4p1p1/3pP3/1b1P4/2N2N2/PPP2PPP/R1BQK2R w - - 0 1"), Square.H7, true));
    }
}
