package com.jamesswafford.chess4j.tuner;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.exceptions.PgnProcessingException;
import com.jamesswafford.chess4j.io.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Deprecated
public class PGNToTuner {

    Logger LOGGER = LogManager.getLogger(PGNToTuner.class);

    private final TunerDatasource tunerDatasource;

    public PGNToTuner(TunerDatasource tunerDatasource) {
        this.tunerDatasource = tunerDatasource;
    }

    public void addFile(File pgnFile) {
        try {
            processPGNFile(pgnFile,true);
            processPGNFile(pgnFile,false);
        } catch (IOException e) {
            throw new PgnProcessingException("Error adding " + pgnFile.getName() + " to tuner", e);
        }
    }

    private void processPGNFile(File pgnFile, boolean dryRun) throws IOException {
        int n = 0;

        PGNIterator it = new PGNIterator(pgnFile);
        PGNGame pgnGame;
        while ((pgnGame = it.next()) != null) {
            LOGGER.info("processing game " + n + " with " + pgnGame.getMoves().size() + " moves " + (dryRun? " (dry run)":""));
            if (!dryRun) {
                addGame(pgnGame);
            }
            n++;
        }
    }

    private void addGame(PGNGame game) {

        // don't process games that don't have an outcome!
        if (!Arrays.asList(PGNResult.WHITE_WINS, PGNResult.BLACK_WINS, PGNResult.DRAW).contains(game.getResult())) {
            return;
        }
        Board board = new Board();

        List<MoveWithNAG> gameMoves = game.getMoves();
        int i=0;
        while (i<gameMoves.size()) {
            MoveWithNAG gameMove = gameMoves.get(i);
            board.applyMove(gameMove.getMove());
            if (i >= 10) { // skip first 5 complete moves
                String fen = FENBuilder.createFen(board, false);
                tunerDatasource.insert(fen, game.getResult());
                // if we have the depth/score in the annotation, use it
                /*if (gameMove.getNag() != null) {
                    CutechessNagParser cutechessNagParser = new CutechessNagParser(gameMove.getNag());
                    if (cutechessNagParser.isValid()) {
                    }
                }*/
            }
            i++;
        }
    }

}
