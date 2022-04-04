package com.jamesswafford.chess4j.tuner;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.eval.Eval;
import com.jamesswafford.chess4j.eval.EvalTermsVector;
import com.jamesswafford.chess4j.utils.GameResult;

public class CostFunction {

    public double calculateCost(EvalTermsVector etv, Board board, GameResult gameResult) {
        int score = Eval.eval(etv, board, false);
        double squishedScore = squishify(score);
        return calculateCost(squishedScore, gameResult);
    }

    public double calculateCost(double squishedScore, GameResult gameResult) {
        double r;
        if (GameResult.WIN.equals(gameResult)) {
            r = 1.0;
        } else if (GameResult.DRAW.equals(gameResult)) {
            r = 0.5;
        } else if (GameResult.LOSS.equals(gameResult)) {
            r = 0.0;
        } else {
            throw new IllegalArgumentException("Cannot compute cost for game result " + gameResult);
        }

        double delta = r - squishedScore;
        return delta * delta;
    }

    public double squishify(int score) {
        // This is the traditional approach: 1 / (1 + e ^ -z)
        //return 1.0 / (1 + Math.exp(-score));

        // This is the "Texel" approach
        double k = -1.13;
        double exp = k * score / 400.0;
        return 1.0 / (1 + Math.pow(10, exp));
    }

}
