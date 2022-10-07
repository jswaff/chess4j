package com.jamesswafford.chess4j.eval;

import com.jamesswafford.chess4j.board.Board;
import io.vavr.Tuple2;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

import static com.jamesswafford.chess4j.board.squares.Square.*;
import static com.jamesswafford.chess4j.eval.EvalMajorOn7th.*;

import static com.jamesswafford.chess4j.eval.EvalWeights.*;

public class EvalMajorOn7thTest {

    private final EvalWeights weights = new EvalWeights();

    private final double testEpsilon = 0.000001;

    @Test
    public void testEvalMajorOn7th() {

        Board board = new Board("7k/2Q2R2/8/8/8/8/r7/7K w - - 0 1");
        /*
            -------k
            --Q--R--	white to move
            --------	castling rights:
            --------	no ep
            --------	fifty=0
            --------	move counter=0
            r-------
            -------K
         */

        Tuple2<Integer, Integer> score = evalMajorOn7th(weights, board, true, C7);
        assertEquals(weights.vals[MAJOR_ON_7TH_MG_IND] + weights.vals[CONNECTED_MAJORS_ON_7TH_MG_IND],
                score._1, testEpsilon);
        assertEquals(weights.vals[MAJOR_ON_7TH_EG_IND] + weights.vals[CONNECTED_MAJORS_ON_7TH_EG_IND],
                score._2, testEpsilon);

        score = evalMajorOn7th(weights, board, true, F7);
        assertEquals(weights.vals[MAJOR_ON_7TH_MG_IND] + weights.vals[CONNECTED_MAJORS_ON_7TH_MG_IND], 
                score._1, testEpsilon);
        assertEquals(weights.vals[MAJOR_ON_7TH_EG_IND] + weights.vals[CONNECTED_MAJORS_ON_7TH_EG_IND], 
                score._2, testEpsilon);

        score = evalMajorOn7th(weights, board, false, A2);
        assertEquals(weights.vals[MAJOR_ON_7TH_MG_IND], score._1, testEpsilon);
        assertEquals(weights.vals[MAJOR_ON_7TH_EG_IND], score._2, testEpsilon);

        // move the black king out from the back rank
        board.setPos("8/2Q2R2/7k/8/8/8/r7/7K w - - 0 1");

        /*
            --------
            --Q--R--	white to move
            -------k	castling rights:
            --------	no ep
            --------	fifty=0
            --------	move counter=0
            r-------
            -------K
         */

        score = evalMajorOn7th(weights, board, true, C7);
        assertEquals(0, score._1, testEpsilon);
        assertEquals(0, score._2, testEpsilon);

        score = evalMajorOn7th(weights, board, true, F7);
        assertEquals(0, score._1, testEpsilon);
        assertEquals(0, score._2, testEpsilon);
    }

    @Test
    public void testExtractMajorOn7thFeatures() {

        Board board = new Board("7k/2Q2R2/8/8/8/8/r7/7K w - - 0 1");
        /*
            -------k
            --Q--R--	white to move
            --------	castling rights:
            --------	no ep
            --------	fifty=0
            --------	move counter=0
            r-------
            -------K
         */

        double[] features = new double[weights.vals.length];
        exractMajorOn7thFeatures(features, board, true, C7, 0.8);
        assertEquals(0.8, features[MAJOR_ON_7TH_MG_IND], testEpsilon);
        assertEquals(0.2, features[MAJOR_ON_7TH_EG_IND], testEpsilon);
        assertEquals(0.8, features[CONNECTED_MAJORS_ON_7TH_MG_IND], testEpsilon);
        assertEquals(0.2, features[CONNECTED_MAJORS_ON_7TH_EG_IND], testEpsilon);

        Arrays.fill(features, 0);
        exractMajorOn7thFeatures(features, board, true, F7, 0.8);
        assertEquals(0.8, features[MAJOR_ON_7TH_MG_IND], testEpsilon);
        assertEquals(0.2, features[MAJOR_ON_7TH_EG_IND], testEpsilon);
        assertEquals(0.8, features[CONNECTED_MAJORS_ON_7TH_MG_IND], testEpsilon);
        assertEquals(0.2, features[CONNECTED_MAJORS_ON_7TH_EG_IND], testEpsilon);

        Arrays.fill(features, 0);
        exractMajorOn7thFeatures(features, board, false, A2, 0.8);
        assertEquals(-0.8, features[MAJOR_ON_7TH_MG_IND], testEpsilon);
        assertEquals(-0.2, features[MAJOR_ON_7TH_EG_IND], testEpsilon);
        assertEquals(0, features[CONNECTED_MAJORS_ON_7TH_MG_IND], testEpsilon);
        assertEquals(0, features[CONNECTED_MAJORS_ON_7TH_EG_IND], testEpsilon);

        // move the black king out from the back rank
        board.setPos("8/2Q2R2/7k/8/8/8/r7/7K w - - 0 1");

        /*
            --------
            --Q--R--	white to move
            -------k	castling rights:
            --------	no ep
            --------	fifty=0
            --------	move counter=0
            r-------
            -------K
         */

        Arrays.fill(features, 0);
        exractMajorOn7thFeatures(features, board, true, C7, 0.8);
        assertEquals(0, features[MAJOR_ON_7TH_MG_IND], testEpsilon);
        assertEquals(0, features[MAJOR_ON_7TH_EG_IND], testEpsilon);
        assertEquals(0, features[CONNECTED_MAJORS_ON_7TH_MG_IND], testEpsilon);
        assertEquals(0, features[CONNECTED_MAJORS_ON_7TH_EG_IND], testEpsilon);

        Arrays.fill(features, 0);
        exractMajorOn7thFeatures(features, board, true, F7, 0.8);
        assertEquals(0, features[MAJOR_ON_7TH_MG_IND], testEpsilon);
        assertEquals(0, features[MAJOR_ON_7TH_EG_IND], testEpsilon);
        assertEquals(0, features[CONNECTED_MAJORS_ON_7TH_MG_IND], testEpsilon);
        assertEquals(0, features[CONNECTED_MAJORS_ON_7TH_EG_IND], testEpsilon);
    }

}
