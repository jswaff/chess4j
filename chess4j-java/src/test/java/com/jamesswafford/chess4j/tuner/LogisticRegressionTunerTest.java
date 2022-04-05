package com.jamesswafford.chess4j.tuner;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

public class LogisticRegressionTunerTest {

    LogisticRegressionTuner tuner;

    @Before
    public void setUp() {
        tuner = new LogisticRegressionTuner();
    }

    @Test
    public void errorShouldDecrease() {

        // get a list of game records

        // call tuner with 10, 20, 30 .. 100 iterations and verify error decreases.

        tuner.optimize(new ArrayList<>(), 3);
    }
}
