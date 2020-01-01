package com.jamesswafford.chess4j.search;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.jamesswafford.chess4j.Constants;
import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.movegen.MoveGen;
import com.jamesswafford.chess4j.eval.Eval;
import com.jamesswafford.chess4j.hash.TTHolder;
import com.jamesswafford.chess4j.hash.TranspositionTableEntryType;

import static org.junit.Assert.*;

import static com.jamesswafford.chess4j.eval.EvalMaterial.*;
import static com.jamesswafford.chess4j.pieces.Pawn.*;
import static com.jamesswafford.chess4j.pieces.Knight.*;
import static com.jamesswafford.chess4j.board.squares.Square.*;

public class SearchTest {

    @Before
    public void setUp() {
        TTHolder.clearAllTables();
    }

    @Test
    public void testMateIn1() {
        Board b = Board.INSTANCE;
        b.setPos("4k3/8/3Q4/2B5/8/8/1K6/8 w - -");

        Search.abortSearch = false;
        SearchStats searchStats = new SearchStats();
        Search.startTime = System.currentTimeMillis();
        Search.stopTime = Search.startTime + 10000;

        int score = Search.search(new ArrayList<>(), -Constants.INFINITY, Constants.INFINITY,
                b, 2, searchStats,false);
        assertEquals(Constants.CHECKMATE-1, score);
    }

    @Test
    public void testMateIn1b() {
        Board b = Board.INSTANCE;
        b.setPos("4K3/8/8/3n2q1/8/8/3k4/8 b - -");

        Search.abortSearch = false;
        SearchStats searchStats = new SearchStats();
        Search.startTime = System.currentTimeMillis();
        Search.stopTime = Search.startTime + 10000;

        int score = Search.search(new ArrayList<>(), -Constants.INFINITY, Constants.INFINITY,
                b, 2,searchStats,false);
        assertEquals(Constants.CHECKMATE-1, score);
    }

    @Test
    public void testMateIn2() {
        Board b = Board.INSTANCE;
        b.setPos("r1bq2r1/b4pk1/p1pp1p2/1p2pP2/1P2P1PB/3P4/1PPQ2P1/R3K2R w - -");

        Search.abortSearch = false;
        SearchStats searchStats = new SearchStats();
        Search.startTime = System.currentTimeMillis();
        Search.stopTime = Search.startTime + 30000;

        int score = Search.search(new ArrayList<>(), -Constants.INFINITY, Constants.INFINITY,
                b, 4, searchStats,false);
        assertEquals(Constants.CHECKMATE-3, score);
    }

    @Test
    public void testMateIn3() {
        Board b = Board.INSTANCE;

        Search.abortSearch = false;
        b.setPos("r5rk/5p1p/5R2/4B3/8/8/7P/7K w - -");
        SearchStats searchStats = new SearchStats();
        Search.startTime = System.currentTimeMillis();
        Search.stopTime = Search.startTime + 10000;

        int score = Search.search(new ArrayList<>(), -Constants.INFINITY, Constants.INFINITY,
                b, 6, searchStats,false);
        assertEquals(Constants.CHECKMATE-5, score);
    }

    @Test
    public void testStaleMate() {
        Board b = Board.INSTANCE;
        b.setPos("8/6p1/5p2/5k1K/7P/8/8/8 w - -");

        Search.abortSearch = false;
        SearchStats searchStats = new SearchStats();
        List<Move> pv = new ArrayList<>();
        Search.startTime = System.currentTimeMillis();
        Search.stopTime = Search.startTime + 10000;

        int score = Search.search(pv, -Constants.INFINITY, Constants.INFINITY,
                b, 1, searchStats,false);
        assertEquals(0, score);
        assertEquals(0, pv.size());
    }

    @Test
    public void testSearchLastPVFirst() {
        Board b = Board.INSTANCE;
        b.resetBoard();

        Search.abortSearch = false;

        // create an artificial PV and ensure it is searched first.
        Move c2c4 = new Move(WHITE_PAWN, C2, C4);
        Move b7b5 = new Move(BLACK_PAWN, B7, B5);
        Move c4b5 = new Move(WHITE_PAWN, C4, B5, BLACK_PAWN);
        Move g8h6 = new Move(BLACK_KNIGHT, G8, H6);
        List<Move> lastPV = new ArrayList<>();
        lastPV.add(c2c4);
        lastPV.add(b7b5);
        lastPV.add(c4b5);
        lastPV.add(g8h6);
        SearchStats searchStats = new SearchStats();
        Search.startTime = System.currentTimeMillis();
        Search.stopTime = Search.startTime + 10000;
        searchStats.setLastPV(lastPV);

        List<Move> pv = new ArrayList<>();
        Search.search(pv, -Constants.INFINITY, Constants.INFINITY,
                b, 5, searchStats,false);

        assertEquals(5, searchStats.getFirstLine().size());
        assertEquals(lastPV, searchStats.getFirstLine().subList(0, 4));
    }

    @Ignore // PVS has changed node count... need to revisit
    @Test
    public void testAbortSearch() {
        Board b = Board.INSTANCE;
        b.resetBoard();

        Search.abortSearch = true;

        SearchStats searchStats = new SearchStats();
        Search.startTime = System.currentTimeMillis();
        Search.stopTime = Search.startTime + 10000;
        Search.search(new ArrayList<>(), -Constants.INFINITY, Constants.INFINITY,
                b, 1, searchStats,false);
        assertEquals(21, searchStats.getNodes());

        searchStats = new SearchStats();
        Search.startTime = System.currentTimeMillis();
        Search.stopTime = Search.startTime + 10000;
        Search.search(new ArrayList<>(), -Constants.INFINITY, Constants.INFINITY,
                b, 3, searchStats,false);
        assertEquals(4, searchStats.getNodes()); // 1 + 3 (down left side of the tree)

        Search.abortSearch = false;
    }

    @Test
    public void testTranspositionTable() {
        Board b = Board.INSTANCE;
        b.resetBoard();

        Search.abortSearch = false;

        SearchStats searchStats = new SearchStats();
        Search.startTime = System.currentTimeMillis();
        Search.stopTime = Search.startTime + 10000;
        // make a (ridiculous) entry up.  the program would never play 1. a4, UNLESS of course it would be a piece up!
        // make 1. ... a5 a rook up for white, everything else a queen up, and verify PV is 1. a4 a5
        Move a2a4 = new Move(WHITE_PAWN, A2, A4);
        b.applyMove(a2a4);
        List<Move> mvs = MoveGen.genLegalMoves(b);
        Move a7a5 = new Move(BLACK_PAWN, A7, A5);
        assertTrue(mvs.contains(a7a5));
        for (Move mv : mvs) {
            b.applyMove(mv);
            int score=900;
            if (mv.equals(a7a5)) {
                score=500;
            }
            TTHolder.getAlwaysReplaceTransTable().store(b.getZobristKey(),
                    TranspositionTableEntryType.EXACT_MATCH,
                    score, 1, mv);
            b.undoMove();
        }
        b.undoMove();

        List<Move> pv = new ArrayList<>();
        Search.search(pv, -Constants.INFINITY, Constants.INFINITY, b, 3, searchStats,false);
        assertEquals(2, pv.size());
        assertEquals(a2a4, pv.get(0));
        assertEquals(a7a5, pv.get(1));
    }

    @Test
    public void testQSearchDoesNotExpandNodesFromInitialPos() {
        Board b = Board.INSTANCE;
        b.resetBoard();

        Search.abortSearch = false;
        SearchStats searchStats = new SearchStats();
        Search.startTime = System.currentTimeMillis();
        Search.stopTime = Search.startTime + 10000;

        Search.quiescenceSearch(-Constants.INFINITY,Constants.INFINITY,false,b,searchStats);
        assertEquals(0,searchStats.getNodes());
        assertEquals(0, searchStats.getQNodes());
    }

    @Test
    public void testQSearchStandpatRaisesAlpha() {
        Board b = Board.INSTANCE;
        b.resetBoard();

        int score = Eval.eval(b);

        Search.abortSearch = false;
        SearchStats searchStats = new SearchStats();
        Search.startTime = System.currentTimeMillis();
        Search.stopTime = Search.startTime + 10000;

        int qScore = Search.quiescenceSearch(-Constants.INFINITY, Constants.INFINITY,false,b,searchStats);
        assertEquals(score, qScore);
    }

    @Test
    public void testQSearchStandpatDoesNotRaiseAlpha() {
        Board b = Board.INSTANCE;
        b.resetBoard();

        Search.abortSearch = false;
        SearchStats searchStats = new SearchStats();
        Search.startTime = System.currentTimeMillis();
        Search.stopTime = Search.startTime + 10000;

        int qScore = Search.quiescenceSearch(QUEEN_VAL,Constants.INFINITY,false, b,searchStats);
        assertEquals(QUEEN_VAL,qScore);
    }

    @Test
    public void testQSearchDoesExpandJustCaptures() {
        Board b = Board.INSTANCE;
        b.setPos("7k/8/8/3b4/8/8/6P1/K7 b - -");

        List<Move> moves = MoveGen.genPseudoLegalMoves(b, true,false);
        assertEquals(1, moves.size());

        Search.abortSearch = false;

        SearchStats searchStats = new SearchStats();
        Search.startTime = System.currentTimeMillis();
        Search.stopTime = Search.startTime + 10000;

        Search.quiescenceSearch(-Constants.INFINITY,Constants.INFINITY,false,b,searchStats);

        assertEquals(0,searchStats.getNodes());
        assertEquals(1, searchStats.getQNodes());
    }

    @Test
    public void testQSearchDoesExpandPromotions() {
        Board b = Board.INSTANCE;
        b.setPos("8/P6k/8/8/8/8/7K/8 w - -");

        List<Move> moves = MoveGen.genPseudoLegalMoves(b, true,false);
        assertEquals(4, moves.size()); // just promotions

        Search.abortSearch = false;

        SearchStats searchStats = new SearchStats();
        Search.startTime = System.currentTimeMillis();
        Search.stopTime = Search.startTime + 10000;

        Search.quiescenceSearch(-Constants.INFINITY,Constants.INFINITY,false,b,searchStats);

        assertEquals(0,searchStats.getNodes());
        assertEquals(4, searchStats.getQNodes());
    }

    @Test
    public void testQSearchBetaCutoff() {
        Board b = Board.INSTANCE;

        // this pos would be very good for white if searched
        b.setPos("8/P6k/8/8/8/8/7K/8 w - -");

        List<Move> moves = MoveGen.genPseudoLegalMoves(b, true,false);
        assertEquals(4, moves.size()); // just promotions

        int score = Eval.eval(b);
        assertTrue(Math.abs(score) < QUEEN_VAL);

        Search.abortSearch = false;
        SearchStats searchStats = new SearchStats();
        Search.startTime = System.currentTimeMillis();
        Search.stopTime = Search.startTime + 10000;

        Search.quiescenceSearch(-Constants.INFINITY,-QUEEN_VAL,false,b,searchStats);

        assertEquals(0,searchStats.getNodes());
        assertEquals(0, searchStats.getQNodes());
    }
}
