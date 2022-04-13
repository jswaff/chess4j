package com.jamesswafford.chess4j.tuner;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.exceptions.EpdProcessingException;
import com.jamesswafford.chess4j.io.EPDOperation;
import com.jamesswafford.chess4j.io.EPDParser;
import com.jamesswafford.chess4j.io.FenBuilder;
import com.jamesswafford.chess4j.io.PGNResult;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

public class FenToTuner {

    private final TunerDatasource tunerDatasource;

    public FenToTuner(TunerDatasource tunerDatasource) {
        this.tunerDatasource = tunerDatasource;
    }

    public void addFile(File epdFile) {
        try {
            processEpdFile(epdFile, true);
            processEpdFile(epdFile, false);
        } catch (IOException e) {
            throw new EpdProcessingException("Error adding " + epdFile.getName() + " to tuner", e);
        }
    }

    private void processEpdFile(File epdFile, boolean dryRun) throws IOException {

        FileInputStream fis = null;
        Scanner sc = null;

        try {
            fis = new FileInputStream(epdFile);
            sc = new Scanner(fis, StandardCharsets.UTF_8);
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (!dryRun) {
                    addGame(line);
                }
            }
            // scanner suppresses exceptions
            if (sc.ioException() != null) {
                throw sc.ioException();
            }
        } finally {
            if (fis != null) {
                fis.close();;
            }
            if (sc != null) {
                sc.close();
            }
        }
    }

    private void addGame(String epdLine) {

        Board board = new Board();
        List<EPDOperation> epdOperations = EPDParser.setPos(board, epdLine);

        String fen = FenBuilder.createFen(board, false);

        // the Zuri dataset uses the "c9" opcode, which is a comment, to denote the outcome
        EPDOperation c9 = epdOperations.stream().filter(epdOperation -> "c9".equals(epdOperation.getEpdOpcode()))
                .findFirst()
                .orElseThrow(() -> new EpdProcessingException("couldn't find c9 opcode"));

        if (c9.getEpdOperands().size() != 1) {
            throw new EpdProcessingException("Expected one operand for c9 opcode");
        }
        String outcome = c9.getEpdOperands().get(0);

        PGNResult pgnResult;
        if ("1-0".equals(outcome)) {
            pgnResult = PGNResult.WHITE_WINS;
        } else if ("0-1".equals(outcome)) {
            pgnResult = PGNResult.BLACK_WINS;
        } else if ("1/2-1/2".equals(outcome)) {
            pgnResult = PGNResult.DRAW;
        } else {
            throw new EpdProcessingException("Don't know how to map outcome to result: " + outcome);
        }

        tunerDatasource.insert(fen, pgnResult);
    }
}
