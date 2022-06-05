package com.jamesswafford.chess4j.eval;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.squares.Square;
import com.jamesswafford.chess4j.movegen.Mobility;
import io.vavr.Tuple2;

import static com.jamesswafford.chess4j.eval.EvalMajorOn7th.evalMajorOn7th;

import static com.jamesswafford.chess4j.eval.EvalMajorOn7th.exractMajorOn7thFeatures;
import static com.jamesswafford.chess4j.eval.EvalWeights.*;

public class EvalQueen {

    public static Tuple2<Integer,Integer> evalQueen(EvalWeights weights, Board board, Square sq) {
        boolean isWhite = board.getPiece(sq).isWhite();

        int mg, eg;

        Tuple2<Integer, Integer> major7th = evalMajorOn7th(weights, board, isWhite, sq);

        int mobility = Mobility.queenMobility(board, sq);
        int mobilityMg = mobility * weights.vals[QUEEN_MOBILITY_IND];
        int mobilityEg = mobility * weights.vals[QUEEN_ENDGAME_MOBILITY_IND];

        if (isWhite) {
            mg = weights.vals[QUEEN_PST_IND + sq.value()] + major7th._1 + mobilityMg;
            eg = weights.vals[QUEEN_ENDGAME_PST_IND + sq.value()] + major7th._2 + mobilityEg;
        } else {
            mg = -(weights.vals[QUEEN_PST_IND + sq.flipVertical().value()] + major7th._1 + mobilityMg);
            eg = -(weights.vals[QUEEN_ENDGAME_PST_IND + sq.flipVertical().value()] + major7th._2 + mobilityEg);
        }

        return new Tuple2<>(mg, eg);
    }

    public static java.lang.Void extractQueenFeatures(double[] features, Board board, Square sq, double phase) {
        boolean isWhite = board.getPiece(sq).isWhite();

        int mobility = Mobility.queenMobility(board, sq);

        if (isWhite) {
            features[QUEEN_ENDGAME_PST_IND + sq.value()] += (1-phase);
            features[QUEEN_PST_IND + sq.value()] += phase;
            features[QUEEN_MOBILITY_IND] += mobility * phase;
            features[QUEEN_ENDGAME_MOBILITY_IND] += mobility * (1-phase);
        } else {
            features[QUEEN_ENDGAME_PST_IND + sq.flipVertical().value()] -= (1-phase);
            features[QUEEN_PST_IND + sq.flipVertical().value()] -= phase;
            features[QUEEN_MOBILITY_IND] -= mobility * phase;
            features[QUEEN_ENDGAME_MOBILITY_IND] -= mobility * (1- phase);
        }
        exractMajorOn7thFeatures(features, board, isWhite, sq, phase);
        return null;
    }

}
