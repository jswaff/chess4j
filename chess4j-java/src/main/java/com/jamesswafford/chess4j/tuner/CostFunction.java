package com.jamesswafford.chess4j.tuner;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.eval.EvalWeights;
import com.jamesswafford.chess4j.io.FENRecord;
import com.jamesswafford.chess4j.io.PGNResult;

import java.util.List;

import static com.jamesswafford.chess4j.tuner.Hypothesis.hypothesis;

public class CostFunction {

    public static double y(PGNResult pgnResult) {
        double y;
        if (PGNResult.WHITE_WINS.equals(pgnResult)) {
            y = 1.0;
        } else if (PGNResult.DRAW.equals(pgnResult)) {
            y = 0.5;
        } else if (PGNResult.BLACK_WINS.equals(pgnResult)) {
            y = 0.0;
        } else {
            throw new IllegalArgumentException("Cannot compute cost for result " + pgnResult);
        }
        return y;
    }

    public static double cost(double hypothesis, PGNResult result) {
        double delta = hypothesis - y(result);
        return delta * delta;
    }

    public static double cost(List<FENRecord> dataSet, EvalWeights weights) {
        double totalError = 0;

        for (FENRecord fenRecord : dataSet) {
            Board board = new Board(fenRecord.getFen());
            double h = hypothesis(board, weights);
            double cost = cost(h, fenRecord.getResult());
            totalError += cost;
        }

        return totalError / dataSet.size();
    }

}
