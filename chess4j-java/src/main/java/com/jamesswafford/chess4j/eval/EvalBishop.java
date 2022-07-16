package com.jamesswafford.chess4j.eval;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.squares.Square;
import com.jamesswafford.chess4j.movegen.Mobility;
import io.vavr.Tuple2;

import static com.jamesswafford.chess4j.eval.EvalWeights.*;

public class EvalBishop {

    public static Tuple2<Integer,Integer> evalBishop(EvalWeights weights, Board board, Square sq) {
        int mg, eg;

        int mobility = Mobility.bishopMobility(board, sq);
        int mobilityMg = weights.vals[BISHOP_MOBILITY_MG_IND + mobility];
        int mobilityEg = weights.vals[BISHOP_MOBILITY_EG_IND + mobility];

        if (board.getPiece(sq).isWhite()) {
            mg = weights.vals[BISHOP_PST_MG_IND + sq.value()] + mobilityMg;
            eg = weights.vals[BISHOP_PST_EG_IND + sq.value()] + mobilityEg;
        } else {
            mg = -(weights.vals[BISHOP_PST_MG_IND + sq.flipVertical().value()] + mobilityMg);
            eg = -(weights.vals[BISHOP_PST_EG_IND + sq.flipVertical().value()]  + mobilityEg);
        }

        return new Tuple2<>(mg, eg);
    }

    public static java.lang.Void extractBishopFeatures(double[] features, Board board, Square sq, double phase) {

        int mobility = Mobility.bishopMobility(board, sq);

        if (board.getPiece(sq).isWhite()) {
            features[BISHOP_PST_MG_IND + sq.value()] += phase;
            features[BISHOP_PST_EG_IND + sq.value()] += (1-phase);
            features[BISHOP_MOBILITY_MG_IND + mobility] += phase;
            features[BISHOP_MOBILITY_EG_IND + mobility] += (1-phase);

        } else {
            features[BISHOP_PST_MG_IND + sq.flipVertical().value()] -= phase;
            features[BISHOP_PST_EG_IND + sq.flipVertical().value()] -= (1-phase);
            features[BISHOP_MOBILITY_MG_IND + mobility] -= phase;
            features[BISHOP_MOBILITY_EG_IND + mobility] -= (1- phase);
        }
        return null;
    }

}
