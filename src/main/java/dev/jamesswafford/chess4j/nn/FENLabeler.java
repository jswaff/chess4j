package dev.jamesswafford.chess4j.nn;

import dev.jamesswafford.chess4j.Constants;
import dev.jamesswafford.chess4j.Globals;
import dev.jamesswafford.chess4j.board.Board;
import dev.jamesswafford.chess4j.board.Move;
import dev.jamesswafford.chess4j.eval.Eval;
import dev.jamesswafford.chess4j.exceptions.LabelingException;
import dev.jamesswafford.chess4j.hash.TTHolder;
import dev.jamesswafford.chess4j.io.FENBuilder;
import dev.jamesswafford.chess4j.io.FENRecord;
import dev.jamesswafford.chess4j.search.*;
import io.vavr.Tuple2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class FENLabeler {

    private static final Logger LOGGER = LogManager.getLogger(FENLabeler.class);

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
        SearchIterator searchIterator = new SearchIteratorImpl();
        searchIterator.setEarlyExitOk(false);
        searchIterator.setSkipTimeChecks(true);
        searchIterator.setPost(false);

        Board board = new Board(fenRecord.getFen());

        // find a quiet position
        if (depth > 0) {
            TTHolder.getInstance().clearTables();
            LOGGER.debug("# searching fen {} to depth {}", fenRecord.getFen(), depth);
            searchIterator.setMaxDepth(depth);
            searchIterator.setMaxNodes(0);
            Tuple2<List<Move>, Integer> pv;
            try {
                pv = searchIterator.findPvFuture(board, new ArrayList<>()).get();
            } catch (InterruptedException | ExecutionException e) {
                throw new LabelingException(e);
            }
            for (Move pvMove : pv._1) {
                board.applyMove(pvMove);
            }
            String pvFen = FENBuilder.createFen(board, false);
            LOGGER.debug("# pvFen: {}", pvFen);
            fenRecord.setFen(pvFen);
        }

        // score the position
        int score;
        if (nodeLimit == 0) {
            score = Eval.eval(Globals.getEvalWeights(), board);
        } else {
            LOGGER.debug("# searching with nodeLimit {}", nodeLimit);
            searchIterator.setMaxDepth(0);
            searchIterator.setMaxNodes(nodeLimit);
            Tuple2<List<Move>, Integer> pv;
            try {
                pv = searchIterator.findPvFuture(board, new ArrayList<>()).get();
            } catch (InterruptedException | ExecutionException e) {
                throw new LabelingException(e);
            }

            score = pv._2;
        }

        fenRecord.setEval(score);
    }
}
