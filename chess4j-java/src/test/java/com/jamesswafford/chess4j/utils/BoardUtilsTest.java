package com.jamesswafford.chess4j.utils;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.jamesswafford.chess4j.Color;
import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.movegen.MoveGen;
import com.jamesswafford.chess4j.board.squares.File;
import com.jamesswafford.chess4j.board.squares.Rank;
import com.jamesswafford.chess4j.board.squares.Square;
import com.jamesswafford.chess4j.pieces.Bishop;
import com.jamesswafford.chess4j.pieces.King;
import com.jamesswafford.chess4j.pieces.Knight;
import com.jamesswafford.chess4j.pieces.Pawn;
import com.jamesswafford.chess4j.pieces.Queen;

import static org.junit.Assert.*;

import static com.jamesswafford.chess4j.pieces.Pawn.*;
import static com.jamesswafford.chess4j.pieces.Knight.*;
import static com.jamesswafford.chess4j.pieces.Bishop.*;
import static com.jamesswafford.chess4j.pieces.Rook.*;
import static com.jamesswafford.chess4j.pieces.Queen.*;
import static com.jamesswafford.chess4j.pieces.King.*;
import static com.jamesswafford.chess4j.board.CastlingRights.*;
import static com.jamesswafford.chess4j.board.squares.Rank.*;
import static com.jamesswafford.chess4j.board.squares.Square.*;

import static com.jamesswafford.chess4j.utils.BoardUtils.*;

public class BoardUtilsTest {

    @Test
    public void testCountPawns_initialPos() {
        Board b = Board.INSTANCE;
        b.resetBoard();

        assertEquals(8, countPawns(b, Color.WHITE));
        assertEquals(8, countPawns(b, Color.BLACK));
    }

    @Test
    public void testCountPawns_pos1() {
        Board b = Board.INSTANCE;
        b.setPos("7k/pp6/8/8/8/8/7P/7K w - - ");

        assertEquals(1, countPawns(b, Color.WHITE));
        assertEquals(2, countPawns(b, Color.BLACK));
    }

    @Test
    public void testCountNonPawns_initialPos() {
        Board b = Board.INSTANCE;
        b.resetBoard();

        assertEquals(7, countNonPawns(b, Color.WHITE));
        assertEquals(7, countNonPawns(b, Color.BLACK));
    }

    @Test
    public void testCountNonPawns_pos1() {
        Board b = Board.INSTANCE;
        b.setPos("7k/br6/8/8/8/8/Q7/7K w - -");

        assertEquals(1, countNonPawns(b, Color.WHITE));
        assertEquals(2, countNonPawns(b, Color.BLACK));
    }

    @Test
    public void testIsDiagonal() {
        assertTrue(isDiagonal(A1, B2));
        assertFalse(isDiagonal(A1, A2));
        assertTrue(isDiagonal(D4, G7));
        assertTrue(isDiagonal(G7, B2));
        assertFalse(isDiagonal(C2, E2));
        assertTrue(isDiagonal(A8, H1));
    }

    @Test
    public void testIsPseudoLegalMove() {
        Board b = Board.INSTANCE;
        b.resetBoard();

        // all moves from the initial position
        assertAllMovesGood(b);

        // some moves that are not legal from the initial position
        assertFalse(isPseudoLegalMove(b, new Move(WHITE_KNIGHT, G1, E2)));
        assertFalse(isPseudoLegalMove(b, new Move(WHITE_KNIGHT, B1, C4)));

        // some pawn tests
        b.setPos("k7/P6P/8/4Pp2/1p6/3p4/1PPP4/K7 w - f6 0 1");
        assertAllMovesGood(b);
        assertFalse(isPseudoLegalMove(b, new Move(WHITE_PAWN, A7, A8, null, WHITE_QUEEN)));
        assertFalse(isPseudoLegalMove(b, new Move(WHITE_PAWN, A7, B8, null, WHITE_QUEEN)));
        assertFalse(isPseudoLegalMove(b, new Move(WHITE_PAWN, A7, B8, BLACK_PAWN, WHITE_QUEEN)));
        assertFalse(isPseudoLegalMove(b, new Move(WHITE_PAWN, A7, A8, BLACK_KING, WHITE_QUEEN)));

        b.flipVertical();
        assertAllMovesGood(b);

        b.setPos("r5k1/6rp/5B2/8/8/7R/7P/7K w - - 0 1");
        assertFalse(isPseudoLegalMove(b, new Move(WHITE_PAWN, H2, H4)));

        // some knight tests
        b.setPos("k7/8/4p3/6n1/4P3/8/8/K7 b - -");
        assertAllMovesGood(b);

        for (int i=0; i<64; i++) {
            Square toSq = Square.valueOf(i);
            Move testMove = new Move(BLACK_KNIGHT, G5, toSq, b.getPiece(toSq));

            assertEquals(Arrays.asList(F7, H7, E4, F3, H3).contains(toSq), isPseudoLegalMove(b, testMove));
        }

        // some bishop tests
        b.setPos("k7/8/1Q3q2/8/3b4/8/8/1K6 b - - 0 1");
        assertAllMovesGood(b);
        assertFalse(isPseudoLegalMove(b, new Move(BLACK_BISHOP, D4, F6, BLACK_QUEEN)));

        // some rook tests
        b.setPos("k7/8/1Q3q2/8/2PR1p2/8/8/1K6 w - - 0 1");
        assertAllMovesGood(b);

        // some queen tests
        b.setPos("k7/8/1Q3q2/8/2PQ1p2/8/8/1K6 w - - 0 1");
        assertAllMovesGood(b);

        // king tests
        b.setPos("r3kb1r/8/8/8/8/8/8/R3K2R b KQkq - 0 1");
        assertAllMovesGood(b);

        Move bogus = new Move(BLACK_KING, E8, G8, true);
        assertFalse(isPseudoLegalMove(b, bogus)); // bishop in the path
    }

    @Test
    public void testIsOpponentInCheck() {

        Board board = Board.INSTANCE;
        board.resetBoard();
        assertFalse(isOpponentInCheck(board));

        board.setPos("k7/8/Q7/K7/8/8/8/8 w - -");
        assertTrue(isOpponentInCheck(board));

        board.setPos("k7/8/1Q6/K7/8/8/8/8 w - -");
        assertFalse(isOpponentInCheck(board));
    }

    @Test
    public void testIsPlayerInCheck() {
        Board board = Board.INSTANCE;
        board.resetBoard();
        assertFalse(isPlayerInCheck(board));

        board.setPos("k7/8/Q7/K7/8/8/8/8 b - -");
        assertTrue(isPlayerInCheck(board));

        board.setPos("k7/8/1Q6/K7/8/8/8/8 b - -");
        assertFalse(isPlayerInCheck(board));
    }

    @Test
    public void testBlackCanCastleQueenSide() {
        Board board = Board.INSTANCE;
        board.setPos("r3k2r/8/8/8/8/8/8/R3K2R b KQkq - 0 1");

        assertTrue(blackCanCastleQueenSide(board));

        // verify that it's not allowed to cross check
        board.setPos("r3k2r/8/1Q6/8/8/8/8/R3K2R b KQkq - 0 1");
        assertFalse(blackCanCastleQueenSide(board));
    }

    @Test
    public void testBlackCanCastleKingSide() {
        Board board = Board.INSTANCE;
        board.setPos("r3k2r/8/8/8/8/8/8/R3K2R b KQkq - 0 1");

        assertTrue(blackCanCastleKingSide(board));

        // verify that it's not allowed to cross check
        board.setPos("r3k2r/8/6Q1/8/8/8/8/R3K2R b KQkq - 0 1");
        assertFalse(blackCanCastleKingSide(board));
    }

    @Test
    public void testWhiteCanCastleQueenSide() {
        Board board = Board.INSTANCE;
        board.setPos("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1");

        assertTrue(whiteCanCastleQueenSide(board));

        // verify that it's not allowed to cross check
        board.setPos("r3k2r/8/8/8/8/1q6/8/R3K2R w KQkq - 0 1");
        assertFalse(whiteCanCastleQueenSide(board));
    }

    @Test
    public void testWhiteCanCastleKingSide() {
        Board board = Board.INSTANCE;
        board.setPos("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1");

        assertTrue(whiteCanCastleKingSide(board));

        // verify that it's not allowed to cross check
        board.setPos("r3k2r/8/8/8/8/6q1/8/R3K2R w KQkq - 0 1");
        assertFalse(whiteCanCastleKingSide(board));
    }

    private void assertAllMovesGood(Board b) {
        List<Move> moves = MoveGen.genLegalMoves(b);

        for (Move m : moves) {
            assertTrue(isPseudoLegalMove(b, m));
        }
    }

}
