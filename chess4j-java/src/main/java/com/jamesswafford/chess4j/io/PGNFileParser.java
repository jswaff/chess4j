package com.jamesswafford.chess4j.io;

import com.jamesswafford.chess4j.board.Board;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PGNFileParser {

    public PGNFileParser() { }

    public static List<FENRecord> load(String pgnFile) throws IOException {
        return load(new File(pgnFile));
    }

    public static List<FENRecord> load(File pgnFile) throws IOException {
        List<FENRecord> fenRecords = new ArrayList<>();

        PGNIterator it = new PGNIterator(pgnFile);
        PGNGame pgnGame;
        while ((pgnGame = it.next()) != null) {
            fenRecords.addAll(toFEN(pgnGame));
        }

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
