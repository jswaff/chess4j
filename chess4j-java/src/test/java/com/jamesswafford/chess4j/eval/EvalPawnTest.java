package com.jamesswafford.chess4j.eval;

import com.jamesswafford.chess4j.board.Board;
import io.vavr.Tuple2;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

import static com.jamesswafford.chess4j.board.squares.Square.*;
import static com.jamesswafford.chess4j.eval.EvalPawn.*;

import static com.jamesswafford.chess4j.eval.EvalWeights.*;

public class EvalPawnTest {

    private final EvalWeights weights = new EvalWeights();

    private final double testEpsilon = 0.000001;

    @Test
    public void testEvalPawn() {

        Board board = new Board();

        Tuple2<Integer, Integer> score = evalPawn(weights, board, E2);

        assertEquals(weights.vals[PAWN_PST_MG_IND + E2.value()], (int)score._1);
        assertEquals(weights.vals[PAWN_PST_EG_IND + E2.value()], (int)score._2);

        // test the symmetry
        Tuple2<Integer, Integer> score2 = evalPawn(weights, board, E7);
        assertEquals((int)score._1, -(int)score2._1);
        assertEquals((int)score._2, -(int)score2._2);
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

        Tuple2<Integer, Integer> score = evalPawn(weights, board, B6);

        assertEquals(weights.vals[PAWN_PST_MG_IND + B6.value()] + weights.vals[PASSED_PAWN_MG_IND], (int)score._1);
        assertEquals(weights.vals[PAWN_PST_EG_IND + B6.value()] + weights.vals[PASSED_PAWN_EG_IND], (int)score._2);

        // the black pawn on A2 is passed and isolated
        Tuple2<Integer, Integer> score2 = evalPawn(weights, board, A2);

        assertEquals(weights.vals[PAWN_PST_MG_IND + A7.value()] + weights.vals[PASSED_PAWN_MG_IND] +
                        weights.vals[ISOLATED_PAWN_MG_IND],
                -(int)score2._1);

        assertEquals(weights.vals[PAWN_PST_EG_IND + A7.value()] + weights.vals[PASSED_PAWN_EG_IND] +
                        weights.vals[ISOLATED_PAWN_EG_IND],
                -(int)score2._2);
    }

    @Test
    public void testExtractPawnFeatures() {

        Board board = new Board();

        double[] features = new double[weights.vals.length];
        extractPawnFeatures(features, board, E2, 1.0);
        assertEquals(1, features[PAWN_PST_MG_IND + E2.value()], testEpsilon);

        // test the symmetry
        double[] features2 = new double[weights.vals.length];
        extractPawnFeatures(features2, board, E7, 1.0);
        assertEquals(-1, features2[PAWN_PST_MG_IND + E2.value()], testEpsilon);
    }

    @Test
    public void testExtractPawnFeatures_endGame() {

        Board board = new Board();

        double[] features = new double[weights.vals.length];
        extractPawnFeatures(features, board, E2, 0.0);
        assertEquals(1, features[PAWN_PST_EG_IND + E2.value()], testEpsilon);

        // test the symmetry
        double[] features2 = new double[weights.vals.length];
        extractPawnFeatures(features2, board, E7, 0.0);
        assertEquals(-1, features2[PAWN_PST_EG_IND + E2.value()], testEpsilon);
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

        double[] features = new double[weights.vals.length];
        extractPawnFeatures(features, board, B6, 0.8);
        assertEquals(0.8, features[PASSED_PAWN_MG_IND], testEpsilon);
        assertEquals(0.2, features[PASSED_PAWN_EG_IND], testEpsilon);

        // the black pawn on A2 is passed and isolated
        Arrays.fill(features, 0);
        extractPawnFeatures(features, board, A2, 0.7);
        assertEquals(-0.7, features[PASSED_PAWN_MG_IND], testEpsilon);
        assertEquals(-0.3, features[PASSED_PAWN_EG_IND], testEpsilon);
        assertEquals(-0.7, features[ISOLATED_PAWN_MG_IND], testEpsilon);
        assertEquals(-0.3, features[ISOLATED_PAWN_EG_IND], testEpsilon);
    }

}
