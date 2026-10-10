package dev.jamesswafford.chess4j.search;

import dev.jamesswafford.chess4j.board.Color;
import dev.jamesswafford.chess4j.board.Move;

public interface HistoryStore {

    void addCutoff(Color player, Move move, int depth);

    void addFailure(Color player, Move move, int depth);

    void clear();

    int getScore(Color player, Move move);

}
