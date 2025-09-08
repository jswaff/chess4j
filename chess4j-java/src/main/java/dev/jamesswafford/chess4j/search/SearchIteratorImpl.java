package dev.jamesswafford.chess4j.search;

import dev.jamesswafford.chess4j.Constants;
import dev.jamesswafford.chess4j.NativeEngineLib;
import dev.jamesswafford.chess4j.board.Board;
import dev.jamesswafford.chess4j.board.Move;
import dev.jamesswafford.chess4j.board.Undo;
import dev.jamesswafford.chess4j.hash.PawnTranspositionTable;
import dev.jamesswafford.chess4j.hash.TTHolder;
import dev.jamesswafford.chess4j.hash.TranspositionTable;
import dev.jamesswafford.chess4j.init.Initializer;
import dev.jamesswafford.chess4j.io.PrintLine;
import dev.jamesswafford.chess4j.movegen.MagicBitboardMoveGenerator;
import dev.jamesswafford.chess4j.movegen.MoveGenerator;
import dev.jamesswafford.chess4j.utils.MoveUtils;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

import static dev.jamesswafford.chess4j.Constants.CHECKMATE;

public class SearchIteratorImpl implements SearchIterator {

    private static final  Logger LOGGER = LogManager.getLogger(SearchIteratorImpl.class);

    static {
        Initializer.init();
    }

    private int maxDepth = 0;
    private int maxTimeMs = 0;
    private boolean post = true;
    private boolean earlyExitOk = true;
    private boolean skipTimeChecks = false;

    @Setter
    private MoveGenerator moveGenerator;
    @Setter
    private Search search;

    public SearchIteratorImpl() {
        moveGenerator = new MagicBitboardMoveGenerator();
        search = new AlphaBetaSearch();
    }

    @Override
    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    @Override
    public void setMaxTime(int maxTimeMs) {
        this.maxTimeMs = maxTimeMs;
    }

    @Override
    public void setPost(boolean post) {
        this.post = post;
    }

    @Override
    public void setSkipTimeChecks(boolean skipTimeChecks) {
        this.skipTimeChecks = skipTimeChecks;
        search.setSkipTimeChecks(skipTimeChecks);
    }

    @Override
    public void setEarlyExitOk(boolean earlyExitOk) {
        this.earlyExitOk = earlyExitOk;
    }

    @Override
    public CompletableFuture<List<Move>> findPvFuture(final Board board, final List<Undo> undos) {
        return CompletableFuture.supplyAsync(() -> findPrincipalVariation(
                board.deepCopy(),
                new ArrayList<>(undos)));
    }

    @Override
    public boolean isStopped() {
        return search.isStopped();
    }

    @Override
    public void stop() {
        search.stop();
    }

    @Override
    public void unstop() {
        search.unstop();
    }

    /**
     * Iterate over the given position and return the principal variation.
     * The returned line (PV) is guaranteed to have at least one move.
     *
     * @return - principal variation
     */
    private List<Move> findPrincipalVariation(Board board, final List<Undo> undos) {

        List<Move> moves = moveGenerator.generateLegalMoves(board);
        assert(!moves.isEmpty());
        LOGGER.debug("# position has {} move(s)", moves.size());

        // if there is only one legal move, there is no need to search
        if (earlyExitOk && moves.size()==1) {
            return List.of(moves.getFirst());
        }

        // initialize the PV to ensure we have a valid move to play
        moves.sort(Comparator.comparingInt(MVVLVA::score).reversed());
        List<Move> pv = new ArrayList<>();
        pv.add(moves.getFirst());

        // set up the search options
        long startTime = System.currentTimeMillis();
        SearchOptions opts = SearchOptions.builder().startTime(startTime).build();
        if (post) {
            Consumer<PvCallbackDTO> rootPvCallback = pvUpdate -> {
                if (pvUpdate.ply == 0) {
                    PrintLine.printLine(false, pvUpdate.pv, pvUpdate.depth, pvUpdate.score, pvUpdate.elapsedMS,
                            pvUpdate.nodes);
                }
            };
            opts.setPvCallback(rootPvCallback);
        }

        if (maxTimeMs > 0) {
            opts.setStopTime(opts.getStartTime() + maxTimeMs);
            opts.setNodesBetweenTimeChecks(50000);
            // if we're getting low on time, check more often
            if (maxTimeMs < 10000) {
                opts.setNodesBetweenTimeChecks(opts.getNodesBetweenTimeChecks() / 10);
            }
            if (maxTimeMs < 1000) {
                opts.setNodesBetweenTimeChecks(opts.getNodesBetweenTimeChecks() / 10);
            }
        }

        // use iterative deepening to find the principal variation
        Tuple2<Integer, Integer> depthScore;
        SearchStats stats = new SearchStats();
        if (Initializer.nativeCodeInitialized() && !opts.isAvoidNative()) {
            depthScore = iterateWithNativeCode(pv, stats, board, undos, opts);
        } else {
            depthScore = iterateWithJavaCode(pv, board, undos, opts);
            stats.set(search.getSearchStats());
        }

        // show some search stats
        printSearchSummary(depthScore._1, depthScore._2, startTime, stats);

        assert(MoveUtils.isLineValid(pv, board));

        return pv;
    }

    private Tuple2<Integer,Integer> iterateWithJavaCode(List<Move> pv, Board board, final List<Undo> undos,
                                                        SearchOptions opts)
    {
        assert(clearTableWrapper());

        // prepare to search
        search.initialize();
        int depth = 0, score;
        boolean stopSearching = false;
        do {
            ++depth;

            int alphaBound = -CHECKMATE;
            int betaBound = CHECKMATE;
            /*if (depth > 2) {
                alphaBound = score - EvalMaterial.PAWN_VAL / 3;
                betaBound = score + EvalMaterial.PAWN_VAL / 3;
            }*/

            SearchParameters parameters = new SearchParameters(depth, alphaBound, betaBound);
            score = search.search(board, undos, parameters, opts);

            // TODO: this is a failed first attempt at aspiration windows, but I intend to revisit it
            /*if ((score <= alphaBound || score >= betaBound) && !search.isStopped()) {
                LOGGER.debug("# researching; score: " + score + ", a: " + alphaBound + ", b: " + betaBound);
                parameters = new SearchParameters(depth, -INFINITY, INFINITY);
                score = search.search(board, undos, parameters, opts);
            }*/

            // the search may or may not have a PV.  If it does, we can use it since the
            // last iteration's PV was tried first
            List<Move> searchPV = search.getPv();
            if (!searchPV.isEmpty()) {
                pv.clear();
                pv.addAll(searchPV);
            }

            if (search.isStopped()) {
                break;
            }

            long elapsed = System.currentTimeMillis() - opts.getStartTime();
            if (post) {
                PrintLine.printLine(true, pv, depth, score, elapsed, search.getSearchStats().nodes);
            }

            // track the number of nodes computed in this iteration
            long nodesPrevDepth = search.getSearchStats().nodesByIteration.computeIfAbsent(depth - 1, _ -> 0L);
            search.getSearchStats().nodesByIteration.put(depth, search.getSearchStats().nodes - nodesPrevDepth);

            // if this is a mate, stop here
            if (Math.abs(score) > CHECKMATE-500) {
                LOGGER.debug("# stopping iterative search because mate found");
                stopSearching = true;
            }

            // if we've hit the user defined max search depth, stop here
            if (maxDepth > 0 && depth >= maxDepth) {
                LOGGER.debug("# stopping iterative search on depth");
                stopSearching = true;
            }

            // if we've hit the system defined max iterations, stop here
            if (depth >= Constants.MAX_ITERATIONS) {
                stopSearching = true;
            }

            // if we've used more than half our time, don't start a new iteration
            if (earlyExitOk && !skipTimeChecks && (elapsed > maxTimeMs / 2)) {
                LOGGER.debug(" # stopping iterative search because half time expired.");
                stopSearching = true;
            }

        } while (!stopSearching);

        return Tuple.of(depth, score);
    }

    private Tuple2<Integer,Integer> iterateWithNativeCode(List<Move> pv, SearchStats stats, Board board,
                                                          final List<Undo> undos, SearchOptions opts) {
        assert(clearTableWrapper());
        List<Move> nativePv = new ArrayList<>();
        // earlyExitOK, maxTimeMS
        Tuple2<Integer, Integer> nativeDepthScore = NativeEngineLib.iterate(nativePv, stats, board, undos,
                earlyExitOk, maxDepth, maxTimeMs);

        // verify equality with java iterator.  This only works for fixed depth searches.
        assert(maxTimeMs>0 || iterationsAreEqual(nativePv, nativeDepthScore._2, stats, board, undos, opts));

        pv.clear();
        pv.addAll(nativePv);

        return nativeDepthScore;
    }

    private boolean iterationsAreEqual(List<Move> nativePV, Integer nativeScore, SearchStats nativeStats,
                                       Board board, final List<Undo> undos, SearchOptions opts) {

        LOGGER.debug("# checking iteration equality with java");

        // anytime we "cross the boundary" the hash tables need to be cleared
        List<Move> pv = new ArrayList<>();
        Tuple2<Integer, Integer> depthScore = iterateWithJavaCode(pv, board, undos, opts);
        int score = depthScore._2;

        // if the search was stopped the comparison won't be valid
        if (search.isStopped()) {
            LOGGER.debug("# not comparing incomplete iteration");
            return true;
        }

        boolean retval = true;
        SearchStats stats = search.getSearchStats();

        // compare principal variations
        if (!pv.equals(nativePV)) {
            LOGGER.error("# PVs not equal! java: {}, native: {}",
                    PrintLine.getMoveString(pv), PrintLine.getMoveString(nativePV));
            retval = false;
        }

        // compare scores
        if (score != nativeScore) {
            LOGGER.error("# scores not equal! java: {}, native: {}", score, nativeScore);
            retval = false;
        }

        // compare node counts
        if (stats.nodes != nativeStats.nodes || stats.qnodes != nativeStats.qnodes) {
            LOGGER.error("# node counts not equal!  java nodes: {}, native nodes:{}, " +
                            "java qnodes: {}, native qnodes: {}", stats.nodes, nativeStats.nodes,
                    stats.qnodes, nativeStats.qnodes);
            retval = false;
        }

        // compare fail highs
        if (stats.failHighs != nativeStats.failHighs) {
            LOGGER.error("# fail highs not equal!  java: {}, native: {}", stats.failHighs, nativeStats.failHighs);
            retval = false;
        }

        // compare fail lows
        if (stats.failLows != nativeStats.failLows) {
            LOGGER.error("# fail lows not equal!  java: {}, native: {}", stats.failLows, nativeStats.failLows);
            retval = false;
        }

        // compare number of draws in search
        if (stats.draws != nativeStats.draws) {
            LOGGER.error("# draws not equal!  java: {}, native: {}", stats.draws, nativeStats.draws);
            retval = false;
        }

        if (retval) {
            LOGGER.debug("# finished - iterations are equal");
        } else {
            LOGGER.debug("# finished - iterations are NOT equal");
        }

        return retval;
    }

    private void printSearchSummary(int lastDepth, int lastScore, long startTime, SearchStats stats) {
        DecimalFormat df = new DecimalFormat("0.00");
        DecimalFormat df2 = new DecimalFormat("#,###,##0");

        long totalNodes = stats.nodes + stats.qnodes;
        double interiorPct = stats.nodes / (totalNodes/100.0);
        double qnodePct = stats.qnodes / (totalNodes/100.0);

        LOGGER.info("\n");
        LOGGER.info("# depth: {}, score: {}", lastDepth, lastScore);
        LOGGER.info("# nodes: {}, interior: {} ({}%), quiescence: {} ({}%)",
                df2.format(totalNodes), df2.format(stats.nodes), df.format(interiorPct),
                df2.format(stats.qnodes), df.format(qnodePct));

        long totalSearchTime = System.currentTimeMillis() - startTime;
        LOGGER.info("# search time: {} seconds, rate: {} nodes per second",
                totalSearchTime / 1000.0, df2.format(totalNodes / (totalSearchTime / 1000.0)));

        TranspositionTable htbl = TTHolder.getInstance().getHashTable();
        long hashHits = htbl.getNumHits();
        long hashProbes = htbl.getNumProbes();
        long hashCollisions = htbl.getNumCollisions();
        double hashHitPct = hashHits / (hashProbes/100.0);
        double hashCollisionPct = hashCollisions / (hashProbes/100.0);
        LOGGER.info("# hash probes: {}, hits: {} ({}%), collisions: {} ({}%)",
                df2.format(hashProbes), df2.format(hashHits), df.format(hashHitPct), df2.format(hashCollisions),
                df.format(hashCollisionPct));

        double hashFailHighPct = stats.hashFailHighs / (hashProbes/100.0);
        double hashFailLowPct = stats.hashFailLows / (hashProbes/100.0);
        double hashExactScorePct = stats.hashExactScores / (hashProbes/100.0);
        LOGGER.info("# hash fail highs: {} ({}%), hash fail lows: {} ({}%), hash exact scores: {} ({}%)",
                df2.format(stats.hashFailHighs), df.format(hashFailHighPct), df2.format(stats.hashFailLows),
                df.format(hashFailLowPct), df2.format(stats.hashExactScores), df.format(hashExactScorePct));

        PawnTranspositionTable pawnTbl = TTHolder.getInstance().getPawnHashTable();
        long pawnHashHits = pawnTbl.getNumHits();
        long pawnHashProbes = pawnTbl.getNumProbes();
        long pawnHashCollisions = pawnTbl.getNumCollisions();
        double pawnHashHitPct = pawnHashHits / (pawnHashProbes/100.0);
        double pawnHashCollisionPct = pawnHashCollisions / (pawnHashProbes/100.0);
        LOGGER.info("# pawn hash probes: " + df2.format(pawnHashProbes)
                + ", hits: " + df2.format(pawnHashHits) + " (" + df.format(pawnHashHitPct) + "%)"
                + ", collisions: " + df2.format(pawnHashCollisions) + " (" + df.format(pawnHashCollisionPct) + "%)");

        // fail high metrics
        long failHighs = stats.failHighs - stats.hashFailHighs - stats.nullMvFailHighs;
        long fh1 = stats.failHighByMove.get(1);
        long fh2 = fh1 + stats.failHighByMove.get(2);
        long fh3 = fh2 + stats.failHighByMove.get(3);
        long fh4 = fh3 + stats.failHighByMove.get(4);
        double failHigh1stPct = fh1 / (failHighs / 100.0);
        double failHigh2ndPct = fh2 / (failHighs / 100.0);
        double failHigh3rdPct = fh3 / (failHighs / 100.0);
        double failHigh4thPct = fh4 / (failHighs / 100.0);
        LOGGER.info("# fail high mv1: {} ({}%), mv2: {} ({}%), mv3: {} ({}%), mv4: {} ({}%)",
                df2.format(fh1), df.format(failHigh1stPct), df2.format(fh2), df.format(failHigh2ndPct),
                df2.format(fh3), df.format(failHigh3rdPct), df2.format(fh4), df.format(failHigh4thPct));

        // effective branching factor metrics
        StringBuilder sb = new StringBuilder();
        double totalEbf = 0.0;
        int numEbfs = 0;
        for (int i=2;i<=Math.min(lastDepth, 12);i++) {
            if (stats.nodesByIteration.get(i) != null) {
                double ebf = stats.nodesByIteration.get(i) / Double.valueOf(stats.nodesByIteration.get(i - 1));
                totalEbf += ebf;
                ++numEbfs;
                sb.append(", i").append(i).append(": ").append(df.format(ebf));
            }
        }
        double avgEbf = totalEbf / numEbfs;
        LOGGER.info("# ebf avg: {}{}", df.format(avgEbf), sb);
    }

    // wrapper so we can clear hash tables when asserts are enabled
    private boolean clearTableWrapper() {
        TTHolder.getInstance().clearTables();
        return true;
    }

}
