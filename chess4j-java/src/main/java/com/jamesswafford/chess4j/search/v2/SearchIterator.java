package com.jamesswafford.chess4j.search.v2;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.board.Undo;
import com.jamesswafford.chess4j.eval.Eval;
import com.jamesswafford.chess4j.io.PrintLine;
import com.jamesswafford.chess4j.movegen.MoveGen;
import com.jamesswafford.chess4j.search.KillerMoves;
import com.jamesswafford.chess4j.search.MVVLVA;
import com.jamesswafford.chess4j.utils.MoveUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.jamesswafford.chess4j.Constants.CHECKMATE;
import static com.jamesswafford.chess4j.Constants.INFINITY;

public class SearchIterator {

    private static final Log LOGGER = LogFactory.getLog(SearchIterator.class);

    private int maxDepth = 0;
    private boolean post = true;
    private boolean testSuiteMode = false;

    public SearchIterator() {
    }

    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public void setPost(boolean post) {
        this.post = post;
    }

    public void setTestSuiteMode(boolean testSuiteMode) {
        this.testSuiteMode = testSuiteMode;
    }

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
    private List<Move> findPrincipalVariation(Board board, List<Undo> undos) {

        List<Move> moves = MoveGen.genLegalMoves(board);
        LOGGER.debug("# position has " + moves.size() + " move(s)");
        if (!testSuiteMode && moves.size()==1) {
            return Collections.singletonList(moves.get(0));
        }

        long startTime = System.currentTimeMillis();
        int depth = 0, score = 0;
        boolean stopSearching = false;
        Search search = new Search(board, undos, new Eval(), new MoveGen(), new MVVLVA(), KillerMoves.getInstance());

        do {
            ++depth;

            int alphaBound = -INFINITY;
            int betaBound = INFINITY;

            SearchParameters parameters = new SearchParameters(depth, alphaBound, betaBound);
            score = search.search(parameters);

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

        printSearchSummary(depth, startTime, search.getSearchStats());

        return search.getLastPV();
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

}
