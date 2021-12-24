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

    private final TunerDatasource tunerDatasource;
    private final ErrorFunction errorFunction;

    public LogisticRegressionTuner(TunerDatasource tunerDatasource) {
        this.tunerDatasource = tunerDatasource;
        this.errorFunction = new ErrorFunction();
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
                LOGGER.info("\toptimizing parameter " + (i+1) + " / " + numParams);
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

            // write to a temporary properties file
            Properties props = EvalTermsVectorUtil.toProperties(bestVector);
            LOGGER.info(props);
            try (OutputStream output = new FileOutputStream("temp-eval.properties")) {
                props.store(output, null);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } while (numParamsImproved == 0);


        return bestVector;
    }

    public double calculateAverageError(EvalTermsVector evalTermsVector) {
        EvalTermsVector originalVector = Globals.getEvalTermsVector();
        Globals.setEvalTermsVector(evalTermsVector);

        tunerDatasource.markAllRecordsAsUnprocessed();
        List<GameRecord> gameRecords = tunerDatasource.getGameRecords(true);

        while (gameRecords.size() > 0) {
            gameRecords.forEach(this::processGameRecord);
            gameRecords = tunerDatasource.getGameRecords(true);
        }

        Globals.setEvalTermsVector(originalVector);

        return tunerDatasource.getAverageError();
    }

    private void processGameRecord(GameRecord gameRecord) {
        Board board = new Board(gameRecord.getFen());
        double error = errorFunction.calculateError(board, gameRecord.getGameResult());
        tunerDatasource.updateError(gameRecord.getFen(), (float)error); // sets "processed" flag
    }
}
