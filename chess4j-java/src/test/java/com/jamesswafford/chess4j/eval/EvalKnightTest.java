package com.jamesswafford.chess4j.eval;

import com.jamesswafford.chess4j.board.Board;
import org.junit.Test;

import static org.junit.Assert.*;

import static com.jamesswafford.chess4j.board.squares.Square.*;
import static com.jamesswafford.chess4j.eval.EvalKnight.*;

import static com.jamesswafford.chess4j.eval.EvalWeightsVector.*;

public class EvalKnightTest {

    private final EvalFeaturesVector features = new EvalFeaturesVector();
    private final EvalFeaturesVector features2 = new EvalFeaturesVector();
    private final EvalWeightsVector weights = new EvalWeightsVector();
    private final Board board = new Board();

    @Test
    public void testEvalKnight() {
        board.resetBoard();
        features.reset();

        assertEquals(weights.weights[KNIGHT_PST_IND + B1.value()] +
                        (long) weights.weights[KNIGHT_TROPISM_IND] * B1.distance(E8),
                evalKnight(features, weights, board, B1, false));
        assertEquals(1, features.features[KNIGHT_PST_IND + B1.value()]);
        assertEquals(B1.distance(E8), features.features[KNIGHT_TROPISM_IND]);

        // test the symmetry
        features.reset();
        features2.reset();
        assertEquals(evalKnight(features, weights, board, B1, false), evalKnight(features2, weights, board, B8, false));
        testFeatureSymmetry(features, features2);
    }

    @Test
    public void testEvalKnight_endGame() {
        board.resetBoard();
        features.reset();

        assertEquals(weights.weights[KNIGHT_ENDGAME_PST_IND + B1.value()] + (long) weights.weights[KNIGHT_TROPISM_IND] * B1.distance(E8),
                evalKnight(features, weights, board, B1, true));
        assertEquals(1, features.features[KNIGHT_ENDGAME_PST_IND + B1.value()]);
        assertEquals(B1.distance(E8), features.features[KNIGHT_TROPISM_IND]);

        // test the symmetry
        features.reset();
        features2.reset();
        assertEquals(evalKnight(features, weights, board, B1, true),
                evalKnight(features2, weights, board, B8, true));
        testFeatureSymmetry(features, features2);
    }

    void testFeatureSymmetry(EvalFeaturesVector f1, EvalFeaturesVector f2) {
        for (int i=0;i<f1.features.length;i++) {
            assertEquals(-f1.features[i], f2.features[i]);
        }
    }

}
