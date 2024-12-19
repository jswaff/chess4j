package dev.jamesswafford.chess4j.nn;

import dev.jamesswafford.chess4j.board.Board;
import dev.jamesswafford.chess4j.pieces.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.UncheckedIOException;

public class NeuralNetwork {
    public static final int NN_SIZE_L1 = 128;
    public static final int NN_SIZE_L2 = 32;
    public static final int NN_SIZE_L3 = 32;
    public static final int NN_SIZE_L4 = 1;

    private final double[] W0;
    private final double[] B0;
    private final double[] W1;
    private final double[] B1;
    private final double[] W2;
    private final double[] B2;
    private final double[] W3;
    private final double[] B3;

    // Temporary - will be moved into separate structure
    private final double[][] accumulator;

    public NeuralNetwork() {
        W0 = new double[768 * NN_SIZE_L1];
        B0 = new double[NN_SIZE_L1];
        W1 = new double[NN_SIZE_L1 * 2 * NN_SIZE_L2];
        B1 = new double[NN_SIZE_L2];
        W2 = new double[NN_SIZE_L2 * NN_SIZE_L3];
        B2 = new double[NN_SIZE_L3];
        W3 = new double[NN_SIZE_L3 * NN_SIZE_L4];
        B3 = new double[NN_SIZE_L4];
        accumulator = new double[2][NN_SIZE_L1];
    }

    public NeuralNetwork(String networkFile) {
        this();
        load(networkFile);
    }

    public void load(String networkFile) {
        try (BufferedReader br = new BufferedReader(new FileReader(networkFile))) {
            // note the transposition for W0!
            for (int row=0;row<NN_SIZE_L1;row++)
                for (int col=0;col<768;col++)
                    W0[col * NN_SIZE_L1 + row] = Double.parseDouble(br.readLine());
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
            throw new UncheckedIOException(e);
        }
    }

    public int eval(Board board) {

        populateAccumulators(board);

        // layer 1 features
        double[] L1 = new double[NN_SIZE_L1 * 2];
        for (int o=0;o<NN_SIZE_L1;o++) {
            //L1[o] = clamp(accumulator[ptm][o]);
            //L1[NN_SIZE_L1 + o] = clamp(accumulator[1-ptm][o]);
            L1[o] = clamp(accumulator[0][o]);
            L1[NN_SIZE_L1 + o] = clamp(accumulator[1][o]);
        }

        // layers 2-4
        double[] L2 = new double[NN_SIZE_L2];
        double[] L3 = new double[NN_SIZE_L3];
        double[] L4 = new double[NN_SIZE_L4];

        computeLayer(L1, W1, B1, L2, true);
        computeLayer(L2, W2, B2, L3, true);
        computeLayer(L3, W3, B3, L4, false);

        int pred = (int)Math.round(L4[0] * 100);
        return board.getPlayerToMove().isWhite() ? pred : -pred;
    }

    private double clamp(double val) {
        if (val < 0.0) return 0.0;
        if (val > 1.0) return 1.0;
        return val;
    }

    private void populateAccumulators(Board board) {

        // initialize with bias term
        for (int o=0;o<NN_SIZE_L1;o++) {
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
        Piece piece = board.getPiece(sq);

        int pieceColor, pieceType;
        if (piece.isWhite()) {
            pieceColor = 0;
            if (piece.equals(Rook.WHITE_ROOK)) {
                pieceType = 0;
            } else if (piece.equals(Knight.WHITE_KNIGHT)) {
                pieceType = 1;
            } else if (piece.equals(Bishop.WHITE_BISHOP)) {
                pieceType = 2;
            } else if (piece.equals(Queen.WHITE_QUEEN)) {
                pieceType = 3;
            } else if (piece.equals(King.WHITE_KING)) {
                pieceType = 4;
            } else {
                pieceType = 5; // pawn
            }
        } else {
            pieceColor = 1;
            if (piece.equals(Rook.BLACK_ROOK)) {
                pieceType = 0;
            } else if (piece.equals(Knight.BLACK_KNIGHT)) {
                pieceType = 1;
            } else if (piece.equals(Bishop.BLACK_BISHOP)) {
                pieceType = 2;
            } else if (piece.equals(Queen.BLACK_QUEEN)) {
                pieceType = 3;
            } else if (piece.equals(King.BLACK_KING)) {
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
            accumulator[0][o] += W0[NN_SIZE_L1 * feature_w + o];
            accumulator[1][o] += W0[NN_SIZE_L1 * feature_b + o];
        }
    }

    private void computeLayer(double[] I, double[] W, double[] B, double[] O, boolean withPosClamp) {
        for (int o=0;o<O.length;o++) {
            double sum = B[o];
            for (int i=0;i<I.length;i++) {
                sum += W[o * I.length + i] * I[i];
            }

            if (withPosClamp)
                O[o] = clamp(sum);
            else
                O[o] = sum;
        }
    }
}
