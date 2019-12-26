package com.jamesswafford.chess4j.board;

import org.junit.Test;

import static org.junit.Assert.*;

import static com.jamesswafford.chess4j.pieces.Pawn.*;
import static com.jamesswafford.chess4j.pieces.King.*;
import static com.jamesswafford.chess4j.pieces.Knight.*;
import static com.jamesswafford.chess4j.board.squares.Square.*;

import static com.jamesswafford.chess4j.board.Draw.*;

public class DrawTest {

    @Test
    public void testIsDrawBy50MoveRule() {
        Board b = Board.INSTANCE;
        b.resetBoard();

        assertFalse(isDrawBy50MoveRule(b));

        // move knights out and back in 25 times.  that will take the 50 move counter to
        // 25 x 4 = 100.  Only on the last move should the draw be claimed.
        for (int i=0; i<25; i++) {

            b.applyMove(new Move(WHITE_KNIGHT, G1, F3));
            assertFalse(isDrawBy50MoveRule(b));

            b.applyMove(new Move(BLACK_KNIGHT, G8, F6));
            assertFalse(isDrawBy50MoveRule(b));

            b.applyMove(new Move(WHITE_KNIGHT, F3, G1));
            assertFalse(isDrawBy50MoveRule(b));

            b.applyMove(new Move(BLACK_KNIGHT, F6, G8));
            assertEquals(i==24, isDrawBy50MoveRule(b));
        }

        assertEquals(100, b.getFiftyCounter());

        // move a pawn and it's reset
        b.applyMove(new Move(WHITE_PAWN, E2, E3));
        assertEquals(0, b.getFiftyCounter());

        assertFalse(isDrawBy50MoveRule(b));
    }

    @Test
    public void testIsDrawby50MoveRule_fen() {
        Board b = Board.INSTANCE;
        b.setPos("8/7p/5k2/5p2/p1p2P2/Pr1pPK2/1P1R3P/8 b - - 12 47");

        assertEquals(12, b.getFiftyCounter());
        assertFalse(isDrawBy50MoveRule(b));

        b.setPos("7k/7p/8/8/8/8/7P/7K w - - 100 200");
        assertEquals(100, b.getFiftyCounter());
        assertTrue(isDrawBy50MoveRule(b));
    }

    @Test
    public void testIsDrawLackOfMaterial_noMaterial() {
        Board b = Board.INSTANCE;
        b.setPos("kb6/8/1K6/8/8/8/8/8 b - - ");
        assertTrue(isDrawLackOfMaterial(b));
    }

    @Test
    public void testIsDrawLackOfMaterial_onePawn() {
        Board b = Board.INSTANCE;
        b.setPos("4k3/8/8/8/8/8/P7/4K3 w - -");
        assertFalse(isDrawLackOfMaterial(b));
    }

    @Test
    public void testIsDrawLackOfMaterial_oneKnight() {
        Board b = Board.INSTANCE;
        b.setPos("4k3/8/8/8/8/8/n7/4K3 w - -");
        assertTrue(isDrawLackOfMaterial(b));
    }

    @Test
    public void testIsDrawLackOfMaterial_oneBishop() {
        Board b = Board.INSTANCE;
        b.setPos("4k3/8/8/8/8/8/B7/4K3 w - -");
        assertTrue(isDrawLackOfMaterial(b));
    }

    @Test
    public void testIsDrawLackOfMaterial_oneRook() {
        Board b = Board.INSTANCE;
        b.setPos("4k3/8/8/8/8/8/r7/4K3 b - -");
        assertFalse(isDrawLackOfMaterial(b));
    }

    @Test
    public void testIsDrawLackOfMaterial_oneQueen() {
        Board b = Board.INSTANCE;
        b.setPos("4k3/8/8/8/8/8/Q7/4K3 w - -");
        assertFalse(isDrawLackOfMaterial(b));
    }

    @Test
    public void testIsDrawLackOfMaterial_twoWhiteKnights() {
        Board b = Board.INSTANCE;
        b.setPos("4k3/8/8/8/8/8/NN6/4K3 w - -");
        assertFalse(isDrawLackOfMaterial(b));
    }

    @Test
    public void testIsDrawLackOfMaterial_twoOpposingKnights() {
        Board b = Board.INSTANCE;
        b.setPos("4k3/8/8/8/8/8/Nn6/4K3 b - -");
        assertFalse(isDrawLackOfMaterial(b));
    }

    @Test
    public void testIsDrawLackOfMaterial_twoOpposingBishopsDifferentColors() {
        Board b = Board.INSTANCE;
        b.setPos("4k3/8/8/8/8/8/Bb6/4K3 w - -");
        assertFalse(isDrawLackOfMaterial(b));
    }

    @Test
    public void testIsDrawLackOfMaterial_twoOpposingBishopsSameColor() {
        Board b = Board.INSTANCE;
        b.setPos("4k3/8/8/8/8/8/B1b5/4K3 b - -");
        assertTrue(isDrawLackOfMaterial(b));
    }

    @Test
    public void testIsDrawLackOfMaterial_bishopVsKnight() {
        Board b = Board.INSTANCE;
        b.setPos("4k3/8/8/8/8/8/B1n5/5K2 b - -");
        assertFalse(isDrawLackOfMaterial(b));
    }

    @Test
    public void testIsDrawByRep() {
        Board b = Board.INSTANCE;
        b.resetBoard();

        assertFalse(isDrawByRep(b));

        b.applyMove(new Move(WHITE_PAWN, E2, E4));
        assertFalse(isDrawByRep(b));

        b.applyMove(new Move(BLACK_KNIGHT, G8, F6));
        assertFalse(isDrawByRep(b));

        b.applyMove(new Move(WHITE_KNIGHT, G1, F3));
        assertFalse(isDrawByRep(b));

        b.applyMove(new Move(BLACK_KNIGHT, F6, G8));
        assertFalse(isDrawByRep(b));

        b.applyMove(new Move(WHITE_KNIGHT, F3, G1));
        assertFalse(isDrawByRep(b));  // still 1 (first has ep square)

        b.applyMove(new Move(BLACK_KNIGHT, G8, F6));
        assertFalse(isDrawByRep(b)); // 2

        b.applyMove(new Move(WHITE_KNIGHT, G1, F3));
        assertFalse(isDrawByRep(b));

        b.applyMove(new Move(BLACK_KNIGHT, F6, G8));
        assertFalse(isDrawByRep(b)); // 2

        b.applyMove(new Move(WHITE_KNIGHT, F3, G1));
        assertFalse(isDrawByRep(b)); // 2

        b.applyMove(new Move(BLACK_KNIGHT, G8, F6));
        assertTrue(isDrawByRep(b)); // 3

        b.applyMove(new Move(WHITE_PAWN, D2, D4));
        assertFalse(isDrawByRep(b));
    }

    @Test
    public void testIsDrawByRep_fen() {
        Board b = Board.INSTANCE;
        b.setPos("7k/7p/8/8/8/8/7P/7K w - - 12 47");

        assertEquals(92, b.getMoveCounter());
        assertFalse(isDrawByRep(b));

        b.applyMove(new Move(BLACK_KING, H8, G8));
        assertFalse(isDrawByRep(b));

        b.applyMove(new Move(WHITE_KING, H1, G1));
        assertFalse(isDrawByRep(b));

        b.applyMove(new Move(BLACK_KING, G8, H8));
        assertFalse(isDrawByRep(b));

        b.applyMove(new Move(WHITE_KING, G1, H1));
        assertFalse(isDrawByRep(b));

        b.applyMove(new Move(BLACK_KING, H8, G8));
        assertFalse(isDrawByRep(b));

        b.applyMove(new Move(WHITE_KING, H1, G1));
        assertFalse(isDrawByRep(b));

        b.applyMove(new Move(BLACK_KING, G8, H8));
        assertFalse(isDrawByRep(b));

        b.applyMove(new Move(WHITE_KING, G1, H1));
        assertTrue(isDrawByRep(b));

        assertEquals(100, b.getMoveCounter());
        assertEquals(20, b.getFiftyCounter());
    }
}
