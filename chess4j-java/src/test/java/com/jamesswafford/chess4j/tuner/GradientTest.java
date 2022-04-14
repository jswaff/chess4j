package com.jamesswafford.chess4j.tuner;

import org.ejml.simple.SimpleMatrix;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class GradientTest {

    @Test
    public void simpleGradientTest() {
        double[] theta = new double[] { -2, -1, 1, 2 };

        double[][] X = new double[][] {
                { 1.0, 0.1, 0.6, 1.1 },
                { 1.0, 0.2, 0.7, 1.2 },
                { 1.0, 0.3, 0.8, 1.3 },
                { 1.0, 0.4, 0.9, 1.4 },
                { 1.0, 0.5, 1.0, 1.5 }
        };

        double[] y = new double[] { 1, 0, 1, 0, 1 };

        SimpleMatrix thetaMatrix = new SimpleMatrix(theta.length, 1);
        for (int i=0;i<theta.length;i++) {
            thetaMatrix.set(i, 0, theta[i]);
        }

        SimpleMatrix xMatrix = new SimpleMatrix(X);

        SimpleMatrix yMatrix = new SimpleMatrix(y.length, 1);
        for (int i=0;i<y.length;i++) {
            yMatrix.set(i, 0, y[i]);
        }

        SimpleMatrix gradient = Gradient.gradient(xMatrix, yMatrix, thetaMatrix, Hypothesis::classicSigmoid);

        assertDoubleEquals(gradient.get(0, 0), 0.146561);
        assertDoubleEquals(gradient.get(1, 0), 0.051442);
        assertDoubleEquals(gradient.get(2, 0), 0.124722);
        assertDoubleEquals(gradient.get(3, 0), 0.198003);

        /*
                with regularization
            fprintf(' 0.146561\n -0.548558\n 0.724722\n 1.398003\n');
         */
    }

    private void assertDoubleEquals(double val, double expected) {
        double epsilon = 0.0001;
        assertTrue(val >= expected - epsilon);
        assertTrue(val <= expected + epsilon);
    }

}
