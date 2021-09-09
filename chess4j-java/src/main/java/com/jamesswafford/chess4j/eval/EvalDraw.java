package com.jamesswafford.chess4j.eval;

import com.jamesswafford.chess4j.board.Board;

public class EvalDraw {

    public static boolean evalDraw(Board board) {

        // get the piece counts, including the king
        int numWhitePieces = Long.bitCount(board.getWhitePieces());
        int numBlackPieces = Long.bitCount(board.getBlackPieces());

        if (numWhitePieces == 1 && numBlackPieces == 1) return true;
        if (numWhitePieces > 3 || numBlackPieces > 3) return false;

        if (numWhitePieces == 1) {
            if (numBlackPieces == 2) {
                if (board.getBlackKnights() != 0 || board.getBlackBishops() != 0) {
                    return true;
                }
            } else { // black has three pieces
                if (Long.bitCount(board.getBlackKnights()) == 2) {
                    return true;
                }
            }
        } else if (numBlackPieces == 1) {
            if (numWhitePieces == 2) {
                if (board.getWhiteKnights() != 0 || board.getWhiteBishops() != 0) {
                    return true;
                }
            } else { // white has three pieces
                if (Long.bitCount(board.getWhiteKnights()) == 2) {
                    return true;
                }
            }
        } else if (numWhitePieces == 2 && numBlackPieces == 2) {
            if ((board.getWhiteBishops() != 0 || board.getWhiteKnights() != 0) &&
                    (board.getBlackBishops() != 0 || board.getBlackKnights() != 0)) {
                return true;
            }
        }

        return false;
    }

}
