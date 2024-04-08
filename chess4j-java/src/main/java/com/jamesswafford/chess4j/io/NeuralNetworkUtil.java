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
import com.jamesswafford.ml.nn.Network;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class NeuralNetworkUtil {

    public static Network load(String networkConfigFileName) {
        String netConfig;
        try {
            netConfig = Files.readString(Path.of(networkConfigFileName));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return Network.fromJson(netConfig);
    }

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

    public static void store(Network network, String configFileName) {
        String netConfig = network.toJson();
        try {
            FileWriter writer = new FileWriter(configFileName);
            writer.write(netConfig);
            writer.close();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
