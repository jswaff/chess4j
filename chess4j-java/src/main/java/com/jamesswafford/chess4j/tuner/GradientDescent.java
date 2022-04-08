package com.jamesswafford.chess4j.tuner;

public class GradientDescent {

    // calculate the gradient - the partial derivatives of the cost w.r.t. theta
    public static double[] batchGradientDescent(double[] theta, double[][] x, double[] y, double alpha) {

        int n = theta.length;
        int m = y.length;

        // x should be m x n
        assert(x.length==m);
        assert(x[0].length==n);

        double[] grad = new double[n];

        // calculate the sum of errors for each example
        double[] e = new double[m];
        for (int i=0;i<m;i++) {
            double[] x_i = x[i];

            // get raw score
            double s = 0;
            for (int j=0;j<n;j++) {
                s += x_i[j] * theta[j];
            }

            // calculate the hypothesis
            double h_i = Hypothesis.hypothesis(s);

            // add to the cumulative error
            e[i] = h_i - y[i];
        }

        // calculate the gradient
        for (int j=0;j<n;j++) {
            double weightedError = 0;
            for (int i=0;i<m;i++) {
                weightedError += e[i] * x[i][j];
            }

            grad[j] = alpha * weightedError / m;
        }

        return grad;
    }

}
