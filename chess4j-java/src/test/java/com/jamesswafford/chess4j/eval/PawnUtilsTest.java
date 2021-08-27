package com.jamesswafford.chess4j.eval;

import org.junit.Test;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.squares.Square;
import com.jamesswafford.chess4j.pieces.Pawn;
import com.jamesswafford.chess4j.pieces.Piece;

import static org.junit.Assert.*;

import static com.jamesswafford.chess4j.board.squares.Square.*;
import static com.jamesswafford.chess4j.eval.PawnUtils.*;

public class PawnUtilsTest {

    @Test
    public void testIsPassedPawn_InitialPos() {
        Board board = new Board();

        int n = 0;
        for (Square sq : Square.allSquares()) {
            Piece p = board.getPiece(sq);
            if (p instanceof Pawn) {
                assertFalse(isPassedPawn(board,sq,p.isWhite()));
                n++;
            }
        }

        assertEquals(16, n);
    }

    @Test
    public void testIsPassedPawn_WikiPos() {
        Board board = new Board("7k/8/7p/1P2Pp1P/2Pp1PP1/8/8/7K w - -");

        assertTrue(isPassedPawn(board, B5,true));
        assertTrue(isPassedPawn(board, C4,true));
        assertTrue(isPassedPawn(board, D4,false));
        assertTrue(isPassedPawn(board, E5,true));

        assertFalse(isPassedPawn(board, F5,false));
        assertFalse(isPassedPawn(board, F4,true));
        assertFalse(isPassedPawn(board, G4,true));
        assertFalse(isPassedPawn(board, H5,true));
        assertFalse(isPassedPawn(board, H6,false));
    }

    @Test
    public void testIsPassedPawn_WikiPos2() {
        Board board = new Board("8/5ppp/8/5PPP/8/6k1/8/6K1 w - -");

        assertFalse(isPassedPawn(board, F7,false));
        assertFalse(isPassedPawn(board, G7,false));
        assertFalse(isPassedPawn(board, H7,false));

        assertFalse(isPassedPawn(board, F5,true));
        assertFalse(isPassedPawn(board, G5,true));
        assertFalse(isPassedPawn(board, H5,true));
    }

    @Test
    public void testIsPassedPawn_WikiPos3() {
        Board board = new Board("8/8/1PP2PbP/3r4/8/1Q5p/p5N1/k3K3 b - -");

        assertTrue(isPassedPawn(board, B6,true));
        assertTrue(isPassedPawn(board, C6,true));
        assertTrue(isPassedPawn(board, F6,true));
        assertTrue(isPassedPawn(board, H6,true));
        assertTrue(isPassedPawn(board, A2,false));
        assertTrue(isPassedPawn(board, H3,false));
    }

    @Test
    public void testIsPassedPawn_WikiPos4() {
        Board board = new Board("k7/b1P5/KP6/6q1/8/8/8/4n3 b - -");

        assertTrue(isPassedPawn(board, B6,true));
        assertTrue(isPassedPawn(board, C7,true));
    }

    @Test
    public void testIsPassedPawn_LevinfishSmyslov57() {
        Board board = new Board("R7/6k1/P5p1/5p1p/5P1P/r5P1/5K2/8 w - -");

        assertTrue(isPassedPawn(board, A6,true));
        assertFalse(isPassedPawn(board, G6,false));
        assertFalse(isPassedPawn(board, F5,false));
        assertFalse(isPassedPawn(board, H5,false));
        assertFalse(isPassedPawn(board, F4,true));
        assertFalse(isPassedPawn(board, H4,true));
        assertFalse(isPassedPawn(board, G3,true));
    }

    @Test
    public void testIsPassedPawn_FischerLarsen71() {
        Board board = new Board("8/4kp2/6p1/7p/P7/2K3P1/7P/8 b - -");

        assertFalse(isPassedPawn(board, F7,false));
        assertFalse(isPassedPawn(board, G6,false));
        assertFalse(isPassedPawn(board, H5,false));

        assertTrue(isPassedPawn(board, A4,true));
        assertFalse(isPassedPawn(board, G3,true));
        assertFalse(isPassedPawn(board, H5,true));
    }

    @Test
    public void testIsPassedPawn_BotvinnikCapablanca38() {
        Board board = new Board("8/p3q1kp/1p2Pnp1/3pQ3/2pP4/1nP3N1/1B4PP/6K1 w - -");

        assertTrue(isPassedPawn(board, A7,false));
        assertFalse(isPassedPawn(board, H7,false));
        assertFalse(isPassedPawn(board, B6,false));
        assertFalse(isPassedPawn(board, G6,false));
        assertFalse(isPassedPawn(board, D5,false));

        assertFalse(isPassedPawn(board, C4,false));
        assertFalse(isPassedPawn(board, D4,true));
        assertFalse(isPassedPawn(board, C3,true));
        assertFalse(isPassedPawn(board, G2,true));
        assertFalse(isPassedPawn(board, H2,true));
    }

    @Test
    public void testIsolatedPawn() {

        Board board = new Board("k7/p1p3p1/3p3p/1P5P/1PP1P3/8/8/K7 b - - 0 1");

        /*
        k - - - - - - -
        p - p - - - p -
        - - - p - - - p    black to move
        - P - - - - - P    no ep
        - P P - P - - -    no castling rights
        - - - - - - - -
        - - - - - - - -
        K - - - - - - -
        */

        // white's pawn on the E file and black's pawn on the A file are isolated
        assertTrue(isIsolated(board, A7,false));
        assertFalse(isIsolated(board, B5,true));
        assertFalse(isIsolated(board, B4,true));
        assertFalse(isIsolated(board, C4,true));
        assertFalse(isIsolated(board, C7,false));
        assertFalse(isIsolated(board, D6,false));
        assertTrue(isIsolated(board, E4,true));
        assertFalse(isIsolated(board, G7,false));
        assertFalse(isIsolated(board, H6,false));
        assertTrue(isIsolated(board, H5,true));
    }

    @Test
    public void testDoubled() {
        Board board = new Board("k7/p1p3p1/3p2pp/1P5P/1PP1P1P1/8/8/K7 w - - 0 1");

        /*
        k - - - - - - -
        p - p - - - p -
        - - - p - - p p    white to move
        - P - - - - - P    no ep
        - P P - P - P -    no castling rights
        - - - - - - - -
        - - - - - - - -
        K - - - - - - -
        */

        assertFalse(isDoubled(board, A7,false));
        assertTrue(isDoubled(board, B5,true));
        assertTrue(isDoubled(board, B4,true));
        assertFalse(isDoubled(board, C4,true));
        assertFalse(isDoubled(board, C7,false));
        assertFalse(isDoubled(board, D6,false));
        assertFalse(isDoubled(board, E4,true));
        assertTrue(isDoubled(board, G7,false));
        assertTrue(isDoubled(board, G6,false));
        assertFalse(isDoubled(board, G4,true));
        assertFalse(isDoubled(board, H6,false));
        assertFalse(isDoubled(board, H5,true));
    }

}
