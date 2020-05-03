package com.jamesswafford.chess4j.search;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Color;
import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.board.Undo;
import com.jamesswafford.chess4j.init.Initializer;
import com.jamesswafford.chess4j.io.FenBuilder;
import com.jamesswafford.chess4j.io.PrintLine;
import com.jamesswafford.chess4j.movegen.MagicBitboardMoveGenerator;
import com.jamesswafford.chess4j.movegen.MoveGenerator;
import com.jamesswafford.chess4j.utils.MoveUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.jamesswafford.chess4j.Constants.CHECKMATE;
import static com.jamesswafford.chess4j.Constants.INFINITY;

public class SearchIteratorImpl implements SearchIterator {

    private static final  Logger LOGGER = LogManager.getLogger(SearchIteratorImpl.class);

    private int maxDepth = 6;
    private boolean post = true;
    private boolean earlyExitOk = true;

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
    public void setPost(boolean post) {
        this.post = post;
    }

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
    public CompletableFuture<List<Move>> findPvFuture(Board board, List<Undo> undos) {
        return CompletableFuture.supplyAsync(() -> findPrincipalVariation(
                board.deepCopy(),
                new ArrayList<>(undos)));
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

        long startTime = System.currentTimeMillis();
        int depth = 0, score;
        boolean stopSearching = false;

        do {
            ++depth;

            int alphaBound = -INFINITY;
            int betaBound = INFINITY;

            SearchParameters parameters = new SearchParameters(depth, alphaBound, betaBound);
            score = search.search(board, undos, parameters);

            assert(search.getLastPV().size()>0);

            if (post) {
                PrintLine.printLine(search.getLastPV(), depth, score, startTime, search.getSearchStats().nodes);
            }

            // if this is a mate, stop here
            if (Math.abs(score) > CHECKMATE-500) {
                LOGGER.debug("# stopping iterative search because mate found");
                stopSearching = true;
            }

            if (maxDepth > 0 && depth >= maxDepth) {
                LOGGER.debug("# stopping iterative search on depth");
                stopSearching = true;
            }

        } while (!stopSearching);

        assert(search.getLastPV().size()>0);
        assert(MoveUtils.isLineValid(search.getLastPV(), board));

        if (post) {
            printSearchSummary(depth, startTime, search.getSearchStats());
        }

        // if we are running with assertions enabled and the native library is loaded, verify equality
        assert(iterationsAreEqual(search.getLastPV(), board, undos));

        return search.getLastPV();
    }

    private boolean iterationsAreEqual(List<Move> javaPV, Board board, List<Undo> undos) {

        if (Initializer.nativeCodeInitialized()) {

            List<Move> nativePV = findPrincipalVariationNative(board, undos);

            if (!nativePV.equals(javaPV)) {
                LOGGER.error("PVs are not equal! javaPV: " + PrintLine.getMoveString(javaPV) +
                        ", nativePV: " + PrintLine.getMoveString(nativePV));
                return false;
            } else {
                return true;
            }

        } else {
            // native library not loaded
            return true;
        }
    }

    private List<Move> findPrincipalVariationNative(Board board, List<Undo> undos) {
        String fen = FenBuilder.createFen(board, false);

        List<Long> prevMoves = undos.stream()
                .map(undo -> MoveUtils.toNativeMove(undo.getMove()))
                .collect(Collectors.toList());

        List<Long> nativePV = new ArrayList<>();

        try {
            iterateNative(fen, prevMoves, maxDepth, nativePV);

            // translate the native PV
            List<Move> pv = new ArrayList<>();
            for (int i=0; i<nativePV.size(); i++) {
                Long nativeMv = nativePV.get(i);
                // which side is moving?  On even moves it is the player on move.
                Color ptm = (i % 2) == 0 ? board.getPlayerToMove() : Color.swap(board.getPlayerToMove());
                pv.add(MoveUtils.fromNativeMove(nativeMv, ptm));
            }
            return pv;
        } catch (IllegalStateException e) {
            LOGGER.error(e);
            throw e;
        }
    }

    private void printSearchSummary(int lastDepth, long startTime, SearchStats stats) {
        DecimalFormat df = new DecimalFormat("0.00");
        DecimalFormat df2 = new DecimalFormat("#,###,##0");

        long totalNodes = stats.nodes; // + stats.getQNodes();
        double interiorPct = stats.nodes / (totalNodes/100.0);
        double qnodePct = 0.0; //stats.getQNodes() / (totalNodes/100.0);

        LOGGER.info("\n");
        LOGGER.info("# depth: " + lastDepth);
        LOGGER.info("# nodes: " + df2.format(totalNodes) + ", interior: "
                + df2.format(stats.nodes) + " (" + df.format(interiorPct) + "%)"
                + ", quiescence: " + df2.format(0.0) + " (" + df.format(qnodePct) + "%)");

        long totalSearchTime = System.currentTimeMillis() - startTime;
        LOGGER.info("# search time: " + totalSearchTime/1000.0 + " seconds"
                + ", rate: " + df2.format(totalNodes / (totalSearchTime/1000.0)) + " nodes per second");

//        long hashHits = TTHolder.getDepthPreferredTransTable().getNumHits()
//                + TTHolder.getAlwaysReplaceTransTable().getNumHits();
//        long hashProbes = TTHolder.getDepthPreferredTransTable().getNumProbes()
//                + TTHolder.getAlwaysReplaceTransTable().getNumProbes();
//        long hashCollisions = TTHolder.getDepthPreferredTransTable().getNumCollisions()
//                + TTHolder.getAlwaysReplaceTransTable().getNumCollisions();
//        double hashHitPct = hashHits / (hashProbes/100.0);
//        double hashCollisionPct = hashCollisions / (hashProbes/100.0);
//
//        LOGGER.info("# hash probes: " + df2.format(hashProbes)
//                + ", hits: " + df2.format(hashHits) + " (" + df.format(hashHitPct) + "%)"
//                + ", collisions: " + df2.format(hashCollisions) + " (" + df.format(hashCollisionPct) + "%)");
//
//        long pawnHashHits = TTHolder.getPawnTransTable().getNumHits();
//        long pawnHashProbes = TTHolder.getPawnTransTable().getNumProbes();
//        long pawnHashCollisions = TTHolder.getPawnTransTable().getNumCollisions();
//        double pawnHashHitPct = pawnHashHits / (pawnHashProbes/100.0);
//        double pawnHashCollisionPct = pawnHashCollisions / (pawnHashProbes/100.0);
//
//        LOGGER.info("# pawn hash probes: " + df2.format(pawnHashProbes)
//                + ", hits: " + df2.format(pawnHashHits) + " (" + df.format(pawnHashHitPct) + "%)"
//                + ", collisions: " + df2.format(pawnHashCollisions) + " (" + df.format(pawnHashCollisionPct) + "%)");

//        double failHighPct = stats.getFailHighs() / (hashProbes/100.0);
//        double failLowPct = stats.getFailLows() / (hashProbes/100.0);
//        double exactScorePct = stats.getHashExactScores() / (hashProbes/100.0);
//        LOGGER.info("# fail highs: " + df2.format(stats.getFailHighs()) + " (" + df.format(failHighPct) + "%)"
//                + ", fail lows: " + df2.format(stats.getFailLows()) + " (" + df.format(failLowPct) + "%)"
//                + ", exact scores: " + df2.format(stats.getHashExactScores()) + " (" + df.format(exactScorePct) + "%)");
//
//        LOGGER.info("# prunes: " + stats.getPrunes());
    }

//        private List<Move> findPrincipalVariationWithNativeCode(Board board, List<Undo> undos) {
//
//
//        }

        private native void iterateNative(String boardFen, List<Long> prevMoves, int maxDepth, List<Long> pv);

}
