package com.jamesswafford.chess4j.tuner;

import com.jamesswafford.chess4j.Globals;
import com.jamesswafford.chess4j.eval.EvalWeights;
import com.jamesswafford.chess4j.io.EvalWeightsUtil;
import io.vavr.Tuple2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ejml.simple.SimpleMatrix;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.jamesswafford.chess4j.tuner.CostFunction.cost;

public class LogisticRegressionTuner {

    private static final Logger LOGGER = LogManager.getLogger(LogisticRegressionTuner.class);


    public Tuple2<EvalWeights, Double> optimize(EvalWeights initialWeights, List<GameRecord> dataSet,
                                                double learningRate, double lambda, int batchSize, int maxIterations) {

        LOGGER.info("optimizing weights.  alpha: {}, lambda: {}, batchSize: {}, maxIteration: {}",
                learningRate, lambda, batchSize, maxIterations);

        // disable pawn hash
        boolean pawnHashEnabled = Globals.isPawnHashEnabled();
        Globals.setPawnHashEnabled(false);

        // if we have enough data, divide data set up into training and test sets in an 80/20 split
        Collections.shuffle(dataSet);
        List<GameRecord> trainingSet;
        List<GameRecord> testSet;
        if (dataSet.size() >= 100) {
            int m = dataSet.size() * 4 / 5;
            trainingSet = new ArrayList<>(dataSet.subList(0, m));
            testSet = dataSet.subList(m, dataSet.size());
        } else {
            trainingSet = new ArrayList<>(dataSet);
            testSet = new ArrayList<>(dataSet);
        }
        LOGGER.info("data set size: {} training: {}, test: {}", dataSet.size(), trainingSet.size(), testSet.size());

        double initialError = cost(testSet, initialWeights);
        LOGGER.info("initial error using test set: {}", initialError);

        long start = System.currentTimeMillis();
        EvalWeights weights = trainWithGradientDescent(trainingSet, testSet, initialWeights, learningRate, lambda,
                batchSize, maxIterations);
        long end = System.currentTimeMillis();
        LOGGER.info("training complete in {} seconds", (end-start)/1000);

        double finalError = cost(testSet, weights);
        LOGGER.info("final error using test set: {}", finalError);

        // restore the pawn hash setting
        Globals.setPawnHashEnabled(pawnHashEnabled);

        return new Tuple2<>(weights, finalError);
    }

    private EvalWeights trainWithGradientDescent(List<GameRecord> trainingSet, List<GameRecord> testSet,
                                                 EvalWeights initialWeights,
                                                 double learningRate, double lambda, int batchSize, int maxIterations) {

        EvalWeights bestWeights = new EvalWeights(initialWeights);
        int n = bestWeights.vals.length;
        SimpleMatrix theta = MatrixUtils.weightsToMatrix(bestWeights);

        for (int it=0; it<maxIterations; it++) {

            // load a batch and set up the X (features) matrix and Y (outcome) vector
            Tuple2<SimpleMatrix, SimpleMatrix> xy = MatrixUtils.loadXY(trainingSet, batchSize, n);
            SimpleMatrix x = xy._1;
            SimpleMatrix y = xy._2;

            // calculate the gradient and adjust accordingly
            SimpleMatrix gradient = Gradient.gradient(x, y, theta, lambda);
            theta = theta.minus(gradient.divide(1.0/learningRate));
            for (int i=1;i<n;i++) { // start at one to keep pawn val anchored
                bestWeights.vals[i] = (int)Math.round(theta.get(i, 0));
            }

            // display the error and store the weights every 100 iterations
            if ((it+1)%100 == 0) {
                double trainingError = cost(trainingSet, bestWeights);
                double testError = cost(testSet, bestWeights);
                LOGGER.info(trainingError + "," + testError);
            }
        }

        return bestWeights;
    }

    private EvalWeights trainWithNaiveSearch(List<GameRecord> trainingSet, EvalWeights initialWeights, int maxIterations) {
        int n = initialWeights.vals.length;
        double bestError = cost(trainingSet, initialWeights);
        EvalWeights bestWeights = new EvalWeights(initialWeights);

        for (int it = 0; it<maxIterations; it++) {
            int numParamsImproved = 0;
            for (int i=0;i<n;i++) {
                EvalWeights candidateWeights = new EvalWeights(bestWeights);
                candidateWeights.vals[i] = candidateWeights.vals[i] + 1;
                double error = cost(trainingSet, candidateWeights);
                if (error < bestError) {
                    bestError = error;
                    bestWeights = candidateWeights;
                    numParamsImproved++;
                } else {
                    candidateWeights.vals[i] = candidateWeights.vals[i] - 2;
                    error = cost(trainingSet, candidateWeights);
                    if (error < bestError) {
                        bestError = error;
                        bestWeights = candidateWeights;
                        numParamsImproved++;
                    }
                }
            }
            EvalWeightsUtil.store(bestWeights, "eval-tune-" + (it+1) + ".properties", "Error: " + bestError);
            if (numParamsImproved == 0) {
                break;
            }
            LOGGER.info("error using training set after iteration {}: {}", (it+1), bestError);
        }

        return bestWeights;
    }

}
