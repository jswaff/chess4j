package com.jamesswafford.chess4j.tuner;

import com.jamesswafford.chess4j.Globals;
import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.eval.Eval;
import com.jamesswafford.chess4j.eval.EvalWeightsVector;
import com.jamesswafford.chess4j.io.EvalWeightsVectorUtil;
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


    public Tuple2<EvalWeightsVector, Double> optimize(EvalWeightsVector initialTheta, List<GameRecord> dataSet,
                                                      double learningRate, int maxIterations) {

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
        EvalWeightsVector theta = trainWithGradientDescent(trainingSet, initialTheta, learningRate, maxIterations);
        long end = System.currentTimeMillis();
        LOGGER.info("training complete in {} seconds", (end-start)/1000);

        double finalError = cost(testSet, theta);
        LOGGER.info("final error using test set: {}", finalError);

        // restore the pawn hash setting
        Globals.setPawnHashEnabled(pawnHashEnabled);

        return new Tuple2<>(theta, finalError);
    }

    private EvalWeightsVector trainWithGradientDescent(List<GameRecord> trainingSet, EvalWeightsVector weights,
                                                       double learningRate, int maxIterations) {

        int m = trainingSet.size();
        int n = weights.weights.length;
        double alpha = 1.0;

        SimpleMatrix x = new SimpleMatrix(m, n);
        SimpleMatrix y = new SimpleMatrix(m, 1);
        for (int i=0;i<m;i++) {
            GameRecord trainingRecord = trainingSet.get(i);
            Board board = new Board(trainingRecord.getFen());
            int[] features_i = Eval.extractFeatures(board);
            for (int j=0;j<n;j++) {
                x.set(i, j, features_i[j]);
            }
            y.set(i, 0, CostFunction.y(trainingRecord.getGameResult()));
        }

        SimpleMatrix xTrans = x.transpose();

        EvalWeightsVector bestWeights = new EvalWeightsVector(weights);
        bestWeights.randomize();
        for (int i=0;i<bestWeights.weights.length;i++) {
            System.out.println("i=" + i + " : " + bestWeights.weights[i]);
        }
        SimpleMatrix theta = new SimpleMatrix(n, 1);
        for (int i=0;i<n;i++) {
            theta.set(i, 0, bestWeights.weights[i]);
        }

        for (int it=0; it<maxIterations; it++) {

            // create the hypothesis vector
            //SimpleMatrix h = x.mult(theta);
            SimpleMatrix h = new SimpleMatrix(m, 1);
            for (int i=0;i<m;i++) {
                GameRecord trainingRecord = trainingSet.get(i);
                Board board = new Board(trainingRecord.getFen());
                h.set(i, 0, Hypothesis.hypothesis(board, bestWeights));
            }

            SimpleMatrix loss = h.minus(y);
            SimpleMatrix gradient = xTrans.mult(loss).divide(m);
            theta = theta.minus(gradient.divide(1.0/learningRate)); // TODO: alpha
            for (int i=0;i<n;i++) {
                bestWeights.weights[i] = (int)Math.round(theta.get(i, 0));
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
