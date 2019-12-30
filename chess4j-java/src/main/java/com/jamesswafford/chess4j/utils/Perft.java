package com.jamesswafford.chess4j.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.movegen.MoveGen;


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

    public PerftCallable(Board board,int depth) {
        this.board=board;
        this.depth=depth;
    }

    private long perft(int myDepth) {
        if (myDepth <= 0) {
            return 1;
        }

        List<Move> moves = MoveGen.genLegalMoves(board);
        long n=0;

        for (Move m : moves) {
            board.applyMove(m);
            n += perft(myDepth-1);
            board.undoLastMove();
        }

        return n;
    }

    @Override
    public Long call() throws Exception {
        return perft(depth);
    }

}

public final class Perft {
    private static final Log LOGGER = LogFactory.getLog(Perft.class);

    private Perft() { }

    public static long perft(Board b,int depth) {
        if (depth <= 0) {
            return 1;
        }

        int processors = Runtime.getRuntime().availableProcessors();
        LOGGER.info("detected " + processors + " processors.");
        ExecutorService executor = Executors.newFixedThreadPool(processors);
        List<Future<Long>> futures = new ArrayList<>();
        List<Move> moves = MoveGen.genLegalMoves(b);

        for (Move m : moves) {
            Board b2 = b.deepCopy();
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

}
