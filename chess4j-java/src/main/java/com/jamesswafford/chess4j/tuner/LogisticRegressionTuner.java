package com.jamesswafford.chess4j.tuner;

import com.jamesswafford.chess4j.Globals;
import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.eval.EvalTermsVector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class LogisticRegressionTuner {

    private static final Logger LOGGER = LogManager.getLogger(LogisticRegressionTuner.class);

    private final ErrorFunction errorFunction;
    private final List<GameRecord> gameRecords;

    public LogisticRegressionTuner(TunerDatasource tunerDatasource) {
        this.errorFunction = new ErrorFunction();
        this.gameRecords = tunerDatasource.getGameRecords(false);
    }

    public EvalTermsVector optimize() {
        long start = System.currentTimeMillis();
        LOGGER.info("tuning started");
        EvalTermsVector optimizedVector = optimize(Globals.getEvalTermsVector());
        long end = System.currentTimeMillis();
        LOGGER.info("tuning complete in {} seconds", (end-start)/1000);
        return optimizedVector;
    }

    public EvalTermsVector optimize(EvalTermsVector evalTermsVector) {
        int numParams = evalTermsVector.terms.length;
        double bestE = calculateAverageError(evalTermsVector);
        LOGGER.info("initial E: " + bestE);
        EvalTermsVector bestVector = new EvalTermsVector(evalTermsVector);
        int numIterations = 0;
        int numParamsImproved;

        do {
            ++numIterations;
            numParamsImproved = 0;
            for (int i=0;i<numParams;i++) {
                EvalTermsVector searchVector = new EvalTermsVector(bestVector);
                searchVector.terms[i] = searchVector.terms[i] + 1;
                double searchE = calculateAverageError(searchVector);
                if (searchE < bestE) {
                    bestE = searchE;
                    bestVector = searchVector;
                    ++numParamsImproved;
                } else {
                    searchVector.terms[i] = searchVector.terms[i] - 2;
                    searchE = calculateAverageError(searchVector);
                    if (searchE < bestE) {
                        bestE = searchE;
                        bestVector = searchVector;
                        ++numParamsImproved;
                    }
                }
            }
            LOGGER.info("iteration " + numIterations + ": bestE=" + bestE + ", numParamsImproved=" + numParamsImproved);

        } while (numParamsImproved > 0);


        return bestVector;
    }

    public double calculateAverageError(EvalTermsVector evalTermsVector) {
        EvalTermsVector originalVector = Globals.getEvalTermsVector();
        Globals.setEvalTermsVector(evalTermsVector);

        double totalError = 0;
        for (GameRecord gameRecord : gameRecords) {
            Board board = new Board(gameRecord.getFen());
            double error = errorFunction.calculateError(board, gameRecord.getGameResult());
            totalError += error;
        }

        Globals.setEvalTermsVector(originalVector);

        return totalError / gameRecords.size();
    }

}
