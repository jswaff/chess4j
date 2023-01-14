package com.jamesswafford.chess4j.eval;

import com.jamesswafford.chess4j.board.Board;

public interface Evaluator {

    int evaluateBoard(Board board);

    double evaluateBoardWithNN(Board board);

}
