package dev.jamesswafford.chess4j.nn;

import dev.jamesswafford.chess4j.board.Board;
import dev.jamesswafford.chess4j.pieces.*;

import static dev.jamesswafford.chess4j.nn.NeuralNetwork.NN_SIZE_L1;
import static dev.jamesswafford.chess4j.pieces.Bishop.*;
import static dev.jamesswafford.chess4j.pieces.King.*;
import static dev.jamesswafford.chess4j.pieces.Knight.*;
import static dev.jamesswafford.chess4j.pieces.Queen.*;
import static dev.jamesswafford.chess4j.pieces.Rook.*;

public class NnueAccumulators {

    private final double[][] accumulators = new double[2][NN_SIZE_L1];

    private final static double epsilon = 0.00001;

    public double get(int ind1, int ind2) {
        return accumulators[ind1][ind2];
    }

    // TODO: probably some opportunity to refactor
    public void addPiece(Board board, int sq, NeuralNetwork nn) {
        Piece piece = board.getPiece(sq);

        int pieceColor, pieceType;
        if (piece.isWhite()) {
            pieceColor = 0;
            if (piece.equals(WHITE_ROOK)) {
                pieceType = 0;
            } else if (piece.equals(WHITE_KNIGHT)) {
                pieceType = 1;
            } else if (piece.equals(WHITE_BISHOP)) {
                pieceType = 2;
            } else if (piece.equals(WHITE_QUEEN)) {
                pieceType = 3;
            } else if (piece.equals(WHITE_KING)) {
                pieceType = 4;
            } else {
                pieceType = 5; // pawn
            }
        } else {
            pieceColor = 1;
            if (piece.equals(BLACK_ROOK)) {
                pieceType = 0;
            } else if (piece.equals(BLACK_KNIGHT)) {
                pieceType = 1;
            } else if (piece.equals(BLACK_BISHOP)) {
                pieceType = 2;
            } else if (piece.equals(BLACK_QUEEN)) {
                pieceType = 3;
            } else if (piece.equals(BLACK_KING)) {
                pieceType = 4;
            } else {
                pieceType = 5; // pawn
            }
        }

        int index_w = pieceType * 2 + pieceColor;
        int feature_w = (64 * index_w) + sq;

        int index_b = pieceType * 2 + (1 - pieceColor);
        int feature_b = (64 * index_b) + (sq ^ 56);

        for (int o=0;o<NN_SIZE_L1;o++) {
            accumulators[0][o] += nn.W0[NN_SIZE_L1 * feature_w + o];
            accumulators[1][o] += nn.W0[NN_SIZE_L1 * feature_b + o];
        }
    }

    public void removePiece(Board board, int sq, NeuralNetwork nn) {
        Piece piece = board.getPiece(sq);

        int pieceColor, pieceType;
        if (piece.isWhite()) {
            pieceColor = 0;
            if (piece.equals(WHITE_ROOK)) {
                pieceType = 0;
            } else if (piece.equals(WHITE_KNIGHT)) {
                pieceType = 1;
            } else if (piece.equals(WHITE_BISHOP)) {
                pieceType = 2;
            } else if (piece.equals(WHITE_QUEEN)) {
                pieceType = 3;
            } else if (piece.equals(WHITE_KING)) {
                pieceType = 4;
            } else {
                pieceType = 5; // pawn
            }
        } else {
            pieceColor = 1;
            if (piece.equals(BLACK_ROOK)) {
                pieceType = 0;
            } else if (piece.equals(BLACK_KNIGHT)) {
                pieceType = 1;
            } else if (piece.equals(BLACK_BISHOP)) {
                pieceType = 2;
            } else if (piece.equals(BLACK_QUEEN)) {
                pieceType = 3;
            } else if (piece.equals(BLACK_KING)) {
                pieceType = 4;
            } else {
                pieceType = 5; // pawn
            }
        }

        int index_w = pieceType * 2 + pieceColor;
        int feature_w = (64 * index_w) + sq;

        int index_b = pieceType * 2 + (1 - pieceColor);
        int feature_b = (64 * index_b) + (sq ^ 56);

        for (int o=0;o<NN_SIZE_L1;o++) {
            accumulators[0][o] -= nn.W0[NN_SIZE_L1 * feature_w + o];
            accumulators[1][o] -= nn.W0[NN_SIZE_L1 * feature_b + o];
        }
    }

    public void populate(Board board, NeuralNetwork nn) {

        // initialize with bias term
        for (int o=0;o<NN_SIZE_L1;o++) {
            accumulators[0][o] = nn.B0[o];
            accumulators[1][o] = nn.B0[o];
        }

        for (int sq=0;sq<64;sq++) {
            if (board.getPiece(sq) != null) {
                addPiece(board, sq, nn);
            }
        }
    }

    public boolean equalsWithinEpsilon(NnueAccumulators that) {
        for (int i=0; i<accumulators.length;i++) {
            for (int j=0;j<accumulators[0].length;j++) {
                if (Math.abs(accumulators[i][j] - that.accumulators[i][j]) > epsilon) return false;
            }
        }
        return true;
    }
}
