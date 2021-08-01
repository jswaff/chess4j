package com.jamesswafford.chess4j.search;

import com.jamesswafford.chess4j.board.*;
import com.jamesswafford.chess4j.board.squares.Square;
import com.jamesswafford.chess4j.eval.Eval;
import com.jamesswafford.chess4j.eval.Evaluator;
import com.jamesswafford.chess4j.hash.TTHolder;
import com.jamesswafford.chess4j.hash.TranspositionTableEntry;
import com.jamesswafford.chess4j.hash.TranspositionTableEntryType;
import com.jamesswafford.chess4j.init.Initializer;
import com.jamesswafford.chess4j.io.DrawBoard;
import com.jamesswafford.chess4j.movegen.MagicBitboardMoveGenerator;
import com.jamesswafford.chess4j.movegen.MoveGenerator;
import com.jamesswafford.chess4j.utils.BoardUtils;
import com.jamesswafford.chess4j.utils.MoveUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.jamesswafford.chess4j.Constants.CHECKMATE;
import static com.jamesswafford.chess4j.hash.TranspositionTableEntryType.*;

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
    private KillerMovesStore killerMovesStore;

    public AlphaBetaSearch() {
        this.pv = new ArrayList<>();
        this.lastPv = new ArrayList<>();
        this.searchStats = new SearchStats();

        unstop();
        this.evaluator = new Eval();
        this.moveGenerator = new MagicBitboardMoveGenerator();
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
        boolean inCheck = BoardUtils.isPlayerInCheck(board);
        int score = search(board, undos, pv, true, 0, searchParameters.getDepth(),
                searchParameters.getAlpha(), searchParameters.getBeta(), inCheck, false, opts);
        lastPv.clear();
        lastPv.addAll(pv);
        return score;
    }

    private int searchWithNativeCode(Board board, List<Undo> undos, SearchParameters searchParameters,
                                     SearchOptions opts) {

        List<Long> nativePV = new ArrayList<>();
        SearchStats nativeStats = new SearchStats(searchStats);

        try {
            assert(clearTableWrapper());
            int nativeScore = searchNative(board, nativePV, searchParameters.getDepth(), searchParameters.getAlpha(),
                    searchParameters.getBeta(), nativeStats, opts.getStartTime(), opts.getStopTime());

            // if the search completed then verify equality with the Java implementation.
            assert (stop || searchesAreEqual(board, undos, searchParameters, opts, nativeScore, nativePV, nativeStats));

            // set the object's stats to the native stats
            searchStats.set(nativeStats);

            // translate the native PV into the object's PV
            pv.clear();
            pv.addAll(MoveUtils.fromNativeLine(nativePV, board.getPlayerToMove()));

            return nativeScore;
        } catch (IllegalStateException e) {
            DrawBoard.drawBoard(board);
            LOGGER.error(e);
            throw e;
        }
    }

    // wrapper so we can clear hash tables when asserts are enabled
    private boolean clearTableWrapper() {
        TTHolder.getInstance().getHashTable().clear();
        TTHolder.getInstance().getPawnHashTable().clear();
        return true;
    }

    private boolean searchesAreEqual(Board board, List<Undo> undos, SearchParameters searchParameters,
                                     SearchOptions opts, int nativeScore, List<Long> nativePV, SearchStats nativeStats)
    {
        LOGGER.debug("# checking search equality with java depth {}", searchParameters.getDepth());
        try {
            long nativeProbes = TTHolder.getInstance().getHashTable().getNumProbes();
            long nativeHits = TTHolder.getInstance().getHashTable().getNumHits();
            long nativeCollisions = TTHolder.getInstance().getHashTable().getNumCollisions();

            assert(clearTableWrapper());
            int javaScore = searchWithJavaCode(board, undos, searchParameters, opts);

            // if the search was interrupted we can't compare
            if (stop) return true;

            // compare the hash table stats
            long javaProbes = TTHolder.getInstance().getHashTable().getNumProbes();
            long javaHits = TTHolder.getInstance().getHashTable().getNumHits();
            long javaCollisions = TTHolder.getInstance().getHashTable().getNumCollisions();
            if (javaProbes != nativeProbes || javaHits != nativeHits || javaCollisions != nativeCollisions) {
                LOGGER.error("hash stats not equal! "
                        + "java probes: " + javaProbes + ", native probes: " + nativeProbes
                        + ", java hits: " + javaHits + ", native hits: " + nativeHits
                        + ", java collisions: " + javaCollisions + ", native collisions: " + nativeCollisions
                        + ", params: " + searchParameters);
                return false;
            }

            // compare node counts
            if (searchStats.nodes != nativeStats.nodes || searchStats.qnodes != nativeStats.qnodes) {
                LOGGER.error("node counts not equal!  java nodes: " + searchStats.nodes
                        + ", native nodes:" + nativeStats.nodes
                        + ", java qnodes: " + searchStats.qnodes
                        + ", native qnodes: " + nativeStats.qnodes);
                return false;
            }

            // compare fail highs
            if (searchStats.failHighs != nativeStats.failHighs) {
                LOGGER.error("fail highs not equal!  java fail highs: " + searchStats.failHighs
                        + ", native fail highs: " + nativeStats.failHighs);
            }

            // compare fail lows
            if (searchStats.failLows != nativeStats.failLows) {
                LOGGER.error("fail lows not equal!  java fail lows: " + searchStats.failLows
                        + ", native fail lows: " + nativeStats.failLows);
            }

            // compare draws
            if (searchStats.draws != nativeStats.draws) {
                LOGGER.error("draws not equal!  java draws: " + searchStats.draws
                        + ", native draws: " + nativeStats.draws);
            }

            // compare the PVs.
            if (!pv.equals(MoveUtils.fromNativeLine(nativePV, board.getPlayerToMove()))) {
                LOGGER.error("pvs are not equal!"
                        + ", java stats: " + searchStats + ", native stats: " + nativeStats
                        + ", params: " + searchParameters);
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
                       int alpha, int beta, boolean inCheck, boolean nullMoveOk, SearchOptions opts) {

        parentPV.clear();

        assert(alpha < beta);
        assert(inCheck == BoardUtils.isPlayerInCheck(board));

        // time check
        if (!skipTimeChecks && stopSearchOnTime(opts)) {
            stop = true;
            return 0;
        }

        // base case
        if (depth == 0) {
            return quiescenceSearch(board, undos, alpha, beta, opts);
        }

        // this is an interior node
        searchStats.nodes++;

        // probe the hash table
        TranspositionTableEntry tte = TTHolder.getInstance().getHashTable().probe(board);

        // try for early exit
        if (ply > 0) {
            // Draw check
            if (Draw.isDraw(board, undos)) {
                searchStats.draws++;
                return 0;
            }

            // is the hash entry useful?
            if (tte != null && tte.getDepth() >= depth) {
                if (tte.getType() == LOWER_BOUND) {
                    if (tte.getScore() >= beta) {
                        searchStats.failHighs++;
                        searchStats.hashFailHighs++;
                        return beta;
                    }
                } else if (tte.getType() == UPPER_BOUND) {
                    if (tte.getScore() <= alpha) {
                        searchStats.failLows++;
                        searchStats.hashFailLows++;
                        return alpha;
                    }
                } else if (tte.getType() == EXACT_SCORE) {
                    searchStats.hashExactScores++;
                    return tte.getScore();
                }
            }

            // try a "null move".  The idea here is that if this position is so good that we can give the opponent
            // an extra turn and it _still_ fails high, it will almost surely fail high in a normal search.  This
            // is based on the "Null Move Observation," which says that "doing something is almost always better than
            // doing nothing."  This isn't entirely sound, but on the whole is a huge time saver.  We avoid the null
            // move when in check, and during zugzwang positions where making a move is actually harmful.
            // Since we are only trying to determine if the position will fail high or not, we search with a
            // minimal search window.
            if (!first && !inCheck && nullMoveOk && depth >= 3 && !ZugzwangDetector.isZugzwang(board)) {

                Square nullEp = board.clearEPSquare();
                board.swapPlayer();

                // set the reduced depth.  For now we are using a static R=3, except near the leaves.  It's important
                // to ensure there is at least one ply of full width depth remaining, since we aren't doing anything
                // with checks in the qsearch.
                int nullDepth = depth - 4; // R = 3
                if (nullDepth < 1) {
                    nullDepth = 1;
                }

                int nullScore = -search(board, undos, new ArrayList<>(), false, ply+1, nullDepth, -beta,
                        -beta+1,false, false, opts);

                board.swapPlayer();
                if (nullEp != null) {
                    board.setEP(nullEp);
                }

                if (stop) {
                    return 0;
                }
                if (nullScore >= beta) {
                    searchStats.failHighs++;
                    searchStats.nullMvFailHighs++;
                    return beta;
                }
            }
        }

        List<Move> pv = new ArrayList<>(50);

        int numMovesSearched = 0;
        Move pvMove = first && lastPv.size() > ply ? lastPv.get(ply) : null;
        Move hashMove = tte == null ? null : tte.getMove();
        MoveOrderer moveOrderer = new MoveOrderer(board, moveGenerator,
                pvMove, hashMove, killerMovesStore.getKiller1(ply), killerMovesStore.getKiller2(ply),
                true, true);

        Move bestMove = null;
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

            // determine if the move we're about to explore gives check
            boolean givesCheck = BoardUtils.isPlayerInCheck(board);

            // extensions
            int ext = givesCheck ? 1 : 0;
            boolean tryNull = ext == 0;

            int val;
            if (numMovesSearched==0 || ply==0 || opts.isAvoidResearches()) {
                val = -search(board, undos, pv, pvNode, ply+1, depth-1+ext,  -beta, -alpha, givesCheck,
                        true, opts);
            } else {

                // If we've already searched a few moves, chances are this is an ALL node, and we're not going to get
                // a beta cutoff.  Unless the move looks "interesting" in some way, just search to a reduced depth.
                // If we're surprised by the score, we'll research it to the normal depth.

                if (numMovesSearched >= 4 && depth >= 3 && !pvNode && !inCheck && !givesCheck && ext==0 &&
                        move.captured()==null && move.promotion()==null &&
                        !move.equals(killerMovesStore.getKiller1(ply)) && !move.equals(killerMovesStore.getKiller2(ply)))
                {
                    val = -search(board, undos, pv, pvNode, ply+1, depth-2,  -(alpha+1), -alpha, givesCheck,
                            tryNull, opts);
                }
                else {
                    val = alpha + 1; // ensure a search
                }

                if (val > alpha) {
                    // try a PVS (zero width) search
                    val = -search(board, undos, pv, pvNode, ply + 1, depth - 1 + ext, -(alpha + 1), -alpha, givesCheck,
                            tryNull, opts);
                    if (val > alpha && val < beta) {
                        val = -search(board, undos, pv, pvNode, ply + 1, depth - 1 + ext, -beta, -alpha, givesCheck,
                                tryNull, opts);
                    }
                }
            }

            ++numMovesSearched;
            board.undoMove(undos.remove(undos.size()-1));

            // if the search was stopped we can't trust these results, so don't update the PV
            if (stop) {
                return 0;
            }

            if (val >= beta) {
                searchStats.failHighs++;
                searchStats.failHighByMove.computeIfPresent(numMovesSearched, (k, v) -> v + 1);
                TTHolder.getInstance().getHashTable().store(board, LOWER_BOUND, beta, depth, move, 0);
                if (move.captured()==null && move.promotion()==null) {
                    killerMovesStore.addKiller(ply, move);
                }
                return beta;
            }
            if (val > alpha) {
                alpha = val;
                bestMove = move;
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

        TranspositionTableEntryType tableEntryType;
        if (bestMove == null) {
            tableEntryType = UPPER_BOUND; // fail low
            searchStats.failLows++;
        } else {
            tableEntryType = EXACT_SCORE;
        }

        TTHolder.getInstance().getHashTable().store(board, tableEntryType, alpha, depth, bestMove, 0);

        return alpha;
    }

    public int quiescenceSearch(Board board, List<Undo> undos, int alpha, int beta, SearchOptions opts) {

        assert(alpha < beta);

        // time check
        if (!skipTimeChecks && stopSearchOnTime(opts)) {
            stop = true;
            return 0;
        }

        searchStats.qnodes++;

        int standPat = evaluator.evaluateBoard(board);
        if (standPat > alpha) {
            if (standPat >= beta) {
                return beta;
            }
            // our static evaluation will serve as the lower bound
            alpha = standPat;
        }

        MoveOrderer moveOrderer = new MoveOrderer(board, moveGenerator,
                null, null, null, null, false, false);
        Move move;

        while ((move = moveOrderer.selectNextMove()) != null) {
            assert(BoardUtils.isPseudoLegalMove(board, move));
            assert(move.captured() != null || move.promotion() != null);

            undos.add(board.applyMove(move));
            // check if move was legal
            if (BoardUtils.isOpponentInCheck(board)) {
                board.undoMove(undos.remove(undos.size()-1));
                continue;
            }

            int val = -quiescenceSearch(board, undos, -beta, -alpha, opts);
            board.undoMove(undos.remove(undos.size()-1));

            // if the search was stopped just unwind back up
            if (stop) {
                return 0;
            }

            if (val >= beta) {
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

    private native int searchNative(Board board, List<Long> parentPV, int depth, int alpha, int beta,
                                    SearchStats searchStats, long startTime, long stopTime);

    private native void stopNative(boolean stop);

    private native void skipTimeChecksNative(boolean skipTimeChecks);

}
