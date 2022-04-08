package com.jamesswafford.chess4j.eval;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.squares.Square;

import static com.jamesswafford.chess4j.eval.EvalWeightsVector.*;

public class EvalBishop {

    public static int evalBishop(EvalWeightsVector weights, Board board, Square sq, boolean endgame) {
        if (board.getPiece(sq).isWhite()) {
            if (endgame) {
                return weights.weights[BISHOP_ENDGAME_PST_IND + sq.value()];
            } else {
                return weights.weights[BISHOP_PST_IND + sq.value()];
            }
        } else {
            if (endgame) {
                return weights.weights[BISHOP_ENDGAME_PST_IND + sq.flipVertical().value()];
            } else {
                return weights.weights[BISHOP_PST_IND + sq.flipVertical().value()];
            }
        }
    }

    public static java.lang.Void extractBishopFeatures(int[] features, Board board, Square sq, boolean endgame) {
        if (board.getPiece(sq).isWhite()) {
            if (endgame) {
                features[BISHOP_ENDGAME_PST_IND + sq.value()]++;
            } else {
                features[BISHOP_PST_IND + sq.value()]++;
            }
        } else {
            if (endgame) {
                features[BISHOP_ENDGAME_PST_IND + sq.flipVertical().value()]--;
            } else {
                features[BISHOP_PST_IND + sq.flipVertical().value()]--;
            }
        }
        return null;
    }

}
