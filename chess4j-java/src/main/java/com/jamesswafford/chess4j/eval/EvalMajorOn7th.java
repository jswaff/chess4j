package com.jamesswafford.chess4j.eval;

import com.jamesswafford.chess4j.Color;
import com.jamesswafford.chess4j.board.Bitboard;
import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.squares.East;
import com.jamesswafford.chess4j.board.squares.Rank;
import com.jamesswafford.chess4j.board.squares.Square;
import com.jamesswafford.chess4j.movegen.Magic;

import static com.jamesswafford.chess4j.board.squares.Rank.*;

public class EvalMajorOn7th {

    // having majors on the 7th is huge advantage.  This might actually be too small.
    public static final int MAJOR_ON_7TH = 50;
    public static final int CONNECTED_MAJORS_ON_7TH = 80;

    public static int evalMajorOn7th(Board board, boolean isWhite, Square sq) {
        int score = 0;

        if (isWhite) {
            if (sq.rank() == RANK_7 && board.getKingSquare(Color.BLACK).rank() == RANK_8) {
                score += MAJOR_ON_7TH;
                score += evalConnectedMajorOn7th(board, isWhite, sq);
            }
        } else {
            if (sq.rank() == RANK_2 && board.getKingSquare(Color.WHITE).rank() == RANK_1) {
                score += MAJOR_ON_7TH;
                score += evalConnectedMajorOn7th(board,isWhite,sq);
            }
        }

        return score;
    }

    private static int evalConnectedMajorOn7th(Board board, boolean isWhite, Square sq) {
        int score = 0;

        long rookMoves = Magic.getRookMoves(board,sq.value(),
                Bitboard.rays[sq.value()][East.getInstance().value()]);

        if (isWhite) {
            if ((rookMoves & (board.getWhiteRooks() | board.getWhiteQueens())) != 0) {
                score += CONNECTED_MAJORS_ON_7TH;
            }
        } else {
            if ((rookMoves & (board.getBlackRooks() | board.getBlackQueens())) != 0) {
                score += CONNECTED_MAJORS_ON_7TH;
            }
        }

        return score;
    }

}
