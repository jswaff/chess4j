package com.jamesswafford.chess4j.eval;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.squares.Square;

import static com.jamesswafford.chess4j.eval.EvalMajorOn7th.evalMajorOn7th;

import static com.jamesswafford.chess4j.eval.EvalMajorOn7th.exractMajorOn7thFeatures;
import static com.jamesswafford.chess4j.eval.EvalWeights.*;

public class EvalQueen {

    public static int evalQueen(EvalWeights weights, Board board, Square sq, boolean endgame) {
        boolean isWhite = board.getPiece(sq).isWhite();
        int score;
        if (isWhite) {
            if (endgame) {
                score = weights.vals[QUEEN_ENDGAME_PST_IND + sq.value()];
            } else {
                score = weights.vals[QUEEN_PST_IND + sq.value()];
            }
        } else {
            if (endgame) {
                score = weights.vals[QUEEN_ENDGAME_PST_IND + sq.flipVertical().value()];
            } else {
                score = weights.vals[QUEEN_PST_IND + sq.flipVertical().value()];
            }
        }
        score += evalMajorOn7th(weights, board, isWhite, sq);
        return score;
    }

    public static java.lang.Void extractQueenFeatures(double[] features, Board board, Square sq, double phase) {
        boolean isWhite = board.getPiece(sq).isWhite();
        if (isWhite) {
            features[QUEEN_ENDGAME_PST_IND + sq.value()] += (1-phase);
            features[QUEEN_PST_IND + sq.value()] += phase;
        } else {
            features[QUEEN_ENDGAME_PST_IND + sq.flipVertical().value()] -= (1-phase);
            features[QUEEN_PST_IND + sq.flipVertical().value()] -= phase;
        }
        exractMajorOn7thFeatures(features, board, isWhite, sq);
        return null;
    }

}
