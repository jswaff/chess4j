package com.jamesswafford.chess4j.search;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.board.Undo;

import java.util.List;

public interface Search {

    SearchStats getSearchStats();

    List<Move> getLastPV();

    int search(Board board, SearchParameters searchParameters);

    int search(Board board, List<Undo> undos, SearchParameters searchParameters);

}
