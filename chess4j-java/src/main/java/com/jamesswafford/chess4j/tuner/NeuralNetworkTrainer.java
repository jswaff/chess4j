package com.jamesswafford.chess4j.tuner;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.ml.nn.Layer;
import com.jamesswafford.ml.nn.Network;
import com.jamesswafford.ml.nn.activation.Identity;
import com.jamesswafford.ml.nn.cost.MSE;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NeuralNetworkTrainer {

    private static final Logger LOGGER = LogManager.getLogger(NeuralNetworkTrainer.class);

    private static final int MINI_BATCH_SIZE = 256;
    private static final int MAX_DATA_SET_SIZE = 1000 * 1000 * 5;

    public Network train(List<GameRecord> dataSet, double learningRate, int numEpochs) {

        LOGGER.info("data set size {} learningRate {} numEpochs {}", dataSet.size(), learningRate, numEpochs);

        // divide the data up into training and test sets
        Collections.shuffle(dataSet);
        if (dataSet.size() > MAX_DATA_SET_SIZE) {
            dataSet.subList(MAX_DATA_SET_SIZE, dataSet.size()).clear();
        }
        int m = dataSet.size() * 95 / 100;
        List<GameRecord> trainingSet = new ArrayList<>(dataSet.subList(0, m));
        List<GameRecord> testSet = dataSet.subList(m, dataSet.size());
        LOGGER.info("training set size {} test set size {}", trainingSet.size(), testSet.size());

        // create a network
        Network network = Network.builder()
                .numInputUnits(BoardToNetwork.NUM_INPUTS)
                .layers(List.of(
                        new Layer(1, Identity.INSTANCE)
                ))
                .costFunction(MSE.INSTANCE)
                .build();

        network.initialize();

        // load the test data
        Pair<double[][], double[][]> X_Y_test = loadXY(testSet);
        double[][] X_test = X_Y_test.getValue0();
        double[][] Y_test = X_Y_test.getValue1();
        LOGGER.info("X_test shape: " + X_test.length + "x" + X_test[0].length);
        LOGGER.info("Y_test shape: " + Y_test.length + "x" + Y_test[0].length);

        // get the initial cost
        double[][] P_init = network.predict(X_test);
        LOGGER.info("initial cost {}", network.cost(P_init, Y_test));

        // train!
        int numMiniBatches = trainingSet.size() / MINI_BATCH_SIZE;
        if ((trainingSet.size() % MINI_BATCH_SIZE) != 0) {
            numMiniBatches++;
        }

        long startTime = System.currentTimeMillis();
        network.train(numMiniBatches,
                batchNum -> {
                    int fromInd = MINI_BATCH_SIZE * batchNum;
                    int toInd = Math.min(MINI_BATCH_SIZE * (batchNum + 1), trainingSet.size());
                    return loadXY(trainingSet.subList(fromInd, toInd));
                },
                numEpochs, learningRate, X_test, Y_test);
        long elapsed = (System.currentTimeMillis() - startTime) / 1000;
        LOGGER.info("elapsed time (sec) {}", elapsed);

        double[][] P_final = network.predict(X_test);
        LOGGER.info("final cost {}", network.cost(P_final, Y_test));

        return network;
    }

    private Pair<double[][], double[][]> loadXY(List<GameRecord> gameRecords) {

        // number of X rows is number of features
        double[][] X = new double[BoardToNetwork.NUM_INPUTS][gameRecords.size()];

        // just one output neuron
        double[][] Y = new double[1][gameRecords.size()];

        for (int c=0;c<gameRecords.size();c++) {
            GameRecord gameRecord = gameRecords.get(c);

            // set the input features for this sample
            Board board = new Board(gameRecord.getFen());
            double[][] data = BoardToNetwork.transform(board);
            for (int r=0;r<X.length;r++) {
                X[r][c] = data[r][0];
            }

            // set label
            double label = ((double)gameRecord.getEval()) / 100.0; // convert to pawns
            if (board.getPlayerToMove().isBlack()) label = -label; // convert to white POV
            Y[0][c] = label;
        }

        return new Pair<>(X, Y);
    }

}
