package com.jamesswafford.chess4j.eval;

import com.jamesswafford.chess4j.board.Board;
import org.junit.Test;

import static org.junit.Assert.*;

import static com.jamesswafford.chess4j.board.squares.Square.*;
import static com.jamesswafford.chess4j.eval.EvalRook.*;

import static com.jamesswafford.chess4j.eval.EvalWeightsVector.*;

public class EvalRookTest {

    private final EvalFeaturesVector features = new EvalFeaturesVector();
    private final EvalWeightsVector weights = new EvalWeightsVector();
    private final Board board = new Board();

    @Test
    public void testEvalRook() {

        board.resetBoard();

        assertEquals(weights.weights[ROOK_PST_IND + A1.value()], evalRook(features, weights, board, A1, false));

        // test the symmetry
        assertEquals(evalRook(features, weights, board, A1, false), evalRook(features, weights, board, A8, false));
    }

    @Test
    public void testEvalRook_endGame() {

        board.resetBoard();

        assertEquals(weights.weights[ROOK_ENDGAME_PST_IND + A1.value()], evalRook(features, weights, board, A1, true));

        // test the symmetry
        assertEquals(evalRook(features, weights, board, A1, true), evalRook(features, weights, board, A8, true));
    }

    @Test
    public void testEvalRook_bankRankMate() {

        board.setPos("7k/2Q2R2/8/8/8/8/r7/7K w - - 0 1");

        assertEquals(weights.weights[ROOK_PST_IND + F7.value()] + weights.weights[MAJOR_ON_7TH_IND] +
                        weights.weights[ROOK_OPEN_FILE_IND],
                evalRook(features, weights, board, F7, false));
    }

    @Test
    public void testEvalRook_rookOpenFile() {

        board.setPos("3r3k/8/8/8/8/8/8/7K b - - 0 1");

        assertEquals(weights.weights[ROOK_PST_IND + D1.value()] + weights.weights[ROOK_OPEN_FILE_IND],
                evalRook(features, weights, board, D8, false));
    }

    @Test
    public void testEvalRook_rookOpenHalfOpenFile() {

        // friendly pawn but no enemy -- not half open (or open)
        board.setPos("8/2P5/8/2R5/K7/8/7k/8 w - - 0 1");

        assertEquals(weights.weights[ROOK_PST_IND + C5.value()], evalRook(features, weights, board, C5, false));

        // enemy pawn on C makes it half open
        board.setPos("8/2p5/8/2R5/K7/8/7k/8 w - - 0 1");

        assertEquals(weights.weights[ROOK_PST_IND + C5.value()] + weights.weights[ROOK_HALF_OPEN_FILE_IND],
                evalRook(features, weights, board, C5, false));
    }
}
