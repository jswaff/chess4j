package com.jamesswafford.chess4j.tuner;

import org.ejml.simple.SimpleMatrix;

import java.util.function.Function;

public class Gradient {

    public static SimpleMatrix gradient(SimpleMatrix x, SimpleMatrix y, SimpleMatrix theta, double lambda) {
        return gradient(x, y, theta, lambda, Hypothesis::texelSigmoid);
    }

    public static SimpleMatrix gradient(SimpleMatrix x, SimpleMatrix y, SimpleMatrix theta, double lambda,
                                        Function<Double, Double> sigmoidFunc) {
        SimpleMatrix b = x.mult(theta);

        int m = b.numRows();
        SimpleMatrix h = new SimpleMatrix(m, 1);
        for (int i=0;i<m;i++) {
            h.set(i, 0, sigmoidFunc.apply(b.get(i, 0)));
        }

        SimpleMatrix loss = h.minus(y);
        SimpleMatrix unregularized = x.transpose().mult(loss).divide(m);

        // add regularization
        SimpleMatrix r = theta.divide(m/lambda);
        r.set(0, 0, 0);

        return unregularized.plus(r);
    }

}
