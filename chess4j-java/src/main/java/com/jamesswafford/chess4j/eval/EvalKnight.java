package com.jamesswafford.chess4j.eval;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Color;
import com.jamesswafford.chess4j.board.squares.Square;

import static com.jamesswafford.chess4j.eval.EvalTermsVector.*;

public class EvalKnight {

    public static int evalKnight(EvalTermsVector etv, Board board, Square sq, boolean endgame) {
        int score = 0;

        if (board.getPiece(sq).isWhite()) {
            score = etv.terms[KNIGHT_PST_IND + sq.value()];
            score += etv.terms[KNIGHT_TROPISM_IND] * sq.distance(board.getKingSquare(Color.BLACK));
        } else {
            score = etv.terms[KNIGHT_PST_IND + sq.flipVertical().value()];
            score += etv.terms[KNIGHT_TROPISM_IND] * sq.distance(board.getKingSquare(Color.WHITE));
        }

        return score;
    }


}
