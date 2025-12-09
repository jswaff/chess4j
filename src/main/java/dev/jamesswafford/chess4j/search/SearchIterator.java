package dev.jamesswafford.chess4j.search;

import dev.jamesswafford.chess4j.board.Board;
import dev.jamesswafford.chess4j.board.Move;
import dev.jamesswafford.chess4j.board.Undo;
import io.vavr.Tuple2;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface SearchIterator {

    void setEarlyExitOk(boolean earlyExitOk);

    void setMaxDepth(int maxDepth);

    void setMaxTime(int maxTimeMs);

    void setMaxNodes(long maxNodes);

    void setPost(boolean post);

    void setSkipTimeChecks(boolean skipTimeChecks);

    CompletableFuture<Tuple2<List<Move>, Integer>> findPvFuture(final Board board, final List<Undo> undos);

    boolean isStopped();

    void stop();

    void unstop();

}
