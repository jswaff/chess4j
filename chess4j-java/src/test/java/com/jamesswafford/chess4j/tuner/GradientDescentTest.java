package com.jamesswafford.chess4j.tuner;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class GradientDescentTest {

    @Test
    public void gradientTest() {

        double[] theta = new double[]{-2, -1, 1, 2};

        double[][] x = new double[][]{
                {1, 0.1, 0.6, 1.1},
                {1, 0.2, 0.7, 1.2},
                {1, 0.3, 0.8, 1.3},
                {1, 0.4, 0.9, 1.4},
                {1, 0.5, 1.0, 1.5}};

        double[] y = new double[]{ 1,0,1,0,1 };

        double alpha = 1.0;

        double[] gradient = GradientDescent.batchGradientDescent(theta, x, y, alpha);

        // the outcome should look like this:
        /*
          -0.098211
          -0.029398
          -0.078504
          -0.127609
        */

        assertDoubleEquals(gradient[0], -0.098211);
        assertDoubleEquals(gradient[1], -0.029398);
        assertDoubleEquals(gradient[2], -0.078504);
        assertDoubleEquals(gradient[3], -0.127609);
    }

    private void assertDoubleEquals(double val, double expected) {
        double epsilon = 0.0001;
        assertTrue(val >= expected - epsilon);
        assertTrue(val <= expected + epsilon);
    }
}
