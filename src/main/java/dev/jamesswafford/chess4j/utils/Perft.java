package dev.jamesswafford.chess4j.utils;

import dev.jamesswafford.chess4j.board.Board;
import dev.jamesswafford.chess4j.board.Move;
import dev.jamesswafford.chess4j.board.Undo;
import dev.jamesswafford.chess4j.io.DrawBoard;
import dev.jamesswafford.chess4j.movegen.MagicBitboardMoveGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;


/*	Initial position
Depth 	Perft(Depth) 	Total Nodes
1 	20 	20
2 	400 	420
3 	8,902 	9322
4 	197,281 	206,603
5 	4,865,609 	5,072,212
6 	119,060,324 	124,132,536
7 	3,195,901,860 	3,320,034,396
*/

/*	r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq -
Depth 	Perft(Depth) 	Total Nodes
1 	48 	48
2 	2039 	2087
3 	97,862 	99,949
4 	4,085,603 	4,185,552
5 	193,690,690 	197,876,242
6 	8,031,647,685
Note: this exceeds 32 bits 	8,229,523,927	
*/	

class PerftCallable implements Callable<Long> {

    private Board board;
    private int depth;

    public PerftCallable(Board board, int depth) {
        this.board=board;
        this.depth=depth;
    }

    private long perft(int myDepth) {
        if (myDepth <= 0) {
            return 1;
        }

        List<Move> moves = MagicBitboardMoveGenerator.genLegalMoves(board);
        long n=0;

        for (Move m : moves) {
            Undo undo = board.applyMove(m);
            n += perft(myDepth-1);
            board.undoMove(undo);
        }

        return n;
    }

    @Override
    public Long call() throws Exception {
        return perft(depth);
    }

}

public final class Perft {
    private static final  Logger LOGGER = LogManager.getLogger(Perft.class);

    private Perft() { }

    public static long perft(Board board, int depth, int maxProcessors) {
        if (depth <= 0) {
            return 1;
        }

        int detectedProcessors = Runtime.getRuntime().availableProcessors();
        int processors = Math.min(maxProcessors, detectedProcessors);
        LOGGER.info("using {} of {} detected processors", processors, detectedProcessors);
        ExecutorService executor = Executors.newFixedThreadPool(processors);
        List<Future<Long>> futures = new ArrayList<>();
        List<Move> moves = MagicBitboardMoveGenerator.genLegalMoves(board);

        for (Move m : moves) {
            Board b2 = board.deepCopy();
            b2.applyMove(m);
            PerftCallable pc = new PerftCallable(b2,depth-1);
            futures.add(executor.submit(pc));
        }

        long n = 0;

        for (Future<Long> future : futures) {
            try {
                n += future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        return n;
    }

    public static void executePerft(Board board, int depth, int maxProcessors) {
        DrawBoard.drawBoard(board);

        long start = System.currentTimeMillis();
        long nodes = perft(board, depth, maxProcessors);
        long end = System.currentTimeMillis();
        if (end==start) end = start + 1; // HACK to avoid div 0

        DecimalFormat df = new DecimalFormat("0,000");
        LOGGER.info("# nodes: " + df.format(nodes));
        LOGGER.info("# elapsed time: " + (end-start) + " ms");
        LOGGER.info("# rate: " + df.format(nodes*1000/(end-start)) + " n/s\n");
    }
}
