package com.jamesswafford.chess4j.eval;

import com.jamesswafford.chess4j.board.Bitboard;
import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Color;
import com.jamesswafford.chess4j.board.squares.East;
import com.jamesswafford.chess4j.board.squares.Square;
import com.jamesswafford.chess4j.movegen.Magic;

import static com.jamesswafford.chess4j.board.squares.Rank.*;

import static com.jamesswafford.chess4j.eval.EvalWeights.*;

public class EvalMajorOn7th {

    public static int evalMajorOn7th(EvalWeights weights, Board board, boolean isWhite, Square sq) {
        int score = 0;

        if (isWhite) {
            if (sq.rank() == RANK_7 && board.getKingSquare(Color.BLACK).rank() == RANK_8) {
                score += weights.vals[MAJOR_ON_7TH_IND];
                score += evalConnectedMajorOn7th(weights, board, true, sq);
            }
        } else {
            if (sq.rank() == RANK_2 && board.getKingSquare(Color.WHITE).rank() == RANK_1) {
                score += weights.vals[MAJOR_ON_7TH_IND];
                score += evalConnectedMajorOn7th(weights, board,false, sq);
            }
        }

        return score;
    }

    private static int evalConnectedMajorOn7th(EvalWeights weights, Board board, boolean isWhite, Square sq) {
        int score = 0;

        long rookMoves = Magic.getRookMoves(board,sq.value(),
                Bitboard.rays[sq.value()][East.getInstance().value()]);

        if (isWhite) {
            if ((rookMoves & (board.getWhiteRooks() | board.getWhiteQueens())) != 0) {
                score += weights.vals[CONNECTED_MAJORS_ON_7TH_IND];
            }
        } else {
            if ((rookMoves & (board.getBlackRooks() | board.getBlackQueens())) != 0) {
                score += weights.vals[CONNECTED_MAJORS_ON_7TH_IND];
            }
        }

        return score;
    }

    public static void exractMajorOn7thFeatures(double[] features, Board board, boolean isWhite, Square sq) {
        if (isWhite) {
            if (sq.rank() == RANK_7 && board.getKingSquare(Color.BLACK).rank() == RANK_8) {
                features[MAJOR_ON_7TH_IND]++;
                extractConnectedMajorOn7thFeatures(features, board, true, sq);
            }
        } else {
            if (sq.rank() == RANK_2 && board.getKingSquare(Color.WHITE).rank() == RANK_1) {
                features[MAJOR_ON_7TH_IND]--;
                extractConnectedMajorOn7thFeatures(features, board, false, sq);
            }
        }
    }

    public static void extractConnectedMajorOn7thFeatures(double[] features, Board board, boolean isWhite, Square sq) {
        long rookMoves = Magic.getRookMoves(board,sq.value(),
                Bitboard.rays[sq.value()][East.getInstance().value()]);

        if (isWhite) {
            if ((rookMoves & (board.getWhiteRooks() | board.getWhiteQueens())) != 0) {
                features[CONNECTED_MAJORS_ON_7TH_IND]++;
            }
        } else {
            if ((rookMoves & (board.getBlackRooks() | board.getBlackQueens())) != 0) {
                features[CONNECTED_MAJORS_ON_7TH_IND]--;
            }
        }
    }

}
