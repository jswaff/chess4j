package com.jamesswafford.chess4j.tuner;

import com.jamesswafford.chess4j.Globals;
import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.eval.EvalTermsVector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static com.jamesswafford.chess4j.tuner.CostFunction.cost;
import static com.jamesswafford.chess4j.tuner.Hypothesis.hypothesis;

public class LogisticRegressionTuner {

    private static final Logger LOGGER = LogManager.getLogger(LogisticRegressionTuner.class);

    private final List<GameRecord> trainingSet;

    public LogisticRegressionTuner(TunerDatasource tunerDatasource) {
        this.trainingSet = tunerDatasource.getGameRecords();
    }

    public EvalTermsVector optimize(int maxIterations) {

        // disable pawn hash
        boolean pawnHashEnabled = Globals.isPawnHashEnabled();
        Globals.setPawnHashEnabled(false);

        long start = System.currentTimeMillis();
        LOGGER.info("training started: m={}", trainingSet.size());
        EvalTermsVector optimizedWeights = trainWithNaiveSearch(Globals.getEvalTermsVector(), maxIterations);
        long end = System.currentTimeMillis();
        LOGGER.info("training complete in {} seconds", (end-start)/1000);

        // restore the pawn hash setting
        Globals.setPawnHashEnabled(pawnHashEnabled);

        return optimizedWeights;
    }

    private EvalTermsVector trainWithGradientDescent(EvalTermsVector theta, int maxIterations) {

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

    private EvalTermsVector trainWithNaiveSearch(EvalTermsVector theta, int maxIterations) {
        int n = theta.terms.length;
        double bestError = calculateAverageError(theta);
        EvalTermsVector bestTheta = new EvalTermsVector(theta);

        for (int it = 0; it<maxIterations; it++) {
            int numParamsImproved = 0;
            for (int i=0;i<n;i++) {
                EvalTermsVector candidateTheta = new EvalTermsVector(bestTheta);
                candidateTheta.terms[i] = candidateTheta.terms[i] + 1;
                double error = calculateAverageError(candidateTheta);
                if (error < bestError) {
                    bestError = error;
                    bestTheta = candidateTheta;
                    numParamsImproved++;
                } else {
                    candidateTheta.terms[i] = candidateTheta.terms[i] - 2;
                    error = calculateAverageError(candidateTheta);
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

    private double calculateAverageError(EvalTermsVector theta) {
        double totalError = 0;

        for (GameRecord gameRecord : trainingSet) {
            Board board = new Board(gameRecord.getFen());
            double h = hypothesis(board, theta);
            double cost = cost(h, gameRecord.getGameResult());
            totalError += cost;
        }

        return totalError / trainingSet.size();
    }

}
