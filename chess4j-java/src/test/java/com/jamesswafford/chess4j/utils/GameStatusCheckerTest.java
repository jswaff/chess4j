package com.jamesswafford.chess4j.utils;

import org.junit.Test;

import com.jamesswafford.chess4j.board.Board;

import static org.junit.Assert.*;

import static com.jamesswafford.chess4j.utils.GameStatus.*;

public class GameStatusCheckerTest {

    @Test
    public void testInitialPosInProgress() {
        Board b = Board.INSTANCE;
        b.resetBoard();
        GameStatus gs = GameStatusChecker.getGameStatus(b);
        assertEquals(INPROGRESS, gs);
    }

    @Test
    public void testGetCheckmateStatus_FoolsMate() {
        Board b = Board.INSTANCE;
        b.setPos("rnb1kbnr/pppp1ppp/8/4p3/6Pq/5P2/PPPPP2P/RNBQKBNR w KQkq -");
        GameStatus gs = GameStatusChecker.getGameStatus(b);
        assertEquals(CHECKMATED, gs);
    }

    @Test
    public void testGetCheckmateStatus_ByrneFischer() {
        Board b = Board.INSTANCE;
        b.setPos("1Q6/5pk1/2p3p1/1p2N2p/1b5P/1bn5/2r3P1/2K5 w - -");
        GameStatus gs = GameStatusChecker.getGameStatus(b);
        assertEquals(CHECKMATED, gs);
    }

    @Test
    public void testGetCheckmateStatus_SimpleMate() {
        Board b = Board.INSTANCE;
        b.setPos("3k2R1/8/3K4/8/8/8/8/8 b - -");
        GameStatus gs = GameStatusChecker.getGameStatus(b);
        assertEquals(CHECKMATED, gs);
    }

    @Test
    public void testGetStalemateStatus() {
        Board b = Board.INSTANCE;
        b.setPos("8/8/8/6K1/8/1Q6/p7/k7 b - -");
        GameStatus gs = GameStatusChecker.getGameStatus(b);
        assertEquals(STALEMATED, gs);
    }

    @Test
    public void testGetStalemateStatus_BurnPilsbury() {
        Board b = Board.INSTANCE;
        b.setPos("5k2/5P2/5K2/8/8/8/8/8 b - -");
        GameStatus gs = GameStatusChecker.getGameStatus(b);
        assertEquals(STALEMATED, gs);
    }


    @Test
    public void testGetStalemateStatus2() {
        Board b = Board.INSTANCE;
        b.setPos("kb5R/8/1K6/8/8/8/8/8 b - - ");
        GameStatus gs = GameStatusChecker.getGameStatus(b);
        assertEquals(STALEMATED, gs);
    }

    @Test
    public void testGetStalemateStatus3() {
        Board b = Board.INSTANCE;
        b.setPos("8/8/8/8/8/2K5/1R6/k7 b - -");
        GameStatus gs = GameStatusChecker.getGameStatus(b);
        assertEquals(STALEMATED, gs);
    }

}
