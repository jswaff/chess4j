package com.jamesswafford.chess4j.search;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.jamesswafford.chess4j.hash.TranspositionTable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jamesswafford.chess4j.App;
import com.jamesswafford.chess4j.Constants;
import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.board.MoveGen;
import com.jamesswafford.chess4j.book.BookMove;
import com.jamesswafford.chess4j.eval.Eval;
import com.jamesswafford.chess4j.hash.TTHolder;
import com.jamesswafford.chess4j.io.PrintGameResult;
import com.jamesswafford.chess4j.io.PrintLine;
import com.jamesswafford.chess4j.utils.GameStatus;
import com.jamesswafford.chess4j.utils.GameStatusChecker;
import com.jamesswafford.chess4j.utils.MoveUtils;
import com.jamesswafford.chess4j.utils.TimeUtils;

/**
 * SearchIterator
 * @author james
 *
 */
public final class SearchIterator {

    private static final Log LOGGER = LogFactory.getLog(SearchIterator.class);

    private static final SearchIterator INSTANCE = new SearchIterator();

    public static boolean ponderEnabled;
    private static boolean pondering;
    private static Move ponderMove;
    public static final Object ponderMutex = new Object();

    public static int maxDepth;

    public static int remainingTimeMS;
    public static int incrementMS;
    public static int maxTime;

    public static boolean showThinking = true;
    public static boolean abortIterator = false;

    private SearchIterator() {	}

    public static SearchIterator getInstance() {
        return INSTANCE;
    }


    /**
     * Kick off the iterative deepening search in its own thread.
     * Returns the thread.
     *
     * @return
     */
    public static Thread think() {

        abortIterator = false;

        // make a copy of the current position.  note that just because we are operating
        // of a copy of the global position, the global position itself should remain
        // unchanged until our search is complete.
        Board searchPos = Board.INSTANCE.deepCopy();

        class Thinker implements Runnable {
            @Override
            public void run() {
                pondering = false;
                List<Move> pv = iterate(searchPos,false);
                assert(Board.INSTANCE.equals(searchPos));
                Board.INSTANCE.applyMove(pv.get(0));
                LOGGER.info("move " + pv.get(0));
                GameStatus gs = GameStatusChecker.getGameStatus();

                // pondering loop.  as long as we guess correctly we'll loop back around
                // if we don't predict correctly this thread is terminated
                boolean ponderSuccess = true;
                while (!abortIterator && gs==GameStatus.INPROGRESS
                        && ponderEnabled && pv.size() > 1 && ponderSuccess) {
                    ponderMove = pv.get(1);
                    LOGGER.debug("### START PONDERING: " + ponderMove);
                    pondering = true;
                    searchPos.applyMove(pv.get(0)); // apply the move just made so we're in sync
                    searchPos.applyMove(ponderMove); // apply the predicted move

                    // start an iterative search in ponder mode.  When the iterative search completes,
                    // if we're not in pondering mode, then a user move was received and we predicted
                    // correctly so we should play our response.  Otherwise, either the search fully
                    // resolved and exited early or the search was aborted.  In either case just terminate
                    // the thread.

                    pv = iterate(searchPos,false);
                    LOGGER.debug("# ponder search terminated.  analysis mode?: " + Search.analysisMode);

                    synchronized (ponderMutex) {
                        if (!pondering) {
                            assert(Board.INSTANCE.equals(searchPos));
                            Board.INSTANCE.applyMove(pv.get(0));
                            LOGGER.info("move " + pv.get(0));
                            gs = GameStatusChecker.getGameStatus();
                        } else {
                            pondering = false;
                            ponderSuccess = false;
                        }
                    }
                }

                LOGGER.debug("### exiting search thread");

                if (gs != GameStatus.INPROGRESS) {
                    PrintGameResult.printResult(gs);
                }
            }
        }

        Thread thinkThread = new Thread(new Thinker());
        thinkThread.start();

        return thinkThread;
    }

    public static void calculateSearchTimes() {
        maxTime = TimeUtils.getSearchTime(remainingTimeMS, incrementMS);
        Search.stopTime = Search.startTime + maxTime;
        LOGGER.debug("# calculated search time: " + maxTime);
    }

    /**
     * Iterate over the given position and return the principal variation.
     * The returned line (PV) is guaranteed to have at least one move.
     *
     * @return
     */
    public static List<Move> iterate(Board board,boolean testSuiteMode) {

        if (!testSuiteMode && !pondering && App.getOpeningBook() != null && board.getMoveCounter() <= 30) {
            BookMove bookMove = App.getOpeningBook().getMoveWeightedRandomByFrequency(board);
            if (bookMove != null) {
                LOGGER.debug("# book move: " + bookMove);
                return Arrays.asList(bookMove.getMove());
            }
        }

        // if just one legal move don't bother searching
        List<Move> moves = MoveGen.genLegalMoves(board);
        LOGGER.debug("# position has " + moves.size() + " move(s)");
        if (!testSuiteMode && moves.size()==1) {
            return Arrays.asList(moves.get(0));
        }

        TTHolder.clearAllTables();
        List<Move> pv = new ArrayList<Move>();
        SearchStats stats = new SearchStats();
        Search.analysisMode = pondering;
        Search.abortSearch = false;
        Search.startTime = System.currentTimeMillis();
        if (testSuiteMode) {
            Search.stopTime = Search.startTime + maxTime;
        } else {
            calculateSearchTimes();
        }
        int depth = 0;
        boolean stopSearching = false;

        int score = 0;
        do {
            ++depth;

            // aspiration windows
            int alphaBound = -Constants.INFINITY;
            int betaBound = Constants.INFINITY;
            if (depth > 2) {
                alphaBound = score - (Eval.PAWN_VAL / 3);
                betaBound = score + (Eval.PAWN_VAL / 3);
            }

            score=Search.search(pv,alphaBound, betaBound, board, depth,stats,true);

            if ((score <= alphaBound || score >= betaBound) && !Search.abortSearch) {
                LOGGER.debug("# research depth " + depth + "! alpha=" + alphaBound + ", beta=" + betaBound + ", score=" + score);
                score=Search.search(pv,-Constants.INFINITY, Constants.INFINITY, board, depth,stats,true);
            }

            assert(pv.size()>0);
            if (Search.abortSearch) {
                break;
            }

            if (showThinking) {
                PrintLine.printLine(pv,depth,score,Search.startTime,stats.getNodes());
            }

            stats.setLastPV(pv);

            //LOGGER.debug("# first line: " + PrintLine.getMoveString(stats.getFirstLine()));

            // if this is a mate, stop here
            if (Math.abs(score) > Constants.CHECKMATE-500) {
                LOGGER.debug("# stopping iterative search because mate found");
                stopSearching = true;
            }

            if (maxDepth > 0 && depth >= maxDepth) {
                LOGGER.debug("# stopping iterative search on depth");
                stopSearching = true;
            }

            // if we've used more than half our time, don't start a new iteration.
            long elapsedTime = System.currentTimeMillis() - Search.startTime;
            if (!pondering && !testSuiteMode && elapsedTime > (maxTime / 2)) {
                LOGGER.debug("# stopping iterative search because half time expired.");
                stopSearching = true;
            }
        } while (!stopSearching);

        assert(pv.size()>0);
        assert(MoveUtils.isLineValid(pv, board));

        printSearchSummary(depth,stats);

        return pv;
    }

    private static void printSearchSummary(int lastDepth,SearchStats stats) {
        DecimalFormat df = new DecimalFormat("0.00");
        DecimalFormat df2 = new DecimalFormat("#,###,##0");

        long totalNodes = stats.getNodes() + stats.getQNodes();
        double interiorPct = stats.getNodes() / (totalNodes/100.0);
        double qnodePct = stats.getQNodes() / (totalNodes/100.0);

        LOGGER.info("\n");
        LOGGER.info("# depth: " + lastDepth);
        LOGGER.info("# nodes: " + df2.format(totalNodes) + ", interior: "
                + df2.format(stats.getNodes()) + " (" + df.format(interiorPct) + "%)"
                + ", quiescense: " + df2.format(stats.getQNodes()) + " (" + df.format(qnodePct) + "%)");

        long totalSearchTime = System.currentTimeMillis() - Search.startTime;
        LOGGER.info("# search time: " + totalSearchTime/1000.0 + " seconds"
                + ", rate: " + df2.format(totalNodes / (totalSearchTime/1000.0)) + " nodes per second");

        long hashHits = TTHolder.getDepthPreferredTransTable().getNumHits()
                + TTHolder.getAlwaysReplaceTransTable().getNumHits();
        long hashProbes = TTHolder.getDepthPreferredTransTable().getNumProbes()
                + TTHolder.getAlwaysReplaceTransTable().getNumProbes();
        long hashCollisions = TTHolder.getDepthPreferredTransTable().getNumCollisions()
                + TTHolder.getAlwaysReplaceTransTable().getNumCollisions();
        double hashHitPct = hashHits / (hashProbes/100.0);
        double hashCollisionPct = hashCollisions / (hashProbes/100.0);

        LOGGER.info("# hash probes: " + df2.format(hashProbes)
                + ", hits: " + df2.format(hashHits) + " (" + df.format(hashHitPct) + "%)"
                + ", collisions: " + df2.format(hashCollisions) + " (" + df.format(hashCollisionPct) + "%)");

        long pawnHashHits = TTHolder.getPawnTransTable().getNumHits();
        long pawnHashProbes = TTHolder.getPawnTransTable().getNumProbes();
        long pawnHashCollisions = TTHolder.getPawnTransTable().getNumCollisions();
        double pawnHashHitPct = pawnHashHits / (pawnHashProbes/100.0);
        double pawnHashCollisionPct = pawnHashCollisions / (pawnHashProbes/100.0);

        LOGGER.info("# pawn hash probes: " + df2.format(pawnHashProbes)
                + ", hits: " + df2.format(pawnHashHits) + " (" + df.format(pawnHashHitPct) + "%)"
                + ", collisions: " + df2.format(pawnHashCollisions) + " (" + df.format(pawnHashCollisionPct) + "%)");

        double failHighPct = stats.getFailHighs() / (hashProbes/100.0);
        double failLowPct = stats.getFailLows() / (hashProbes/100.0);
        double exactScorePct = stats.getHashExactScores() / (hashProbes/100.0);
        LOGGER.info("# fail highs: " + df2.format(stats.getFailHighs()) + " (" + df.format(failHighPct) + "%)"
                + ", fail lows: " + df2.format(stats.getFailLows()) + " (" + df.format(failLowPct) + "%)"
                + ", exact scores: " + df2.format(stats.getHashExactScores()) + " (" + df.format(exactScorePct) + "%)");

        LOGGER.info("# prunes: " + stats.getPrunes());
    }

    private static void printHashStats(TranspositionTable ttable,String tableName) {

    }

    public static Move getPonderMove() {
        return ponderMove;
    }

    public static boolean isPondering() {
        return pondering;
    }

    public static void stopPondering() {
        pondering = false;
        Search.analysisMode = false;
    }

}
