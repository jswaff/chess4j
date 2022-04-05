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
    public void optimizeTest() {
        tuner.optimize(new ArrayList<>(), 3);
    }
}
