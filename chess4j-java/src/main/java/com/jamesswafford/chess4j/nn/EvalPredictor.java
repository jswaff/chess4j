package com.jamesswafford.chess4j.nn;

import ai.djl.inference.Predictor;
import ai.djl.translate.TranslateException;
import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.exceptions.ModelException;

public class EvalPredictor {

    public static int predict(Predictor<Board, Float> predictor, Board board) {
        try {
            int score = Math.round(predictor.predict(board));
            return board.getPlayerToMove().isWhite() ? score : -score;
        } catch (TranslateException e) {
            throw new ModelException(e);
        }
    }

}
