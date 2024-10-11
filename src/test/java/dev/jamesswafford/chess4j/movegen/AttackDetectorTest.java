package dev.jamesswafford.chess4j.movegen;

import dev.jamesswafford.chess4j.board.Bitboard;
import dev.jamesswafford.chess4j.board.Board;
import org.junit.Test;

import dev.jamesswafford.chess4j.board.Color;

import static org.junit.Assert.*;

import static dev.jamesswafford.chess4j.board.squares.Square.*;

public class AttackDetectorTest {

    @Test
    public void testGetAttackers1() {
        Board board = new Board("1k1r3q/1ppn3p/p4b2/4p3/8/P2N2P1/1PP1R1BP/2K1Q3 w - -");
        long attackers = AttackDetector.getAttackers(board, E5, Color.WHITE);
        assertTrue((attackers & Bitboard.squares[D3.value()]) != 0);
        assertTrue((attackers & Bitboard.squares[E2.value()]) != 0);
        assertEquals(2, Long.bitCount(attackers));
    }

    @Test
    public void testGetAttackers2() {
        Board board = new Board("1k1r3q/1ppn3p/p4b2/4p3/8/P2N2P1/1PP1R1BP/2K1Q3 w - -");

        long attackers = AttackDetector.getAttackers(board, E5, Color.BLACK);

        assertTrue((attackers & Bitboard.squares[D7.value()]) != 0);
        assertTrue((attackers & Bitboard.squares[F6.value()]) != 0);
        assertEquals(2,Long.bitCount(attackers));
    }

    @Test
    public void testGetAttackers3() {
        Board board = new Board("8/p1k5/1p6/8/3b4/1Q6/8/7K w - -");

        long attackers = AttackDetector.getAttackers(board, B6, Color.WHITE);

        assertTrue((attackers & Bitboard.squares[B3.value()]) != 0);
        assertEquals(1, Long.bitCount(attackers));
    }

    @Test
    public void testGetAttackers4() {
        Board board = new Board("8/p1k5/1p6/8/3b4/1Q6/8/7K w - -");

        long attackers = AttackDetector.getAttackers(board, B6, Color.BLACK);

        assertTrue((attackers & Bitboard.squares[A7.value()]) != 0);
        assertTrue((attackers & Bitboard.squares[C7.value()]) != 0);
        assertTrue((attackers & Bitboard.squares[D4.value()]) != 0);
        assertEquals(3, Long.bitCount(attackers));
    }

    @Test
    public void testGetAttackers5() {
        Board board = new Board("r4rk1/2p2pp1/P2b1n2/8/3P4/1B1PN2q/1P2QP1p/R1B2RKb w - -");
        long attackers = AttackDetector.getAttackers(board, H1, Color.WHITE);

        assertEquals(Bitboard.squares[G1.value()], attackers);

        attackers = AttackDetector.getAttackers(board, H1, Color.BLACK);
        assertEquals(0L, attackers);
    }
}
