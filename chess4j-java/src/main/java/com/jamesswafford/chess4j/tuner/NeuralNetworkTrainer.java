package com.jamesswafford.chess4j.tuner;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.ml.nn.Layer;
import com.jamesswafford.ml.nn.Network;
import com.jamesswafford.ml.nn.activation.Identity;
import com.jamesswafford.ml.nn.activation.Sigmoid;
import com.jamesswafford.ml.nn.cost.MSE;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ejml.simple.SimpleMatrix;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NeuralNetworkTrainer {

    private static final Logger LOGGER = LogManager.getLogger(NeuralNetworkTrainer.class);

    private final int MINI_BATCH_SIZE = 512;
    private final int MAX_TEST_SET_SIZE = 100000;

    public Network train(List<GameRecord> dataSet, double learningRate, int numEpochs) {

        // divide the data up into training and test sets
        Collections.shuffle(dataSet);
        List<GameRecord> trainingSet;
        List<GameRecord> testSet;
        if (dataSet.size() >= 100) {
            int m;
            if (dataSet.size() > 10000) {
                m = dataSet.size() * 9 / 10;
            } else {
                m = dataSet.size() * 4 / 5;
            }
            if (dataSet.size() - m > MAX_TEST_SET_SIZE) {
                m = dataSet.size() - MAX_TEST_SET_SIZE;
            }
            trainingSet = new ArrayList<>(dataSet.subList(0, m));
            testSet = dataSet.subList(m, dataSet.size());
        } else {
            trainingSet = new ArrayList<>(dataSet);
            testSet = new ArrayList<>(dataSet);
        }
        LOGGER.info("data set size: {} training: {}, test: {}", dataSet.size(), trainingSet.size(), testSet.size());

        // create a network
        Network network = Network.builder()
                .numInputUnits(837)
                .layers(List.of(
                        new Layer(800, Sigmoid.INSTANCE),
                        //new Layer(50, Sigmoid.INSTANCE),
                        new Layer(1, Identity.INSTANCE)
                ))
                .costFunction(MSE.INSTANCE)
                .build();

        network.initialize();

        // load the test data
        Pair<SimpleMatrix, SimpleMatrix> X_Y_test = loadXY(testSet);
        SimpleMatrix X_test = X_Y_test.getValue0();
        SimpleMatrix Y_test = X_Y_test.getValue1();

        // get the initial cost
        SimpleMatrix P_init = network.predict(X_test);
        System.out.println("initial cost: " + network.cost(P_init, Y_test));

        // train!
        int numMiniBatches = trainingSet.size() / MINI_BATCH_SIZE;
        if ((trainingSet.size() % MINI_BATCH_SIZE) != 0) {
            numMiniBatches++;
        }

        network.train(numMiniBatches,
                batchNum -> {
                    int fromInd = MINI_BATCH_SIZE * batchNum;
                    int toInd = Math.min(MINI_BATCH_SIZE * (batchNum + 1), trainingSet.size());
                    return loadXY(trainingSet.subList(fromInd, toInd));
                },
                numEpochs, learningRate, X_test, Y_test);

        SimpleMatrix P_final = network.predict(X_test);
        System.out.println("final cost: " + network.cost(P_final, Y_test));

        return network;
    }

    private Pair<SimpleMatrix, SimpleMatrix> loadXY(List<GameRecord> gameRecords) {

        // number of X rows is number of features
        SimpleMatrix X = new SimpleMatrix(837, gameRecords.size());

        // just one output neuron
        SimpleMatrix Y = new SimpleMatrix(1, gameRecords.size());

        for (int c=0;c<gameRecords.size();c++) {
            GameRecord gameRecord = gameRecords.get(c);

            // set the input features for this sample
            double[] data = BoardToNetwork.transform(new Board(gameRecord.getFen()));
            for (int r=0;r<X.numRows();r++) {
                X.set(r, c, data[r]);
            }

            // set label
            double label = gameRecord.getEval() / 100.0; // convert to pawns
            Y.set(0, c, label);
        }

        return new Pair<>(X, Y);
    }

}
