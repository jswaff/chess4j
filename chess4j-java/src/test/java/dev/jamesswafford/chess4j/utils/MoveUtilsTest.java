package dev.jamesswafford.chess4j.utils;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import dev.jamesswafford.chess4j.board.Move;

import static org.junit.Assert.*;

import static dev.jamesswafford.chess4j.pieces.Pawn.*;
import static dev.jamesswafford.chess4j.pieces.Knight.*;
import static dev.jamesswafford.chess4j.board.squares.Square.*;


public class MoveUtilsTest {

    @Test
    public void testMoveToTopList() {
        Move m1 = new Move(WHITE_PAWN, E2, E3);
        Move m2 = new Move(WHITE_PAWN, E2, E4);
        Move m3 = new Move(WHITE_PAWN, A2, A3);
        Move m4 = new Move(WHITE_KNIGHT, G1, F3);

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
        Move m1 = new Move(WHITE_PAWN, E2, E3);
        Move m2 = new Move(WHITE_PAWN, E2, E4);
        Move m3 = new Move(WHITE_PAWN, A2, A3);
        Move m4 = new Move(WHITE_KNIGHT, G1, F3);

        Move[] moves = new Move[] { m1,m2,m3,m4 };

        assertEquals(m1, moves[0]);
        MoveUtils.putMoveAtTop(moves, m3);
        assertEquals(4, moves.length);
        assertEquals(m3, moves[0]);
    }

    @Test
    public void testSwapList() {
        Move m0 = new Move(WHITE_PAWN, E2, E3);
        Move m1 = new Move(WHITE_PAWN, E2, E4);
        Move m2 = new Move(WHITE_PAWN, A2, A3);
        Move m3 = new Move(WHITE_KNIGHT, G1, F3);

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
        Move m0 = new Move(WHITE_PAWN, E2, E3);
        Move m1 = new Move(WHITE_PAWN, E2, E4);
        Move m2 = new Move(WHITE_PAWN, A2, A3);
        Move m3 = new Move(WHITE_KNIGHT, G1, F3);

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
        Move m0 = new Move(WHITE_PAWN, E2, E3);
        Move m1 = new Move(WHITE_PAWN, E2, E4);
        Move m2 = new Move(WHITE_PAWN, A2, A3);
        Move m3 = new Move(WHITE_KNIGHT, G1, F3);

        List<Move> moves = new ArrayList<>();
        moves.add(m0);
        moves.add(m1);
        moves.add(m2);
        moves.add(m3);

        assertEquals(1,MoveUtils.indexOf(moves, m1, 0));
        assertEquals(1,MoveUtils.indexOf(moves, m1, 1));
        assertEquals(-1,MoveUtils.indexOf(moves,m1,2));
        assertEquals(-1,MoveUtils.indexOf(moves, new Move(WHITE_PAWN, C2, C4), 0));
    }

    @Test
    public void testIndexOfArray() {
        Move m0 = new Move(WHITE_PAWN, E2, E3);
        Move m1 = new Move(WHITE_PAWN, E2, E4);
        Move m2 = new Move(WHITE_PAWN, A2, A3);
        Move m3 = new Move(WHITE_KNIGHT, G1, F3);

        Move[] moves = new Move[] { m0,m1,m2,m3 };

        assertEquals(1,MoveUtils.indexOf(moves, m1, 0));
        assertEquals(1,MoveUtils.indexOf(moves, m1, 1));
        assertEquals(-1,MoveUtils.indexOf(moves,m1,2));
        assertEquals(-1,MoveUtils.indexOf(moves, new Move(WHITE_PAWN, C2, C4), 0));
    }

}
