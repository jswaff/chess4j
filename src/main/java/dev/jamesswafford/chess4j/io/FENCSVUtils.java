package dev.jamesswafford.chess4j.io;

import dev.jamesswafford.chess4j.nn.FENLabeler;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

public class FENCSVUtils {

    private static final Logger LOGGER = LogManager.getLogger(FENCSVUtils.class);

    /**
     * Relabel a CSV file.
     *
     * The input file should have at least two fields.
     * First field: the FEN
     * Second field: the score (label) to be updated
     * Any fields after the second field are preserved as-is.
     *
     * @param inCsvFile
     * @param outCsvFile
     * @param depth
     */
    @SneakyThrows
    public static void relabel(String inCsvFile, String outCsvFile, int depth) {

        LOGGER.info("relabeling records from {} to {} depth {}", inCsvFile, outCsvFile, depth);

        FENLabeler fenLabeler = new FENLabeler();
        try (BufferedReader in = new BufferedReader(new FileReader(inCsvFile));
             BufferedWriter out = new BufferedWriter(new FileWriter(outCsvFile)))
        {
            String line;
            while ((line = in.readLine()) != null) {
                String[] parts = line.split(",");
                String fen = parts[0];
                FENRecord fenRecord = FENRecord.builder().fen(fen).build();
                fenLabeler.label(fenRecord, depth);
                StringBuilder sb = new StringBuilder(fenRecord.getFen()).append(",").append(fenRecord.getEval());
                for (int i=2;i<parts.length;i++) {
                    sb.append(",").append(parts[i]);
                }
                sb.append("\n");
                out.write(sb.toString());
            }
        }
    }
}
