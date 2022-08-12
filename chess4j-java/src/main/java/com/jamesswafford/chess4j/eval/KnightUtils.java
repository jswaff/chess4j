package com.jamesswafford.chess4j.eval;

import com.jamesswafford.chess4j.board.Bitboard;
import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Color;
import com.jamesswafford.chess4j.board.squares.File;
import com.jamesswafford.chess4j.board.squares.North;
import com.jamesswafford.chess4j.board.squares.South;
import com.jamesswafford.chess4j.board.squares.Square;

public class KnightUtils {

    private static final long[][] outpost = new long[64][2];

    static {
        for (int i=0;i<64;i++) {
            Square sq = Square.valueOf(i);

            outpost[i][Color.WHITE.ordinal()] = 0;
            outpost[i][Color.BLACK.ordinal()] = 0;

            if (sq.file().getValue() > File.FILE_A.getValue()) {
                outpost[i][Color.WHITE.ordinal()] |= Bitboard.rays[i-1][North.getInstance().value()];
                outpost[i][Color.BLACK.ordinal()] |= Bitboard.rays[i-1][South.getInstance().value()];
            }
            if (sq.file().getValue() < File.FILE_H.getValue()) {
                outpost[i][Color.WHITE.ordinal()] |= Bitboard.rays[i+1][North.getInstance().value()];
                outpost[i][Color.BLACK.ordinal()] |= Bitboard.rays[i+1][South.getInstance().value()];
            }
        }
    }

    /**
     * An outpost is a square that cannot be attacked by enemy pawns.  This is useful for knights because they
     * can't be driven away by a pawn. 
     * 
     * @param board
     * @param sq
     * @param isWhite
     * @return
     */    
    public static boolean isOutpost(Board board, Square sq, boolean isWhite) {
        long enemies = isWhite ? board.getBlackPawns() : board.getWhitePawns();
        return (outpost[sq.value()][isWhite?Color.WHITE.ordinal():Color.BLACK.ordinal()] & enemies)==0;
    }


}
