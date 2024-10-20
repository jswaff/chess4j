package dev.jamesswafford.chess4j.nn;

import ai.djl.MalformedModelException;
import ai.djl.inference.Predictor;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.translate.TranslateException;
import dev.jamesswafford.chess4j.board.Board;
import dev.jamesswafford.chess4j.exceptions.ModelException;

import java.io.*;
import java.nio.file.Paths;

public class ModelLoader {

    public static Predictor<Board, Float> load(String modelFileName) {
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
