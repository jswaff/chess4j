package com.jamesswafford.chess4j.tuner;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.eval.EvalWeights;
import org.junit.Test;

import static com.jamesswafford.chess4j.Constants.CHECKMATE;
import static org.junit.Assert.assertTrue;

import static com.jamesswafford.chess4j.tuner.Hypothesis.*;

public class HypothesisTest {

    @Test
    public void hypothesisTest() {
        EvalWeights weights = new EvalWeights();
        Board board = new Board();
        assertDoubleEquals(hypothesis(board, weights), 0.5);

        // one queen on the board should heavily favor white
        board = new Board("3k4/3Q4/3K4/8/8/8/8/8 w - -");
        double h = hypothesis(board, weights);
        assertTrue(h > 0.99);

        // even on blacks turn the hypothesis should be from white's perspective
        board = new Board("3k4/3Q4/3K4/8/8/8/8/8 b - -");
        double h2 = hypothesis(board, weights);
        assertTrue(h2 > 0.99);

        assertDoubleEquals(h, h2);
    }

    @Test
    public void sigmoidTest() {
        assertDoubleEquals(Hypothesis.texelSigmoid(0), 0.5);

        double e50 = Hypothesis.texelSigmoid(50);
        assertTrue(e50 > 0.5);
        assertTrue(Hypothesis.texelSigmoid(100) > e50);

        double eNeg300 = Hypothesis.texelSigmoid(-300);
        assertTrue(eNeg300 < 0.5);
        assertTrue(Hypothesis.texelSigmoid(-500) < eNeg300);

        assertDoubleEquals(Hypothesis.texelSigmoid(CHECKMATE), 1);
        assertDoubleEquals(Hypothesis.texelSigmoid(-CHECKMATE), 0);
    }

    private void assertDoubleEquals(double val, double expected) {
        double epsilon = 0.0001;
        assertTrue(val >= expected - epsilon);
        assertTrue(val <= expected + epsilon);
    }

}
