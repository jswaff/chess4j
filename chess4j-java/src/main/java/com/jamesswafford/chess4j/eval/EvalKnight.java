package com.jamesswafford.chess4j.eval;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Color;
import com.jamesswafford.chess4j.board.squares.Square;

import static com.jamesswafford.chess4j.eval.EvalWeights.*;

public class EvalKnight {

    public static int evalKnight(EvalWeights weights, Board board, Square sq, boolean endgame) {
        int score;

        if (board.getPiece(sq).isWhite()) {
            if (endgame) {
                score = weights.vals[KNIGHT_ENDGAME_PST_IND + sq.value()];
            } else {
                score = weights.vals[KNIGHT_PST_IND + sq.value()];
            }
            score += weights.vals[KNIGHT_TROPISM_IND] * sq.distance(board.getKingSquare(Color.BLACK));
        } else {
            if (endgame) {
                score = weights.vals[KNIGHT_ENDGAME_PST_IND + sq.flipVertical().value()];
            } else {
                score = weights.vals[KNIGHT_PST_IND + sq.flipVertical().value()];
            }
            score += weights.vals[KNIGHT_TROPISM_IND] * sq.distance(board.getKingSquare(Color.WHITE));
        }

        return score;
    }

    public static java.lang.Void extractKnightFeatures(double[] features, Board board, Square sq, double phase) {
        if (board.getPiece(sq).isWhite()) {
            features[KNIGHT_ENDGAME_PST_IND + sq.value()] += (1-phase);
            features[KNIGHT_PST_IND + sq.value()] += phase;
            features[KNIGHT_TROPISM_IND] += sq.distance(board.getKingSquare(Color.BLACK));
        } else {
            features[KNIGHT_ENDGAME_PST_IND + sq.flipVertical().value()] -= (1-phase);
            features[KNIGHT_PST_IND + sq.flipVertical().value()] -= phase;
            features[KNIGHT_TROPISM_IND] -= sq.distance(board.getKingSquare(Color.WHITE));
        }
        return null;
    }

}
