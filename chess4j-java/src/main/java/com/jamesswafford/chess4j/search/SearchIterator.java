package com.jamesswafford.chess4j.search;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.board.Undo;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface SearchIterator {

    void setMaxDepth(int maxDepth);

    void setPost(boolean post);

    CompletableFuture<List<Move>> findPvFuture(Board board, List<Undo> undos);

}
