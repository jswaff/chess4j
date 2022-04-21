package com.jamesswafford.chess4j.tuner;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.eval.Eval;
import com.jamesswafford.chess4j.eval.EvalWeights;
import io.vavr.Tuple2;
import org.ejml.simple.SimpleMatrix;

import java.util.List;
import java.util.Random;

public class MatrixUtils {

    public static Tuple2<SimpleMatrix, SimpleMatrix> loadXY(List<GameRecord> gameRecords, int batchSize, int numFeatures) {

        Random random = new Random(System.currentTimeMillis());

        int m = Math.min(batchSize, gameRecords.size());
        SimpleMatrix x = new SimpleMatrix(m, numFeatures);
        SimpleMatrix y = new SimpleMatrix(m, 1);

        for (int i=0;i<m;i++) {
            GameRecord trainingRecord = gameRecords.get(random.nextInt(gameRecords.size()));
            Board board = new Board(trainingRecord.getFen());
            double[] features_i = Eval.extractFeatures(board);
            for (int j=0;j<numFeatures;j++) {
                x.set(i, j, features_i[j]);
            }
            y.set(i, 0, CostFunction.y(trainingRecord.getResult()));
        }

        return new Tuple2<>(x, y);
    }

    public static SimpleMatrix weightsToMatrix(EvalWeights weights) {
        int n = weights.vals.length;
        SimpleMatrix theta = new SimpleMatrix(n, 1);
        for (int i=0;i<n;i++) {
            theta.set(i, 0, weights.vals[i]);
        }
        return theta;
    }

}
