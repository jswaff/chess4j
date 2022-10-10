package com.jamesswafford.chess4j.eval;

import com.jamesswafford.chess4j.board.Bitboard;
import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Color;
import com.jamesswafford.chess4j.board.squares.Square;
import com.jamesswafford.chess4j.movegen.Magic;
import io.vavr.Tuple2;

import static com.jamesswafford.chess4j.board.squares.Rank.*;

import static com.jamesswafford.chess4j.eval.EvalWeights.*;

public class EvalMajorOn7th {

    public static Tuple2<Integer, Integer> evalMajorOn7th(EvalWeights weights, Board board, boolean isWhite, Square sq) {

        int mg = 0, eg = 0;

        if (isWhite) {
            if (sq.rank() == RANK_7 && board.getKingSquare(Color.BLACK).rank() == RANK_8) {
                mg += weights.vals[MAJOR_ON_7TH_MG_IND];
                eg += weights.vals[MAJOR_ON_7TH_EG_IND];
                Tuple2<Integer, Integer> m7 = evalConnectedMajorOn7th(weights, board, true, sq);
                mg += m7._1;
                eg += m7._2;
            }
        } else {
            if (sq.rank() == RANK_2 && board.getKingSquare(Color.WHITE).rank() == RANK_1) {
                mg += weights.vals[MAJOR_ON_7TH_MG_IND];
                eg += weights.vals[MAJOR_ON_7TH_EG_IND];
                Tuple2<Integer, Integer> m7 = evalConnectedMajorOn7th(weights, board,false, sq);
                mg += m7._1;
                eg += m7._2;
            }
        }

        return new Tuple2<>(mg, eg);
    }

    private static Tuple2<Integer, Integer> evalConnectedMajorOn7th(EvalWeights weights, Board board, boolean isWhite, Square sq) {
        int mg = 0, eg = 0;

        long rookMoves = Magic.getRookMoves(board,sq.value(), Bitboard.ranks[sq.rank().getValue()]);

        if (isWhite) {
            if ((rookMoves & (board.getWhiteRooks() | board.getWhiteQueens())) != 0) {
                mg += weights.vals[CONNECTED_MAJORS_ON_7TH_MG_IND];
                eg += weights.vals[CONNECTED_MAJORS_ON_7TH_EG_IND];
            }
        } else {
            if ((rookMoves & (board.getBlackRooks() | board.getBlackQueens())) != 0) {
                mg += weights.vals[CONNECTED_MAJORS_ON_7TH_MG_IND];
                eg += weights.vals[CONNECTED_MAJORS_ON_7TH_EG_IND];
            }
        }

        return new Tuple2<>(mg, eg);
    }

    public static void exractMajorOn7thFeatures(double[] features, Board board, boolean isWhite, Square sq, double phase) {
        if (isWhite) {
            if (sq.rank() == RANK_7 && board.getKingSquare(Color.BLACK).rank() == RANK_8) {
                features[MAJOR_ON_7TH_MG_IND] += phase;
                features[MAJOR_ON_7TH_EG_IND] += (1-phase);
                extractConnectedMajorOn7thFeatures(features, board, true, sq, phase);
            }
        } else {
            if (sq.rank() == RANK_2 && board.getKingSquare(Color.WHITE).rank() == RANK_1) {
                features[MAJOR_ON_7TH_MG_IND] -= phase;
                features[MAJOR_ON_7TH_EG_IND] -= (1-phase);
                extractConnectedMajorOn7thFeatures(features, board, false, sq, phase);
            }
        }
    }

    public static void extractConnectedMajorOn7thFeatures(double[] features, Board board, boolean isWhite, Square sq, double phase) {
        long rookMoves = Magic.getRookMoves(board,sq.value(), Bitboard.ranks[sq.rank().getValue()]);

        if (isWhite) {
            if ((rookMoves & (board.getWhiteRooks() | board.getWhiteQueens())) != 0) {
                features[CONNECTED_MAJORS_ON_7TH_MG_IND] += phase;
                features[CONNECTED_MAJORS_ON_7TH_EG_IND] += (1-phase);
            }
        } else {
            if ((rookMoves & (board.getBlackRooks() | board.getBlackQueens())) != 0) {
                features[CONNECTED_MAJORS_ON_7TH_MG_IND] -= phase;
                features[CONNECTED_MAJORS_ON_7TH_EG_IND] -= (1-phase);
            }
        }
    }

}
