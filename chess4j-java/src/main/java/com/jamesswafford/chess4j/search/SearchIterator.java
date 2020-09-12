package com.jamesswafford.chess4j.search;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.board.Undo;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface SearchIterator {

    void setEarlyExitOk(boolean earlyExitOk);

    void setMaxDepth(int maxDepth);

    void setMaxTime(long maxTimeMs);

    void setPost(boolean post);

    void setSkipTimeChecks(boolean skipTimeChecks);

    CompletableFuture<List<Move>> findPvFuture(final Board board, final List<Undo> undos);

    boolean isStopped();

    void stop();

    void unstop();

}
