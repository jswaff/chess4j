package dev.jamesswafford.chess4j.eval;

import dev.jamesswafford.chess4j.board.Board;

import dev.jamesswafford.chess4j.board.squares.Square;
import io.vavr.Tuple2;

import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class EvalRookTest {

    private final EvalWeights weights = new EvalWeights();

    private final double testEpsilon = 0.000001;

    @Test
    public void testEvalRook() {

        Board board = new Board();

        Tuple2<Integer, Integer> score = EvalRook.evalRook(weights, board, Square.A1);

        assertEquals(weights.vals[EvalWeights.ROOK_PST_MG_IND + Square.A1.value()] + weights.vals[EvalWeights.ROOK_MOBILITY_MG_IND], (int)score._1);
        assertEquals(weights.vals[EvalWeights.ROOK_PST_EG_IND + Square.A1.value()] + weights.vals[EvalWeights.ROOK_MOBILITY_EG_IND], (int)score._2);

        // test the symmetry
        Tuple2<Integer, Integer> score2 = EvalRook.evalRook(weights, board, Square.A8);
        assertEquals((int)score._1, -(int)score2._1);
        assertEquals((int)score._2, -(int)score2._2);
    }

    @Test
    public void testEvalRook_bankRank() {

        Board board = new Board("7k/2Q2R2/8/8/8/8/r7/7K w - - 0 1");

        Tuple2<Integer, Integer> score = EvalRook.evalRook(weights, board, Square.F7);

        assertEquals(weights.vals[EvalWeights.ROOK_PST_MG_IND + Square.F7.value()] + weights.vals[EvalWeights.MAJOR_ON_7TH_MG_IND] +
                        weights.vals[EvalWeights.CONNECTED_MAJORS_ON_7TH_MG_IND] +
                        weights.vals[EvalWeights.ROOK_OPEN_FILE_MG_IND] + weights.vals[EvalWeights.ROOK_MOBILITY_MG_IND + 11],
                (int)score._1);

        assertEquals(weights.vals[EvalWeights.ROOK_PST_EG_IND + Square.F7.value()] + weights.vals[EvalWeights.MAJOR_ON_7TH_EG_IND] +
                        weights.vals[EvalWeights.CONNECTED_MAJORS_ON_7TH_EG_IND] +
                        weights.vals[EvalWeights.ROOK_OPEN_FILE_EG_IND] + weights.vals[EvalWeights.ROOK_MOBILITY_EG_IND + 11],
                (int)score._2);
    }

    @Test
    public void testEvalRook_rookOpenFile() {

        Board board = new Board("3r3k/8/8/8/8/8/8/7K b - - 0 1");

        Tuple2<Integer, Integer> score = EvalRook.evalRook(weights, board, Square.D8);

        assertEquals(-(weights.vals[EvalWeights.ROOK_PST_MG_IND + Square.D1.value()] + weights.vals[EvalWeights.ROOK_OPEN_FILE_MG_IND] + weights.vals[EvalWeights.ROOK_MOBILITY_MG_IND + 13]),
                (int)score._1);

        assertEquals(-(weights.vals[EvalWeights.ROOK_PST_EG_IND + Square.D1.value()] + weights.vals[EvalWeights.ROOK_OPEN_FILE_EG_IND] + weights.vals[EvalWeights.ROOK_MOBILITY_EG_IND + 13]),
                (int)score._2);
    }

    @Ignore
    @Test
    public void testEvalRook_rookOpenFileSupported() {
        Board board = new Board("7k/8/8/8/8/3R4/8/3R3K w - - 0 1");

        Tuple2<Integer, Integer> score = EvalRook.evalRook(weights, board, Square.D1);

        assertEquals(weights.vals[EvalWeights.ROOK_PST_MG_IND + Square.D1.value()] + weights.vals[EvalWeights.ROOK_OPEN_FILE_MG_IND] + weights.vals[EvalWeights.ROOK_OPEN_FILE_SUPPORTED_MG_IND] +
                 weights.vals[EvalWeights.ROOK_MOBILITY_MG_IND + 7],
                (int)score._1);

        assertEquals(weights.vals[EvalWeights.ROOK_PST_EG_IND + Square.D1.value()] + weights.vals[EvalWeights.ROOK_OPEN_FILE_EG_IND] + weights.vals[EvalWeights.ROOK_OPEN_FILE_SUPPORTED_EG_IND] +
                weights.vals[EvalWeights.ROOK_MOBILITY_EG_IND + 7],
                (int)score._2);
    }

    @Test
    public void testEvalRook_rookOpenHalfOpenFile() {

        // friendly pawn but no enemy -- not half open (or open)
        Board board = new Board("8/2P5/8/2R5/K7/8/7k/8 w - - 0 1");

        Tuple2<Integer, Integer> score = EvalRook.evalRook(weights, board, Square.C5);

        assertEquals(weights.vals[EvalWeights.ROOK_PST_MG_IND + Square.C5.value()] + weights.vals[EvalWeights.ROOK_MOBILITY_MG_IND + 12], (int)score._1);

        // enemy pawn on C makes it half open
        board.setPos("8/2p5/8/2R5/K7/8/7k/8 w - - 0 1");

        Tuple2<Integer, Integer> score2 = EvalRook.evalRook(weights, board, Square.C5);

        assertEquals(weights.vals[EvalWeights.ROOK_PST_MG_IND + Square.C5.value()] + weights.vals[EvalWeights.ROOK_HALF_OPEN_FILE_MG_IND] + weights.vals[EvalWeights.ROOK_MOBILITY_MG_IND + 12],
                (int)score2._1);
        assertEquals(weights.vals[EvalWeights.ROOK_PST_EG_IND + Square.C5.value()] + weights.vals[EvalWeights.ROOK_HALF_OPEN_FILE_EG_IND] + weights.vals[EvalWeights.ROOK_MOBILITY_EG_IND + 12],
                (int)score2._2);
    }

    @Test
    public void testExtractRookFeatures() {

        Board board = new Board();

        double[] features = new double[weights.vals.length];
        EvalRook.extractRookFeatures(features, board, Square.A1, 1.0);
        assertEquals(1, features[EvalWeights.ROOK_PST_MG_IND + Square.A1.value()], testEpsilon);
        assertEquals(1, features[EvalWeights.ROOK_MOBILITY_MG_IND], testEpsilon);

        // test the symmetry
        double[] features2 = new double[weights.vals.length];
        EvalRook.extractRookFeatures(features2, board, Square.A8, 1.0);
        assertEquals(-1, features2[EvalWeights.ROOK_PST_MG_IND + Square.A1.value()], testEpsilon);
        assertEquals(-1, features2[EvalWeights.ROOK_MOBILITY_MG_IND], testEpsilon);
    }

    @Test
    public void testExtractRookFeatures_endGame() {

        Board board = new Board();

        double[] features = new double[weights.vals.length];
        EvalRook.extractRookFeatures(features, board, Square.A1, 0.0);
        assertEquals(1, features[EvalWeights.ROOK_PST_EG_IND + Square.A1.value()], testEpsilon);
        assertEquals(1, features[EvalWeights.ROOK_MOBILITY_EG_IND], testEpsilon);

        // test the symmetry
        double[] features2 = new double[weights.vals.length];
        EvalRook.extractRookFeatures(features2, board, Square.A8, 0.0);
        assertEquals(-1, features2[EvalWeights.ROOK_PST_EG_IND + Square.A1.value()], testEpsilon);
        assertEquals(-1, features2[EvalWeights.ROOK_MOBILITY_EG_IND], testEpsilon);
    }

    @Test
    public void testExtractRookFeatures_bankRank() {

        Board board = new Board("7k/2Q2R2/8/8/8/8/r7/7K w - - 0 1");

        double[] features = new double[weights.vals.length];
        EvalRook.extractRookFeatures(features, board, Square.F7, 0.8);
        assertEquals(0.8, features[EvalWeights.MAJOR_ON_7TH_MG_IND], testEpsilon);
        assertEquals(0.8, features[EvalWeights.CONNECTED_MAJORS_ON_7TH_MG_IND], testEpsilon);
        assertEquals(0.2, features[EvalWeights.MAJOR_ON_7TH_EG_IND], testEpsilon);
        assertEquals(0.2, features[EvalWeights.CONNECTED_MAJORS_ON_7TH_EG_IND], testEpsilon);
        assertEquals(0.8, features[EvalWeights.ROOK_OPEN_FILE_MG_IND], testEpsilon);
        assertEquals(0.2, features[EvalWeights.ROOK_OPEN_FILE_EG_IND], testEpsilon);
        assertEquals(0.8, features[EvalWeights.ROOK_MOBILITY_MG_IND + 11], testEpsilon);
        assertEquals(0.2, features[EvalWeights.ROOK_MOBILITY_EG_IND + 11], testEpsilon);

    }

    @Test
    public void testExtractRookFeatures_rookOpenFile() {

        Board board = new Board("3r3k/8/8/8/8/8/8/7K b - - 0 1");

        double[] features = new double[weights.vals.length];
        EvalRook.extractRookFeatures(features, board, Square.D8, 0.8);
        assertEquals(-0.8, features[EvalWeights.ROOK_OPEN_FILE_MG_IND], testEpsilon);
        assertEquals(-0.2, features[EvalWeights.ROOK_OPEN_FILE_EG_IND], testEpsilon);
        assertEquals(-0.8, features[EvalWeights.ROOK_MOBILITY_MG_IND + 13], testEpsilon);
        assertEquals(-0.2, features[EvalWeights.ROOK_MOBILITY_EG_IND + 13], testEpsilon);
    }

    @Ignore
    @Test
    public void testExtractRookFeatures_rookOpenFileSupported() {
        Board board = new Board("7k/8/8/8/8/3R4/8/3R3K w - - 0 1");

        double[] features = new double[weights.vals.length];
        EvalRook.extractRookFeatures(features, board, Square.D1, 0.8);
        assertEquals(0.8, features[EvalWeights.ROOK_OPEN_FILE_MG_IND], testEpsilon);
        assertEquals(0.2, features[EvalWeights.ROOK_OPEN_FILE_EG_IND], testEpsilon);
        assertEquals(0.8, features[EvalWeights.ROOK_OPEN_FILE_SUPPORTED_MG_IND], testEpsilon);
        assertEquals(0.2, features[EvalWeights.ROOK_OPEN_FILE_SUPPORTED_EG_IND], testEpsilon);
        assertEquals(0.8, features[EvalWeights.ROOK_MOBILITY_MG_IND + 7], testEpsilon);
        assertEquals(0.2, features[EvalWeights.ROOK_MOBILITY_EG_IND + 7], testEpsilon);
    }

    @Test
    public void testExtractRookFeatures_rookOpenHalfOpenFile() {

        // friendly pawn but no enemy -- not half open (or open)
        Board board = new Board("8/2P5/8/2R5/K7/8/7k/8 w - - 0 1");

        double[] features = new double[weights.vals.length];
        EvalRook.extractRookFeatures(features, board, Square.C5, 0.8);
        assertEquals(0, features[EvalWeights.ROOK_OPEN_FILE_MG_IND], testEpsilon);
        assertEquals(0, features[EvalWeights.ROOK_OPEN_FILE_EG_IND], testEpsilon);
        assertEquals(0, features[EvalWeights.ROOK_HALF_OPEN_FILE_MG_IND], testEpsilon);
        assertEquals(0, features[EvalWeights.ROOK_HALF_OPEN_FILE_EG_IND], testEpsilon);
        assertEquals(0.8, features[EvalWeights.ROOK_MOBILITY_MG_IND + 12], testEpsilon);
        assertEquals(0.2, features[EvalWeights.ROOK_MOBILITY_EG_IND + 12], testEpsilon);

        // enemy pawn on C makes it half open
        board.setPos("8/2p5/8/2R5/K7/8/7k/8 w - - 0 1");

        Arrays.fill(features, 0);
        EvalRook.extractRookFeatures(features, board, Square.C5, 0.8);
        assertEquals(0, features[EvalWeights.ROOK_OPEN_FILE_MG_IND], testEpsilon);
        assertEquals(0, features[EvalWeights.ROOK_OPEN_FILE_EG_IND], testEpsilon);
        assertEquals(0.8, features[EvalWeights.ROOK_HALF_OPEN_FILE_MG_IND], testEpsilon);
        assertEquals(0.2, features[EvalWeights.ROOK_HALF_OPEN_FILE_EG_IND], testEpsilon);
        assertEquals(0.8, features[EvalWeights.ROOK_MOBILITY_MG_IND + 12], testEpsilon);
        assertEquals(0.2, features[EvalWeights.ROOK_MOBILITY_EG_IND + 12], testEpsilon);
    }

}
