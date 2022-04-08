package com.jamesswafford.chess4j.eval;

import com.jamesswafford.chess4j.board.Bitboard;
import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.squares.Square;

import static com.jamesswafford.chess4j.eval.EvalMajorOn7th.*;

import static com.jamesswafford.chess4j.eval.EvalWeightsVector.*;

public class EvalRook {

    public static int evalRook(EvalWeightsVector weights, Board board, Square sq, boolean endgame) {
        boolean isWhite = board.getPiece(sq).isWhite();
        int score;
        if (isWhite) {
            if (endgame) {
                score = weights.weights[ROOK_ENDGAME_PST_IND + sq.value()];
            } else {
                score = weights.weights[ROOK_PST_IND + sq.value()];
            }
        } else {
            if (endgame) {
                score = weights.weights[ROOK_ENDGAME_PST_IND + sq.flipVertical().value()];
            } else {
                score = weights.weights[ROOK_PST_IND + sq.flipVertical().value()];
            }
        }
        score += evalMajorOn7th(weights, board, isWhite, sq);
        score += evalRookOpenFile(weights, board, isWhite, sq);
        return score;
    }

    private static int evalRookOpenFile(EvalWeightsVector weights, Board board, boolean isWhite, Square sq) {
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
                score += weights.weights[ROOK_HALF_OPEN_FILE_IND];
            } else {
                score += weights.weights[ROOK_OPEN_FILE_IND];
            }
        }

        return score;
    }

    public static java.lang.Void extractRookFeatures(int[] features, Board board, Square sq, boolean endgame) {
        boolean isWhite = board.getPiece(sq).isWhite();
        if (isWhite) {
            if (endgame) {
                features[ROOK_ENDGAME_PST_IND + sq.value()]++;
            } else {
                features[ROOK_PST_IND + sq.value()]++;
            }
        } else {
            if (endgame) {
                features[ROOK_ENDGAME_PST_IND + sq.flipVertical().value()]--;
            } else {
                features[ROOK_PST_IND + sq.flipVertical().value()]--;
            }
        }

        exractMajorOn7thFeatures(features, board, isWhite, sq);
        extractRookFeatures_OpenFile(features, board, isWhite, sq);

        return null;
    }

    private static void extractRookFeatures_OpenFile(int[] features, Board board, boolean isWhite, Square sq) {
        long friends,enemies;
        int inc;
        if (isWhite) {
            friends = board.getWhitePawns();
            enemies = board.getBlackPawns();
            inc = 1;
        } else {
            friends = board.getBlackPawns();
            enemies = board.getWhitePawns();
            inc = -1;
        }

        long fileMask = Bitboard.files[sq.file().getValue()] ^ Bitboard.squares[sq.value()];
        if ((fileMask & friends)==0) {
            if ((fileMask & enemies)!=0) {
                features[ROOK_HALF_OPEN_FILE_IND] += inc;
            } else {
                features[ROOK_OPEN_FILE_IND] += inc;
            }
        }

    }

}
