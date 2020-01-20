package com.jamesswafford.chess4j.search;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.board.Undo;
import com.jamesswafford.chess4j.eval.Evaluator;
import com.jamesswafford.chess4j.init.Initializer;
import com.jamesswafford.chess4j.io.FenBuilder;
import com.jamesswafford.chess4j.movegen.MoveGenerator;
import com.jamesswafford.chess4j.utils.BoardUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

import static com.jamesswafford.chess4j.Constants.CHECKMATE;

public class AlphaBetaSearch {

    private static final Log LOGGER = LogFactory.getLog(AlphaBetaSearch.class);

    static {
        Initializer.init();
    }

    private final Board board;
    private final SearchParameters searchParameters;
    private final Evaluator evaluator;
    private final MoveGenerator moveGenerator;

    public AlphaBetaSearch(Board board, SearchParameters searchParameters, Evaluator evaluator,
                           MoveGenerator moveGenerator) {
        this.board = board;
        this.searchParameters = searchParameters;
        this.evaluator = evaluator;
        this.moveGenerator = moveGenerator;
    }

    public int search(boolean useNative) {
        if (useNative && Initializer.useNative()) {
            return searchWithNativeCode();
        } else {
            return searchWithJavaCode();
        }
    }

    private int searchWithJavaCode() {
        long startTime = System.currentTimeMillis();
        LOGGER.debug("# performing depth " + searchParameters.getDepth() + " search with java code.");
        int javaScore = search(0, searchParameters.getDepth(), searchParameters.getAlpha(),
                searchParameters.getBeta());
        LOGGER.debug("# ... finished java search in " + (System.currentTimeMillis() - startTime) + " ms.");
        return javaScore;
    }

    private int searchWithNativeCode() {
        long startTime = System.currentTimeMillis();
        LOGGER.debug("# performing depth " + searchParameters.getDepth() + " search with native code.");
        String fen = FenBuilder.createFen(board, false);
        try {
            int nativeScore = searchNative(fen, searchParameters.getDepth(),
                    searchParameters.getAlpha(), searchParameters.getBeta());
            LOGGER.debug("# ... finished native search in " + (System.currentTimeMillis() - startTime) + " ms.");
            assert (searchesAreEqual(nativeScore, fen));
            return nativeScore;
        } catch (IllegalStateException e) {
            LOGGER.error(e);
            throw e;
        }
    }

    private boolean searchesAreEqual(int nativeScore, String fen) {
        try {
            int javaScore = searchWithJavaCode();
            if (javaScore != nativeScore) {
                LOGGER.error("searches not equal!  javaScore: " + javaScore + ", nativeScore: " + nativeScore +
                        ", params: " + searchParameters + ", fen: " + fen);
                return false;
            }
            return true;
        } catch (IllegalStateException e) {
            LOGGER.error(e);
            throw e;
        }
    }

    private int search(int ply, int depth, int alpha, int beta) {

        if (depth == 0) {
            return evaluator.evaluateBoard(board);
        }

        List<Move> moves = moveGenerator.generatePseudoLegalMoves(board);

        int numMovesSearched = 0;
        for (Move move : moves) {
            Undo undo = board.applyMove(move);
            // check if move was legal
            if (BoardUtils.isOpponentInCheck(board)) {
                board.undoMove(undo);
                continue;
            }

            // TODO: undo should be passed through for draw checks
            int val = -search(ply+1, depth-1, -beta, -alpha);
            ++numMovesSearched;
            board.undoMove(undo);
            if (val >= beta) {
                return beta;
            }
            if (val > alpha) {
                alpha = val;
            }
        }

        alpha = adjustFinalScoreForMates(alpha, numMovesSearched, ply);

        return alpha;
    }

    private int adjustFinalScoreForMates(int score, int numMovesSearched, int ply) {
        int adjScore = score;

        if (numMovesSearched==0) {
            if (BoardUtils.isPlayerInCheck(board)) {
                adjScore = -CHECKMATE + ply;
            } else {
                // draw score
                adjScore = 0;
            }
        }

        return adjScore;
    }

    private native int searchNative(String boardFen, int depth, int alpha, int beta);

}
