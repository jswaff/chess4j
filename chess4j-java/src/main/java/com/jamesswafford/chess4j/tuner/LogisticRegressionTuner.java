package com.jamesswafford.chess4j.tuner;

import com.jamesswafford.chess4j.Globals;
import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.eval.EvalWeightsVector;
import com.jamesswafford.chess4j.io.EvalTermsVectorUtil;
import io.vavr.Tuple2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.jamesswafford.chess4j.tuner.CostFunction.cost;
import static com.jamesswafford.chess4j.tuner.Hypothesis.hypothesis;

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
        EvalWeightsVector theta = trainWithNaiveSearch(trainingSet, initialTheta, maxIterations);
        long end = System.currentTimeMillis();
        LOGGER.info("training complete in {} seconds", (end-start)/1000);

        double finalError = cost(testSet, theta);
        LOGGER.info("final error using test set: {}", finalError);

        // restore the pawn hash setting
        Globals.setPawnHashEnabled(pawnHashEnabled);

        return new Tuple2<>(theta, finalError);
    }

    private EvalWeightsVector trainWithGradientDescent(List<GameRecord> trainingSet, EvalWeightsVector theta, int maxIterations) {

        EvalWeightsVector bestTheta = new EvalWeightsVector(theta);
        for (int it=0; it<maxIterations; it++) {

            // calculate the hypothesis and cost over the entire training set
            List<Double> hypothesis = new ArrayList<>();
            List<Double> cost = new ArrayList<>();

            for (GameRecord trainingRecord : trainingSet) {
                double h = hypothesis(new Board(trainingRecord.getFen()), bestTheta);
                double e = cost(h, trainingRecord.getGameResult());
                hypothesis.add(h);
                cost.add(e);
            }

            // adjust the weights using batch gradient descent
            // TODO: need feature vector

            // TODO: convergence test
        }

        return bestTheta;
    }

    private EvalWeightsVector trainWithNaiveSearch(List<GameRecord> trainingSet, EvalWeightsVector theta, int maxIterations) {
        int n = theta.terms.length;
        double bestError = cost(trainingSet, theta);
        EvalWeightsVector bestTheta = new EvalWeightsVector(theta);

        for (int it = 0; it<maxIterations; it++) {
            int numParamsImproved = 0;
            for (int i=0;i<n;i++) {
                EvalWeightsVector candidateTheta = new EvalWeightsVector(bestTheta);
                candidateTheta.terms[i] = candidateTheta.terms[i] + 1;
                double error = cost(trainingSet, candidateTheta);
                if (error < bestError) {
                    bestError = error;
                    bestTheta = candidateTheta;
                    numParamsImproved++;
                } else {
                    candidateTheta.terms[i] = candidateTheta.terms[i] - 2;
                    error = cost(trainingSet, candidateTheta);
                    if (error < bestError) {
                        bestError = error;
                        bestTheta = candidateTheta;
                        numParamsImproved++;
                    }
                }
            }
            EvalTermsVectorUtil.store(bestTheta, "eval-tune-" + (it+1) + ".properties", "Error: " + bestError);
            if (numParamsImproved == 0) {
                break;
            }
            LOGGER.info("error using training set after iteration {}: {}", (it+1), bestError);
        }

        return bestTheta;
    }

}
