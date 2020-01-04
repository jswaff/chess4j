package com.jamesswafford.chess4j.utils;

import org.junit.Test;

import com.jamesswafford.chess4j.board.Board;

import java.util.ArrayList;

import static org.junit.Assert.*;

import static com.jamesswafford.chess4j.utils.GameStatus.*;

public class GameStatusCheckerTest {

    @Test
    public void testInitialPosInProgress() {
        Board board = new Board();
        GameStatus gameStatus = GameStatusChecker.getGameStatus(board, new ArrayList<>());
        assertEquals(INPROGRESS, gameStatus);
    }

    @Test
    public void testGetCheckmateStatus_FoolsMate() {
        Board board = new Board("rnb1kbnr/pppp1ppp/8/4p3/6Pq/5P2/PPPPP2P/RNBQKBNR w KQkq -");
        GameStatus gameStatus = GameStatusChecker.getGameStatus(board, new ArrayList<>());
        assertEquals(CHECKMATED, gameStatus);
    }

    @Test
    public void testGetCheckmateStatus_ByrneFischer() {
        Board board = new Board("1Q6/5pk1/2p3p1/1p2N2p/1b5P/1bn5/2r3P1/2K5 w - -");
        GameStatus gameStatus = GameStatusChecker.getGameStatus(board, new ArrayList<>());
        assertEquals(CHECKMATED, gameStatus);
    }

    @Test
    public void testGetCheckmateStatus_SimpleMate() {
        Board board = new Board("3k2R1/8/3K4/8/8/8/8/8 b - -");
        GameStatus gameStatus = GameStatusChecker.getGameStatus(board, new ArrayList<>());
        assertEquals(CHECKMATED, gameStatus);
    }

    @Test
    public void testGetStalemateStatus() {
        Board board = new Board("8/8/8/6K1/8/1Q6/p7/k7 b - -");
        GameStatus gameStatus = GameStatusChecker.getGameStatus(board, new ArrayList<>());
        assertEquals(STALEMATED, gameStatus);
    }

    @Test
    public void testGetStalemateStatus_BurnPilsbury() {
        Board board = new Board("5k2/5P2/5K2/8/8/8/8/8 b - -");
        GameStatus gameStatus = GameStatusChecker.getGameStatus(board, new ArrayList<>());
        assertEquals(STALEMATED, gameStatus);
    }


    @Test
    public void testGetStalemateStatus2() {
        Board board = new Board("kb5R/8/1K6/8/8/8/8/8 b - - ");
        GameStatus gameStatus = GameStatusChecker.getGameStatus(board, new ArrayList<>());
        assertEquals(STALEMATED, gameStatus);
    }

    @Test
    public void testGetStalemateStatus3() {
        Board board = new Board("8/8/8/8/8/2K5/1R6/k7 b - -");
        GameStatus gameStatus = GameStatusChecker.getGameStatus(board, new ArrayList<>());
        assertEquals(STALEMATED, gameStatus);
    }

}
