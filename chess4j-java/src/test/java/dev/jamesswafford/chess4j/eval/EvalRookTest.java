package dev.jamesswafford.chess4j.eval;

import dev.jamesswafford.chess4j.board.Board;

import io.vavr.Tuple2;

import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

import static dev.jamesswafford.chess4j.board.squares.Square.*;
import static dev.jamesswafford.chess4j.eval.EvalRook.*;

import static dev.jamesswafford.chess4j.eval.EvalWeights.*;

public class EvalRookTest {

    private final EvalWeights weights = new EvalWeights();

    private final double testEpsilon = 0.000001;

    @Test
    public void testEvalRook() {

        Board board = new Board();

        Tuple2<Integer, Integer> score = evalRook(weights, board, A1);

        assertEquals(weights.vals[ROOK_PST_MG_IND + A1.value()] + weights.vals[ROOK_MOBILITY_MG_IND], (int)score._1);
        assertEquals(weights.vals[ROOK_PST_EG_IND + A1.value()] + weights.vals[ROOK_MOBILITY_EG_IND], (int)score._2);

        // test the symmetry
        Tuple2<Integer, Integer> score2 = evalRook(weights, board, A8);
        assertEquals((int)score._1, -(int)score2._1);
        assertEquals((int)score._2, -(int)score2._2);
    }

    @Test
    public void testEvalRook_bankRank() {

        Board board = new Board("7k/2Q2R2/8/8/8/8/r7/7K w - - 0 1");

        Tuple2<Integer, Integer> score = evalRook(weights, board, F7);

        assertEquals(weights.vals[ROOK_PST_MG_IND + F7.value()] + weights.vals[MAJOR_ON_7TH_MG_IND] +
                        weights.vals[CONNECTED_MAJORS_ON_7TH_MG_IND] +
                        weights.vals[ROOK_OPEN_FILE_MG_IND] + weights.vals[ROOK_MOBILITY_MG_IND + 11],
                (int)score._1);

        assertEquals(weights.vals[ROOK_PST_EG_IND + F7.value()] + weights.vals[MAJOR_ON_7TH_EG_IND] +
                        weights.vals[CONNECTED_MAJORS_ON_7TH_EG_IND] +
                        weights.vals[ROOK_OPEN_FILE_EG_IND] + weights.vals[ROOK_MOBILITY_EG_IND + 11],
                (int)score._2);
    }

    @Test
    public void testEvalRook_rookOpenFile() {

        Board board = new Board("3r3k/8/8/8/8/8/8/7K b - - 0 1");

        Tuple2<Integer, Integer> score = evalRook(weights, board, D8);

        assertEquals(-(weights.vals[ROOK_PST_MG_IND + D1.value()] + weights.vals[ROOK_OPEN_FILE_MG_IND] + weights.vals[ROOK_MOBILITY_MG_IND + 13]),
                (int)score._1);

        assertEquals(-(weights.vals[ROOK_PST_EG_IND + D1.value()] + weights.vals[ROOK_OPEN_FILE_EG_IND] + weights.vals[ROOK_MOBILITY_EG_IND + 13]),
                (int)score._2);
    }

    @Ignore
    @Test
    public void testEvalRook_rookOpenFileSupported() {
        Board board = new Board("7k/8/8/8/8/3R4/8/3R3K w - - 0 1");

        Tuple2<Integer, Integer> score = evalRook(weights, board, D1);

        assertEquals(weights.vals[ROOK_PST_MG_IND + D1.value()] + weights.vals[ROOK_OPEN_FILE_MG_IND] + weights.vals[ROOK_OPEN_FILE_SUPPORTED_MG_IND] +
                 weights.vals[ROOK_MOBILITY_MG_IND + 7],
                (int)score._1);

        assertEquals(weights.vals[ROOK_PST_EG_IND + D1.value()] + weights.vals[ROOK_OPEN_FILE_EG_IND] + weights.vals[ROOK_OPEN_FILE_SUPPORTED_EG_IND] + 
                weights.vals[ROOK_MOBILITY_EG_IND + 7],
                (int)score._2);
    }

    @Test
    public void testEvalRook_rookOpenHalfOpenFile() {

        // friendly pawn but no enemy -- not half open (or open)
        Board board = new Board("8/2P5/8/2R5/K7/8/7k/8 w - - 0 1");

        Tuple2<Integer, Integer> score = evalRook(weights, board, C5);

        assertEquals(weights.vals[ROOK_PST_MG_IND + C5.value()] + weights.vals[ROOK_MOBILITY_MG_IND + 12], (int)score._1);

        // enemy pawn on C makes it half open
        board.setPos("8/2p5/8/2R5/K7/8/7k/8 w - - 0 1");

        Tuple2<Integer, Integer> score2 = evalRook(weights, board, C5);

        assertEquals(weights.vals[ROOK_PST_MG_IND + C5.value()] + weights.vals[ROOK_HALF_OPEN_FILE_MG_IND] + weights.vals[ROOK_MOBILITY_MG_IND + 12],
                (int)score2._1);
        assertEquals(weights.vals[ROOK_PST_EG_IND + C5.value()] + weights.vals[ROOK_HALF_OPEN_FILE_EG_IND] + weights.vals[ROOK_MOBILITY_EG_IND + 12],
                (int)score2._2);
    }

    @Test
    public void testExtractRookFeatures() {

        Board board = new Board();

        double[] features = new double[weights.vals.length];
        extractRookFeatures(features, board, A1, 1.0);
        assertEquals(1, features[ROOK_PST_MG_IND + A1.value()], testEpsilon);
        assertEquals(1, features[ROOK_MOBILITY_MG_IND], testEpsilon);

        // test the symmetry
        double[] features2 = new double[weights.vals.length];
        extractRookFeatures(features2, board, A8, 1.0);
        assertEquals(-1, features2[ROOK_PST_MG_IND + A1.value()], testEpsilon);
        assertEquals(-1, features2[ROOK_MOBILITY_MG_IND], testEpsilon);
    }

    @Test
    public void testExtractRookFeatures_endGame() {

        Board board = new Board();

        double[] features = new double[weights.vals.length];
        extractRookFeatures(features, board, A1, 0.0);
        assertEquals(1, features[ROOK_PST_EG_IND + A1.value()], testEpsilon);
        assertEquals(1, features[ROOK_MOBILITY_EG_IND], testEpsilon);

        // test the symmetry
        double[] features2 = new double[weights.vals.length];
        extractRookFeatures(features2, board, A8, 0.0);
        assertEquals(-1, features2[ROOK_PST_EG_IND + A1.value()], testEpsilon);
        assertEquals(-1, features2[ROOK_MOBILITY_EG_IND], testEpsilon);
    }

    @Test
    public void testExtractRookFeatures_bankRank() {

        Board board = new Board("7k/2Q2R2/8/8/8/8/r7/7K w - - 0 1");

        double[] features = new double[weights.vals.length];
        extractRookFeatures(features, board, F7, 0.8);
        assertEquals(0.8, features[MAJOR_ON_7TH_MG_IND], testEpsilon);
        assertEquals(0.8, features[CONNECTED_MAJORS_ON_7TH_MG_IND], testEpsilon);
        assertEquals(0.2, features[MAJOR_ON_7TH_EG_IND], testEpsilon);
        assertEquals(0.2, features[CONNECTED_MAJORS_ON_7TH_EG_IND], testEpsilon);
        assertEquals(0.8, features[ROOK_OPEN_FILE_MG_IND], testEpsilon);
        assertEquals(0.2, features[ROOK_OPEN_FILE_EG_IND], testEpsilon);
        assertEquals(0.8, features[ROOK_MOBILITY_MG_IND + 11], testEpsilon);
        assertEquals(0.2, features[ROOK_MOBILITY_EG_IND + 11], testEpsilon);

    }

    @Test
    public void testExtractRookFeatures_rookOpenFile() {

        Board board = new Board("3r3k/8/8/8/8/8/8/7K b - - 0 1");

        double[] features = new double[weights.vals.length];
        extractRookFeatures(features, board, D8, 0.8);
        assertEquals(-0.8, features[ROOK_OPEN_FILE_MG_IND], testEpsilon);
        assertEquals(-0.2, features[ROOK_OPEN_FILE_EG_IND], testEpsilon);
        assertEquals(-0.8, features[ROOK_MOBILITY_MG_IND + 13], testEpsilon);
        assertEquals(-0.2, features[ROOK_MOBILITY_EG_IND + 13], testEpsilon);
    }

    @Ignore
    @Test
    public void testExtractRookFeatures_rookOpenFileSupported() {
        Board board = new Board("7k/8/8/8/8/3R4/8/3R3K w - - 0 1");

        double[] features = new double[weights.vals.length];
        extractRookFeatures(features, board, D1, 0.8);
        assertEquals(0.8, features[ROOK_OPEN_FILE_MG_IND], testEpsilon);
        assertEquals(0.2, features[ROOK_OPEN_FILE_EG_IND], testEpsilon);
        assertEquals(0.8, features[ROOK_OPEN_FILE_SUPPORTED_MG_IND], testEpsilon);
        assertEquals(0.2, features[ROOK_OPEN_FILE_SUPPORTED_EG_IND], testEpsilon);
        assertEquals(0.8, features[ROOK_MOBILITY_MG_IND + 7], testEpsilon);
        assertEquals(0.2, features[ROOK_MOBILITY_EG_IND + 7], testEpsilon);
    }

    @Test
    public void testExtractRookFeatures_rookOpenHalfOpenFile() {

        // friendly pawn but no enemy -- not half open (or open)
        Board board = new Board("8/2P5/8/2R5/K7/8/7k/8 w - - 0 1");

        double[] features = new double[weights.vals.length];
        extractRookFeatures(features, board, C5, 0.8);
        assertEquals(0, features[ROOK_OPEN_FILE_MG_IND], testEpsilon);
        assertEquals(0, features[ROOK_OPEN_FILE_EG_IND], testEpsilon);
        assertEquals(0, features[ROOK_HALF_OPEN_FILE_MG_IND], testEpsilon);
        assertEquals(0, features[ROOK_HALF_OPEN_FILE_EG_IND], testEpsilon);
        assertEquals(0.8, features[ROOK_MOBILITY_MG_IND + 12], testEpsilon);
        assertEquals(0.2, features[ROOK_MOBILITY_EG_IND + 12], testEpsilon);

        // enemy pawn on C makes it half open
        board.setPos("8/2p5/8/2R5/K7/8/7k/8 w - - 0 1");

        Arrays.fill(features, 0);
        extractRookFeatures(features, board, C5, 0.8);
        assertEquals(0, features[ROOK_OPEN_FILE_MG_IND], testEpsilon);
        assertEquals(0, features[ROOK_OPEN_FILE_EG_IND], testEpsilon);
        assertEquals(0.8, features[ROOK_HALF_OPEN_FILE_MG_IND], testEpsilon);
        assertEquals(0.2, features[ROOK_HALF_OPEN_FILE_EG_IND], testEpsilon);
        assertEquals(0.8, features[ROOK_MOBILITY_MG_IND + 12], testEpsilon);
        assertEquals(0.2, features[ROOK_MOBILITY_EG_IND + 12], testEpsilon);
    }

}
