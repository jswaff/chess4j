package com.jamesswafford.chess4j.search;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.jamesswafford.chess4j.Globals;
import com.jamesswafford.chess4j.board.Undo;
import com.jamesswafford.chess4j.search.v2.Search;
import com.jamesswafford.chess4j.search.v2.SearchParameters;
import com.jamesswafford.chess4j.search.v2.SearchStats;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jamesswafford.chess4j.App;
import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.movegen.MoveGen;
import com.jamesswafford.chess4j.book.BookMove;
import com.jamesswafford.chess4j.hash.TTHolder;
import com.jamesswafford.chess4j.io.PrintGameResult;
import com.jamesswafford.chess4j.io.PrintLine;
import com.jamesswafford.chess4j.utils.GameStatus;
import com.jamesswafford.chess4j.utils.GameStatusChecker;
import com.jamesswafford.chess4j.utils.MoveUtils;

import static com.jamesswafford.chess4j.Constants.*;
import static com.jamesswafford.chess4j.utils.GameStatus.*;

public final class SearchIterator {

    private static final Log LOGGER = LogFactory.getLog(SearchIterator.class);

    private static final SearchIterator INSTANCE = new SearchIterator();

    public static int remainingTimeMS;
    public static int incrementMS;
    public static int maxDepth;
    public static int maxTime;
    public static boolean post = true;

    private static Board searchPos;
    private static List<Undo> searchUndos;

    private SearchIterator() {	}

    public static SearchIterator getInstance() {
        return INSTANCE;
    }

    /**
     * Kick off the iterative deepening search in its own thread.
     * Returns the thread.
     *
     * @return - search thread
     */
    public static Thread think() {

        // make a copy of the current position.  note that just because we are operating
        // of a copy of the global position, the global position itself should remain
        // unchanged until our search is complete.
        searchPos = Globals.getBoard().deepCopy();

        // make a copy of the global undo stack
        searchUndos = new ArrayList<>(Globals.gameUndos);

        Thread thinkThread = new Thread(SearchIterator::threadHelper);
        thinkThread.start();

        return thinkThread;
    }

    private static void threadHelper() {

        List<Move> pv = iterate(searchPos, searchUndos, false);

        // sanity check - the global position shouldn't have changed
        assert(Globals.getBoard().equals(searchPos));

        Globals.gameUndos.add(Globals.getBoard().applyMove(pv.get(0)));
        LOGGER.info("move " + pv.get(0));
        GameStatus gameStatus = GameStatusChecker.getGameStatus(Globals.getBoard(), Globals.gameUndos);

        if (gameStatus != INPROGRESS) {
            PrintGameResult.printResult(gameStatus);
        }
    }

    /**
     * Iterate over the given position and return the principal variation.
     * The returned line (PV) is guaranteed to have at least one move.
     *
     * @return - principal variation
     */
    public static List<Move> iterate(Board board, List<Undo> undos, boolean testSuiteMode) {

        if (!testSuiteMode && App.getOpeningBook() != null && board.getMoveCounter() <= 30) {
            BookMove bookMove = App.getOpeningBook().getMoveWeightedRandomByFrequency(board);
            if (bookMove != null) {
                LOGGER.debug("# book move: " + bookMove);
                return Collections.singletonList(bookMove.getMove());
            }
        }

        // if just one legal move don't bother searching
        List<Move> moves = MoveGen.genLegalMoves(board);
        LOGGER.debug("# position has " + moves.size() + " move(s)");
        if (!testSuiteMode && moves.size()==1) {
            return Collections.singletonList(moves.get(0));
        }

        TTHolder.clearAllTables();
        long startTime = System.currentTimeMillis();
        int depth = 0, score = 0;
        boolean stopSearching = false;
        Search search = new Search(board, undos);

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

            //LOGGER.debug("# first line: " + PrintLine.getMoveString(stats.getFirstLine()));

            // if this is a mate, stop here
            if (Math.abs(score) > CHECKMATE-500) {
                LOGGER.debug("# stopping iterative search because mate found");
                stopSearching = true;
            }

            if (maxDepth > 0 && depth >= maxDepth) {
                LOGGER.debug("# stopping iterative search on depth");
                stopSearching = true;
            }

            // if we've used more than half our time, don't start a new iteration.
            /*long elapsedTime = System.currentTimeMillis() - startTime;
            if (!testSuiteMode && elapsedTime > (maxTime / 2)) {
                LOGGER.debug("# stopping iterative search because half time expired.");
                stopSearching = true;
            }*/
        } while (!stopSearching);

        assert(search.getLastPV().size()>0);
        assert(MoveUtils.isLineValid(search.getLastPV(), board));

        printSearchSummary(depth, startTime, search.getSearchStats());

        return search.getLastPV();
    }

    private static void printSearchSummary(int lastDepth, long startTime, SearchStats stats) {
        DecimalFormat df = new DecimalFormat("0.00");
        DecimalFormat df2 = new DecimalFormat("#,###,##0");

        long totalNodes = stats.nodes; // + stats.getQNodes();
        double interiorPct = stats.nodes / (totalNodes/100.0);
        double qnodePct = 0.0; //stats.getQNodes() / (totalNodes/100.0);

        LOGGER.info("\n");
        LOGGER.info("# depth: " + lastDepth);
        LOGGER.info("# nodes: " + df2.format(totalNodes) + ", interior: "
                + df2.format(stats.nodes) + " (" + df.format(interiorPct) + "%)"
                + ", quiescense: " + df2.format(0.0) + " (" + df.format(qnodePct) + "%)");

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
