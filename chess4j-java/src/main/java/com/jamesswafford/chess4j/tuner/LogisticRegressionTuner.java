package com.jamesswafford.chess4j.tuner;

import com.jamesswafford.chess4j.Globals;
import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.eval.EvalTermsVector;
import io.vavr.Tuple2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static com.jamesswafford.chess4j.tuner.CostFunction.cost;
import static com.jamesswafford.chess4j.tuner.Hypothesis.hypothesis;

public class LogisticRegressionTuner {

    private static final Logger LOGGER = LogManager.getLogger(LogisticRegressionTuner.class);


    public Tuple2<EvalTermsVector, Double> optimize(EvalTermsVector initialTheta, List<GameRecord> dataSet, int maxIterations) {

        List<GameRecord> trainingSet = dataSet; // TODO

        // disable pawn hash
        boolean pawnHashEnabled = Globals.isPawnHashEnabled();
        Globals.setPawnHashEnabled(false);

        double initialError = cost(trainingSet, initialTheta);
        LOGGER.info("initial error={}", initialError);

        long start = System.currentTimeMillis();
        LOGGER.info("training started: m={}", trainingSet.size());
        EvalTermsVector theta = trainWithNaiveSearch(trainingSet, initialTheta, maxIterations);
        long end = System.currentTimeMillis();
        LOGGER.info("training complete in {} seconds", (end-start)/1000);

        // TODO - measure error with test set
        double finalError = cost(trainingSet, theta);
        LOGGER.info("final error={}", finalError);

        // restore the pawn hash setting
        Globals.setPawnHashEnabled(pawnHashEnabled);

        return new Tuple2<>(theta, finalError);
    }

    private EvalTermsVector trainWithGradientDescent(List<GameRecord> trainingSet, EvalTermsVector theta, int maxIterations) {

        EvalTermsVector bestTheta = new EvalTermsVector(theta);
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

    private EvalTermsVector trainWithNaiveSearch(List<GameRecord> trainingSet, EvalTermsVector theta, int maxIterations) {
        int n = theta.terms.length;
        double bestError = cost(trainingSet, theta);
        EvalTermsVector bestTheta = new EvalTermsVector(theta);

        for (int it = 0; it<maxIterations; it++) {
            int numParamsImproved = 0;
            for (int i=0;i<n;i++) {
                EvalTermsVector candidateTheta = new EvalTermsVector(bestTheta);
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
            if (numParamsImproved > 0) {
                break;
            }
        }

        return bestTheta;
    }

}
