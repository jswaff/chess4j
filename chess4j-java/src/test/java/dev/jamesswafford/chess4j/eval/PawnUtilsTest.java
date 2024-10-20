package dev.jamesswafford.chess4j.eval;

import dev.jamesswafford.chess4j.board.Board;
import dev.jamesswafford.chess4j.board.Move;
import dev.jamesswafford.chess4j.board.squares.Square;
import dev.jamesswafford.chess4j.pieces.Knight;
import dev.jamesswafford.chess4j.pieces.Pawn;
import dev.jamesswafford.chess4j.pieces.Piece;
import org.junit.Test;

import static org.junit.Assert.*;

public class PawnUtilsTest {

    @Test
    public void testIsPassedPawn_InitialPos() {
        Board board = new Board();

        int n = 0;
        for (Square sq : Square.allSquares()) {
            Piece p = board.getPiece(sq);
            if (p instanceof Pawn) {
                assertFalse(PawnUtils.isPassedPawn(board,sq,p.isWhite()));
                n++;
            }
        }

        assertEquals(16, n);
    }

    @Test
    public void testIsPassedPawn_WikiPos() {
        Board board = new Board("7k/8/7p/1P2Pp1P/2Pp1PP1/8/8/7K w - -");

        assertTrue(PawnUtils.isPassedPawn(board, Square.B5,true));
        assertTrue(PawnUtils.isPassedPawn(board, Square.C4,true));
        assertTrue(PawnUtils.isPassedPawn(board, Square.D4,false));
        assertTrue(PawnUtils.isPassedPawn(board, Square.E5,true));

        assertFalse(PawnUtils.isPassedPawn(board, Square.F5,false));
        assertFalse(PawnUtils.isPassedPawn(board, Square.F4,true));
        assertFalse(PawnUtils.isPassedPawn(board, Square.G4,true));
        assertFalse(PawnUtils.isPassedPawn(board, Square.H5,true));
        assertFalse(PawnUtils.isPassedPawn(board, Square.H6,false));
    }

    @Test
    public void testIsPassedPawn_WikiPos2() {
        Board board = new Board("8/5ppp/8/5PPP/8/6k1/8/6K1 w - -");

        assertFalse(PawnUtils.isPassedPawn(board, Square.F7,false));
        assertFalse(PawnUtils.isPassedPawn(board, Square.G7,false));
        assertFalse(PawnUtils.isPassedPawn(board, Square.H7,false));

        assertFalse(PawnUtils.isPassedPawn(board, Square.F5,true));
        assertFalse(PawnUtils.isPassedPawn(board, Square.G5,true));
        assertFalse(PawnUtils.isPassedPawn(board, Square.H5,true));
    }

    @Test
    public void testIsPassedPawn_WikiPos3() {
        Board board = new Board("8/8/1PP2PbP/3r4/8/1Q5p/p5N1/k3K3 b - -");

        assertTrue(PawnUtils.isPassedPawn(board, Square.B6,true));
        assertTrue(PawnUtils.isPassedPawn(board, Square.C6,true));
        assertTrue(PawnUtils.isPassedPawn(board, Square.F6,true));
        assertTrue(PawnUtils.isPassedPawn(board, Square.H6,true));
        assertTrue(PawnUtils.isPassedPawn(board, Square.A2,false));
        assertTrue(PawnUtils.isPassedPawn(board, Square.H3,false));
    }

    @Test
    public void testIsPassedPawn_WikiPos4() {
        Board board = new Board("k7/b1P5/KP6/6q1/8/8/8/4n3 b - -");

        assertTrue(PawnUtils.isPassedPawn(board, Square.B6,true));
        assertTrue(PawnUtils.isPassedPawn(board, Square.C7,true));
    }

    @Test
    public void testIsPassedPawn_LevinfishSmyslov57() {
        Board board = new Board("R7/6k1/P5p1/5p1p/5P1P/r5P1/5K2/8 w - -");

        assertTrue(PawnUtils.isPassedPawn(board, Square.A6,true));
        assertFalse(PawnUtils.isPassedPawn(board, Square.G6,false));
        assertFalse(PawnUtils.isPassedPawn(board, Square.F5,false));
        assertFalse(PawnUtils.isPassedPawn(board, Square.H5,false));
        assertFalse(PawnUtils.isPassedPawn(board, Square.F4,true));
        assertFalse(PawnUtils.isPassedPawn(board, Square.H4,true));
        assertFalse(PawnUtils.isPassedPawn(board, Square.G3,true));
    }

    @Test
    public void testIsPassedPawn_FischerLarsen71() {
        Board board = new Board("8/4kp2/6p1/7p/P7/2K3P1/7P/8 b - -");

        assertFalse(PawnUtils.isPassedPawn(board, Square.F7,false));
        assertFalse(PawnUtils.isPassedPawn(board, Square.G6,false));
        assertFalse(PawnUtils.isPassedPawn(board, Square.H5,false));

        assertTrue(PawnUtils.isPassedPawn(board, Square.A4,true));
        assertFalse(PawnUtils.isPassedPawn(board, Square.G3,true));
        assertFalse(PawnUtils.isPassedPawn(board, Square.H5,true));
    }

    @Test
    public void testIsPassedPawn_BotvinnikCapablanca38() {
        Board board = new Board("8/p3q1kp/1p2Pnp1/3pQ3/2pP4/1nP3N1/1B4PP/6K1 w - -");

        assertTrue(PawnUtils.isPassedPawn(board, Square.A7,false));
        assertFalse(PawnUtils.isPassedPawn(board, Square.H7,false));
        assertFalse(PawnUtils.isPassedPawn(board, Square.B6,false));
        assertFalse(PawnUtils.isPassedPawn(board, Square.G6,false));
        assertFalse(PawnUtils.isPassedPawn(board, Square.D5,false));

        assertFalse(PawnUtils.isPassedPawn(board, Square.C4,false));
        assertFalse(PawnUtils.isPassedPawn(board, Square.D4,true));
        assertFalse(PawnUtils.isPassedPawn(board, Square.C3,true));
        assertFalse(PawnUtils.isPassedPawn(board, Square.G2,true));
        assertFalse(PawnUtils.isPassedPawn(board, Square.H2,true));
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
        assertTrue(PawnUtils.isIsolated(board, Square.A7,false));
        assertFalse(PawnUtils.isIsolated(board, Square.B5,true));
        assertFalse(PawnUtils.isIsolated(board, Square.B4,true));
        assertFalse(PawnUtils.isIsolated(board, Square.C4,true));
        assertFalse(PawnUtils.isIsolated(board, Square.C7,false));
        assertFalse(PawnUtils.isIsolated(board, Square.D6,false));
        assertTrue(PawnUtils.isIsolated(board, Square.E4,true));
        assertFalse(PawnUtils.isIsolated(board, Square.G7,false));
        assertFalse(PawnUtils.isIsolated(board, Square.H6,false));
        assertTrue(PawnUtils.isIsolated(board, Square.H5,true));
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

        assertFalse(PawnUtils.isDoubled(board, Square.A7,false));
        assertTrue(PawnUtils.isDoubled(board, Square.B5,true));
        assertTrue(PawnUtils.isDoubled(board, Square.B4,true));
        assertFalse(PawnUtils.isDoubled(board, Square.C4,true));
        assertFalse(PawnUtils.isDoubled(board, Square.C7,false));
        assertFalse(PawnUtils.isDoubled(board, Square.D6,false));
        assertFalse(PawnUtils.isDoubled(board, Square.E4,true));
        assertTrue(PawnUtils.isDoubled(board, Square.G7,false));
        assertTrue(PawnUtils.isDoubled(board, Square.G6,false));
        assertFalse(PawnUtils.isDoubled(board, Square.G4,true));
        assertFalse(PawnUtils.isDoubled(board, Square.H6,false));
        assertFalse(PawnUtils.isDoubled(board, Square.H5,true));
    }

    @Test
    public void testSupported1() {
        Board board = new Board("1r3rk1/3q1ppp/3p4/p1pNpP2/PpP1P1P1/1P3Q2/6KP/5R2 w - - 0 1");
        assertTrue(PawnUtils.isSupported(board, Square.D5, true));
    }

    @Test
    public void testSupported2() {
        Board board = new Board("r1br1k2/ppp2pp1/1b4np/4P3/2pNN3/2P3B1/PP1R1PPP/3R2K1 w - - 0 1");
        assertTrue(PawnUtils.isSupported(board, Square.D4, true));
        assertFalse(PawnUtils.isSupported(board, Square.E4, true));
        assertTrue(PawnUtils.isSupported(board, Square.G6, false));
    }

    @Test
    public void testSupported3() {
        Board board = new Board();
        assertFalse(PawnUtils.isSupported(board, Square.A7, false));
        assertFalse(PawnUtils.isSupported(board, Square.B7, false));
        assertFalse(PawnUtils.isSupported(board, Square.G7, false));
        assertFalse(PawnUtils.isSupported(board, Square.H7, false));
        assertFalse(PawnUtils.isSupported(board, Square.A2, true));
        assertFalse(PawnUtils.isSupported(board, Square.B2, true));
        assertFalse(PawnUtils.isSupported(board, Square.G2, true));
        assertFalse(PawnUtils.isSupported(board, Square.H2, true));

        board.applyMove(new Move(Pawn.WHITE_PAWN, Square.A2, Square.A3));
        assertTrue(PawnUtils.isSupported(board, Square.A3, true));
        assertFalse(PawnUtils.isSupported(board, Square.B2, true));

        board.applyMove(new Move(Pawn.BLACK_PAWN, Square.B7, Square.B5));
        assertFalse(PawnUtils.isSupported(board, Square.B5, false));

        board.applyMove(new Move(Pawn.WHITE_PAWN, Square.E2, Square.E4));
        assertFalse(PawnUtils.isSupported(board, Square.E4, true));

        board.applyMove(new Move(Knight.BLACK_KNIGHT, Square.G8, Square.F6));
        assertTrue(PawnUtils.isSupported(board, Square.F6, false));

        board.applyMove(new Move(Pawn.WHITE_PAWN, Square.D2, Square.D3));
        assertTrue(PawnUtils.isSupported(board, Square.E4, true));
        assertTrue(PawnUtils.isSupported(board, Square.D3, true));
    }
}
