package com.jamesswafford.chess4j.eval;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.squares.Square;

import static com.jamesswafford.chess4j.eval.EvalMajorOn7th.evalMajorOn7th;

import static com.jamesswafford.chess4j.eval.EvalTermsVector.*;

public class EvalQueen {

    public static int evalQueen(EvalTermsVector etv, Board board, Square sq, boolean endgame) {
        boolean isWhite = board.getPiece(sq).isWhite();
        int score;
        if (isWhite) {
            if (endgame) {
                score = etv.terms[QUEEN_ENDGAME_PST_IND + sq.value()];
            } else {
                score = etv.terms[QUEEN_PST_IND + sq.value()];
            }
        } else {
            if (endgame) {
                score = etv.terms[QUEEN_ENDGAME_PST_IND + sq.flipVertical().value()];
            } else {
                score = etv.terms[QUEEN_PST_IND + sq.flipVertical().value()];
            }
        }
        score += evalMajorOn7th(etv, board, isWhite, sq);
        return score;
    }

}
