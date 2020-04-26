package com.jamesswafford.chess4j.eval;

import com.jamesswafford.chess4j.board.Bitboard;
import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.squares.Square;

import static com.jamesswafford.chess4j.eval.EvalMajorOn7th.evalMajorOn7th;

public class EvalRook {

    public static final int[] ROOK_PST = {
             0,  0,  0,  0,  0,  0,  0,  0,
             0,  0,  0,  0,  0,  0,  0,  0,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
             0,  0,  0,  0,  0,  0,  0,  0 };

    // an open file is one with no pawns of either color on it
    public static final int ROOK_OPEN_FILE = 25;

    // a half-open file is one with enemy pawns but not our own
    public static final int ROOK_HALF_OPEN_FILE = 15;


    public static int evalRook(Board board, Square sq) {
        boolean isWhite = board.getPiece(sq).isWhite();
        int score = ROOK_PST[isWhite?sq.value():sq.flipVertical().value()];
        score += evalMajorOn7th(board, isWhite, sq);
        score += evalRookOpenFile(board, isWhite, sq);
        return score;
    }

    private static int evalRookOpenFile(Board board, boolean isWhite, Square sq) {
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
                score += ROOK_HALF_OPEN_FILE;
            } else {
                score += ROOK_OPEN_FILE;
            }
        }

        return score;
    }

}
