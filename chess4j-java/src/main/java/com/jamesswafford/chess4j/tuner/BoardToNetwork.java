package com.jamesswafford.chess4j.tuner;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.pieces.*;

public class BoardToNetwork {

    public static final int NUM_INPUTS = 10;

    public static double[][] transform(Board board) {
        double[][] data = new double[NUM_INPUTS][1];

        for (int i=0;i<64;i++) {
            Piece p = board.getPiece(i);
            if (Rook.WHITE_ROOK.equals(p)) data[0][0]++;
            else if (Rook.BLACK_ROOK.equals(p)) data[1][0]++;
            else if (Knight.WHITE_KNIGHT.equals(p)) data[2][0]++;
            else if (Knight.BLACK_KNIGHT.equals(p)) data[3][0]++;
            else if (Bishop.WHITE_BISHOP.equals(p)) data[4][0]++;
            else if (Bishop.BLACK_BISHOP.equals(p)) data[5][0]++;
            else if (Queen.WHITE_QUEEN.equals(p)) data[6][0]++;
            else if (Queen.BLACK_QUEEN.equals(p)) data[7][0]++;
            else if (Pawn.WHITE_PAWN.equals(p)) data[8][0]++;
            else if (Pawn.BLACK_PAWN.equals(p)) data[9][0]++;
        }

        return data;
    }

}
