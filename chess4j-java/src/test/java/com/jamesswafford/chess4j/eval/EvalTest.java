package com.jamesswafford.chess4j.eval;

import org.junit.Test;

import com.jamesswafford.chess4j.board.Board;

import static org.junit.Assert.*;

import static com.jamesswafford.chess4j.eval.Eval.*;

/**
 * Test the evaluator.  We only do a few basic sanity checks here.  Most of the
 * functionality is tested in other, smaller scoped tests.  Our goal here is
 * to make sure all the pieces are wired together correctly.
 */
public class EvalTest {

    Board board = new Board();
    EvalTermsVector etv = new EvalTermsVector();

    @Test
    public void testStartPosIs0() {
        board.resetBoard();
        int eval = eval(etv, board);
        assertEquals(0, eval);
    }

    @Test
    public void testSymmetry() {
        testCaseSymmetry("7r/R6p/2K4P/5k1P/2p4n/5p2/8/8 w - - 0 1");
        testCaseSymmetry("8/k3Nr2/2rR4/1P1n4/6p1/1K6/8/6n1 w - - 0 1");

        // these positions from Arasan
        String[] positions = new String[] {
            "8/4K3/8/1NR5/8/4k1r1/8/8 w - -",
            "8/4K3/8/1N6/6p1/4k2p/8/8 w - -",
            "8/4K3/8/1r6/6B1/4k2N/8/8 w - -",
            "3b4/1n3n2/1pk3Np/p7/P4P1p/1P6/5BK1/3R4 b - -",
            "8/3r1ppk/8/P6P/3n4/2K5/R2B4/8 b - -",
            "1rb1r1k1/2q2pb1/pp1p4/2n1pPPQ/Pn1BP3/1NN4R/1PP4P/R5K1 b - -",
            "6k1/1b4p1/5p1p/pq3P2/1p1BP3/1P2QR1P/P1r3PK/8 w - -",
            "8/5pk1/7p/3p1R2/p1p3P1/2P2K1P/1P1r4/8 w - -",
            "6k1/p3pp2/6p1/7P/R7/b1q2P2/B1P1K2P/7R b - -",
            "r7/1b4k1/pp1np1p1/3pq1NN/7P/4P3/PP4P1/1Q3RK1 b - -",
            "4b3/2p4p/pp1bk3/2p3p1/2P5/PPB2PP1/7P/3K1N2 w - -",
            "r1bqr1k1/ppp2ppp/3p4/4n3/2PN4/P1Q1P3/1PB2PPP/R4RK1 b - -",
            "r4rk1/1ppqbppp/p2p1n2/8/1n1PP3/1Q3N2/PP1N1PPP/R1B1R1K1 b - -",
            "r6k/1p4bp/1p1n1pp1/1B6/8/P4NP1/1P3P1P/2R3K1 w - -",
            "r1b2r1k/pp3n1p/2p1p3/3Pnppq/3PP3/1P1N1PP1/P5BP/R1Q2RK1 w - -",
            "2kr3r/1bpnqp2/1p2p3/p2p3p/P1PPBPp1/2P1P1P1/2QN2P1/1R2K2R w K -",
            "8/1R6/3k4/2p5/2p1B3/5K2/8/8 w - -",
            "1BR2rk1/pP1nbpp1/B2P2p1/8/8/8/1P4P1/3n2K1 b - -",
            "r1b1k2r/1p1n1pp1/p6p/2p5/4Nb2/5NP1/PPP2P1P/1K1R1B1R b kq -",
            "r1b2rk1/1p1n1pp1/p6p/2p5/4Nb2/3R1NP1/PPP2P1P/1K1R1B2 b - -",
            "1kr5/1p1b2R1/p3p2Q/2bp3P/8/P1PB1P2/1P1K1P2/R6q b - -",
            "rb3rk1/1p1RRpp1/p6p/r1p5/4Nb2/5NP1/PPP2P1P/1K3B2 b - -",
            "5rk1/1pqn2pp/4pn2/p7/2P5/4PP2/1B2BP1P/3Q1RK1 w - -",
            "3k1q2/p3p1p1/1p1nQ3/3P4/P2P4/B2P4/6KP/8 b - -",
            "6k1/4R1P1/5P2/5K1p/7r/8/8/8 w - -",
            "1n1q1rk1/4ppbp/3p1np1/1PpP4/4P3/2N2N2/3B1PPP/Q3K2R b K -",
            "3q1rk1/4ppbp/1n1p1np1/1PpP4/2N1P3/5N2/3B1PPP/Q3K2R b K -",
            "3q1rk1/4ppbp/1n1p1np1/1P1P4/4P3/2p1BN2/2N2PPP/Q3K2R b K -",
            "N5r1/pQ6/3b1nkp/2q5/2Pp1p2/4nP2/PP1B2PP/1RR3K1 b - -",
            "8/2kn2q1/B1p2pP1/P1P2p1p/3P2bP/3P1B2/1K1P1Q2/8 b - -",
            "5nk1/3b1r2/2p1p3/1pPpP1qp/1P1Q4/6P1/4BN1P/R5K1 w - - 0 1"
        };

        for (String position : positions) {
            testCaseSymmetry(position);
        }
    }

    @Test
    public void testKK() {
        String fen = "k7/8/8/8/8/8/8/K7 w - -";
        Board board = new Board(fen);
        assertEquals(0, eval(etv, board));
        testCaseSymmetry(fen);
    }

    @Test
    public void testKNK() {
        String fen = "kn6/8/8/8/8/8/8/K7 w - -";
        Board board = new Board(fen);
        assertEquals(0, eval(etv, board));
        testCaseSymmetry(fen);
    }

    @Test
    public void testKNKP() {
        String fen = "kn6/8/8/8/8/8/P7/K7 w - -";
        Board board = new Board(fen);
        int eval = eval(etv, board);
        assertTrue(eval > -50 && eval < 0);
        testCaseSymmetry(fen);
    }

    @Test
    public void testKNKN() {
        String fen = "k7/n7/8/8/8/8/8/KN6 w - -";
        Board board = new Board(fen);
        assertEquals(0, eval(etv, board));
        testCaseSymmetry(fen);
    }

    @Test
    public void testKNNK() {
        String fen = "knn5/8/8/8/8/8/8/K7 w - -";
        Board board = new Board(fen);
        assertEquals(0, eval(etv, board));
        testCaseSymmetry(fen);
    }

    @Test
    public void testKNKB() {
        String fen = "kb6/8/8/8/8/8/8/KN6 w - -";
        Board board = new Board(fen);
        assertEquals(0, eval(etv, board));
        testCaseSymmetry(fen);
    }

    @Test
    public void testKBK() {
        String fen = "kb6/8/8/8/8/8/8/K7 w - -";
        Board board = new Board(fen);
        assertEquals(0, eval(etv, board));
        testCaseSymmetry(fen);
    }

    @Test
    public void testKBKP() {
        String fen = "kb6/8/8/8/8/8/P7/K7 w - -";
        Board board = new Board(fen);
        int eval = eval(etv, board);
        assertTrue(eval > -50 && eval < 0);
        testCaseSymmetry(fen);
    }

    @Test
    public void testKBKB() {
        String fen = "k7/b7/8/8/8/8/8/KB6 w - -";
        Board board = new Board(fen);
        assertEquals(0, eval(etv, board));
        testCaseSymmetry(fen);
    }

    private void testCaseSymmetry(String fen) {
        board.setPos(fen);
        int eval = eval(etv, board);
        board.flipVertical();
        int eval2 = eval(etv, board);
        assertEquals(eval, eval2);
    }

}
