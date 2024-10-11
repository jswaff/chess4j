package dev.jamesswafford.chess4j.io;

import dev.jamesswafford.chess4j.nn.FENLabeler;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.List;

public class FENCSVUtils {

    private static final Logger LOGGER = LogManager.getLogger(FENCSVUtils.class);

    @SneakyThrows
    public static void relabel(String inCsvFile, String outCsvFile) {

        LOGGER.info("relabeling records from {} to {}", inCsvFile, outCsvFile);

        BufferedReader in = null;
        BufferedWriter out = null;
        FENLabeler fenLabeler = new FENLabeler();
        try {
            in = new BufferedReader(new FileReader(inCsvFile));
            out = new BufferedWriter(new FileWriter(outCsvFile));
            String line;
            while ((line = in.readLine()) != null) {
                String[] parts = line.split(",");
                String fen = parts[1];
                FENRecord fenRecord = FENRecord.builder().fen(fen).build();
                fenLabeler.label(fenRecord, 0);
                out.write(fenRecord.getEval() + "," + fenRecord.getFen() + "\n");
            }
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) { /* ignore */ }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) { /* ignore */  }
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
