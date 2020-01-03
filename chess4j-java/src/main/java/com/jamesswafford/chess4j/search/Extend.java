package com.jamesswafford.chess4j.search;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.board.squares.Rank;
import com.jamesswafford.chess4j.pieces.Pawn;
import com.jamesswafford.chess4j.pieces.Piece;
import com.jamesswafford.chess4j.utils.BoardUtils;

public class Extend {

    /**
     * Return amount to extend search by, given <board> with move <m> just played.
     * @param board
     * @param lastMove
     * @return
     */
    public static int extendDepth(Board board, Move lastMove) {
        return extendDepth(board,lastMove, BoardUtils.isPlayerInCheck(board));
    }

    public static int extendDepth(Board board, Move lastMove, boolean inCheck) {
        if (inCheck) return 1;
        if (lastMove.promotion() != null) return 1;

        //int d = pawnTo7th(b,lastMove);
        //if (d > 0) return d;

        return 0;
    }

    private static int pawnTo7th(Board board, Move lastMove) {
        Piece p = board.getPiece(lastMove.to());
        if (! (p instanceof Pawn)) return 0;

        if (p.isWhite()) {
            if (lastMove.to().rank() == Rank.RANK_7) return 1;
        } else {
            if (lastMove.to().rank() == Rank.RANK_2) return 1;
        }

        return 0;
    }

}
