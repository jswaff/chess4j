package dev.jamesswafford.chess4j.nn;

public class NnueAccumulators {

    private final double[][] accumulators = new double[2][NeuralNetwork.NN_SIZE_L1];

    private final static double epsilon = 0.00001;

    public void add(int ind1, int ind2, double val) {
        accumulators[ind1][ind2] += val;
    }

    public double get(int ind1, int ind2) {
        return accumulators[ind1][ind2];
    }

    public void set(int ind1, int ind2, double val) {
        accumulators[ind1][ind2] = val;
    }

    public boolean equalsWithinEpsilon(NnueAccumulators that) {
        for (int i=0; i<accumulators.length;i++) {
            for (int j=0;j<accumulators[0].length;j++) {
                if (Math.abs(accumulators[i][j] - that.accumulators[i][j]) > epsilon) return false;
            }
        }
        return true;
    }
}
