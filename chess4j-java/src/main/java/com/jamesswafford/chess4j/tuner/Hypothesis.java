package com.jamesswafford.chess4j.tuner;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.eval.Eval;
import com.jamesswafford.chess4j.eval.EvalTermsVector;

public class Hypothesis {

    public static double hypothesis(Board board, EvalTermsVector evalTermsVector) {
        int score = Eval.eval(evalTermsVector, board, false);
        return squishify(score);
    }

    public static double squishify(int score) {
        // This is the traditional approach: 1 / (1 + e ^ -z)
        //return 1.0 / (1 + Math.exp(-score));

        // This is the "Texel" approach
        double k = -1.13;
        double exp = k * score / 400.0;
        return 1.0 / (1 + Math.pow(10, exp));
    }

}
