package com.jamesswafford.chess4j.tuner;

import org.ejml.simple.SimpleMatrix;

public class Gradient {

    public static SimpleMatrix gradient(SimpleMatrix x, SimpleMatrix y, SimpleMatrix theta) {
        SimpleMatrix b = x.mult(theta);

        int m = b.numRows();
        SimpleMatrix h = new SimpleMatrix(m, 1);
        for (int i=0;i<m;i++) {
            h.set(i, 0, Hypothesis.sigmoid(b.get(i, 0)));
        }

        SimpleMatrix loss = h.minus(y);
        return x.transpose().mult(loss).divide(m);
    }

}
