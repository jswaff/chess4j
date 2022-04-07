package com.jamesswafford.chess4j.eval;

import com.jamesswafford.chess4j.board.Board;
import org.junit.Test;

import static org.junit.Assert.*;

import static com.jamesswafford.chess4j.board.squares.Square.*;
import static com.jamesswafford.chess4j.eval.EvalBishop.*;

import static com.jamesswafford.chess4j.eval.EvalWeightsVector.*;

public class EvalBishopTest {

    private final EvalWeightsVector weights = new EvalWeightsVector();

    @Test
    public void testEvalBishop() {

        Board board = new Board();

        assertEquals(weights.weights[BISHOP_PST_IND + C1.value()], evalBishop(weights, board, C1, false));

        // test the symmetry
        assertEquals(evalBishop(weights, board, C1, false), evalBishop(weights, board, C8, false));
    }

    @Test
    public void testEvalBishop_endGame() {

        Board board = new Board();

        assertEquals(weights.weights[BISHOP_ENDGAME_PST_IND + C1.value()], evalBishop(weights, board, C1, true));

        // test the symmetry
        assertEquals(evalBishop(weights, board, C1, true), evalBishop(weights, board, C8, true));
    }

    @Test
    public void testExtractBishopFeatures() {

        Board board = new Board();

        int[] features = new int[NUM_WEIGHTS];
        extractBishopFeatures(features, board, C1, false);
        assertEquals(1, features[BISHOP_PST_IND + C1.value()]);

        // test the symmetry
        int[] features2 = new int[NUM_WEIGHTS];
        extractBishopFeatures(features2, board, C8, false);
        assertEquals(-1, features2[BISHOP_PST_IND + C1.value()]);
    }

    @Test
    public void testExtractBishopFeatures_endGame() {

        Board board = new Board();

        int[] features = new int[NUM_WEIGHTS];
        extractBishopFeatures(features, board, C1, true);
        assertEquals(1, features[BISHOP_ENDGAME_PST_IND + C1.value()]);

        // test the symmetry
        int[] features2 = new int[NUM_WEIGHTS];
        extractBishopFeatures(features2, board, C8, true);
        assertEquals(-1, features2[BISHOP_ENDGAME_PST_IND + C1.value()]);
    }

}
