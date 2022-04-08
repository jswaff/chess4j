package com.jamesswafford.chess4j.eval;

import com.jamesswafford.chess4j.board.Board;
import org.junit.Test;

import static org.junit.Assert.*;

import static com.jamesswafford.chess4j.board.squares.Square.*;
import static com.jamesswafford.chess4j.eval.EvalQueen.*;

import static com.jamesswafford.chess4j.eval.EvalWeightsVector.*;

public class EvalQueenTest {

    private final EvalWeightsVector weights = new EvalWeightsVector();

    @Test
    public void testEvalQueen() {

        Board board = new Board();

        assertEquals(weights.weights[QUEEN_PST_IND + D1.value()], evalQueen(weights, board, D1, false));

        // test symmetry
        assertEquals(evalQueen(weights, board, D1, false), evalQueen(weights, board, D8, false));
    }

    @Test
    public void testEvalQueen_endGame() {

        Board board = new Board();

        assertEquals(weights.weights[QUEEN_ENDGAME_PST_IND + D1.value()], evalQueen(weights, board, D1, true));

        // test symmetry
        assertEquals(evalQueen(weights, board, D1, true), evalQueen(weights, board, D8, true));
    }

    @Test
    public void testEvalQueen_bankRankMate() {

        Board board = new Board("7k/2Q2R2/8/8/8/8/r7/7K w - - 0 1");

        assertEquals(weights.weights[QUEEN_PST_IND + C7.value()] + weights.weights[MAJOR_ON_7TH_IND] +
                        weights.weights[CONNECTED_MAJORS_ON_7TH_IND],
                evalQueen(weights, board, C7, false));
    }

    @Test
    public void testExtractQueenFeatures() {

        Board board = new Board();

        int[] features = new int[NUM_WEIGHTS];
        extractQueenFeatures(features, board, D1, false);
        assertEquals(1, features[QUEEN_PST_IND + D1.value()]);

        // test the symmetry
        int[] features2 = new int[NUM_WEIGHTS];
        extractQueenFeatures(features2, board, D8, false);
        assertEquals(-1, features2[QUEEN_PST_IND + D1.value()]);
    }

    @Test
    public void testExtractQueenFeatures_endGame() {

        Board board = new Board();

        int[] features = new int[NUM_WEIGHTS];
        extractQueenFeatures(features, board, D1, true);
        assertEquals(1, features[QUEEN_ENDGAME_PST_IND + D1.value()]);

        // test the symmetry
        int[] features2 = new int[NUM_WEIGHTS];
        extractQueenFeatures(features2, board, D8, true);
        assertEquals(-1, features2[QUEEN_ENDGAME_PST_IND + D1.value()]);
    }

    @Test
    public void testExtractQueenFeatures_bankRankMate() {

        Board board = new Board("7k/2Q2R2/8/8/8/8/r7/7K w - - 0 1");

        int[] features = new int[NUM_WEIGHTS];
        extractQueenFeatures(features, board, C7, false);
        assertEquals(1, features[MAJOR_ON_7TH_IND]);
        assertEquals(1, features[CONNECTED_MAJORS_ON_7TH_IND]);
    }

}
