package com.jamesswafford.chess4j.io;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.exceptions.EpdProcessingException;
import com.jamesswafford.chess4j.exceptions.ParseException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// TODO: can the parser be made smart enough to detect the format without using the zuriFormat flag?
public final class EPDParser {

    private static final Pattern outcomeBracketPattern = Pattern.compile("^\\[\\d\\.\\d]");

    private EPDParser() { }

    public static List<FENRecord> load(String epdFile, boolean zuriFormat) throws IOException {
        return load(new File(epdFile), zuriFormat);
    }
    public static List<FENRecord> load(File epdFile, boolean zuriFormat) throws IOException {
        List<FENRecord> fenRecords = new ArrayList<>();
        FileInputStream fis = null;
        Scanner sc = null;

        try {
            fis = new FileInputStream(epdFile);
            sc = new Scanner(fis, StandardCharsets.UTF_8);
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                fenRecords.add(readLine(line, zuriFormat));
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
        return fenRecords;
    }

    // set the board and return a list of operations
    // Note the EPD grammar can be found here:
    // https://www.chessprogramming.org/Extended_Position_Description
    public static List<EPDOperation> setPos(Board b, String epd) throws ParseException {
        List<EPDOperation> opsList = new ArrayList<>();

        // want the string up to the 4th space.
        int ind=0;
        for (int i=0;i<4;i++) {
            ind = epd.indexOf(' ', ind+1);
        }
        String fenPart = epd.substring(0, ind);
        b.setPos(fenPart);

        // the remaining bits are operations
        // e.g. bm Ba2 Nxf7
        //      id "WAC.022"
        String opsPart = epd.substring(ind+1);
        String[] opsArr = opsPart.split(";");

        for (String ops : opsArr) {
            ops = ops.trim();
            String opCode;
            List<String> operands;
            int opsInd = ops.indexOf(' ');
            if (opsInd==-1) {
                opCode = ops;
                operands = new ArrayList<>();
            } else {
                opCode = ops.substring(0, opsInd);
                operands = getOperands(ops.substring(opsInd+1));
            }

            EPDOperation epdOp = new EPDOperation(opCode);
            for (String operand : operands) {
                epdOp.addOperand(operand);
            }
            opsList.add(epdOp);
        }

        return opsList;
    }

    private static List<String> getOperands(String strOperands) {
        List<String> operands = new ArrayList<>();

        strOperands = strOperands.trim();
        if ("".equals(strOperands)) {
            return operands;
        }

        // if the first character is a quotation mark, go to the next quotation mark.
        // otherwise, split on spaces
        if (strOperands.charAt(0)=='"') {
            int ind = strOperands.indexOf('"', 1);
            String myOperand = strOperands.substring(1, ind);
            operands.add(myOperand);
            operands.addAll(getOperands(strOperands.substring(ind+1)));
        } else {
            int ind = strOperands.indexOf(' ');
            if (ind==-1) {
                operands.add(strOperands);
            } else {
                String myOperand = strOperands.substring(0, ind);
                operands.add(myOperand);
                operands.addAll(getOperands(strOperands.substring(ind+1)));
            }
        }

        return operands;
    }

    private static FENRecord readLine(String epdLine, boolean zuriFormat) {

        Board board = new Board();
        List<EPDOperation> epdOperations = setPos(board, epdLine);
        String fen = FENBuilder.createFen(board, false);
        if (!epdLine.startsWith(fen)) {
            throw new EpdProcessingException("Error processing epdLine " + epdLine + ".  Expected FEN " + fen);
        }

        String outcome;
        if (zuriFormat) {
            // the Zuri dataset uses the "c9" opcode, which is a comment, to denote the outcome
            EPDOperation c9 = epdOperations.stream().filter(epdOperation -> "c9".equals(epdOperation.getEpdOpcode()))
                    .findFirst()
                    .orElseThrow(() -> new EpdProcessingException("couldn't find c9 opcode"));

            if (c9.getEpdOperands().size() != 1) {
                throw new EpdProcessingException("Expected one operand for c9 opcode");
            }
            outcome = c9.getEpdOperands().get(0);
        } else {
            // Assume format of Ethereal data.  The outcome is the operand in brackets
            //8/2N2kp1/5b1p/1P6/3p4/4P1P1/5K1P/8 b - - 0 38 [1.0] -154
            outcome = epdOperations.get(0).getEpdOperands().stream().filter(opCode -> {
                Matcher outcomeMatcher = outcomeBracketPattern.matcher(opCode);
                return outcomeMatcher.matches();
            }).findFirst().orElseThrow(() -> new EpdProcessingException("couldn't find outcome in epd op codes"));
            outcome = outcome.replace("[", "");
            outcome = outcome.replace("]", "");
        }

        PGNResult pgnResult;
        if ("1-0".equals(outcome) || "1.0".equals(outcome)) {
            pgnResult = PGNResult.WHITE_WINS;
        } else if ("0-1".equals(outcome) || "0.0".equals(outcome)) {
            pgnResult = PGNResult.BLACK_WINS;
        } else if ("1/2-1/2".equals(outcome) || "0.5".equals(outcome)) {
            pgnResult = PGNResult.DRAW;
        } else {
            throw new EpdProcessingException("Don't know how to map outcome to result: " + outcome);
        }

        return FENRecord.builder().fen(fen).result(pgnResult).build();
    }

}
