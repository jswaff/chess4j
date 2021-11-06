package com.jamesswafford.chess4j.eval;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.squares.Square;

import static com.jamesswafford.chess4j.eval.EvalMajorOn7th.evalMajorOn7th;

import static com.jamesswafford.chess4j.eval.EvalTermsVector.*;

public class EvalQueen {

    public static final int[] QUEEN_PST = {
            -1, -1, -1, -1, -1, -1, -1, -1,
            -1,  0,  0,  0,  0,  0,  0, -1,
            -1,  0,  1,  1,  1,  1,  0, -1,
            -1,  0,  1,  2,  2,  1,  0, -1,
            -1,  0,  1,  2,  2,  1,  0, -1,
            -1,  0,  1,  1,  1,  1,  0, -1,
            -1,  0,  0,  0,  0,  0,  0, -1,
            -1, -1, -1, -1, -1, -1, -1, -1 };

    public static int evalQueen(EvalTermsVector etv, Board board, Square sq) {
        boolean isWhite = board.getPiece(sq).isWhite();
        int score = QUEEN_PST[isWhite?sq.value():sq.flipVertical().value()];
        score += evalMajorOn7th(board, isWhite, sq);
        return score;
    }

}
