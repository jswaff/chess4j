package dev.jamesswafford.chess4j.eval;

import dev.jamesswafford.chess4j.board.Board;

import dev.jamesswafford.chess4j.board.squares.Square;
import io.vavr.Tuple2;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class EvalKnightTest {

    private final EvalWeights weights = new EvalWeights();

    private final double testEpsilon = 0.000001;

    @Test
    public void testEvalKnight() {
        Board board = new Board();

        Tuple2<Integer, Integer> score = EvalKnight.evalKnight(weights, board, Square.B1);

        int tropismMg = weights.vals[EvalWeights.KNIGHT_TROPISM_MG_IND] * Square.B1.distance(Square.E8);
        assertEquals(weights.vals[EvalWeights.KNIGHT_PST_MG_IND + Square.B1.value()] + tropismMg,(int)score._1);

        int tropismEg = weights.vals[EvalWeights.KNIGHT_TROPISM_EG_IND] * Square.B1.distance(Square.E8);
        assertEquals(weights.vals[EvalWeights.KNIGHT_PST_EG_IND + Square.B1.value()] + tropismEg, (int)score._2);

        // test the symmetry
        Tuple2<Integer, Integer> score2 = EvalKnight.evalKnight(weights, board, Square.B8);
        assertEquals((int)score._1, -(int)score2._1);
        assertEquals((int)score._2, -(int)score2._2);
    }

    @Test
    public void testEvalKnightWithOutpost() {
        Board board = new Board("r1br1k2/pp3pp1/1b4np/4P3/2pNN3/2P3B1/PP1R1PPP/3R2K1 w - - 0 1");

        Tuple2<Integer, Integer> score = EvalKnight.evalKnight(weights, board, Square.D4);
        int tropismMg = weights.vals[EvalWeights.KNIGHT_TROPISM_MG_IND] * Square.D4.distance(Square.F8);
        int outpostScore = weights.vals[EvalWeights.KNIGHT_OUTPOST_IND + Square.D4.value()] + weights.vals[EvalWeights.KNIGHT_SUPPORTED_OUTPOST_IND + Square.D4.value()];
        assertEquals(weights.vals[EvalWeights.KNIGHT_PST_MG_IND + Square.D4.value()] + tropismMg + outpostScore, (int)score._1);

        int tropismEg = weights.vals[EvalWeights.KNIGHT_TROPISM_EG_IND] * Square.D4.distance(Square.F8);
        assertEquals(weights.vals[EvalWeights.KNIGHT_PST_EG_IND + Square.D4.value()] + tropismEg + outpostScore, (int)score._2);
    }

    @Test
    public void testExtractKnightFeatures() {
        Board board = new Board();

        double[] features = new double[weights.vals.length];
        EvalKnight.extractKnightFeatures(features, board, Square.B1, 1.0);
        assertEquals(1, features[EvalWeights.KNIGHT_PST_MG_IND + Square.B1.value()], testEpsilon);
        Assert.assertEquals(Square.B1.distance(Square.E8), features[EvalWeights.KNIGHT_TROPISM_MG_IND], testEpsilon);

        // test the symmetry
        double[] features2 = new double[weights.vals.length];
        EvalKnight.extractKnightFeatures(features2, board, Square.B8, 1.0);
        assertEquals(-1, features2[EvalWeights.KNIGHT_PST_MG_IND + Square.B1.value()], testEpsilon);
        Assert.assertEquals(-Square.B8.distance(Square.E1), features2[EvalWeights.KNIGHT_TROPISM_MG_IND], testEpsilon);
    }

    @Test
    public void testExtractKnightFeatures_endGame() {
        Board board = new Board();

        double[] features = new double[weights.vals.length];
        EvalKnight.extractKnightFeatures(features, board, Square.B1, 0.0);
        assertEquals(1, features[EvalWeights.KNIGHT_PST_EG_IND + Square.B1.value()], testEpsilon);
        Assert.assertEquals(Square.B1.distance(Square.E8), features[EvalWeights.KNIGHT_TROPISM_EG_IND], testEpsilon);

        // test the symmetry
        double[] features2 = new double[weights.vals.length];
        EvalKnight.extractKnightFeatures(features2, board, Square.B8, 0.0);
        assertEquals(-1, features2[EvalWeights.KNIGHT_PST_EG_IND + Square.B1.value()], testEpsilon);
        Assert.assertEquals(-Square.B8.distance(Square.E1), features2[EvalWeights.KNIGHT_TROPISM_EG_IND], testEpsilon);
    }

    @Test
    public void testExtractKnightFeatures_outpost() {
        Board board = new Board("r1br1k2/pp3pp1/1b4np/4P3/2pNN3/2P3B1/PP1R1PPP/3R2K1 w - - 0 1");

        double[] features = new double[weights.vals.length];
        EvalKnight.extractKnightFeatures(features, board, Square.D4, 0.3); // phase doesn't really matter
        assertEquals(1, features[EvalWeights.KNIGHT_OUTPOST_IND + Square.D4.value()], testEpsilon);
        assertEquals(1, features[EvalWeights.KNIGHT_SUPPORTED_OUTPOST_IND + Square.D4.value()], testEpsilon);
    }
}
