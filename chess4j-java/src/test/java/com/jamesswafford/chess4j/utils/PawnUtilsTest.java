package com.jamesswafford.chess4j.utils;

import org.junit.Test;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.squares.Square;
import com.jamesswafford.chess4j.pieces.Pawn;
import com.jamesswafford.chess4j.pieces.Piece;

import static org.junit.Assert.*;

import static com.jamesswafford.chess4j.board.squares.Square.*;
import static com.jamesswafford.chess4j.utils.PawnUtils.*;

public class PawnUtilsTest {

    @Test
    public void testPassedPawnInitialPos() {
        Board b = Board.INSTANCE;
        b.resetBoard();

        int n = 0;
        for (Square sq : Square.allSquares()) {
            Piece p = b.getPiece(sq);
            if (p instanceof Pawn) {
                assertFalse(isPassedPawn(b,sq,p.isWhite()));
                n++;
            }
        }

        assertEquals(16, n);
    }

    @Test
    public void testPassedPawnWikiPos() {
        Board b = Board.INSTANCE;
        b.setPos("7k/8/7p/1P2Pp1P/2Pp1PP1/8/8/7K w - -");

        assertTrue(isPassedPawn(b, B5,true));
        assertTrue(isPassedPawn(b, C4,true));
        assertTrue(isPassedPawn(b, D4,false));
        assertTrue(isPassedPawn(b, E5,true));

        assertFalse(isPassedPawn(b, F5,false));
        assertFalse(isPassedPawn(b, F4,true));
        assertFalse(isPassedPawn(b, G4,true));
        assertFalse(isPassedPawn(b, H5,true));
        assertFalse(isPassedPawn(b, H6,false));
    }

    @Test
    public void testPassedPawnWikiPos2() {
        Board b = Board.INSTANCE;
        b.setPos("8/5ppp/8/5PPP/8/6k1/8/6K1 w - -");

        assertFalse(isPassedPawn(b, F7,false));
        assertFalse(isPassedPawn(b, G7,false));
        assertFalse(isPassedPawn(b, H7,false));

        assertFalse(isPassedPawn(b, F5,true));
        assertFalse(isPassedPawn(b, G5,true));
        assertFalse(isPassedPawn(b, H5,true));
    }

    @Test
    public void tesPassedPawnWikiPos3() {
        Board b = Board.INSTANCE;
        b.setPos("8/8/1PP2PbP/3r4/8/1Q5p/p5N1/k3K3 b - -");

        assertTrue(isPassedPawn(b, B6,true));
        assertTrue(isPassedPawn(b, C6,true));
        assertTrue(isPassedPawn(b, F6,true));
        assertTrue(isPassedPawn(b, H6,true));
        assertTrue(isPassedPawn(b, A2,false));
        assertTrue(isPassedPawn(b, H3,false));
    }

    @Test
    public void testPassedPawnWikiPos4() {
        Board b = Board.INSTANCE;
        b.setPos("k7/b1P5/KP6/6q1/8/8/8/4n3 b - -");

        assertTrue(isPassedPawn(b, B6,true));
        assertTrue(isPassedPawn(b, C7,true));
    }

    @Test
    public void testLevinfishSmyslov57() {
        Board b = Board.INSTANCE;
        b.setPos("R7/6k1/P5p1/5p1p/5P1P/r5P1/5K2/8 w - -");

        assertTrue(isPassedPawn(b, A6,true));
        assertFalse(isPassedPawn(b, G6,false));
        assertFalse(isPassedPawn(b, F5,false));
        assertFalse(isPassedPawn(b, H5,false));
        assertFalse(isPassedPawn(b, F4,true));
        assertFalse(isPassedPawn(b, H4,true));
        assertFalse(isPassedPawn(b, G3,true));
    }

    @Test
    public void testFischerLarsen71() {
        Board b = Board.INSTANCE;
        b.setPos("8/4kp2/6p1/7p/P7/2K3P1/7P/8 b - -");

        assertFalse(isPassedPawn(b, F7,false));
        assertFalse(isPassedPawn(b, G6,false));
        assertFalse(isPassedPawn(b, H5,false));

        assertTrue(isPassedPawn(b, A4,true));
        assertFalse(isPassedPawn(b, G3,true));
        assertFalse(isPassedPawn(b, H5,true));
    }

    @Test
    public void testBotvinnikCapablanca38() {
        Board b = Board.INSTANCE;
        b.setPos("8/p3q1kp/1p2Pnp1/3pQ3/2pP4/1nP3N1/1B4PP/6K1 w - -");

        assertTrue(isPassedPawn(b, A7,false));
        assertFalse(isPassedPawn(b, H7,false));
        assertFalse(isPassedPawn(b, B6,false));
        assertFalse(isPassedPawn(b, G6,false));
        assertFalse(isPassedPawn(b, D5,false));

        assertFalse(isPassedPawn(b, C4,false));
        assertFalse(isPassedPawn(b, D4,true));
        assertFalse(isPassedPawn(b, C3,true));
        assertFalse(isPassedPawn(b, G2,true));
        assertFalse(isPassedPawn(b, H2,true));
    }

    @Test
    public void testIsolatedPawn() {
        Board b = Board.INSTANCE;
        b.setPos("k7/p1p3p1/3p3p/1P5P/1PP1P1P1/8/8/K7 w - - 0 1");

        // white's pawn on the E file and black's pawn on the A file are isolated
        assertTrue(isIsolated(b, E4,true));
        assertFalse(isDoubled(b, E4,true));
        assertFalse(isPassedPawn(b, E4,true));

        assertTrue(isIsolated(b, A7,false));
        assertFalse(isDoubled(b, A7,false));
        assertFalse(isPassedPawn(b, A7,false));

        assertFalse(isIsolated(b, C7,true));
        assertFalse(isDoubled(b, C7,false));
        assertFalse(isPassedPawn(b, C7,false));

        assertFalse(isIsolated(b, G4,true));
        assertFalse(isDoubled(b, G4,true));
        assertFalse(isPassedPawn(b, G4,true));
    }

    @Test
    public void testDoubled() {
        Board b = Board.INSTANCE;
        b.setPos("k7/p1p3p1/3p3p/1P5P/1PP1P1P1/8/8/K7 w - - 0 1");

        assertTrue(isDoubled(b, B5,true));
        assertFalse(isIsolated(b, B5,true));
        assertFalse(isPassedPawn(b, B5,true));

        assertFalse(isIsolated(b, B4,true));
        assertTrue(isDoubled(b, B4,true));
        assertFalse(isIsolated(b, B4,true));
    }

}
