package dev.jamesswafford.chess4j.eval;

import dev.jamesswafford.chess4j.board.Board;
import dev.jamesswafford.chess4j.io.DrawBoard;
import dev.jamesswafford.chess4j.board.squares.Square;
import io.vavr.Tuple2;
import org.junit.Test;

import static org.junit.Assert.*;

public class EvalQueenTest {

    private final EvalWeights weights = new EvalWeights();

    private final double testEpsilon = 0.000001;

    @Test
    public void testEvalQueen() {

        Board board = new Board();

        Tuple2<Integer, Integer> score = EvalQueen.evalQueen(weights, board, Square.D1);

        assertEquals(weights.vals[EvalWeights.QUEEN_PST_MG_IND + Square.D1.value()] + weights.vals[EvalWeights.QUEEN_MOBILITY_MG_IND], (int)score._1);
        assertEquals(weights.vals[EvalWeights.QUEEN_PST_EG_IND + Square.D1.value()] + weights.vals[EvalWeights.QUEEN_MOBILITY_EG_IND], (int)score._2);

        // test symmetry
        Tuple2<Integer, Integer> score2 = EvalQueen.evalQueen(weights, board, Square.D8);
        assertEquals((int)score._1, -(int)score2._1);
        assertEquals((int)score._2, -(int)score2._2);
    }

    @Test
    public void testEvalQueen_bankRankMate() {

        Board board = new Board("7k/2Q2R2/8/8/8/8/r7/7K w - - 0 1");

        Tuple2<Integer, Integer> score = EvalQueen.evalQueen(weights, board, Square.C7);

        assertEquals(weights.vals[EvalWeights.QUEEN_PST_MG_IND + Square.C7.value()] + weights.vals[EvalWeights.MAJOR_ON_7TH_MG_IND] +
                        weights.vals[EvalWeights.CONNECTED_MAJORS_ON_7TH_MG_IND] +
                        weights.vals[EvalWeights.QUEEN_MOBILITY_MG_IND + 20], (int)score._1);

        assertEquals(weights.vals[EvalWeights.QUEEN_PST_EG_IND + Square.C7.value()] + weights.vals[EvalWeights.MAJOR_ON_7TH_EG_IND] +
                weights.vals[EvalWeights.CONNECTED_MAJORS_ON_7TH_EG_IND] +
                weights.vals[EvalWeights.QUEEN_MOBILITY_EG_IND + 20], (int)score._2);
    }

    @Test
    public void testExtractQueenFeatures() {

        Board board = new Board();

        double[] features = new double[weights.vals.length];
        EvalQueen.extractQueenFeatures(features, board, Square.D1, 1.0);
        assertEquals(1, features[EvalWeights.QUEEN_PST_MG_IND + Square.D1.value()], testEpsilon);
        assertEquals(1, features[EvalWeights.QUEEN_MOBILITY_MG_IND], testEpsilon);

        // test the symmetry
        double[] features2 = new double[weights.vals.length];
        EvalQueen.extractQueenFeatures(features2, board, Square.D8, 1.0);
        assertEquals(-1, features2[EvalWeights.QUEEN_PST_MG_IND + Square.D1.value()], testEpsilon);
        assertEquals(-1, features2[EvalWeights.QUEEN_MOBILITY_MG_IND], testEpsilon);
    }

    @Test
    public void testExtractQueenFeatures_endGame() {

        Board board = new Board();

        double[] features = new double[weights.vals.length];
        EvalQueen.extractQueenFeatures(features, board, Square.D1, 0.0);
        assertEquals(1, features[EvalWeights.QUEEN_PST_EG_IND + Square.D1.value()], testEpsilon);
        assertEquals(1, features[EvalWeights.QUEEN_MOBILITY_EG_IND], testEpsilon);

        // test the symmetry
        double[] features2 = new double[weights.vals.length];
        EvalQueen.extractQueenFeatures(features2, board, Square.D8, 0.0);
        assertEquals(-1, features2[EvalWeights.QUEEN_PST_EG_IND + Square.D1.value()], testEpsilon);
        assertEquals(-1, features2[EvalWeights.QUEEN_MOBILITY_EG_IND], testEpsilon);
    }

    @Test
    public void testExtractQueenFeatures_bankRankMate() {

        Board board = new Board("7k/2Q2R2/8/8/8/8/r7/7K w - - 0 1");

        DrawBoard.drawBoard(board);

        double[] features = new double[weights.vals.length];
        EvalQueen.extractQueenFeatures(features, board, Square.C7, 0.4);
        assertEquals(0.4, features[EvalWeights.MAJOR_ON_7TH_MG_IND], testEpsilon);
        assertEquals(0.6, features[EvalWeights.MAJOR_ON_7TH_EG_IND], testEpsilon);
        assertEquals(0.4, features[EvalWeights.CONNECTED_MAJORS_ON_7TH_MG_IND], testEpsilon);
        assertEquals(0.6, features[EvalWeights.CONNECTED_MAJORS_ON_7TH_EG_IND], testEpsilon);
        assertEquals(0.4, features[EvalWeights.QUEEN_MOBILITY_MG_IND + 20], testEpsilon);
        assertEquals(0.6, features[EvalWeights.QUEEN_MOBILITY_EG_IND + 20], testEpsilon);
    }

}
