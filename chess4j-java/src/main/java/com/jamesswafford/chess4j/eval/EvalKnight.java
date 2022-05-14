package com.jamesswafford.chess4j.eval;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Color;
import com.jamesswafford.chess4j.board.squares.Square;
import io.vavr.Tuple2;

import static com.jamesswafford.chess4j.eval.EvalWeights.*;

public class EvalKnight {

    public static Tuple2<Integer,Integer> evalKnight(EvalWeights weights, Board board, Square sq) {
        int mg, eg;

        if (board.getPiece(sq).isWhite()) {
            int tropismScore = weights.vals[KNIGHT_TROPISM_IND] * sq.distance(board.getKingSquare(Color.BLACK));
            mg = weights.vals[KNIGHT_PST_IND + sq.value()] + tropismScore;
            eg = weights.vals[KNIGHT_ENDGAME_PST_IND + sq.value()] + tropismScore;
        } else {
            int tropismScore = weights.vals[KNIGHT_TROPISM_IND] * sq.distance(board.getKingSquare(Color.WHITE));
            mg = -(weights.vals[KNIGHT_PST_IND + sq.flipVertical().value()] + tropismScore);
            eg = -(weights.vals[KNIGHT_ENDGAME_PST_IND + sq.flipVertical().value()] + tropismScore);
        }

        return new Tuple2<>(mg, eg);
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
