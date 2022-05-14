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
    public void testEvalBishop2() {

        Board board = new Board("r2qk2r/3bbppp/p1pppn2/6B1/3NP3/2N5/PPP2PPP/R2QR1K1 w - -");

        Tuple2<Integer, Integer> score = evalBishop(weights, board, G5);

        assertEquals(weights.vals[BISHOP_PST_IND + G5.value()] +
                weights.vals[BISHOP_MOBILITY_IND] * 6L, (int)score._1);
        assertEquals(weights.vals[BISHOP_ENDGAME_PST_IND + G5.value()] +
                weights.vals[BISHOP_ENDGAME_MOBILITY_IND] * 6L, (int)score._2);


        // test one of the black bishops
        Tuple2<Integer, Integer> score2 = evalBishop(weights, board, D7);

        assertEquals(-(weights.vals[BISHOP_PST_IND + D2.value()] +
                weights.vals[BISHOP_MOBILITY_IND]), (int)score2._1);
        assertEquals(-(weights.vals[BISHOP_ENDGAME_PST_IND + D2.value()] +
                weights.vals[BISHOP_ENDGAME_MOBILITY_IND]), (int)score2._2);
    }

    @Test
    public void testExtractBishopFeatures() {

        Board board = new Board();

        double[] features = new double[weights.vals.length];
        extractBishopFeatures(features, board, C1, 1.0);
        assertEquals(1, features[BISHOP_PST_IND + C1.value()], testEpsilon);
        assertEquals(0, features[BISHOP_MOBILITY_IND], testEpsilon);

        // test the symmetry
        double[] features2 = new double[weights.vals.length];
        extractBishopFeatures(features2, board, C8, 1.0);
        assertEquals(-1, features2[BISHOP_PST_IND + C1.value()], testEpsilon);
        assertEquals(0, features2[BISHOP_MOBILITY_IND], testEpsilon);
    }

    @Test
    public void testExtractBishopFeatures_endGame() {

        Board board = new Board();

        double[] features = new double[weights.vals.length];
        extractBishopFeatures(features, board, C1, 0.0);
        assertEquals(1, features[BISHOP_ENDGAME_PST_IND + C1.value()], testEpsilon);
        assertEquals(0, features[BISHOP_ENDGAME_MOBILITY_IND], testEpsilon);

        // test the symmetry
        double[] features2 = new double[weights.vals.length];
        extractBishopFeatures(features2, board, C8, 0.0);
        assertEquals(-1, features2[BISHOP_ENDGAME_PST_IND + C1.value()], testEpsilon);
        assertEquals(0, features2[BISHOP_ENDGAME_MOBILITY_IND], testEpsilon);
    }

    @Test
    public void testExtractBishopFeatures_mobility() {

        Board board = new Board("r2qk2r/3bbppp/p1pppn2/6B1/3NP3/2N5/PPP2PPP/R2QR1K1 w - -");

        double[] features = new double[weights.vals.length];
        extractBishopFeatures(features, board, G5, 0.4);
        assertEquals(0.4, features[BISHOP_PST_IND + G5.value()], testEpsilon);
        assertEquals(0.6, features[BISHOP_ENDGAME_PST_IND + G5.value()], testEpsilon);
        assertEquals(0.4 * 6, features[BISHOP_MOBILITY_IND], testEpsilon);
        assertEquals(0.6 * 6, features[BISHOP_ENDGAME_MOBILITY_IND], testEpsilon);

        double[] features2 = new double[weights.vals.length];
        extractBishopFeatures(features2, board, D7, 0.4);
        assertEquals(-0.4, features2[BISHOP_PST_IND + D2.value()], testEpsilon);
        assertEquals(-0.6, features2[BISHOP_ENDGAME_PST_IND + D2.value()], testEpsilon);
        assertEquals(-0.4, features2[BISHOP_MOBILITY_IND], testEpsilon);
        assertEquals(-0.6, features2[BISHOP_ENDGAME_MOBILITY_IND], testEpsilon);
    }

}
