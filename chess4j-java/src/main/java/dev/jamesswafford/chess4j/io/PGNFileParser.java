package dev.jamesswafford.chess4j.io;

import dev.jamesswafford.chess4j.board.Board;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class PGNFileParser {

    private static final Logger LOGGER = LogManager.getLogger(PGNFileParser.class);

    public PGNFileParser() { }

    public static List<FENRecord> load(String pgnFile, boolean dedupe, double probability) throws IOException {
        return load(new File(pgnFile), dedupe, probability);
    }

    public static List<FENRecord> load(File pgnFile, boolean dedupe) throws IOException {
        return load(pgnFile, dedupe, 1.0);
    }

    public static List<FENRecord> load(File pgnFile, boolean dedupe, double probability) throws IOException {
        LOGGER.info("loading records from: {} dedupe: {} probability: {}", pgnFile, dedupe, probability);
        List<FENRecord> fenRecords = new ArrayList<>();

        Random random = new Random(System.currentTimeMillis());
        Set<String> seen = new HashSet<>();

        PGNIterator it = new PGNIterator(pgnFile);
        PGNGame pgnGame;
        while ((pgnGame = it.next()) != null) {
            List<FENRecord> gameFens = toFEN(pgnGame);
            gameFens.stream()
                    .filter(fenRecord -> !dedupe || !seen.contains(fenRecord.getFen()))
                    .filter(fenRecord -> random.nextDouble() < probability)
                    .forEach(fenRecord -> {
                        seen.add(fenRecord.getFen());
                        fenRecords.add(fenRecord);
                    });
        }

        LOGGER.info("loaded {} FEN records", fenRecords.size());
        return fenRecords;
    }

    public static List<FENRecord> toFEN(PGNGame pgnGame) {
        List<FENRecord> fenRecords = new ArrayList<>();

        Board board = new Board();
        fenRecords.add(FENRecord.builder()
                .fen(FENBuilder.createFen(board, true))
                .result(pgnGame.getResult())
                .build());

        pgnGame.getMoves().forEach(mv -> {
            board.applyMove(mv.getMove());
            fenRecords.add(FENRecord.builder()
                    .fen(FENBuilder.createFen(board, true))
                    .result(pgnGame.getResult())
                    .build());
        });

        return fenRecords;
    }

}
