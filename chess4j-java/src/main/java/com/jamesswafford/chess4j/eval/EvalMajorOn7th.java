package com.jamesswafford.chess4j.eval;

import com.jamesswafford.chess4j.board.Bitboard;
import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Color;
import com.jamesswafford.chess4j.board.squares.East;
import com.jamesswafford.chess4j.board.squares.Square;
import com.jamesswafford.chess4j.movegen.Magic;

import static com.jamesswafford.chess4j.board.squares.Rank.*;

import static com.jamesswafford.chess4j.eval.EvalWeightsVector.*;

public class EvalMajorOn7th {

    public static int evalMajorOn7th(EvalWeightsVector weights, Board board, boolean isWhite, Square sq) {
        int score = 0;

        if (isWhite) {
            if (sq.rank() == RANK_7 && board.getKingSquare(Color.BLACK).rank() == RANK_8) {
                score += weights.weights[MAJOR_ON_7TH_IND];
                score += evalConnectedMajorOn7th(weights, board, true, sq);
            }
        } else {
            if (sq.rank() == RANK_2 && board.getKingSquare(Color.WHITE).rank() == RANK_1) {
                score += weights.weights[MAJOR_ON_7TH_IND];
                score += evalConnectedMajorOn7th(weights, board,false, sq);
            }
        }

        return score;
    }

    private static int evalConnectedMajorOn7th(EvalWeightsVector etv, Board board, boolean isWhite, Square sq) {
        int score = 0;

        long rookMoves = Magic.getRookMoves(board,sq.value(),
                Bitboard.rays[sq.value()][East.getInstance().value()]);

        if (isWhite) {
            if ((rookMoves & (board.getWhiteRooks() | board.getWhiteQueens())) != 0) {
                score += etv.weights[CONNECTED_MAJORS_ON_7TH_IND];
            }
        } else {
            if ((rookMoves & (board.getBlackRooks() | board.getBlackQueens())) != 0) {
                score += etv.weights[CONNECTED_MAJORS_ON_7TH_IND];
            }
        }

        return score;
    }

}
