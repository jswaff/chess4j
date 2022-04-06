package com.jamesswafford.chess4j.eval;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.squares.Square;

import static com.jamesswafford.chess4j.eval.EvalWeightsVector.*;

public class EvalPawn {

    public static int evalPawn(EvalWeightsVector etv, Board board, Square sq, boolean endgame) {
        boolean isWhite = board.getPiece(sq).isWhite();

        int score;
        if (isWhite) {
            if (endgame) {
                score = etv.terms[PAWN_ENDGAME_PST_IND + sq.value()];
            } else {
                score = etv.terms[PAWN_PST_IND + sq.value()];
            }
        } else {
            if (endgame) {
                score = etv.terms[PAWN_ENDGAME_PST_IND + sq.flipVertical().value()];
            } else {
                score = etv.terms[PAWN_PST_IND + sq.flipVertical().value()];
            }
        }

        if (PawnUtils.isPassedPawn(board, sq, isWhite)) {
            score += etv.terms[PASSED_PAWN_IND];
        }
        if (PawnUtils.isIsolated(board, sq, isWhite)) {
            score += etv.terms[ISOLATED_PAWN_IND];
        }
        if (PawnUtils.isDoubled(board, sq, isWhite)) {
            score += etv.terms[DOUBLED_PAWN_IND];
        }

        return score;
    }

}
