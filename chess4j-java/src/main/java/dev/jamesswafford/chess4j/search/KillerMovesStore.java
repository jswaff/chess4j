package dev.jamesswafford.chess4j.search;

import dev.jamesswafford.chess4j.board.Move;

public interface KillerMovesStore {

    void addKiller(int ply, Move killerMove);

    void clear();

    Move getKiller1(int ply);

    Move getKiller2(int ply);

}
