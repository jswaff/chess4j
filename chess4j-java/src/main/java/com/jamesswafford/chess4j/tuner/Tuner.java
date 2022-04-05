package com.jamesswafford.chess4j.tuner;

import com.jamesswafford.chess4j.Globals;
import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.eval.EvalTermsVector;
import com.jamesswafford.chess4j.io.EvalTermsVectorUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class Tuner {

    private static final Logger LOGGER = LogManager.getLogger(Tuner.class);

    private final CostFunction costFunction;
    private final List<GameRecord> gameRecords;

    public Tuner(TunerDatasource tunerDatasource) {
        this.costFunction = new CostFunction();
        this.gameRecords = tunerDatasource.getGameRecords();
    }

    public EvalTermsVector optimize() {
        long start = System.currentTimeMillis();
        LOGGER.info("tuning started: num positions={}", gameRecords.size());
        EvalTermsVector optimizedVector = optimize(Globals.getEvalTermsVector());
        long end = System.currentTimeMillis();
        LOGGER.info("tuning complete in {} seconds", (end-start)/1000);
        return optimizedVector;
    }

    public EvalTermsVector optimize(EvalTermsVector evalTermsVector) {
        boolean pawnHashEnabled = Globals.isPawnHashEnabled();
        Globals.setPawnHashEnabled(false);
        int numParams = evalTermsVector.terms.length;
        double bestError = calculateAverageError(evalTermsVector);
        LOGGER.info("initial E: " + bestError);
        EvalTermsVector bestVector = new EvalTermsVector(evalTermsVector);
        int numIterations = 0;
        int numParamsImproved;
        long startTime, totalSeconds;

        do {
            startTime = System.currentTimeMillis();
            ++numIterations;
            numParamsImproved = 0;
            for (int i=0;i<numParams;i++) {
                EvalTermsVector searchVector = new EvalTermsVector(bestVector);
                searchVector.terms[i] = searchVector.terms[i] + 1;
                double error = calculateAverageError(searchVector);
                if (error < bestError) {
                    bestError = error;
                    bestVector = searchVector;
                    ++numParamsImproved;
                } else {
                    searchVector.terms[i] = searchVector.terms[i] - 2;
                    error = calculateAverageError(searchVector);
                    if (error < bestError) {
                        bestError = error;
                        bestVector = searchVector;
                        ++numParamsImproved;
                    }
                }
            }
            totalSeconds = (System.currentTimeMillis() - startTime) / 1000;
            LOGGER.info("iteration " + numIterations + ": time=" + (totalSeconds/60) + "m " + (totalSeconds%60) + "s, " +
                    "E=" + bestError + ", numParamsImproved=" + numParamsImproved);

            // write to file in case execution is interrupted
            EvalTermsVectorUtil.store(bestVector, "eval-tuning.properties");
        } while (numParamsImproved > 0);

        // restore the pawn hash setting
        Globals.setPawnHashEnabled(pawnHashEnabled);

        return bestVector;
    }

    private double calculateAverageError(EvalTermsVector evalTermsVector) {

        double totalError = 0;
        for (GameRecord gameRecord : gameRecords) {
            Board board = new Board(gameRecord.getFen());
            double h = Hypothesis.hypothesis(board, evalTermsVector);
            double cost = costFunction.cost(h, gameRecord.getGameResult());
            totalError += cost;
        }

        return totalError / gameRecords.size();
    }

}
