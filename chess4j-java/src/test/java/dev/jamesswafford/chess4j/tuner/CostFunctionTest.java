package dev.jamesswafford.chess4j.tuner;

import dev.jamesswafford.chess4j.io.PGNResult;
import org.junit.Test;

import static org.junit.Assert.assertTrue;


public class CostFunctionTest {

    @Test
    public void calculateCostFromSquishedScore() {
        assertDoubleEquals(CostFunction.cost(0.5806, PGNResult.WHITE_WINS), 0.1759);
        assertDoubleEquals(CostFunction.cost(1, PGNResult.WHITE_WINS), 0);
        assertDoubleEquals(CostFunction.cost(0.5, PGNResult.DRAW), 0);
        assertDoubleEquals(CostFunction.cost(0, PGNResult.BLACK_WINS), 0);
        assertDoubleEquals(CostFunction.cost(0.5, PGNResult.WHITE_WINS), 0.25);
        assertDoubleEquals(CostFunction.cost(0.5, PGNResult.BLACK_WINS), 0.25);
        assertDoubleEquals(CostFunction.cost(1, PGNResult.BLACK_WINS), 1);
        assertDoubleEquals(CostFunction.cost(0, PGNResult.WHITE_WINS), 1);
        assertDoubleEquals(CostFunction.cost(0.31459, PGNResult.DRAW), 0.0344);
    }

    private void assertDoubleEquals(double val, double expected) {
        double epsilon = 0.0001;
        assertTrue(val >= expected - epsilon);
        assertTrue(val <= expected + epsilon);
    }
}
