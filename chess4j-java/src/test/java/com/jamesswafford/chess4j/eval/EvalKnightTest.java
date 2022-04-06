package com.jamesswafford.chess4j.eval;

import com.jamesswafford.chess4j.board.Board;
import org.junit.Test;

import static org.junit.Assert.*;

import static com.jamesswafford.chess4j.board.squares.Square.*;
import static com.jamesswafford.chess4j.eval.EvalKnight.*;

import static com.jamesswafford.chess4j.eval.EvalWeightsVector.*;

public class EvalKnightTest {

    private final EvalFeaturesVector features = new EvalFeaturesVector();
    private final EvalWeightsVector weights = new EvalWeightsVector();
    private final Board board = new Board();

    @Test
    public void testEvalKnight() {
        board.resetBoard();

        assertEquals(weights.weights[KNIGHT_PST_IND + B1.value()] + (long) weights.weights[KNIGHT_TROPISM_IND] * B1.distance(E8),
                evalKnight(features, weights, board, B1, false));

        // test the symmetry
        assertEquals(evalKnight(features, weights, board, B1, false), evalKnight(features, weights, board, B8, false));
    }

    @Test
    public void testEvalKnight_endGame() {
        board.resetBoard();

        assertEquals(weights.weights[KNIGHT_ENDGAME_PST_IND + B1.value()] + (long) weights.weights[KNIGHT_TROPISM_IND] * B1.distance(E8),
                evalKnight(features, weights, board, B1, true));

        // test the symmetry
        assertEquals(evalKnight(features, weights, board, B1, true), evalKnight(features, weights, board, B8, true));
    }

}
