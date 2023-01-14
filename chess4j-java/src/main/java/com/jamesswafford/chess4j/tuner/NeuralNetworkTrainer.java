package com.jamesswafford.chess4j.tuner;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.squares.Square;
import com.jamesswafford.chess4j.pieces.*;
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

    public Network train(int maxSamples, List<GameRecord> dataSet, double learningRate, int numEpochs) {

        int MAX_SIZE = 1048576 / 4;
        if (dataSet.size() > MAX_SIZE) {
            dataSet = dataSet.subList(0, MAX_SIZE);
        }

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
                .numInputUnits(837)
                .layers(List.of(
                        new Layer(10, Sigmoid.INSTANCE),
                        new Layer(1, Sigmoid.INSTANCE)
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
            double[] data = transform(new Board(gameRecord.getFen()));
            for (int r=0;r<X.numRows();r++) {
                X.set(r, c, data[r]);
            }

            // set label
            double label = CostFunction.y(gameRecord.getResult());
            Y.set(0, c, label);
        }

        return new Pair<>(X, Y);
    }

    private double[] transform(Board board) {
        double[] data = new double[837];

        for (int i=0;i<64;i++) {
            Piece p = board.getPiece(i);
            if (Rook.WHITE_ROOK.equals(p)) data[i] = 1;
            else if (Rook.BLACK_ROOK.equals(p)) data[64+i] = 1;
            else if (Knight.WHITE_KNIGHT.equals(p)) data[128+i] = 1;
            else if (Knight.BLACK_KNIGHT.equals(p)) data[192+i] = 1;
            else if (Bishop.WHITE_BISHOP.equals(p)) data[256+i] = 1;
            else if (Bishop.BLACK_BISHOP.equals(p)) data[320+i] = 1;
            else if (Queen.WHITE_QUEEN.equals(p)) data[384+i] = 1;
            else if (Queen.BLACK_QUEEN.equals(p)) data[448+i] = 1;
            else if (King.WHITE_KING.equals(p)) data[512+i] = 1;
            else if (King.BLACK_KING.equals(p)) data[576+i] = 1;
            else if (Pawn.WHITE_PAWN.equals(p)) data[640+i] = 1;
            else if (Pawn.BLACK_PAWN.equals(p)) data[704+i] = 1;

            if (Square.valueOf(i).equals(board.getEPSquare())) data[768+i] = 1;
        }

        if (board.hasBQCastlingRight()) data[832] = 1;
        if (board.hasBKCastlingRight()) data[833] = 1;
        if (board.hasWQCastlingRight()) data[834] = 1;
        if (board.hasWKCastlingRight()) data[835] = 1;

        if (board.getPlayerToMove().isWhite()) data[836] = 1;

        return data;
    }
}
