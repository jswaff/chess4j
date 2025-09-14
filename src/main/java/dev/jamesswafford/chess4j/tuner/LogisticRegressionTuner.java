package dev.jamesswafford.chess4j.tuner;

import dev.jamesswafford.chess4j.Globals;
import dev.jamesswafford.chess4j.eval.EvalWeights;
import dev.jamesswafford.chess4j.io.FENRecord;
import io.vavr.Tuple2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ejml.simple.SimpleMatrix;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static dev.jamesswafford.chess4j.tuner.CostFunction.cost;

public class LogisticRegressionTuner {

    private static final Logger LOGGER = LogManager.getLogger(LogisticRegressionTuner.class);


    public Tuple2<EvalWeights, Double> optimize(EvalWeights initialWeights, List<FENRecord> dataSet,
                                                double learningRate, int maxIterations) {

        LOGGER.info("tuning eval weights.  learningRate: {} maxIterations: {}", learningRate, maxIterations);

        // disable pawn hash
        boolean pawnHashEnabled = Globals.isPawnHashEnabled();
        Globals.setPawnHashEnabled(false);

        Collections.shuffle(dataSet);

        // divide data set up into training and test sets in an 80/20 split
        int m = dataSet.size() * 4 / 5;
        List<FENRecord> trainingSet = new ArrayList<>(dataSet.subList(0, m));
        List<FENRecord> testSet = dataSet.subList(m, dataSet.size());
        LOGGER.info("data set size: {} training: {} test: {}", dataSet.size(), trainingSet.size(), testSet.size());

        double initialError = cost(testSet, initialWeights);
        LOGGER.info(String.format("initial error: %.4f", initialError));

        long start = System.currentTimeMillis();
        EvalWeights weights = trainWithGradientDescent(trainingSet, testSet, initialWeights, learningRate, maxIterations);
        long end = System.currentTimeMillis();
        LOGGER.info("training complete in {} seconds", (end-start)/1000);

        double finalError = cost(testSet, weights);
        LOGGER.info(String.format("final error: %.4f", finalError));

        // restore the pawn hash setting
        Globals.setPawnHashEnabled(pawnHashEnabled);

        return new Tuple2<>(weights, finalError);
    }

    private EvalWeights trainWithGradientDescent(List<FENRecord> trainingSet, List<FENRecord> testSet,
                                                 EvalWeights initialWeights, double learningRate, int maxIterations) {

        EvalWeights bestWeights = new EvalWeights(initialWeights);
        int n = bestWeights.vals.length;
        SimpleMatrix theta = MatrixUtils.weightsToMatrix(bestWeights);

        // reduce the learning rate to 10% over the run
        //double lrDelta = (learningRate / maxIterations) * 0.9;
        LOGGER.info("Epoch       Train        Test");
        for (int it=0; it<maxIterations; it++) {

            // load a batch and set up the X (features) matrix and Y (outcome) vector
            Tuple2<SimpleMatrix, SimpleMatrix> xy = MatrixUtils.loadXY(trainingSet, 10000, n);
            SimpleMatrix x = xy._1;
            SimpleMatrix y = xy._2;

            // calculate the gradient and adjust accordingly
            SimpleMatrix gradient = Gradient.gradient(x, y, theta, 0);
            theta = theta.minus(gradient.divide(1.0/learningRate));
            for (int i = 1; i<n; i++) { // anchor pawn value
                if (i != EvalWeights.BISHOP_TRAPPED_IND) { // anchor trapped bishop
                    bestWeights.vals[i] = (int)Math.round(theta.get(i, 0));
                }
            }

            // display the error and store the weights every 100 iterations
            if ((it+1) % 100 == 0) {
                double trainingError = cost(trainingSet, bestWeights);
                double testError = cost(testSet, bestWeights);
                LOGGER.info(String.format("%5d%12.4f%12.4f", (it+1),trainingError, testError));
            }

            //learningRate -= lrDelta;
        }

        return bestWeights;
    }

}
