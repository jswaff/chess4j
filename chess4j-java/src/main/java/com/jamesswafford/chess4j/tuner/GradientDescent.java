package com.jamesswafford.chess4j.tuner;

import com.jamesswafford.chess4j.eval.EvalWeightsVector;

import java.util.List;

public class GradientDescent {

    // calculate the gradient - the partial derivatives of the cost w.r.t. theta
    public static double[] batchGradientDescent(double[] theta, int[][] x, double[] y, double lambda) {

        int n = theta.length;
        int m = y.length;

        // x should be m x n

        double[] grad = new double[n];

        return grad;
    }

}
