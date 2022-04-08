package com.jamesswafford.chess4j.tuner;

import com.jamesswafford.chess4j.Globals;
import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.eval.Eval;
import com.jamesswafford.chess4j.eval.EvalWeightsVector;
import com.jamesswafford.chess4j.io.EvalWeightsVectorUtil;
import io.vavr.Tuple2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.jamesswafford.chess4j.tuner.CostFunction.cost;

public class LogisticRegressionTuner {

    private static final Logger LOGGER = LogManager.getLogger(LogisticRegressionTuner.class);


    public Tuple2<EvalWeightsVector, Double> optimize(EvalWeightsVector initialTheta, List<GameRecord> dataSet, int maxIterations) {

        // disable pawn hash
        boolean pawnHashEnabled = Globals.isPawnHashEnabled();
        Globals.setPawnHashEnabled(false);

        // divide data set up into training and test sets
        Collections.shuffle(dataSet);
        int m = dataSet.size() * 4 / 5;
        List<GameRecord> trainingSet = new ArrayList<>(dataSet.subList(0, m));
        List<GameRecord> testSet = dataSet.subList(m, dataSet.size());
        LOGGER.info("data set size: {} training: {}, test: {}", dataSet.size(), trainingSet.size(), testSet.size());

        double initialError = cost(testSet, initialTheta);
        LOGGER.info("initial error using test set: {}", initialError);

        long start = System.currentTimeMillis();
        //EvalWeightsVector theta = trainWithNaiveSearch(trainingSet, initialTheta, maxIterations);
        EvalWeightsVector theta = trainWithGradientDescent(trainingSet, initialTheta, maxIterations);
        long end = System.currentTimeMillis();
        LOGGER.info("training complete in {} seconds", (end-start)/1000);

        double finalError = cost(testSet, theta);
        LOGGER.info("final error using test set: {}", finalError);

        // restore the pawn hash setting
        Globals.setPawnHashEnabled(pawnHashEnabled);

        return new Tuple2<>(theta, finalError);
    }

    private EvalWeightsVector trainWithGradientDescent(List<GameRecord> trainingSet, EvalWeightsVector weights, int maxIterations) {

        EvalWeightsVector bestWeights = new EvalWeightsVector(weights);

        int m = trainingSet.size();
        int n = weights.weights.length;
        double alpha = 1.0;

        for (int it=0; it<maxIterations; it++) {

            double[][] features = new double[m][n];
            double[] y = new double[m];

            for (int i=0;i<m;i++) {
                GameRecord trainingRecord = trainingSet.get(i);
                Tuple2<Integer, int[]> eval = Eval.evalWithFeatures(bestWeights, new Board(trainingRecord.getFen()));
                for (int j=0;j<n;j++) {
                    features[i][j] = eval._2[j];
                }
                y[i] = CostFunction.y(trainingRecord.getGameResult());

                // sanity check
                double s = 0;
                for (int j=0;j<n;j++) {
                    s += features[i][j] * bestWeights.weights[j];
                }
                System.out.println("s=" + s + ", eval=" + eval._1);
                assert(s == eval._1);
            }

            // adjust the weights using batch gradient descent
            double[] theta = new double[n];
            for (int j=0;j<n;j++) {
                theta[j] = bestWeights.weights[j];
            }
            double[] gradient = GradientDescent.batchGradientDescent(theta, features, y, alpha);

            // adjust theta
            for (int j=0;j<n;j++) {
                bestWeights.weights[j] -= (int)gradient[j];
            }

            // calculate cost
            double error = cost(trainingSet, bestWeights);
            LOGGER.info("error using training set after iteration {}: {}", (it+1), error);
        }

        return bestWeights;
    }

    private EvalWeightsVector trainWithNaiveSearch(List<GameRecord> trainingSet, EvalWeightsVector theta, int maxIterations) {
        int n = theta.weights.length;
        double bestError = cost(trainingSet, theta);
        EvalWeightsVector bestTheta = new EvalWeightsVector(theta);

        for (int it = 0; it<maxIterations; it++) {
            int numParamsImproved = 0;
            for (int i=0;i<n;i++) {
                EvalWeightsVector candidateTheta = new EvalWeightsVector(bestTheta);
                candidateTheta.weights[i] = candidateTheta.weights[i] + 1;
                double error = cost(trainingSet, candidateTheta);
                if (error < bestError) {
                    bestError = error;
                    bestTheta = candidateTheta;
                    numParamsImproved++;
                } else {
                    candidateTheta.weights[i] = candidateTheta.weights[i] - 2;
                    error = cost(trainingSet, candidateTheta);
                    if (error < bestError) {
                        bestError = error;
                        bestTheta = candidateTheta;
                        numParamsImproved++;
                    }
                }
            }
            EvalWeightsVectorUtil.store(bestTheta, "eval-tune-" + (it+1) + ".properties", "Error: " + bestError);
            if (numParamsImproved == 0) {
                break;
            }
            LOGGER.info("error using training set after iteration {}: {}", (it+1), bestError);
        }

        return bestTheta;
    }

}
