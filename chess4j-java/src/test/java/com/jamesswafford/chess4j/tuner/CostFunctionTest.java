package com.jamesswafford.chess4j.tuner;

import org.junit.Test;

import static com.jamesswafford.chess4j.tuner.CostFunction.*;
import static com.jamesswafford.chess4j.io.PGNResult.*;
import static org.junit.Assert.assertTrue;


public class CostFunctionTest {

    @Test
    public void calculateCostFromSquishedScore() {
        assertDoubleEquals(cost(0.5806, WHITE_WINS), 0.1759);
        assertDoubleEquals(cost(1, WHITE_WINS), 0);
        assertDoubleEquals(cost(0.5, DRAW), 0);
        assertDoubleEquals(cost(0, BLACK_WINS), 0);
        assertDoubleEquals(cost(0.5, WHITE_WINS), 0.25);
        assertDoubleEquals(cost(0.5, BLACK_WINS), 0.25);
        assertDoubleEquals(cost(1, BLACK_WINS), 1);
        assertDoubleEquals(cost(0, WHITE_WINS), 1);
        assertDoubleEquals(cost(0.31459, DRAW), 0.0344);
    }

    private void assertDoubleEquals(double val, double expected) {
        double epsilon = 0.0001;
        assertTrue(val >= expected - epsilon);
        assertTrue(val <= expected + epsilon);
    }
}
