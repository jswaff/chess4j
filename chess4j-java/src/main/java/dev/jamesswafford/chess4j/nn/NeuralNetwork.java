package dev.jamesswafford.chess4j.nn;

import dev.jamesswafford.chess4j.NativeEngineLib;
import dev.jamesswafford.chess4j.board.Board;
import dev.jamesswafford.chess4j.init.Initializer;
import dev.jamesswafford.chess4j.io.DrawBoard;
import dev.jamesswafford.chess4j.io.FENBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

public class NeuralNetwork {

    private static final Logger LOGGER = LogManager.getLogger(NeuralNetwork.class);

    public static final int NN_SIZE_L1 = 384;
    public static final int NN_SIZE_L2 = 2;

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
            LOGGER.debug("# loading network {} into native code", networkFile.getPath());
            loadNeuralNetworkNative();
            NativeEngineLib.loadNeuralNetwork(networkFile);
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
            L1[o] = clamp(board.getNnueAccumulators().get(0, o));
            L1[NN_SIZE_L1 + o] = clamp(board.getNnueAccumulators().get(1, o));
        }

        // calculate layer 2
        int[] L2 = new int[NN_SIZE_L2];

        for (int i=0;i<NN_SIZE_L2;i++) {
            int sum = B1[i];
            for (int j=0;j<(NN_SIZE_L1*2);j++) {
                sum += W1[i * (NN_SIZE_L1*2) + j] * L1[j];
            }
            L2[i] = sum;
        }

        // translate into scores
        float wscore = ((float)L2[0]) / (SCALE * SCALE) * 100; // to centipawns
        float wr = ((float)L2[1]) / (SCALE * SCALE) * 1000;
        int y_hat = my_round((0.5F * wscore) + (0.5F * wr));

        // return for player on move
        int retval = board.getPlayerToMove().isWhite() ? y_hat : -y_hat;

        assert(verifyNativeEvalIsEqual(retval, board));

        return retval;
    }

    private int clamp(int val) {
        if (val < 0) return 0;
        return Math.min(val, NeuralNetwork.THRESHOLD);
    }

    // this is used for consistency with the native code
    private int my_round(float val) {
        if (val > 0) return (int)(val + 0.5);
        else return (int)(val - 0.5);
    }

    private boolean verifyNativeEvalIsEqual(int javaScore, Board board) {
        if (Initializer.nativeCodeInitialized()) {
            int nativeScore = NativeEngineLib.evalNN(board);
            if (javaScore != nativeScore) {
                DrawBoard.drawBoard(board);
                LOGGER.error("nn eval not equal!  java: {}, native: {} fen {}",
                        javaScore, nativeScore, FENBuilder.createFen(board, false));
                return false;
            }
            return true;
        } else {
            return true;
        }
    }

    private native void loadNeuralNetworkNative();

}
