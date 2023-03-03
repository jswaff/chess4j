package com.jamesswafford.chess4j.tuner;

import com.jamesswafford.chess4j.Constants;
import com.jamesswafford.chess4j.Globals;
import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.eval.Eval;
import com.jamesswafford.chess4j.search.AlphaBetaSearch;
import com.jamesswafford.chess4j.search.Search;
import com.jamesswafford.chess4j.search.SearchParameters;
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

        List<GameRecord> gameRecords = tunerDatasource.getGameRecords(true);
        Collections.shuffle(gameRecords);
        SearchParameters parameters = new SearchParameters(depth, -Constants.CHECKMATE, Constants.CHECKMATE);

        for (int i=0;i< gameRecords.size();i++) {
            GameRecord gameRecord = gameRecords.get(i);
            if (i % 1000 == 0) {
                LOGGER.info("\t {} of {} {}", +i, gameRecords.size(), gameRecord.getFen());
            }
            Board board = new Board(gameRecord.getFen());
            search.initialize();
            //int score = search.search(board, parameters);
            int score = Eval.eval(Globals.getEvalWeights(), board);
            tunerDatasource.updateEval(gameRecord.getFen(), score);
        }
    }

}
