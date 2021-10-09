package com.jamesswafford.chess4j.tuner;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.exceptions.PgnProcessingException;
import com.jamesswafford.chess4j.io.PGNGame;
import com.jamesswafford.chess4j.io.PGNIterator;
import com.jamesswafford.chess4j.io.PGNResult;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public interface TunerDatasource {

    void addToTunerDS(Board board, PGNResult pgnResult);

    default void addToTunerDS(PGNGame game) {

        // don't process games that don't have an outcome!
        if (!Arrays.asList(PGNResult.WHITE_WINS, PGNResult.BLACK_WINS, PGNResult.DRAW).contains(game.getResult())) {
            return;
        }
        Board board = new Board();

        List<Move> gameMoves = game.getMoves();
        int i=0;
        while (i<gameMoves.size()) {
            Move gameMove = gameMoves.get(i);
            if (i > 10) { // skip opening moves
                addToTunerDS(board, game.getResult());
            }
            board.applyMove(gameMove);
            i++;
        }
    }

    default int addToTunerDS(File pgnFile) {
        try {
            processPGNFile(pgnFile,true);
            return processPGNFile(pgnFile,false);
        } catch (IOException e) {
            throw new PgnProcessingException("Error adding " + pgnFile.getName() + " to tuner", e);
        }
    }

    private int processPGNFile(File pgnFile, boolean dryRun) throws IOException {
        int n = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(pgnFile))) {
            PGNIterator it = new PGNIterator(br);
            PGNGame pgnGame;
            while ((pgnGame = it.next()) != null) {
                if (!dryRun) {
                    addToTunerDS(pgnGame);
                }
                n++;
            }
        }

        return n;
    }

}
