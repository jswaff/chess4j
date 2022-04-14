package com.jamesswafford.chess4j.tuner;

import org.ejml.simple.SimpleMatrix;

import java.util.function.Function;

public class Gradient {

    public static SimpleMatrix gradient(SimpleMatrix x, SimpleMatrix y, SimpleMatrix theta) {
        return gradient(x, y, theta, Hypothesis::texelSigmoid);
    }

    public static SimpleMatrix gradient(SimpleMatrix x, SimpleMatrix y, SimpleMatrix theta,
                                        Function<Double, Double> sigmoidFunc) {
        SimpleMatrix b = x.mult(theta);

        int m = b.numRows();
        SimpleMatrix h = new SimpleMatrix(m, 1);
        for (int i=0;i<m;i++) {
            h.set(i, 0, sigmoidFunc.apply(b.get(i, 0)));
        }

        SimpleMatrix loss = h.minus(y);
        SimpleMatrix unregularized = x.transpose().mult(loss).divide(m);

//        % add the lambda term for j >= 1
//        lv2 = (lambda / m) * theta;  % nx1 vector
//        lv2(1,1) = 0;
//        grad = grad_1 + lv2;

        return unregularized;
    }

}
