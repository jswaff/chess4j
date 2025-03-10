package dev.jamesswafford.chess4j.nn;

import dev.jamesswafford.chess4j.board.Board;
import dev.jamesswafford.chess4j.pieces.*;
import io.vavr.Tuple2;

import java.util.Arrays;

import static dev.jamesswafford.chess4j.nn.NeuralNetwork.NN_SIZE_L1;
import static dev.jamesswafford.chess4j.pieces.Bishop.*;
import static dev.jamesswafford.chess4j.pieces.King.*;
import static dev.jamesswafford.chess4j.pieces.Knight.*;
import static dev.jamesswafford.chess4j.pieces.Queen.*;
import static dev.jamesswafford.chess4j.pieces.Rook.*;

public class NnueAccumulators {

    private final int[][] accumulators = new int[2][NN_SIZE_L1];

    public int get(int ind1, int ind2) {
        return accumulators[ind1][ind2];
    }

    public void addPiece(Piece piece, int sq, NeuralNetwork nn) {

        Tuple2<Integer, Integer> ind = getIndexes(piece);

        int feature_w = (64 * ind._1) + sq;
        int feature_b = (64 * ind._2) + (sq ^ 56);

        for (int i=0;i<NN_SIZE_L1;i++) {
            accumulators[0][i] += nn.W0[NN_SIZE_L1 * feature_w + i];
            accumulators[1][i] += nn.W0[NN_SIZE_L1 * feature_b + i];
        }
    }

    public void movePiece(Piece piece, int fromsq, int tosq, NeuralNetwork nn) {

        Tuple2<Integer, Integer> ind = getIndexes(piece);

        int from_feature_w = (64 * ind._1) + fromsq;
        int to_feature_w = (64 * ind._1) + tosq;

        int from_feature_b = (64 * ind._2) + (fromsq ^ 56);
        int to_feature_b = (64 * ind._2) + (tosq ^ 56);

        for (int i=0;i<NN_SIZE_L1;i++) {
            accumulators[0][i] -= nn.W0[NN_SIZE_L1 * from_feature_w + i];
            accumulators[0][i] += nn.W0[NN_SIZE_L1 * to_feature_w + i];
            accumulators[1][i] -= nn.W0[NN_SIZE_L1 * from_feature_b + i];
            accumulators[1][i] += nn.W0[NN_SIZE_L1 * to_feature_b + i];
        }
    }

    public void removePiece(Piece piece, int sq, NeuralNetwork nn) {

        Tuple2<Integer, Integer> ind = getIndexes(piece);

        int feature_w = (64 * ind._1) + sq;
        int feature_b = (64 * ind._2) + (sq ^ 56);

        for (int i=0;i<NN_SIZE_L1;i++) {
            accumulators[0][i] -= nn.W0[NN_SIZE_L1 * feature_w + i];
            accumulators[1][i] -= nn.W0[NN_SIZE_L1 * feature_b + i];
        }
    }

    private Tuple2<Integer, Integer> getIndexes(Piece piece) {
        int pieceType = getPieceType(piece);
        int pieceColor = piece.isWhite() ? 0 : 1;

        int index_w = pieceType * 2 + pieceColor;
        int index_b = pieceType * 2 + (1 - pieceColor);

        return new Tuple2<>(index_w, index_b);
    }

    private int getPieceType(Piece piece) {
        if (piece.isWhite()) {
            if (piece.equals(WHITE_ROOK)) {
                return 0;
            } else if (piece.equals(WHITE_KNIGHT)) {
                return 1;
            } else if (piece.equals(WHITE_BISHOP)) {
                return 2;
            } else if (piece.equals(WHITE_QUEEN)) {
                return 3;
            } else if (piece.equals(WHITE_KING)) {
                return 4;
            } else {
                return 5; // pawn
            }
        } else {
            if (piece.equals(BLACK_ROOK)) {
                return 0;
            } else if (piece.equals(BLACK_KNIGHT)) {
                return 1;
            } else if (piece.equals(BLACK_BISHOP)) {
                return 2;
            } else if (piece.equals(BLACK_QUEEN)) {
                return 3;
            } else if (piece.equals(BLACK_KING)) {
                return 4;
            } else {
                return 5; // pawn
            }
        }
    }

    public void populate(Board board, NeuralNetwork nn) {

        // initialize with bias term
        for (int o=0;o<NN_SIZE_L1;o++) {
            accumulators[0][o] = nn.B0[o];
            accumulators[1][o] = nn.B0[o];
        }

        for (int sq=0;sq<64;sq++) {
            Piece piece = board.getPiece(sq);
            if (piece != null) {
                addPiece(piece, sq, nn);
            }
        }
    }

    public void copy(NnueAccumulators other) {
        for (int i=0; i<accumulators.length;i++) {
            System.arraycopy(other.accumulators[i], 0, accumulators[i], 0, accumulators[0].length);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof NnueAccumulators)) return false;
        NnueAccumulators that = (NnueAccumulators) obj;

        for (int i=0; i<accumulators.length;i++) {
            for (int j=0;j<accumulators[0].length;j++) {
                if (accumulators[i][j] != that.accumulators[i][j]) return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(accumulators);
    }
}
