package com.jamesswafford.chess4j.eval;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.squares.Square;

import static com.jamesswafford.chess4j.eval.EvalWeights.*;

public class EvalPawn {

    public static int evalPawn(EvalWeights weights, Board board, Square sq, boolean endgame) {
        boolean isWhite = board.getPiece(sq).isWhite();

        int score;
        if (isWhite) {
            if (endgame) {
                score = weights.vals[PAWN_ENDGAME_PST_IND + sq.value()];
            } else {
                score = weights.vals[PAWN_PST_IND + sq.value()];
            }
        } else {
            if (endgame) {
                score = weights.vals[PAWN_ENDGAME_PST_IND + sq.flipVertical().value()];
            } else {
                score = weights.vals[PAWN_PST_IND + sq.flipVertical().value()];
            }
        }

        if (PawnUtils.isPassedPawn(board, sq, isWhite)) {
            score += weights.vals[PASSED_PAWN_IND];
        }
        if (PawnUtils.isIsolated(board, sq, isWhite)) {
            score += weights.vals[ISOLATED_PAWN_IND];
        }
        if (PawnUtils.isDoubled(board, sq, isWhite)) {
            score += weights.vals[DOUBLED_PAWN_IND];
        }

        return score;
    }

    public static java.lang.Void extractPawnFeatures(double[] features, Board board, Square sq, boolean endgame) {
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