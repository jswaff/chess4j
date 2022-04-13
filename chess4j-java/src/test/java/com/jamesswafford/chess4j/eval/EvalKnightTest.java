package com.jamesswafford.chess4j.eval;

import com.jamesswafford.chess4j.board.Board;
import org.junit.Test;

import static org.junit.Assert.*;

import static com.jamesswafford.chess4j.board.squares.Square.*;
import static com.jamesswafford.chess4j.eval.EvalKnight.*;

import static com.jamesswafford.chess4j.eval.EvalWeights.*;

public class EvalKnightTest {

    private final EvalWeights weights = new EvalWeights();

    @Test
    public void testEvalKnight() {
        Board board = new Board();

        assertEquals(weights.vals[KNIGHT_PST_IND + B1.value()] +
                        (long) weights.vals[KNIGHT_TROPISM_IND] * B1.distance(E8),
                evalKnight(weights, board, B1, false));

        // test the symmetry
        assertEquals(evalKnight(weights, board, B1, false),
                evalKnight(weights, board, B8, false));
    }

    @Test
    public void testEvalKnight_endGame() {
        Board board = new Board();

        assertEquals(weights.vals[KNIGHT_ENDGAME_PST_IND + B1.value()] + (long) weights.vals[KNIGHT_TROPISM_IND] * B1.distance(E8),
                evalKnight(weights, board, B1, true));

        // test the symmetry
        assertEquals(evalKnight(weights, board, B1, true),
                evalKnight(weights, board, B8, true));
    }

    @Test
    public void testExtractKnightFeatures() {
        Board board = new Board();

        int[] features = new int[NUM_WEIGHTS];
        extractKnightFeatures(features, board, B1, false);
        assertEquals(1, features[KNIGHT_PST_IND + B1.value()]);
        assertEquals(B1.distance(E8), features[KNIGHT_TROPISM_IND]);

        // test the symmetry
        int[] features2 = new int[NUM_WEIGHTS];
        extractKnightFeatures(features2, board, B8, false);
        assertEquals(-1, features2[KNIGHT_PST_IND + B1.value()]);
        assertEquals(-B8.distance(E1), features2[KNIGHT_TROPISM_IND]);
    }

    @Test
    public void testExtractKnightFeatures_endGame() {
        Board board = new Board();

        int[] features = new int[NUM_WEIGHTS];
        extractKnightFeatures(features, board, B1, true);
        assertEquals(1, features[KNIGHT_ENDGAME_PST_IND + B1.value()]);
        assertEquals(B1.distance(E8), features[KNIGHT_TROPISM_IND]);

        // test the symmetry
        int[] features2 = new int[NUM_WEIGHTS];
        extractKnightFeatures(features2, board, B8, true);
        assertEquals(-1, features2[KNIGHT_ENDGAME_PST_IND + B1.value()]);
        assertEquals(-B8.distance(E1), features2[KNIGHT_TROPISM_IND]);
    }

}
