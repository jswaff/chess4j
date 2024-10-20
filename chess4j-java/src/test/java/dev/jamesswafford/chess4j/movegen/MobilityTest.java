package dev.jamesswafford.chess4j.movegen;

import dev.jamesswafford.chess4j.board.Board;
import dev.jamesswafford.chess4j.board.Move;
import dev.jamesswafford.chess4j.pieces.Pawn;
import dev.jamesswafford.chess4j.pieces.Queen;
import dev.jamesswafford.chess4j.pieces.Rook;

import dev.jamesswafford.chess4j.board.squares.Square;
import org.junit.Test;

import static org.junit.Assert.*;

import static dev.jamesswafford.chess4j.movegen.Mobility.*;

public class MobilityTest {

    @Test
    public void bishopMobility1() {

        Board board = new Board();
        assertEquals(0, bishopMobility(board, Square.F1));
        assertEquals(0, bishopMobility(board, Square.C8));

        board.applyMove(new Move(Pawn.WHITE_PAWN, Square.E2, Square.E4));
        assertEquals(5, bishopMobility(board, Square.F1));
        assertEquals(0, bishopMobility(board, Square.C8));

        board.applyMove(new Move(Pawn.BLACK_PAWN, Square.D7, Square.D5));
        assertEquals(5, bishopMobility(board, Square.F1));
        assertEquals(5, bishopMobility(board, Square.C8));

        board.applyMove(new Move(Pawn.WHITE_PAWN, Square.H2, Square.H3));
        assertEquals(5, bishopMobility(board, Square.F1));
        assertEquals(4, bishopMobility(board, Square.C8));

        board.applyMove(new Move(Pawn.BLACK_PAWN, Square.B7, Square.B5));
        assertEquals(3, bishopMobility(board, Square.F1));
        assertEquals(6, bishopMobility(board, Square.C8));
    }

    @Test
    public void rookMobility1() {

        Board board = new Board();
        assertEquals(0, rookMobility(board, Square.A1));
        assertEquals(0, rookMobility(board, Square.A8));

        board.applyMove(new Move(Pawn.WHITE_PAWN, Square.A2, Square.A4));
        assertEquals(2, rookMobility(board, Square.A1));
        assertEquals(0, rookMobility(board, Square.A8));

        board.applyMove(new Move(Pawn.BLACK_PAWN, Square.A7, Square.A6));
        assertEquals(2, rookMobility(board, Square.A1));
        assertEquals(1, rookMobility(board, Square.A8));

        board.applyMove(new Move(Rook.WHITE_ROOK, Square.A1, Square.A3));
        assertEquals(9, rookMobility(board, Square.A3));
        assertEquals(1, rookMobility(board, Square.A8));
    }

    @Test
    public void queenMobility1() {

        Board board = new Board();
        assertEquals(0, queenMobility(board, Square.D1));
        assertEquals(0, queenMobility(board, Square.D8));

        board.applyMove(new Move(Pawn.WHITE_PAWN, Square.D2, Square.D4));
        assertEquals(2, queenMobility(board, Square.D1));
        assertEquals(0, queenMobility(board, Square.D8));

        board.applyMove(new Move(Pawn.BLACK_PAWN, Square.E7, Square.E5));
        assertEquals(2, queenMobility(board, Square.D1));
        assertEquals(4, queenMobility(board, Square.D8));

        board.applyMove(new Move(Queen.WHITE_QUEEN, Square.D1, Square.D3));
        assertEquals(15, queenMobility(board, Square.D3));
        assertEquals(4, queenMobility(board, Square.D8));
    }

}
