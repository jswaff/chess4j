package com.jamesswafford.chess4j.movegen;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.squares.Square;

public final class Mobility {

    public static int bishopMobility(Board board, Square fromSq) {

        long emptySquares = ~(board.getWhitePieces() | board.getBlackPieces());
        long bishopMoves = Magic.getBishopMoves(board, fromSq.value(), emptySquares);

        return Long.bitCount(bishopMoves);
    }

}
