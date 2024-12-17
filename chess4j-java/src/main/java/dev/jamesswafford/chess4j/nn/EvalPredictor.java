package dev.jamesswafford.chess4j.nn;

import ai.djl.inference.Predictor;
import ai.djl.translate.TranslateException;
import dev.jamesswafford.chess4j.board.Board;
import dev.jamesswafford.chess4j.exceptions.ModelException;

public class EvalPredictor {

    public static int predict(Predictor<Board, Float> predictor, Board board) {
        try {
            double prediction = predictor.predict(board) * 100.0;
            int score = (int)Math.round(prediction);
            return board.getPlayerToMove().isWhite() ? score : -score;
        } catch (TranslateException e) {
            throw new ModelException(e);
        }
    }

}
