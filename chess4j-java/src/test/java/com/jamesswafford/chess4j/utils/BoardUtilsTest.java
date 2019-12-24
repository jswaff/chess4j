package com.jamesswafford.chess4j.utils;

import java.util.List;

import com.jamesswafford.chess4j.board.CastlingRights;
import org.junit.Test;

import com.jamesswafford.chess4j.Color;
import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.movegen.MoveGen;
import com.jamesswafford.chess4j.board.squares.File;
import com.jamesswafford.chess4j.board.squares.Rank;
import com.jamesswafford.chess4j.board.squares.Square;
import com.jamesswafford.chess4j.io.FenParser;
import com.jamesswafford.chess4j.pieces.Bishop;
import com.jamesswafford.chess4j.pieces.King;
import com.jamesswafford.chess4j.pieces.Knight;
import com.jamesswafford.chess4j.pieces.Pawn;
import com.jamesswafford.chess4j.pieces.Queen;

import static org.junit.Assert.*;

public class BoardUtilsTest {

    @Test
    public void testNumPawnsInitialPos() {
        Board b = Board.INSTANCE;
        b.resetBoard();

        assertEquals(8, BoardUtils.getNumPawns(b, Color.WHITE));

        assertEquals(8, BoardUtils.getNumPawns(b, Color.BLACK));
    }

    @Test
    public void testNumPawns() throws Exception {
        Board b = Board.INSTANCE;

        FenParser.setPos(b, "7k/pp6/8/8/8/8/7P/7K w - - ");

        assertEquals(1, BoardUtils.getNumPawns(b, Color.WHITE));

        assertEquals(2, BoardUtils.getNumPawns(b, Color.BLACK));
    }

    @Test
    public void testNumNonPawnsInitialPos() {
        Board b = Board.INSTANCE;
        b.resetBoard();

        assertEquals(7, BoardUtils.getNumNonPawns(b, Color.WHITE));

        assertEquals(7, BoardUtils.getNumNonPawns(b, Color.BLACK));
    }

    @Test
    public void testNumNonPawns() throws Exception {
        Board b = Board.INSTANCE;

        FenParser.setPos(b, "7k/br6/8/8/8/8/Q7/7K w - -");

        assertEquals(1, BoardUtils.getNumNonPawns(b, Color.WHITE));

        assertEquals(2, BoardUtils.getNumNonPawns(b, Color.BLACK));
    }

    @Test
    public void testIsDiagonal() {
        Square a1 = Square.valueOf(File.FILE_A,Rank.RANK_1);
        Square b2 = Square.valueOf(File.FILE_B,Rank.RANK_2);
        assertTrue(BoardUtils.isDiagonal(a1,b2));

        Square a2 = Square.valueOf(File.FILE_A,Rank.RANK_2);
        assertFalse(BoardUtils.isDiagonal(a1,a2));

        Square d4 = Square.valueOf(File.FILE_D,Rank.RANK_4);
        Square g7 = Square.valueOf(File.FILE_G,Rank.RANK_7);
        assertTrue(BoardUtils.isDiagonal(d4,g7));
        assertTrue(BoardUtils.isDiagonal(g7,b2));

        Square c2 = Square.valueOf(File.FILE_C,Rank.RANK_2);
        Square e2 = Square.valueOf(File.FILE_E,Rank.RANK_2);
        assertFalse(BoardUtils.isDiagonal(c2,e2));

        Square a8 = Square.valueOf(File.FILE_A,Rank.RANK_8);
        Square h1 = Square.valueOf(File.FILE_H,Rank.RANK_1);
        assertTrue(BoardUtils.isDiagonal(a8,h1));
    }

    private void assertAllMovesGood(Board b) {
        List<Move> moves = MoveGen.genLegalMoves(b);

        for (Move m : moves) {
            assertTrue(BoardUtils.isGoodMove(b, m));
        }
    }

    @Test
    public void testIsGoodMove() throws Exception {
        Board b = Board.INSTANCE;
        b.resetBoard();

        // all moves from the initial position
        assertAllMovesGood(b);

        // some moves that are not legal from the initial position
        assertFalse(BoardUtils.isGoodMove(b,
                new Move(Knight.WHITE_KNIGHT,
                        Square.valueOf(File.FILE_G, Rank.RANK_1),
                        Square.valueOf(File.FILE_E, Rank.RANK_2))));

        assertFalse(BoardUtils.isGoodMove(b,
                new Move(Knight.WHITE_KNIGHT,
                        Square.valueOf(File.FILE_B, Rank.RANK_1),
                        Square.valueOf(File.FILE_C, Rank.RANK_4))));

        // some pawn tests
        FenParser.setPos(b, "k7/P6P/8/4Pp2/1p6/3p4/1PPP4/K7 w - f6 0 1");
        assertAllMovesGood(b);
        assertFalse(BoardUtils.isGoodMove(b,
                new Move(Pawn.WHITE_PAWN,
                        Square.valueOf(File.FILE_A, Rank.RANK_7),
                        Square.valueOf(File.FILE_A, Rank.RANK_8),
                        null,Queen.WHITE_QUEEN)));

        assertFalse(BoardUtils.isGoodMove(b,
                new Move(Pawn.WHITE_PAWN,
                        Square.valueOf(File.FILE_A, Rank.RANK_7),
                        Square.valueOf(File.FILE_B, Rank.RANK_8),
                        null,Queen.WHITE_QUEEN)));

        assertFalse(BoardUtils.isGoodMove(b,
                new Move(Pawn.WHITE_PAWN,
                        Square.valueOf(File.FILE_A, Rank.RANK_7),
                        Square.valueOf(File.FILE_B, Rank.RANK_8),
                        Pawn.BLACK_PAWN,Queen.WHITE_QUEEN)));

        assertFalse(BoardUtils.isGoodMove(b,
                new Move(Pawn.WHITE_PAWN,
                        Square.valueOf(File.FILE_A, Rank.RANK_7),
                        Square.valueOf(File.FILE_A, Rank.RANK_8),
                        King.BLACK_KING,Queen.WHITE_QUEEN)));

        b.flipVertical();

        assertAllMovesGood(b);

        FenParser.setPos(b, "r5k1/6rp/5B2/8/8/7R/7P/7K w - - 0 1");
        assertFalse(BoardUtils.isGoodMove(b,
                new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_H, Rank.RANK_2),
                        Square.valueOf(File.FILE_H, Rank.RANK_4))));


        // some knight tests
        FenParser.setPos(b, "k7/8/4p3/6n1/4P3/8/8/K7 b - -");
        assertAllMovesGood(b);

        for (int i=0;i<64;i++) {
            Square toSq = Square.valueOf(i);
            Move testMove = new Move(Knight.BLACK_KNIGHT,Square.valueOf(File.FILE_G, Rank.RANK_5),
                    toSq,b.getPiece(toSq));

            assertTrue(BoardUtils.isGoodMove(b, testMove)==
                    (toSq==Square.valueOf(File.FILE_F, Rank.RANK_7)
                    || toSq==Square.valueOf(File.FILE_H,Rank.RANK_7)
                    || toSq==Square.valueOf(File.FILE_E,Rank.RANK_4)
                    || toSq==Square.valueOf(File.FILE_F, Rank.RANK_3)
                    || toSq==Square.valueOf(File.FILE_H, Rank.RANK_3)));
        }

        // some bishop tests
        FenParser.setPos(b, "k7/8/1Q3q2/8/3b4/8/8/1K6 b - - 0 1");
        assertAllMovesGood(b);
        assertFalse(BoardUtils.isGoodMove(b, new Move(Bishop.BLACK_BISHOP,
                Square.valueOf(File.FILE_D, Rank.RANK_4),
                Square.valueOf(File.FILE_F, Rank.RANK_6),
                Queen.BLACK_QUEEN)));

        // some rook tests
        FenParser.setPos(b, "k7/8/1Q3q2/8/2PR1p2/8/8/1K6 w - - 0 1");
        assertAllMovesGood(b);

        // some queen tests
        FenParser.setPos(b, "k7/8/1Q3q2/8/2PQ1p2/8/8/1K6 w - - 0 1");
        assertAllMovesGood(b);

        // king tests
        FenParser.setPos(b, "r3kb1r/8/8/8/8/8/8/R3K2R b KQkq - 0 1");
        assertAllMovesGood(b);

        Move bogus = new Move(King.BLACK_KING,
                Square.valueOf(File.FILE_E, Rank.RANK_8),
                Square.valueOf(File.FILE_G, Rank.RANK_8),
                true);
        assertFalse(BoardUtils.isGoodMove(b, bogus)); // bishop in the path
    }

    @Test
    public void testBlackCanCastleQueenSide() throws Exception {
        Board board = Board.INSTANCE;
        FenParser.setPos(board, "r3k2r/8/8/8/8/8/8/R3K2R b KQkq - 0 1");

        assertTrue(BoardUtils.blackCanCastleQueenSide(board));

        board.clearCastlingRight(CastlingRights.BLACK_QUEENSIDE);
        assertFalse(BoardUtils.blackCanCastleQueenSide(board));
        board.addCastlingRight(CastlingRights.BLACK_QUEENSIDE);
        assertTrue(BoardUtils.blackCanCastleQueenSide(board));

        // verify that it's not allowed to cross check
        FenParser.setPos(board, "r3k2r/8/1Q6/8/8/8/8/R3K2R b KQkq - 0 1");
        assertFalse(BoardUtils.blackCanCastleQueenSide(board));
    }

    @Test
    public void testBlackCanCastleKingSide() throws Exception {
        Board board = Board.INSTANCE;
        FenParser.setPos(board, "r3k2r/8/8/8/8/8/8/R3K2R b KQkq - 0 1");

        assertTrue(BoardUtils.blackCanCastleKingSide(board));

        board.clearCastlingRight(CastlingRights.BLACK_KINGSIDE);
        assertFalse(BoardUtils.blackCanCastleKingSide(board));
        board.addCastlingRight(CastlingRights.BLACK_KINGSIDE);
        assertTrue(BoardUtils.blackCanCastleKingSide(board));

        // verify that it's not allowed to cross check
        FenParser.setPos(board, "r3k2r/8/6Q1/8/8/8/8/R3K2R b KQkq - 0 1");
        assertFalse(BoardUtils.blackCanCastleKingSide(board));
    }


    @Test
    public void testWhiteCanCastleQueenSide() throws Exception {
        Board board = Board.INSTANCE;
        FenParser.setPos(board, "r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1");

        assertTrue(BoardUtils.whiteCanCastleQueenSide(board));

        board.clearCastlingRight(CastlingRights.WHITE_QUEENSIDE);
        assertFalse(BoardUtils.whiteCanCastleQueenSide(board));
        board.addCastlingRight(CastlingRights.WHITE_QUEENSIDE);
        assertTrue(BoardUtils.whiteCanCastleQueenSide(board));

        // verify that it's not allowed to cross check
        FenParser.setPos(board, "r3k2r/8/8/8/8/1q6/8/R3K2R w KQkq - 0 1");
        assertFalse(BoardUtils.whiteCanCastleQueenSide(board));
    }


    @Test
    public void testWhiteCanCastleKingSide() throws Exception {
        Board board = Board.INSTANCE;
        FenParser.setPos(board, "r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1");

        assertTrue(BoardUtils.whiteCanCastleKingSide(board));

        board.clearCastlingRight(CastlingRights.WHITE_KINGSIDE);
        assertFalse(BoardUtils.whiteCanCastleKingSide(board));
        board.addCastlingRight(CastlingRights.WHITE_KINGSIDE);
        assertTrue(BoardUtils.whiteCanCastleKingSide(board));

        // verify that it's not allowed to cross check
        FenParser.setPos(board, "r3k2r/8/8/8/8/6q1/8/R3K2R w KQkq - 0 1");
        assertFalse(BoardUtils.whiteCanCastleKingSide(board));
    }

}
