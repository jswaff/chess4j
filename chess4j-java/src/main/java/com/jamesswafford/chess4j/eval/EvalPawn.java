package com.jamesswafford.chess4j.eval;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.squares.Square;
import io.vavr.Tuple2;

import static com.jamesswafford.chess4j.eval.EvalWeights.*;

public class EvalPawn {

    public static Tuple2<Integer, Integer> evalPawn(EvalWeights weights, Board board, Square sq) {
        boolean isWhite = board.getPiece(sq).isWhite();

        int mg, eg, s=0;

        if (PawnUtils.isPassedPawn(board, sq, isWhite)) {
            s += weights.vals[PASSED_PAWN_IND];
        }
        if (PawnUtils.isIsolated(board, sq, isWhite)) {
            s += weights.vals[ISOLATED_PAWN_IND];
        }
        if (PawnUtils.isDoubled(board, sq, isWhite)) {
            s += weights.vals[DOUBLED_PAWN_IND];
        }

        if (isWhite) {
            mg = weights.vals[PAWN_PST_IND + sq.value()] + s;
            eg = weights.vals[PAWN_ENDGAME_PST_IND + sq.value()] + s;
        } else {
            mg = -(weights.vals[PAWN_PST_IND + sq.flipVertical().value()] + s);
            eg = -(weights.vals[PAWN_ENDGAME_PST_IND + sq.flipVertical().value()] + s);
        }

        return new Tuple2<>(mg, eg);
    }

    public static java.lang.Void extractPawnFeatures(double[] features, Board board, Square sq, double phase) {
        boolean isWhite = board.getPiece(sq).isWhite();

        if (isWhite) {
            features[PAWN_ENDGAME_PST_IND + sq.value()] += (1-phase);
            features[PAWN_PST_IND + sq.value()] += phase;
        } else {
            features[PAWN_ENDGAME_PST_IND + sq.flipVertical().value()] -= (1-phase);
            features[PAWN_PST_IND + sq.flipVertical().value()] -= phase;
        }

        int v = isWhite ? 1 : -1;
        if (PawnUtils.isPassedPawn(board, sq, isWhite)) {
            features[PASSED_PAWN_IND] += v;
        }
        if (PawnUtils.isIsolated(board, sq, isWhite)) {
            features[ISOLATED_PAWN_IND] += v;
        }
        if (PawnUtils.isDoubled(board, sq, isWhite)) {
            features[DOUBLED_PAWN_IND] += v;
        }

        return null;
    }
}