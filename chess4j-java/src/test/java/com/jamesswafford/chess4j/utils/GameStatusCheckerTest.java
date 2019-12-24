package com.jamesswafford.chess4j.utils;

import org.junit.Test;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.io.FenParser;

import static org.junit.Assert.*;

import static com.jamesswafford.chess4j.pieces.Pawn.*;
import static com.jamesswafford.chess4j.pieces.Knight.*;
import static com.jamesswafford.chess4j.board.squares.Square.*;
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
    public void testGetCheckmateStatus_FoolsMate() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "rnb1kbnr/pppp1ppp/8/4p3/6Pq/5P2/PPPPP2P/RNBQKBNR w KQkq -");
        GameStatus gs = GameStatusChecker.getGameStatus(b);
        assertEquals(CHECKMATED, gs);
    }

    @Test
    public void testGetCheckmateStatus_ByrneFischer() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "1Q6/5pk1/2p3p1/1p2N2p/1b5P/1bn5/2r3P1/2K5 w - -");
        GameStatus gs = GameStatusChecker.getGameStatus(b);
        assertEquals(CHECKMATED, gs);
    }

    @Test
    public void testGetCheckmateStatus_SimpleMate() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "3k2R1/8/3K4/8/8/8/8/8 b - -");
        GameStatus gs = GameStatusChecker.getGameStatus(b);
        assertEquals(CHECKMATED, gs);
    }

    @Test
    public void testGetStalemateStatus() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "8/8/8/6K1/8/1Q6/p7/k7 b - -");
        GameStatus gs = GameStatusChecker.getGameStatus(b);
        assertEquals(STALEMATED, gs);
    }

    @Test
    public void testGetStalemateStatus_BurnPilsbury() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "5k2/5P2/5K2/8/8/8/8/8 b - -");
        GameStatus gs = GameStatusChecker.getGameStatus(b);
        assertEquals(STALEMATED, gs);
    }


    @Test
    public void testGetStalemateStatus2() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "kb5R/8/1K6/8/8/8/8/8 b - - ");
        GameStatus gs = GameStatusChecker.getGameStatus(b);
        assertEquals(STALEMATED, gs);
    }

    @Test
    public void testGetStalemateStatus3() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "8/8/8/8/8/2K5/1R6/k7 b - -");
        GameStatus gs = GameStatusChecker.getGameStatus(b);
        assertEquals(STALEMATED, gs);
    }

    @Test
    public void testGetDraw50Status() {
        Board b = Board.INSTANCE;
        b.resetBoard();
        GameStatus gs = GameStatusChecker.getGameStatus(b);
        assertEquals(INPROGRESS, gs);

        // move knights out and back in 25 times.  that will take the 50 move counter to
        // 25 x 4 = 100.  Only on the last move should the draw be claimed.
        for (int i=0; i<25; i++) {

            b.applyMove(new Move(WHITE_KNIGHT, G1, F3));
            b.applyMove(new Move(BLACK_KNIGHT, G8, F6));
            b.applyMove(new Move(WHITE_KNIGHT, F3, G1));
            b.applyMove(new Move(BLACK_KNIGHT, F6, G8));
        }

        // fudging a little...if we called the checker directly it would give us a draw-by-rep
        assertEquals(100, b.getFiftyCounter());

        // move a pawn and it's reset
        b.applyMove(new Move(WHITE_PAWN, E2, E3));
        assertEquals(0, b.getFiftyCounter());

        assertEquals(INPROGRESS, GameStatusChecker.getGameStatus(b));
    }

    @Test
    public void testNoMaterialStatus() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "kb6/8/1K6/8/8/8/8/8 b - - ");
        GameStatus gs = GameStatusChecker.getGameStatus(b);
        assertEquals(DRAW_MATERIAL, gs);
    }

    @Test
    public void testNoMaterialStatusOnePawn() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "4k3/8/8/8/8/8/P7/4K3 w - -");
        GameStatus gs = GameStatusChecker.getGameStatus(b);
        assertNotEquals(DRAW_MATERIAL, gs);
    }

    @Test
    public void testNoMaterialStatusOneKnight() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "4k3/8/8/8/8/8/n7/4K3 w - -");
        GameStatus gs = GameStatusChecker.getGameStatus(b);
        assertEquals(DRAW_MATERIAL, gs);
    }

    @Test
    public void testNoMaterialStatusOneBishop() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "4k3/8/8/8/8/8/B7/4K3 w - -");
        GameStatus gs = GameStatusChecker.getGameStatus(b);
        assertEquals(DRAW_MATERIAL, gs);
    }

    @Test
    public void testNoMaterialStatusOneRook() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "4k3/8/8/8/8/8/r7/4K3 b - -");
        GameStatus gs = GameStatusChecker.getGameStatus(b);
        assertNotEquals(DRAW_MATERIAL, gs);
    }

    @Test
    public void testNoMaterialStatusOneQueen() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "4k3/8/8/8/8/8/Q7/4K3 w - -");
        GameStatus gs = GameStatusChecker.getGameStatus(b);
        assertNotEquals(DRAW_MATERIAL, gs);
    }

    @Test
    public void testNoMaterialStatusJustKings() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "4k3/8/8/8/8/8/8/4K3 w - -");
        GameStatus gs = GameStatusChecker.getGameStatus(b);
        assertEquals(DRAW_MATERIAL, gs);
    }

    @Test
    public void testNoMaterialStatusTwoWhiteKnights() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "4k3/8/8/8/8/8/NN6/4K3 w - -");
        GameStatus gs = GameStatusChecker.getGameStatus(b);
        assertNotEquals(DRAW_MATERIAL, gs);
    }

    @Test
    public void testNoMaterialStatusTwoOpposingKnights() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "4k3/8/8/8/8/8/Nn6/4K3 b - -");
        GameStatus gs = GameStatusChecker.getGameStatus(b);
        assertNotEquals(DRAW_MATERIAL, gs);
    }

    @Test
    public void testNoMaterialStatusTwoBishopsDifferentColors() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "4k3/8/8/8/8/8/Bb6/4K3 w - -");
        GameStatus gs = GameStatusChecker.getGameStatus(b);
        assertNotEquals(DRAW_MATERIAL, gs);
    }

    @Test
    public void testNoMaterialStatusTwoBishopsSameColor() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "4k3/8/8/8/8/8/B1b5/4K3 b - -");
        GameStatus gs = GameStatusChecker.getGameStatus(b);
        assertEquals(DRAW_MATERIAL, gs);
    }

    @Test
    public void testNoMaterialStatusBishopVsKnight() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "4k3/8/8/8/8/8/B1n5/5K2 b - -");
        GameStatus gs = GameStatusChecker.getGameStatus(b);
        assertNotEquals(DRAW_MATERIAL, gs);
    }

    @Test
    public void testDrawByRep() {
        Board b = Board.INSTANCE;
        b.resetBoard();
        GameStatus gs = GameStatusChecker.getGameStatus(b);
        assertNotEquals(DRAW_REP, gs);

        b.applyMove(new Move(WHITE_PAWN, E2, E4));
        assertNotEquals(DRAW_REP, GameStatusChecker.getGameStatus(b));

        b.applyMove(new Move(BLACK_KNIGHT, G8, F6));
        assertNotEquals(DRAW_REP, GameStatusChecker.getGameStatus(b));

        b.applyMove(new Move(WHITE_KNIGHT, G1, F3));
        assertNotEquals(DRAW_REP, GameStatusChecker.getGameStatus(b));

        b.applyMove(new Move(BLACK_KNIGHT, F6, G8));
        assertNotEquals(DRAW_REP, GameStatusChecker.getGameStatus(b));

        b.applyMove(new Move(WHITE_KNIGHT, F3, G1));
        assertNotEquals(DRAW_REP, GameStatusChecker.getGameStatus(b)); // still 1 (first has ep square)

        b.applyMove(new Move(BLACK_KNIGHT, G8, F6));
        assertNotEquals(DRAW_REP, GameStatusChecker.getGameStatus(b)); // 2

        b.applyMove(new Move(WHITE_KNIGHT, G1, F3));
        assertNotEquals(DRAW_REP, GameStatusChecker.getGameStatus(b)); // 2

        b.applyMove(new Move(BLACK_KNIGHT, F6, G8));
        assertNotEquals(DRAW_REP, GameStatusChecker.getGameStatus(b)); // 2

        b.applyMove(new Move(WHITE_KNIGHT, F3, G1));
        assertNotEquals(DRAW_REP, GameStatusChecker.getGameStatus(b)); // 2

        b.applyMove(new Move(BLACK_KNIGHT, G8, F6));
        assertEquals(DRAW_REP, GameStatusChecker.getGameStatus(b)); // 3

        b.applyMove(new Move(WHITE_PAWN, D2, D4));
        assertNotEquals(DRAW_REP, GameStatusChecker.getGameStatus(b));
    }
}
