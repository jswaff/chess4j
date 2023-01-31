package com.jamesswafford.chess4j.tuner;

import com.jamesswafford.chess4j.board.Bitboard;
import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Color;
import org.ejml.simple.SimpleMatrix;

import static com.jamesswafford.chess4j.board.Bitboard.LOWER16;

public class BoardToNetwork {

    public static final int NUM_INPUTS = 55;

    public static double[] transform(Board board) {
        double[] data = new double[NUM_INPUTS];

        // six planes to encode the positions of the pieces of the white player
        data[0] = board.getWhitePawns() & LOWER16;
        data[1] = board.getWhitePawns() >> 16 & LOWER16;
        data[2] = board.getWhitePawns() >> 32 & LOWER16;
        data[3] = board.getWhitePawns() >> 48 & LOWER16;

        data[4] = board.getWhiteRooks() & LOWER16;
        data[5] = board.getWhiteRooks() >> 16 & LOWER16;
        data[6] = board.getWhiteRooks() >> 32 & LOWER16;
        data[7] = board.getWhiteRooks() >> 48 & LOWER16;

        data[8] = board.getWhiteKnights() & LOWER16;
        data[9] = board.getWhiteKnights() >> 16 & LOWER16;
        data[10] = board.getWhiteKnights() >> 32 & LOWER16;
        data[11] = board.getWhiteKnights() >> 48 & LOWER16;

        data[12] = board.getWhiteBishops() & LOWER16;
        data[13] = board.getWhiteBishops() >> 16 & LOWER16;
        data[14] = board.getWhiteBishops() >> 32 & LOWER16;
        data[15] = board.getWhiteBishops() >> 48 & LOWER16;

        data[16] = board.getWhiteQueens() & LOWER16;
        data[17] = board.getWhiteQueens() >> 16 & LOWER16;
        data[18] = board.getWhiteQueens() >> 32 & LOWER16;
        data[19] = board.getWhiteQueens() >> 48 & LOWER16;

        long wKingBB = Bitboard.toBitboard(board.getKingSquare(Color.WHITE));
        data[20] = wKingBB & LOWER16;
        data[21] = wKingBB >> 16 & LOWER16;
        data[22] = wKingBB >> 32 & LOWER16;
        data[23] = wKingBB >> 48 & LOWER16;

        // six planes to encode the positions of the pieces of the black player
        data[24] = board.getBlackPawns() & LOWER16;
        data[25] = board.getBlackPawns() >> 16 & LOWER16;
        data[26] = board.getBlackPawns() >> 32 & LOWER16;
        data[27] = board.getBlackPawns() >> 48 & LOWER16;

        data[28] = board.getBlackRooks() & LOWER16;
        data[29] = board.getBlackRooks() >> 16 & LOWER16;
        data[30] = board.getBlackRooks() >> 32 & LOWER16;
        data[31] = board.getBlackRooks() >> 48 & LOWER16;

        data[32] = board.getBlackKnights() & LOWER16;
        data[33] = board.getBlackKnights() >> 16 & LOWER16;
        data[34] = board.getBlackKnights() >> 32 & LOWER16;
        data[35] = board.getBlackKnights() >> 48 & LOWER16;

        data[36] = board.getBlackBishops() & LOWER16;
        data[37] = board.getBlackBishops() >> 16 & LOWER16;
        data[38] = board.getBlackBishops() >> 32 & LOWER16;
        data[39] = board.getBlackBishops() >> 48 & LOWER16;

        data[40] = board.getBlackQueens() & LOWER16;
        data[41] = board.getBlackQueens() >> 16 & LOWER16;
        data[42] = board.getBlackQueens() >> 32 & LOWER16;
        data[43] = board.getBlackQueens() >> 48 & LOWER16;

        long bKingBB = Bitboard.toBitboard(board.getKingSquare(Color.BLACK));
        data[44] = bKingBB & LOWER16;
        data[45] = bKingBB >> 16 & LOWER16;
        data[46] = bKingBB >> 32 & LOWER16;
        data[47] = bKingBB >> 48 & LOWER16;

        // one plane to encode the color of the player
        data[48] = board.getPlayerToMove().isWhite() ? LOWER16 : 0;

        // four planes for castling rights
        data[49] = board.hasWKCastlingRight() ? LOWER16 : 0;
        data[50] = board.hasWQCastlingRight() ? LOWER16 : 0;
        data[51] = board.hasBKCastlingRight() ? LOWER16 : 0;
        data[52] = board.hasBQCastlingRight() ? LOWER16 : 0;

        // move count as a number
        data[53] = board.getMoveCounter();

        // fifty move counter as a number
        data[54] = board.getFiftyCounter();

        return data;
    }

    public static SimpleMatrix transformToMatrix(Board board) {
        double[] data = transform(board);
        return new SimpleMatrix(data.length, 1, true, data);
    }

}
