package com.jamesswafford.chess4j.movegen;

import java.util.ArrayList;
import java.util.List;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Move;

import org.junit.Test;

import com.jamesswafford.chess4j.io.EPDParser;
import com.jamesswafford.chess4j.io.MoveParser;

import static org.junit.Assert.*;

import static com.jamesswafford.chess4j.pieces.Pawn.*;
import static com.jamesswafford.chess4j.pieces.Knight.*;
import static com.jamesswafford.chess4j.pieces.Bishop.*;
import static com.jamesswafford.chess4j.pieces.Rook.*;
import static com.jamesswafford.chess4j.pieces.Queen.*;
import static com.jamesswafford.chess4j.pieces.King.*;
import static com.jamesswafford.chess4j.board.squares.Square.*;


public class MoveGenTest {

    @Test
    public void testKnightMoves() {
        Board b = Board.INSTANCE;
        b.resetBoard();

        List<Move> moves = new ArrayList<>();
        MoveGen.genKnightMoves(b,moves,true,true);

        assertEquals(4, moves.size());
        assertTrue(moves.contains(new Move(WHITE_KNIGHT, B1, A3)));
        assertTrue(moves.contains(new Move(WHITE_KNIGHT, B1, C3)));
        assertTrue(moves.contains(new Move(WHITE_KNIGHT, G1, F3)));
        assertTrue(moves.contains(new Move(WHITE_KNIGHT, G1, H3)));
    }

    @Test
    public void testKnightCaptures() {
        Board b = Board.INSTANCE;
        b.setPos("4k3/8/3P1p2/8/4N3/8/8/4K3 w - - 0 1");

        List<Move> moves = new ArrayList<>();
        MoveGen.genKnightMoves(b,moves,true,false);

        assertEquals(1, moves.size());
        assertTrue(moves.contains(new Move(WHITE_KNIGHT, E4, F6 ,BLACK_PAWN)));
    }

    @Test
    public void testKnightNoncaptures() {
        Board b = Board.INSTANCE;
        b.setPos("4k3/8/3P1p2/8/4N3/8/8/4K3 w - - 0 1");

        List<Move> moves = new ArrayList<>();
        MoveGen.genKnightMoves(b,moves,false,true);

        assertEquals(6, moves.size());
        assertTrue(moves.contains(new Move(WHITE_KNIGHT, E4, G5)));
        assertTrue(moves.contains(new Move(WHITE_KNIGHT, E4, G3)));
        assertTrue(moves.contains(new Move(WHITE_KNIGHT, E4, F2)));
        assertTrue(moves.contains(new Move(WHITE_KNIGHT, E4, D2)));
        assertTrue(moves.contains(new Move(WHITE_KNIGHT, E4, C3)));
        assertTrue(moves.contains(new Move(WHITE_KNIGHT, E4, C5)));
    }

    @Test
    public void testBishopMoves() {
        Board b = Board.INSTANCE;
        b.setPos("4k3/2p3P1/8/4B3/4B3/3p1P2/8/4K3 w - - 0 1");

        List<Move> moves = new ArrayList<>();
        MoveGen.genBishopMoves(b, moves, true, true);
        assertEquals(18, moves.size());
        assertTrue(moves.contains(new Move(WHITE_BISHOP, E5, F6)));
        assertTrue(moves.contains(new Move(WHITE_BISHOP, E5, F4)));
        assertTrue(moves.contains(new Move(WHITE_BISHOP, E5, G3)));
        assertTrue(moves.contains(new Move(WHITE_BISHOP, E5, H2)));
        assertTrue(moves.contains(new Move(WHITE_BISHOP, E5, D4)));
        assertTrue(moves.contains(new Move(WHITE_BISHOP, E5, C3)));
        assertTrue(moves.contains(new Move(WHITE_BISHOP, E5, B2)));
        assertTrue(moves.contains(new Move(WHITE_BISHOP, E5, A1)));
        assertTrue(moves.contains(new Move(WHITE_BISHOP, E5, D6)));
        assertTrue(moves.contains(new Move(WHITE_BISHOP, E5, C7, BLACK_PAWN)));
        assertTrue(moves.contains(new Move(WHITE_BISHOP, E4, F5)));
        assertTrue(moves.contains(new Move(WHITE_BISHOP, E4, G6)));
        assertTrue(moves.contains(new Move(WHITE_BISHOP, E4, H7)));
        assertTrue(moves.contains(new Move(WHITE_BISHOP, E4, D3, BLACK_PAWN)));
        assertTrue(moves.contains(new Move(WHITE_BISHOP, E4, D5)));
        assertTrue(moves.contains(new Move(WHITE_BISHOP, E4, C6)));
        assertTrue(moves.contains(new Move(WHITE_BISHOP, E4, B7)));
        assertTrue(moves.contains(new Move(WHITE_BISHOP, E4, A8)));
    }

    @Test
    public void testBishopCaptures() {
        Board b = Board.INSTANCE;
        b.setPos("4k3/2p3P1/8/4B3/4B3/3p1P2/8/4K3 w - - 0 1");

        List<Move> moves = new ArrayList<>();
        MoveGen.genBishopMoves(b, moves, true, false);
        assertEquals(2, moves.size());
        assertTrue(moves.contains(new Move(WHITE_BISHOP, E5, C7,BLACK_PAWN)));
        assertTrue(moves.contains(new Move(WHITE_BISHOP, E4, D3, BLACK_PAWN)));
    }

    @Test
    public void testBishopNoncaptures() {
        Board b = Board.INSTANCE;
        b.setPos("4k3/2p3P1/8/4B3/4B3/3p1P2/8/4K3 w - - 0 1");

        List<Move> moves = new ArrayList<>();
        MoveGen.genBishopMoves(b, moves, false, true);
        assertEquals(16, moves.size());
        assertTrue(moves.contains(new Move(WHITE_BISHOP, E5, F6)));
        assertTrue(moves.contains(new Move(WHITE_BISHOP, E5, F4)));
        assertTrue(moves.contains(new Move(WHITE_BISHOP, E5, G3)));
        assertTrue(moves.contains(new Move(WHITE_BISHOP, E5, H2)));
        assertTrue(moves.contains(new Move(WHITE_BISHOP, E5, D4)));
        assertTrue(moves.contains(new Move(WHITE_BISHOP, E5, C3)));
        assertTrue(moves.contains(new Move(WHITE_BISHOP, E5, B2)));
        assertTrue(moves.contains(new Move(WHITE_BISHOP, E5, A1)));
        assertTrue(moves.contains(new Move(WHITE_BISHOP, E5, D6)));
        assertTrue(moves.contains(new Move(WHITE_BISHOP, E4, F5)));
        assertTrue(moves.contains(new Move(WHITE_BISHOP, E4, G6)));
        assertTrue(moves.contains(new Move(WHITE_BISHOP, E4, H7)));
        assertTrue(moves.contains(new Move(WHITE_BISHOP, E4, D5)));
        assertTrue(moves.contains(new Move(WHITE_BISHOP, E4, C6)));
        assertTrue(moves.contains(new Move(WHITE_BISHOP, E4, B7)));
        assertTrue(moves.contains(new Move(WHITE_BISHOP, E4, A8)));
    }

    @Test
    public void testRookMoves() {
        Board b = Board.INSTANCE;
        b.resetBoard();

        List<Move> moves = new ArrayList<>();
        MoveGen.genRookMoves(b, moves, true, true);
        assertEquals(0, moves.size());

        b.setPos("8/8/3k1p2/8/3K4/8/1R3r2/8 b - - 0 1");
        MoveGen.genRookMoves(b, moves, true, true);

        assertEquals(10, moves.size());
        assertTrue(moves.contains(new Move(BLACK_ROOK, F2, F3)));
        assertTrue(moves.contains(new Move(BLACK_ROOK, F2, F4)));
        assertTrue(moves.contains(new Move(BLACK_ROOK, F2, F5)));
        assertTrue(moves.contains(new Move(BLACK_ROOK, F2, F1)));
        assertTrue(moves.contains(new Move(BLACK_ROOK, F2, G2)));
        assertTrue(moves.contains(new Move(BLACK_ROOK, F2, H2)));
        assertTrue(moves.contains(new Move(BLACK_ROOK, F2, E2)));
        assertTrue(moves.contains(new Move(BLACK_ROOK, F2, D2)));
        assertTrue(moves.contains(new Move(BLACK_ROOK, F2, C2)));
        assertTrue(moves.contains(new Move(BLACK_ROOK, F2, B2,WHITE_ROOK)));
    }

    @Test
    public void testRookCaptures() {
        Board b = Board.INSTANCE;

        List<Move> moves = new ArrayList<>();
        b.setPos("8/8/3k1p2/8/3K4/8/1R3r2/8 b - - 0 1");
        MoveGen.genRookMoves(b, moves, true, false);

        assertEquals(1, moves.size());
        assertTrue(moves.contains(new Move(BLACK_ROOK, F2, B2, WHITE_ROOK)));
    }

    @Test
    public void testRookNoncaptures() {
        Board b = Board.INSTANCE;

        List<Move> moves = new ArrayList<>();
        b.setPos("8/8/3k1p2/8/3K4/8/1R3r2/8 b - - 0 1");
        MoveGen.genRookMoves(b, moves, false, true);

        assertEquals(9, moves.size());
        assertTrue(moves.contains(new Move(BLACK_ROOK, F2, F3)));
        assertTrue(moves.contains(new Move(BLACK_ROOK, F2, F4)));
        assertTrue(moves.contains(new Move(BLACK_ROOK, F2, F5)));
        assertTrue(moves.contains(new Move(BLACK_ROOK, F2, F1)));
        assertTrue(moves.contains(new Move(BLACK_ROOK, F2, G2)));
        assertTrue(moves.contains(new Move(BLACK_ROOK, F2, H2)));
        assertTrue(moves.contains(new Move(BLACK_ROOK, F2, E2)));
        assertTrue(moves.contains(new Move(BLACK_ROOK, F2, D2)));
        assertTrue(moves.contains(new Move(BLACK_ROOK, F2, C2)));
    }

    @Test
    public void testQueenMoves() {
        Board b = Board.INSTANCE;
        b.setPos("8/8/3bk3/8/8/2K3Q1/8/8 w - - 0 1");

        List<Move> moves = new ArrayList<>();
        MoveGen.genQueenMoves(b, moves, true, true);
        assertEquals(18, moves.size());
        assertTrue(moves.contains(new Move(WHITE_QUEEN, G3, G4)));
        assertTrue(moves.contains(new Move(WHITE_QUEEN, G3, G5)));
        assertTrue(moves.contains(new Move(WHITE_QUEEN, G3, G6)));
        assertTrue(moves.contains(new Move(WHITE_QUEEN, G3, G7)));
        assertTrue(moves.contains(new Move(WHITE_QUEEN, G3, G8)));
        assertTrue(moves.contains(new Move(WHITE_QUEEN, G3, H4)));
        assertTrue(moves.contains(new Move(WHITE_QUEEN, G3, H3)));
        assertTrue(moves.contains(new Move(WHITE_QUEEN, G3, H2)));
        assertTrue(moves.contains(new Move(WHITE_QUEEN, G3, G2)));
        assertTrue(moves.contains(new Move(WHITE_QUEEN, G3, G1)));
        assertTrue(moves.contains(new Move(WHITE_QUEEN, G3, F2)));
        assertTrue(moves.contains(new Move(WHITE_QUEEN, G3, E1)));
        assertTrue(moves.contains(new Move(WHITE_QUEEN, G3, F3)));
        assertTrue(moves.contains(new Move(WHITE_QUEEN, G3, E3)));
        assertTrue(moves.contains(new Move(WHITE_QUEEN, G3, D3)));
        assertTrue(moves.contains(new Move(WHITE_QUEEN, G3, F4)));
        assertTrue(moves.contains(new Move(WHITE_QUEEN, G3, E5)));
        assertTrue(moves.contains(new Move(WHITE_QUEEN, G3, D6, BLACK_BISHOP)));
    }

    @Test
    public void testQueenCaptures() {
        Board b = Board.INSTANCE;
        b.setPos("8/8/3bk3/8/8/2K3Q1/8/8 w - - 0 1");

        List<Move> moves = new ArrayList<>();
        MoveGen.genQueenMoves(b, moves, true, false);
        assertEquals(1, moves.size());
        assertTrue(moves.contains(new Move(WHITE_QUEEN, G3, D6 ,BLACK_BISHOP)));
    }

    @Test
    public void testQueenNoncaptures() {
        Board b = Board.INSTANCE;
        b.setPos("8/8/3bk3/8/8/2K3Q1/8/8 w - - 0 1");

        List<Move> moves = new ArrayList<>();
        MoveGen.genQueenMoves(b, moves, false, true);
        assertEquals(17, moves.size());
        assertTrue(moves.contains(new Move(WHITE_QUEEN, G3, G4)));
        assertTrue(moves.contains(new Move(WHITE_QUEEN, G3, G5)));
        assertTrue(moves.contains(new Move(WHITE_QUEEN, G3, G6)));
        assertTrue(moves.contains(new Move(WHITE_QUEEN, G3, G7)));
        assertTrue(moves.contains(new Move(WHITE_QUEEN, G3, G8)));
        assertTrue(moves.contains(new Move(WHITE_QUEEN, G3, H4)));
        assertTrue(moves.contains(new Move(WHITE_QUEEN, G3, H3)));
        assertTrue(moves.contains(new Move(WHITE_QUEEN, G3, H2)));
        assertTrue(moves.contains(new Move(WHITE_QUEEN, G3, G2)));
        assertTrue(moves.contains(new Move(WHITE_QUEEN, G3, G1)));
        assertTrue(moves.contains(new Move(WHITE_QUEEN, G3, F2)));
        assertTrue(moves.contains(new Move(WHITE_QUEEN, G3, E1)));
        assertTrue(moves.contains(new Move(WHITE_QUEEN, G3, F3)));
        assertTrue(moves.contains(new Move(WHITE_QUEEN, G3, E3)));
        assertTrue(moves.contains(new Move(WHITE_QUEEN, G3, D3)));
        assertTrue(moves.contains(new Move(WHITE_QUEEN, G3, F4)));
        assertTrue(moves.contains(new Move(WHITE_QUEEN, G3, E5)));
    }


    @Test
    public void testKingMoves() {
        Board b = Board.INSTANCE;
        b.setPos("8/8/3k4/2n1P3/8/8/3rP3/R3K2R b KQ - 0 1");

        List<Move> moves = new ArrayList<>();
        MoveGen.genKingMoves(b, moves, true, true);
        assertEquals(7, moves.size());
        assertTrue(moves.contains(new Move(BLACK_KING, D6, D7)));
        assertTrue(moves.contains(new Move(BLACK_KING, D6, E7)));
        assertTrue(moves.contains(new Move(BLACK_KING, D6, E6)));
        assertTrue(moves.contains(new Move(BLACK_KING, D6, E5, WHITE_PAWN)));
        assertTrue(moves.contains(new Move(BLACK_KING, D6, D5)));
        assertTrue(moves.contains(new Move(BLACK_KING, D6, C6)));
        assertTrue(moves.contains(new Move(BLACK_KING, D6, C7)));

        // flip sides
        b.setPos("8/8/3k4/2n1P3/8/8/3rP3/RN2K2R w KQ - 0 1");
        moves.clear();
        MoveGen.genKingMoves(b, moves, true, true);
        assertEquals(5, moves.size());
        assertTrue(moves.contains(new Move(WHITE_KING, E1, F2)));
        assertTrue(moves.contains(new Move(WHITE_KING, E1, F1)));
        assertTrue(moves.contains(new Move(WHITE_KING, E1, D1)));
        assertTrue(moves.contains(new Move(WHITE_KING, E1, D2, BLACK_ROOK)));
        assertTrue(moves.contains(new Move(WHITE_KING, E1, G1,true)));
    }

    @Test
    public void testKingMoves2() {
        Board b = Board.INSTANCE;
        b.setPos("3k4/8/8/2n1P3/8/8/3rP3/RN2K2R w KQ - 0 1");

        List<Move> moves = new ArrayList<>();
        MoveGen.genKingMoves(b, moves, true, true);

        assertEquals(5, moves.size());
        assertTrue(moves.contains(new Move(WHITE_KING, E1, F2)));
        assertTrue(moves.contains(new Move(WHITE_KING, E1, F1)));
        // Kd1 illegal but that's handled elsewhere
        assertTrue(moves.contains(new Move(WHITE_KING, E1, D1)));
        assertTrue(moves.contains(new Move(WHITE_KING, E1, D2, BLACK_ROOK)));
        assertTrue(moves.contains(new Move(WHITE_KING, E1, G1,true)));
    }

    @Test
    public void testKingMovesInCorner() {
        Board b = Board.INSTANCE;
        b.setPos("k7/8/8/8/8/8/8/7K w - - 0 1");

        List<Move> moves = new ArrayList<>();
        MoveGen.genKingMoves(b, moves, true, true);

        assertEquals(3, moves.size());
        assertTrue(moves.contains(new Move(WHITE_KING, H1, G1)));
        assertTrue(moves.contains(new Move(WHITE_KING, H1, G2)));
        assertTrue(moves.contains(new Move(WHITE_KING, H1, H2)));
    }

    @Test
    public void testKingMovesInCorner2() {
        Board b = Board.INSTANCE;
        b.setPos("k7/8/8/8/8/8/8/7K b - - 0 1");

        List<Move> moves = new ArrayList<>();
        MoveGen.genKingMoves(b, moves, true, true);

        assertEquals(3, moves.size());
        assertTrue(moves.contains(new Move(BLACK_KING, A8, A7)));
        assertTrue(moves.contains(new Move(BLACK_KING, A8, B7)));
        assertTrue(moves.contains(new Move(BLACK_KING, A8, B8)));
    }

    @Test
    public void testKingNoCastleToEscapeCheck() {
        Board b = Board.INSTANCE;
        b.setPos("3k4/8/8/2N1P3/7q/8/4P3/R3K2R w KQ - 0 1");

        List<Move> moves = new ArrayList<>();
        MoveGen.genKingMoves(b, moves, true, true);

        assertEquals(4, moves.size());
        assertTrue(moves.contains(new Move(WHITE_KING, E1, F1)));
        // Kf2 stays in check, but that's handled separately
        assertTrue(moves.contains(new Move(WHITE_KING, E1, F2)));
        assertTrue(moves.contains(new Move(WHITE_KING, E1, D1)));
        assertTrue(moves.contains(new Move(WHITE_KING, E1, D2)));
    }

    @Test
    public void testKingCannotCastleThroughCheck() {
        Board b = Board.INSTANCE;
        b.setPos("r3k2r/8/8/8/8/5Q2/8/4K3 b kq - 0 1");

        List<Move> moves = new ArrayList<>();
        MoveGen.genKingMoves(b, moves, true, true);

        assertEquals(6, moves.size());
        assertTrue(moves.contains(new Move(BLACK_KING, E8, F8)));
        assertTrue(moves.contains(new Move(BLACK_KING, E8, F7)));
        assertTrue(moves.contains(new Move(BLACK_KING, E8, D8)));
        assertTrue(moves.contains(new Move(BLACK_KING, E8, D7)));
        assertTrue(moves.contains(new Move(BLACK_KING, E8, E7)));
        assertTrue(moves.contains(new Move(BLACK_KING, E8, C8,true)));
    }

    @Test
    public void testKingCaptures() {
        Board b = Board.INSTANCE;
        b.setPos("8/8/3k4/2n1P3/8/8/3rP3/R3K2R b KQ - 0 1");

        List<Move> moves = new ArrayList<>();
        MoveGen.genKingMoves(b, moves, true, false);

        assertEquals(1, moves.size());
        assertTrue(moves.contains(new Move(BLACK_KING, D6, E5, WHITE_PAWN)));
    }

    @Test
    public void testKingNoncaptures() {
        Board b = Board.INSTANCE;
        b.setPos("8/8/3k4/2n1P3/8/8/3rP3/R3K2R b KQ - 0 1");

        List<Move> moves = new ArrayList<>();
        MoveGen.genKingMoves(b, moves, false,true);
        assertEquals(6, moves.size());

        assertTrue(moves.contains(new Move(BLACK_KING, D6, D7)));
        assertTrue(moves.contains(new Move(BLACK_KING, D6, E7)));
        assertTrue(moves.contains(new Move(BLACK_KING, D6, E6)));
        assertTrue(moves.contains(new Move(BLACK_KING, D6, D5)));
        assertTrue(moves.contains(new Move(BLACK_KING, D6, C6)));
        assertTrue(moves.contains(new Move(BLACK_KING, D6, C7)));
    }

    @Test
    public void testPawnMoves() {
        Board b = Board.INSTANCE;
        b.setPos("2b1k3/PP6/8/3pP3/4P3/8/6P1/4K3 w - d6 0 1");

        List<Move> moves = new ArrayList<>();
        MoveGen.genPawnMoves(b, moves, true, true);

        assertEquals(17, moves.size());
        assertTrue(moves.contains(new Move(WHITE_PAWN, A7, A8,null, WHITE_QUEEN)));
        assertTrue(moves.contains(new Move(WHITE_PAWN, A7, A8,null, WHITE_ROOK)));
        assertTrue(moves.contains(new Move(WHITE_PAWN, A7, A8,null, WHITE_BISHOP)));
        assertTrue(moves.contains(new Move(WHITE_PAWN, A7, A8,null, WHITE_KNIGHT)));
        assertTrue(moves.contains(new Move(WHITE_PAWN, B7, B8,null, WHITE_QUEEN)));
        assertTrue(moves.contains(new Move(WHITE_PAWN, B7, B8,null, WHITE_ROOK)));
        assertTrue(moves.contains(new Move(WHITE_PAWN, B7, B8,null, WHITE_BISHOP)));
        assertTrue(moves.contains(new Move(WHITE_PAWN, B7, B8,null, WHITE_KNIGHT)));
        // Capture + Promotion
        assertTrue(moves.contains(new Move(WHITE_PAWN, B7, C8, BLACK_BISHOP, WHITE_QUEEN)));
        assertTrue(moves.contains(new Move(WHITE_PAWN, B7, C8, BLACK_BISHOP, WHITE_ROOK)));
        assertTrue(moves.contains(new Move(WHITE_PAWN, B7, C8, BLACK_BISHOP, WHITE_BISHOP)));
        assertTrue(moves.contains(new Move(WHITE_PAWN, B7, C8, BLACK_BISHOP, WHITE_KNIGHT)));

        assertTrue(moves.contains(new Move(WHITE_PAWN, E5, E6)));
        // EP
        assertTrue(moves.contains(new Move(WHITE_PAWN, E5, D6, BLACK_PAWN,true)));
        assertTrue(moves.contains(new Move(WHITE_PAWN, E4, D5, BLACK_PAWN)));

        assertTrue(moves.contains(new Move(WHITE_PAWN, G2, G3)));
        assertTrue(moves.contains(new Move(WHITE_PAWN, G2, G4)));
    }

    @Test
    public void testPawnCaptures() {
        Board b = Board.INSTANCE;
        b.setPos("2b1k3/PP6/8/3pP3/4P3/8/6P1/4K3 w - d6 0 1");

        List<Move> moves = new ArrayList<>();
        MoveGen.genPawnMoves(b, moves, true, false);

        assertEquals(14, moves.size());
        assertTrue(moves.contains(new Move(WHITE_PAWN, A7, A8, null, WHITE_QUEEN)));
        assertTrue(moves.contains(new Move(WHITE_PAWN, A7, A8, null, WHITE_ROOK)));
        assertTrue(moves.contains(new Move(WHITE_PAWN, A7, A8, null, WHITE_BISHOP)));
        assertTrue(moves.contains(new Move(WHITE_PAWN, A7, A8, null, WHITE_KNIGHT)));
        assertTrue(moves.contains(new Move(WHITE_PAWN, B7, B8, null, WHITE_QUEEN)));
        assertTrue(moves.contains(new Move(WHITE_PAWN, B7, B8, null, WHITE_ROOK)));
        assertTrue(moves.contains(new Move(WHITE_PAWN, B7, B8, null, WHITE_BISHOP)));
        assertTrue(moves.contains(new Move(WHITE_PAWN, B7, B8, null, WHITE_KNIGHT)));
        // Capture + Promotion
        assertTrue(moves.contains(new Move(WHITE_PAWN, B7, C8, BLACK_BISHOP, WHITE_QUEEN)));
        assertTrue(moves.contains(new Move(WHITE_PAWN, B7, C8, BLACK_BISHOP, WHITE_ROOK)));
        assertTrue(moves.contains(new Move(WHITE_PAWN, B7, C8, BLACK_BISHOP, WHITE_BISHOP)));
        assertTrue(moves.contains(new Move(WHITE_PAWN, B7, C8, BLACK_BISHOP, WHITE_KNIGHT)));

        // EP
        assertTrue(moves.contains(new Move(WHITE_PAWN, E5, D6, BLACK_PAWN, true)));
        assertTrue(moves.contains(new Move(WHITE_PAWN, E4, D5, BLACK_PAWN)));
    }

    @Test
    public void testPawnNoncaps() {
        Board b = Board.INSTANCE;
        b.setPos("2b1k3/PP6/8/3pP3/4P3/8/6P1/4K3 w - d6 0 1");

        List<Move> moves = new ArrayList<>();
        MoveGen.genPawnMoves(b, moves, false, true);

        assertEquals(3, moves.size());
        assertTrue(moves.contains(new Move(WHITE_PAWN, E5, E6)));
        assertTrue(moves.contains(new Move(WHITE_PAWN, G2, G3)));
        assertTrue(moves.contains(new Move(WHITE_PAWN, G2, G4)));
    }

    @Test
    public void testPawnMoves2() {
        Board b = Board.INSTANCE;
        b.setPos("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1");

        List<Move> moves = new ArrayList<>();
        MoveGen.genPawnMoves(b, moves, true, true);

        assertEquals(16, moves.size());
        assertTrue(moves.contains(new Move(BLACK_PAWN, D7, D6)));
        assertTrue(moves.contains(new Move(BLACK_PAWN, D7, D5)));
    }

    @Test
    public void testMovesFromInitialPos() {
        Board b = Board.INSTANCE;
        b.resetBoard();

        List<Move> moves = MoveGen.genLegalMoves(b);
        assertEquals(20, moves.size());

        assertTrue(moves.contains(new Move(WHITE_PAWN, A2, A3)));
        assertTrue(moves.contains(new Move(WHITE_PAWN, A2, A4)));
        assertTrue(moves.contains(new Move(WHITE_PAWN, B2, B3)));
        assertTrue(moves.contains(new Move(WHITE_PAWN, B2, B4)));
        assertTrue(moves.contains(new Move(WHITE_PAWN, C2, C3)));
        assertTrue(moves.contains(new Move(WHITE_PAWN, C2, C4)));
        assertTrue(moves.contains(new Move(WHITE_PAWN, D2, D3)));
        assertTrue(moves.contains(new Move(WHITE_PAWN, D2, D4)));
        assertTrue(moves.contains(new Move(WHITE_PAWN, E2, E3)));
        assertTrue(moves.contains(new Move(WHITE_PAWN, E2, E4)));
        assertTrue(moves.contains(new Move(WHITE_PAWN, F2, F3)));
        assertTrue(moves.contains(new Move(WHITE_PAWN, F2, F4)));
        assertTrue(moves.contains(new Move(WHITE_PAWN, G2, G3)));
        assertTrue(moves.contains(new Move(WHITE_PAWN, G2, G4)));
        assertTrue(moves.contains(new Move(WHITE_PAWN, H2, H3)));
        assertTrue(moves.contains(new Move(WHITE_PAWN, H2, H4)));

        assertTrue(moves.contains(new Move(WHITE_KNIGHT, B1, A3)));
        assertTrue(moves.contains(new Move(WHITE_KNIGHT, B1, C3)));
        assertTrue(moves.contains(new Move(WHITE_KNIGHT, G1, F3)));
        assertTrue(moves.contains(new Move(WHITE_KNIGHT, G1, H3)));
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
        b.setPos("8/8/8/3pP3/8/8/K6k/8 w - d6");

        List<Move> moves = MoveGen.genPseudoLegalMoves(b, true,false);
        assertEquals(1, moves.size());
        MoveParser mp = new MoveParser();
        assertTrue(moves.contains(mp.parseMove("e5d6", b)));
    }
}
