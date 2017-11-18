package com.jamesswafford.chess4j.search;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.pieces.Bishop;
import com.jamesswafford.chess4j.pieces.King;
import com.jamesswafford.chess4j.pieces.Knight;
import com.jamesswafford.chess4j.pieces.Pawn;
import com.jamesswafford.chess4j.pieces.Queen;
import com.jamesswafford.chess4j.pieces.Rook;

public class MVVLVA implements Comparator<Move> {

    private static Map<Class<?>,Integer> pieceMap;

    static {
        pieceMap = new HashMap<Class<?>,Integer>();
        pieceMap.put(King.class, 6);
        pieceMap.put(Queen.class, 5);
        pieceMap.put(Rook.class, 4);
        pieceMap.put(Bishop.class, 3);
        pieceMap.put(Knight.class, 2);
        pieceMap.put(Pawn.class, 1);
    }

    private Board board;

    public MVVLVA(Board board) {
        this.board = board;
    }

    /**
     * Score the move.  Promotions are scored highest of all, with capturing promotions
     * being higher than non-capturing promotions.  Secondary to capture vs non-capture
     * is the piece being promoted to.  Queen promotions are higher, followed by rook,
     * bishop, then knight.
     *
     * Captures are scored next using the MVV/LVA algorithm.  This is not a very accurate
     * algorithm, but it's fast and cheap.  The idea is to score moves that capture bigger
     * pieces higher. If two moves capture the same piece, the move with the smallest capturer
     * is scored higher.
     * PxQ, NxQ, BxQ, RxQ, QxQ, KxQ, PxR, BxR, NxR, RxR, QxR, KxR ...
     *
     * The remaining (non-capturing) moves are next, in no particular order.
     *
     * @param m
     * @return
     */
    public static int score(Board b,Move m) {
        int score = 0;

        if (m.promotion() != null) {
            score = scorePromotion(m);
        }

        if (m.captured() != null) {
            score += scoreCapture(b,m);
        }

        return score;
    }

    private static int scorePromotion(Move m) {
        int promoVal = pieceMap.get(m.promotion().getClass());

        return 10000 + promoVal;
    }

    private static int scoreCapture(Board b,Move m) {
        int capturedVal = pieceMap.get(m.captured().getClass());
        int moverVal = pieceMap.get(b.getPiece(m.from()).getClass());
        return 1000 + (capturedVal * 10) - moverVal;
    }

    @Override
    public int compare(Move m1, Move m2) {
        int score1 = score(board,m1);
        int score2 = score(board,m2);

        int retVal = 0;
        if (score1 > score2) {
            retVal = -1;
        } else if (score1 < score2) {
            retVal = 1;
        }

        return retVal;
    }

}
