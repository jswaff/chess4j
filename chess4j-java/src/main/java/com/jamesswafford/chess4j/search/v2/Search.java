package com.jamesswafford.chess4j.search.v2;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.jamesswafford.chess4j.Constants.CHECKMATE;

public class Search {

    private static final Log LOGGER = LogFactory.getLog(Search.class);

    static {
        Initializer.init();
    }

    private final Board board;
    private final List<Undo> undos;
    private final Evaluator evaluator;
    private final MoveGenerator moveGenerator;
    private final SearchStats searchStats;
    private final List<Move> lastPV;

    public Search(Board board, List<Undo> undos, Evaluator evaluator, MoveGenerator moveGenerator) {
        this.board = board;
        this.undos = new ArrayList<>(undos);
        this.evaluator = evaluator;
        this.moveGenerator = moveGenerator;
        this.searchStats = new SearchStats();
        this.lastPV = new ArrayList<>();
    }

    public SearchStats getSearchStats() {
        return searchStats;
    }

    public List<Move> getLastPV() { return Collections.unmodifiableList(lastPV); }

    public int search(boolean useNative, SearchParameters searchParameters) {
        if (useNative && Initializer.useNative()) {
            return searchWithNativeCode(searchParameters);
        } else {
            return searchWithJavaCode(searchParameters);
        }
    }

    private int searchWithJavaCode(SearchParameters searchParameters) {
        lastPV.clear();
        return search(lastPV, 0, searchParameters.getDepth(),
                searchParameters.getAlpha(), searchParameters.getBeta());
    }

    private int searchWithNativeCode(SearchParameters searchParameters) {
        String fen = FenBuilder.createFen(board, false);
        try {
            int nativeScore = searchNative(fen, searchParameters.getDepth(),
                    searchParameters.getAlpha(), searchParameters.getBeta(), searchStats);
            assert (searchesAreEqual(searchParameters, nativeScore, fen));
            return nativeScore;
        } catch (IllegalStateException e) {
            LOGGER.error(e);
            throw e;
        }
    }

    private boolean searchesAreEqual(SearchParameters searchParameters, int nativeScore, String fen) {
        try {
            // copy the search stats for comparison
            SearchStats nativeStats = new SearchStats();
            nativeStats.nodes = searchStats.nodes;
            nativeStats.failHighs = searchStats.failHighs;

            searchStats.initialize();
            int javaScore = searchWithJavaCode(searchParameters);
            if (javaScore != nativeScore || !searchStats.equals(nativeStats)) {
                LOGGER.error("searches not equal!  javaScore: " + javaScore + ", nativeScore: " + nativeScore
                        + ", java stats: " + searchStats + ", native stats: " + nativeStats
                        + ", params: " + searchParameters + ", fen: " + fen);
                return false;
            }
            return true;
        } catch (IllegalStateException e) {
            LOGGER.error(e);
            throw e;
        }
    }

    private int search(List<Move> parentPV, int ply, int depth, int alpha, int beta) {

        searchStats.nodes++;

        if (depth == 0) {
            return evaluator.evaluateBoard(board);
        }

        // Draw check
        /*if (Draw.isDraw(board, undos)) {
            return 0;
        }*/

        List<Move> moves = moveGenerator.generatePseudoLegalMoves(board);
        List<Move> pv = new ArrayList<>(50);

        int numMovesSearched = 0;
        for (Move move : moves) {
            undos.add(board.applyMove(move));
            // check if move was legal
            if (BoardUtils.isOpponentInCheck(board)) {
                board.undoMove(undos.remove(undos.size()-1));
                continue;
            }

            // TODO: undo should be passed through for draw checks
            int val = -search(pv, ply+1, depth-1, -beta, -alpha);
            ++numMovesSearched;
            board.undoMove(undos.remove(undos.size()-1));
            if (val >= beta) {
                searchStats.failHighs++;
                return beta;
            }
            if (val > alpha) {
                alpha = val;
                setParentPV(parentPV, move, pv);
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

    private void setParentPV(List<Move> parentPV, Move head, List<Move> tail) {
        parentPV.clear();
        parentPV.add(head);
        parentPV.addAll(tail);
    }

    private native int searchNative(String boardFen, int depth, int alpha, int beta, SearchStats searchStats);

}
