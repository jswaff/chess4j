package com.jamesswafford.chess4j.search;

import java.util.HashMap;
import java.util.Map;

import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.pieces.Bishop;
import com.jamesswafford.chess4j.pieces.King;
import com.jamesswafford.chess4j.pieces.Knight;
import com.jamesswafford.chess4j.pieces.Pawn;
import com.jamesswafford.chess4j.pieces.Queen;
import com.jamesswafford.chess4j.pieces.Rook;
import com.jamesswafford.chess4j.search.v2.MoveScorer;

public class MVVLVA implements MoveScorer {

    private static Map<Class<?>,Integer> pieceMap;

    static {
        pieceMap = new HashMap<>();
        pieceMap.put(King.class, 6);
        pieceMap.put(Queen.class, 5);
        pieceMap.put(Rook.class, 4);
        pieceMap.put(Bishop.class, 3);
        pieceMap.put(Knight.class, 2);
        pieceMap.put(Pawn.class, 1);
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
    public static int score(Move m) {
        int score = 0;

        if (m.promotion() != null) {
            score = scorePromotion(m);
        }

        if (m.captured() != null) {
            score += scoreCapture(m);
        }

        return score;
    }

    private static int scorePromotion(Move m) {
        int promoVal = pieceMap.get(m.promotion().getClass());

        return 10000 + promoVal;
    }

    private static int scoreCapture(Move m) {
        int capturedVal = pieceMap.get(m.captured().getClass());
        int moverVal = pieceMap.get(m.piece().getClass());
        return 1000 + (capturedVal * 10) - moverVal;
    }

    @Override
    public int calculateStaticScore(Move mv) {
        return score(mv);
    }
}
