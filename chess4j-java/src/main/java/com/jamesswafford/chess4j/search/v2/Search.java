package com.jamesswafford.chess4j.search.v2;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Color;
import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.board.Undo;
import com.jamesswafford.chess4j.eval.Evaluator;
import com.jamesswafford.chess4j.init.Initializer;
import com.jamesswafford.chess4j.io.FenBuilder;
import com.jamesswafford.chess4j.movegen.MoveGenerator;
import com.jamesswafford.chess4j.search.KillerMoves;
import com.jamesswafford.chess4j.search.KillerMovesStore;
import com.jamesswafford.chess4j.utils.BoardUtils;
import com.jamesswafford.chess4j.utils.MoveUtils;
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
    private final MoveScorer moveScorer;
    private final KillerMovesStore killerMovesStore;
    private final SearchStats searchStats;
    private final List<Move> lastPV;

    public Search(Board board, List<Undo> undos, Evaluator evaluator, MoveGenerator moveGenerator,
                  MoveScorer moveScorer, KillerMovesStore killerMovesStore) {
        this.board = board.deepCopy();
        this.undos = new ArrayList<>(undos);
        this.evaluator = evaluator;
        this.moveGenerator = moveGenerator;
        this.moveScorer = moveScorer;
        this.killerMovesStore = killerMovesStore;
        this.searchStats = new SearchStats();
        this.lastPV = new ArrayList<>();
    }

    public SearchStats getSearchStats() {
        return searchStats;
    }

    public List<Move> getLastPV() { return Collections.unmodifiableList(lastPV); }

    public int search(SearchParameters searchParameters) {
        if (Initializer.nativeCodeInitialized()) {
            return searchWithNativeCode(searchParameters);
        } else {
            return searchWithJavaCode(searchParameters);
        }
    }

    private int searchWithJavaCode(SearchParameters searchParameters) {
        killerMovesStore.clear();
        return search(lastPV, 0, searchParameters.getDepth(),
                searchParameters.getAlpha(), searchParameters.getBeta());
    }

    private int searchWithNativeCode(SearchParameters searchParameters) {
        String fen = FenBuilder.createFen(board, false);
        List<Long> nativePV = new ArrayList<>();
        try {
            int nativeScore = searchNative(fen, nativePV, searchParameters.getDepth(),
                    searchParameters.getAlpha(), searchParameters.getBeta(), searchStats);

            assert (searchesAreEqual(fen, searchParameters, nativeScore, nativePV));

            // translate the native PV into the object's PV
            lastPV.clear();
            for (int i=0; i<nativePV.size(); i++) {
                Long nativeMv = nativePV.get(i);
                // which side is moving?  On even moves it is the player on move.
                Color ptm = (i % 2) == 0 ? board.getPlayerToMove() : Color.swap(board.getPlayerToMove());
                lastPV.add(MoveUtils.convertNativeMove(nativeMv, ptm));
            }

            return nativeScore;
        } catch (IllegalStateException e) {
            LOGGER.error(e);
            throw e;
        }
    }

    private boolean searchesAreEqual(String fen, SearchParameters searchParameters, int nativeScore,
                                     List<Long> nativePV) {
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
            // compare the PVs.
            if (!moveLinesAreEqual(nativePV, lastPV)) {
                LOGGER.error("pvs are not equal!"
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

    private boolean moveLinesAreEqual(List<Long> nativePV, List<Move> javaPV) {
        if (nativePV.size() != lastPV.size()) {
            LOGGER.error("nativePV.size: " + nativePV.size() + ", javaPV.size: " + javaPV.size());
            return false;
        }

        for (int i=0; i<nativePV.size(); i++) {
            Long nativeMv = nativePV.get(i);
            // which side is moving?  On even moves it is the player on move.
            Color ptm = (i % 2) == 0 ? board.getPlayerToMove() : Color.swap(board.getPlayerToMove());
            Move convertedMv = MoveUtils.convertNativeMove(nativeMv, ptm);
            Move javaMv = javaPV.get(i);
            if (! javaMv.equals(convertedMv)) {
                return false;
            }
        }

        return true;
    }

    private int search(List<Move> parentPV, int ply, int depth, int alpha, int beta) {

        searchStats.nodes++;
        parentPV.clear();

        if (depth == 0) {
            return evaluator.evaluateBoard(board);
        }

        // Draw check
        /*if (Draw.isDraw(board, undos)) {
            return 0;
        }*/

        List<Move> pv = new ArrayList<>(50);

        int numMovesSearched = 0;
        MoveOrderer moveOrderer = new MoveOrderer(board, moveGenerator, moveScorer,
                killerMovesStore.getKiller1(ply), killerMovesStore.getKiller2(ply));
        Move move;

        while ((move = moveOrderer.selectNextMove()) != null) {
            assert(BoardUtils.isPseudoLegalMove(board, move));

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
                if (move.captured()==null && move.promotion()==null) {
                    killerMovesStore.addKiller(ply, move);
                }
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

    private native int searchNative(String boardFen, List<Long> parentPV, int depth, int alpha, int beta,
                                    SearchStats searchStats);

}
