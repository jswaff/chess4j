package dev.jamesswafford.chess4j.eval;

import dev.jamesswafford.chess4j.board.Board;
import io.vavr.Tuple2;
import org.junit.Test;

import static org.junit.Assert.*;

import static dev.jamesswafford.chess4j.board.squares.Square.*;
import static dev.jamesswafford.chess4j.eval.EvalBishop.*;

import static dev.jamesswafford.chess4j.eval.EvalWeights.*;

public class EvalBishopTest {

    private final EvalWeights weights = new EvalWeights();

    private final double testEpsilon = 0.000001;

    @Test
    public void testEvalBishop() {

        Board board = new Board();

        Tuple2<Integer, Integer> score = evalBishop(weights, board, C1);

        assertEquals(weights.vals[BISHOP_PST_MG_IND + C1.value()] + weights.vals[BISHOP_MOBILITY_MG_IND], (int)score._1);
        assertEquals(weights.vals[BISHOP_PST_EG_IND + C1.value()] + weights.vals[BISHOP_MOBILITY_EG_IND], (int)score._2);

        // test the symmetry
        Tuple2<Integer, Integer> score2 = evalBishop(weights, board, C8);
        assertEquals((int)score._1, -(int)score2._1);
        assertEquals((int)score._2, -(int)score2._2);
    }

    @Test
    public void testEvalBishop2() {

        Board board = new Board("r2qk2r/3bbppp/p1pppn2/6B1/3NP3/2N5/PPP2PPP/R2QR1K1 w - -");

        Tuple2<Integer, Integer> score = evalBishop(weights, board, G5);

        assertEquals(weights.vals[BISHOP_PST_MG_IND + G5.value()] +
                weights.vals[BISHOP_MOBILITY_MG_IND + 6], (int)score._1);
        assertEquals(weights.vals[BISHOP_PST_EG_IND + G5.value()] +
                weights.vals[BISHOP_MOBILITY_EG_IND + 6], (int)score._2);


        // test one of the black bishops
        Tuple2<Integer, Integer> score2 = evalBishop(weights, board, D7);

        assertEquals(-(weights.vals[BISHOP_PST_MG_IND + D2.value()] +
                weights.vals[BISHOP_MOBILITY_MG_IND + 1]), (int)score2._1);
        assertEquals(-(weights.vals[BISHOP_PST_EG_IND + D2.value()] +
                weights.vals[BISHOP_MOBILITY_EG_IND + 1]), (int)score2._2);
    }

    @Test
    public void testEvalBishop3_Trapped() {
        Board board = new Board("8/pp2k1p1/4pp2/1P6/7p/P3P1P1/5PKb/2B5 b - - 0 1");

        Tuple2<Integer, Integer> score = evalBishop(weights, board, H2);
        assertEquals(-weights.vals[BISHOP_PST_MG_IND + H2.flipVertical().value()] -
                weights.vals[BISHOP_MOBILITY_MG_IND + 1] -
                weights.vals[BISHOP_TRAPPED_IND], (int)score._1);
        assertEquals(-weights.vals[BISHOP_PST_EG_IND + H2.flipVertical().value()] -
                weights.vals[BISHOP_MOBILITY_EG_IND + 1] -
                weights.vals[BISHOP_TRAPPED_IND], (int)score._2);
    }

    @Test
    public void testExtractBishopFeatures() {

        Board board = new Board();

        double[] features = new double[weights.vals.length];
        extractBishopFeatures(features, board, C1, 1.0);
        assertEquals(1, features[BISHOP_PST_MG_IND + C1.value()], testEpsilon);
        assertEquals(1, features[BISHOP_MOBILITY_MG_IND], testEpsilon);

        // test the symmetry
        double[] features2 = new double[weights.vals.length];
        extractBishopFeatures(features2, board, C8, 1.0);
        assertEquals(-1, features2[BISHOP_PST_MG_IND + C1.value()], testEpsilon);
        assertEquals(-1, features2[BISHOP_MOBILITY_MG_IND], testEpsilon);
    }

    @Test
    public void testExtractBishopFeatures_endGame() {

        Board board = new Board();

        double[] features = new double[weights.vals.length];
        extractBishopFeatures(features, board, C1, 0.0);
        assertEquals(1, features[BISHOP_PST_EG_IND + C1.value()], testEpsilon);
        assertEquals(1, features[BISHOP_MOBILITY_EG_IND], testEpsilon);

        // test the symmetry
        double[] features2 = new double[weights.vals.length];
        extractBishopFeatures(features2, board, C8, 0.0);
        assertEquals(-1, features2[BISHOP_PST_EG_IND + C1.value()], testEpsilon);
        assertEquals(-1, features2[BISHOP_MOBILITY_EG_IND], testEpsilon);
    }

    @Test
    public void testExtractBishopFeatures_mobility() {

        Board board = new Board("r2qk2r/3bbppp/p1pppn2/6B1/3NP3/2N5/PPP2PPP/R2QR1K1 w - -");

        double[] features = new double[weights.vals.length];
        extractBishopFeatures(features, board, G5, 0.4);
        assertEquals(0.4, features[BISHOP_PST_MG_IND + G5.value()], testEpsilon);
        assertEquals(0.6, features[BISHOP_PST_EG_IND + G5.value()], testEpsilon);
        assertEquals(0.4, features[BISHOP_MOBILITY_MG_IND + 6], testEpsilon);
        assertEquals(0.6, features[BISHOP_MOBILITY_EG_IND + 6], testEpsilon);

        double[] features2 = new double[weights.vals.length];
        extractBishopFeatures(features2, board, D7, 0.4);
        assertEquals(-0.4, features2[BISHOP_PST_MG_IND + D2.value()], testEpsilon);
        assertEquals(-0.6, features2[BISHOP_PST_EG_IND + D2.value()], testEpsilon);
        assertEquals(-0.4, features2[BISHOP_MOBILITY_MG_IND + 1], testEpsilon);
        assertEquals(-0.6, features2[BISHOP_MOBILITY_EG_IND + 1], testEpsilon);
    }

    @Test
    public void testExtractBishopFeatures_trapped() {

        Board board = new Board("8/pp2k1p1/4pp2/1P6/7p/P3P1P1/5PKb/2B5 b - - 0 1");

        double[] features = new double[weights.vals.length];
        extractBishopFeatures(features, board, H2, 0.4);

        assertEquals(-1.0, features[BISHOP_TRAPPED_IND], 0);
    }
}
