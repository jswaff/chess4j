package com.jamesswafford.chess4j.tuner;

import com.jamesswafford.chess4j.Globals;
import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.eval.EvalTermsVector;
import com.jamesswafford.chess4j.io.EvalTermsVectorUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Properties;

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
        double bestError = calculateAverageError(evalTermsVector);
        LOGGER.info("initial E: " + bestError);
        EvalTermsVector bestVector = new EvalTermsVector(evalTermsVector);
        int numIterations = 0;
        int numParamsImproved;

        do {
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
            LOGGER.info("iteration " + numIterations + ": E=" + bestError + ", numParamsImproved=" + numParamsImproved);

            // write to file in case execution is interrupted
            writeVectorToTempProperties(bestVector);
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

    private void writeVectorToTempProperties(EvalTermsVector evalTermsVector) {
        Properties props = EvalTermsVectorUtil.toProperties(evalTermsVector);
        LOGGER.info(props);
        try (OutputStream output = new FileOutputStream("eval-tuning.properties")) {
            props.store(output, null);
        } catch (IOException e) {
            LOGGER.error(e);
            throw new RuntimeException(e);
        }
    }
}
