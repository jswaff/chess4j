package com.jamesswafford.chess4j.tuner;

import com.jamesswafford.ml.nn.Layer;
import com.jamesswafford.ml.nn.Network;
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

    public void train(List<GameRecord> dataSet, double learningRate, int numEpochs) {

        // if we have enough data, divide data set up into training and test sets in an 80/20 split
        Collections.shuffle(dataSet);
        List<GameRecord> trainingSet;
        List<GameRecord> testSet;
        if (dataSet.size() >= 100) {
            int m = dataSet.size() * 4 / 5;
            trainingSet = new ArrayList<>(dataSet.subList(0, m));
            testSet = dataSet.subList(m, dataSet.size());
        } else {
            trainingSet = new ArrayList<>(dataSet);
            testSet = new ArrayList<>(dataSet);
        }
        LOGGER.info("data set size: {} training: {}, test: {}", dataSet.size(), trainingSet.size(), testSet.size());

        // create a network
        Network network = Network.builder()
                .numInputUnits(0) // TODO
                .layers(List.of(
                        new Layer(100, Sigmoid.INSTANCE),
                        new Layer(20, Sigmoid.INSTANCE)
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

        // load the training data
        Pair<SimpleMatrix, SimpleMatrix> X_Y_train = loadXY(trainingSet);
        SimpleMatrix X_train = X_Y_train.getValue0();
        SimpleMatrix Y_train = X_Y_train.getValue1();

        // train!
        network.train(X_train, Y_train, numEpochs, 512, learningRate, X_test, Y_test);
        SimpleMatrix P_final = network.predict(X_test);
        System.out.println("final cost: " + network.cost(P_final, Y_test));
    }

    private Pair<SimpleMatrix, SimpleMatrix> loadXY(List<GameRecord> gameRecords) {

        // number of X rows is number of features
        SimpleMatrix X = new SimpleMatrix(1, gameRecords.size()); // TODO

        // just one output neuron
        SimpleMatrix Y = new SimpleMatrix(1, gameRecords.size());

        for (int c=0;c<gameRecords.size();c++) {
            GameRecord gameRecord = gameRecords.get(c);

            // set the input features for this sample
            double[] data = null; // TODO
            for (int r=0;r<X.numRows();r++) {
                X.set(r, c, data[r]);
            }

            // set label
            double label = CostFunction.y(gameRecord.getResult());
            Y.set(0, c, label);
        }

        return new Pair<>(X, Y);
    }

}