package com.jamesswafford.chess4j.tuner;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class LogisticRegressionTunerTest {

    LogisticRegressionTuner tuner;

    TunerDatasource tunerDatasource = Mockito.mock(TunerDatasource.class);

    @Before
    public void setUp() {
        tuner = new LogisticRegressionTuner(tunerDatasource);
    }

    @Ignore
    @Test
    public void optimizeTest() {
        tuner.optimize();
    }
}
