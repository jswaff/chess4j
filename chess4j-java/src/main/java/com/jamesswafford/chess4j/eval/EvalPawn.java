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

}
