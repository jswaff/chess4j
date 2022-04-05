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

public class Tuner {

    private static final Logger LOGGER = LogManager.getLogger(Tuner.class);

    private final List<GameRecord> trainingSet;

    public Tuner(TunerDatasource tunerDatasource) {
        this.trainingSet = tunerDatasource.getGameRecords();
    }

    public EvalTermsVector optimize() {
        long start = System.currentTimeMillis();
        LOGGER.info("tuning started: num positions={}", trainingSet.size());
        EvalTermsVector optimizedWeights = optimizeWithNaiveSearch(Globals.getEvalTermsVector(), 1000);
        long end = System.currentTimeMillis();
        LOGGER.info("tuning complete in {} seconds", (end-start)/1000);
        return optimizedWeights;
    }

    public EvalTermsVector optimizeWithGradientDescent(EvalTermsVector theta, int maxIterations) {
        boolean pawnHashEnabled = Globals.isPawnHashEnabled();
        Globals.setPawnHashEnabled(false);

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

        // restore the pawn hash setting
        Globals.setPawnHashEnabled(pawnHashEnabled);

        return bestTheta;
    }

    public EvalTermsVector optimizeWithNaiveSearch(EvalTermsVector theta, int maxIterations) {
        boolean pawnHashEnabled = Globals.isPawnHashEnabled();
        Globals.setPawnHashEnabled(false);

        int n = theta.terms.length;
        double bestError = calculateAverageError(theta);

        LOGGER.info("initial error: " + bestError);
        EvalTermsVector bestTheta = new EvalTermsVector(theta);

        for (int it=0; it<maxIterations; it++) {
            int numParamsImproved = 0;
            for (int i=0;i<n;i++) {
                EvalTermsVector candidateTheta = new EvalTermsVector(bestTheta);
                candidateTheta.terms[i] = candidateTheta.terms[i] + 1;
                double error = calculateAverageError(candidateTheta);
                if (error < bestError) {
                    bestError = error;
                    bestTheta = candidateTheta;
                    ++numParamsImproved;
                } else {
                    candidateTheta.terms[i] = candidateTheta.terms[i] - 2;
                    error = calculateAverageError(candidateTheta);
                    if (error < bestError) {
                        bestError = error;
                        bestTheta = candidateTheta;
                        ++numParamsImproved;
                    }
                }
            }
            if (numParamsImproved==0) {
                // TODO: convergence test
                break;
            }
        }

        // restore the pawn hash setting
        Globals.setPawnHashEnabled(pawnHashEnabled);

        LOGGER.info("final error: " + bestError);

        return bestTheta;
    }

    private double calculateAverageError(EvalTermsVector evalTermsVector) {

        double totalError = 0;
        for (GameRecord gameRecord : trainingSet) {
            Board board = new Board(gameRecord.getFen());
            double h = hypothesis(board, evalTermsVector);
            double cost = cost(h, gameRecord.getGameResult());
            totalError += cost;
        }

        return totalError / trainingSet.size();
    }

}
