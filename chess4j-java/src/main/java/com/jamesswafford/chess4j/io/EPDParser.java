package com.jamesswafford.chess4j.io;

import java.util.ArrayList;
import java.util.List;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.exceptions.ParseException;

public final class EPDParser {

    private EPDParser() { }

    // set the board and return a list of operations
    // Note the EPD grammar can be found here:
    // http://chessprogramming.wikispaces.com/Extended+Position+Description
    public static List<EPDOperation> setPos(Board b,String epd) throws ParseException {
        List<EPDOperation> opsList = new ArrayList<>();

        // want the string up to the 4th space.
        int ind=0;
        for (int i=0;i<4;i++) {
            ind = epd.indexOf(' ', ind+1);
        }
        String fenPart = epd.substring(0, ind);
        FenParser.setPos(b, fenPart);

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
}
