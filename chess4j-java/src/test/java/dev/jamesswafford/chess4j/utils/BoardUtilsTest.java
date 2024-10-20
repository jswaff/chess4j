package dev.jamesswafford.chess4j.utils;

import java.util.Arrays;
import java.util.List;

import dev.jamesswafford.chess4j.board.Bitboard;
import dev.jamesswafford.chess4j.board.Board;
import dev.jamesswafford.chess4j.board.Color;
import dev.jamesswafford.chess4j.board.Move;
import dev.jamesswafford.chess4j.board.squares.Square;
import dev.jamesswafford.chess4j.pieces.*;
import org.junit.Assert;
import org.junit.Test;

import dev.jamesswafford.chess4j.movegen.MagicBitboardMoveGenerator;

import static org.junit.Assert.*;

public class BoardUtilsTest {

    @Test
    public void testAllSquares() {
        long v = Bitboard.ALL_SQUARES;
        assertEquals(64, Long.bitCount(v));

        System.out.println(Bitboard.drawBitboard(v));
        System.out.println(Bitboard.drawBitboard(Bitboard.LOWER16));
    }

    @Test
    public void testCountPawns_initialPos() {
        Board board = new Board();

        Assert.assertEquals(8, BoardUtils.countPawns(board, Color.WHITE));
        Assert.assertEquals(8, BoardUtils.countPawns(board, Color.BLACK));
    }

    @Test
    public void testCountPawns_pos1() {
        Board board = new Board("7k/pp6/8/8/8/8/7P/7K w - - ");

        Assert.assertEquals(1, BoardUtils.countPawns(board, Color.WHITE));
        Assert.assertEquals(2, BoardUtils.countPawns(board, Color.BLACK));
    }

    @Test
    public void testCountNonPawns_initialPos() {
        Board board = new Board();

        Assert.assertEquals(7, BoardUtils.countNonPawns(board, Color.WHITE));
        Assert.assertEquals(7, BoardUtils.countNonPawns(board, Color.BLACK));
    }

    @Test
    public void testCountNonPawns_pos1() {
        Board board = new Board("7k/br6/8/8/8/8/Q7/7K w - -");

        Assert.assertEquals(1, BoardUtils.countNonPawns(board, Color.WHITE));
        Assert.assertEquals(2, BoardUtils.countNonPawns(board, Color.BLACK));
    }

    @Test
    public void testIsDiagonal() {
        assertTrue(BoardUtils.isDiagonal(Square.A1, Square.B2));
        assertFalse(BoardUtils.isDiagonal(Square.A1, Square.A2));
        assertTrue(BoardUtils.isDiagonal(Square.D4, Square.G7));
        assertTrue(BoardUtils.isDiagonal(Square.G7, Square.B2));
        assertFalse(BoardUtils.isDiagonal(Square.C2, Square.E2));
        assertTrue(BoardUtils.isDiagonal(Square.A8, Square.H1));
    }

    @Test
    public void testIsPseudoLegalMove() {
        Board board = new Board();

        // all moves from the initial position
        assertAllMovesGood(board);

        // some moves that are not legal from the initial position
        assertFalse(BoardUtils.isPseudoLegalMove(board, new Move(Knight.WHITE_KNIGHT, Square.G1, Square.E2)));
        assertFalse(BoardUtils.isPseudoLegalMove(board, new Move(Knight.WHITE_KNIGHT, Square.B1, Square.C4)));

        // some pawn tests
        board.setPos("k7/P6P/8/4Pp2/1p6/3p4/1PPP4/K7 w - f6 0 1");
        assertAllMovesGood(board);
        assertFalse(BoardUtils.isPseudoLegalMove(board, new Move(Pawn.WHITE_PAWN, Square.A7, Square.A8, null, Queen.WHITE_QUEEN)));
        assertFalse(BoardUtils.isPseudoLegalMove(board, new Move(Pawn.WHITE_PAWN, Square.A7, Square.B8, null, Queen.WHITE_QUEEN)));
        assertFalse(BoardUtils.isPseudoLegalMove(board, new Move(Pawn.WHITE_PAWN, Square.A7, Square.B8, Pawn.BLACK_PAWN, Queen.WHITE_QUEEN)));
        assertFalse(BoardUtils.isPseudoLegalMove(board, new Move(Pawn.WHITE_PAWN, Square.A7, Square.A8, King.BLACK_KING, Queen.WHITE_QUEEN)));

        board.flipVertical();
        assertAllMovesGood(board);

        board.setPos("r5k1/6rp/5B2/8/8/7R/7P/7K w - - 0 1");
        assertFalse(BoardUtils.isPseudoLegalMove(board, new Move(Pawn.WHITE_PAWN, Square.H2, Square.H4)));

        // some knight tests
        board.setPos("k7/8/4p3/6n1/4P3/8/8/K7 b - -");
        assertAllMovesGood(board);

        for (int i=0; i<64; i++) {
            Square toSq = Square.valueOf(i);
            Move testMove = new Move(Knight.BLACK_KNIGHT, Square.G5, toSq, board.getPiece(toSq));

            Assert.assertEquals(Arrays.asList(Square.F7, Square.H7, Square.E4, Square.F3, Square.H3).contains(toSq), BoardUtils.isPseudoLegalMove(board, testMove));
        }

        // some bishop tests
        board.setPos("k7/8/1Q3q2/8/3b4/8/8/1K6 b - - 0 1");
        assertAllMovesGood(board);
        assertFalse(BoardUtils.isPseudoLegalMove(board, new Move(Bishop.BLACK_BISHOP, Square.D4, Square.F6, Queen.BLACK_QUEEN)));

        // some rook tests
        board.setPos("k7/8/1Q3q2/8/2PR1p2/8/8/1K6 w - - 0 1");
        assertAllMovesGood(board);

        // some queen tests
        board.setPos("k7/8/1Q3q2/8/2PQ1p2/8/8/1K6 w - - 0 1");
        assertAllMovesGood(board);

        // king tests
        board.setPos("r3kb1r/8/8/8/8/8/8/R3K2R b KQkq - 0 1");
        assertAllMovesGood(board);

        Move bogus = new Move(King.BLACK_KING, Square.E8, Square.G8, true);
        assertFalse(BoardUtils.isPseudoLegalMove(board, bogus)); // bishop in the path
    }

    @Test
    public void testIsOpponentInCheck() {

        Board board = new Board();
        assertFalse(BoardUtils.isOpponentInCheck(board));

        board.setPos("k7/8/Q7/K7/8/8/8/8 w - -");
        assertTrue(BoardUtils.isOpponentInCheck(board));

        board.setPos("k7/8/1Q6/K7/8/8/8/8 w - -");
        assertFalse(BoardUtils.isOpponentInCheck(board));
    }

    @Test
    public void testIsPlayerInCheck() {
        Board board = new Board();
        assertFalse(BoardUtils.isPlayerInCheck(board));

        board.setPos("k7/8/Q7/K7/8/8/8/8 b - -");
        assertTrue(BoardUtils.isPlayerInCheck(board));

        board.setPos("k7/8/1Q6/K7/8/8/8/8 b - -");
        assertFalse(BoardUtils.isPlayerInCheck(board));
    }

    @Test
    public void testBlackCanCastleQueenSide() {
        Board board = new Board("r3k2r/8/8/8/8/8/8/R3K2R b KQkq - 0 1");

        assertTrue(BoardUtils.blackCanCastleQueenSide(board));

        // verify that it's not allowed to cross check
        board.setPos("r3k2r/8/1Q6/8/8/8/8/R3K2R b KQkq - 0 1");
        assertFalse(BoardUtils.blackCanCastleQueenSide(board));
    }

    @Test
    public void testBlackCanCastleKingSide() {
        Board board = new Board("r3k2r/8/8/8/8/8/8/R3K2R b KQkq - 0 1");

        assertTrue(BoardUtils.blackCanCastleKingSide(board));

        // verify that it's not allowed to cross check
        board.setPos("r3k2r/8/6Q1/8/8/8/8/R3K2R b KQkq - 0 1");
        assertFalse(BoardUtils.blackCanCastleKingSide(board));
    }

    @Test
    public void testWhiteCanCastleQueenSide() {
        Board board = new Board("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1");

        assertTrue(BoardUtils.whiteCanCastleQueenSide(board));

        // verify that it's not allowed to cross check
        board.setPos("r3k2r/8/8/8/8/1q6/8/R3K2R w KQkq - 0 1");
        assertFalse(BoardUtils.whiteCanCastleQueenSide(board));
    }

    @Test
    public void testWhiteCanCastleKingSide() {
        Board board = new Board("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1");

        assertTrue(BoardUtils.whiteCanCastleKingSide(board));

        // verify that it's not allowed to cross check
        board.setPos("r3k2r/8/8/8/8/6q1/8/R3K2R w KQkq - 0 1");
        assertFalse(BoardUtils.whiteCanCastleKingSide(board));
    }

    private void assertAllMovesGood(Board b) {
        List<Move> moves = MagicBitboardMoveGenerator.genLegalMoves(b);

        for (Move m : moves) {
            assertTrue(BoardUtils.isPseudoLegalMove(b, m));
        }
    }

}
