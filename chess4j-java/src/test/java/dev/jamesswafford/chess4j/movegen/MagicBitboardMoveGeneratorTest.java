package dev.jamesswafford.chess4j.movegen;

import java.util.ArrayList;
import java.util.List;

import dev.jamesswafford.chess4j.board.Board;
import dev.jamesswafford.chess4j.board.Move;

import dev.jamesswafford.chess4j.board.squares.Square;
import dev.jamesswafford.chess4j.io.MoveParser;
import dev.jamesswafford.chess4j.pieces.*;
import org.junit.Test;

import static org.junit.Assert.*;


public class MagicBitboardMoveGeneratorTest {

    @Test
    public void testKnightMoves() {
        Board board = new Board();

        List<Move> moves = new ArrayList<>();
        MagicBitboardMoveGenerator.genKnightMoves(board,moves,true,true);

        assertEquals(4, moves.size());
        assertTrue(moves.contains(new Move(Knight.WHITE_KNIGHT, Square.B1, Square.A3)));
        assertTrue(moves.contains(new Move(Knight.WHITE_KNIGHT, Square.B1, Square.C3)));
        assertTrue(moves.contains(new Move(Knight.WHITE_KNIGHT, Square.G1, Square.F3)));
        assertTrue(moves.contains(new Move(Knight.WHITE_KNIGHT, Square.G1, Square.H3)));
    }

    @Test
    public void testKnightCaptures() {
        Board board = new Board("4k3/8/3P1p2/8/4N3/8/8/4K3 w - - 0 1");

        List<Move> moves = new ArrayList<>();
        MagicBitboardMoveGenerator.genKnightMoves(board,moves,true,false);

        assertEquals(1, moves.size());
        assertTrue(moves.contains(new Move(Knight.WHITE_KNIGHT, Square.E4, Square.F6 , Pawn.BLACK_PAWN)));
    }

    @Test
    public void testKnightNoncaptures() {
        Board board = new Board("4k3/8/3P1p2/8/4N3/8/8/4K3 w - - 0 1");

        List<Move> moves = new ArrayList<>();
        MagicBitboardMoveGenerator.genKnightMoves(board,moves,false,true);

        assertEquals(6, moves.size());
        assertTrue(moves.contains(new Move(Knight.WHITE_KNIGHT, Square.E4, Square.G5)));
        assertTrue(moves.contains(new Move(Knight.WHITE_KNIGHT, Square.E4, Square.G3)));
        assertTrue(moves.contains(new Move(Knight.WHITE_KNIGHT, Square.E4, Square.F2)));
        assertTrue(moves.contains(new Move(Knight.WHITE_KNIGHT, Square.E4, Square.D2)));
        assertTrue(moves.contains(new Move(Knight.WHITE_KNIGHT, Square.E4, Square.C3)));
        assertTrue(moves.contains(new Move(Knight.WHITE_KNIGHT, Square.E4, Square.C5)));
    }

    @Test
    public void testBishopMoves() {
        Board board = new Board("4k3/2p3P1/8/4B3/4B3/3p1P2/8/4K3 w - - 0 1");

        List<Move> moves = new ArrayList<>();
        MagicBitboardMoveGenerator.genBishopMoves(board, moves, true, true);
        assertEquals(18, moves.size());
        assertTrue(moves.contains(new Move(Bishop.WHITE_BISHOP, Square.E5, Square.F6)));
        assertTrue(moves.contains(new Move(Bishop.WHITE_BISHOP, Square.E5, Square.F4)));
        assertTrue(moves.contains(new Move(Bishop.WHITE_BISHOP, Square.E5, Square.G3)));
        assertTrue(moves.contains(new Move(Bishop.WHITE_BISHOP, Square.E5, Square.H2)));
        assertTrue(moves.contains(new Move(Bishop.WHITE_BISHOP, Square.E5, Square.D4)));
        assertTrue(moves.contains(new Move(Bishop.WHITE_BISHOP, Square.E5, Square.C3)));
        assertTrue(moves.contains(new Move(Bishop.WHITE_BISHOP, Square.E5, Square.B2)));
        assertTrue(moves.contains(new Move(Bishop.WHITE_BISHOP, Square.E5, Square.A1)));
        assertTrue(moves.contains(new Move(Bishop.WHITE_BISHOP, Square.E5, Square.D6)));
        assertTrue(moves.contains(new Move(Bishop.WHITE_BISHOP, Square.E5, Square.C7, Pawn.BLACK_PAWN)));
        assertTrue(moves.contains(new Move(Bishop.WHITE_BISHOP, Square.E4, Square.F5)));
        assertTrue(moves.contains(new Move(Bishop.WHITE_BISHOP, Square.E4, Square.G6)));
        assertTrue(moves.contains(new Move(Bishop.WHITE_BISHOP, Square.E4, Square.H7)));
        assertTrue(moves.contains(new Move(Bishop.WHITE_BISHOP, Square.E4, Square.D3, Pawn.BLACK_PAWN)));
        assertTrue(moves.contains(new Move(Bishop.WHITE_BISHOP, Square.E4, Square.D5)));
        assertTrue(moves.contains(new Move(Bishop.WHITE_BISHOP, Square.E4, Square.C6)));
        assertTrue(moves.contains(new Move(Bishop.WHITE_BISHOP, Square.E4, Square.B7)));
        assertTrue(moves.contains(new Move(Bishop.WHITE_BISHOP, Square.E4, Square.A8)));
    }

    @Test
    public void testBishopCaptures() {
        Board board = new Board("4k3/2p3P1/8/4B3/4B3/3p1P2/8/4K3 w - - 0 1");

        List<Move> moves = new ArrayList<>();
        MagicBitboardMoveGenerator.genBishopMoves(board, moves, true, false);
        assertEquals(2, moves.size());
        assertTrue(moves.contains(new Move(Bishop.WHITE_BISHOP, Square.E5, Square.C7, Pawn.BLACK_PAWN)));
        assertTrue(moves.contains(new Move(Bishop.WHITE_BISHOP, Square.E4, Square.D3, Pawn.BLACK_PAWN)));
    }

    @Test
    public void testBishopNoncaptures() {
        Board board = new Board("4k3/2p3P1/8/4B3/4B3/3p1P2/8/4K3 w - - 0 1");

        List<Move> moves = new ArrayList<>();
        MagicBitboardMoveGenerator.genBishopMoves(board, moves, false, true);
        assertEquals(16, moves.size());
        assertTrue(moves.contains(new Move(Bishop.WHITE_BISHOP, Square.E5, Square.F6)));
        assertTrue(moves.contains(new Move(Bishop.WHITE_BISHOP, Square.E5, Square.F4)));
        assertTrue(moves.contains(new Move(Bishop.WHITE_BISHOP, Square.E5, Square.G3)));
        assertTrue(moves.contains(new Move(Bishop.WHITE_BISHOP, Square.E5, Square.H2)));
        assertTrue(moves.contains(new Move(Bishop.WHITE_BISHOP, Square.E5, Square.D4)));
        assertTrue(moves.contains(new Move(Bishop.WHITE_BISHOP, Square.E5, Square.C3)));
        assertTrue(moves.contains(new Move(Bishop.WHITE_BISHOP, Square.E5, Square.B2)));
        assertTrue(moves.contains(new Move(Bishop.WHITE_BISHOP, Square.E5, Square.A1)));
        assertTrue(moves.contains(new Move(Bishop.WHITE_BISHOP, Square.E5, Square.D6)));
        assertTrue(moves.contains(new Move(Bishop.WHITE_BISHOP, Square.E4, Square.F5)));
        assertTrue(moves.contains(new Move(Bishop.WHITE_BISHOP, Square.E4, Square.G6)));
        assertTrue(moves.contains(new Move(Bishop.WHITE_BISHOP, Square.E4, Square.H7)));
        assertTrue(moves.contains(new Move(Bishop.WHITE_BISHOP, Square.E4, Square.D5)));
        assertTrue(moves.contains(new Move(Bishop.WHITE_BISHOP, Square.E4, Square.C6)));
        assertTrue(moves.contains(new Move(Bishop.WHITE_BISHOP, Square.E4, Square.B7)));
        assertTrue(moves.contains(new Move(Bishop.WHITE_BISHOP, Square.E4, Square.A8)));
    }

    @Test
    public void testRookMoves() {
        Board board = new Board();

        List<Move> moves = new ArrayList<>();
        MagicBitboardMoveGenerator.genRookMoves(board, moves, true, true);
        assertEquals(0, moves.size());

        board.setPos("8/8/3k1p2/8/3K4/8/1R3r2/8 b - - 0 1");
        MagicBitboardMoveGenerator.genRookMoves(board, moves, true, true);

        assertEquals(10, moves.size());
        assertTrue(moves.contains(new Move(Rook.BLACK_ROOK, Square.F2, Square.F3)));
        assertTrue(moves.contains(new Move(Rook.BLACK_ROOK, Square.F2, Square.F4)));
        assertTrue(moves.contains(new Move(Rook.BLACK_ROOK, Square.F2, Square.F5)));
        assertTrue(moves.contains(new Move(Rook.BLACK_ROOK, Square.F2, Square.F1)));
        assertTrue(moves.contains(new Move(Rook.BLACK_ROOK, Square.F2, Square.G2)));
        assertTrue(moves.contains(new Move(Rook.BLACK_ROOK, Square.F2, Square.H2)));
        assertTrue(moves.contains(new Move(Rook.BLACK_ROOK, Square.F2, Square.E2)));
        assertTrue(moves.contains(new Move(Rook.BLACK_ROOK, Square.F2, Square.D2)));
        assertTrue(moves.contains(new Move(Rook.BLACK_ROOK, Square.F2, Square.C2)));
        assertTrue(moves.contains(new Move(Rook.BLACK_ROOK, Square.F2, Square.B2, Rook.WHITE_ROOK)));
    }

    @Test
    public void testRookCaptures() {
        Board board = new Board("8/8/3k1p2/8/3K4/8/1R3r2/8 b - - 0 1");

        List<Move> moves = new ArrayList<>();
        MagicBitboardMoveGenerator.genRookMoves(board, moves, true, false);

        assertEquals(1, moves.size());
        assertTrue(moves.contains(new Move(Rook.BLACK_ROOK, Square.F2, Square.B2, Rook.WHITE_ROOK)));
    }

    @Test
    public void testRookNoncaptures() {
        Board board = new Board("8/8/3k1p2/8/3K4/8/1R3r2/8 b - - 0 1");

        List<Move> moves = new ArrayList<>();
        MagicBitboardMoveGenerator.genRookMoves(board, moves, false, true);

        assertEquals(9, moves.size());
        assertTrue(moves.contains(new Move(Rook.BLACK_ROOK, Square.F2, Square.F3)));
        assertTrue(moves.contains(new Move(Rook.BLACK_ROOK, Square.F2, Square.F4)));
        assertTrue(moves.contains(new Move(Rook.BLACK_ROOK, Square.F2, Square.F5)));
        assertTrue(moves.contains(new Move(Rook.BLACK_ROOK, Square.F2, Square.F1)));
        assertTrue(moves.contains(new Move(Rook.BLACK_ROOK, Square.F2, Square.G2)));
        assertTrue(moves.contains(new Move(Rook.BLACK_ROOK, Square.F2, Square.H2)));
        assertTrue(moves.contains(new Move(Rook.BLACK_ROOK, Square.F2, Square.E2)));
        assertTrue(moves.contains(new Move(Rook.BLACK_ROOK, Square.F2, Square.D2)));
        assertTrue(moves.contains(new Move(Rook.BLACK_ROOK, Square.F2, Square.C2)));
    }

    @Test
    public void testQueenMoves() {
        Board board = new Board("8/8/3bk3/8/8/2K3Q1/8/8 w - - 0 1");

        List<Move> moves = new ArrayList<>();
        MagicBitboardMoveGenerator.genQueenMoves(board, moves, true, true);
        assertEquals(18, moves.size());
        assertTrue(moves.contains(new Move(Queen.WHITE_QUEEN, Square.G3, Square.G4)));
        assertTrue(moves.contains(new Move(Queen.WHITE_QUEEN, Square.G3, Square.G5)));
        assertTrue(moves.contains(new Move(Queen.WHITE_QUEEN, Square.G3, Square.G6)));
        assertTrue(moves.contains(new Move(Queen.WHITE_QUEEN, Square.G3, Square.G7)));
        assertTrue(moves.contains(new Move(Queen.WHITE_QUEEN, Square.G3, Square.G8)));
        assertTrue(moves.contains(new Move(Queen.WHITE_QUEEN, Square.G3, Square.H4)));
        assertTrue(moves.contains(new Move(Queen.WHITE_QUEEN, Square.G3, Square.H3)));
        assertTrue(moves.contains(new Move(Queen.WHITE_QUEEN, Square.G3, Square.H2)));
        assertTrue(moves.contains(new Move(Queen.WHITE_QUEEN, Square.G3, Square.G2)));
        assertTrue(moves.contains(new Move(Queen.WHITE_QUEEN, Square.G3, Square.G1)));
        assertTrue(moves.contains(new Move(Queen.WHITE_QUEEN, Square.G3, Square.F2)));
        assertTrue(moves.contains(new Move(Queen.WHITE_QUEEN, Square.G3, Square.E1)));
        assertTrue(moves.contains(new Move(Queen.WHITE_QUEEN, Square.G3, Square.F3)));
        assertTrue(moves.contains(new Move(Queen.WHITE_QUEEN, Square.G3, Square.E3)));
        assertTrue(moves.contains(new Move(Queen.WHITE_QUEEN, Square.G3, Square.D3)));
        assertTrue(moves.contains(new Move(Queen.WHITE_QUEEN, Square.G3, Square.F4)));
        assertTrue(moves.contains(new Move(Queen.WHITE_QUEEN, Square.G3, Square.E5)));
        assertTrue(moves.contains(new Move(Queen.WHITE_QUEEN, Square.G3, Square.D6, Bishop.BLACK_BISHOP)));
    }

    @Test
    public void testQueenCaptures() {
        Board board = new Board("8/8/3bk3/8/8/2K3Q1/8/8 w - - 0 1");

        List<Move> moves = new ArrayList<>();
        MagicBitboardMoveGenerator.genQueenMoves(board, moves, true, false);
        assertEquals(1, moves.size());
        assertTrue(moves.contains(new Move(Queen.WHITE_QUEEN, Square.G3, Square.D6 , Bishop.BLACK_BISHOP)));
    }

    @Test
    public void testQueenNoncaptures() {
        Board board = new Board("8/8/3bk3/8/8/2K3Q1/8/8 w - - 0 1");

        List<Move> moves = new ArrayList<>();
        MagicBitboardMoveGenerator.genQueenMoves(board, moves, false, true);
        assertEquals(17, moves.size());
        assertTrue(moves.contains(new Move(Queen.WHITE_QUEEN, Square.G3, Square.G4)));
        assertTrue(moves.contains(new Move(Queen.WHITE_QUEEN, Square.G3, Square.G5)));
        assertTrue(moves.contains(new Move(Queen.WHITE_QUEEN, Square.G3, Square.G6)));
        assertTrue(moves.contains(new Move(Queen.WHITE_QUEEN, Square.G3, Square.G7)));
        assertTrue(moves.contains(new Move(Queen.WHITE_QUEEN, Square.G3, Square.G8)));
        assertTrue(moves.contains(new Move(Queen.WHITE_QUEEN, Square.G3, Square.H4)));
        assertTrue(moves.contains(new Move(Queen.WHITE_QUEEN, Square.G3, Square.H3)));
        assertTrue(moves.contains(new Move(Queen.WHITE_QUEEN, Square.G3, Square.H2)));
        assertTrue(moves.contains(new Move(Queen.WHITE_QUEEN, Square.G3, Square.G2)));
        assertTrue(moves.contains(new Move(Queen.WHITE_QUEEN, Square.G3, Square.G1)));
        assertTrue(moves.contains(new Move(Queen.WHITE_QUEEN, Square.G3, Square.F2)));
        assertTrue(moves.contains(new Move(Queen.WHITE_QUEEN, Square.G3, Square.E1)));
        assertTrue(moves.contains(new Move(Queen.WHITE_QUEEN, Square.G3, Square.F3)));
        assertTrue(moves.contains(new Move(Queen.WHITE_QUEEN, Square.G3, Square.E3)));
        assertTrue(moves.contains(new Move(Queen.WHITE_QUEEN, Square.G3, Square.D3)));
        assertTrue(moves.contains(new Move(Queen.WHITE_QUEEN, Square.G3, Square.F4)));
        assertTrue(moves.contains(new Move(Queen.WHITE_QUEEN, Square.G3, Square.E5)));
    }


    @Test
    public void testKingMoves() {
        Board board = new Board("8/8/3k4/2n1P3/8/8/3rP3/R3K2R b KQ - 0 1");

        List<Move> moves = new ArrayList<>();
        MagicBitboardMoveGenerator.genKingMoves(board, moves, true, true);
        assertEquals(7, moves.size());
        assertTrue(moves.contains(new Move(King.BLACK_KING, Square.D6, Square.D7)));
        assertTrue(moves.contains(new Move(King.BLACK_KING, Square.D6, Square.E7)));
        assertTrue(moves.contains(new Move(King.BLACK_KING, Square.D6, Square.E6)));
        assertTrue(moves.contains(new Move(King.BLACK_KING, Square.D6, Square.E5, Pawn.WHITE_PAWN)));
        assertTrue(moves.contains(new Move(King.BLACK_KING, Square.D6, Square.D5)));
        assertTrue(moves.contains(new Move(King.BLACK_KING, Square.D6, Square.C6)));
        assertTrue(moves.contains(new Move(King.BLACK_KING, Square.D6, Square.C7)));

        // flip sides
        board.setPos("8/8/3k4/2n1P3/8/8/3rP3/RN2K2R w KQ - 0 1");
        moves.clear();
        MagicBitboardMoveGenerator.genKingMoves(board, moves, true, true);
        assertEquals(5, moves.size());
        assertTrue(moves.contains(new Move(King.WHITE_KING, Square.E1, Square.F2)));
        assertTrue(moves.contains(new Move(King.WHITE_KING, Square.E1, Square.F1)));
        assertTrue(moves.contains(new Move(King.WHITE_KING, Square.E1, Square.D1)));
        assertTrue(moves.contains(new Move(King.WHITE_KING, Square.E1, Square.D2, Rook.BLACK_ROOK)));
        assertTrue(moves.contains(new Move(King.WHITE_KING, Square.E1, Square.G1,true)));
    }

    @Test
    public void testKingMoves2() {
        Board board = new Board("3k4/8/8/2n1P3/8/8/3rP3/RN2K2R w KQ - 0 1");

        List<Move> moves = new ArrayList<>();
        MagicBitboardMoveGenerator.genKingMoves(board, moves, true, true);

        assertEquals(5, moves.size());
        assertTrue(moves.contains(new Move(King.WHITE_KING, Square.E1, Square.F2)));
        assertTrue(moves.contains(new Move(King.WHITE_KING, Square.E1, Square.F1)));
        // Kd1 illegal but that's handled elsewhere
        assertTrue(moves.contains(new Move(King.WHITE_KING, Square.E1, Square.D1)));
        assertTrue(moves.contains(new Move(King.WHITE_KING, Square.E1, Square.D2, Rook.BLACK_ROOK)));
        assertTrue(moves.contains(new Move(King.WHITE_KING, Square.E1, Square.G1,true)));
    }

    @Test
    public void testKingMovesInCorner() {
        Board board = new Board("k7/8/8/8/8/8/8/7K w - - 0 1");

        List<Move> moves = new ArrayList<>();
        MagicBitboardMoveGenerator.genKingMoves(board, moves, true, true);

        assertEquals(3, moves.size());
        assertTrue(moves.contains(new Move(King.WHITE_KING, Square.H1, Square.G1)));
        assertTrue(moves.contains(new Move(King.WHITE_KING, Square.H1, Square.G2)));
        assertTrue(moves.contains(new Move(King.WHITE_KING, Square.H1, Square.H2)));
    }

    @Test
    public void testKingMovesInCorner2() {
        Board board = new Board("k7/8/8/8/8/8/8/7K b - - 0 1");

        List<Move> moves = new ArrayList<>();
        MagicBitboardMoveGenerator.genKingMoves(board, moves, true, true);

        assertEquals(3, moves.size());
        assertTrue(moves.contains(new Move(King.BLACK_KING, Square.A8, Square.A7)));
        assertTrue(moves.contains(new Move(King.BLACK_KING, Square.A8, Square.B7)));
        assertTrue(moves.contains(new Move(King.BLACK_KING, Square.A8, Square.B8)));
    }

    @Test
    public void testKingNoCastleToEscapeCheck() {
        Board board = new Board("3k4/8/8/2N1P3/7q/8/4P3/R3K2R w KQ - 0 1");

        List<Move> moves = new ArrayList<>();
        MagicBitboardMoveGenerator.genKingMoves(board, moves, true, true);

        assertEquals(4, moves.size());
        assertTrue(moves.contains(new Move(King.WHITE_KING, Square.E1, Square.F1)));
        // Kf2 stays in check, but that's handled separately
        assertTrue(moves.contains(new Move(King.WHITE_KING, Square.E1, Square.F2)));
        assertTrue(moves.contains(new Move(King.WHITE_KING, Square.E1, Square.D1)));
        assertTrue(moves.contains(new Move(King.WHITE_KING, Square.E1, Square.D2)));
    }

    @Test
    public void testKingCannotCastleThroughCheck() {
        Board board = new Board("r3k2r/8/8/8/8/5Q2/8/4K3 b kq - 0 1");

        List<Move> moves = new ArrayList<>();
        MagicBitboardMoveGenerator.genKingMoves(board, moves, true, true);

        assertEquals(6, moves.size());
        assertTrue(moves.contains(new Move(King.BLACK_KING, Square.E8, Square.F8)));
        assertTrue(moves.contains(new Move(King.BLACK_KING, Square.E8, Square.F7)));
        assertTrue(moves.contains(new Move(King.BLACK_KING, Square.E8, Square.D8)));
        assertTrue(moves.contains(new Move(King.BLACK_KING, Square.E8, Square.D7)));
        assertTrue(moves.contains(new Move(King.BLACK_KING, Square.E8, Square.E7)));
        assertTrue(moves.contains(new Move(King.BLACK_KING, Square.E8, Square.C8,true)));
    }

    @Test
    public void testKingCaptures() {
        Board board = new Board("8/8/3k4/2n1P3/8/8/3rP3/R3K2R b KQ - 0 1");

        List<Move> moves = new ArrayList<>();
        MagicBitboardMoveGenerator.genKingMoves(board, moves, true, false);

        assertEquals(1, moves.size());
        assertTrue(moves.contains(new Move(King.BLACK_KING, Square.D6, Square.E5, Pawn.WHITE_PAWN)));
    }

    @Test
    public void testKingNoncaptures() {
        Board board = new Board("8/8/3k4/2n1P3/8/8/3rP3/R3K2R b KQ - 0 1");

        List<Move> moves = new ArrayList<>();
        MagicBitboardMoveGenerator.genKingMoves(board, moves, false,true);
        assertEquals(6, moves.size());

        assertTrue(moves.contains(new Move(King.BLACK_KING, Square.D6, Square.D7)));
        assertTrue(moves.contains(new Move(King.BLACK_KING, Square.D6, Square.E7)));
        assertTrue(moves.contains(new Move(King.BLACK_KING, Square.D6, Square.E6)));
        assertTrue(moves.contains(new Move(King.BLACK_KING, Square.D6, Square.D5)));
        assertTrue(moves.contains(new Move(King.BLACK_KING, Square.D6, Square.C6)));
        assertTrue(moves.contains(new Move(King.BLACK_KING, Square.D6, Square.C7)));
    }

    @Test
    public void testPawnMoves() {
        Board board = new Board("2b1k3/PP6/8/3pP3/4P3/8/6P1/4K3 w - d6 0 1");

        List<Move> moves = new ArrayList<>();
        MagicBitboardMoveGenerator.genPawnMoves(board, moves, true, true);

        assertEquals(17, moves.size());
        assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN, Square.A7, Square.A8,null, Queen.WHITE_QUEEN)));
        assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN, Square.A7, Square.A8,null, Rook.WHITE_ROOK)));
        assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN, Square.A7, Square.A8,null, Bishop.WHITE_BISHOP)));
        assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN, Square.A7, Square.A8,null, Knight.WHITE_KNIGHT)));
        assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN, Square.B7, Square.B8,null, Queen.WHITE_QUEEN)));
        assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN, Square.B7, Square.B8,null, Rook.WHITE_ROOK)));
        assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN, Square.B7, Square.B8,null, Bishop.WHITE_BISHOP)));
        assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN, Square.B7, Square.B8,null, Knight.WHITE_KNIGHT)));
        // Capture + Promotion
        assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN, Square.B7, Square.C8, Bishop.BLACK_BISHOP, Queen.WHITE_QUEEN)));
        assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN, Square.B7, Square.C8, Bishop.BLACK_BISHOP, Rook.WHITE_ROOK)));
        assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN, Square.B7, Square.C8, Bishop.BLACK_BISHOP, Bishop.WHITE_BISHOP)));
        assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN, Square.B7, Square.C8, Bishop.BLACK_BISHOP, Knight.WHITE_KNIGHT)));

        assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN, Square.E5, Square.E6)));
        // EP
        assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN, Square.E5, Square.D6, Pawn.BLACK_PAWN,true)));
        assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN, Square.E4, Square.D5, Pawn.BLACK_PAWN)));

        assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN, Square.G2, Square.G3)));
        assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN, Square.G2, Square.G4)));
    }

    @Test
    public void testPawnCaptures() {
        Board board = new Board("2b1k3/PP6/8/3pP3/4P3/8/6P1/4K3 w - d6 0 1");

        List<Move> moves = new ArrayList<>();
        MagicBitboardMoveGenerator.genPawnMoves(board, moves, true, false);

        assertEquals(14, moves.size());
        assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN, Square.A7, Square.A8, null, Queen.WHITE_QUEEN)));
        assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN, Square.A7, Square.A8, null, Rook.WHITE_ROOK)));
        assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN, Square.A7, Square.A8, null, Bishop.WHITE_BISHOP)));
        assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN, Square.A7, Square.A8, null, Knight.WHITE_KNIGHT)));
        assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN, Square.B7, Square.B8, null, Queen.WHITE_QUEEN)));
        assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN, Square.B7, Square.B8, null, Rook.WHITE_ROOK)));
        assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN, Square.B7, Square.B8, null, Bishop.WHITE_BISHOP)));
        assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN, Square.B7, Square.B8, null, Knight.WHITE_KNIGHT)));
        // Capture + Promotion
        assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN, Square.B7, Square.C8, Bishop.BLACK_BISHOP, Queen.WHITE_QUEEN)));
        assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN, Square.B7, Square.C8, Bishop.BLACK_BISHOP, Rook.WHITE_ROOK)));
        assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN, Square.B7, Square.C8, Bishop.BLACK_BISHOP, Bishop.WHITE_BISHOP)));
        assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN, Square.B7, Square.C8, Bishop.BLACK_BISHOP, Knight.WHITE_KNIGHT)));

        // EP
        assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN, Square.E5, Square.D6, Pawn.BLACK_PAWN, true)));
        assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN, Square.E4, Square.D5, Pawn.BLACK_PAWN)));
    }

    @Test
    public void testPawnNoncaps() {
        Board board = new Board("2b1k3/PP6/8/3pP3/4P3/8/6P1/4K3 w - d6 0 1");

        List<Move> moves = new ArrayList<>();
        MagicBitboardMoveGenerator.genPawnMoves(board, moves, false, true);

        assertEquals(3, moves.size());
        assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN, Square.E5, Square.E6)));
        assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN, Square.G2, Square.G3)));
        assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN, Square.G2, Square.G4)));
    }

    @Test
    public void testPawnMoves2() {
        Board board = new Board("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1");

        List<Move> moves = new ArrayList<>();
        MagicBitboardMoveGenerator.genPawnMoves(board, moves, true, true);

        assertEquals(16, moves.size());
        assertTrue(moves.contains(new Move(Pawn.BLACK_PAWN, Square.D7, Square.D6)));
        assertTrue(moves.contains(new Move(Pawn.BLACK_PAWN, Square.D7, Square.D5)));
    }

    @Test
    public void testMovesFromInitialPos() {
        Board board = new Board();

        List<Move> moves = MagicBitboardMoveGenerator.genLegalMoves(board);
        assertEquals(20, moves.size());

        assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN, Square.A2, Square.A3)));
        assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN, Square.A2, Square.A4)));
        assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN, Square.B2, Square.B3)));
        assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN, Square.B2, Square.B4)));
        assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN, Square.C2, Square.C3)));
        assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN, Square.C2, Square.C4)));
        assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN, Square.D2, Square.D3)));
        assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN, Square.D2, Square.D4)));
        assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN, Square.E2, Square.E3)));
        assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN, Square.E2, Square.E4)));
        assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN, Square.F2, Square.F3)));
        assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN, Square.F2, Square.F4)));
        assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN, Square.G2, Square.G3)));
        assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN, Square.G2, Square.G4)));
        assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN, Square.H2, Square.H3)));
        assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN, Square.H2, Square.H4)));

        assertTrue(moves.contains(new Move(Knight.WHITE_KNIGHT, Square.B1, Square.A3)));
        assertTrue(moves.contains(new Move(Knight.WHITE_KNIGHT, Square.B1, Square.C3)));
        assertTrue(moves.contains(new Move(Knight.WHITE_KNIGHT, Square.G1, Square.F3)));
        assertTrue(moves.contains(new Move(Knight.WHITE_KNIGHT, Square.G1, Square.H3)));
    }

    @Test
    public void tsetCapturesPromosOnlyFromInitialPosition() {
        Board board = new Board();
        List<Move> moves = MagicBitboardMoveGenerator.genPseudoLegalMoves(board, true,false);
        assertTrue(moves.isEmpty());
    }

    @Test
    public void testCapturesPromosOnlyContainsPromotions() throws Exception {
        Board board = new Board( "8/4Pk1p/6p1/1r6/8/5N2/2B2PPP/b5K1 w - -");

        List<Move> moves = MagicBitboardMoveGenerator.genPseudoLegalMoves(board, true,false);

        MoveParser mp = new MoveParser();
        Move e7e8q = mp.parseMove("e7e8=q", board);
        Move e7e8r = mp.parseMove("e7e8=r", board);
        Move e7e8b = mp.parseMove("e7e8=b", board);
        Move e7e8n = mp.parseMove("e7e8=n", board);
        Move c2g6 = mp.parseMove("c2g6", board);
        assertTrue(moves.contains(e7e8q));
        assertTrue(moves.contains(e7e8r));
        assertTrue(moves.contains(e7e8b));
        assertTrue(moves.contains(e7e8n));
        assertTrue(moves.contains(c2g6));
    }

    @Test
    public void testCapturesPromosOnlyContainsEP() throws Exception {
        Board board = new Board("8/8/8/3pP3/8/8/K6k/8 w - d6");

        List<Move> moves = MagicBitboardMoveGenerator.genPseudoLegalMoves(board, true,false);
        assertEquals(1, moves.size());
        MoveParser mp = new MoveParser();
        assertTrue(moves.contains(mp.parseMove("e5d6", board)));
    }
}
