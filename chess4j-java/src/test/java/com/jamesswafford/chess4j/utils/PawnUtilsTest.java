package com.jamesswafford.chess4j.utils;

import org.junit.Test;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.squares.Square;
import com.jamesswafford.chess4j.io.FenParser;
import com.jamesswafford.chess4j.pieces.Pawn;
import com.jamesswafford.chess4j.pieces.Piece;

import static org.junit.Assert.*;

import static com.jamesswafford.chess4j.board.squares.File.*;
import static com.jamesswafford.chess4j.board.squares.Rank.*;
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
    public void testPassedPawnWikiPos() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "7k/8/7p/1P2Pp1P/2Pp1PP1/8/8/7K w - -");

        assertTrue(isPassedPawn(b,Square.valueOf(FILE_B,RANK_5),true));
        assertTrue(isPassedPawn(b,Square.valueOf(FILE_C,RANK_4),true));
        assertTrue(isPassedPawn(b,Square.valueOf(FILE_D,RANK_4),false));
        assertTrue(isPassedPawn(b,Square.valueOf(FILE_E,RANK_5),true));

        assertFalse(isPassedPawn(b,Square.valueOf(FILE_F,RANK_5),false));
        assertFalse(isPassedPawn(b,Square.valueOf(FILE_F,RANK_4),true));
        assertFalse(isPassedPawn(b,Square.valueOf(FILE_G,RANK_4),true));
        assertFalse(isPassedPawn(b,Square.valueOf(FILE_H,RANK_5),true));
        assertFalse(isPassedPawn(b,Square.valueOf(FILE_H,RANK_6),false));
    }

    @Test
    public void testPassedPawnWikiPos2() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "8/5ppp/8/5PPP/8/6k1/8/6K1 w - -");

        assertFalse(isPassedPawn(b,Square.valueOf(FILE_F,RANK_7),false));
        assertFalse(isPassedPawn(b,Square.valueOf(FILE_G,RANK_7),false));
        assertFalse(isPassedPawn(b,Square.valueOf(FILE_H,RANK_7),false));

        assertFalse(isPassedPawn(b,Square.valueOf(FILE_F,RANK_5),true));
        assertFalse(isPassedPawn(b,Square.valueOf(FILE_G,RANK_5),true));
        assertFalse(isPassedPawn(b,Square.valueOf(FILE_H,RANK_5),true));
    }

    @Test
    public void tesPassedPawnWikiPos3() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "8/8/1PP2PbP/3r4/8/1Q5p/p5N1/k3K3 b - -");

        assertTrue(isPassedPawn(b,Square.valueOf(FILE_B,RANK_6),true));
        assertTrue(isPassedPawn(b,Square.valueOf(FILE_C,RANK_6),true));
        assertTrue(isPassedPawn(b,Square.valueOf(FILE_F,RANK_6),true));
        assertTrue(isPassedPawn(b,Square.valueOf(FILE_H,RANK_6),true));
        assertTrue(isPassedPawn(b,Square.valueOf(FILE_A,RANK_2),false));
        assertTrue(isPassedPawn(b,Square.valueOf(FILE_H,RANK_3),false));
    }

    @Test
    public void testPassedPawnWikiPos4() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "k7/b1P5/KP6/6q1/8/8/8/4n3 b - -");

        assertTrue(isPassedPawn(b,Square.valueOf(FILE_B,RANK_6),true));
        assertTrue(isPassedPawn(b,Square.valueOf(FILE_C,RANK_7),true));
    }

    @Test
    public void testLevinfishSmyslov57() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "R7/6k1/P5p1/5p1p/5P1P/r5P1/5K2/8 w - -");

        assertTrue(isPassedPawn(b,Square.valueOf(FILE_A,RANK_6),true));
        assertFalse(isPassedPawn(b,Square.valueOf(FILE_G,RANK_6),false));
        assertFalse(isPassedPawn(b,Square.valueOf(FILE_F,RANK_5),false));
        assertFalse(isPassedPawn(b,Square.valueOf(FILE_H,RANK_5),false));
        assertFalse(isPassedPawn(b,Square.valueOf(FILE_F,RANK_4),true));
        assertFalse(isPassedPawn(b,Square.valueOf(FILE_H,RANK_4),true));
        assertFalse(isPassedPawn(b,Square.valueOf(FILE_G,RANK_3),true));
    }

    @Test
    public void testFischerLarsen71() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "8/4kp2/6p1/7p/P7/2K3P1/7P/8 b - -");

        assertFalse(isPassedPawn(b,Square.valueOf(FILE_F,RANK_7),false));
        assertFalse(isPassedPawn(b,Square.valueOf(FILE_G,RANK_6),false));
        assertFalse(isPassedPawn(b,Square.valueOf(FILE_H,RANK_5),false));

        assertTrue(isPassedPawn(b,Square.valueOf(FILE_A,RANK_4),true));
        assertFalse(isPassedPawn(b,Square.valueOf(FILE_G,RANK_3),true));
        assertFalse(isPassedPawn(b,Square.valueOf(FILE_H,RANK_5),true));
    }

    @Test
    public void testBotvinnikCapablanca38() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "8/p3q1kp/1p2Pnp1/3pQ3/2pP4/1nP3N1/1B4PP/6K1 w - -");

        assertTrue(isPassedPawn(b,Square.valueOf(FILE_A,RANK_7),false));
        assertFalse(isPassedPawn(b,Square.valueOf(FILE_H,RANK_7),false));
        assertFalse(isPassedPawn(b,Square.valueOf(FILE_B,RANK_6),false));
        assertFalse(isPassedPawn(b,Square.valueOf(FILE_G,RANK_6),false));
        assertFalse(isPassedPawn(b,Square.valueOf(FILE_D,RANK_5),false));

        assertFalse(isPassedPawn(b,Square.valueOf(FILE_C,RANK_4),false));
        assertFalse(isPassedPawn(b,Square.valueOf(FILE_D,RANK_4),true));
        assertFalse(isPassedPawn(b,Square.valueOf(FILE_C,RANK_3),true));
        assertFalse(isPassedPawn(b,Square.valueOf(FILE_G,RANK_2),true));
        assertFalse(isPassedPawn(b,Square.valueOf(FILE_H,RANK_2),true));
    }

    @Test
    public void testIsolatedPawn() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "k7/p1p3p1/3p3p/1P5P/1PP1P1P1/8/8/K7 w - - 0 1");

        // white's pawn on the E file and black's pawn on the A file are isolated
        assertTrue(isIsolated(b,Square.valueOf(FILE_E, RANK_4),true));
        assertFalse(isDoubled(b,Square.valueOf(FILE_E, RANK_4),true));
        assertFalse(isPassedPawn(b,Square.valueOf(FILE_E, RANK_4),true));

        assertTrue(isIsolated(b,Square.valueOf(FILE_A, RANK_7),false));
        assertFalse(isDoubled(b,Square.valueOf(FILE_A, RANK_7),false));
        assertFalse(isPassedPawn(b,Square.valueOf(FILE_A, RANK_7),false));

        assertFalse(isIsolated(b,Square.valueOf(FILE_C, RANK_7),true));
        assertFalse(isDoubled(b,Square.valueOf(FILE_C, RANK_7),false));
        assertFalse(isPassedPawn(b,Square.valueOf(FILE_C, RANK_7),false));

        assertFalse(isIsolated(b,Square.valueOf(FILE_G, RANK_4),true));
        assertFalse(isDoubled(b,Square.valueOf(FILE_G, RANK_4),true));
        assertFalse(isPassedPawn(b,Square.valueOf(FILE_G, RANK_4),true));
    }

    @Test
    public void testDoubled() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "k7/p1p3p1/3p3p/1P5P/1PP1P1P1/8/8/K7 w - - 0 1");

        assertTrue(isDoubled(b,Square.valueOf(FILE_B, RANK_5),true));
        assertFalse(isIsolated(b,Square.valueOf(FILE_B, RANK_5),true));
        assertFalse(isPassedPawn(b,Square.valueOf(FILE_B, RANK_5),true));

        assertFalse(isIsolated(b,Square.valueOf(FILE_B, RANK_4),true));
        assertTrue(isDoubled(b,Square.valueOf(FILE_B, RANK_4),true));
        assertFalse(isIsolated(b,Square.valueOf(FILE_B, RANK_4),true));
    }

}
