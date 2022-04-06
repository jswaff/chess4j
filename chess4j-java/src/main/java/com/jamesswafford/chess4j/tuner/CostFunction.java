package com.jamesswafford.chess4j.tuner;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.eval.EvalWeightsVector;
import com.jamesswafford.chess4j.utils.GameResult;

import java.util.List;

import static com.jamesswafford.chess4j.tuner.Hypothesis.hypothesis;

public class CostFunction {

    public static double cost(double hypothesis, GameResult gameResult) {
        double y;
        if (GameResult.WIN.equals(gameResult)) {
            y = 1.0;
        } else if (GameResult.DRAW.equals(gameResult)) {
            y = 0.5;
        } else if (GameResult.LOSS.equals(gameResult)) {
            y = 0.0;
        } else {
            throw new IllegalArgumentException("Cannot compute cost for game result " + gameResult);
        }

        double delta = y - hypothesis;
        return delta * delta;
    }

    public static double cost(List<GameRecord> dataSet, EvalWeightsVector theta) {
        double totalError = 0;

        for (GameRecord gameRecord : dataSet) {
            Board board = new Board(gameRecord.getFen());
            double h = hypothesis(board, theta);
            double cost = cost(h, gameRecord.getGameResult());
            totalError += cost;
        }

        return totalError / dataSet.size();
    }
}
