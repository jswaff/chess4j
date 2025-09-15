package dev.jamesswafford.chess4j.eval;

import dev.jamesswafford.chess4j.board.Board;
import dev.jamesswafford.chess4j.board.squares.Square;
import dev.jamesswafford.chess4j.pieces.Pawn;

public class BishopUtils {

    /**
     * Is the bishop on <sq> trapped?  
     * This is a naive implementation, only looking for bishops on A2/H2/A7/H7 that are trapped behind two pawns.
     * 
     * Note, this routine doesn't validate that a bishop is actually present on sq.
     * 
     * @param board
     * @param sq
     * @param isWhite
     * @return
     */
    public static boolean isTrapped(Board board, Square sq, boolean isWhite) {

        if (isWhite) {
            if (sq==Square.A7) {
                return board.getPiece(Square.B6)==Pawn.BLACK_PAWN && board.getPiece(Square.C7)==Pawn.BLACK_PAWN;
            } else if (sq==Square.H7) {
                return board.getPiece(Square.F7)==Pawn.BLACK_PAWN && board.getPiece(Square.G6)==Pawn.BLACK_PAWN;
            }
        } else {
            if (sq==Square.A2) {
                return board.getPiece(Square.B3)==Pawn.WHITE_PAWN && board.getPiece(Square.C2)==Pawn.WHITE_PAWN;
            } else if (sq==Square.H2) {
                return board.getPiece(Square.F2)==Pawn.WHITE_PAWN && board.getPiece(Square.G3)==Pawn.WHITE_PAWN;
            } 
        }

        return false;
    }
}
