package com.jamesswafford.chess4j.tuner;

import com.jamesswafford.chess4j.board.Bitboard;
import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Color;
import org.ejml.simple.SimpleMatrix;

import static com.jamesswafford.chess4j.board.Bitboard.ALL_SQUARES;

public class BoardToNetwork {

    public static final int NUM_INPUTS = 19;

    public static double[] transform(Board board) {
        double[] data = new double[NUM_INPUTS];

        // six planes to encode the positions of the pieces of the white player
        data[0] = board.getWhitePawns();
        data[1] = board.getWhiteRooks();
        data[2] = board.getWhiteKnights();
        data[3] = board.getWhiteBishops();
        data[4] = board.getWhiteQueens();
        data[5] = Bitboard.toBitboard(board.getKingSquare(Color.WHITE));

        // six planes to encode the positions of the pieces of the black player
        data[6] = board.getBlackPawns();
        data[7] = board.getBlackRooks();
        data[8] = board.getBlackKnights();
        data[9] = board.getBlackBishops();
        data[10] = board.getBlackQueens();
        data[11] = Bitboard.toBitboard(board.getKingSquare(Color.BLACK));

        // one plane to encode the color of the player
        data[12] = board.getPlayerToMove().isWhite() ? ALL_SQUARES : 0;

        // four planes for castling rights
        data[13] = board.hasWKCastlingRight() ? ALL_SQUARES : 0;
        data[14] = board.hasWQCastlingRight() ? ALL_SQUARES : 0;
        data[15] = board.hasBKCastlingRight() ? ALL_SQUARES : 0;
        data[16] = board.hasBQCastlingRight() ? ALL_SQUARES : 0;

        // move count as a number
        data[17] = board.getMoveCounter();

        // fifty move counter as a number
        data[18] = board.getFiftyCounter();

        return data;
    }

    public static SimpleMatrix transformToMatrix(Board board) {
        double[] data = transform(board);
        return new SimpleMatrix(data.length, 1, true, data);
    }

}
