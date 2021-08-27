package com.jamesswafford.chess4j.eval;

import com.jamesswafford.chess4j.board.Bitboard;
import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Color;
import com.jamesswafford.chess4j.board.squares.File;
import com.jamesswafford.chess4j.board.squares.North;
import com.jamesswafford.chess4j.board.squares.South;
import com.jamesswafford.chess4j.board.squares.Square;

public class PawnUtils {

    private static final long[] isolated = new long[64];
    private static final long[][] passed = new long[64][2];

    static {
        for (int i=0;i<64;i++) {
            isolated[i] = 0;
            Square sq = Square.valueOf(i);
            if (sq.file().getValue() > File.FILE_A.getValue()) {
                isolated[i] |= Bitboard.files[sq.file().getValue()-1];
            }
            if (sq.file().getValue() < File.FILE_H.getValue()) {
                isolated[i] |= Bitboard.files[sq.file().getValue()+1];
            }

            passed[i][Color.WHITE.ordinal()] = Bitboard.rays[i][North.getInstance().value()];
            passed[i][Color.BLACK.ordinal()] = Bitboard.rays[i][South.getInstance().value()];

            if (sq.file().getValue() > File.FILE_A.getValue()) {
                passed[i][Color.WHITE.ordinal()] |= Bitboard.rays[i-1][North.getInstance().value()];
                passed[i][Color.BLACK.ordinal()] |= Bitboard.rays[i-1][South.getInstance().value()];
            }
            if (sq.file().getValue() < File.FILE_H.getValue()) {
                passed[i][Color.WHITE.ordinal()] |= Bitboard.rays[i+1][North.getInstance().value()];
                passed[i][Color.BLACK.ordinal()] |= Bitboard.rays[i+1][South.getInstance().value()];
            }
        }
    }

    /**
     * isPassed(board,pawnSq) - return true if the pawn on <pawnSq> is a passed pawn.  A passed pawn is a pawn
     * with no opposing pawns to prevent it from advancing to the eighth rank.  i.e. there are no opposing pawns
     * in front of it on the same file nor on an adjacent file. (//http://en.wikipedia.org/wiki/Passed_pawn)
     *
     * @param board - the board
     * @param pawnSq - the square the pawn to be evaluated is on
     * @return - the score
     */
    public static boolean isPassedPawn(Board board, final Square pawnSq, boolean isWhite) {
        long enemies = isWhite ? board.getBlackPawns() : board.getWhitePawns();
        return (passed[pawnSq.value()][isWhite?Color.WHITE.ordinal():Color.BLACK.ordinal()] & enemies)==0;
    }

    public static boolean isDoubled(Board board, Square pawnSq, boolean isWhite) {

        long fileMask = Bitboard.files[pawnSq.file().getValue()] ^ Bitboard.squares[pawnSq.value()];
        if (isWhite) {
            return (fileMask & board.getWhitePawns()) != 0;
        } else {
            return (fileMask & board.getBlackPawns()) != 0;
        }
    }

    public static boolean isIsolated(Board board, Square pawnSq, boolean isWhite) {
        long friends = isWhite ? board.getWhitePawns() : board.getBlackPawns();
        return (isolated[pawnSq.value()] & friends)==0;
    }
}
