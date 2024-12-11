package dev.jamesswafford.chess4j.nn;

import dev.jamesswafford.chess4j.board.Board;
import dev.jamesswafford.chess4j.board.Color;
import dev.jamesswafford.chess4j.board.squares.Square;
import dev.jamesswafford.chess4j.pieces.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class NeuralNetwork {
    public static final int NN_SIZE = 128;

    private double[] W0;
    private double[] B0;
    private double[] W1;
    private double[] B1;
    private double[] W2;
    private double[] B2;
    private double[] W3;
    private double[] B3;

    // Temporary - will be moved into Board
    private double[][] accumulator;

    NeuralNetwork() {
        W0 = new double[40960 * NN_SIZE];
        B0 = new double[NN_SIZE];
        W1 = new double[NN_SIZE*2*32];
        B1 = new double[32];
        W2 = new double[32*32];
        B2 = new double[32];
        W3 = new double[32*1];
        B3 = new double[1];
        accumulator = new double[2][NN_SIZE];
    }

    public void load(String networkFile) {
        try (BufferedReader br = new BufferedReader(new FileReader(networkFile))) {
            for (int i=0;i<W0.length;i++)
                W0[i] = Double.parseDouble(br.readLine());
            for (int i=0;i<B0.length;i++)
                B0[i] = Double.parseDouble(br.readLine());
            for (int i=0;i<W1.length;i++)
                W1[i] = Double.parseDouble(br.readLine());
            for (int i=0;i<B1.length;i++)
                B1[i] = Double.parseDouble(br.readLine());
            for (int i=0;i<W2.length;i++)
                W2[i] = Double.parseDouble(br.readLine());
            for (int i=0;i<B2.length;i++)
                B2[i] = Double.parseDouble(br.readLine());
            for (int i=0;i<W3.length;i++)
                W3[i] = Double.parseDouble(br.readLine());
            for (int i=0;i<B3.length;i++)
                B3[i] = Double.parseDouble(br.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int eval(Board board) {

        int ptm = board.getPlayerToMove().isWhite() ? 0 : 1;

        populateAccumulators(board);

        // layer 0 features
        double[] O0 = new double[NN_SIZE * 2];
        for (int i=0;i<NN_SIZE;i++) {
            O0[i] = clamp(accumulator[ptm][i]);
            O0[NN_SIZE + i] = clamp(accumulator[1-ptm][i]);
        }

        // layers 1-3
        double[] O1 = new double[32];
        double[] O2 = new double[32];
        double[] O3 = new double[1];

        computeLayer(B1, O0, W1, O1, true);
        computeLayer(B2, O1, W2, O2, true);
        computeLayer(B3, O2, W3, O3, false);

        return (int)(O3[0] * 100);
    }

    private double clamp(double val) {
        if (val < 0.0) return 0.0;
        if (val > 1.0) return 1.0;
        return val;
    }

    private void populateAccumulators(Board board) {

        // initialize with bias term
        for (int o=0;o<NN_SIZE;o++) {
            accumulator[0][o] = B0[o];
            accumulator[1][o] = B0[o];
        }

        for (int sq=0;sq<64;sq++) {
            if (board.getPiece(sq) != null) {
                addPiece(board, sq);
            }
        }
    }

    private void addPiece(Board board, int sq) {
        int wKingSq = board.getKingSquare(Color.WHITE).flipVertical().value();
        int bKingSq = board.getKingSquare(Color.BLACK).flipVertical().value();

        Piece piece = board.getPiece(sq);
        int pieceColor = piece.isWhite() ? 0 : 1;

        int pieceType, sq_w, sq_b;
        if (piece.isWhite()) {
            if (piece.equals(Pawn.WHITE_PAWN)) {
                pieceType = 0;
            } else if (piece.equals(Knight.WHITE_KNIGHT)) {
                pieceType = 1;
            } else if (piece.equals(Bishop.WHITE_BISHOP)) {
                pieceType = 2;
            } else if (piece.equals(Rook.WHITE_ROOK)) {
                pieceType = 3;
            } else if (piece.equals(Queen.WHITE_QUEEN)) {
                pieceType = 4;
            } else {
                return;
            }
            sq_w = sq ^ 63;
            sq_b = sq;
        } else {
            if (piece.equals(Pawn.BLACK_PAWN)) {
                pieceType = 0;
            } else if (piece.equals(Knight.BLACK_KNIGHT)) {
                pieceType = 1;
            } else if (piece.equals(Bishop.BLACK_BISHOP)) {
                pieceType = 2;
            } else if (piece.equals(Rook.BLACK_ROOK)) {
                pieceType = 3;
            } else if (piece.equals(Queen.BLACK_QUEEN)) {
                pieceType = 4;
            } else {
                return;
            }
            sq_b = sq ^ 63;
            sq_w = sq;
        }

        int index_w = (pieceType << 1) + pieceColor;
        int index_b = (pieceType << 1) + (1 - pieceColor);

        int feature_w = (640 * wKingSq) + (64 + index_w) + sq_w;
        int feature_b = (640 * bKingSq) + (64 + index_b) + sq_b;

        for (int o=0;o<NN_SIZE;o++) {
            accumulator[0][o] += W0[NN_SIZE * feature_w + o];
            accumulator[1][o] += W0[NN_SIZE * feature_b + o];
        }
    }

    private void computeLayer(double[] B, double[] I, double[] W, double[] O, boolean withRelu) {
        for (int o=0;o<O.length;o++) {
            double sum = B[o];
            for (int i=0;i<I.length;i++) {
                sum += W[o * I.length + i] * I[i];
            }

            if (withRelu)
                O[o] = clamp(sum);
            else
                O[o] = sum;
        }
    }
}
