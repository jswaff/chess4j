package com.jamesswafford.chess4j.eval;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.squares.Square;
import io.vavr.Tuple2;

import static com.jamesswafford.chess4j.eval.EvalWeights.*;

public class EvalBishop {

    public static Tuple2<Integer,Integer> evalBishop(EvalWeights weights, Board board, Square sq) {
        int mg, eg;

        if (board.getPiece(sq).isWhite()) {
            mg = weights.vals[BISHOP_PST_IND + sq.value()];
            eg = weights.vals[BISHOP_ENDGAME_PST_IND + sq.value()];
        } else {
            mg = -weights.vals[BISHOP_PST_IND + sq.flipVertical().value()];
            eg = -weights.vals[BISHOP_ENDGAME_PST_IND + sq.flipVertical().value()];
        }

        return new Tuple2<>(mg, eg);
    }

    public static java.lang.Void extractBishopFeatures(double[] features, Board board, Square sq, double phase) {
        if (board.getPiece(sq).isWhite()) {
            features[BISHOP_ENDGAME_PST_IND + sq.value()] += (1-phase);
            features[BISHOP_PST_IND + sq.value()] += phase;
        } else {
            features[BISHOP_ENDGAME_PST_IND + sq.flipVertical().value()] -= (1-phase);
            features[BISHOP_PST_IND + sq.flipVertical().value()] -= phase;
        }
        return null;
    }

}
