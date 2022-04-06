package com.jamesswafford.chess4j.eval;

import com.jamesswafford.chess4j.board.Board;
import org.junit.Test;

import static org.junit.Assert.*;

import static com.jamesswafford.chess4j.board.squares.Square.*;
import static com.jamesswafford.chess4j.eval.EvalBishop.*;

import static com.jamesswafford.chess4j.eval.EvalWeightsVector.*;

public class EvalBishopTest {

    private final EvalFeaturesVector features = new EvalFeaturesVector();
    private final EvalWeightsVector weights = new EvalWeightsVector();

    @Test
    public void testEvalBishop() {

        Board board = new Board();

        assertEquals(weights.weights[BISHOP_PST_IND + C1.value()], evalBishop(features, weights, board, C1, false));

        // test the symmetry
        assertEquals(evalBishop(features, weights, board, C1, false), evalBishop(features, weights, board, C8, false));
    }

    @Test
    public void testEvalBishop_endGame() {

        Board board = new Board();

        assertEquals(weights.weights[BISHOP_ENDGAME_PST_IND + C1.value()], evalBishop(features, weights, board, C1, true));

        // test the symmetry
        assertEquals(evalBishop(features, weights, board, C1, true), evalBishop(features, weights, board, C8, true));
    }
}
