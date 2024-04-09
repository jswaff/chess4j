package com.jamesswafford.chess4j.tuner;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.eval.Eval;
import com.jamesswafford.chess4j.eval.EvalWeights;
import com.jamesswafford.chess4j.io.FENRecord;
import com.jamesswafford.chess4j.search.AlphaBetaSearch;
import com.jamesswafford.chess4j.search.Search;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.List;

public class EvalTuner {

    private static final Logger LOGGER = LogManager.getLogger(EvalTuner.class);

    private final TunerDatasource tunerDatasource;
    private final Search search;

    public EvalTuner(TunerDatasource tunerDatasource) {
        this.tunerDatasource = tunerDatasource;
        this.search = new AlphaBetaSearch();
        search.setSkipTimeChecks(true);
    }

    public void eval(int depth) {
        LOGGER.info("# evaluating tuner records to depth {}", depth);

        List<FENRecord> fenRecords = tunerDatasource.getGameRecords(true);
        Collections.shuffle(fenRecords);
        //SearchParameters parameters = new SearchParameters(depth, -Constants.CHECKMATE, Constants.CHECKMATE);

        for (int i = 0; i< fenRecords.size(); i++) {
            FENRecord fenRecord = fenRecords.get(i);
            if (i % 1000 == 0) {
                LOGGER.info("\t {} of {} {}", +i, fenRecords.size(), fenRecord.getFen());
            }
            Board board = new Board(fenRecord.getFen());
            //search.initialize();
            //int score = search.search(board, parameters);
            EvalWeights trainingWeights = new EvalWeights();
            int score = Eval.eval(trainingWeights, board, false, false);
            tunerDatasource.updateEval(fenRecord.getFen(), score);
        }
    }
}
