package com.jamesswafford.chess4j.eval;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.squares.Square;

import static com.jamesswafford.chess4j.eval.EvalTermsVector.*;

public class EvalBishop {

    public static int evalBishop(EvalTermsVector etv, Board board, Square sq, boolean endgame) {
        if (board.getPiece(sq).isWhite()) {
            return etv.terms[BISHOP_PST_IND + sq.value()];
        } else {
            return etv.terms[BISHOP_PST_IND + sq.flipVertical().value()];
        }
    }

}
