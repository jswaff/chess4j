package dev.jamesswafford.chess4j.search;

import dev.jamesswafford.chess4j.Constants;
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
    private long maxTimeMs = 0;
    private boolean post = true;
    private boolean earlyExitOk = true;
    private boolean skipTimeChecks = false;

    private MoveGenerator moveGenerator;
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
    public void setMaxTime(long maxTimeMs) {
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

    public void setMoveGenerator(MoveGenerator moveGenerator) {
        this.moveGenerator = moveGenerator;
    }

    public void setSearch(Search search) {
        this.search = search;
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
        LOGGER.debug("# position has " + moves.size() + " move(s)");
        if (earlyExitOk && moves.size()==1) {
            return Collections.singletonList(moves.get(0));
        }

        // initialize the PV to ensure we have a valid move to play
        moves.sort(Comparator.comparingInt(MVVLVA::score).reversed());
        List<Move> pv = Collections.singletonList(moves.get(0));

        // create a callback to print the PV when it changes
        long startTime = System.currentTimeMillis();
        SearchOptions opts = SearchOptions.builder().startTime(startTime).build();
        Consumer<PvCallbackDTO> rootPvCallback = pvUpdate -> {
            if (pvUpdate.ply == 0) {
                PrintLine.printLine(false, pvUpdate.pv, pvUpdate.depth, pvUpdate.score, pvUpdate.elapsedMS,
                        pvUpdate.nodes);
            }
        };
        if (post) {
            opts.setPvCallback(rootPvCallback);
        }
        int depth = 0, score = 0;
        search.initialize();

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

            // TODO: this is a failed first attempt at aspiration windows, but I intend to revisit it after
            // the search matures a little more.
            /*if ((score <= alphaBound || score >= betaBound) && !search.isStopped()) {
                LOGGER.debug("# researching; score: " + score + ", a: " + alphaBound + ", b: " + betaBound);
                parameters = new SearchParameters(depth, -INFINITY, INFINITY);
                score = search.search(board, undos, parameters, opts);
            }*/

            // the search may or may not have a PV.  If it does, we can use it since the
            // last iteration's PV was tried first
            List<Move> searchPV = search.getPv();
            if (searchPV.size() > 0) {
                pv = new ArrayList<>(searchPV);
            }

            if (search.isStopped()) {
                break;
            }

            long elapsed = System.currentTimeMillis() - startTime;
            if (post) {
                PrintLine.printLine(true, pv, depth, score, elapsed, search.getSearchStats().nodes);
            }

            // track the number of nodes computed in this iteration
            long nodesPrevDepth = search.getSearchStats().nodesByIteration.computeIfAbsent(depth - 1, k -> 0L);
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
            if (maxDepth >= Constants.MAX_ITERATIONS) {
                stopSearching = true;
            }

            // if we've used more than half our time, don't start a new iteration
            if (earlyExitOk && !skipTimeChecks && (elapsed > maxTimeMs / 2)) {
                LOGGER.debug(" # stopping iterative search because half time expired.");
                stopSearching = true;
            }

        } while (!stopSearching);

        if (post) {
            printSearchSummary(depth, startTime, search.getSearchStats());
        }

        assert(pv.size() > 0);
        assert(MoveUtils.isLineValid(pv, board));

        // if we are running with assertions enabled and the native library is loaded, verify equality
        // we can only do this for fixed depth searches that have not been interrupted.
        assert(maxTimeMs > 0 || search.isStopped() || iterationsAreEqual(pv, board));

        return pv;
    }

    private boolean iterationsAreEqual(List<Move> javaPV, Board board) {

        if (Initializer.nativeCodeInitialized()) {

            LOGGER.debug("# checking iteration equality with native");
            List<Move> nativePV = findPrincipalVariationNative(board);

            // if the search was stopped the comparison won't be valid
            if (search.isStopped()) {
                LOGGER.debug("# not comparing incomplete iteration");
                return true;
            }

            if (!nativePV.equals(javaPV)) {
                LOGGER.error("PVs are not equal! javaPV: " + PrintLine.getMoveString(javaPV) +
                        ", nativePV: " + PrintLine.getMoveString(nativePV));
                return false;
            } else {
                LOGGER.debug("# finished - iterations produce the same PVs");
                return true;
            }

        } else {
            // native library not loaded
            return true;
        }
    }

    private List<Move> findPrincipalVariationNative(Board board) {
        List<Long> nativePV = new ArrayList<>();
        try {
            LOGGER.debug("# starting native iterator maxDepth: {}", maxDepth);
            iterateNative(board, maxDepth, nativePV);
            return MoveUtils.fromNativeLine(nativePV, board.getPlayerToMove());
        } catch (IllegalStateException e) {
            LOGGER.error(e);
            throw e;
        }
    }

    private void printSearchSummary(int lastDepth, long startTime, SearchStats stats) {
        DecimalFormat df = new DecimalFormat("0.00");
        DecimalFormat df2 = new DecimalFormat("#,###,##0");

        long totalNodes = stats.nodes + stats.qnodes;
        double interiorPct = stats.nodes / (totalNodes/100.0);
        double qnodePct = stats.qnodes / (totalNodes/100.0);

        LOGGER.info("\n");
        LOGGER.info("# depth: " + lastDepth);
        LOGGER.info("# nodes: " + df2.format(totalNodes) + ", interior: "
                + df2.format(stats.nodes) + " (" + df.format(interiorPct) + "%)"
                + ", quiescence: " + df2.format(stats.qnodes) + " (" + df.format(qnodePct) + "%)");

        long totalSearchTime = System.currentTimeMillis() - startTime;
        LOGGER.info("# search time: " + totalSearchTime/1000.0 + " seconds"
                + ", rate: " + df2.format(totalNodes / (totalSearchTime/1000.0)) + " nodes per second");

        TranspositionTable htbl = TTHolder.getInstance().getHashTable();
        long hashHits = htbl.getNumHits();
        long hashProbes = htbl.getNumProbes();
        long hashCollisions = htbl.getNumCollisions();
        double hashHitPct = hashHits / (hashProbes/100.0);
        double hashCollisionPct = hashCollisions / (hashProbes/100.0);
        LOGGER.info("# hash probes: " + df2.format(hashProbes)
                + ", hits: " + df2.format(hashHits) + " (" + df.format(hashHitPct) + "%)"
                + ", collisions: " + df2.format(hashCollisions) + " (" + df.format(hashCollisionPct) + "%)");

        double hashFailHighPct = stats.hashFailHighs / (hashProbes/100.0);
        double hashFailLowPct = stats.hashFailLows / (hashProbes/100.0);
        double hashExactScorePct = stats.hashExactScores / (hashProbes/100.0);
        LOGGER.info("# hash fail highs: " + df2.format(stats.hashFailHighs)
                + " (" + df.format(hashFailHighPct) + "%)"
                + ", hash fail lows: " + df2.format(stats.hashFailLows)
                + " (" + df.format(hashFailLowPct) + "%)"
                + ", hash exact scores: " + df2.format(stats.hashExactScores)
                + " (" + df.format(hashExactScorePct) + "%)");

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
        LOGGER.info("# fail high mv1: " + df2.format(fh1) + " (" + df.format(failHigh1stPct) + "%)"
                + ", mv2: " + df2.format(fh2) + " (" + df.format(failHigh2ndPct) + "%)"
                + ", mv3: " + df2.format(fh3) + " (" + df.format(failHigh3rdPct) + "%)"
                + ", mv4: " + df2.format(fh4) + " (" + df.format(failHigh4thPct) + "%)"
        );

        // effective branching factor metrics
        StringBuilder sb = new StringBuilder("");
        double totalEbf = 0.0;
        int numEbfs = 0;
        for (int i=2;i<=Math.min(lastDepth, 12);i++) {
            if (stats.nodesByIteration.get(i) != null) {
                double ebf = stats.nodesByIteration.get(i) / Double.valueOf(stats.nodesByIteration.get(i - 1));
                totalEbf += ebf;
                ++numEbfs;
                sb.append(", i" + i + ": " + df.format(ebf));
            }
        }
        double avgEbf = totalEbf / numEbfs;
        LOGGER.info("# ebf avg: " + df.format(avgEbf) + sb.toString());
    }

    private native void iterateNative(Board board, int maxDepth, List<Long> pv);

}
