package com.jamesswafford.chess4j.search;

import com.jamesswafford.chess4j.board.*;
import com.jamesswafford.chess4j.eval.Eval;
import com.jamesswafford.chess4j.eval.Evaluator;
import com.jamesswafford.chess4j.init.Initializer;
import com.jamesswafford.chess4j.io.FenBuilder;
import com.jamesswafford.chess4j.movegen.MagicBitboardMoveGenerator;
import com.jamesswafford.chess4j.movegen.MoveGenerator;
import com.jamesswafford.chess4j.utils.BoardUtils;
import com.jamesswafford.chess4j.utils.MoveUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.jamesswafford.chess4j.Constants.CHECKMATE;

public class AlphaBetaSearch implements Search {

    private static final  Logger LOGGER = LogManager.getLogger(AlphaBetaSearch.class);

    static {
        Initializer.init();
    }

    private final List<Move> pv;
    private final List<Move> lastPv;
    private final SearchStats searchStats;

    private boolean stop;
    private boolean skipTimeChecks;
    private Evaluator evaluator;
    private MoveGenerator moveGenerator;
    private MoveScorer moveScorer;
    private KillerMovesStore killerMovesStore;

    public AlphaBetaSearch() {
        this.pv = new ArrayList<>();
        this.lastPv = new ArrayList<>();
        this.searchStats = new SearchStats();

        unstop();
        this.evaluator = new Eval();
        this.moveGenerator = new MagicBitboardMoveGenerator();
        this.moveScorer = new MVVLVA();
        this.killerMovesStore = KillerMoves.getInstance();

        if (Initializer.nativeCodeInitialized()) {
            initializeNativeSearch();
        }
    }
    public SearchStats getSearchStats() {
        return searchStats;
    }

    public List<Move> getPv() { return Collections.unmodifiableList(pv); }

    @Override
    public void initialize() {
        lastPv.clear();
        searchStats.initialize();
        if (Initializer.nativeCodeInitialized()) {
            initializeNativeSearch();
        }
    }

    public void setEvaluator(Evaluator evaluator) {
        this.evaluator = evaluator;
    }

    public void setMoveGenerator(MoveGenerator moveGenerator) {
        this.moveGenerator = moveGenerator;
    }

    public void setMoveScorer(MoveScorer moveScorer) {
        this.moveScorer = moveScorer;
    }

    public void setKillerMovesStore(KillerMovesStore killerMovesStore) {
        this.killerMovesStore = killerMovesStore;
    }

    @Override
    public int search(Board board, SearchParameters searchParameters) {
        return search(board, searchParameters, SearchOptions.builder().startTime(System.currentTimeMillis()).build());
    }

    @Override
    public int search(Board board, SearchParameters searchParameters, SearchOptions opts) {
        return search(board, new ArrayList<>(), searchParameters, opts);
    }

    @Override
    public int search(Board board, List<Undo> undos, SearchParameters searchParameters) {
        return search(board, undos, searchParameters,
                SearchOptions.builder().startTime(System.currentTimeMillis()).build());
    }

    @Override
    public int search(Board board, List<Undo> undos, SearchParameters searchParameters, SearchOptions opts) {
        if (!opts.isAvoidNative() && Initializer.nativeCodeInitialized()) {
            return searchWithNativeCode(board, undos, searchParameters, opts);
        } else {
            return searchWithJavaCode(board, undos, searchParameters, opts);
        }
    }

    @Override
    public boolean isStopped() {
        return stop;
    }

    @Override
    public void stop() {
        stop = true;
        if (Initializer.nativeCodeInitialized()) {
            stopNative(true);
        }
    }

    @Override
    public void unstop() {
        stop = false;
        if (Initializer.nativeCodeInitialized()) {
            stopNative(false);
        }
    }

    @Override
    public void setSkipTimeChecks(boolean skipTimeChecks) {
        this.skipTimeChecks = skipTimeChecks;
        if (Initializer.nativeCodeInitialized()) {
            skipTimeChecksNative(skipTimeChecks);
        }
    }

    private int searchWithJavaCode(Board board, List<Undo> undos, SearchParameters searchParameters,
                                   SearchOptions opts) {
        killerMovesStore.clear();
        int score = search(board, undos, pv, true, 0, searchParameters.getDepth(),
                searchParameters.getAlpha(), searchParameters.getBeta(), opts);
        lastPv.clear();
        lastPv.addAll(pv);
        return score;
    }

    private int searchWithNativeCode(Board board, List<Undo> undos, SearchParameters searchParameters,
                                     SearchOptions opts) {

        String fen = FenBuilder.createFen(board, true);

        List<Long> prevMoves = undos.stream()
                .map(undo -> MoveUtils.toNativeMove(undo.getMove()))
                .collect(Collectors.toList());

        List<Long> nativePV = new ArrayList<>();
        SearchStats nativeStats = new SearchStats(searchStats);

        try {

            int nativeScore = searchNative(fen, prevMoves, nativePV, searchParameters.getDepth(),
                    searchParameters.getAlpha(), searchParameters.getBeta(), nativeStats, opts.getStartTime(),
                    opts.getStopTime());

            // if the search completed then verify equality with the Java implementation.
            assert (stop || searchesAreEqual(board, undos, searchParameters, opts, fen, nativeScore, nativePV,
                    nativeStats));

            // set the object's stats to the native stats
            searchStats.set(nativeStats);

            // translate the native PV into the object's PV
            pv.clear();
            pv.addAll(MoveUtils.fromNativeLine(nativePV, board.getPlayerToMove()));

            return nativeScore;
        } catch (IllegalStateException e) {
            LOGGER.error(e);
            throw e;
        }
    }

    private boolean searchesAreEqual(Board board, List<Undo> undos, SearchParameters searchParameters,
                                     SearchOptions opts, String fen, int nativeScore, List<Long> nativePV,
                                     SearchStats nativeStats)
    {
        LOGGER.debug("# checking search equality with java depth {}", searchParameters.getDepth());
        try {
            int javaScore = searchWithJavaCode(board, undos, searchParameters, opts);

            // if the search was interrupted we can't compare
            if (stop) return true;

            if (javaScore != nativeScore || !searchStats.equals(nativeStats)) {
                LOGGER.error("searches not equal!  javaScore: " + javaScore + ", nativeScore: " + nativeScore
                        + ", java stats: " + searchStats + ", native stats: " + nativeStats
                        + ", params: " + searchParameters + ", fen: " + fen);
                return false;
            }
            // compare the PVs.
            if (!pv.equals(MoveUtils.fromNativeLine(nativePV, board.getPlayerToMove()))) {
                LOGGER.error("pvs are not equal!"
                        + ", java stats: " + searchStats + ", native stats: " + nativeStats
                        + ", params: " + searchParameters + ", fen: " + fen);
                return false;
            }

            LOGGER.debug("# finished - searches are equivalent");
            return true;
        } catch (IllegalStateException e) {
            LOGGER.error(e);
            throw e;
        }
    }

    private int search(Board board, List<Undo> undos, List<Move> parentPV, boolean first, int ply, int depth,
                       int alpha, int beta, SearchOptions opts) {

        searchStats.nodes++;
        parentPV.clear();

        // time check
        if (!skipTimeChecks && stopSearchOnTime(opts)) {
            stop = true;
            return 0;
        }

        // base case
        if (depth == 0) {
            return quiescenceSearch(board, undos, alpha, beta, opts);
        }

        // try for early exit
        if (ply > 0) {
            // Draw check
            if (Draw.isDraw(board, undos)) {
                searchStats.draws++;
                return 0;
            }
        }

        List<Move> pv = new ArrayList<>(50);

        int numMovesSearched = 0;
        Move pvMove = first && lastPv.size() > ply ? lastPv.get(ply) : null;
        MoveOrderer moveOrderer = new MoveOrderer(board, moveGenerator, moveScorer,
                pvMove, killerMovesStore.getKiller1(ply), killerMovesStore.getKiller2(ply), true);
        Move move;

        while ((move = moveOrderer.selectNextMove()) != null) {
            assert(BoardUtils.isPseudoLegalMove(board, move));

            undos.add(board.applyMove(move));
            // check if move was legal
            if (BoardUtils.isOpponentInCheck(board)) {
                board.undoMove(undos.remove(undos.size()-1));
                continue;
            }

            boolean pvNode = first && numMovesSearched == 0;
            int val = -search(board, undos, pv, pvNode, ply+1, depth-1,  -beta, -alpha, opts);
            ++numMovesSearched;
            board.undoMove(undos.remove(undos.size()-1));

            // if the search was stopped we can't trust these results, so don't update the PV
            if (stop) {
                return 0;
            }

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
                if (opts.getPvCallback() != null) {
                    opts.getPvCallback().accept(
                            PvCallbackDTO.builder()
                                    .ply(ply).pv(parentPV).depth(depth).score(alpha)
                                    .elapsedMS(System.currentTimeMillis() - opts.getStartTime())
                                    .nodes(searchStats.nodes)
                                    .build());
                }
            }
        }

        alpha = adjustFinalScoreForMates(board, alpha, numMovesSearched, ply);

        return alpha;
    }

    public int quiescenceSearch(Board board, List<Undo> undos, int alpha, int beta, SearchOptions opts) {

        assert(alpha < beta);

        searchStats.qnodes++;

        // time check
        if (!skipTimeChecks && stopSearchOnTime(opts)) {
            stop = true;
            return 0;
        }

        int standPat = evaluator.evaluateBoard(board);
        if (standPat > alpha) {
            if (standPat >= beta) {
                return beta;
            }
            // our static evaluation will serve as the lower bound
            alpha = standPat;
        }

        MoveOrderer moveOrderer = new MoveOrderer(board, moveGenerator, moveScorer,
                null, null, null, false);
        Move move;

        while ((move = moveOrderer.selectNextMove()) != null) {
            assert(BoardUtils.isPseudoLegalMove(board, move));

            undos.add(board.applyMove(move));
            // check if move was legal
            if (BoardUtils.isOpponentInCheck(board)) {
                board.undoMove(undos.remove(undos.size()-1));
                continue;
            }

            // TODO: possibly prune

            // TODO: recurse
            int val = alpha;
            board.undoMove(undos.remove(undos.size()-1));

            // if the search was stopped we can't trust these results, so don't update the PV
            if (stop) {
                return 0;
            }

            if (val >= beta) {
                searchStats.failHighs++;
                return beta;
            }
            if (val > alpha) {
                alpha = val;
            }
        }

        return alpha;
    }

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
        opts.setNodeCountLastTimeCheck(visitedNodes); // TODO: don't use options for state?

        return System.currentTimeMillis() >= opts.getStopTime();
    }

    private int adjustFinalScoreForMates(Board board, int score, int numMovesSearched, int ply) {
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

    private native void initializeNativeSearch();

    private native int searchNative(String boardFen, List<Long> prevMoves, List<Long> parentPV, int depth,
                                    int alpha, int beta, SearchStats searchStats, long startTime,
                                    long stopTime);

    private native void stopNative(boolean stop);

    private native void skipTimeChecksNative(boolean skipTimeChecks);

}
