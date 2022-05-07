package com.jamesswafford.chess4j.eval;

import com.jamesswafford.chess4j.board.Board;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

import static com.jamesswafford.chess4j.board.squares.Square.*;
import static com.jamesswafford.chess4j.eval.EvalKing.*;

import static com.jamesswafford.chess4j.eval.EvalWeights.*;

public class EvalKingTest {

    private final EvalWeights weights = new EvalWeights();

    private final double testEpsilon = 0.000001;


    @Test
    public void testEvalKing_middleGame() {

        // as odd as this position is, it has all material on the board.
        // therefore, there should be no scaling of king safety.
        Board board = new Board("rnbq1rk1/pppppppp/bn6/8/BN6/5P2/PPPPP1PP/RNBQ1RK1 w - - 0 1");

        assertEquals(weights.vals[KING_PST_IND + G1.value()] + evalKingSafety(weights, board, true),
                evalKing(weights, board, G1, false));

        assertEquals(weights.vals[KING_PST_IND + G1.value()] + evalKingSafety(weights, board, false),
                evalKing(weights, board, G8, false));
    }

    @Test
    public void testEvalKing_endGame() {

        Board board = new Board("8/p3k3/8/8/8/8/4K3/8 w - - 0 1");

        assertEquals(weights.vals[KING_ENDGAME_PST_IND + E2.value()], evalKing(weights, board, E2, true));

        // test the symmetry
        assertEquals(evalKing(weights, board, E2, true), evalKing(weights, board, E7, true));
    }

    @Test
    public void testEvalKingSafety_middleFiles() {

        // initial position then e3... no penalty
        Board board = new Board("rnbqkbnr/pppppppp/8/8/8/4P3/PPPP1PPP/RNBQKBNR w KQkq - 0 1");

        assertEquals(0, evalKingSafety(weights, board, true));
        assertEquals(0, evalKingSafety(weights, board, false));

        // open file for both
        board.setPos("rnbqkbnr/pppp1ppp/8/8/8/8/PPPP1PPP/RNBQKBNR w KQkq - 0 1");

        assertEquals(weights.vals[KING_SAFETY_MIDDLE_OPEN_FILE_IND],
                evalKingSafety(weights, board, true));

        assertEquals(weights.vals[KING_SAFETY_MIDDLE_OPEN_FILE_IND],
                evalKingSafety(weights, board, false));

        // remove both queens.  open e file.  put black on D8
        // white should be penalized but black is not
        board.setPos("rnbk1bnr/pppp1ppp/8/8/8/8/PPPP1PPP/RNB1KBNR b KQ - 0 1");

        assertEquals(weights.vals[KING_SAFETY_MIDDLE_OPEN_FILE_IND],
                evalKingSafety(weights, board, true));
        assertEquals(0, evalKingSafety(weights, board, false));
    }

    @Test
    public void testEvalKingSafety_kingSide() {

        Board board = new Board("rnbq1rk1/pppppppp/8/8/8/8/PPPPPPPP/RNBQ1RK1 w - - 0 1");

        assertEquals(0, evalKingSafety(weights, board, true));
        assertEquals(0, evalKingSafety(weights, board, false));

        // white pawn on F3
        board.setPos("rnbq1rk1/pppppppp/8/8/8/5P2/PPPPP1PP/RNBQ1RK1 w - - 0 1");

        assertEquals(weights.vals[KING_SAFETY_PAWN_ONE_AWAY_IND],
                evalKingSafety(weights, board, true));
        assertEquals(0, evalKingSafety(weights, board, false));

        // white pawn on G4
        board.setPos("rnbq1rk1/pppppppp/8/8/6P1/8/PPPPPP1P/RNBQ1RK1 w - - 0 1");

        assertEquals(weights.vals[KING_SAFETY_PAWN_TWO_AWAY_IND],
                evalKingSafety(weights, board, true));
        assertEquals(0, evalKingSafety(weights, board, false));
    }

    @Test
    public void testEvalKingSafety_queenSide() {

        // pawn on C3
        Board board = new Board("1krq1bnr/pppppppp/8/8/8/2P5/PP1PPPPP/1KRQ1BNR w - - 0 1");

        assertEquals(weights.vals[KING_SAFETY_PAWN_ONE_AWAY_IND],
                evalKingSafety(weights, board, true));
        assertEquals(0, evalKingSafety(weights, board, false));

        // white pawn on B4
        board.setPos("1krq1bnr/pppppppp/8/8/1P6/8/P1PPPPPP/1KRQ1BNR w - - 0 1");

        assertEquals(weights.vals[KING_SAFETY_PAWN_TWO_AWAY_IND],
                evalKingSafety(weights, board, true));
        assertEquals(0, evalKingSafety(weights, board, false));

        // black pawn on A4
        board.setPos("1krq1bnr/1ppppppp/8/8/p7/8/PPPPPPPP/1KRQ1BNR b - - 0 1");

        assertEquals(0, evalKingSafety(weights, board, true));
        assertEquals(weights.vals[KING_SAFETY_WING_PAWN_FAR_AWAY_IND],
                evalKingSafety(weights, board, false));
    }

    @Test
    public void testExtractKingFeatures_middleGame() {

        Board board = new Board("rnbq1rk1/pppppppp/bn6/8/BN6/5P2/PPPPP1PP/RNBQ1RK1 w - - 0 1");

        double[] features = new double[weights.vals.length];
        extractKingFeatures(features, board, G1, 1.0);
        assertEquals(1, features[KING_PST_IND + G1.value()], testEpsilon);
    }

    @Test
    public void testExtractKingFeatures_endGame() {

        Board board = new Board("8/p3k3/8/8/8/8/4K3/8 w - - 0 1");

        double[] features = new double[weights.vals.length];
        extractKingFeatures(features, board, E2, 0.0);
        assertEquals(1, features[KING_ENDGAME_PST_IND + E2.value()], testEpsilon);

        // test the symmetry
        double[] features2 = new double[weights.vals.length];
        extractKingFeatures(features2, board, E7, 0.0);
        assertEquals(-1, features2[KING_ENDGAME_PST_IND + E2.value()], testEpsilon);
    }

    @Test
    public void testExtractKingSafetyFeatures_middleFiles() {

        // initial position then e3... no penalty
        Board board = new Board("rnbqkbnr/pppppppp/8/8/8/4P3/PPPP1PPP/RNBQKBNR w KQkq - 0 1");

        double[] features = new double[weights.vals.length];
        extractKingSafetyFeatures(features, board, true, 1.0);
        for (double feature : features) assertEquals(0, feature, testEpsilon);

        extractKingSafetyFeatures(features, board, false, 1.0);
        for (double feature : features) assertEquals(0, feature, testEpsilon);

        // open file for both
        board.setPos("rnbqkbnr/pppp1ppp/8/8/8/8/PPPP1PPP/RNBQKBNR w KQkq - 0 1");

        extractKingSafetyFeatures(features, board, true, 1.0);
        assertEquals(1, features[KING_SAFETY_MIDDLE_OPEN_FILE_IND], testEpsilon);

        Arrays.fill(features, 0);
        extractKingSafetyFeatures(features, board, false, 1.0);
        assertEquals(-1, features[KING_SAFETY_MIDDLE_OPEN_FILE_IND], testEpsilon);

        // remove both queens.  open e file.  put black on D8
        // white should be penalized but black is not
        board.setPos("rnbk1bnr/pppp1ppp/8/8/8/8/PPPP1PPP/RNB1KBNR b KQ - 0 1");

        Arrays.fill(features, 0);
        extractKingSafetyFeatures(features, board, true, 1.0);
        assertEquals(1, features[KING_SAFETY_MIDDLE_OPEN_FILE_IND], testEpsilon);

        Arrays.fill(features, 0);
        extractKingSafetyFeatures(features, board, false, 1.0);
        assertEquals(0, features[KING_SAFETY_MIDDLE_OPEN_FILE_IND], testEpsilon);
    }

    @Test
    public void testExtractKingSafetyFeatures_kingSide() {

        Board board = new Board("rnbq1rk1/pppppppp/8/8/8/8/PPPPPPPP/RNBQ1RK1 w - - 0 1");

        double[] features = new double[weights.vals.length];
        extractKingSafetyFeatures(features, board, true, 1.0);
        for (double feature : features) assertEquals(0, feature, testEpsilon);

        extractKingSafetyFeatures(features, board, false, 1.0);
        for (double feature : features) assertEquals(0, feature, testEpsilon);

        // white pawn on F3
        board.setPos("rnbq1rk1/pppppppp/8/8/8/5P2/PPPPP1PP/RNBQ1RK1 w - - 0 1");

        extractKingSafetyFeatures(features, board, true, 1.0);
        assertEquals(1, features[KING_SAFETY_PAWN_ONE_AWAY_IND], testEpsilon);

        Arrays.fill(features, 0);
        extractKingSafetyFeatures(features, board, false, 1.0);
        for (double feature : features) assertEquals(0, feature, testEpsilon);

        // white pawn on G4
        board.setPos("rnbq1rk1/pppppppp/8/8/6P1/8/PPPPPP1P/RNBQ1RK1 w - - 0 1");

        Arrays.fill(features, 0);
        extractKingSafetyFeatures(features, board, true, 1.0);
        assertEquals(1, features[KING_SAFETY_PAWN_TWO_AWAY_IND], testEpsilon);

        Arrays.fill(features, 0);
        extractKingSafetyFeatures(features, board, false, 1.0);
        for (double feature : features) assertEquals(0, feature, testEpsilon);
    }

    @Test
    public void testExtractKingSafetyFeatures_queenSide() {

        // pawn on C3
        Board board = new Board("1krq1bnr/pppppppp/8/8/8/2P5/PP1PPPPP/1KRQ1BNR w - - 0 1");

        double[] features = new double[weights.vals.length];
        extractKingSafetyFeatures(features, board, true, 1.0);
        assertEquals(1, features[KING_SAFETY_PAWN_ONE_AWAY_IND], testEpsilon);

        Arrays.fill(features, 0);
        extractKingSafetyFeatures(features, board, false, 1.0);
        for (double feature : features) assertEquals(0, feature, testEpsilon);

        // white pawn on B4
        board.setPos("1krq1bnr/pppppppp/8/8/1P6/8/P1PPPPPP/1KRQ1BNR w - - 0 1");

        Arrays.fill(features, 0);
        extractKingSafetyFeatures(features, board, true, 1.0);
        assertEquals(1, features[KING_SAFETY_PAWN_TWO_AWAY_IND], testEpsilon);

        Arrays.fill(features, 0);
        extractKingSafetyFeatures(features, board, false, 1.0);
        for (double feature : features) assertEquals(0, feature, testEpsilon);

        // black pawn on A4
        board.setPos("1krq1bnr/1ppppppp/8/8/p7/8/PPPPPPPP/1KRQ1BNR b - - 0 1");

        Arrays.fill(features, 0);
        extractKingSafetyFeatures(features, board, true, 1.0);
        for (double feature : features) assertEquals(0, feature, testEpsilon);

        extractKingSafetyFeatures(features, board, false, 1.0);
        assertEquals(-1, features[KING_SAFETY_WING_PAWN_FAR_AWAY_IND], testEpsilon);
    }

}
