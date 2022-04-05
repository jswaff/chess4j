package com.jamesswafford.chess4j.tuner;

import com.jamesswafford.chess4j.utils.GameResult;

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

}
