package com.jamesswafford.chess4j.eval;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.squares.Square;

import static com.jamesswafford.chess4j.eval.EvalTermsVector.*;

public class EvalPawn {

    public static final int[] PAWN_PST = {
             0,  0,  0,  0,  0,  0,  0,  0,
            30, 30, 30, 30, 30, 30, 30, 30,
            14, 14, 14, 18, 18, 14, 14, 14,
             7,  7,  7, 10, 10,  7,  7,  7,
             5,  5,  5,  7,  7,  5,  5,  5,
             3,  3,  3,  5,  5,  3,  3,  3,
             0,  0,  0, -3, -3,  0,  0,  0,
             0,  0,  0,  0,  0,  0,  0,  0 };

    // a passed pawn is a pawn with no enemy pawn in front of it or on an adjacent file
    public static final int PASSED_PAWN = 20;

    // an isolated pawn is one with no friendly pawn on an adjacent file
    public static final int ISOLATED_PAWN = -20;

    // a doubled pawn is a pawn that resides on the same file as a friendly pawn
    // note this would get "awarded" to both pawns
    public static final int DOUBLED_PAWN = -10;


    public static int evalPawn(EvalTermsVector etv, Board board, Square sq) {
        int score=0;

        boolean isWhite = board.getPiece(sq).isWhite();

        score += PAWN_PST[isWhite ? sq.value() : sq.flipVertical().value()];
        if (PawnUtils.isPassedPawn(board, sq, isWhite)) {
            score += PASSED_PAWN;
        }
        if (PawnUtils.isIsolated(board, sq, isWhite)) {
            score += ISOLATED_PAWN;
        }
        if (PawnUtils.isDoubled(board, sq, isWhite)) {
            score += DOUBLED_PAWN;
        }

        return score;
    }

}
