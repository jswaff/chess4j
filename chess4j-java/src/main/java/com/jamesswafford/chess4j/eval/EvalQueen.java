package com.jamesswafford.chess4j.eval;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.squares.Square;

import static com.jamesswafford.chess4j.eval.EvalMajorOn7th.evalMajorOn7th;

import static com.jamesswafford.chess4j.eval.EvalWeightsVector.*;

public class EvalQueen {

    public static int evalQueen(EvalFeaturesVector features, EvalWeightsVector weights, Board board, Square sq, boolean endgame) {
        boolean isWhite = board.getPiece(sq).isWhite();
        int score;
        if (isWhite) {
            if (endgame) {
                score = weights.weights[QUEEN_ENDGAME_PST_IND + sq.value()];
            } else {
                score = weights.weights[QUEEN_PST_IND + sq.value()];
            }
        } else {
            if (endgame) {
                score = weights.weights[QUEEN_ENDGAME_PST_IND + sq.flipVertical().value()];
            } else {
                score = weights.weights[QUEEN_PST_IND + sq.flipVertical().value()];
            }
        }
        score += evalMajorOn7th(weights, board, isWhite, sq);
        return score;
    }

}
