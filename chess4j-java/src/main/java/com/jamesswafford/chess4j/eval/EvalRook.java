package com.jamesswafford.chess4j.eval;

import com.jamesswafford.chess4j.board.Bitboard;
import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.squares.Square;
import com.jamesswafford.chess4j.movegen.Magic;
import com.jamesswafford.chess4j.movegen.Mobility;

import io.vavr.Tuple2;

import static com.jamesswafford.chess4j.eval.EvalMajorOn7th.*;

import static com.jamesswafford.chess4j.eval.EvalWeights.*;

public class EvalRook {

    public static Tuple2<Integer, Integer> evalRook(EvalWeights weights, Board board, Square sq) {
        boolean isWhite = board.getPiece(sq).isWhite();

        int mg, eg;

        int mobility = Mobility.rookMobility(board, sq);
        int mobilityMg = weights.vals[ROOK_MOBILITY_MG_IND + mobility];
        int mobilityEg = weights.vals[ROOK_MOBILITY_EG_IND + mobility];

        Tuple2<Integer, Integer> major7th = evalMajorOn7th(weights, board, isWhite, sq);
        Tuple2<Integer, Integer> rookOpen = evalRookOpenFile(weights, board, isWhite, sq);

        if (isWhite) {
            mg = weights.vals[ROOK_PST_MG_IND + sq.value()] + major7th._1 + rookOpen._1 + mobilityMg;
            eg = weights.vals[ROOK_PST_EG_IND + sq.value()] + major7th._2 + rookOpen._2 + mobilityEg;
        } else {
            mg = -(weights.vals[ROOK_PST_MG_IND + sq.flipVertical().value()] + major7th._1 + rookOpen._1 + mobilityMg);
            eg = -(weights.vals[ROOK_PST_EG_IND + sq.flipVertical().value()] + major7th._2 + rookOpen._2 + mobilityEg);
        }

        return new Tuple2<>(mg, eg);
    }

    private static Tuple2<Integer, Integer> evalRookOpenFile(EvalWeights weights, Board board, boolean isWhite, Square sq) {
        int mg = 0, eg = 0;

        long friendlyPawns, enemyPawns, friendlyRooks;
        if (isWhite) {
            friendlyPawns = board.getWhitePawns();
            enemyPawns = board.getBlackPawns();
            friendlyRooks = board.getWhiteRooks();
        } else {
            friendlyPawns = board.getBlackPawns();
            enemyPawns = board.getWhitePawns();
            friendlyRooks = board.getBlackRooks();
        }

        long fileMask = Bitboard.files[sq.file().getValue()] ^ Bitboard.squares[sq.value()];
        if ((fileMask & friendlyPawns) == 0) {
            long rookFileMoves = Magic.getRookMoves(board,sq.value(), Bitboard.files[sq.file().getValue()]);
            if ((fileMask & enemyPawns) != 0) {
                mg += weights.vals[ROOK_HALF_OPEN_FILE_MG_IND];
                eg += weights.vals[ROOK_HALF_OPEN_FILE_EG_IND];
                if ((rookFileMoves & friendlyRooks) != 0) {
                    mg += weights.vals[ROOK_HALF_OPEN_FILE_SUPPORTED_MG_IND];
                    eg += weights.vals[ROOK_HALF_OPEN_FILE_SUPPORTED_EG_IND];
                }
            } else {
                mg += weights.vals[ROOK_OPEN_FILE_MG_IND];
                eg += weights.vals[ROOK_OPEN_FILE_EG_IND];
                if ((rookFileMoves & friendlyRooks) != 0) {
                    mg += weights.vals[ROOK_OPEN_FILE_SUPPORTED_MG_IND];
                    eg += weights.vals[ROOK_OPEN_FILE_SUPPORTED_EG_IND];
                }
            }
        }

        return new Tuple2<>(mg, eg);
    }

    public static java.lang.Void extractRookFeatures(double[] features, Board board, Square sq, double phase) {
        boolean isWhite = board.getPiece(sq).isWhite();

        int mobility = Mobility.rookMobility(board, sq);

        if (isWhite) {
            features[ROOK_PST_EG_IND + sq.value()] += (1-phase);
            features[ROOK_PST_MG_IND + sq.value()] += phase;
            features[ROOK_MOBILITY_MG_IND + mobility] += phase;
            features[ROOK_MOBILITY_EG_IND + mobility] += (1-phase);
        } else {
            features[ROOK_PST_EG_IND + sq.flipVertical().value()] -= (1-phase);
            features[ROOK_PST_MG_IND + sq.flipVertical().value()] -= phase;
            features[ROOK_MOBILITY_MG_IND + mobility] -= phase;
            features[ROOK_MOBILITY_EG_IND + mobility] -= (1- phase);
        }

        exractMajorOn7thFeatures(features, board, isWhite, sq, phase);
        extractRookFeatures_OpenFile(features, board, isWhite, sq, phase);

        return null;
    }

    private static void extractRookFeatures_OpenFile(double[] features, Board board, boolean isWhite, Square sq, double phase) {
        
        long friendlyPawns, enemyPawns, friendlyRooks;
        if (isWhite) {
            friendlyPawns = board.getWhitePawns();
            enemyPawns = board.getBlackPawns();
            friendlyRooks = board.getWhiteRooks();
        } else {
            friendlyPawns = board.getBlackPawns();
            enemyPawns = board.getWhitePawns();
            friendlyRooks = board.getBlackRooks();
        }


        long fileMask = Bitboard.files[sq.file().getValue()] ^ Bitboard.squares[sq.value()];
        long rookFileMoves = Magic.getRookMoves(board,sq.value(), Bitboard.files[sq.file().getValue()]);
        if ((fileMask & friendlyPawns)==0) {
            if ((fileMask & enemyPawns)!=0) {
                if (isWhite) {
                    features[ROOK_HALF_OPEN_FILE_MG_IND] += phase;
                    features[ROOK_HALF_OPEN_FILE_EG_IND] += (1-phase);
                    if ((rookFileMoves & friendlyRooks) != 0) {
                        features[ROOK_HALF_OPEN_FILE_SUPPORTED_MG_IND] += phase;
                        features[ROOK_HALF_OPEN_FILE_SUPPORTED_EG_IND] += (1-phase);
                    }
                } else {
                    features[ROOK_HALF_OPEN_FILE_MG_IND] -= phase;
                    features[ROOK_HALF_OPEN_FILE_EG_IND] -= (1-phase);
                    if ((rookFileMoves & friendlyRooks) != 0) {
                        features[ROOK_HALF_OPEN_FILE_SUPPORTED_MG_IND] -= phase;
                        features[ROOK_HALF_OPEN_FILE_SUPPORTED_EG_IND] -= (1-phase);
                    }
                }
            } else {
                if (isWhite) {
                    features[ROOK_OPEN_FILE_MG_IND] += phase;
                    features[ROOK_OPEN_FILE_EG_IND] += (1-phase);
                    if ((rookFileMoves & friendlyRooks) != 0) {
                        features[ROOK_OPEN_FILE_SUPPORTED_MG_IND] += phase;
                        features[ROOK_OPEN_FILE_SUPPORTED_EG_IND] += (1-phase);
                    }
                } else {
                    features[ROOK_OPEN_FILE_MG_IND] -= phase;
                    features[ROOK_OPEN_FILE_EG_IND] -= (1-phase);
                    if ((rookFileMoves & friendlyRooks) != 0) {
                        features[ROOK_OPEN_FILE_SUPPORTED_MG_IND] -= phase;
                        features[ROOK_OPEN_FILE_SUPPORTED_EG_IND] -= (1-phase);
                    }
                }
            }
        }
    }
}
