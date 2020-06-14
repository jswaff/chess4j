package com.jamesswafford.chess4j.io;

import com.jamesswafford.chess4j.board.Color;
import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.utils.MoveUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class PrintLine {
    private static final  Logger LOGGER = LogManager.getLogger(PrintLine.class);

    public static void printLine(boolean depthChange, List<Move> moves, int depth, int score, long elapsedMs,
                                 long nodes) {
        long timeInCentis = elapsedMs / 10;
        String line = getMoveString(moves);
        String output = String.format("%2d%s %5d %5d %7d %s", depth, depthChange?".":" ", score, timeInCentis, nodes,
                line);
        LOGGER.info(output);
    }

    public static void printNativeLine(int depth, List<Long> nativeMoves, boolean whiteToMove, int score,
                                       long elapsedMS, long nodes) {
        List<Move> convertedMoves = MoveUtils.fromNativeLine(nativeMoves, whiteToMove ? Color.WHITE : Color.BLACK);
        printLine(false, convertedMoves, depth, score, elapsedMS, nodes);
    }

    public static String getMoveString(List<Move> moves) {
        StringBuilder s = new StringBuilder();
        for (Move m : moves) {
            s.append(m.toString()).append(" ");
        }
        return s.toString();
    }

}
