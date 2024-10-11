package dev.jamesswafford.chess4j.eval;

import dev.jamesswafford.chess4j.board.Board;

public interface Evaluator {

    int evaluateBoard(Board board);

}
