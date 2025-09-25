package dev.jamesswafford.chess4j.nn;

import dev.jamesswafford.chess4j.nativelib.NativeEngineLib;
import dev.jamesswafford.chess4j.board.Board;
import dev.jamesswafford.chess4j.nativelib.NativeLibraryLoader;
import dev.jamesswafford.chess4j.io.DrawBoard;
import dev.jamesswafford.chess4j.io.FENBuilder;
import jdk.incubator.vector.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Arrays;

public class NeuralNetwork {

    private static final Logger LOGGER = LogManager.getLogger(NeuralNetwork.class);

    public static final int NN_SIZE_L1 = 384;
    public static final int NN_SIZE_L2 = 2;

    private static final int SCALE = 64;
    private static final int THRESHOLD = 127;

    public final short[] W0;
    public final short[] B0;
    public final byte[] W1;
    public final byte[] B1;

    static {
        NativeLibraryLoader.init();
    }

    public NeuralNetwork() {
        W0 = new short[768 * NN_SIZE_L1];
        B0 = new short[NN_SIZE_L1];
        W1 = new byte[NN_SIZE_L1 * 2 * NN_SIZE_L2];
        B1 = new byte[NN_SIZE_L2];
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
                    W0[col * NN_SIZE_L1 + row] = (short)parseInt(br.readLine());
            for (int i=0;i<B0.length;i++)
                B0[i] = (short)parseInt(br.readLine());
            for (int i=0;i<W1.length;i++)
                W1[i] = (byte)parseInt(br.readLine());
            for (int i=0;i<B1.length;i++)
                B1[i] = (byte)parseInt(br.readLine());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        if (NativeLibraryLoader.nativeCodeInitialized()) {
            LOGGER.debug("# loading network {} into native code", networkFile.getPath());
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
        byte[] L1 = new byte[NN_SIZE_L1 * 2];
        for (int o=0;o<NN_SIZE_L1;o++) {
            L1[o] = (byte)clamp(board.getNnueAccumulators().get(0, o));
            L1[NN_SIZE_L1 + o] = (byte)clamp(board.getNnueAccumulators().get(1, o));
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

        ////////////////////////////////////////
/*
    const __m256i one = _mm256_set1_epi16(1);
    for (int i=0;i<NN_SIZE_L2;i++) {
        __m256i sum0 = _mm256_setzero_si256();
        for (int j=0;j<NN_SIZE_L1*2;j+=32) {
            const __m256i inp = _mm256_loadu_si256((__m256i*) &L1[j]);
            const __m256i wei = _mm256_loadu_si256((__m256i*) &nn->W1[i * (NN_SIZE_L1*2) + j]);
            const __m256i dot = _mm256_madd_epi16(_mm256_maddubs_epi16(inp, wei), one);
            sum0 = _mm256_add_epi32(sum0, dot);
        }

        const __m128i sum128lo = _mm256_castsi256_si128(sum0);
        const __m128i sum128hi = _mm256_extracti128_si256(sum0, 1);
        __m128i sum128 = _mm_add_epi32(sum128lo, sum128hi);
        sum128 = _mm_add_epi32(sum128, _mm_shuffle_epi32(sum128, _MM_PERM_ABCD));
        sum128 = _mm_add_epi32(sum128, _mm_shuffle_epi32(sum128, _MM_PERM_CDAB));
        L2[i] = _mm_cvtsi128_si32(sum128) + nn->B1[i];
    }
 */
        int[] L2_1 = new int[NN_SIZE_L2];

        VectorSpecies<Short> species256 = ShortVector.SPECIES_256;
//        ShortVector one = ShortVector.broadcast(species256, 1);
//        assert(one.length()==16);

        for (int i=0;i<NN_SIZE_L2;i++) {
            ShortVector vector256 = ShortVector.zero(species256);
//            for (int j=0;j<(NN_SIZE_L1*2);j++) {
//                // load 256 bits (32 bytes) starting at L1[j]
//    //            ShortVector inp = ShortVector.fromArray(species, L1, j);
//            }

            L2_1[i] = vector256.reduceLanes(VectorOperators.ADD) + B1[i];
        }


//        assert(Arrays.equals(L2, L2_1));
        ////////////////////////////////////////

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
        if (NativeLibraryLoader.nativeCodeInitialized()) {
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

}
