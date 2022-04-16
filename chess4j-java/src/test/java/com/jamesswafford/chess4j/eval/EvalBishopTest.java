package com.jamesswafford.chess4j.eval;

import com.jamesswafford.chess4j.board.Board;
import org.junit.Test;

import static org.junit.Assert.*;

import static com.jamesswafford.chess4j.board.squares.Square.*;
import static com.jamesswafford.chess4j.eval.EvalBishop.*;

import static com.jamesswafford.chess4j.eval.EvalWeights.*;

public class EvalBishopTest {

    private final EvalWeights weights = new EvalWeights();

    private final double testEpsilon = 0.000001;

    @Test
    public void testEvalBishop() {

        Board board = new Board();

        assertEquals(weights.vals[BISHOP_PST_IND + C1.value()], evalBishop(weights, board, C1, false));

        // test the symmetry
        assertEquals(evalBishop(weights, board, C1, false), evalBishop(weights, board, C8, false));
    }

    @Test
    public void testEvalBishop_endGame() {

        Board board = new Board();

        assertEquals(weights.vals[BISHOP_ENDGAME_PST_IND + C1.value()], evalBishop(weights, board, C1, true));

        // test the symmetry
        assertEquals(evalBishop(weights, board, C1, true), evalBishop(weights, board, C8, true));
    }

    @Test
    public void testExtractBishopFeatures() {

        Board board = new Board();

        double[] features = new double[weights.vals.length];
        extractBishopFeatures(features, board, C1, false);
        assertEquals(1, features[BISHOP_PST_IND + C1.value()], testEpsilon);

        // test the symmetry
        double[] features2 = new double[weights.vals.length];
        extractBishopFeatures(features2, board, C8, false);
        assertEquals(-1, features2[BISHOP_PST_IND + C1.value()], testEpsilon);
    }

    @Test
    public void testExtractBishopFeatures_endGame() {

        Board board = new Board();

        double[] features = new double[weights.vals.length];
        extractBishopFeatures(features, board, C1, true);
        assertEquals(1, features[BISHOP_ENDGAME_PST_IND + C1.value()], testEpsilon);

        // test the symmetry
        double[] features2 = new double[weights.vals.length];
        extractBishopFeatures(features2, board, C8, true);
        assertEquals(-1, features2[BISHOP_ENDGAME_PST_IND + C1.value()], testEpsilon);
    }

}
