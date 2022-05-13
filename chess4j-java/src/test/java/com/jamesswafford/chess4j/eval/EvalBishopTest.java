package com.jamesswafford.chess4j.eval;

import com.jamesswafford.chess4j.board.Board;
import io.vavr.Tuple2;
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

        Tuple2<Integer, Integer> score = evalBishop(weights, board, C1);

        assertEquals(weights.vals[BISHOP_PST_IND + C1.value()], (int)score._1);
        assertEquals(weights.vals[BISHOP_ENDGAME_PST_IND + C1.value()], (int)score._2);

        // test the symmetry
        Tuple2<Integer, Integer> score2 = evalBishop(weights, board, C8);
        assertEquals((int)score._1, -(int)score2._1);
        assertEquals((int)score._2, -(int)score2._2);
    }

    @Test
    public void testExtractBishopFeatures() {

        Board board = new Board();

        double[] features = new double[weights.vals.length];
        extractBishopFeatures(features, board, C1, 1.0);
        assertEquals(1, features[BISHOP_PST_IND + C1.value()], testEpsilon);

        // test the symmetry
        double[] features2 = new double[weights.vals.length];
        extractBishopFeatures(features2, board, C8, 1.0);
        assertEquals(-1, features2[BISHOP_PST_IND + C1.value()], testEpsilon);
    }

    @Test
    public void testExtractBishopFeatures_endGame() {

        Board board = new Board();

        double[] features = new double[weights.vals.length];
        extractBishopFeatures(features, board, C1, 0.0);
        assertEquals(1, features[BISHOP_ENDGAME_PST_IND + C1.value()], testEpsilon);

        // test the symmetry
        double[] features2 = new double[weights.vals.length];
        extractBishopFeatures(features2, board, C8, 0.0);
        assertEquals(-1, features2[BISHOP_ENDGAME_PST_IND + C1.value()], testEpsilon);
    }

}
