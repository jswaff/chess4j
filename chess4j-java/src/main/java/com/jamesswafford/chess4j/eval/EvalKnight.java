package com.jamesswafford.chess4j.eval;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Color;
import com.jamesswafford.chess4j.board.squares.Square;

import static com.jamesswafford.chess4j.eval.EvalWeightsVector.*;

public class EvalKnight {

    public static int evalKnight(EvalFeaturesVector features, EvalWeightsVector weights, Board board, Square sq,
                                 boolean endgame) {
        int score;

        if (board.getPiece(sq).isWhite()) {
            if (endgame) {
                score = weights.weights[KNIGHT_ENDGAME_PST_IND + sq.value()];
            } else {
                score = weights.weights[KNIGHT_PST_IND + sq.value()];
            }
            score += weights.weights[KNIGHT_TROPISM_IND] * sq.distance(board.getKingSquare(Color.BLACK));
        } else {
            if (endgame) {
                score = weights.weights[KNIGHT_ENDGAME_PST_IND + sq.flipVertical().value()];
            } else {
                score = weights.weights[KNIGHT_PST_IND + sq.flipVertical().value()];
            }
            score += weights.weights[KNIGHT_TROPISM_IND] * sq.distance(board.getKingSquare(Color.WHITE));
        }

        return score;
    }


}
