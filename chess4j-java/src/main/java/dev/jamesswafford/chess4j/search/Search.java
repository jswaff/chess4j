package dev.jamesswafford.chess4j.search;

import dev.jamesswafford.chess4j.board.Board;
import dev.jamesswafford.chess4j.board.Move;
import dev.jamesswafford.chess4j.board.Undo;

import java.util.List;

public interface Search {

    SearchStats getSearchStats();

    List<Move> getPv();

    void initialize();

    int search(Board board, SearchParameters searchParameters);

    int search(Board board, SearchParameters searchParameters, SearchOptions opts);

    int search(Board board, List<Undo> undos, SearchParameters searchParameters);

    int search(Board board, List<Undo> undos, SearchParameters searchParameters, SearchOptions opts);

    boolean isStopped();

    void stop();

    void unstop();

    void setSkipTimeChecks(boolean skipTimeChecks);

}
