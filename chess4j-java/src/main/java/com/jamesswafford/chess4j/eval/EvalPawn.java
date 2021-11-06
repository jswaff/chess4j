package com.jamesswafford.chess4j.eval;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.squares.Square;

import static com.jamesswafford.chess4j.eval.EvalTermsVector.*;

public class EvalPawn {

    public static int evalPawn(EvalTermsVector etv, Board board, Square sq) {
        int score=0;

        boolean isWhite = board.getPiece(sq).isWhite();

        score += etv.terms[PAWN_PST_IND + (isWhite ? sq.value() : sq.flipVertical().value())];
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
