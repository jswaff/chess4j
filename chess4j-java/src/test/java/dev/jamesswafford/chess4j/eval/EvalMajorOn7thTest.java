package dev.jamesswafford.chess4j.eval;

import dev.jamesswafford.chess4j.board.Board;
import dev.jamesswafford.chess4j.board.squares.Square;
import io.vavr.Tuple2;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

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

        Tuple2<Integer, Integer> score = EvalMajorOn7th.evalMajorOn7th(weights, board, true, Square.C7);
        assertEquals(weights.vals[EvalWeights.MAJOR_ON_7TH_MG_IND] + weights.vals[EvalWeights.CONNECTED_MAJORS_ON_7TH_MG_IND],
                score._1, testEpsilon);
        assertEquals(weights.vals[EvalWeights.MAJOR_ON_7TH_EG_IND] + weights.vals[EvalWeights.CONNECTED_MAJORS_ON_7TH_EG_IND],
                score._2, testEpsilon);

        score = EvalMajorOn7th.evalMajorOn7th(weights, board, true, Square.F7);
        assertEquals(weights.vals[EvalWeights.MAJOR_ON_7TH_MG_IND] + weights.vals[EvalWeights.CONNECTED_MAJORS_ON_7TH_MG_IND],
                score._1, testEpsilon);
        assertEquals(weights.vals[EvalWeights.MAJOR_ON_7TH_EG_IND] + weights.vals[EvalWeights.CONNECTED_MAJORS_ON_7TH_EG_IND],
                score._2, testEpsilon);

        score = EvalMajorOn7th.evalMajorOn7th(weights, board, false, Square.A2);
        assertEquals(weights.vals[EvalWeights.MAJOR_ON_7TH_MG_IND], score._1, testEpsilon);
        assertEquals(weights.vals[EvalWeights.MAJOR_ON_7TH_EG_IND], score._2, testEpsilon);

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

        score = EvalMajorOn7th.evalMajorOn7th(weights, board, true, Square.C7);
        assertEquals(0, score._1, testEpsilon);
        assertEquals(0, score._2, testEpsilon);

        score = EvalMajorOn7th.evalMajorOn7th(weights, board, true, Square.F7);
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
        EvalMajorOn7th.exractMajorOn7thFeatures(features, board, true, Square.C7, 0.8);
        assertEquals(0.8, features[EvalWeights.MAJOR_ON_7TH_MG_IND], testEpsilon);
        assertEquals(0.2, features[EvalWeights.MAJOR_ON_7TH_EG_IND], testEpsilon);
        assertEquals(0.8, features[EvalWeights.CONNECTED_MAJORS_ON_7TH_MG_IND], testEpsilon);
        assertEquals(0.2, features[EvalWeights.CONNECTED_MAJORS_ON_7TH_EG_IND], testEpsilon);

        Arrays.fill(features, 0);
        EvalMajorOn7th.exractMajorOn7thFeatures(features, board, true, Square.F7, 0.8);
        assertEquals(0.8, features[EvalWeights.MAJOR_ON_7TH_MG_IND], testEpsilon);
        assertEquals(0.2, features[EvalWeights.MAJOR_ON_7TH_EG_IND], testEpsilon);
        assertEquals(0.8, features[EvalWeights.CONNECTED_MAJORS_ON_7TH_MG_IND], testEpsilon);
        assertEquals(0.2, features[EvalWeights.CONNECTED_MAJORS_ON_7TH_EG_IND], testEpsilon);

        Arrays.fill(features, 0);
        EvalMajorOn7th.exractMajorOn7thFeatures(features, board, false, Square.A2, 0.8);
        assertEquals(-0.8, features[EvalWeights.MAJOR_ON_7TH_MG_IND], testEpsilon);
        assertEquals(-0.2, features[EvalWeights.MAJOR_ON_7TH_EG_IND], testEpsilon);
        assertEquals(0, features[EvalWeights.CONNECTED_MAJORS_ON_7TH_MG_IND], testEpsilon);
        assertEquals(0, features[EvalWeights.CONNECTED_MAJORS_ON_7TH_EG_IND], testEpsilon);

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
        EvalMajorOn7th.exractMajorOn7thFeatures(features, board, true, Square.C7, 0.8);
        assertEquals(0, features[EvalWeights.MAJOR_ON_7TH_MG_IND], testEpsilon);
        assertEquals(0, features[EvalWeights.MAJOR_ON_7TH_EG_IND], testEpsilon);
        assertEquals(0, features[EvalWeights.CONNECTED_MAJORS_ON_7TH_MG_IND], testEpsilon);
        assertEquals(0, features[EvalWeights.CONNECTED_MAJORS_ON_7TH_EG_IND], testEpsilon);

        Arrays.fill(features, 0);
        EvalMajorOn7th.exractMajorOn7thFeatures(features, board, true, Square.F7, 0.8);
        assertEquals(0, features[EvalWeights.MAJOR_ON_7TH_MG_IND], testEpsilon);
        assertEquals(0, features[EvalWeights.MAJOR_ON_7TH_EG_IND], testEpsilon);
        assertEquals(0, features[EvalWeights.CONNECTED_MAJORS_ON_7TH_MG_IND], testEpsilon);
        assertEquals(0, features[EvalWeights.CONNECTED_MAJORS_ON_7TH_EG_IND], testEpsilon);
    }

}
