package com.jamesswafford.chess4j.io;

import ai.djl.MalformedModelException;
import ai.djl.inference.Predictor;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.translate.TranslateException;
import com.jamesswafford.chess4j.board.Board;
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

    public static void loadModel(String modelFileName) {
        Criteria<Board, Float> criteria = Criteria.builder()
                .setTypes(Board.class, Float.class)
                .optTranslator(new BoardTranslator())
                .optModelPath(Paths.get("/home/james/chess-trainer"))
                .optModelName(modelFileName)
                .build();
        try {
            ZooModel<Board, Float> model = criteria.loadModel();
            System.out.println("model loaded!");

            // test
            Predictor<Board, Float> predictor = model.newPredictor();
            try {
                System.out.println(predictor.predict(new Board("rnb1kbnr/pp1pppp1/7p/2q5/5P2/N1P1P3/P2P2PP/R1BQKBNR w KQkq -")));
                System.out.println(predictor.predict(new Board("r1q1kb1r/6pp/b1p1pn2/2P1Np2/QP6/4P3/P2N2PP/R1BR2K1 b kq -")));
                System.out.println(predictor.predict(new Board("8/8/1p1k4/1P6/8/3p3P/1r4P1/5K2 w - -")));
            } catch (TranslateException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException | ModelNotFoundException | MalformedModelException e) {
            throw new RuntimeException(e); // TODO
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
