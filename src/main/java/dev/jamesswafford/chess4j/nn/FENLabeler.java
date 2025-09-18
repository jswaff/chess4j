package dev.jamesswafford.chess4j.nn;

import dev.jamesswafford.chess4j.Globals;
import dev.jamesswafford.chess4j.board.Board;
import dev.jamesswafford.chess4j.board.Move;
import dev.jamesswafford.chess4j.eval.Eval;
import dev.jamesswafford.chess4j.hash.TTHolder;
import dev.jamesswafford.chess4j.io.FENBuilder;
import dev.jamesswafford.chess4j.io.FENRecord;
import dev.jamesswafford.chess4j.search.AlphaBetaSearch;
import dev.jamesswafford.chess4j.search.Search;
import dev.jamesswafford.chess4j.search.SearchParameters;
import dev.jamesswafford.chess4j.utils.MoveUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

import static dev.jamesswafford.chess4j.Constants.CHECKMATE;

public class FENLabeler {

    private static final Logger LOGGER = LogManager.getLogger(FENLabeler.class);

    private final Search search;

    public FENLabeler() {
        this.search = new AlphaBetaSearch();
        search.setSkipTimeChecks(true);
    }

    public void label(FENRecord fenRecord, int depth) {
        Board board = new Board(fenRecord.getFen());
        int score;
        if (depth < 0) {
            score = Eval.eval(Globals.getEvalWeights(), board);
        } else {
            SearchParameters parameters = new SearchParameters(depth, -CHECKMATE, CHECKMATE);
            TTHolder.getInstance().clearTables();
            search.initialize();
            score = search.search(board, parameters);

            List<Move> pv = search.getPv();
            if (pv.size() >= depth) { // if it hasn't been truncated, perhaps by a hash hit
                for (Move pvMove : pv) {
                    board.applyMove(pvMove);
                }
                String pvFen = FENBuilder.createFen(board, false);
                fenRecord.setFen(pvFen);
                // TODO: try a fixed node search
                parameters = new SearchParameters(3, -CHECKMATE, CHECKMATE);
                score = search.search(board, parameters);
            }
        }

        fenRecord.setEval(score);
    }
}
