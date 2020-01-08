package com.jamesswafford.chess4j.search;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.eval.Evaluator;
import org.junit.Test;

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
        int score = alphaBetaSearch.search();

        // then the evaluator should have been invoked for each move
        verify(evaluator, times(20)).evaluateBoard(any(Board.class));

        // and the score should be the highest returned score
        assertEquals(5, score);
    }

}
