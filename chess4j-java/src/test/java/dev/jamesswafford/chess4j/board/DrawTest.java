package dev.jamesswafford.chess4j.board;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

import static dev.jamesswafford.chess4j.pieces.Pawn.*;
import static dev.jamesswafford.chess4j.pieces.King.*;
import static dev.jamesswafford.chess4j.pieces.Knight.*;
import static dev.jamesswafford.chess4j.board.squares.Square.*;

import static dev.jamesswafford.chess4j.board.Draw.*;

public class DrawTest {

    @Test
    public void testIsDrawBy50MoveRule() {
        Board board = new Board();

        assertFalse(isDrawBy50MoveRule(board));

        // move knights out and back in 25 times.  that will take the 50 move counter to
        // 25 x 4 = 100.  Only on the last move should the draw be claimed.
        for (int i=0; i<25; i++) {

            board.applyMove(new Move(WHITE_KNIGHT, G1, F3));
            assertFalse(isDrawBy50MoveRule(board));

            board.applyMove(new Move(BLACK_KNIGHT, G8, F6));
            assertFalse(isDrawBy50MoveRule(board));

            board.applyMove(new Move(WHITE_KNIGHT, F3, G1));
            assertFalse(isDrawBy50MoveRule(board));

            board.applyMove(new Move(BLACK_KNIGHT, F6, G8));
            assertEquals(i==24, isDrawBy50MoveRule(board));
        }

        assertEquals(100, board.getFiftyCounter());

        // move a pawn and it's reset
        board.applyMove(new Move(WHITE_PAWN, E2, E3));
        assertEquals(0, board.getFiftyCounter());

        assertFalse(isDrawBy50MoveRule(board));
    }

    @Test
    public void testIsDrawby50MoveRule_fen() {
        Board board = new Board("8/7p/5k2/5p2/p1p2P2/Pr1pPK2/1P1R3P/8 b - - 12 47");

        assertEquals(12, board.getFiftyCounter());
        assertFalse(isDrawBy50MoveRule(board));

        board.setPos("7k/7p/8/8/8/8/7P/7K w - - 100 200");
        assertEquals(100, board.getFiftyCounter());
        assertTrue(isDrawBy50MoveRule(board));
    }

    @Test
    public void testIsDrawLackOfMaterial_noMaterial() {
        Board board = new Board("kb6/8/1K6/8/8/8/8/8 b - - ");
        assertTrue(isDrawLackOfMaterial(board));
    }

    @Test
    public void testIsDrawLackOfMaterial_onePawn() {
        Board board = new Board("4k3/8/8/8/8/8/P7/4K3 w - -");
        assertFalse(isDrawLackOfMaterial(board));
    }

    @Test
    public void testIsDrawLackOfMaterial_oneKnight() {
        Board board = new Board("4k3/8/8/8/8/8/n7/4K3 w - -");
        assertTrue(isDrawLackOfMaterial(board));
    }

    @Test
    public void testIsDrawLackOfMaterial_oneBishop() {
        Board board = new Board("4k3/8/8/8/8/8/B7/4K3 w - -");
        assertTrue(isDrawLackOfMaterial(board));
    }

    @Test
    public void testIsDrawLackOfMaterial_oneRook() {
        Board board = new Board("4k3/8/8/8/8/8/r7/4K3 b - -");
        assertFalse(isDrawLackOfMaterial(board));
    }

    @Test
    public void testIsDrawLackOfMaterial_oneQueen() {
        Board board = new Board("4k3/8/8/8/8/8/Q7/4K3 w - -");
        assertFalse(isDrawLackOfMaterial(board));
    }

    @Test
    public void testIsDrawLackOfMaterial_twoWhiteKnights() {
        Board board = new Board("4k3/8/8/8/8/8/NN6/4K3 w - -");
        assertFalse(isDrawLackOfMaterial(board));
    }

    @Test
    public void testIsDrawLackOfMaterial_twoOpposingKnights() {
        Board board = new Board("4k3/8/8/8/8/8/Nn6/4K3 b - -");
        assertFalse(isDrawLackOfMaterial(board));
    }

    @Test
    public void testIsDrawLackOfMaterial_twoOpposingBishopsDifferentColors() {
        Board board = new Board("4k3/8/8/8/8/8/Bb6/4K3 w - -");
        assertFalse(isDrawLackOfMaterial(board));
    }

    @Test
    public void testIsDrawLackOfMaterial_twoOpposingBishopsSameColor() {
        Board board = new Board("4k3/8/8/8/8/8/B1b5/4K3 b - -");
        assertTrue(isDrawLackOfMaterial(board));
    }

    @Test
    public void testIsDrawLackOfMaterial_bishopVsKnight() {
        Board board = new Board("4k3/8/8/8/8/8/B1n5/5K2 b - -");
        assertFalse(isDrawLackOfMaterial(board));
    }

    @Test
    public void testIsDrawByRep() {
        Board board = new Board();
        List<Undo> undos = new ArrayList<>();

        assertFalse(isDrawByRep(board, undos, 2));

        undos.add(board.applyMove(new Move(WHITE_PAWN, E2, E4)));
        assertFalse(isDrawByRep(board, undos, 2));

        undos.add(board.applyMove(new Move(BLACK_KNIGHT, G8, F6)));
        assertFalse(isDrawByRep(board, undos, 2));

        undos.add(board.applyMove(new Move(WHITE_KNIGHT, G1, F3)));
        assertFalse(isDrawByRep(board, undos, 2));

        undos.add(board.applyMove(new Move(BLACK_KNIGHT, F6, G8)));
        assertFalse(isDrawByRep(board, undos, 2));

        undos.add(board.applyMove(new Move(WHITE_KNIGHT, F3, G1)));
        assertFalse(isDrawByRep(board, undos, 2));  // still 1 (first has ep square)

        undos.add(board.applyMove(new Move(BLACK_KNIGHT, G8, F6)));
        assertFalse(isDrawByRep(board, undos, 2)); // 2

        undos.add(board.applyMove(new Move(WHITE_KNIGHT, G1, F3)));
        assertFalse(isDrawByRep(board, undos, 2));

        undos.add(board.applyMove(new Move(BLACK_KNIGHT, F6, G8)));
        assertFalse(isDrawByRep(board, undos, 2)); // 2

        undos.add(board.applyMove(new Move(WHITE_KNIGHT, F3, G1)));
        assertFalse(isDrawByRep(board, undos, 2)); // 2

        undos.add(board.applyMove(new Move(BLACK_KNIGHT, G8, F6)));
        assertTrue(isDrawByRep(board, undos, 2)); // 3

        undos.add(board.applyMove(new Move(WHITE_PAWN, D2, D4)));
        assertFalse(isDrawByRep(board, undos, 2));
    }

    @Test
    public void testIsDrawByRep_fen() {
        Board board = new Board("7k/7p/8/8/8/8/7P/7K w - - 12 47");
        List<Undo> undos = new ArrayList<>();

        assertEquals(92, board.getMoveCounter());
        assertFalse(isDrawByRep(board, undos, 2));

        undos.add(board.applyMove(new Move(BLACK_KING, H8, G8)));
        assertFalse(isDrawByRep(board, undos, 2));

        undos.add(board.applyMove(new Move(WHITE_KING, H1, G1)));
        assertFalse(isDrawByRep(board, undos, 2));

        undos.add(board.applyMove(new Move(BLACK_KING, G8, H8)));
        assertFalse(isDrawByRep(board, undos, 2));

        undos.add(board.applyMove(new Move(WHITE_KING, G1, H1)));
        assertFalse(isDrawByRep(board, undos, 2));

        undos.add(board.applyMove(new Move(BLACK_KING, H8, G8)));
        assertFalse(isDrawByRep(board, undos, 2));

        undos.add(board.applyMove(new Move(WHITE_KING, H1, G1)));
        assertFalse(isDrawByRep(board, undos, 2));

        undos.add(board.applyMove(new Move(BLACK_KING, G8, H8)));
        assertFalse(isDrawByRep(board, undos, 2));

        undos.add(board.applyMove(new Move(WHITE_KING, G1, H1)));
        assertTrue(isDrawByRep(board, undos, 2));

        assertEquals(100, board.getMoveCounter());
        assertEquals(20, board.getFiftyCounter());
    }
}
