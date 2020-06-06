package com.jamesswafford.chess4j.search;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Color;
import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.board.Undo;
import org.javatuples.Quintet;

import java.util.List;
import java.util.function.Consumer;

public interface Search {

    SearchStats getSearchStats();

    List<Move> getPv();

    void initialize();

    int search(Board board, SearchParameters searchParameters);

    int search(Board board, List<Undo> undos, SearchParameters searchParameters);

    void setPvCallback(Consumer<Quintet<Integer, List<Move>, Integer, Integer, Long>> pvCallback, Color ptm);

    boolean isStopped();

    void stop();

    void unstop();

}
