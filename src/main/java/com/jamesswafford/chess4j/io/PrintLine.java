package com.jamesswafford.chess4j.io;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jamesswafford.chess4j.board.Move;

public class PrintLine {
    private static final Log logger = LogFactory.getLog(PrintLine.class);

    public static void printLine(List<Move> moves,int depth,int score,long startTime,long nodes) {
        long timeInCentis = (System.currentTimeMillis() - startTime) / 10;
        String line = getMoveString(moves);
        String output = String.format("%2d %5d %5d %7d %s",depth,score,timeInCentis,nodes,line);
        logger.info(output);
    }

    public static String getMoveString(List<Move> moves) {
        String s = "";
        for (Move m : moves) {
            s += m.toString() + " ";
        }
        return s;
    }

}
