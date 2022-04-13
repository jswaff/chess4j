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
    public void squishifyTest() {
        assertDoubleEquals(Hypothesis.hypothesis(0), 0.5);
        assertDoubleEquals(Hypothesis.hypothesis(50), 0.5806);
        assertDoubleEquals(Hypothesis.hypothesis(100), 0.6571);
        assertDoubleEquals(Hypothesis.hypothesis(-300), 0.1244);
        assertDoubleEquals(Hypothesis.hypothesis(-500), 0.0372);
        assertDoubleEquals(Hypothesis.hypothesis(CHECKMATE), 1);
        assertDoubleEquals(Hypothesis.hypothesis(-CHECKMATE), 0);
    }

    private void assertDoubleEquals(double val, double expected) {
        double epsilon = 0.0001;
        assertTrue(val >= expected - epsilon);
        assertTrue(val <= expected + epsilon);
    }

}
