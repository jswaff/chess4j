package com.jamesswafford.chess4j.board;

import junit.framework.Assert;

import org.junit.Test;

import com.jamesswafford.chess4j.Color;
import com.jamesswafford.chess4j.board.squares.File;
import com.jamesswafford.chess4j.board.squares.Rank;
import com.jamesswafford.chess4j.board.squares.Square;
import com.jamesswafford.chess4j.io.FenParser;

public class AttackDetectorTest {

    @Test
    public void testGetAttackers1() throws Exception {
        Board board = Board.INSTANCE;
        FenParser.setPos(board, "1k1r3q/1ppn3p/p4b2/4p3/8/P2N2P1/1PP1R1BP/2K1Q3 w - -");
        long attackers = AttackDetector.getAttackers(board,Square.valueOf(File.FILE_E, Rank.RANK_5), Color.WHITE);
        Assert.assertTrue((attackers & Bitboard.squares[Square.valueOf(File.FILE_D,Rank.RANK_3).value()]) != 0);
        Assert.assertTrue((attackers & Bitboard.squares[Square.valueOf(File.FILE_E,Rank.RANK_2).value()]) != 0);
        Assert.assertEquals(2,Long.bitCount(attackers));
    }

    @Test
    public void testGetAttackers2() throws Exception {
        Board board = Board.INSTANCE;
        FenParser.setPos(board, "1k1r3q/1ppn3p/p4b2/4p3/8/P2N2P1/1PP1R1BP/2K1Q3 w - -");

        long attackers = AttackDetector.getAttackers(board,
                Square.valueOf(File.FILE_E, Rank.RANK_5), Color.BLACK);

        Assert.assertTrue((attackers & Bitboard.squares[Square.valueOf(File.FILE_D,Rank.RANK_7).value()]) != 0);
        Assert.assertTrue((attackers & Bitboard.squares[Square.valueOf(File.FILE_F,Rank.RANK_6).value()]) != 0);
        Assert.assertEquals(2,Long.bitCount(attackers));
    }

    @Test
    public void testGetAttackers3() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "8/p1k5/1p6/8/3b4/1Q6/8/7K w - -");

        long attackers = AttackDetector.getAttackers(b,
                Square.valueOf(File.FILE_B, Rank.RANK_6), Color.WHITE);

        Assert.assertTrue((attackers & Bitboard.squares[Square.valueOf(File.FILE_B,Rank.RANK_3).value()]) != 0);
        Assert.assertEquals(1,Long.bitCount(attackers));
    }

    @Test
    public void testGetAttackers4() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "8/p1k5/1p6/8/3b4/1Q6/8/7K w - -");

        long attackers = AttackDetector.getAttackers(b,
                Square.valueOf(File.FILE_B, Rank.RANK_6), Color.BLACK);

        Assert.assertTrue((attackers & Bitboard.squares[Square.valueOf(File.FILE_A,Rank.RANK_7).value()]) != 0);
        Assert.assertTrue((attackers & Bitboard.squares[Square.valueOf(File.FILE_C,Rank.RANK_7).value()]) != 0);
        Assert.assertTrue((attackers & Bitboard.squares[Square.valueOf(File.FILE_D,Rank.RANK_4).value()]) != 0);
        Assert.assertEquals(3,Long.bitCount(attackers));
    }
}
