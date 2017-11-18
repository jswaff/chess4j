package com.jamesswafford.chess4j.utils;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.board.squares.File;
import com.jamesswafford.chess4j.board.squares.Rank;
import com.jamesswafford.chess4j.board.squares.Square;
import com.jamesswafford.chess4j.pieces.Knight;
import com.jamesswafford.chess4j.pieces.Pawn;
import com.jamesswafford.chess4j.utils.MoveUtils;


public class MoveUtilsTest {

    @Test
    public void testMoveToTopList() {
        Move m1 = new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_E, Rank.RANK_2),Square.valueOf(File.FILE_E, Rank.RANK_3));
        Move m2 = new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_E, Rank.RANK_2),Square.valueOf(File.FILE_E, Rank.RANK_4));
        Move m3 = new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_A, Rank.RANK_2),Square.valueOf(File.FILE_A, Rank.RANK_3));
        Move m4 = new Move(Knight.WHITE_KNIGHT,Square.valueOf(File.FILE_G, Rank.RANK_1),Square.valueOf(File.FILE_F, Rank.RANK_3));

        List<Move> moves = new ArrayList<Move>();
        moves.add(m1);
        moves.add(m2);
        moves.add(m3);
        moves.add(m4);

        Assert.assertEquals(m1, moves.get(0));
        MoveUtils.putMoveAtTop(moves, m3);
        Assert.assertEquals(4, moves.size());
        Assert.assertEquals(m3, moves.get(0));
    }

    @Test
    public void testMoveToTopArray() {
        Move m1 = new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_E, Rank.RANK_2),Square.valueOf(File.FILE_E, Rank.RANK_3));
        Move m2 = new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_E, Rank.RANK_2),Square.valueOf(File.FILE_E, Rank.RANK_4));
        Move m3 = new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_A, Rank.RANK_2),Square.valueOf(File.FILE_A, Rank.RANK_3));
        Move m4 = new Move(Knight.WHITE_KNIGHT,Square.valueOf(File.FILE_G, Rank.RANK_1),Square.valueOf(File.FILE_F, Rank.RANK_3));

        Move[] moves = new Move[] { m1,m2,m3,m4 };

        Assert.assertEquals(m1, moves[0]);
        MoveUtils.putMoveAtTop(moves, m3);
        Assert.assertEquals(4, moves.length);
        Assert.assertEquals(m3, moves[0]);
    }

    @Test
    public void testSwapList() {
        Move m0 = new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_E, Rank.RANK_2),Square.valueOf(File.FILE_E, Rank.RANK_3));
        Move m1 = new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_E, Rank.RANK_2),Square.valueOf(File.FILE_E, Rank.RANK_4));
        Move m2 = new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_A, Rank.RANK_2),Square.valueOf(File.FILE_A, Rank.RANK_3));
        Move m3 = new Move(Knight.WHITE_KNIGHT,Square.valueOf(File.FILE_G, Rank.RANK_1),Square.valueOf(File.FILE_F, Rank.RANK_3));

        List<Move> moves = new ArrayList<Move>();
        moves.add(m0);
        moves.add(m1);
        moves.add(m2);
        moves.add(m3);

        Assert.assertEquals(m1, moves.get(1));
        MoveUtils.swap(moves, 1, 3);
        Assert.assertEquals(m0, moves.get(0));
        Assert.assertEquals(m3, moves.get(1));
        Assert.assertEquals(m2, moves.get(2));
        Assert.assertEquals(m1, moves.get(3));

        // swap same index
        MoveUtils.swap(moves, 2,2);
        Assert.assertEquals(m0, moves.get(0));
        Assert.assertEquals(m3, moves.get(1));
        Assert.assertEquals(m2, moves.get(2));
        Assert.assertEquals(m1, moves.get(3));
    }

    @Test
    public void testSwapArray() {
        Move m0 = new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_E, Rank.RANK_2),Square.valueOf(File.FILE_E, Rank.RANK_3));
        Move m1 = new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_E, Rank.RANK_2),Square.valueOf(File.FILE_E, Rank.RANK_4));
        Move m2 = new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_A, Rank.RANK_2),Square.valueOf(File.FILE_A, Rank.RANK_3));
        Move m3 = new Move(Knight.WHITE_KNIGHT,Square.valueOf(File.FILE_G, Rank.RANK_1),Square.valueOf(File.FILE_F, Rank.RANK_3));

        Move[] moves = new Move[] { m0,m1,m2,m3 };

        Assert.assertEquals(m1, moves[1]);
        MoveUtils.swap(moves, 1, 3);
        Assert.assertEquals(m0, moves[0]);
        Assert.assertEquals(m3, moves[1]);
        Assert.assertEquals(m2, moves[2]);
        Assert.assertEquals(m1, moves[3]);

        // swap same index
        MoveUtils.swap(moves, 2,2);
        Assert.assertEquals(m0, moves[0]);
        Assert.assertEquals(m3, moves[1]);
        Assert.assertEquals(m2, moves[2]);
        Assert.assertEquals(m1, moves[3]);
    }

    @Test
    public void testIndexOfList() {
        Move m0 = new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_E, Rank.RANK_2),Square.valueOf(File.FILE_E, Rank.RANK_3));
        Move m1 = new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_E, Rank.RANK_2),Square.valueOf(File.FILE_E, Rank.RANK_4));
        Move m2 = new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_A, Rank.RANK_2),Square.valueOf(File.FILE_A, Rank.RANK_3));
        Move m3 = new Move(Knight.WHITE_KNIGHT,Square.valueOf(File.FILE_G, Rank.RANK_1),Square.valueOf(File.FILE_F, Rank.RANK_3));

        List<Move> moves = new ArrayList<Move>();
        moves.add(m0);
        moves.add(m1);
        moves.add(m2);
        moves.add(m3);

        Assert.assertEquals(1,MoveUtils.indexOf(moves, m1, 0));
        Assert.assertEquals(1,MoveUtils.indexOf(moves, m1, 1));
        Assert.assertEquals(-1,MoveUtils.indexOf(moves,m1,2));
        Assert.assertEquals(-1,MoveUtils.indexOf(moves, new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_C,Rank.RANK_2),Square.valueOf(File.FILE_C,Rank.RANK_4)), 0));
    }

    @Test
    public void testIndexOfArray() {
        Move m0 = new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_E, Rank.RANK_2),Square.valueOf(File.FILE_E, Rank.RANK_3));
        Move m1 = new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_E, Rank.RANK_2),Square.valueOf(File.FILE_E, Rank.RANK_4));
        Move m2 = new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_A, Rank.RANK_2),Square.valueOf(File.FILE_A, Rank.RANK_3));
        Move m3 = new Move(Knight.WHITE_KNIGHT,Square.valueOf(File.FILE_G, Rank.RANK_1),Square.valueOf(File.FILE_F, Rank.RANK_3));

        Move[] moves = new Move[] { m0,m1,m2,m3 };

        Assert.assertEquals(1,MoveUtils.indexOf(moves, m1, 0));
        Assert.assertEquals(1,MoveUtils.indexOf(moves, m1, 1));
        Assert.assertEquals(-1,MoveUtils.indexOf(moves,m1,2));
        Assert.assertEquals(-1,MoveUtils.indexOf(moves, new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_C,Rank.RANK_2),Square.valueOf(File.FILE_C,Rank.RANK_4)), 0));
    }


}
