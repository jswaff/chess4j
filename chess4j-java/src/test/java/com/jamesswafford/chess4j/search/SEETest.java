package com.jamesswafford.chess4j.search;

import java.util.List;

import org.junit.Test;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.movegen.MoveGen;
import com.jamesswafford.chess4j.eval.Eval;
import com.jamesswafford.chess4j.io.MoveParser;

import static org.junit.Assert.*;

import static com.jamesswafford.chess4j.pieces.Pawn.*;
import static com.jamesswafford.chess4j.pieces.Knight.*;
import static com.jamesswafford.chess4j.pieces.Rook.*;
import static com.jamesswafford.chess4j.pieces.Queen.*;
import static com.jamesswafford.chess4j.board.squares.Square.*;


public class SEETest {

    @Test
    public void testQueenTakesUndefendedPawn() {
        Board b = Board.INSTANCE;
        b.setPos("7k/8/1p6/8/8/1Q6/8/7K w - -");

        Move m = new Move(WHITE_QUEEN, B3, B6, BLACK_PAWN);
        b.applyMove(m);

        int score = SEE.see(b,m);
        assertEquals(Eval.PAWN_VAL, score);
    }

    @Test
    public void testQueenTakesDefendedPawn() {
        Board b = Board.INSTANCE;
        b.setPos("7k/p7/1p6/8/8/1Q6/8/7K w - -");

        Move m = new Move(WHITE_QUEEN, B3, B6, BLACK_PAWN);
        b.applyMove(m);

        int score = SEE.see(b,m);
        assertEquals(Eval.PAWN_VAL - Eval.QUEEN_VAL, score);
    }

    @Test
    public void testRookTakesUndefendedPawn() {
        Board b= Board.INSTANCE;
        b.setPos("1k1r4/1pp4p/p7/4p3/8/P5P1/1PP4P/2K1R3 w - -");

        Move m = new Move(WHITE_ROOK, E1, E5, BLACK_PAWN);

        List<Move> moves = MoveGen.genLegalMoves(b);
        assertTrue(moves.contains(m));
        b.applyMove(m);

        int score = SEE.see(b, m);
        assertEquals(Eval.PAWN_VAL, score);
    }

    @Test
    public void testWithXrays() {
        Board b = Board.INSTANCE;
        b.setPos("1k1r3q/1ppn3p/p4b2/4p3/8/P2N2P1/1PP1R1BP/2K1Q3 w - -");

        Move m = new Move(WHITE_KNIGHT, D3, E5, BLACK_PAWN);

        List<Move> moves = MoveGen.genLegalMoves(b);
        assertTrue(moves.contains(m));
        b.applyMove(m);

        int score = SEE.see(b, m);
        assertEquals(Eval.PAWN_VAL - Eval.KNIGHT_VAL, score);
    }

    @Test
    public void testRookXRays() {
        Board b = Board.INSTANCE;
        b.setPos("3kr3/8/4p3/8/8/4R3/4R3/4K3 w - -");

        Move m = new Move(WHITE_ROOK, E3, E6 ,BLACK_PAWN);

        List<Move> moves = MoveGen.genLegalMoves(b);
        assertTrue(moves.contains(m));
        b.applyMove(m);

        int score = SEE.see(b, m);
        assertEquals(Eval.PAWN_VAL, score);
    }

    @Test
    public void testKnightTakesDefendedPawnAsWhite() throws Exception {
        Board b = Board.INSTANCE;
        b.setPos("k7/8/5n2/3p4/8/2N2B2/8/K7 w - -");

        List<Move> moves = MoveGen.genLegalMoves(b);
        MoveParser mp = new MoveParser();
        Move m = mp.parseMove("c3d5", b);
        assertTrue(moves.contains(m));
        b.applyMove(m);

        int score = SEE.see(b, m);
        assertEquals(Eval.PAWN_VAL, score);
    }

    @Test
    public void testKnightTakesDefendedPawnAsBlack() throws Exception {
        Board b = Board.INSTANCE;
        b.setPos("K7/8/5N2/3P4/8/2n2b2/8/k7 b - -");

        List<Move> moves = MoveGen.genLegalMoves(b);
        MoveParser mp = new MoveParser();
        Move m = mp.parseMove("c3d5", b);
        assertTrue(moves.contains(m));
        b.applyMove(m);

        int score = SEE.see(b, m);
        assertEquals(Eval.PAWN_VAL, score);
    }

    @Test
    public void testCrazyRooks() throws Exception {
        Board b = Board.INSTANCE;
        b.setPos("2K5/8/8/3pRrRr/8/8/8/2k5 w - -");

        List<Move> moves = MoveGen.genLegalMoves(b);
        MoveParser mp = new MoveParser();
        Move m = mp.parseMove("e5d5", b);
        assertTrue(moves.contains(m));
        b.applyMove(m);

        int score = SEE.see(b, m);
        assertEquals(Eval.PAWN_VAL - Eval.ROOK_VAL, score);
    }

    @Test
    public void testCrazyRooks2() throws Exception {
        Board b = Board.INSTANCE;
        b.setPos("2K5/8/8/3pRrR1/8/8/8/2k5 w - -");

        List<Move> moves = MoveGen.genLegalMoves(b);
        MoveParser mp = new MoveParser();
        Move m = mp.parseMove("e5d5", b);
        assertTrue(moves.contains(m));
        b.applyMove(m);

        int score = SEE.see(b, m);
        assertEquals(Eval.PAWN_VAL, score);
    }

    @Test
    public void testKnightTakesDefendedPawn() throws Exception {
        Board b = Board.INSTANCE;
        b.setPos("1K1k4/8/5n2/3p4/8/1BN5/8/8 w - -");

        List<Move> moves = MoveGen.genLegalMoves(b);
        MoveParser mp = new MoveParser();
        Move m = mp.parseMove("c3d5", b);
        assertTrue(moves.contains(m));
        b.applyMove(m);

        int score = SEE.see(b, m);
        assertEquals(Eval.PAWN_VAL, score);
    }

    @Test
    public void testBishopTakesDefendedPawn() throws Exception {
        Board b = Board.INSTANCE;
        b.setPos("1K1k4/8/5n2/3p4/8/1BN5/8/8 w - -");

        List<Move> moves = MoveGen.genLegalMoves(b);
        MoveParser mp = new MoveParser();
        Move m = mp.parseMove("b3d5", b);
        assertTrue(moves.contains(m));
        b.applyMove(m);

        int score = SEE.see(b, m);
        assertEquals(Eval.PAWN_VAL-Eval.BISHOP_VAL+Eval.KNIGHT_VAL, score);
    }

    @Test
    public void testKnightTakesDefendedPawnWithCrazyBishops() throws Exception {
        Board b = Board.INSTANCE;
        b.setPos("1K1k4/8/5n2/3p4/8/2N2B2/6b1/7b w - -");

        List<Move> moves = MoveGen.genLegalMoves(b);
        MoveParser mp = new MoveParser();
        Move m = mp.parseMove("c3d5", b);
        assertTrue(moves.contains(m));
        b.applyMove(m);

        int score = SEE.see(b, m);
        assertEquals(Eval.PAWN_VAL - Eval.KNIGHT_VAL, score);
    }


    // these tests from Arasan... though some expected scores are different
    @Test
    public void testSEE() throws Exception {
        testCaseSEE("4R3/2r3p1/5bk1/1p1r3p/p2PR1P1/P1BK1P2/1P6/8 b - -","hxg4",0);
        testCaseSEE("4R3/2r3p1/5bk1/1p1r1p1p/p2PR1P1/P1BK1P2/1P6/8 b - -","hxg4",0);
        testCaseSEE("4r1k1/5pp1/nbp4p/1p2p2q/1P2P1b1/1BP2N1P/1B2QPPK/3R4 b - -","Bxf3",Eval.KNIGHT_VAL-Eval.BISHOP_VAL);
        testCaseSEE("2r1r1k1/pp1bppbp/3p1np1/q3P3/2P2P2/1P2B3/P1N1B1PP/2RQ1RK1 b - -","dxe5",Eval.PAWN_VAL);
        testCaseSEE("7r/5qpk/p1Qp1b1p/3r3n/BB3p2/5p2/P1P2P2/4RK1R w - -","Re8",0);
        testCaseSEE("7R/4bP2/8/8/1q6/3K4/5p2/4k3 w - -","f8=R",10004);
        testCaseSEE("8/4kp2/2npp3/1Nn5/1p2PQP1/7q/1PP1B3/4KR1r b - -","Rxf1+",0);
        testCaseSEE("8/4kp2/2npp3/1Nn5/1p2P1P1/7q/1PP1B3/4KR1r b - -","Rxf1+", 0);
        testCaseSEE("2r2r1k/6bp/p7/2q2p1Q/3PpP2/1B6/P5PP/2RR3K b - -","Qxc1",2*Eval.ROOK_VAL-Eval.QUEEN_VAL);
        testCaseSEE("r2qk1nr/pp2ppbp/2b3p1/2p1p3/8/2N2N2/PPPP1PPP/R1BQR1K1 w kq -","Nxe5",Eval.PAWN_VAL);
        testCaseSEE("6r1/4kq2/b2p1p2/p1pPb3/p1P2B1Q/2P4P/2B1R1P1/6K1 w - -","Bxe5",0);
        testCaseSEE("3q2nk/pb1r1p2/np6/3P2Pp/2p1P3/2R4B/PQ3P1P/3R2K1 w - h6","gxh6",0);
        testCaseSEE("3q2nk/pb1r1p2/np6/3P2Pp/2p1P3/2R1B2B/PQ3P1P/3R2K1 w - h6","gxh6",Eval.PAWN_VAL);
        testCaseSEE("2r4r/1P4pk/p2p1b1p/7n/BB3p2/2R2p2/P1P2P2/4RK2 w - -","Rxc8",Eval.ROOK_VAL);

        // Arasan says +rook for this, but RxR then BxR then PxB ==> Bishop Val
        // Note the last pawn capture is also a promotion so that could be added
        testCaseSEE("2r5/1P4pk/p2p1b1p/5b1n/BB3p2/2R2p2/P1P2P2/4RK2 w - -","Rxc8",Eval.BISHOP_VAL);

        testCaseSEE("2r4k/2r4p/p7/2b2p1b/4pP2/1BR5/P1R3PP/2Q4K w - -","Rxc5",Eval.BISHOP_VAL);
        testCaseSEE("8/pp6/2pkp3/4bp2/2R3b1/2P5/PP4B1/1K6 w - -","Bxc6",Eval.PAWN_VAL-Eval.BISHOP_VAL);
        testCaseSEE("4q3/1p1pr1k1/1B2rp2/6p1/p3PP2/P3R1P1/1P2R1K1/4Q3 b - -","Rxe4",Eval.PAWN_VAL-Eval.ROOK_VAL);
        testCaseSEE("4q3/1p1pr1kb/1B2rp2/6p1/p3PP2/P3R1P1/1P2R1K1/4Q3 b - -","Bxe4",Eval.PAWN_VAL);

        // not captures
        //testCaseSEE("6rr/6pk/p1Qp1b1p/2n5/1B3p2/5p2/P1P2P2/4RK1R w - -","Re8",-Eval.ROOK_VAL);
        //testCaseSEE("7r/5qpk/2Qp1b1p/1N1r3n/BB3p2/5p2/P1P2P2/4RK1R w - -","Re8",-Eval.ROOK_VAL);

        // promotion that doesn't capture
        //testCaseSEE("6RR/4bP2/8/8/5r2/3K4/5p2/4k3 w - -","f8=Q",Eval.BISHOP_VAL-Eval.PAWN_VAL);
        //testCaseSEE("6RR/4bP2/8/8/5r2/3K4/5p2/4k3 w - -","f8=N",Eval.KNIGHT_VAL-Eval.PAWN_VAL);
        //testCaseSEE("7R/5P2/8/8/8/3K2r1/5p2/4k3 w - -","f8=Q",Eval.QUEEN_VAL-Eval.PAWN_VAL);
        //testCaseSEE("7R/5P2/8/8/8/3K2r1/5p2/4k3 w - -","f8=B",Eval.BISHOP_VAL-Eval.PAWN_VAL);

    }

    private void testCaseSEE(String fen,String mv,int score) throws Exception {
        Board b = Board.INSTANCE;
        b.setPos(fen);

        MoveParser mp = new MoveParser();
        Move m = mp.parseMove(mv, b);

        assertTrue(MoveGen.genLegalMoves(b).contains(m));
        b.applyMove(m);

        int myScore = SEE.see(b, m);
        assertEquals(score, myScore);
    }

}
