package com.jamesswafford.chess4j.eval;

import com.jamesswafford.chess4j.board.Color;
import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.squares.Square;

public class EvalKnight {

    public static final int[] KNIGHT_PST = {
            -5, -5, -5, -5, -5, -5, -5, -5,
            -5,  0, 10, 10, 10, 10,  0, -5,
            -5,  0, 15, 20, 20, 15,  0, -5,
            -5,  5, 10, 15, 15, 10,  5, -5,
            -5,  0, 10, 15, 15, 10,  5, -5,
            -5,  0,  8,  0,  0,  8,  0, -5,
            -5,  0,  0,  5,  5,  0,  0, -5,
            -10,-10,-5, -5, -5, -5,-10,-10 };

    public static final int KNIGHT_TROPISM = -2;

    public static int evalKnight(Board board, Square sq) {
        int score = 0;

        if (board.getPiece(sq).isWhite()) {
            score = KNIGHT_PST[sq.value()];
            score += KNIGHT_TROPISM * sq.distance(board.getKingSquare(Color.BLACK));
        } else {
            score = KNIGHT_PST[sq.flipVertical().value()];
            score += KNIGHT_TROPISM * sq.distance(board.getKingSquare(Color.WHITE));
        }

        return score;
    }


}
