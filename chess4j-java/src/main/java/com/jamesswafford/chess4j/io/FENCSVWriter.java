package com.jamesswafford.chess4j.io;

import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class FENCSVWriter {

    private static final Logger LOGGER = LogManager.getLogger(FENCSVWriter.class);

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
