package com.jamesswafford.chess4j.eval;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.squares.Square;

public class EvalBishop {

    public static final int[] BISHOP_PST = {
            0, 0,  0,  0,  0,  0, 0, 0,
            0, 7,  7,  7,  7,  7, 7, 0,
            0, 7, 15, 15, 15, 15, 7, 0,
            0, 7, 15, 20, 20, 15, 7, 0,
            0, 7, 15, 20, 20, 15, 7, 0,
            0, 7, 15, 15, 15, 15, 7, 0,
            0, 7,  7,  7,  7,  7, 7, 0,
            0, 0,  0,  0,  0,  0, 0, 0 };

    public static final int BISHOP_PAIR = 50;

    public static int evalBishop(Board board, Square sq) {
        if (board.getPiece(sq).isWhite()) {
            return BISHOP_PST[sq.value()];
        } else {
            return BISHOP_PST[sq.flipVertical().value()];
        }
    }

    public static int evalBishopPair(Board board) {
        int score = 0;
        if (Long.bitCount(board.getWhiteBishops()) > 1) score += BISHOP_PAIR;
        if (Long.bitCount(board.getBlackBishops()) > 1) score -= BISHOP_PAIR;
        return score;
    }
}
