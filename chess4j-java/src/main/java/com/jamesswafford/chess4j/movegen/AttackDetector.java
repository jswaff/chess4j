package com.jamesswafford.chess4j.movegen;

import com.jamesswafford.chess4j.board.Bitboard;
import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Color;
import com.jamesswafford.chess4j.board.squares.File;
import com.jamesswafford.chess4j.board.squares.Square;


public final class AttackDetector {

    private AttackDetector() {}

    /**
     * Get a bitmap representation of all pieces of a specific color that are attacking a specific square.
     *
     * @param board - the chess board
     * @param sq - the square we want to know is attacked or not
     * @param color - the player that would do the attacking
     *
     * @return - bitmap representation of all attackers
     */
    public static long getAttackers(Board board, Square sq, Color color) {
        int sqVal = sq.value();
        long attackers = Bitboard.knightMoves[sqVal] &
                (color==Color.WHITE ? board.getWhiteKnights() : board.getBlackKnights());
        attackers |= Bitboard.kingMoves[sqVal] & Bitboard.squares[board.getKingSquare(color).value()];

        attackers |= Magic.getRookMoves(board, sqVal,
                (color==Color.WHITE ? board.getWhiteRooks() : board.getBlackRooks()));

        attackers |= Magic.getBishopMoves(board, sqVal,
                (color==Color.WHITE ? board.getWhiteBishops() : board.getBlackBishops()));
        attackers |= Magic.getQueenMoves(board, sqVal,
                (color==Color.WHITE ? board.getWhiteQueens() : board.getBlackQueens()));

        if (color==Color.WHITE) {
            attackers |=  ((Bitboard.squares[sqVal] & ~Bitboard.files[File.FILE_A.getValue()]) << 7)
                    & board.getWhitePawns();
            attackers |=  ((Bitboard.squares[sqVal] & ~Bitboard.files[File.FILE_H.getValue()]) << 9)
                    & board.getWhitePawns();
        } else {
            attackers |=  ((Bitboard.squares[sqVal] & ~Bitboard.files[File.FILE_A.getValue()]) >> 9)
                    & board.getBlackPawns();
            attackers |=  ((Bitboard.squares[sqVal] & ~Bitboard.files[File.FILE_H.getValue()]) >> 7)
                    & board.getBlackPawns();
        }

        return attackers;
    }

    /**
     * Is <sq> attacked by <player>?
     *
     * @param board - the chess board
     * @param sq - the square we want to know is attacked or not
     * @param player - the player that would do the attacking
     *
     * @return - true if player is attacking square, otherwise false
     */
    public static boolean attacked(Board board, Square sq, Color player) {
        if (attackedByPawn(board,sq,player)) { return true; }
        if (attackedByRook(board,sq,player)) { return true; }
        if (attackedByKnight(board,sq,player)) { return true; }
        if (attackedByBishop(board,sq,player)) { return true; }
        if (attackedByQueen(board,sq,player)) { return true; }
        return attackedByKing(board, sq, player);
    }

    private static boolean attackedByBishop(Board board,Square sq,Color player) {
        return (Magic.getBishopMoves(board,sq.value(),
                player==Color.WHITE ? board.getWhiteBishops() : board.getBlackBishops() )) != 0;
    }

    private static boolean attackedByKing(Board board,Square sq,Color player) {
        return (Bitboard.kingMoves[sq.value()]
                & Bitboard.squares[board.getKingSquare(player).value()]) != 0;
    }

    private static boolean attackedByKnight(Board board,Square sq,Color player) {
        return (Bitboard.knightMoves[sq.value()]
                & (player==Color.WHITE ? board.getWhiteKnights() : board.getBlackKnights())) != 0;
    }

    private static boolean attackedByPawn(Board board,Square sq,Color player) {
        if (player==Color.WHITE) {
            if ((((board.getWhitePawns() & ~Bitboard.files[File.FILE_A.getValue()]) >> 9)
                    & Bitboard.squares[sq.value()]) != 0)
            {
                return true;
            }
            return (((board.getWhitePawns() & ~Bitboard.files[File.FILE_H.getValue()]) >> 7)
                    & Bitboard.squares[sq.value()]) != 0;
        } else {
            if ((((board.getBlackPawns() & ~Bitboard.files[File.FILE_A.getValue()]) << 7)
                    & Bitboard.squares[sq.value()]) != 0)
            {
                return true;
            }
            return (((board.getBlackPawns() & ~Bitboard.files[File.FILE_H.getValue()]) << 9)
                    & Bitboard.squares[sq.value()]) != 0;
        }
    }

    private static boolean attackedByQueen(Board board,Square sq,Color player) {
        return (Magic.getQueenMoves(board,sq.value(),
                player==Color.WHITE ? board.getWhiteQueens() : board.getBlackQueens() )) != 0;
    }

    private static boolean attackedByRook(Board board,Square sq,Color player) {
        return (Magic.getRookMoves(board,sq.value(),
                player==Color.WHITE ? board.getWhiteRooks() : board.getBlackRooks() )) != 0;
    }

}
