package com.jamesswafford.chess4j.eval;

import com.jamesswafford.chess4j.board.Bitboard;
import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.squares.Square;

import static com.jamesswafford.chess4j.eval.EvalMajorOn7th.evalMajorOn7th;

import static com.jamesswafford.chess4j.eval.EvalTermsVector.*;

public class EvalRook {

    public static int evalRook(EvalTermsVector etv, Board board, Square sq) {
        boolean isWhite = board.getPiece(sq).isWhite();
        int score = etv.terms[ROOK_PST_IND + (isWhite?sq.value():sq.flipVertical().value())];
        score += evalMajorOn7th(board, isWhite, sq);
        score += evalRookOpenFile(etv, board, isWhite, sq);
        return score;
    }

    private static int evalRookOpenFile(EvalTermsVector etv, Board board, boolean isWhite, Square sq) {
        int score = 0;

        long friends,enemies;
        if (isWhite) {
            friends = board.getWhitePawns();
            enemies = board.getBlackPawns();
        } else {
            friends = board.getBlackPawns();
            enemies = board.getWhitePawns();
        }

        long fileMask = Bitboard.files[sq.file().getValue()] ^ Bitboard.squares[sq.value()];
        if ((fileMask & friends)==0) {
            if ((fileMask & enemies)!=0) {
                score += etv.terms[ROOK_HALF_OPEN_FILE_IND];
            } else {
                score += etv.terms[ROOK_OPEN_FILE_IND];
            }
        }

        return score;
    }

}
