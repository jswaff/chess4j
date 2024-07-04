package com.jamesswafford.chess4j.nn;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.eval.Eval;
import com.jamesswafford.chess4j.eval.EvalWeights;
import com.jamesswafford.chess4j.io.FENRecord;
import com.jamesswafford.chess4j.search.AlphaBetaSearch;
import com.jamesswafford.chess4j.search.Search;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class FENLabeler {

    private static final Logger LOGGER = LogManager.getLogger(FENLabeler.class);

    private final Search search;

    public FENLabeler() {
        this.search = new AlphaBetaSearch();
        search.setSkipTimeChecks(true);
    }

    public void label(List<FENRecord> fenRecords, int depth) {
        LOGGER.info("labeling {} fen records to depth {}", fenRecords.size(), depth);
        for (FENRecord fenRecord : fenRecords) {
            label(fenRecord, depth);
        }
    }

    public void label(FENRecord fenRecord, int depth) {
        Board board = new Board(fenRecord.getFen());
        //search.initialize();
        //int score = search.search(board, parameters);
        EvalWeights trainingWeights = new EvalWeights();
        int score = Eval.eval(trainingWeights, board);
        fenRecord.setEval(score);
    }
}
