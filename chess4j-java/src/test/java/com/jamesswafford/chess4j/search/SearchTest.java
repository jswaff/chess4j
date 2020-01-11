package com.jamesswafford.chess4j.search;

import java.util.ArrayList;
import java.util.List;

import com.jamesswafford.chess4j.board.Undo;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.movegen.MoveGen;
import com.jamesswafford.chess4j.eval.Eval;
import com.jamesswafford.chess4j.hash.TTHolder;
import com.jamesswafford.chess4j.hash.TranspositionTableEntryType;

import static org.junit.Assert.*;

import static com.jamesswafford.chess4j.Constants.*;
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
    public void testSearchLastPVFirst() {
        Board board = new Board();

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
        Search.search(pv, -INFINITY, INFINITY,
                board, new ArrayList<>(), 5, searchStats,false);

        assertEquals(5, searchStats.getFirstLine().size());
        assertEquals(lastPV, searchStats.getFirstLine().subList(0, 4));
    }

    @Ignore // PVS has changed node count... need to revisit
    @Test
    public void testAbortSearch() {
        Board board = new Board();

        Search.abortSearch = true;

        SearchStats searchStats = new SearchStats();
        Search.startTime = System.currentTimeMillis();
        Search.stopTime = Search.startTime + 10000;
        Search.search(new ArrayList<>(), -INFINITY, INFINITY,
                board, new ArrayList<>(), 1, searchStats,false);
        assertEquals(21, searchStats.getNodes());

        searchStats = new SearchStats();
        Search.startTime = System.currentTimeMillis();
        Search.stopTime = Search.startTime + 10000;
        Search.search(new ArrayList<>(), -INFINITY, INFINITY,
                board, new ArrayList<>(), 3, searchStats,false);
        assertEquals(4, searchStats.getNodes()); // 1 + 3 (down left side of the tree)

        Search.abortSearch = false;
    }

    @Test
    public void testTranspositionTable() {
        Board board = new Board();

        Search.abortSearch = false;

        SearchStats searchStats = new SearchStats();
        Search.startTime = System.currentTimeMillis();
        Search.stopTime = Search.startTime + 10000;
        // make a (ridiculous) entry up.  the program would never play 1. a4, UNLESS of course it would be a piece up!
        // make 1. ... a5 a rook up for white, everything else a queen up, and verify PV is 1. a4 a5
        Move a2a4 = new Move(WHITE_PAWN, A2, A4);
        Undo undoa2a4 = board.applyMove(a2a4);
        List<Move> mvs = MoveGen.genLegalMoves(board);
        Move a7a5 = new Move(BLACK_PAWN, A7, A5);
        assertTrue(mvs.contains(a7a5));
        for (Move mv : mvs) {
            Undo u = board.applyMove(mv);
            int score=900;
            if (mv.equals(a7a5)) {
                score=500;
            }
            TTHolder.getAlwaysReplaceTransTable().store(board.getZobristKey(),
                    TranspositionTableEntryType.EXACT_MATCH,
                    score, 1, mv);
            board.undoMove(u);
        }
        board.undoMove(undoa2a4);

        List<Move> pv = new ArrayList<>();
        Search.search(pv, -INFINITY, INFINITY, board, new ArrayList<>(), 3, searchStats,false);
        assertEquals(2, pv.size());
        assertEquals(a2a4, pv.get(0));
        assertEquals(a7a5, pv.get(1));
    }

    @Test
    public void testQSearchDoesNotExpandNodesFromInitialPos() {
        Board board = new Board();

        Search.abortSearch = false;
        SearchStats searchStats = new SearchStats();
        Search.startTime = System.currentTimeMillis();
        Search.stopTime = Search.startTime + 10000;

        Search.quiescenceSearch(-INFINITY, INFINITY, false, board, new ArrayList<>(), searchStats);
        assertEquals(0,searchStats.getNodes());
        assertEquals(0, searchStats.getQNodes());
    }

    @Test
    public void testQSearchStandpatRaisesAlpha() {
        Board board = new Board();

        int score = Eval.eval(board);

        Search.abortSearch = false;
        SearchStats searchStats = new SearchStats();
        Search.startTime = System.currentTimeMillis();
        Search.stopTime = Search.startTime + 10000;

        int qScore = Search.quiescenceSearch(-INFINITY, INFINITY,false, board, new ArrayList<>(), searchStats);
        assertEquals(score, qScore);
    }

    @Test
    public void testQSearchStandpatDoesNotRaiseAlpha() {
        Board board = new Board();

        Search.abortSearch = false;
        SearchStats searchStats = new SearchStats();
        Search.startTime = System.currentTimeMillis();
        Search.stopTime = Search.startTime + 10000;

        int qScore = Search.quiescenceSearch(QUEEN_VAL, INFINITY,false, board, new ArrayList<>(), searchStats);
        assertEquals(QUEEN_VAL,qScore);
    }

    @Test
    public void testQSearchDoesExpandJustCaptures() {
        Board board = new Board("7k/8/8/3b4/8/8/6P1/K7 b - -");

        List<Move> moves = MoveGen.genPseudoLegalMoves(board, true,false);
        assertEquals(1, moves.size());

        Search.abortSearch = false;

        SearchStats searchStats = new SearchStats();
        Search.startTime = System.currentTimeMillis();
        Search.stopTime = Search.startTime + 10000;

        Search.quiescenceSearch(-INFINITY, INFINITY,false, board, new ArrayList<>(), searchStats);

        assertEquals(0,searchStats.getNodes());
        assertEquals(1, searchStats.getQNodes());
    }

    @Test
    public void testQSearchDoesExpandPromotions() {
        Board board = new Board("8/P6k/8/8/8/8/7K/8 w - -");

        List<Move> moves = MoveGen.genPseudoLegalMoves(board, true,false);
        assertEquals(4, moves.size()); // just promotions

        Search.abortSearch = false;

        SearchStats searchStats = new SearchStats();
        Search.startTime = System.currentTimeMillis();
        Search.stopTime = Search.startTime + 10000;

        Search.quiescenceSearch(-INFINITY, INFINITY,false, board, new ArrayList<>(), searchStats);

        assertEquals(0,searchStats.getNodes());
        assertEquals(4, searchStats.getQNodes());
    }

    @Test
    public void testQSearchBetaCutoff() {
        Board board = new Board("8/P6k/8/8/8/8/7K/8 w - -");

        List<Move> moves = MoveGen.genPseudoLegalMoves(board, true,false);
        assertEquals(4, moves.size()); // just promotions

        int score = Eval.eval(board);
        assertTrue(Math.abs(score) < QUEEN_VAL);

        Search.abortSearch = false;
        SearchStats searchStats = new SearchStats();
        Search.startTime = System.currentTimeMillis();
        Search.stopTime = Search.startTime + 10000;

        Search.quiescenceSearch(-INFINITY, -QUEEN_VAL,false, board, new ArrayList<>(), searchStats);

        assertEquals(0, searchStats.getNodes());
        assertEquals(0, searchStats.getQNodes());
    }
}
