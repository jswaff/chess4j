package com.jamesswafford.chess4j.tuner;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.squares.Square;
import com.jamesswafford.chess4j.pieces.*;
import org.ejml.simple.SimpleMatrix;

public class BoardToNetwork {

    public static double[] transform(Board board) {
        double[] data = new double[837];

        for (int i=0;i<64;i++) {
            Piece p = board.getPiece(i);
            if (Rook.WHITE_ROOK.equals(p)) data[i] = 1;
            else if (Rook.BLACK_ROOK.equals(p)) data[64+i] = 1;
            else if (Knight.WHITE_KNIGHT.equals(p)) data[128+i] = 1;
            else if (Knight.BLACK_KNIGHT.equals(p)) data[192+i] = 1;
            else if (Bishop.WHITE_BISHOP.equals(p)) data[256+i] = 1;
            else if (Bishop.BLACK_BISHOP.equals(p)) data[320+i] = 1;
            else if (Queen.WHITE_QUEEN.equals(p)) data[384+i] = 1;
            else if (Queen.BLACK_QUEEN.equals(p)) data[448+i] = 1;
            else if (King.WHITE_KING.equals(p)) data[512+i] = 1;
            else if (King.BLACK_KING.equals(p)) data[576+i] = 1;
            else if (Pawn.WHITE_PAWN.equals(p)) data[640+i] = 1;
            else if (Pawn.BLACK_PAWN.equals(p)) data[704+i] = 1;

            if (Square.valueOf(i).equals(board.getEPSquare())) data[768+i] = 1;
        }

        if (board.hasBQCastlingRight()) data[832] = 1;
        if (board.hasBKCastlingRight()) data[833] = 1;
        if (board.hasWQCastlingRight()) data[834] = 1;
        if (board.hasWKCastlingRight()) data[835] = 1;

        if (board.getPlayerToMove().isWhite()) data[836] = 1;

        return data;
    }
    public static SimpleMatrix transformToMatrix(Board board) {
        double[] data = transform(board);
        return new SimpleMatrix(data.length, 1, true, data);
    }

}
