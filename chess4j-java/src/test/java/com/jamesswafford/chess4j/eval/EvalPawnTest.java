package com.jamesswafford.chess4j.eval;

import com.jamesswafford.chess4j.board.Board;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

import static com.jamesswafford.chess4j.board.squares.Square.*;
import static com.jamesswafford.chess4j.eval.EvalPawn.*;

import static com.jamesswafford.chess4j.eval.EvalWeights.*;

public class EvalPawnTest {

    private final EvalWeights weights = new EvalWeights();

    @Test
    public void testEvalPawn() {

        Board board = new Board();

        assertEquals(weights.vals[PAWN_PST_IND + E2.value()], evalPawn(weights, board, E2, false));

        // test the symmetry
        assertEquals(evalPawn(weights, board, E2, false), evalPawn(weights, board, E7, false));
    }

    @Test
    public void testEvalPawn_endGame() {

        Board board = new Board();

        assertEquals(weights.vals[PAWN_ENDGAME_PST_IND + E2.value()], evalPawn(weights, board, E2, true));

        // test the symmetry
        assertEquals(evalPawn(weights, board, E2, true), evalPawn(weights, board, E7, true));
    }

    @Test
    public void testEvalPawn_wiki3() {

        Board board = new Board("8/8/1PP2PbP/3r4/8/1Q5p/p5N1/k3K3 b - - 0 1");

        /*
        - - - - - - - -
        - - - - - - - -
        - P P - - P b P    black to move
        - - - r - - - -    no ep
        - - - - - - - -    no castling rights
        - Q - - - - - p
        p - - - - - N -
        k - - - K - - -
        */

        assertEquals(weights.vals[PAWN_PST_IND + B6.value()] + weights.vals[PASSED_PAWN_IND],
                evalPawn(weights, board, B6, false));

        // the black pawn on A2 is passed and isolated
        assertEquals(weights.vals[PAWN_PST_IND + A7.value()] + weights.vals[PASSED_PAWN_IND] +
                        weights.vals[ISOLATED_PAWN_IND],
                evalPawn(weights, board, A2, false));
    }

    @Test
    public void testExtractPawnFeatures() {

        Board board = new Board();

        int[] features = new int[NUM_WEIGHTS];
        extractPawnFeatures(features, board, E2, false);
        assertEquals(1, features[PAWN_PST_IND + E2.value()]);

        // test the symmetry
        int[] features2 = new int[NUM_WEIGHTS];
        extractPawnFeatures(features2, board, E7, false);
        assertEquals(-1, features2[PAWN_PST_IND + E2.value()]);
    }

    @Test
    public void testExtractPawnFeatures_endGame() {

        Board board = new Board();

        int[] features = new int[NUM_WEIGHTS];
        extractPawnFeatures(features, board, E2, true);
        assertEquals(1, features[PAWN_ENDGAME_PST_IND + E2.value()]);

        // test the symmetry
        int[] features2 = new int[NUM_WEIGHTS];
        extractPawnFeatures(features2, board, E7, true);
        assertEquals(-1, features2[PAWN_ENDGAME_PST_IND + E2.value()]);
    }

    @Test
    public void testExtractPawnFeatures_wiki3() {

        Board board = new Board("8/8/1PP2PbP/3r4/8/1Q5p/p5N1/k3K3 b - - 0 1");

        /*
        - - - - - - - -
        - - - - - - - -
        - P P - - P b P    black to move
        - - - r - - - -    no ep
        - - - - - - - -    no castling rights
        - Q - - - - - p
        p - - - - - N -
        k - - - K - - -
        */

        int[] features = new int[NUM_WEIGHTS];
        extractPawnFeatures(features, board, B6, false);
        assertEquals(1, features[PASSED_PAWN_IND]);

        // the black pawn on A2 is passed and isolated
        Arrays.fill(features, 0);
        extractPawnFeatures(features, board, A2, false);
        assertEquals(-1, features[PASSED_PAWN_IND]);
        assertEquals(-1, features[ISOLATED_PAWN_IND]);
    }

}
