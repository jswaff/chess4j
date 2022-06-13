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
            int tropismScoreMg = weights.vals[KNIGHT_TROPISM_MG_IND] * sq.distance(board.getKingSquare(Color.BLACK));
            mg = weights.vals[KNIGHT_PST_MG_IND + sq.value()] + tropismScoreMg;

            int tropismScoreEg = weights.vals[KNIGHT_TROPISM_EG_IND] * sq.distance(board.getKingSquare(Color.BLACK));
            eg = weights.vals[KNIGHT_PST_EG_IND + sq.value()] + tropismScoreEg;
        } else {
            int tropismScoreMg = weights.vals[KNIGHT_TROPISM_MG_IND] * sq.distance(board.getKingSquare(Color.WHITE));
            mg = -(weights.vals[KNIGHT_PST_MG_IND + sq.flipVertical().value()] + tropismScoreMg);

            int tropismScoreEg = weights.vals[KNIGHT_TROPISM_EG_IND] * sq.distance(board.getKingSquare(Color.WHITE));
            eg = -(weights.vals[KNIGHT_PST_EG_IND + sq.flipVertical().value()] + tropismScoreEg);
        }

        return new Tuple2<>(mg, eg);
    }

    public static java.lang.Void extractKnightFeatures(double[] features, Board board, Square sq, double phase) {
        if (board.getPiece(sq).isWhite()) {
            features[KNIGHT_PST_MG_IND + sq.value()] += phase;
            features[KNIGHT_PST_EG_IND + sq.value()] += (1-phase);

            features[KNIGHT_TROPISM_MG_IND] += sq.distance(board.getKingSquare(Color.BLACK)) * phase;
            features[KNIGHT_TROPISM_EG_IND] += sq.distance(board.getKingSquare(Color.BLACK)) * (1-phase);
        } else {
            features[KNIGHT_PST_MG_IND + sq.flipVertical().value()] -= phase;
            features[KNIGHT_PST_EG_IND + sq.flipVertical().value()] -= (1-phase);

            features[KNIGHT_TROPISM_MG_IND] -= sq.distance(board.getKingSquare(Color.WHITE)) * phase;
            features[KNIGHT_TROPISM_EG_IND] -= sq.distance(board.getKingSquare(Color.WHITE)) * (1-phase);
        }
        return null;
    }

}
