package com.jamesswafford.chess4j.tuner;

import org.junit.Before;
import org.junit.Test;

import static com.jamesswafford.chess4j.Constants.CHECKMATE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ErrorFunctionTest {

    ErrorFunction errorFunction;

    @Before
    public void setUp() {
        errorFunction = new ErrorFunction();
    }

    @Test
    public void squishify() {
        assertSquishedScore(50, 0.5806);
        assertSquishedScore(0, 0.5);
        assertSquishedScore(-500, 0.0372);
        assertSquishedScore(CHECKMATE, 1);
        assertSquishedScore(-CHECKMATE, 0);
    }

    private void assertSquishedScore(int rawScore, double expected) {
        double squishedScore = errorFunction.squishify(rawScore);
        System.out.println(squishedScore);
        assertTrue(squishedScore >= expected - 0.0001);
        assertTrue(squishedScore <= expected + 0.0001);
    }
}
