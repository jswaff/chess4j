package com.jamesswafford.chess4j.search;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Undo;
import com.jamesswafford.chess4j.eval.Eval;
import com.jamesswafford.chess4j.eval.Evaluator;

import java.util.List;

// TODO: implement Search interface?
public class QSearch {

    private final Search parentSearch;
    private final SearchStats searchStats;

    private boolean stop;
    private boolean skipTimeChecks;
    private Evaluator evaluator;

    public QSearch(Search parentSearch, SearchStats searchStats) {
        this.parentSearch = parentSearch;
        this.searchStats = searchStats;
        this.stop = false;
        this.evaluator = new Eval();
    }

    public int quiescenceSearch(Board board, List<Undo> undos, int alpha, int beta, boolean inCheck,
                                SearchOptions opts) {

        assert(alpha < beta);

        searchStats.qnodes++;

        // time check
//        if (!skipTimeChecks && stopSearchOnTime(opts)) {
//            stop = true;
//            parentSearch.stop();
//            return 0;
//        }

        int standPat = evaluator.evaluateBoard(board);
        if (standPat > alpha) {
            if (standPat >= beta) {
                return beta;
            }
            // our static evaluation will serve as the lower bound
            alpha = standPat;
        }

        return alpha;
    }


    // TODO: this duplicates code in AlphaBetaSearch
    private boolean stopSearchOnTime(SearchOptions opts) {

        // if we don't have a stop time, nevermind!
        if (opts.getStopTime() == 0) {
            return false;
        }

        // avoid doing expensive time checks too often
        long visitedNodes = searchStats.nodes + searchStats.qnodes;
        if (visitedNodes - opts.getNodeCountLastTimeCheck() < opts.getNodesBetweenTimeChecks()) {
            return false;
        }

        // ok, time check
        opts.setNodeCountLastTimeCheck(visitedNodes);

        return System.currentTimeMillis() >= opts.getStopTime();
    }

}
