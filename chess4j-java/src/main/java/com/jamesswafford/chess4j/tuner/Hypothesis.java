package com.jamesswafford.chess4j.tuner;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.eval.Eval;
import com.jamesswafford.chess4j.eval.EvalWeights;

public class Hypothesis {

    public static double hypothesis(Board board, EvalWeights weights) {
        int score = Eval.eval(weights, board, false, false, true);
        if (board.getPlayerToMove().isBlack()) score = -score;
       return texelSigmoid(score);
    }

    public static double texelSigmoid(double z) {
        double k = -1.13; // computed to minimize error
        double exp = k * z / 400.0;
        return 1.0 / (1 + Math.pow(10, exp));
    }

    public static double classicSigmoid(double z) {
        //  1 / (1 + e ^ -z)
        return 1.0 / (1 + Math.exp(-z));
    }
}
