package com.jamesswafford.chess4j.eval;

import com.jamesswafford.chess4j.board.Board;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

import static com.jamesswafford.chess4j.board.squares.Square.*;
import static com.jamesswafford.chess4j.eval.EvalMajorOn7th.*;

import static com.jamesswafford.chess4j.eval.EvalWeights.*;

public class EvalMajorOn7thTest {

    private final EvalWeights weights = new EvalWeights();

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

        assertEquals(weights.vals[MAJOR_ON_7TH_IND] + weights.vals[CONNECTED_MAJORS_ON_7TH_IND],
                evalMajorOn7th(weights, board, true, C7));

        assertEquals(weights.vals[MAJOR_ON_7TH_IND], evalMajorOn7th(weights, board, true, F7));

        assertEquals(weights.vals[MAJOR_ON_7TH_IND], evalMajorOn7th(weights, board, false, A2));

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

        assertEquals(0, evalMajorOn7th(weights, board, true, C7));

        assertEquals(0, evalMajorOn7th(weights, board, true, F7));

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

        int[] features = new int[NUM_WEIGHTS];
        exractMajorOn7thFeatures(features, board, true, C7);
        assertEquals(1, features[MAJOR_ON_7TH_IND]);
        assertEquals(1, features[CONNECTED_MAJORS_ON_7TH_IND]);

        Arrays.fill(features, 0);
        exractMajorOn7thFeatures(features, board, true, F7);
        assertEquals(1, features[MAJOR_ON_7TH_IND]);
        assertEquals(0, features[CONNECTED_MAJORS_ON_7TH_IND]);

        Arrays.fill(features, 0);
        exractMajorOn7thFeatures(features, board, false, A2);
        assertEquals(-1, features[MAJOR_ON_7TH_IND]);
        assertEquals(0, features[CONNECTED_MAJORS_ON_7TH_IND]);

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
        exractMajorOn7thFeatures(features, board, true, C7);
        assertEquals(0, features[MAJOR_ON_7TH_IND]);
        assertEquals(0, features[CONNECTED_MAJORS_ON_7TH_IND]);

        Arrays.fill(features, 0);
        exractMajorOn7thFeatures(features, board, true, F7);
        assertEquals(0, features[MAJOR_ON_7TH_IND]);
        assertEquals(0, features[CONNECTED_MAJORS_ON_7TH_IND]);
    }

}
