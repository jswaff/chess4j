package com.jamesswafford.chess4j.tuner;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.eval.EvalTermsVector;
import org.junit.Test;

import static com.jamesswafford.chess4j.Constants.CHECKMATE;
import static org.junit.Assert.assertTrue;

import static com.jamesswafford.chess4j.tuner.Hypothesis.*;

public class HypothesisTest {

    @Test
    public void hypothesisTest() {
        EvalTermsVector etv = new EvalTermsVector();
        Board board = new Board();
        assertDoubleEquals(hypothesis(board, etv), 0.5);
    }

    @Test
    public void squishifyTest() {
        assertDoubleEquals(squishify(0), 0.5);
        assertDoubleEquals(squishify(50), 0.5806);
        assertDoubleEquals(squishify(100), 0.6571);
        assertDoubleEquals(squishify(-300), 0.1244);
        assertDoubleEquals(squishify(-500), 0.0372);
        assertDoubleEquals(squishify(CHECKMATE), 1);
        assertDoubleEquals(squishify(-CHECKMATE), 0);
    }

    private void assertDoubleEquals(double val, double expected) {
        double epsilon = 0.0001;
        assertTrue(val >= expected - epsilon);
        assertTrue(val <= expected + epsilon);
    }

}
