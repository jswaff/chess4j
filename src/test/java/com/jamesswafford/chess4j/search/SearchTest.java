package com.jamesswafford.chess4j.search;

import java.util.ArrayList;
import java.util.List;

import com.jamesswafford.chess4j.hash.TranspositionTable;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.jamesswafford.chess4j.Constants;
import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.board.MoveGen;
import com.jamesswafford.chess4j.board.squares.File;
import com.jamesswafford.chess4j.board.squares.Rank;
import com.jamesswafford.chess4j.board.squares.Square;
import com.jamesswafford.chess4j.eval.Eval;
import com.jamesswafford.chess4j.hash.TTHolder;
import com.jamesswafford.chess4j.hash.TranspositionTableEntryType;
import com.jamesswafford.chess4j.io.FenParser;
import com.jamesswafford.chess4j.pieces.Knight;
import com.jamesswafford.chess4j.pieces.Pawn;
import com.jamesswafford.chess4j.search.Search;
import com.jamesswafford.chess4j.search.SearchStats;


public class SearchTest {

    @Before
    public void setUp() {
        TTHolder.getTransTable().clear();
        TTHolder.getPawnTransTable().clear();
    }

    @Test
    public void testMateIn1() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "4k3/8/3Q4/2B5/8/8/1K6/8 w - -");

        Search.abortSearch = false;
        SearchStats searchStats = new SearchStats();
        Search.startTime = System.currentTimeMillis();
        Search.stopTime = Search.startTime + 10000;

        int score = Search.search(new ArrayList<>(), -Constants.INFINITY, Constants.INFINITY,
                b, 2, searchStats,false);
        Assert.assertEquals(Constants.CHECKMATE-1, score);
    }

    @Test
    public void testMateIn1b() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "4K3/8/8/3n2q1/8/8/3k4/8 b - -");

        Search.abortSearch = false;
        SearchStats searchStats = new SearchStats();
        Search.startTime = System.currentTimeMillis();
        Search.stopTime = Search.startTime + 10000;

        int score = Search.search(new ArrayList<>(), -Constants.INFINITY, Constants.INFINITY,
                b, 2,searchStats,false);
        Assert.assertEquals(Constants.CHECKMATE-1, score);
    }

    @Test
    public void testMateIn2() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "r1bq2r1/b4pk1/p1pp1p2/1p2pP2/1P2P1PB/3P4/1PPQ2P1/R3K2R w - -");

        Search.abortSearch = false;
        SearchStats searchStats = new SearchStats();
        Search.startTime = System.currentTimeMillis();
        Search.stopTime = Search.startTime + 30000;

        int score = Search.search(new ArrayList<Move>(), -Constants.INFINITY, Constants.INFINITY,
                b, 4, searchStats,false);
        Assert.assertEquals(Constants.CHECKMATE-3, score);
    }

    @Test
    public void testMateIn3() throws Exception {
        Board b = Board.INSTANCE;

        Search.abortSearch = false;
        FenParser.setPos(b, "r5rk/5p1p/5R2/4B3/8/8/7P/7K w - -");
        SearchStats searchStats = new SearchStats();
        Search.startTime = System.currentTimeMillis();
        Search.stopTime = Search.startTime + 10000;

        int score = Search.search(new ArrayList<>(), -Constants.INFINITY, Constants.INFINITY,
                b, 6, searchStats,false);
        Assert.assertEquals(Constants.CHECKMATE-5, score);
    }

    @Test
    public void testStaleMate() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "8/6p1/5p2/5k1K/7P/8/8/8 w - -");

        Search.abortSearch = false;
        SearchStats searchStats = new SearchStats();
        List<Move> pv = new ArrayList<>();
        Search.startTime = System.currentTimeMillis();
        Search.stopTime = Search.startTime + 10000;

        int score = Search.search(pv, -Constants.INFINITY, Constants.INFINITY,
                b, 1, searchStats,false);
        Assert.assertEquals(0, score);
        Assert.assertEquals(0, pv.size());
    }

    @Test
    public void testSearchLastPVFirst() throws Exception {
        Board b = Board.INSTANCE;
        b.resetBoard();

        Search.abortSearch = false;

        // create an artificial PV and ensure it is searched first.
        Move c2c4 = new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_C, Rank.RANK_2),Square.valueOf(File.FILE_C, Rank.RANK_4));
        Move b7b5 = new Move(Pawn.BLACK_PAWN,Square.valueOf(File.FILE_B, Rank.RANK_7),Square.valueOf(File.FILE_B, Rank.RANK_5));
        Move c4b5 = new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_C, Rank.RANK_4),Square.valueOf(File.FILE_B, Rank.RANK_5),Pawn.BLACK_PAWN);
        Move g8h6 = new Move(Knight.BLACK_KNIGHT,Square.valueOf(File.FILE_G, Rank.RANK_8),Square.valueOf(File.FILE_H, Rank.RANK_6));
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

        Assert.assertEquals(5, searchStats.getFirstLine().size());
        Assert.assertEquals(lastPV, searchStats.getFirstLine().subList(0, 4));
    }

    @Ignore // PVS has changed node count... need to revisit
    @Test
    public void testAbortSearch() throws Exception {
        Board b = Board.INSTANCE;
        b.resetBoard();

        Search.abortSearch = true;

        SearchStats searchStats = new SearchStats();
        Search.startTime = System.currentTimeMillis();
        Search.stopTime = Search.startTime + 10000;
        Search.search(new ArrayList<>(), -Constants.INFINITY, Constants.INFINITY,
                b, 1, searchStats,false);
        Assert.assertEquals(21, searchStats.getNodes());

        searchStats = new SearchStats();
        Search.startTime = System.currentTimeMillis();
        Search.stopTime = Search.startTime + 10000;
        Search.search(new ArrayList<Move>(), -Constants.INFINITY, Constants.INFINITY,
                b, 3, searchStats,false);
        Assert.assertEquals(4, searchStats.getNodes()); // 1 + 3 (down left side of the tree)

        Search.abortSearch = false;
    }

    @Test
    public void testTranspositionTable() throws Exception {
        Board b = Board.INSTANCE;
        b.resetBoard();

        Search.abortSearch = false;

        SearchStats searchStats = new SearchStats();
        Search.startTime = System.currentTimeMillis();
        Search.stopTime = Search.startTime + 10000;
        // make a (ridiculous) entry up.  the program would never play 1. a4, UNLESS of course it would be a piece up!
        // make 1. ... a5 a rook up for white, everything else a queen up, and verify PV is 1. a4 a5
        Move a2a4 = new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_A,Rank.RANK_2),Square.valueOf(File.FILE_A, Rank.RANK_4));
        b.applyMove(a2a4);
        List<Move> mvs = MoveGen.genLegalMoves(b);
        Move a7a5 = new Move(Pawn.BLACK_PAWN,Square.valueOf(File.FILE_A,Rank.RANK_7),Square.valueOf(File.FILE_A, Rank.RANK_5));
        Assert.assertTrue(mvs.contains(a7a5));
        for (Move mv : mvs) {
            b.applyMove(mv);
            int score=900;
            if (mv.equals(a7a5)) {
                score=500;
            }
            TTHolder.getTransTable().store(
                    TranspositionTableEntryType.EXACT_MATCH,
                    b.getZobristKey(),
                    score, 1, mv);
            b.undoLastMove();
        }
        b.undoLastMove();

        List<Move> pv = new ArrayList<>();
        Search.search(pv, -Constants.INFINITY, Constants.INFINITY, b, 3, searchStats,false);
        Assert.assertEquals(2, pv.size());
        Assert.assertEquals(a2a4, pv.get(0));
        Assert.assertEquals(a7a5, pv.get(1));
    }

    @Test
    public void testDraw50() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/1NBQKBNR w Kkq - 0 1");
        TTHolder.getTransTable().clear();

        Search.startTime = System.currentTimeMillis();
        Search.stopTime = Search.startTime + 10000;
        int score = Search.search(new ArrayList<>(), -Constants.INFINITY, Constants.INFINITY,
                b, 2, new SearchStats(),false);
        Assert.assertTrue(score != 0);

        // up to 99 (half) moves ... the extra comes from the root search
        b.setFiftyCounter(98);
        b.setMoveCounter(98);
        TTHolder.getTransTable().clear();
        Search.startTime = System.currentTimeMillis();
        Search.stopTime = Search.startTime + 10000;
        score = Search.search(new ArrayList<>(), -Constants.INFINITY, Constants.INFINITY,
                b, 2, new SearchStats(),false);
        Assert.assertTrue(score != 0);

        // trigger 50 move rule
        b.setFiftyCounter(99);
        b.setMoveCounter(99);
        TTHolder.getTransTable().clear();
        Search.startTime = System.currentTimeMillis();
        Search.stopTime = Search.startTime + 10000;
        score = Search.search(new ArrayList<>(), -Constants.INFINITY, Constants.INFINITY,
                b, 2, new SearchStats(),false);
        Assert.assertTrue(score == 0);
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
        Assert.assertEquals(0,searchStats.getNodes());
        Assert.assertEquals(0, searchStats.getQNodes());
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
        Assert.assertEquals(score, qScore);
    }

    @Test
    public void testQSearchStandpatDoesNotRaiseAlpha() {
        Board b = Board.INSTANCE;
        b.resetBoard();

        Search.abortSearch = false;
        SearchStats searchStats = new SearchStats();
        Search.startTime = System.currentTimeMillis();
        Search.stopTime = Search.startTime + 10000;

        int qScore = Search.quiescenceSearch(Eval.QUEEN_VAL,Constants.INFINITY,false, b,searchStats);
        Assert.assertEquals(Eval.QUEEN_VAL,qScore);
    }

    @Test
    public void testQSearchDoesExpandJustCaptures() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "7k/8/8/3b4/8/8/6P1/K7 b - -");

        List<Move> moves = MoveGen.genPseudoLegalMoves(b, true,false);
        Assert.assertEquals(1, moves.size());

        Search.abortSearch = false;

        SearchStats searchStats = new SearchStats();
        Search.startTime = System.currentTimeMillis();
        Search.stopTime = Search.startTime + 10000;

        Search.quiescenceSearch(-Constants.INFINITY,Constants.INFINITY,false,b,searchStats);

        Assert.assertEquals(0,searchStats.getNodes());
        Assert.assertEquals(1, searchStats.getQNodes());
    }

    @Test
    public void testQSearchDoesExpandPromotions() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b,"8/P6k/8/8/8/8/7K/8 w - -");

        List<Move> moves = MoveGen.genPseudoLegalMoves(b, true,false);
        Assert.assertEquals(4, moves.size()); // just promotions

        Search.abortSearch = false;

        SearchStats searchStats = new SearchStats();
        Search.startTime = System.currentTimeMillis();
        Search.stopTime = Search.startTime + 10000;

        Search.quiescenceSearch(-Constants.INFINITY,Constants.INFINITY,false,b,searchStats);

        Assert.assertEquals(0,searchStats.getNodes());
        Assert.assertEquals(4, searchStats.getQNodes());
    }

    @Test
    public void testQSearchBetaCutoff() throws Exception {
        Board b = Board.INSTANCE;

        // this pos would be very good for white if searched
        FenParser.setPos(b,"8/P6k/8/8/8/8/7K/8 w - -");

        List<Move> moves = MoveGen.genPseudoLegalMoves(b, true,false);
        Assert.assertEquals(4, moves.size()); // just promotions

        int score = Eval.eval(b);
        Assert.assertTrue(Math.abs(score) < Eval.QUEEN_VAL);

        Search.abortSearch = false;
        SearchStats searchStats = new SearchStats();
        Search.startTime = System.currentTimeMillis();
        Search.stopTime = Search.startTime + 10000;

        Search.quiescenceSearch(-Constants.INFINITY,-Eval.QUEEN_VAL,false,b,searchStats);

        Assert.assertEquals(0,searchStats.getNodes());
        Assert.assertEquals(0, searchStats.getQNodes());
    }
}
