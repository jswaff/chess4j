package com.jamesswafford.chess4j.tuner;

import com.jamesswafford.chess4j.Globals;
import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.eval.Eval;
import com.jamesswafford.chess4j.eval.EvalWeights;
import com.jamesswafford.chess4j.io.EvalWeightsUtil;
import io.vavr.Tuple2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ejml.simple.SimpleMatrix;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static com.jamesswafford.chess4j.tuner.CostFunction.cost;

public class LogisticRegressionTuner {

    private static final Logger LOGGER = LogManager.getLogger(LogisticRegressionTuner.class);


    public Tuple2<EvalWeights, Double> optimize(EvalWeights initialWeights, List<GameRecord> dataSet,
                                                double learningRate, int maxIterations) {

        // disable pawn hash
        boolean pawnHashEnabled = Globals.isPawnHashEnabled();
        Globals.setPawnHashEnabled(false);

        // divide data set up into training and test sets
        Collections.shuffle(dataSet);
        int m = dataSet.size() * 4 / 5;
        List<GameRecord> trainingSet;
        List<GameRecord> testSet;
        if (m > 0) {
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
        EvalWeights weights = trainWithGradientDescent(trainingSet, initialWeights, learningRate, maxIterations);
        long end = System.currentTimeMillis();
        LOGGER.info("training complete in {} seconds", (end-start)/1000);

        double finalError = cost(testSet, weights);
        LOGGER.info("final error using test set: {}", finalError);

        // restore the pawn hash setting
        Globals.setPawnHashEnabled(pawnHashEnabled);

        return new Tuple2<>(weights, finalError);
    }

    private EvalWeights trainWithGradientDescent(List<GameRecord> trainingSet, EvalWeights weights,
                                                 double learningRate, int maxIterations) {

        Random random = new Random(System.currentTimeMillis());

        EvalWeights bestWeights = new EvalWeights(weights);
        int n = 6; //weights.weights.length; FIXME
        SimpleMatrix theta = new SimpleMatrix(n, 1);
        for (int i=0;i<n;i++) {
            theta.set(i, 0, bestWeights.vals[i]);
        }

        for (int it=0; it<maxIterations; it++) {

            // randomly pick some records from the training set
            int m = Math.min(1000, trainingSet.size());
            SimpleMatrix x = new SimpleMatrix(m, n);
            SimpleMatrix y = new SimpleMatrix(m, 1);
            for (int i=0;i<m;i++) {
                GameRecord trainingRecord = trainingSet.get(random.nextInt(trainingSet.size()));
                Board board = new Board(trainingRecord.getFen());
                int[] features_i = Eval.extractFeatures(board);
                for (int j=0;j<n;j++) {
                    x.set(i, j, features_i[j]);
                }
                y.set(i, 0, CostFunction.y(trainingRecord.getResult()));
            }
            SimpleMatrix xTrans = x.transpose();

            // calculate the gradient
            SimpleMatrix h = x.mult(theta);
            // TODO: use vector operations
            for (int i=0;i<m;i++) {
                h.set(i, 0, Hypothesis.hypothesis(h.get(i, 0)));
            }

            SimpleMatrix loss = h.minus(y);
            SimpleMatrix gradient = xTrans.mult(loss).divide(m);
            theta = theta.minus(gradient.divide(1.0/learningRate)); // TODO: verify the learning rate
            for (int i=0;i<n;i++) {
                bestWeights.vals[i] = (int)Math.round(theta.get(i, 0));
            }

            // calculate cost
            double error = cost(trainingSet, bestWeights);
            LOGGER.info("error using training set after iteration {}: {}", (it+1), error);
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
