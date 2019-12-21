package com.jamesswafford.chess4j;

import junit.framework.Assert;

import org.junit.Test;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.io.FenParser;
import com.jamesswafford.chess4j.utils.Perft;

public class PerfTest {


    // these positions came from the Chess Programming Wiki:
    // https://chessprogramming.wikispaces.com/Perft+Results
    @Test
    public void perftTest1() throws Exception {
        testCase("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",4,197281);
    }

    @Test
    public void perftTest2() throws Exception {
        testCase("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1",3,97862);
    }

    @Test
    public void perftTest3() throws Exception {
        testCase("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - 0 1",6,11030083L);
    }

    @Test
    public void perftTest4() throws Exception {
        testCase("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1",5,15833292L);
    }

    @Test
    public void perftTest5() throws Exception {
        testCase("rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8",4,2103487);
    }

    @Test
    public void perftTest6() throws Exception {
        testCase("r4rk1/1pp1qppp/p1np1n2/2b1p1B1/2B1P1b1/P1NP1N2/1PP1QPPP/R4RK1 w - - 0 10",4,3894594L);
    }

    //// These tests taken from Arasan:
    // https://github.com/jdart1/arasan-chess/blob/master/src/unit.cpp

    @Test
    public void testIllegalEP1() throws Exception {
        testCase("3k4/3p4/8/K1P4r/8/8/8/8 b - - 0 1", 6, 1134888);
        testCase("8/8/8/8/k1p4R/8/3P4/3K4 w - - 0 1", 6, 1134888);
    }

    @Test
    public void testIllegalEP2() throws Exception {
        testCase("8/8/4k3/8/2p5/8/B2P2K1/8 w - - 0 1", 6, 1015133);
        testCase("8/b2p2k1/8/2P5/8/4K3/8/8 b - - 0 1", 6, 1015133);
    }

    @Test
    public void testEPCaptureChecksOpponent() throws Exception {
        testCase("8/8/1k6/2b5/2pP4/8/5K2/8 b - d3 0 1", 6, 1440467);
        testCase("8/5k2/8/2Pp4/2B5/1K6/8/8 w - d6 0 1", 6, 1440467);
    }

    @Test
    public void testShortCastleGivesCheck() throws Exception {
        testCase("5k2/8/8/8/8/8/8/4K2R w K - 0 1", 6, 661072);
        testCase("4k2r/8/8/8/8/8/8/5K2 b k - 0 1", 6, 661072);
    }

    @Test
    public void testLongCastleGivesCheck() throws Exception {
        testCase("3k4/8/8/8/8/8/8/R3K3 w Q - 0 1", 6, 803711);
        testCase("r3k3/8/8/8/8/8/8/3K4 b q - 0 1", 6, 803711);
    }

    @Test
    public void testLosingCastlingRights() throws Exception {
        testCase("r3k2r/1b4bq/8/8/8/8/7B/R3K2R w KQkq - 0 1", 4, 1274206);
        testCase("r3k2r/7b/8/8/8/8/1B4BQ/R3K2R b KQkq - 0 1", 4, 1274206);
    }

    @Test
    public void testCastlingPrevented() throws Exception {
        testCase("r3k2r/8/3Q4/8/8/5q2/8/R3K2R b KQkq - 0 1", 4, 1720476);
        testCase("r3k2r/8/5Q2/8/8/3q4/8/R3K2R w KQkq - 0 1", 4, 1720476);
    }

    @Test
    public void testPromoteOutofCheck() throws Exception {
        testCase("2K2r2/4P3/8/8/8/8/8/3k4 w - - 0 1", 6, 3821001);
        testCase("3K4/8/8/8/8/8/4p3/2k2R2 b - - 0 1", 6, 3821001);
    }

    @Test
    public void testDiscoveredCheck() throws Exception {
        testCase("8/8/1P2K3/8/2n5/1q6/8/5k2 b - - 0 1", 5, 1004658);
        testCase("5K2/8/1Q6/2N5/8/1p2k3/8/8 w - - 0 1", 5, 1004658);
    }

    @Test
    public void testPromoteToGiveCheck() throws Exception {
        testCase("4k3/1P6/8/8/8/8/K7/8 w - - 0 1", 6, 217342);
        testCase("8/k7/8/8/8/8/1p6/4K3 b - - 0 1", 6, 217342);
    }

    @Test
    public void testUnpromoteToCheck() throws Exception {
        testCase("8/P1k5/K7/8/8/8/8/8 w - - 0 1", 6, 92683);
        testCase("8/8/8/8/8/k7/p1K5/8 b - - 0 1", 6, 92683);
    }

    @Test
    public void testSelfStalemate() throws Exception {
        testCase("K1k5/8/P7/8/8/8/8/8 w - - 0 1", 6, 2217);
        testCase("8/8/8/8/8/p7/8/k1K5 b - - 0 1", 6, 2217);
    }

    @Test
    public void testStaleMateCheckMate() throws Exception {
        testCase("8/k1P5/8/1K6/8/8/8/8 w - - 0 1", 7, 567584);
        testCase("8/8/8/8/1k6/8/K1p5/8 b - - 0 1", 7, 567584);
    }

    @Test
    public void testDoubleCheck() throws Exception {
        testCase("8/8/2k5/5q2/5n2/8/5K2/8 b - - 0 1", 4, 23527);
        testCase("8/5k2/8/5N2/5Q2/2K5/8/8 w - - 0 1", 4, 23527);
    }

    private void testCase(String fen, int depth, long nodes) throws Exception {
        FenParser.setPos(Board.INSTANCE, fen);
        //DrawBoard.drawBoard(Board.INSTANCE);
        long n = Perft.perft(Board.INSTANCE, depth);
        Assert.assertEquals(nodes, n);
    }

}
