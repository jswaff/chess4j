package dev.jamesswafford.chess4j.eval;

import dev.jamesswafford.chess4j.board.Board;
import dev.jamesswafford.chess4j.board.squares.Square;
import dev.jamesswafford.chess4j.movegen.Mobility;
import io.vavr.Tuple2;

import static dev.jamesswafford.chess4j.eval.EvalMajorOn7th.evalMajorOn7th;

import static dev.jamesswafford.chess4j.eval.EvalMajorOn7th.exractMajorOn7thFeatures;
import static dev.jamesswafford.chess4j.eval.EvalWeights.*;

public class EvalQueen {

    public static Tuple2<Integer,Integer> evalQueen(EvalWeights weights, Board board, Square sq) {
        boolean isWhite = board.getPiece(sq).isWhite();

        int mg, eg;

        Tuple2<Integer, Integer> major7th = evalMajorOn7th(weights, board, isWhite, sq);

        int mobility = Mobility.queenMobility(board, sq);
        int mobilityMg = weights.vals[QUEEN_MOBILITY_MG_IND + mobility];
        int mobilityEg = weights.vals[QUEEN_MOBILITY_EG_IND + mobility];

        if (isWhite) {
            mg = weights.vals[QUEEN_PST_MG_IND + sq.value()] + major7th._1 + mobilityMg;
            eg = weights.vals[QUEEN_PST_EG_IND + sq.value()] + major7th._2 + mobilityEg;
        } else {
            mg = -(weights.vals[QUEEN_PST_MG_IND + sq.flipVertical().value()] + major7th._1 + mobilityMg);
            eg = -(weights.vals[QUEEN_PST_EG_IND + sq.flipVertical().value()] + major7th._2 + mobilityEg);
        }

        return new Tuple2<>(mg, eg);
    }

    public static java.lang.Void extractQueenFeatures(double[] features, Board board, Square sq, double phase) {
        boolean isWhite = board.getPiece(sq).isWhite();

        int mobility = Mobility.queenMobility(board, sq);

        if (isWhite) {
            features[QUEEN_PST_EG_IND + sq.value()] += (1-phase);
            features[QUEEN_PST_MG_IND + sq.value()] += phase;
            features[QUEEN_MOBILITY_MG_IND + mobility] += phase;
            features[QUEEN_MOBILITY_EG_IND + mobility] += (1-phase);
        } else {
            features[QUEEN_PST_EG_IND + sq.flipVertical().value()] -= (1-phase);
            features[QUEEN_PST_MG_IND + sq.flipVertical().value()] -= phase;
            features[QUEEN_MOBILITY_MG_IND + mobility] -= phase;
            features[QUEEN_MOBILITY_EG_IND + mobility] -= (1- phase);
        }
        exractMajorOn7thFeatures(features, board, isWhite, sq, phase);
        return null;
    }

}
