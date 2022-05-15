package com.jamesswafford.chess4j.movegen;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.pieces.Pawn;
import org.junit.Test;

import static org.junit.Assert.*;

import static com.jamesswafford.chess4j.board.squares.Square.*;
import static com.jamesswafford.chess4j.movegen.Mobility.bishopMobility;

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

}
