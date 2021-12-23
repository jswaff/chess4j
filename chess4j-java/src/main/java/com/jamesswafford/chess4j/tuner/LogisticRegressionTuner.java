package com.jamesswafford.chess4j.tuner;

import com.jamesswafford.chess4j.Globals;
import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.eval.EvalTermsVector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class LogisticRegressionTuner {

    private static final Logger LOGGER = LogManager.getLogger(LogisticRegressionTuner.class);

    private final TunerDatasource tunerDatasource;
    private final ErrorFunction errorFunction;
    private final long numPositions;

    public LogisticRegressionTuner(TunerDatasource tunerDatasource) {
        this.tunerDatasource = tunerDatasource;
        this.errorFunction = new ErrorFunction();
        this.numPositions = tunerDatasource.getTotalPositionsCount();
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
        EvalTermsVector bestVector = new EvalTermsVector(evalTermsVector);
        boolean improved = true;
        while (improved) {
            improved = false;
            for (int i=0;i<numParams;i++) {
                LOGGER.info("optimizing parameter " + (i+1) + " / " + numParams);
                EvalTermsVector searchVector = new EvalTermsVector(bestVector);
                searchVector.terms[i] = searchVector.terms[i] + 1;
                double searchE = calculateAverageError(searchVector);
                if (searchE < bestE) {
                    bestE = searchE;
                    bestVector = searchVector;
                    improved = true;
                } else {
                    LOGGER.info("retrying parameter " + (i+1) + " / " + numParams);
                    searchVector.terms[i] = searchVector.terms[i] - 2;
                    searchE = calculateAverageError(searchVector);
                    if (searchE < bestE) {
                        bestE = searchE;
                        bestVector = searchVector;
                        improved = true;
                    }
                }
            }
        }

        return bestVector;
    }

    public double calculateAverageError(EvalTermsVector evalTermsVector) {
        EvalTermsVector originalVector = Globals.getEvalTermsVector();
        Globals.setEvalTermsVector(evalTermsVector);

        tunerDatasource.markAllRecordsAsUnprocessed();
        List<GameRecord> gameRecords = tunerDatasource.getGameRecords(true);

        int numProcessed = 0;
        while (gameRecords.size() > 0) {
            for (GameRecord gameRecord : gameRecords) {
                processGameRecord(gameRecord);
                ++numProcessed;
                if (numProcessed % 1000 == 0) {
                    LOGGER.info("\tprocessed " + numProcessed + " / " + numPositions);
                }
            }
            gameRecords = tunerDatasource.getGameRecords(true);
        }

        double averageError = tunerDatasource.getAverageError();
        LOGGER.info("average error: " + averageError);

        Globals.setEvalTermsVector(originalVector);

        return averageError;
    }

    private void processGameRecord(GameRecord gameRecord) {
        Board board = new Board(gameRecord.getFen());
        double error = errorFunction.calculateError(board, gameRecord.getGameResult());
        tunerDatasource.updateError(gameRecord.getFen(), (float)error); // sets "processed" flag
    }
}
