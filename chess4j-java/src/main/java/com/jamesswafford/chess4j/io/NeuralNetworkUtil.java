package com.jamesswafford.chess4j.io;

import ai.djl.MalformedModelException;
import ai.djl.inference.Predictor;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.translate.TranslateException;
import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.exceptions.ModelException;
import com.jamesswafford.chess4j.tuner.BoardTranslator;

import java.io.*;
import java.nio.file.Paths;

public class NeuralNetworkUtil {

    public static Predictor<Board, Float> loadModel(String modelFileName) {
        Criteria<Board, Float> criteria = Criteria.builder()
                .setTypes(Board.class, Float.class)
                .optTranslator(new BoardTranslator())
                .optModelPath(Paths.get(modelFileName))
                .build();
        try {
            ZooModel<Board, Float> model = criteria.loadModel();
            Predictor<Board, Float> predictor = model.newPredictor();
            predictor.predict(new Board()); // verify everything is working
            return predictor;
        } catch (IOException | ModelNotFoundException | MalformedModelException | TranslateException e) {
            throw new ModelException(e);
        }
    }

}
