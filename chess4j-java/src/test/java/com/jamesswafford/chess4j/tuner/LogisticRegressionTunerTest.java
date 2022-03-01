package com.jamesswafford.chess4j.tuner;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertTrue;

public class LogisticRegressionTunerTest {

    Tuner tuner;

    TunerDatasource tunerDatasource = Mockito.mock(TunerDatasource.class);

    @Before
    public void setUp() {
        tuner = new Tuner(tunerDatasource);
    }

    @Test
    public void optimizeTest() {
        tuner.optimize();
    }
}
