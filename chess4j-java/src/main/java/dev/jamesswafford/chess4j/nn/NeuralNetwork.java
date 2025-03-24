package dev.jamesswafford.chess4j.nn;

import dev.jamesswafford.chess4j.board.Board;
import dev.jamesswafford.chess4j.init.Initializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

public class NeuralNetwork {

    private static final Logger LOGGER = LogManager.getLogger(NeuralNetwork.class);

    public static final int NN_SIZE_L1 = 1536;
    public static final int NN_SIZE_L2 = 1;

    private static final int SCALE = 64;
    private static final int THRESHOLD = 127;

    public final int[] W0;
    public final int[] B0;
    public final int[] W1;
    public final int[] B1;

    static {
        Initializer.init();
    }

    public NeuralNetwork() {
        W0 = new int[768 * NN_SIZE_L1];
        B0 = new int[NN_SIZE_L1];
        W1 = new int[NN_SIZE_L1 * 2 * NN_SIZE_L2];
        B1 = new int[NN_SIZE_L2];
    }

    public NeuralNetwork(File networkFile) {
        this();
        load(networkFile);
    }

    public NeuralNetwork(String networkFile) {
        this();
        load(new File(networkFile));
    }

    public void load(File networkFile) {
        LOGGER.debug("# loading neural network from {}", networkFile);

        try (BufferedReader br = new BufferedReader(new FileReader(networkFile))) {
            // note the transposition for W0!
            for (int row=0;row<NN_SIZE_L1;row++)
                for (int col=0;col<768;col++)
                    W0[col * NN_SIZE_L1 + row] = parseInt(br.readLine());
            for (int i=0;i<B0.length;i++)
                B0[i] = parseInt(br.readLine());
            for (int i=0;i<W1.length;i++)
                W1[i] = parseInt(br.readLine());
            for (int i=0;i<B1.length;i++)
                B1[i] = parseInt(br.readLine());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        if (Initializer.nativeCodeInitialized()) {
            LOGGER.debug("# loading network into native code");
            loadNeuralNetworkNative();
        }
    }

    private int parseInt(String val) {
        int ival = Integer.parseInt(val);
        if (ival < -THRESHOLD || ival > THRESHOLD)
            throw new IllegalStateException("Expected weights to be in range [" + -THRESHOLD +
                    "," + THRESHOLD + "].  Read " + ival);
        return ival;
    }

    public int eval(Board board) {

        // set layer 1 features from accumulators
        int[] L1 = new int[NN_SIZE_L1 * 2];
        for (int o=0;o<NN_SIZE_L1;o++) {
            L1[o] = clamp(board.getNnueAccumulators().get(0, o), 0, THRESHOLD);
            L1[NN_SIZE_L1 + o] = clamp(board.getNnueAccumulators().get(1, o), 0, THRESHOLD);
        }

        // calculate other layers
        int[] L2 = new int[NN_SIZE_L2];

        computeLayer(L1, W1, B1, L2);

        double y = ((double)L2[0]) / (SCALE * SCALE);

        int pred = (int)Math.round(y * 100); // centi-pawns
        return board.getPlayerToMove().isWhite() ? pred : -pred;
    }

    private int clamp(int val, int min, int max) {
        if (val < min) return min;
        if (val > max) return max;
        return val;
    }

    private void computeLayer(int[] I, int[] W, int[] B, int[] O) {
        for (int o=0;o<O.length;o++) {
            int sum = B[o];

            for (int i=0;i<I.length;i++) {
                sum += W[o * I.length + i] * I[i];
            }

            O[o] = sum;
        }
    }

    private native void loadNeuralNetworkNative();
}
