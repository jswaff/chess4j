package com.jamesswafford.chess4j.eval;

import com.jamesswafford.chess4j.board.Board;
import org.junit.Test;

import static org.junit.Assert.*;

import static com.jamesswafford.chess4j.board.squares.Square.*;
import static com.jamesswafford.chess4j.eval.EvalQueen.*;

import static com.jamesswafford.chess4j.eval.EvalWeights.*;

public class EvalQueenTest {

    private final EvalWeights weights = new EvalWeights();

    private final double testEpsilon = 0.000001;

    @Test
    public void testEvalQueen() {

        Board board = new Board();

        assertEquals(weights.vals[QUEEN_PST_IND + D1.value()], evalQueen(weights, board, D1, false));

        // test symmetry
        assertEquals(evalQueen(weights, board, D1, false), evalQueen(weights, board, D8, false));
    }

    @Test
    public void testEvalQueen_endGame() {

        Board board = new Board();

        assertEquals(weights.vals[QUEEN_ENDGAME_PST_IND + D1.value()], evalQueen(weights, board, D1, true));

        // test symmetry
        assertEquals(evalQueen(weights, board, D1, true), evalQueen(weights, board, D8, true));
    }

    @Test
    public void testEvalQueen_bankRankMate() {

        Board board = new Board("7k/2Q2R2/8/8/8/8/r7/7K w - - 0 1");

        assertEquals(weights.vals[QUEEN_PST_IND + C7.value()] + weights.vals[MAJOR_ON_7TH_IND] +
                        weights.vals[CONNECTED_MAJORS_ON_7TH_IND],
                evalQueen(weights, board, C7, false));
    }

    @Test
    public void testExtractQueenFeatures() {

        Board board = new Board();

        double[] features = new double[weights.vals.length];
        extractQueenFeatures(features, board, D1, false);
        assertEquals(1, features[QUEEN_PST_IND + D1.value()], testEpsilon);

        // test the symmetry
        double[] features2 = new double[weights.vals.length];
        extractQueenFeatures(features2, board, D8, false);
        assertEquals(-1, features2[QUEEN_PST_IND + D1.value()], testEpsilon);
    }

    @Test
    public void testExtractQueenFeatures_endGame() {

        Board board = new Board();

        double[] features = new double[weights.vals.length];
        extractQueenFeatures(features, board, D1, true);
        assertEquals(1, features[QUEEN_ENDGAME_PST_IND + D1.value()], testEpsilon);

        // test the symmetry
        double[] features2 = new double[weights.vals.length];
        extractQueenFeatures(features2, board, D8, true);
        assertEquals(-1, features2[QUEEN_ENDGAME_PST_IND + D1.value()], testEpsilon);
    }

    @Test
    public void testExtractQueenFeatures_bankRankMate() {

        Board board = new Board("7k/2Q2R2/8/8/8/8/r7/7K w - - 0 1");

        double[] features = new double[weights.vals.length];
        extractQueenFeatures(features, board, C7, false);
        assertEquals(1, features[MAJOR_ON_7TH_IND], testEpsilon);
        assertEquals(1, features[CONNECTED_MAJORS_ON_7TH_IND], testEpsilon);
    }

}
