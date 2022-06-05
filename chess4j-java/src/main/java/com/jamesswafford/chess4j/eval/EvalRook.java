package com.jamesswafford.chess4j.eval;

import com.jamesswafford.chess4j.board.Bitboard;
import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.squares.Square;
import io.vavr.Tuple2;

import static com.jamesswafford.chess4j.eval.EvalMajorOn7th.*;

import static com.jamesswafford.chess4j.eval.EvalWeights.*;

public class EvalRook {

    public static Tuple2<Integer, Integer> evalRook(EvalWeights weights, Board board, Square sq) {
        boolean isWhite = board.getPiece(sq).isWhite();

        int mg, eg;

        Tuple2<Integer, Integer> major7th = evalMajorOn7th(weights, board, isWhite, sq);
        Tuple2<Integer, Integer> rookOpen = evalRookOpenFile(weights, board, isWhite, sq);

        if (isWhite) {
            mg = weights.vals[ROOK_PST_IND + sq.value()] + major7th._1 + rookOpen._1;
            eg = weights.vals[ROOK_ENDGAME_PST_IND + sq.value()] + major7th._2 + rookOpen._2;
        } else {
            mg = -(weights.vals[ROOK_PST_IND + sq.flipVertical().value()] + major7th._1 + rookOpen._1);
            eg = -(weights.vals[ROOK_ENDGAME_PST_IND + sq.flipVertical().value()] + major7th._2 + rookOpen._2);
        }

        return new Tuple2<>(mg, eg);
    }

    private static Tuple2<Integer, Integer> evalRookOpenFile(EvalWeights weights, Board board, boolean isWhite, Square sq) {
        int mg = 0, eg = 0;

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
                mg += weights.vals[ROOK_HALF_OPEN_FILE_IND];
                eg += weights.vals[ROOK_HALF_OPEN_FILE_ENDGAME_IND];
            } else {
                mg += weights.vals[ROOK_OPEN_FILE_IND];
                eg += weights.vals[ROOK_OPEN_FILE_ENDGAME_IND];
            }
        }

        return new Tuple2<>(mg, eg);
    }

    public static java.lang.Void extractRookFeatures(double[] features, Board board, Square sq, double phase) {
        boolean isWhite = board.getPiece(sq).isWhite();
        if (isWhite) {
            features[ROOK_ENDGAME_PST_IND + sq.value()] += (1-phase);
            features[ROOK_PST_IND + sq.value()] += phase;
        } else {
            features[ROOK_ENDGAME_PST_IND + sq.flipVertical().value()] -= (1-phase);
            features[ROOK_PST_IND + sq.flipVertical().value()] -= phase;
        }

        exractMajorOn7thFeatures(features, board, isWhite, sq, phase);
        extractRookFeatures_OpenFile(features, board, isWhite, sq, phase);

        return null;
    }

    private static void extractRookFeatures_OpenFile(double[] features, Board board, boolean isWhite, Square sq, double phase) {
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
                if (isWhite) {
                    features[ROOK_HALF_OPEN_FILE_IND] += phase;
                    features[ROOK_HALF_OPEN_FILE_ENDGAME_IND] += (1-phase);
                } else {
                    features[ROOK_HALF_OPEN_FILE_IND] -= phase;
                    features[ROOK_HALF_OPEN_FILE_ENDGAME_IND] -= (1-phase);
                }
            } else {
                if (isWhite) {
                    features[ROOK_OPEN_FILE_IND] += phase;
                    features[ROOK_OPEN_FILE_ENDGAME_IND] += (1-phase);
                } else {
                    features[ROOK_OPEN_FILE_IND] -= phase;
                    features[ROOK_OPEN_FILE_ENDGAME_IND] -= (1-phase);
                }
            }
        }
    }
}
