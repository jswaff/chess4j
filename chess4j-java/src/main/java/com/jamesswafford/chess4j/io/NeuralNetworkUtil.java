package com.jamesswafford.chess4j.io;

import com.jamesswafford.ml.nn.Network;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

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
