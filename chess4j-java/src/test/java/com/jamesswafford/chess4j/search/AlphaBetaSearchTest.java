package com.jamesswafford.chess4j.search;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.eval.Evaluator;
import org.junit.Test;

import static com.jamesswafford.chess4j.Constants.CHECKMATE;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import static com.jamesswafford.chess4j.Constants.INFINITY;
import static com.jamesswafford.chess4j.pieces.Pawn.*;
import static com.jamesswafford.chess4j.pieces.Knight.*;
import static com.jamesswafford.chess4j.board.squares.Square.*;

public class AlphaBetaSearchTest {

    @Test
    public void testSearch_initialPos_depth1() {

        // given a board in the initial position
        Board board = new Board();
        Evaluator evaluator = mock(Evaluator.class);
        SearchParameters params = new SearchParameters(1, -INFINITY, INFINITY);

        // set up the return scores for a couple of moves.  all others default to score=0.
        // the returned scores are from black's point-of-view so the lower the better for white.
        Board board1 = new Board();
        board1.applyMove(new Move(WHITE_PAWN, E2, E4));
        when(evaluator.evaluateBoard(board1)).thenReturn(3);

        Board board2 = new Board();
        board2.applyMove(new Move(WHITE_KNIGHT, B1, C3));
        when(evaluator.evaluateBoard(board2)).thenReturn(-5);

        // when the search is invoked
        AlphaBetaSearch alphaBetaSearch = new AlphaBetaSearch(board, params, evaluator);
        int score = alphaBetaSearch.search(false);

        // then the evaluator should have been invoked for each move
        verify(evaluator, times(20)).evaluateBoard(any(Board.class));

        // and the score should be the highest returned score
        assertEquals(5, score);
    }

    @Test
    public void testMateIn1() {
        Board board = new Board("4k3/8/3Q4/2B5/8/8/1K6/8 w - -");

        Evaluator evaluator = mock(Evaluator.class);
        SearchParameters params = new SearchParameters(2, -INFINITY, INFINITY);

        AlphaBetaSearch alphaBetaSearch = new AlphaBetaSearch(board, params, evaluator);
        int score = alphaBetaSearch.search(false);

        assertEquals(CHECKMATE-1, score);
    }

    @Test
    public void testMateIn1b() {
        Board board = new Board("4K3/8/8/3n2q1/8/8/3k4/8 b - -");

        Evaluator evaluator = mock(Evaluator.class);
        SearchParameters params = new SearchParameters(2, -INFINITY, INFINITY);

        AlphaBetaSearch alphaBetaSearch = new AlphaBetaSearch(board, params, evaluator);
        int score = alphaBetaSearch.search(false);

        assertEquals(CHECKMATE-1, score);
    }

    @Test
    public void testMateIn2() {
        Board board = new Board("r1bq2r1/b4pk1/p1pp1p2/1p2pP2/1P2P1PB/3P4/1PPQ2P1/R3K2R w - -");

        Evaluator evaluator = mock(Evaluator.class);
        SearchParameters params = new SearchParameters(4, -INFINITY, INFINITY);

        AlphaBetaSearch alphaBetaSearch = new AlphaBetaSearch(board, params, evaluator);
        int score = alphaBetaSearch.search(false);

        assertEquals(CHECKMATE-3, score);
    }

    @Test
    public void testMateIn3() {
        Board board = new Board("r5rk/5p1p/5R2/4B3/8/8/7P/7K w - -");

        Evaluator evaluator = mock(Evaluator.class);
        SearchParameters params = new SearchParameters(6, -INFINITY, INFINITY);

        AlphaBetaSearch alphaBetaSearch = new AlphaBetaSearch(board, params, evaluator);
        int score = alphaBetaSearch.search(false);

        assertEquals(CHECKMATE-5, score);
    }

    @Test
    public void testStaleMate() {
        Board board = new Board("8/6p1/5p2/5k1K/7P/8/8/8 w - -");

        Evaluator evaluator = mock(Evaluator.class);
        SearchParameters params = new SearchParameters(1, -INFINITY, INFINITY);

        AlphaBetaSearch alphaBetaSearch = new AlphaBetaSearch(board, params, evaluator);
        int score = alphaBetaSearch.search(false);

        assertEquals(0, score);
    }

}
