package com.jamesswafford.chess4j.eval;

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

    public static int evalBishop(boolean isWhite, Square sq) {
        return BISHOP_PST[isWhite?sq.value():sq.flipVertical().value()];
    }

}
