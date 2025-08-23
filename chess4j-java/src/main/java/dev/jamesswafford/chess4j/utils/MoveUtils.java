package dev.jamesswafford.chess4j.utils;

import dev.jamesswafford.chess4j.board.Board;
import dev.jamesswafford.chess4j.board.Move;
import dev.jamesswafford.chess4j.io.PrintLine;
import dev.jamesswafford.chess4j.movegen.MagicBitboardMoveGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.List;


public final class MoveUtils {

    private static final  Logger LOGGER = LogManager.getLogger(MoveUtils.class);

    private MoveUtils() { }

    public static void putMoveAtTop(List<Move> moves,Move m) {
        if (moves.remove(m)) {
            moves.add(0, m);
        }
    }

    public static void putMoveAtTop(Move[] moves,Move m) {

        for (int i=1;i<moves.length;i++) {
            if (moves[i].equals(m)) {
                swap(moves,0,i);
                break;
            }
        }
    }

    public static void swap(List<Move> moves,int ind1,int ind2) {
        Collections.swap(moves, ind1, ind2);
    }

    public static void swap(Move[] moves,int ind1,int ind2) {
        Move tmp = moves[ind1];
        moves[ind1] = moves[ind2];
        moves[ind2] = tmp;
    }

    public static int indexOf(List<Move> moves,Move move,int from) {
        for (int i=from;i<moves.size();i++) {
            if (move.equals(moves.get(i))) {
                return i;
            }
        }
        return -1;
    }

    public static int indexOf(Move[] moves,Move move,int from) {

        for (int i=from;i<moves.length;i++) {
            if (move.equals(moves[i])) {
                return i;
            }
        }

        return -1;
    }

    public static boolean isLineValid(List<Move> moveLine, Board board) {
        Board myBoard = board.deepCopy();

        for (Move move : moveLine) {
            if (!isLegalMove(move, myBoard)) {
                LOGGER.debug("# invalid line! - " + PrintLine.getMoveString(moveLine));
                return false;
            }
            myBoard.applyMove(move);
        }

        return true;
    }

    private static boolean isLegalMove(Move move, Board board) {
        List<Move> legalMoves = MagicBitboardMoveGenerator.genLegalMoves(board);
        return legalMoves.contains(move);
    }

}
