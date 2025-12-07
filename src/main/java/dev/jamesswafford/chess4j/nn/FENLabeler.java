package dev.jamesswafford.chess4j.nn;

import dev.jamesswafford.chess4j.Globals;
import dev.jamesswafford.chess4j.board.Board;
import dev.jamesswafford.chess4j.board.Move;
import dev.jamesswafford.chess4j.eval.Eval;
import dev.jamesswafford.chess4j.exceptions.LabelingException;
import dev.jamesswafford.chess4j.hash.TTHolder;
import dev.jamesswafford.chess4j.io.FENBuilder;
import dev.jamesswafford.chess4j.io.FENRecord;
import dev.jamesswafford.chess4j.search.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class FENLabeler {

    private final SearchIterator searchIterator;

    public FENLabeler() {
        searchIterator = new SearchIteratorImpl();
        searchIterator.setEarlyExitOk(false);
        searchIterator.setSkipTimeChecks(true);
    }

    /**
     * Label CSV records.
     * There are two steps to the labeling process.  The first is to find a quiet position.  This is done by
     * performing an iterative search and following the PV to the terminal position.  Once that is complete,
     * a second search is performed using the nodeLimit parameter.
     *
     * @param fenRecord - starting position
     * @param depth - depth to search to find a quiet position
     * @param nodeLimit - node limit to search from the quiet position
     */
    public void label(FENRecord fenRecord, int depth, long nodeLimit) {
        Board board = new Board(fenRecord.getFen());

        // find a quiet position
        if (depth > 0) {
            TTHolder.getInstance().clearTables();
            searchIterator.setMaxDepth(depth);
            searchIterator.setMaxNodes(0);
            List<Move> pv;
            try {
                pv = searchIterator.findPvFuture(board, new ArrayList<>()).get();
            } catch (InterruptedException | ExecutionException e) {
                throw new LabelingException(e);
            }
            for (Move pvMove : pv) {
                board.applyMove(pvMove);
            }
            String pvFen = FENBuilder.createFen(board, false);
            fenRecord.setFen(pvFen);
        }

        // score the position
        int score;
        if (nodeLimit == 0) {
            score = Eval.eval(Globals.getEvalWeights(), board);
        } else {
            searchIterator.setMaxDepth(0);
            searchIterator.setMaxNodes(nodeLimit);
            List<Move> pv;
            try {
                pv = searchIterator.findPvFuture(board, new ArrayList<>()).get();
            } catch (InterruptedException | ExecutionException e) {
                throw new LabelingException(e);
            }

            score = 0; // FIXME
        }

        fenRecord.setEval(score);
    }
}
