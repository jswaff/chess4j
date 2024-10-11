package dev.jamesswafford.chess4j.movegen;

import dev.jamesswafford.chess4j.board.Board;
import dev.jamesswafford.chess4j.board.squares.Square;

public final class Mobility {

    public static int bishopMobility(Board board, Square fromSq) {

        long emptySquares = ~(board.getWhitePieces() | board.getBlackPieces());
        long bishopMoves = Magic.getBishopMoves(board, fromSq.value(), emptySquares);

        return Long.bitCount(bishopMoves);
    }

    public static int rookMobility(Board board, Square fromSq) {

        long emptySquares = ~(board.getWhitePieces() | board.getBlackPieces());
        long rookMoves = Magic.getRookMoves(board, fromSq.value(), emptySquares);

        return Long.bitCount(rookMoves);
    }

    public static int queenMobility(Board board, Square fromSq) {

        long emptySquares = ~(board.getWhitePieces() | board.getBlackPieces());
        long queenMoves = Magic.getQueenMoves(board, fromSq.value(), emptySquares);

        return Long.bitCount(queenMoves);
    }
}
