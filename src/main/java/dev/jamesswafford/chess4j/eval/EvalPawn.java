package dev.jamesswafford.chess4j.eval;

import dev.jamesswafford.chess4j.board.Board;
import dev.jamesswafford.chess4j.board.squares.Square;
import io.vavr.Tuple2;

import static dev.jamesswafford.chess4j.eval.EvalWeights.*;

public class EvalPawn {

    public static Tuple2<Integer, Integer> evalPawn(EvalWeights weights, Board board, Square sq) {
        
        boolean isWhite = board.getPiece(sq).isWhite();

        int mg=0, eg=0;

        if (PawnUtils.isIsolated(board, sq, isWhite)) {
            mg += weights.vals[ISOLATED_PAWN_MG_IND];
            eg += weights.vals[ISOLATED_PAWN_EG_IND];
        }
        if (PawnUtils.isDoubled(board, sq, isWhite)) {
            mg += weights.vals[DOUBLED_PAWN_MG_IND];
            eg += weights.vals[DOUBLED_PAWN_EG_IND];
        }

        int mg2, eg2;
        if (isWhite) {
            mg2 = weights.vals[PAWN_PST_MG_IND + sq.value()] + mg;
            eg2 = weights.vals[PAWN_PST_EG_IND + sq.value()] + eg;
            if (PawnUtils.isPassedPawn(board, sq, true)) {
                mg2 += weights.vals[PASSED_PAWN_MG_IND+sq.rank().getValue()];
                eg2 += weights.vals[PASSED_PAWN_EG_IND+sq.rank().getValue()];
            }
        } else {
            mg2 = -(weights.vals[PAWN_PST_MG_IND + sq.flipVertical().value()] + mg);
            eg2 = -(weights.vals[PAWN_PST_EG_IND + sq.flipVertical().value()] + eg);
            if (PawnUtils.isPassedPawn(board, sq, false)) {
                mg2 -= weights.vals[PASSED_PAWN_MG_IND+sq.rank().flip().getValue()];
                eg2 -= weights.vals[PASSED_PAWN_EG_IND+sq.rank().flip().getValue()];
            }
        }

        return new Tuple2<>(mg2, eg2);
    }

    public static java.lang.Void extractPawnFeatures(double[] features, Board board, Square sq, double phase) {

        boolean isWhite = board.getPiece(sq).isWhite();

        boolean passed = PawnUtils.isPassedPawn(board, sq, isWhite);
        boolean isolated = PawnUtils.isIsolated(board, sq, isWhite);
        boolean doubled = PawnUtils.isDoubled(board, sq, isWhite);

        if (isWhite) {
            features[PAWN_PST_MG_IND + sq.value()] += phase;
            features[PAWN_PST_EG_IND + sq.value()] += (1-phase);

            if (passed) {
                features[PASSED_PAWN_MG_IND + sq.rank().getValue()] += phase;
                features[PASSED_PAWN_EG_IND + sq.rank().getValue()] += (1-phase);
            }
            if (isolated) {
                features[ISOLATED_PAWN_MG_IND] += phase;
                features[ISOLATED_PAWN_EG_IND] += (1-phase);
            }
            if (doubled) {
                features[DOUBLED_PAWN_MG_IND] += phase;
                features[DOUBLED_PAWN_EG_IND] += (1-phase);
            }
        } else {
            features[PAWN_PST_MG_IND + sq.flipVertical().value()] -= phase;
            features[PAWN_PST_EG_IND + sq.flipVertical().value()] -= (1-phase);

            if (passed) {
                features[PASSED_PAWN_MG_IND + sq.flipVertical().rank().getValue()] -= phase;
                features[PASSED_PAWN_EG_IND + sq.flipVertical().rank().getValue()] -= (1-phase);
            }
            if (isolated) {
                features[ISOLATED_PAWN_MG_IND] -= phase;
                features[ISOLATED_PAWN_EG_IND] -= (1-phase);
            }
            if (doubled) {
                features[DOUBLED_PAWN_MG_IND] -= phase;
                features[DOUBLED_PAWN_EG_IND] -= (1-phase);
            }
        }

        return null;
    }
}