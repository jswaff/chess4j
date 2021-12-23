package com.jamesswafford.chess4j.tuner;

import com.jamesswafford.chess4j.board.Board;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class LogisticRegressionTuner {

    private static final Logger LOGGER = LogManager.getLogger(LogisticRegressionTuner.class);

    private final TunerDatasource tunerDatasource;
    private final ErrorFunction errorFunction;

    public LogisticRegressionTuner(TunerDatasource tunerDatasource) {
        this.tunerDatasource = tunerDatasource;
        this.errorFunction = new ErrorFunction();
    }

    public void tuneEvalVector() {
        long start = System.currentTimeMillis();
        LOGGER.info("tuning started");
        double averageError = calculateAverageError();
        LOGGER.info("average error: " + averageError);
        long end = System.currentTimeMillis();
        LOGGER.info("tuning complete in {} seconds", (end-start)/1000);
    }

    public double calculateAverageError() { // TODO: w.r.t. eval vector
        tunerDatasource.markAllRecordsAsUnprocessed();
        List<GameRecord> gameRecords = tunerDatasource.getGameRecords(true);

        while (gameRecords.size() > 0) {
            gameRecords.forEach(this::processGameRecord);
            gameRecords = tunerDatasource.getGameRecords(true);
        }

        return tunerDatasource.getAverageError();
    }

    private void processGameRecord(GameRecord gameRecord) {
        Board board = new Board(gameRecord.getFen());
        double error = errorFunction.calculateError(board, gameRecord.getGameResult());
        tunerDatasource.updateError(gameRecord.getFen(), (float)error); // sets "processed" flag
    }
}
