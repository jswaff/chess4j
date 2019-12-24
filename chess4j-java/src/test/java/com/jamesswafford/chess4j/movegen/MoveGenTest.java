package com.jamesswafford.chess4j.movegen;

import java.util.ArrayList;
import java.util.List;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Move;

import org.junit.Test;

import com.jamesswafford.chess4j.board.squares.Square;
import com.jamesswafford.chess4j.io.EPDParser;
import com.jamesswafford.chess4j.io.FenParser;
import com.jamesswafford.chess4j.io.MoveParser;

import static org.junit.Assert.*;

import static com.jamesswafford.chess4j.pieces.Pawn.*;
import static com.jamesswafford.chess4j.pieces.Knight.*;
import static com.jamesswafford.chess4j.pieces.Bishop.*;
import static com.jamesswafford.chess4j.pieces.Rook.*;
import static com.jamesswafford.chess4j.pieces.Queen.*;
import static com.jamesswafford.chess4j.pieces.King.*;
import static com.jamesswafford.chess4j.board.squares.File.*;
import static com.jamesswafford.chess4j.board.squares.Rank.*;

public class MoveGenTest {

    @Test
    public void testKnightMoves() {
        Board b = Board.INSTANCE;
        b.resetBoard();

        List<Move> moves = new ArrayList<>();
        MoveGen.genKnightMoves(b,moves,true,true);

        assertEquals(4, moves.size());
        assertTrue(moves.contains(new Move(WHITE_KNIGHT,Square.valueOf(FILE_B, RANK_1),Square.valueOf(FILE_A, RANK_3))));
        assertTrue(moves.contains(new Move(WHITE_KNIGHT,Square.valueOf(FILE_B, RANK_1),Square.valueOf(FILE_C, RANK_3))));
        assertTrue(moves.contains(new Move(WHITE_KNIGHT,Square.valueOf(FILE_G, RANK_1),Square.valueOf(FILE_F, RANK_3))));
        assertTrue(moves.contains(new Move(WHITE_KNIGHT,Square.valueOf(FILE_G, RANK_1),Square.valueOf(FILE_H, RANK_3))));
    }

    @Test
    public void testKnightCaptures() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "4k3/8/3P1p2/8/4N3/8/8/4K3 w - - 0 1");

        List<Move> moves = new ArrayList<>();
        MoveGen.genKnightMoves(b,moves,true,false);

        assertEquals(1, moves.size());
        assertTrue(moves.contains(new Move(WHITE_KNIGHT,Square.valueOf(FILE_E, RANK_4),Square.valueOf(FILE_F, RANK_6),BLACK_PAWN)));
    }

    @Test
    public void testKnightNoncaptures() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "4k3/8/3P1p2/8/4N3/8/8/4K3 w - - 0 1");

        List<Move> moves = new ArrayList<>();
        MoveGen.genKnightMoves(b,moves,false,true);

        assertEquals(6, moves.size());
        assertTrue(moves.contains(new Move(WHITE_KNIGHT,Square.valueOf(FILE_E, RANK_4),Square.valueOf(FILE_G, RANK_5))));
        assertTrue(moves.contains(new Move(WHITE_KNIGHT,Square.valueOf(FILE_E, RANK_4),Square.valueOf(FILE_G, RANK_3))));
        assertTrue(moves.contains(new Move(WHITE_KNIGHT,Square.valueOf(FILE_E, RANK_4),Square.valueOf(FILE_F, RANK_2))));
        assertTrue(moves.contains(new Move(WHITE_KNIGHT,Square.valueOf(FILE_E, RANK_4),Square.valueOf(FILE_D, RANK_2))));
        assertTrue(moves.contains(new Move(WHITE_KNIGHT,Square.valueOf(FILE_E, RANK_4),Square.valueOf(FILE_C, RANK_3))));
        assertTrue(moves.contains(new Move(WHITE_KNIGHT,Square.valueOf(FILE_E, RANK_4),Square.valueOf(FILE_C, RANK_5))));
    }

    @Test
    public void testBishopMoves() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "4k3/2p3P1/8/4B3/4B3/3p1P2/8/4K3 w - - 0 1");
        List<Move> moves = new ArrayList<>();
        MoveGen.genBishopMoves(b, moves, true, true);
        assertEquals(18, moves.size());
        assertTrue(moves.contains(new Move(WHITE_BISHOP,Square.valueOf(FILE_E, RANK_5),Square.valueOf(FILE_F, RANK_6))));
        assertTrue(moves.contains(new Move(WHITE_BISHOP,Square.valueOf(FILE_E, RANK_5),Square.valueOf(FILE_F, RANK_4))));
        assertTrue(moves.contains(new Move(WHITE_BISHOP,Square.valueOf(FILE_E, RANK_5),Square.valueOf(FILE_G, RANK_3))));
        assertTrue(moves.contains(new Move(WHITE_BISHOP,Square.valueOf(FILE_E, RANK_5),Square.valueOf(FILE_H, RANK_2))));
        assertTrue(moves.contains(new Move(WHITE_BISHOP,Square.valueOf(FILE_E, RANK_5),Square.valueOf(FILE_D, RANK_4))));
        assertTrue(moves.contains(new Move(WHITE_BISHOP,Square.valueOf(FILE_E, RANK_5),Square.valueOf(FILE_C, RANK_3))));
        assertTrue(moves.contains(new Move(WHITE_BISHOP,Square.valueOf(FILE_E, RANK_5),Square.valueOf(FILE_B, RANK_2))));
        assertTrue(moves.contains(new Move(WHITE_BISHOP,Square.valueOf(FILE_E, RANK_5),Square.valueOf(FILE_A, RANK_1))));
        assertTrue(moves.contains(new Move(WHITE_BISHOP,Square.valueOf(FILE_E, RANK_5),Square.valueOf(FILE_D, RANK_6))));
        assertTrue(moves.contains(new Move(WHITE_BISHOP,Square.valueOf(FILE_E, RANK_5),Square.valueOf(FILE_C, RANK_7),BLACK_PAWN)));
        assertTrue(moves.contains(new Move(WHITE_BISHOP,Square.valueOf(FILE_E, RANK_4),Square.valueOf(FILE_F, RANK_5))));
        assertTrue(moves.contains(new Move(WHITE_BISHOP,Square.valueOf(FILE_E, RANK_4),Square.valueOf(FILE_G, RANK_6))));
        assertTrue(moves.contains(new Move(WHITE_BISHOP,Square.valueOf(FILE_E, RANK_4),Square.valueOf(FILE_H, RANK_7))));
        assertTrue(moves.contains(new Move(WHITE_BISHOP,Square.valueOf(FILE_E, RANK_4),Square.valueOf(FILE_D, RANK_3),BLACK_PAWN)));
        assertTrue(moves.contains(new Move(WHITE_BISHOP,Square.valueOf(FILE_E, RANK_4),Square.valueOf(FILE_D, RANK_5))));
        assertTrue(moves.contains(new Move(WHITE_BISHOP,Square.valueOf(FILE_E, RANK_4),Square.valueOf(FILE_C, RANK_6))));
        assertTrue(moves.contains(new Move(WHITE_BISHOP,Square.valueOf(FILE_E, RANK_4),Square.valueOf(FILE_B, RANK_7))));
        assertTrue(moves.contains(new Move(WHITE_BISHOP,Square.valueOf(FILE_E, RANK_4),Square.valueOf(FILE_A, RANK_8))));
    }

    @Test
    public void testBishopCaptures() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "4k3/2p3P1/8/4B3/4B3/3p1P2/8/4K3 w - - 0 1");
        List<Move> moves = new ArrayList<>();
        MoveGen.genBishopMoves(b, moves, true, false);
        assertEquals(2, moves.size());
        assertTrue(moves.contains(new Move(WHITE_BISHOP,Square.valueOf(FILE_E, RANK_5),Square.valueOf(FILE_C, RANK_7),BLACK_PAWN)));
        assertTrue(moves.contains(new Move(WHITE_BISHOP,Square.valueOf(FILE_E, RANK_4),Square.valueOf(FILE_D, RANK_3),BLACK_PAWN)));
    }

    @Test
    public void testBishopNoncaptures() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "4k3/2p3P1/8/4B3/4B3/3p1P2/8/4K3 w - - 0 1");
        List<Move> moves = new ArrayList<>();
        MoveGen.genBishopMoves(b, moves, false, true);
        assertEquals(16, moves.size());
        assertTrue(moves.contains(new Move(WHITE_BISHOP,Square.valueOf(FILE_E, RANK_5),Square.valueOf(FILE_F, RANK_6))));
        assertTrue(moves.contains(new Move(WHITE_BISHOP,Square.valueOf(FILE_E, RANK_5),Square.valueOf(FILE_F, RANK_4))));
        assertTrue(moves.contains(new Move(WHITE_BISHOP,Square.valueOf(FILE_E, RANK_5),Square.valueOf(FILE_G, RANK_3))));
        assertTrue(moves.contains(new Move(WHITE_BISHOP,Square.valueOf(FILE_E, RANK_5),Square.valueOf(FILE_H, RANK_2))));
        assertTrue(moves.contains(new Move(WHITE_BISHOP,Square.valueOf(FILE_E, RANK_5),Square.valueOf(FILE_D, RANK_4))));
        assertTrue(moves.contains(new Move(WHITE_BISHOP,Square.valueOf(FILE_E, RANK_5),Square.valueOf(FILE_C, RANK_3))));
        assertTrue(moves.contains(new Move(WHITE_BISHOP,Square.valueOf(FILE_E, RANK_5),Square.valueOf(FILE_B, RANK_2))));
        assertTrue(moves.contains(new Move(WHITE_BISHOP,Square.valueOf(FILE_E, RANK_5),Square.valueOf(FILE_A, RANK_1))));
        assertTrue(moves.contains(new Move(WHITE_BISHOP,Square.valueOf(FILE_E, RANK_5),Square.valueOf(FILE_D, RANK_6))));
        assertTrue(moves.contains(new Move(WHITE_BISHOP,Square.valueOf(FILE_E, RANK_4),Square.valueOf(FILE_F, RANK_5))));
        assertTrue(moves.contains(new Move(WHITE_BISHOP,Square.valueOf(FILE_E, RANK_4),Square.valueOf(FILE_G, RANK_6))));
        assertTrue(moves.contains(new Move(WHITE_BISHOP,Square.valueOf(FILE_E, RANK_4),Square.valueOf(FILE_H, RANK_7))));
        assertTrue(moves.contains(new Move(WHITE_BISHOP,Square.valueOf(FILE_E, RANK_4),Square.valueOf(FILE_D, RANK_5))));
        assertTrue(moves.contains(new Move(WHITE_BISHOP,Square.valueOf(FILE_E, RANK_4),Square.valueOf(FILE_C, RANK_6))));
        assertTrue(moves.contains(new Move(WHITE_BISHOP,Square.valueOf(FILE_E, RANK_4),Square.valueOf(FILE_B, RANK_7))));
        assertTrue(moves.contains(new Move(WHITE_BISHOP,Square.valueOf(FILE_E, RANK_4),Square.valueOf(FILE_A, RANK_8))));
    }

    @Test
    public void testRookMoves() throws Exception {
        Board b = Board.INSTANCE;
        b.resetBoard();

        List<Move> moves = new ArrayList<>();
        MoveGen.genRookMoves(b, moves, true, true);
        assertEquals(0, moves.size());

        FenParser.setPos(b, "8/8/3k1p2/8/3K4/8/1R3r2/8 b - - 0 1");
        MoveGen.genRookMoves(b, moves, true, true);

        assertEquals(10, moves.size());
        assertTrue(moves.contains(new Move(BLACK_ROOK,Square.valueOf(FILE_F, RANK_2),Square.valueOf(FILE_F, RANK_3))));
        assertTrue(moves.contains(new Move(BLACK_ROOK,Square.valueOf(FILE_F, RANK_2),Square.valueOf(FILE_F, RANK_4))));
        assertTrue(moves.contains(new Move(BLACK_ROOK,Square.valueOf(FILE_F, RANK_2),Square.valueOf(FILE_F, RANK_5))));
        assertTrue(moves.contains(new Move(BLACK_ROOK,Square.valueOf(FILE_F, RANK_2),Square.valueOf(FILE_F, RANK_1))));
        assertTrue(moves.contains(new Move(BLACK_ROOK,Square.valueOf(FILE_F, RANK_2),Square.valueOf(FILE_G, RANK_2))));
        assertTrue(moves.contains(new Move(BLACK_ROOK,Square.valueOf(FILE_F, RANK_2),Square.valueOf(FILE_H, RANK_2))));
        assertTrue(moves.contains(new Move(BLACK_ROOK,Square.valueOf(FILE_F, RANK_2),Square.valueOf(FILE_E, RANK_2))));
        assertTrue(moves.contains(new Move(BLACK_ROOK,Square.valueOf(FILE_F, RANK_2),Square.valueOf(FILE_D, RANK_2))));
        assertTrue(moves.contains(new Move(BLACK_ROOK,Square.valueOf(FILE_F, RANK_2),Square.valueOf(FILE_C, RANK_2))));
        assertTrue(moves.contains(new Move(BLACK_ROOK,Square.valueOf(FILE_F, RANK_2),Square.valueOf(FILE_B, RANK_2),WHITE_ROOK)));
    }

    @Test
    public void testRookCaptures() throws Exception {
        Board b = Board.INSTANCE;

        List<Move> moves = new ArrayList<>();
        FenParser.setPos(b, "8/8/3k1p2/8/3K4/8/1R3r2/8 b - - 0 1");
        MoveGen.genRookMoves(b, moves, true, false);

        assertEquals(1, moves.size());
        assertTrue(moves.contains(new Move(BLACK_ROOK,Square.valueOf(FILE_F, RANK_2),Square.valueOf(FILE_B, RANK_2),WHITE_ROOK)));
    }

    @Test
    public void testRookNoncaptures() throws Exception {
        Board b = Board.INSTANCE;

        List<Move> moves = new ArrayList<>();
        FenParser.setPos(b, "8/8/3k1p2/8/3K4/8/1R3r2/8 b - - 0 1");
        MoveGen.genRookMoves(b, moves, false, true);

        assertEquals(9, moves.size());
        assertTrue(moves.contains(new Move(BLACK_ROOK,Square.valueOf(FILE_F, RANK_2),Square.valueOf(FILE_F, RANK_3))));
        assertTrue(moves.contains(new Move(BLACK_ROOK,Square.valueOf(FILE_F, RANK_2),Square.valueOf(FILE_F, RANK_4))));
        assertTrue(moves.contains(new Move(BLACK_ROOK,Square.valueOf(FILE_F, RANK_2),Square.valueOf(FILE_F, RANK_5))));
        assertTrue(moves.contains(new Move(BLACK_ROOK,Square.valueOf(FILE_F, RANK_2),Square.valueOf(FILE_F, RANK_1))));
        assertTrue(moves.contains(new Move(BLACK_ROOK,Square.valueOf(FILE_F, RANK_2),Square.valueOf(FILE_G, RANK_2))));
        assertTrue(moves.contains(new Move(BLACK_ROOK,Square.valueOf(FILE_F, RANK_2),Square.valueOf(FILE_H, RANK_2))));
        assertTrue(moves.contains(new Move(BLACK_ROOK,Square.valueOf(FILE_F, RANK_2),Square.valueOf(FILE_E, RANK_2))));
        assertTrue(moves.contains(new Move(BLACK_ROOK,Square.valueOf(FILE_F, RANK_2),Square.valueOf(FILE_D, RANK_2))));
        assertTrue(moves.contains(new Move(BLACK_ROOK,Square.valueOf(FILE_F, RANK_2),Square.valueOf(FILE_C, RANK_2))));
    }

    @Test
    public void testQueenMoves() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "8/8/3bk3/8/8/2K3Q1/8/8 w - - 0 1");

        List<Move> moves = new ArrayList<>();
        MoveGen.genQueenMoves(b, moves, true, true);
        assertEquals(18, moves.size());
        assertTrue(moves.contains(new Move(WHITE_QUEEN,Square.valueOf(FILE_G, RANK_3),Square.valueOf(FILE_G, RANK_4))));
        assertTrue(moves.contains(new Move(WHITE_QUEEN,Square.valueOf(FILE_G, RANK_3),Square.valueOf(FILE_G, RANK_5))));
        assertTrue(moves.contains(new Move(WHITE_QUEEN,Square.valueOf(FILE_G, RANK_3),Square.valueOf(FILE_G, RANK_6))));
        assertTrue(moves.contains(new Move(WHITE_QUEEN,Square.valueOf(FILE_G, RANK_3),Square.valueOf(FILE_G, RANK_7))));
        assertTrue(moves.contains(new Move(WHITE_QUEEN,Square.valueOf(FILE_G, RANK_3),Square.valueOf(FILE_G, RANK_8))));
        assertTrue(moves.contains(new Move(WHITE_QUEEN,Square.valueOf(FILE_G, RANK_3),Square.valueOf(FILE_H, RANK_4))));
        assertTrue(moves.contains(new Move(WHITE_QUEEN,Square.valueOf(FILE_G, RANK_3),Square.valueOf(FILE_H, RANK_3))));
        assertTrue(moves.contains(new Move(WHITE_QUEEN,Square.valueOf(FILE_G, RANK_3),Square.valueOf(FILE_H, RANK_2))));
        assertTrue(moves.contains(new Move(WHITE_QUEEN,Square.valueOf(FILE_G, RANK_3),Square.valueOf(FILE_G, RANK_2))));
        assertTrue(moves.contains(new Move(WHITE_QUEEN,Square.valueOf(FILE_G, RANK_3),Square.valueOf(FILE_G, RANK_1))));
        assertTrue(moves.contains(new Move(WHITE_QUEEN,Square.valueOf(FILE_G, RANK_3),Square.valueOf(FILE_F, RANK_2))));
        assertTrue(moves.contains(new Move(WHITE_QUEEN,Square.valueOf(FILE_G, RANK_3),Square.valueOf(FILE_E, RANK_1))));
        assertTrue(moves.contains(new Move(WHITE_QUEEN,Square.valueOf(FILE_G, RANK_3),Square.valueOf(FILE_F, RANK_3))));
        assertTrue(moves.contains(new Move(WHITE_QUEEN,Square.valueOf(FILE_G, RANK_3),Square.valueOf(FILE_E, RANK_3))));
        assertTrue(moves.contains(new Move(WHITE_QUEEN,Square.valueOf(FILE_G, RANK_3),Square.valueOf(FILE_D, RANK_3))));
        assertTrue(moves.contains(new Move(WHITE_QUEEN,Square.valueOf(FILE_G, RANK_3),Square.valueOf(FILE_F, RANK_4))));
        assertTrue(moves.contains(new Move(WHITE_QUEEN,Square.valueOf(FILE_G, RANK_3),Square.valueOf(FILE_E, RANK_5))));
        assertTrue(moves.contains(new Move(WHITE_QUEEN,Square.valueOf(FILE_G, RANK_3),Square.valueOf(FILE_D, RANK_6),BLACK_BISHOP)));
    }

    @Test
    public void testQueenCaptures() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "8/8/3bk3/8/8/2K3Q1/8/8 w - - 0 1");

        List<Move> moves = new ArrayList<>();
        MoveGen.genQueenMoves(b, moves, true, false);
        assertEquals(1, moves.size());
        assertTrue(moves.contains(new Move(WHITE_QUEEN,Square.valueOf(FILE_G, RANK_3),Square.valueOf(FILE_D, RANK_6),BLACK_BISHOP)));
    }

    @Test
    public void testQueenNoncaptures() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "8/8/3bk3/8/8/2K3Q1/8/8 w - - 0 1");

        List<Move> moves = new ArrayList<>();
        MoveGen.genQueenMoves(b, moves, false, true);
        assertEquals(17, moves.size());
        assertTrue(moves.contains(new Move(WHITE_QUEEN,Square.valueOf(FILE_G, RANK_3),Square.valueOf(FILE_G, RANK_4))));
        assertTrue(moves.contains(new Move(WHITE_QUEEN,Square.valueOf(FILE_G, RANK_3),Square.valueOf(FILE_G, RANK_5))));
        assertTrue(moves.contains(new Move(WHITE_QUEEN,Square.valueOf(FILE_G, RANK_3),Square.valueOf(FILE_G, RANK_6))));
        assertTrue(moves.contains(new Move(WHITE_QUEEN,Square.valueOf(FILE_G, RANK_3),Square.valueOf(FILE_G, RANK_7))));
        assertTrue(moves.contains(new Move(WHITE_QUEEN,Square.valueOf(FILE_G, RANK_3),Square.valueOf(FILE_G, RANK_8))));
        assertTrue(moves.contains(new Move(WHITE_QUEEN,Square.valueOf(FILE_G, RANK_3),Square.valueOf(FILE_H, RANK_4))));
        assertTrue(moves.contains(new Move(WHITE_QUEEN,Square.valueOf(FILE_G, RANK_3),Square.valueOf(FILE_H, RANK_3))));
        assertTrue(moves.contains(new Move(WHITE_QUEEN,Square.valueOf(FILE_G, RANK_3),Square.valueOf(FILE_H, RANK_2))));
        assertTrue(moves.contains(new Move(WHITE_QUEEN,Square.valueOf(FILE_G, RANK_3),Square.valueOf(FILE_G, RANK_2))));
        assertTrue(moves.contains(new Move(WHITE_QUEEN,Square.valueOf(FILE_G, RANK_3),Square.valueOf(FILE_G, RANK_1))));
        assertTrue(moves.contains(new Move(WHITE_QUEEN,Square.valueOf(FILE_G, RANK_3),Square.valueOf(FILE_F, RANK_2))));
        assertTrue(moves.contains(new Move(WHITE_QUEEN,Square.valueOf(FILE_G, RANK_3),Square.valueOf(FILE_E, RANK_1))));
        assertTrue(moves.contains(new Move(WHITE_QUEEN,Square.valueOf(FILE_G, RANK_3),Square.valueOf(FILE_F, RANK_3))));
        assertTrue(moves.contains(new Move(WHITE_QUEEN,Square.valueOf(FILE_G, RANK_3),Square.valueOf(FILE_E, RANK_3))));
        assertTrue(moves.contains(new Move(WHITE_QUEEN,Square.valueOf(FILE_G, RANK_3),Square.valueOf(FILE_D, RANK_3))));
        assertTrue(moves.contains(new Move(WHITE_QUEEN,Square.valueOf(FILE_G, RANK_3),Square.valueOf(FILE_F, RANK_4))));
        assertTrue(moves.contains(new Move(WHITE_QUEEN,Square.valueOf(FILE_G, RANK_3),Square.valueOf(FILE_E, RANK_5))));
    }


    @Test
    public void testKingMoves() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "8/8/3k4/2n1P3/8/8/3rP3/R3K2R b KQ - 0 1");
        List<Move> moves = new ArrayList<>();
        MoveGen.genKingMoves(b, moves, true, true);
        assertEquals(7, moves.size());
        assertTrue(moves.contains(new Move(BLACK_KING,Square.valueOf(FILE_D, RANK_6),Square.valueOf(FILE_D, RANK_7))));
        assertTrue(moves.contains(new Move(BLACK_KING,Square.valueOf(FILE_D, RANK_6),Square.valueOf(FILE_E, RANK_7))));
        assertTrue(moves.contains(new Move(BLACK_KING,Square.valueOf(FILE_D, RANK_6),Square.valueOf(FILE_E, RANK_6))));
        assertTrue(moves.contains(new Move(BLACK_KING,Square.valueOf(FILE_D, RANK_6),Square.valueOf(FILE_E, RANK_5),WHITE_PAWN)));
        assertTrue(moves.contains(new Move(BLACK_KING,Square.valueOf(FILE_D, RANK_6),Square.valueOf(FILE_D, RANK_5))));
        assertTrue(moves.contains(new Move(BLACK_KING,Square.valueOf(FILE_D, RANK_6),Square.valueOf(FILE_C, RANK_6))));
        assertTrue(moves.contains(new Move(BLACK_KING,Square.valueOf(FILE_D, RANK_6),Square.valueOf(FILE_C, RANK_7))));

        // flip sides
        FenParser.setPos(b, "8/8/3k4/2n1P3/8/8/3rP3/RN2K2R w KQ - 0 1");
        moves.clear();
        MoveGen.genKingMoves(b, moves, true, true);
        assertEquals(5, moves.size());
        assertTrue(moves.contains(new Move(WHITE_KING,Square.valueOf(FILE_E, RANK_1),Square.valueOf(FILE_F, RANK_2))));
        assertTrue(moves.contains(new Move(WHITE_KING,Square.valueOf(FILE_E, RANK_1),Square.valueOf(FILE_F, RANK_1))));
        assertTrue(moves.contains(new Move(WHITE_KING,Square.valueOf(FILE_E, RANK_1),Square.valueOf(FILE_D, RANK_1))));
        assertTrue(moves.contains(new Move(WHITE_KING,Square.valueOf(FILE_E, RANK_1),Square.valueOf(FILE_D, RANK_2),BLACK_ROOK)));
        assertTrue(moves.contains(new Move(WHITE_KING,Square.valueOf(FILE_E, RANK_1),Square.valueOf(FILE_G, RANK_1),true)));

    }

    @Test
    public void testKingMoves2() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "3k4/8/8/2n1P3/8/8/3rP3/RN2K2R w KQ - 0 1");
        List<Move> moves = new ArrayList<>();
        MoveGen.genKingMoves(b, moves, true, true);

        assertEquals(5, moves.size());
        assertTrue(moves.contains(new Move(WHITE_KING,Square.valueOf(FILE_E, RANK_1),Square.valueOf(FILE_F, RANK_2))));
        assertTrue(moves.contains(new Move(WHITE_KING,Square.valueOf(FILE_E, RANK_1),Square.valueOf(FILE_F, RANK_1))));
        // Kd1 illegal but that's handled elsewhere
        assertTrue(moves.contains(new Move(WHITE_KING,Square.valueOf(FILE_E, RANK_1),Square.valueOf(FILE_D, RANK_1))));
        assertTrue(moves.contains(new Move(WHITE_KING,Square.valueOf(FILE_E, RANK_1),Square.valueOf(FILE_D, RANK_2),BLACK_ROOK)));
        assertTrue(moves.contains(new Move(WHITE_KING,Square.valueOf(FILE_E, RANK_1),Square.valueOf(FILE_G, RANK_1),true)));
    }

    @Test
    public void testKingMovesInCorner() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "k7/8/8/8/8/8/8/7K w - - 0 1");
        List<Move> moves = new ArrayList<>();
        MoveGen.genKingMoves(b, moves, true, true);

        assertEquals(3, moves.size());
        assertTrue(moves.contains(new Move(WHITE_KING,Square.valueOf(FILE_H, RANK_1),Square.valueOf(FILE_G, RANK_1))));
        assertTrue(moves.contains(new Move(WHITE_KING,Square.valueOf(FILE_H, RANK_1),Square.valueOf(FILE_G, RANK_2))));
        assertTrue(moves.contains(new Move(WHITE_KING,Square.valueOf(FILE_H, RANK_1),Square.valueOf(FILE_H, RANK_2))));
    }

    @Test
    public void testKingMovesInCorner2() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "k7/8/8/8/8/8/8/7K b - - 0 1");
        List<Move> moves = new ArrayList<>();
        MoveGen.genKingMoves(b, moves, true, true);

        assertEquals(3, moves.size());
        assertTrue(moves.contains(new Move(BLACK_KING,Square.valueOf(FILE_A, RANK_8),Square.valueOf(FILE_A, RANK_7))));
        assertTrue(moves.contains(new Move(BLACK_KING,Square.valueOf(FILE_A, RANK_8),Square.valueOf(FILE_B, RANK_7))));
        assertTrue(moves.contains(new Move(BLACK_KING,Square.valueOf(FILE_A, RANK_8),Square.valueOf(FILE_B, RANK_8))));
    }

    @Test
    public void testKingNoCastleToEscapeCheck() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "3k4/8/8/2N1P3/7q/8/4P3/R3K2R w KQ - 0 1");
        List<Move> moves = new ArrayList<>();
        MoveGen.genKingMoves(b, moves, true, true);

        assertEquals(4, moves.size());
        assertTrue(moves.contains(new Move(WHITE_KING,Square.valueOf(FILE_E, RANK_1),Square.valueOf(FILE_F, RANK_1))));
        // Kf2 stays in check, but that's handled separately
        assertTrue(moves.contains(new Move(WHITE_KING,Square.valueOf(FILE_E, RANK_1),Square.valueOf(FILE_F, RANK_2))));
        assertTrue(moves.contains(new Move(WHITE_KING,Square.valueOf(FILE_E, RANK_1),Square.valueOf(FILE_D, RANK_1))));
        assertTrue(moves.contains(new Move(WHITE_KING,Square.valueOf(FILE_E, RANK_1),Square.valueOf(FILE_D, RANK_2))));
    }

    @Test
    public void testKingCannotCastleThroughCheck() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "r3k2r/8/8/8/8/5Q2/8/4K3 b kq - 0 1");
        List<Move> moves = new ArrayList<>();
        MoveGen.genKingMoves(b, moves, true, true);

        assertEquals(6, moves.size());
        assertTrue(moves.contains(new Move(BLACK_KING,Square.valueOf(FILE_E, RANK_8),Square.valueOf(FILE_F, RANK_8))));
        assertTrue(moves.contains(new Move(BLACK_KING,Square.valueOf(FILE_E, RANK_8),Square.valueOf(FILE_F, RANK_7))));
        assertTrue(moves.contains(new Move(BLACK_KING,Square.valueOf(FILE_E, RANK_8),Square.valueOf(FILE_D, RANK_8))));
        assertTrue(moves.contains(new Move(BLACK_KING,Square.valueOf(FILE_E, RANK_8),Square.valueOf(FILE_D, RANK_7))));
        assertTrue(moves.contains(new Move(BLACK_KING,Square.valueOf(FILE_E, RANK_8),Square.valueOf(FILE_E, RANK_7))));
        assertTrue(moves.contains(new Move(BLACK_KING,Square.valueOf(FILE_E, RANK_8),Square.valueOf(FILE_C, RANK_8),true)));
    }

    @Test
    public void testKingCaptures() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "8/8/3k4/2n1P3/8/8/3rP3/R3K2R b KQ - 0 1");
        List<Move> moves = new ArrayList<>();
        MoveGen.genKingMoves(b, moves, true, false);

        assertEquals(1, moves.size());
        assertTrue(moves.contains(new Move(BLACK_KING,Square.valueOf(FILE_D, RANK_6),Square.valueOf(FILE_E, RANK_5),WHITE_PAWN)));
    }

    @Test
    public void testKingNoncaptures() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "8/8/3k4/2n1P3/8/8/3rP3/R3K2R b KQ - 0 1");
        List<Move> moves = new ArrayList<>();
        MoveGen.genKingMoves(b, moves, false,true);
        assertEquals(6, moves.size());

        assertTrue(moves.contains(new Move(BLACK_KING,Square.valueOf(FILE_D, RANK_6),Square.valueOf(FILE_D, RANK_7))));
        assertTrue(moves.contains(new Move(BLACK_KING,Square.valueOf(FILE_D, RANK_6),Square.valueOf(FILE_E, RANK_7))));
        assertTrue(moves.contains(new Move(BLACK_KING,Square.valueOf(FILE_D, RANK_6),Square.valueOf(FILE_E, RANK_6))));
        assertTrue(moves.contains(new Move(BLACK_KING,Square.valueOf(FILE_D, RANK_6),Square.valueOf(FILE_D, RANK_5))));
        assertTrue(moves.contains(new Move(BLACK_KING,Square.valueOf(FILE_D, RANK_6),Square.valueOf(FILE_C, RANK_6))));
        assertTrue(moves.contains(new Move(BLACK_KING,Square.valueOf(FILE_D, RANK_6),Square.valueOf(FILE_C, RANK_7))));
    }

    @Test
    public void testPawnMoves() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "2b1k3/PP6/8/3pP3/4P3/8/6P1/4K3 w - d6 0 1");
        List<Move> moves = new ArrayList<>();
        MoveGen.genPawnMoves(b, moves, true, true);

        assertEquals(17, moves.size());
        assertTrue(moves.contains(new Move(WHITE_PAWN,Square.valueOf(FILE_A, RANK_7),Square.valueOf(FILE_A, RANK_8),null,WHITE_QUEEN)));
        assertTrue(moves.contains(new Move(WHITE_PAWN,Square.valueOf(FILE_A, RANK_7),Square.valueOf(FILE_A, RANK_8),null,WHITE_ROOK)));
        assertTrue(moves.contains(new Move(WHITE_PAWN,Square.valueOf(FILE_A, RANK_7),Square.valueOf(FILE_A, RANK_8),null,WHITE_BISHOP)));
        assertTrue(moves.contains(new Move(WHITE_PAWN,Square.valueOf(FILE_A, RANK_7),Square.valueOf(FILE_A, RANK_8),null,WHITE_KNIGHT)));
        assertTrue(moves.contains(new Move(WHITE_PAWN,Square.valueOf(FILE_B, RANK_7),Square.valueOf(FILE_B, RANK_8),null,WHITE_QUEEN)));
        assertTrue(moves.contains(new Move(WHITE_PAWN,Square.valueOf(FILE_B, RANK_7),Square.valueOf(FILE_B, RANK_8),null,WHITE_ROOK)));
        assertTrue(moves.contains(new Move(WHITE_PAWN,Square.valueOf(FILE_B, RANK_7),Square.valueOf(FILE_B, RANK_8),null,WHITE_BISHOP)));
        assertTrue(moves.contains(new Move(WHITE_PAWN,Square.valueOf(FILE_B, RANK_7),Square.valueOf(FILE_B, RANK_8),null,WHITE_KNIGHT)));
        // Capture + Promotion
        assertTrue(moves.contains(new Move(WHITE_PAWN,Square.valueOf(FILE_B, RANK_7),Square.valueOf(FILE_C, RANK_8),BLACK_BISHOP,WHITE_QUEEN)));
        assertTrue(moves.contains(new Move(WHITE_PAWN,Square.valueOf(FILE_B, RANK_7),Square.valueOf(FILE_C, RANK_8),BLACK_BISHOP,WHITE_ROOK)));
        assertTrue(moves.contains(new Move(WHITE_PAWN,Square.valueOf(FILE_B, RANK_7),Square.valueOf(FILE_C, RANK_8),BLACK_BISHOP,WHITE_BISHOP)));
        assertTrue(moves.contains(new Move(WHITE_PAWN,Square.valueOf(FILE_B, RANK_7),Square.valueOf(FILE_C, RANK_8),BLACK_BISHOP,WHITE_KNIGHT)));

        assertTrue(moves.contains(new Move(WHITE_PAWN,Square.valueOf(FILE_E, RANK_5),Square.valueOf(FILE_E, RANK_6))));
        // EP
        assertTrue(moves.contains(new Move(WHITE_PAWN,Square.valueOf(FILE_E, RANK_5),Square.valueOf(FILE_D, RANK_6),BLACK_PAWN,true)));
        assertTrue(moves.contains(new Move(WHITE_PAWN,Square.valueOf(FILE_E, RANK_4),Square.valueOf(FILE_D, RANK_5),BLACK_PAWN)));

        assertTrue(moves.contains(new Move(WHITE_PAWN,Square.valueOf(FILE_G, RANK_2),Square.valueOf(FILE_G, RANK_3))));
        assertTrue(moves.contains(new Move(WHITE_PAWN,Square.valueOf(FILE_G, RANK_2),Square.valueOf(FILE_G, RANK_4))));
    }

    @Test
    public void testPawnCaptures() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "2b1k3/PP6/8/3pP3/4P3/8/6P1/4K3 w - d6 0 1");
        List<Move> moves = new ArrayList<>();
        MoveGen.genPawnMoves(b, moves, true, false);

        assertEquals(14, moves.size());
        assertTrue(moves.contains(new Move(WHITE_PAWN,Square.valueOf(FILE_A, RANK_7),Square.valueOf(FILE_A, RANK_8),null,WHITE_QUEEN)));
        assertTrue(moves.contains(new Move(WHITE_PAWN,Square.valueOf(FILE_A, RANK_7),Square.valueOf(FILE_A, RANK_8),null,WHITE_ROOK)));
        assertTrue(moves.contains(new Move(WHITE_PAWN,Square.valueOf(FILE_A, RANK_7),Square.valueOf(FILE_A, RANK_8),null,WHITE_BISHOP)));
        assertTrue(moves.contains(new Move(WHITE_PAWN,Square.valueOf(FILE_A, RANK_7),Square.valueOf(FILE_A, RANK_8),null,WHITE_KNIGHT)));
        assertTrue(moves.contains(new Move(WHITE_PAWN,Square.valueOf(FILE_B, RANK_7),Square.valueOf(FILE_B, RANK_8),null,WHITE_QUEEN)));
        assertTrue(moves.contains(new Move(WHITE_PAWN,Square.valueOf(FILE_B, RANK_7),Square.valueOf(FILE_B, RANK_8),null,WHITE_ROOK)));
        assertTrue(moves.contains(new Move(WHITE_PAWN,Square.valueOf(FILE_B, RANK_7),Square.valueOf(FILE_B, RANK_8),null,WHITE_BISHOP)));
        assertTrue(moves.contains(new Move(WHITE_PAWN,Square.valueOf(FILE_B, RANK_7),Square.valueOf(FILE_B, RANK_8),null,WHITE_KNIGHT)));
        // Capture + Promotion
        assertTrue(moves.contains(new Move(WHITE_PAWN,Square.valueOf(FILE_B, RANK_7),Square.valueOf(FILE_C, RANK_8),BLACK_BISHOP,WHITE_QUEEN)));
        assertTrue(moves.contains(new Move(WHITE_PAWN,Square.valueOf(FILE_B, RANK_7),Square.valueOf(FILE_C, RANK_8),BLACK_BISHOP,WHITE_ROOK)));
        assertTrue(moves.contains(new Move(WHITE_PAWN,Square.valueOf(FILE_B, RANK_7),Square.valueOf(FILE_C, RANK_8),BLACK_BISHOP,WHITE_BISHOP)));
        assertTrue(moves.contains(new Move(WHITE_PAWN,Square.valueOf(FILE_B, RANK_7),Square.valueOf(FILE_C, RANK_8),BLACK_BISHOP,WHITE_KNIGHT)));

        // EP
        assertTrue(moves.contains(new Move(WHITE_PAWN,Square.valueOf(FILE_E, RANK_5),Square.valueOf(FILE_D, RANK_6),BLACK_PAWN,true)));
        assertTrue(moves.contains(new Move(WHITE_PAWN,Square.valueOf(FILE_E, RANK_4),Square.valueOf(FILE_D, RANK_5),BLACK_PAWN)));
    }

    @Test
    public void testPawnNoncaps() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "2b1k3/PP6/8/3pP3/4P3/8/6P1/4K3 w - d6 0 1");
        List<Move> moves = new ArrayList<>();
        MoveGen.genPawnMoves(b, moves, false, true);

        assertEquals(3, moves.size());
        assertTrue(moves.contains(new Move(WHITE_PAWN,Square.valueOf(FILE_E, RANK_5),Square.valueOf(FILE_E, RANK_6))));
        assertTrue(moves.contains(new Move(WHITE_PAWN,Square.valueOf(FILE_G, RANK_2),Square.valueOf(FILE_G, RANK_3))));
        assertTrue(moves.contains(new Move(WHITE_PAWN,Square.valueOf(FILE_G, RANK_2),Square.valueOf(FILE_G, RANK_4))));
    }

    @Test
    public void testPawnMoves2() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1");
        List<Move> moves = new ArrayList<>();
        MoveGen.genPawnMoves(b, moves, true, true);

        assertEquals(16, moves.size());
        assertTrue(moves.contains(new Move(BLACK_PAWN,Square.valueOf(FILE_D, RANK_7),Square.valueOf(FILE_D, RANK_6))));
        assertTrue(moves.contains(new Move(BLACK_PAWN,Square.valueOf(FILE_D, RANK_7),Square.valueOf(FILE_D, RANK_5))));
    }

    @Test
    public void testMovesFromInitialPos() {
        Board b = Board.INSTANCE;
        b.resetBoard();

        List<Move> moves = MoveGen.genLegalMoves(b);
        assertEquals(20, moves.size());

        assertTrue(moves.contains(new Move(WHITE_PAWN,Square.valueOf(FILE_A, RANK_2),Square.valueOf(FILE_A, RANK_3))));
        assertTrue(moves.contains(new Move(WHITE_PAWN,Square.valueOf(FILE_A, RANK_2),Square.valueOf(FILE_A, RANK_4))));
        assertTrue(moves.contains(new Move(WHITE_PAWN,Square.valueOf(FILE_B, RANK_2),Square.valueOf(FILE_B, RANK_3))));
        assertTrue(moves.contains(new Move(WHITE_PAWN,Square.valueOf(FILE_B, RANK_2),Square.valueOf(FILE_B, RANK_4))));
        assertTrue(moves.contains(new Move(WHITE_PAWN,Square.valueOf(FILE_C, RANK_2),Square.valueOf(FILE_C, RANK_3))));
        assertTrue(moves.contains(new Move(WHITE_PAWN,Square.valueOf(FILE_C, RANK_2),Square.valueOf(FILE_C, RANK_4))));
        assertTrue(moves.contains(new Move(WHITE_PAWN,Square.valueOf(FILE_D, RANK_2),Square.valueOf(FILE_D, RANK_3))));
        assertTrue(moves.contains(new Move(WHITE_PAWN,Square.valueOf(FILE_D, RANK_2),Square.valueOf(FILE_D, RANK_4))));
        assertTrue(moves.contains(new Move(WHITE_PAWN,Square.valueOf(FILE_E, RANK_2),Square.valueOf(FILE_E, RANK_3))));
        assertTrue(moves.contains(new Move(WHITE_PAWN,Square.valueOf(FILE_E, RANK_2),Square.valueOf(FILE_E, RANK_4))));
        assertTrue(moves.contains(new Move(WHITE_PAWN,Square.valueOf(FILE_F, RANK_2),Square.valueOf(FILE_F, RANK_3))));
        assertTrue(moves.contains(new Move(WHITE_PAWN,Square.valueOf(FILE_F, RANK_2),Square.valueOf(FILE_F, RANK_4))));
        assertTrue(moves.contains(new Move(WHITE_PAWN,Square.valueOf(FILE_G, RANK_2),Square.valueOf(FILE_G, RANK_3))));
        assertTrue(moves.contains(new Move(WHITE_PAWN,Square.valueOf(FILE_G, RANK_2),Square.valueOf(FILE_G, RANK_4))));
        assertTrue(moves.contains(new Move(WHITE_PAWN,Square.valueOf(FILE_H, RANK_2),Square.valueOf(FILE_H, RANK_3))));
        assertTrue(moves.contains(new Move(WHITE_PAWN,Square.valueOf(FILE_H, RANK_2),Square.valueOf(FILE_H, RANK_4))));

        assertTrue(moves.contains(new Move(WHITE_KNIGHT,Square.valueOf(FILE_B, RANK_1),Square.valueOf(FILE_A, RANK_3))));
        assertTrue(moves.contains(new Move(WHITE_KNIGHT,Square.valueOf(FILE_B, RANK_1),Square.valueOf(FILE_C, RANK_3))));
        assertTrue(moves.contains(new Move(WHITE_KNIGHT,Square.valueOf(FILE_G, RANK_1),Square.valueOf(FILE_F, RANK_3))));
        assertTrue(moves.contains(new Move(WHITE_KNIGHT,Square.valueOf(FILE_G, RANK_1),Square.valueOf(FILE_H, RANK_3))));
    }

    @Test
    public void tsetCapturesPromosOnlyFromInitialPosition() {
        Board b = Board.INSTANCE;
        b.resetBoard();
        List<Move> moves = MoveGen.genPseudoLegalMoves(b, true,false);
        assertTrue(moves.isEmpty());
    }

    @Test
    public void testCapturesPromosOnlyContainsPromotions() throws Exception {
        Board b = Board.INSTANCE;
        EPDParser.setPos(b, "8/4Pk1p/6p1/1r6/8/5N2/2B2PPP/b5K1 w - - bm e8=Q+; id \"position 0631\";");

        List<Move> moves = MoveGen.genPseudoLegalMoves(b, true,false);

        MoveParser mp = new MoveParser();
        Move e7e8q = mp.parseMove("e7e8=q", b);
        Move e7e8r = mp.parseMove("e7e8=r", b);
        Move e7e8b = mp.parseMove("e7e8=b", b);
        Move e7e8n = mp.parseMove("e7e8=n", b);
        Move c2g6 = mp.parseMove("c2g6", b);
        assertTrue(moves.contains(e7e8q));
        assertTrue(moves.contains(e7e8r));
        assertTrue(moves.contains(e7e8b));
        assertTrue(moves.contains(e7e8n));
        assertTrue(moves.contains(c2g6));
    }

    @Test
    public void testCapturesPromosOnlyContainsEP() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "8/8/8/3pP3/8/8/K6k/8 w - d6");

        List<Move> moves = MoveGen.genPseudoLegalMoves(b, true,false);
        assertEquals(1, moves.size());
        MoveParser mp = new MoveParser();
        assertTrue(moves.contains(mp.parseMove("e5d6", b)));
    }
}
