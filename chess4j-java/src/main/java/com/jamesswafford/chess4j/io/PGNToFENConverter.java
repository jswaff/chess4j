package com.jamesswafford.chess4j.io;

import com.jamesswafford.chess4j.board.Board;

import java.util.ArrayList;
import java.util.List;

public class PGNToFENConverter {

    public static List<FENRecord> convert(PGNGame pgnGame) {
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
