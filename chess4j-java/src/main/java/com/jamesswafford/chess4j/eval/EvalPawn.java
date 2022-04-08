package com.jamesswafford.chess4j.eval;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.squares.Square;

import static com.jamesswafford.chess4j.eval.EvalWeightsVector.*;

public class EvalPawn {

    public static int evalPawn(EvalWeightsVector weights, Board board, Square sq, boolean endgame) {
        boolean isWhite = board.getPiece(sq).isWhite();

        int score;
        if (isWhite) {
            if (endgame) {
                score = weights.weights[PAWN_ENDGAME_PST_IND + sq.value()];
            } else {
                score = weights.weights[PAWN_PST_IND + sq.value()];
            }
        } else {
            if (endgame) {
                score = weights.weights[PAWN_ENDGAME_PST_IND + sq.flipVertical().value()];
            } else {
                score = weights.weights[PAWN_PST_IND + sq.flipVertical().value()];
            }
        }

        if (PawnUtils.isPassedPawn(board, sq, isWhite)) {
            score += weights.weights[PASSED_PAWN_IND];
        }
        if (PawnUtils.isIsolated(board, sq, isWhite)) {
            score += weights.weights[ISOLATED_PAWN_IND];
        }
        if (PawnUtils.isDoubled(board, sq, isWhite)) {
            score += weights.weights[DOUBLED_PAWN_IND];
        }

        return score;
    }

    public static java.lang.Void extractPawnFeatures(int[] features, Board board, Square sq, boolean endgame) {
        boolean isWhite = board.getPiece(sq).isWhite();

        if (isWhite) {
            if (endgame) {
                features[PAWN_ENDGAME_PST_IND + sq.value()]++;
            } else {
                features[PAWN_PST_IND + sq.value()]++;
            }
        } else {
            if (endgame) {
                features[PAWN_ENDGAME_PST_IND + sq.flipVertical().value()]--;
            } else {
                features[PAWN_PST_IND + sq.flipVertical().value()]--;
            }
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