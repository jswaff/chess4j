package com.jamesswafford.chess4j.utils;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.board.squares.Square;

import static org.junit.Assert.*;

import static com.jamesswafford.chess4j.pieces.Pawn.*;
import static com.jamesswafford.chess4j.pieces.Knight.*;
import static com.jamesswafford.chess4j.board.squares.File.*;
import static com.jamesswafford.chess4j.board.squares.Rank.*;


public class MoveUtilsTest {

    @Test
    public void testMoveToTopList() {
        Move m1 = new Move(WHITE_PAWN,Square.valueOf(FILE_E, RANK_2),Square.valueOf(FILE_E, RANK_3));
        Move m2 = new Move(WHITE_PAWN,Square.valueOf(FILE_E, RANK_2),Square.valueOf(FILE_E, RANK_4));
        Move m3 = new Move(WHITE_PAWN,Square.valueOf(FILE_A, RANK_2),Square.valueOf(FILE_A, RANK_3));
        Move m4 = new Move(WHITE_KNIGHT,Square.valueOf(FILE_G, RANK_1),Square.valueOf(FILE_F, RANK_3));

        List<Move> moves = new ArrayList<>();
        moves.add(m1);
        moves.add(m2);
        moves.add(m3);
        moves.add(m4);

        assertEquals(m1, moves.get(0));
        MoveUtils.putMoveAtTop(moves, m3);
        assertEquals(4, moves.size());
        assertEquals(m3, moves.get(0));
    }

    @Test
    public void testMoveToTopArray() {
        Move m1 = new Move(WHITE_PAWN,Square.valueOf(FILE_E, RANK_2),Square.valueOf(FILE_E, RANK_3));
        Move m2 = new Move(WHITE_PAWN,Square.valueOf(FILE_E, RANK_2),Square.valueOf(FILE_E, RANK_4));
        Move m3 = new Move(WHITE_PAWN,Square.valueOf(FILE_A, RANK_2),Square.valueOf(FILE_A, RANK_3));
        Move m4 = new Move(WHITE_KNIGHT,Square.valueOf(FILE_G, RANK_1),Square.valueOf(FILE_F, RANK_3));

        Move[] moves = new Move[] { m1,m2,m3,m4 };

        assertEquals(m1, moves[0]);
        MoveUtils.putMoveAtTop(moves, m3);
        assertEquals(4, moves.length);
        assertEquals(m3, moves[0]);
    }

    @Test
    public void testSwapList() {
        Move m0 = new Move(WHITE_PAWN, Square.valueOf(FILE_E, RANK_2), Square.valueOf(FILE_E, RANK_3));
        Move m1 = new Move(WHITE_PAWN, Square.valueOf(FILE_E, RANK_2), Square.valueOf(FILE_E, RANK_4));
        Move m2 = new Move(WHITE_PAWN, Square.valueOf(FILE_A, RANK_2), Square.valueOf(FILE_A, RANK_3));
        Move m3 = new Move(WHITE_KNIGHT, Square.valueOf(FILE_G, RANK_1), Square.valueOf(FILE_F, RANK_3));

        List<Move> moves = new ArrayList<>();
        moves.add(m0);
        moves.add(m1);
        moves.add(m2);
        moves.add(m3);

        assertEquals(m1, moves.get(1));
        MoveUtils.swap(moves, 1, 3);
        assertEquals(m0, moves.get(0));
        assertEquals(m3, moves.get(1));
        assertEquals(m2, moves.get(2));
        assertEquals(m1, moves.get(3));

        // swap same index
        MoveUtils.swap(moves, 2,2);
        assertEquals(m0, moves.get(0));
        assertEquals(m3, moves.get(1));
        assertEquals(m2, moves.get(2));
        assertEquals(m1, moves.get(3));
    }

    @Test
    public void testSwapArray() {
        Move m0 = new Move(WHITE_PAWN, Square.valueOf(FILE_E, RANK_2), Square.valueOf(FILE_E, RANK_3));
        Move m1 = new Move(WHITE_PAWN, Square.valueOf(FILE_E, RANK_2), Square.valueOf(FILE_E, RANK_4));
        Move m2 = new Move(WHITE_PAWN, Square.valueOf(FILE_A, RANK_2), Square.valueOf(FILE_A, RANK_3));
        Move m3 = new Move(WHITE_KNIGHT, Square.valueOf(FILE_G, RANK_1), Square.valueOf(FILE_F, RANK_3));

        Move[] moves = new Move[] { m0,m1,m2,m3 };

        assertEquals(m1, moves[1]);
        MoveUtils.swap(moves, 1, 3);
        assertEquals(m0, moves[0]);
        assertEquals(m3, moves[1]);
        assertEquals(m2, moves[2]);
        assertEquals(m1, moves[3]);

        // swap same index
        MoveUtils.swap(moves, 2,2);
        assertEquals(m0, moves[0]);
        assertEquals(m3, moves[1]);
        assertEquals(m2, moves[2]);
        assertEquals(m1, moves[3]);
    }

    @Test
    public void testIndexOfList() {
        Move m0 = new Move(WHITE_PAWN, Square.valueOf(FILE_E, RANK_2), Square.valueOf(FILE_E, RANK_3));
        Move m1 = new Move(WHITE_PAWN, Square.valueOf(FILE_E, RANK_2), Square.valueOf(FILE_E, RANK_4));
        Move m2 = new Move(WHITE_PAWN, Square.valueOf(FILE_A, RANK_2), Square.valueOf(FILE_A, RANK_3));
        Move m3 = new Move(WHITE_KNIGHT, Square.valueOf(FILE_G, RANK_1), Square.valueOf(FILE_F, RANK_3));

        List<Move> moves = new ArrayList<>();
        moves.add(m0);
        moves.add(m1);
        moves.add(m2);
        moves.add(m3);

        assertEquals(1,MoveUtils.indexOf(moves, m1, 0));
        assertEquals(1,MoveUtils.indexOf(moves, m1, 1));
        assertEquals(-1,MoveUtils.indexOf(moves,m1,2));
        assertEquals(-1,MoveUtils.indexOf(moves, new Move(WHITE_PAWN,Square.valueOf(FILE_C, RANK_2),Square.valueOf(FILE_C, RANK_4)), 0));
    }

    @Test
    public void testIndexOfArray() {
        Move m0 = new Move(WHITE_PAWN,Square.valueOf(FILE_E, RANK_2),Square.valueOf(FILE_E, RANK_3));
        Move m1 = new Move(WHITE_PAWN,Square.valueOf(FILE_E, RANK_2),Square.valueOf(FILE_E, RANK_4));
        Move m2 = new Move(WHITE_PAWN,Square.valueOf(FILE_A, RANK_2),Square.valueOf(FILE_A, RANK_3));
        Move m3 = new Move(WHITE_KNIGHT,Square.valueOf(FILE_G, RANK_1),Square.valueOf(FILE_F, RANK_3));

        Move[] moves = new Move[] { m0,m1,m2,m3 };

        assertEquals(1,MoveUtils.indexOf(moves, m1, 0));
        assertEquals(1,MoveUtils.indexOf(moves, m1, 1));
        assertEquals(-1,MoveUtils.indexOf(moves,m1,2));
        assertEquals(-1,MoveUtils.indexOf(moves, new Move(WHITE_PAWN,Square.valueOf(FILE_C, RANK_2),Square.valueOf(FILE_C, RANK_4)), 0));
    }


}
