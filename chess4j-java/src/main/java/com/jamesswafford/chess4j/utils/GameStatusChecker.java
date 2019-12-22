package com.jamesswafford.chess4j.utils;

import java.util.List;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.movegen.MoveGen;
import com.jamesswafford.chess4j.board.squares.Square;
import com.jamesswafford.chess4j.pieces.Bishop;
import com.jamesswafford.chess4j.pieces.Knight;
import com.jamesswafford.chess4j.pieces.Pawn;
import com.jamesswafford.chess4j.pieces.Piece;
import com.jamesswafford.chess4j.pieces.Queen;
import com.jamesswafford.chess4j.pieces.Rook;


public final class GameStatusChecker {

    private GameStatusChecker() { }

    public static GameStatus getGameStatus() {
        Board b = Board.INSTANCE;
        List<Move> moves = MoveGen.genLegalMoves(b);
        if (moves.size()==0) {
            if (b.isPlayerInCheck()) {
                return GameStatus.CHECKMATED;
            } else {
                return GameStatus.STALEMATED;
            }
        }

        if (isLackOfMatingMaterial(b)) {
            return GameStatus.DRAW_MATERIAL;
        }

        if (isDrawByRep(b)) {
            return GameStatus.DRAW_REP;
        }

        if (b.getFiftyCounter() >= 100) {
            return GameStatus.DRAW_BY_50;
        }

        return GameStatus.INPROGRESS;
    }

    private static boolean isLackOfMatingMaterial(Board b) {

        int numKnights=0,numWhiteSqBishops=0,numBlackSqBishops=0;
        List<Square> squares = Square.allSquares();
        for (Square sq : squares) {
            Piece p = b.getPiece(sq);
            if (p instanceof Pawn || p instanceof Rook || p instanceof Queen) { return false; }
            if (p instanceof Knight) {
                numKnights++;
            }

            if (p instanceof Bishop) {
                if (sq.isLight()) {
                    numWhiteSqBishops++;
                } else {
                    numBlackSqBishops++;
                }
            }
        }

        return !canMateWithMinors(numKnights,numWhiteSqBishops,numBlackSqBishops);
    }

    private static boolean canMateWithMinors(int numKnights,int numWhiteSqBishops,int numDarkSqBishops) {
        if (numKnights > 0) {
            if (numKnights > 1) {
                return true;
            }
            if (numWhiteSqBishops > 0 || numDarkSqBishops > 0) {
                return true;
            }
        }

        if (numWhiteSqBishops > 0 && numDarkSqBishops > 0) {
            return true;
        }

        return false;
    }

    public static int getNumberPreviousVisits(Board b) {
        long currentZobristKey = b.getZobristKey();
        return (int) b.getUndos().stream()
                .filter(u -> u.getZobristKey()==currentZobristKey)
                .count();
    }

    public static boolean isDrawByRep(Board b) {
        int numPrevVisits = getNumberPreviousVisits(b);
        return numPrevVisits >= 2;
    }

}
