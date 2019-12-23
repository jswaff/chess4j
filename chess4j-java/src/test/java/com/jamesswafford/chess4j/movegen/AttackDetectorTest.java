package com.jamesswafford.chess4j.movegen;

import com.jamesswafford.chess4j.board.Bitboard;
import com.jamesswafford.chess4j.board.Board;
import org.junit.Test;

import com.jamesswafford.chess4j.Color;
import com.jamesswafford.chess4j.board.squares.Square;
import com.jamesswafford.chess4j.io.FenParser;

import static org.junit.Assert.*;

import static com.jamesswafford.chess4j.board.squares.File.*;
import static com.jamesswafford.chess4j.board.squares.Rank.*;

public class AttackDetectorTest {

    @Test
    public void testGetAttackers1() throws Exception {
        Board board = Board.INSTANCE;
        FenParser.setPos(board, "1k1r3q/1ppn3p/p4b2/4p3/8/P2N2P1/1PP1R1BP/2K1Q3 w - -");
        long attackers = AttackDetector.getAttackers(board,Square.valueOf(FILE_E, RANK_5), Color.WHITE);
        assertTrue((attackers & Bitboard.squares[Square.valueOf(FILE_D, RANK_3).value()]) != 0);
        assertTrue((attackers & Bitboard.squares[Square.valueOf(FILE_E, RANK_2).value()]) != 0);
        assertEquals(2, Long.bitCount(attackers));
    }

    @Test
    public void testGetAttackers2() throws Exception {
        Board board = Board.INSTANCE;
        FenParser.setPos(board, "1k1r3q/1ppn3p/p4b2/4p3/8/P2N2P1/1PP1R1BP/2K1Q3 w - -");

        long attackers = AttackDetector.getAttackers(board, Square.valueOf(FILE_E, RANK_5), Color.BLACK);

        assertTrue((attackers & Bitboard.squares[Square.valueOf(FILE_D, RANK_7).value()]) != 0);
        assertTrue((attackers & Bitboard.squares[Square.valueOf(FILE_F, RANK_6).value()]) != 0);
        assertEquals(2,Long.bitCount(attackers));
    }

    @Test
    public void testGetAttackers3() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "8/p1k5/1p6/8/3b4/1Q6/8/7K w - -");

        long attackers = AttackDetector.getAttackers(b, Square.valueOf(FILE_B, RANK_6), Color.WHITE);

        assertTrue((attackers & Bitboard.squares[Square.valueOf(FILE_B, RANK_3).value()]) != 0);
        assertEquals(1, Long.bitCount(attackers));
    }

    @Test
    public void testGetAttackers4() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "8/p1k5/1p6/8/3b4/1Q6/8/7K w - -");

        long attackers = AttackDetector.getAttackers(b, Square.valueOf(FILE_B, RANK_6), Color.BLACK);

        assertTrue((attackers & Bitboard.squares[Square.valueOf(FILE_A, RANK_7).value()]) != 0);
        assertTrue((attackers & Bitboard.squares[Square.valueOf(FILE_C, RANK_7).value()]) != 0);
        assertTrue((attackers & Bitboard.squares[Square.valueOf(FILE_D, RANK_4).value()]) != 0);
        assertEquals(3, Long.bitCount(attackers));
    }
}
