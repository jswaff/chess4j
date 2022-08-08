package com.jamesswafford.chess4j.movegen;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.pieces.Pawn;
import com.jamesswafford.chess4j.pieces.Queen;
import com.jamesswafford.chess4j.pieces.Rook;

import org.junit.Test;

import static org.junit.Assert.*;

import static com.jamesswafford.chess4j.board.squares.Square.*;
import static com.jamesswafford.chess4j.movegen.Mobility.*;

public class MobilityTest {

    @Test
    public void bishopMobility1() {

        Board board = new Board();
        assertEquals(0, bishopMobility(board, F1));
        assertEquals(0, bishopMobility(board, C8));

        board.applyMove(new Move(Pawn.WHITE_PAWN, E2, E4));
        assertEquals(5, bishopMobility(board, F1));
        assertEquals(0, bishopMobility(board, C8));

        board.applyMove(new Move(Pawn.BLACK_PAWN, D7, D5));
        assertEquals(5, bishopMobility(board, F1));
        assertEquals(5, bishopMobility(board, C8));

        board.applyMove(new Move(Pawn.WHITE_PAWN, H2, H3));
        assertEquals(5, bishopMobility(board, F1));
        assertEquals(4, bishopMobility(board, C8));

        board.applyMove(new Move(Pawn.BLACK_PAWN, B7, B5));
        assertEquals(3, bishopMobility(board, F1));
        assertEquals(6, bishopMobility(board, C8));
    }

    @Test
    public void rookMobility1() {

        Board board = new Board();
        assertEquals(0, rookMobility(board, A1));
        assertEquals(0, rookMobility(board, A8));

        board.applyMove(new Move(Pawn.WHITE_PAWN, A2, A4));
        assertEquals(2, rookMobility(board, A1));
        assertEquals(0, rookMobility(board, A8));

        board.applyMove(new Move(Pawn.BLACK_PAWN, A7, A6));
        assertEquals(2, rookMobility(board, A1));
        assertEquals(1, rookMobility(board, A8));

        board.applyMove(new Move(Rook.WHITE_ROOK, A1, A3));
        assertEquals(9, rookMobility(board, A3));
        assertEquals(1, rookMobility(board, A8));
    }

    @Test
    public void queenMobility1() {

        Board board = new Board();
        assertEquals(0, queenMobility(board, D1));
        assertEquals(0, queenMobility(board, D8));

        board.applyMove(new Move(Pawn.WHITE_PAWN, D2, D4));
        assertEquals(2, queenMobility(board, D1));
        assertEquals(0, queenMobility(board, D8));

        board.applyMove(new Move(Pawn.BLACK_PAWN, E7, E5));
        assertEquals(2, queenMobility(board, D1));
        assertEquals(4, queenMobility(board, D8));

        board.applyMove(new Move(Queen.WHITE_QUEEN, D1, D3));
        assertEquals(15, queenMobility(board, D3));
        assertEquals(4, queenMobility(board, D8));
    }

}
