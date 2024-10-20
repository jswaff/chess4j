package dev.jamesswafford.chess4j.eval;

import dev.jamesswafford.chess4j.board.Board;
import dev.jamesswafford.chess4j.board.squares.Square;
import org.junit.Test;

import static org.junit.Assert.*;

public class KnightUtilsTest {
    
    @Test
    public void testKnightOutpostInitialPos() {
        Board board = new Board();
        assertFalse(KnightUtils.isOutpost(board, Square.B8, false));
        assertFalse(KnightUtils.isOutpost(board, Square.G8, false));
        assertFalse(KnightUtils.isOutpost(board, Square.B2, true));
        assertFalse(KnightUtils.isOutpost(board, Square.G2, true));
    }

    @Test
    public void testIsKnightOutpost1() {
        Board board = new Board("1r3rk1/3q1ppp/3p4/p1pNpP2/PpP1P1P1/1P3Q2/6KP/5R2 w - - 0 1");
        assertTrue(KnightUtils.isOutpost(board, Square.D5, true));
    }

    @Test
    public void testIsKnightOutpost2() {
        Board board = new Board("r1br1k2/ppp2pp1/1b4np/4P3/2pNN3/2P3B1/PP1R1PPP/3R2K1 w - - 0 1");
        
        assertFalse(KnightUtils.isOutpost(board, Square.D4, true));
        assertFalse(KnightUtils.isOutpost(board, Square.E4, true));
        assertFalse(KnightUtils.isOutpost(board, Square.G6, false));
    }

    @Test
    public void testIsKnightOutpost3() {
        Board board = new Board("r1br1k2/pp3pp1/1b4np/4P3/2pNN3/2P3B1/PP1R1PPP/3R2K1 w - - 0 1");
        assertTrue(KnightUtils.isOutpost(board, Square.D4, true));
        assertFalse(KnightUtils.isOutpost(board, Square.E4, true));
        assertFalse(KnightUtils.isOutpost(board, Square.G6, false));
    }
}
