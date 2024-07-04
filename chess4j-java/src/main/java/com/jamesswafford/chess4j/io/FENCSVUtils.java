package com.jamesswafford.chess4j.io;

import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FENCSVUtils {

    private static final Logger LOGGER = LogManager.getLogger(FENCSVUtils.class);

    @SneakyThrows
    public static List<FENRecord> readFromCSV(String csvFile) {

        LOGGER.info("reading records from {}", csvFile);

        List<FENRecord> fenRecords = new ArrayList<>();
        BufferedReader in = null;
        try {
            File file = new File(csvFile);
            in = new BufferedReader(new FileReader(file));
            String line;
            while ((line = in.readLine()) != null) {
                String[] parts = line.split(",");
                int evalScore = Integer.parseInt(parts[0]);
                String fen = parts[1];
                fenRecords.add(FENRecord.builder().eval(evalScore).fen(fen).build());
            }
            return fenRecords;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) { /* ignore */ }
            }
        }

    }

    @SneakyThrows
    public static void writeToCSV(List<FENRecord> fenRecords, String csvFile) {

        LOGGER.info("writing labeled records to {}", csvFile);

        BufferedWriter out = null;
        try {
            FileWriter fstream = new FileWriter(csvFile);
            out = new BufferedWriter(fstream);
            for (FENRecord fenRecord : fenRecords) {
                out.write(fenRecord.getEval() + "," + fenRecord.getFen() + "\n");
            }
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) { /* ignore */  }
            }
        }
    }

}
