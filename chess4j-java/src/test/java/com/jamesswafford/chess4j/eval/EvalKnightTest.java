package com.jamesswafford.chess4j.eval;

import com.jamesswafford.chess4j.board.Board;

import io.vavr.Tuple2;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

import static com.jamesswafford.chess4j.board.squares.Square.*;
import static com.jamesswafford.chess4j.eval.EvalKnight.*;

import static com.jamesswafford.chess4j.eval.EvalWeights.*;

public class EvalKnightTest {

    private final EvalWeights weights = new EvalWeights();

    private final double testEpsilon = 0.000001;

    @Test
    public void testEvalKnight() {
        Board board = new Board();

        Tuple2<Integer, Integer> score = evalKnight(weights, board, B1);

        int tropismMg = weights.vals[KNIGHT_TROPISM_MG_IND] * B1.distance(E8);
        assertEquals(weights.vals[KNIGHT_PST_MG_IND + B1.value()] /*+ tropismMg*/,(int)score._1);

        int tropismEg = weights.vals[KNIGHT_TROPISM_EG_IND] * B1.distance(E8);
        assertEquals(weights.vals[KNIGHT_PST_EG_IND + B1.value()] /*+ tropismEg*/, (int)score._2);

        // test the symmetry
        Tuple2<Integer, Integer> score2 = evalKnight(weights, board, B8);
        assertEquals((int)score._1, -(int)score2._1);
        assertEquals((int)score._2, -(int)score2._2);
    }

    @Test
    public void testEvalKnightWithOutpost() {
        Board board = new Board("r1br1k2/pp3pp1/1b4np/4P3/2pNN3/2P3B1/PP1R1PPP/3R2K1 w - - 0 1");

        Tuple2<Integer, Integer> score = evalKnight(weights, board, D4);
        int tropismMg = weights.vals[KNIGHT_TROPISM_MG_IND] * D4.distance(F8);
        int outpostScore = weights.vals[KNIGHT_OUTPOST_IND + D4.value()] + weights.vals[KNIGHT_SUPPORTED_OUTPOST_IND + D4.value()];
        assertEquals(weights.vals[KNIGHT_PST_MG_IND + D4.value()] /*+ tropismMg + outpostScore*/, (int)score._1);

        int tropismEg = weights.vals[KNIGHT_TROPISM_EG_IND] * D4.distance(F8);
        assertEquals(weights.vals[KNIGHT_PST_EG_IND + D4.value()] /*+ tropismEg + outpostScore*/, (int)score._2);
    }

    @Test
    public void testExtractKnightFeatures() {
        Board board = new Board();

        double[] features = new double[weights.vals.length];
        extractKnightFeatures(features, board, B1, 1.0);
        assertEquals(1, features[KNIGHT_PST_MG_IND + B1.value()], testEpsilon);
//        assertEquals(B1.distance(E8), features[KNIGHT_TROPISM_MG_IND], testEpsilon);

        // test the symmetry
        double[] features2 = new double[weights.vals.length];
        extractKnightFeatures(features2, board, B8, 1.0);
        assertEquals(-1, features2[KNIGHT_PST_MG_IND + B1.value()], testEpsilon);
//        assertEquals(-B8.distance(E1), features2[KNIGHT_TROPISM_MG_IND], testEpsilon);
    }

    @Ignore
    @Test
    public void testExtractKnightFeatures_endGame() {
        Board board = new Board();

        double[] features = new double[weights.vals.length];
        extractKnightFeatures(features, board, B1, 0.0);
        assertEquals(1, features[KNIGHT_PST_EG_IND + B1.value()], testEpsilon);
//        assertEquals(B1.distance(E8), features[KNIGHT_TROPISM_EG_IND], testEpsilon);

        // test the symmetry
        double[] features2 = new double[weights.vals.length];
        extractKnightFeatures(features2, board, B8, 0.0);
        assertEquals(-1, features2[KNIGHT_PST_EG_IND + B1.value()], testEpsilon);
//        assertEquals(-B8.distance(E1), features2[KNIGHT_TROPISM_EG_IND], testEpsilon);
    }

    @Test
    public void testExtractKnightFeatures_outpost() {
        Board board = new Board("r1br1k2/pp3pp1/1b4np/4P3/2pNN3/2P3B1/PP1R1PPP/3R2K1 w - - 0 1");

        double[] features = new double[weights.vals.length];
        extractKnightFeatures(features, board, D4, 0.3); // phase doesn't really matter
//        assertEquals(1, features[KNIGHT_OUTPOST_IND + D4.value()], testEpsilon);
//        assertEquals(1, features[KNIGHT_SUPPORTED_OUTPOST_IND + D4.value()], testEpsilon);
    }
}
