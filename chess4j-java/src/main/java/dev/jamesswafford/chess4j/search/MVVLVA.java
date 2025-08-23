package dev.jamesswafford.chess4j.search;

import dev.jamesswafford.chess4j.NativeEngineLib;
import dev.jamesswafford.chess4j.board.Move;
import dev.jamesswafford.chess4j.init.Initializer;
import dev.jamesswafford.chess4j.pieces.*;
import dev.jamesswafford.chess4j.utils.MoveUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class MVVLVA {

    private static final Logger LOGGER = LogManager.getLogger(MVVLVA.class);

    static {
        Initializer.init();
    }

    private static final Map<Class<?>,Integer> pieceMap;

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
     * @param m - the move to score
     * @return - the score
     */
    public static int score(Move m) {
        int score = 0;

        if (m.promotion() != null) {
            score = scorePromotion(m);
        }

        if (m.captured() != null) {
            score += scoreCapture(m);
        }

        // if we are running with assertions enabled and the native library is loaded, verify equality
        assert(mvvlvaAreEqual(score, m));

        return score;
    }

    private static int scorePromotion(Move m) {
        int promoVal = pieceMap.get(m.promotion().getClass());

        return 10000 + promoVal;
    }

    private static int scoreCapture(Move m) {
        int capturedVal = pieceMap.get(m.captured().getClass());
        assert(!m.isEpCapture() || capturedVal==1);
        int moverVal = pieceMap.get(m.piece().getClass());
        return 1000 + (capturedVal * 10) - moverVal;
    }

    private static boolean mvvlvaAreEqual(int javaScore, Move mv) {
        if (Initializer.nativeCodeInitialized()) {
            try {
                int nativeScore = (int) NativeEngineLib.mvvlva.invoke(MoveUtils.toNativeMove(mv));
                if (javaScore != nativeScore) {
                    LOGGER.error("mvvlva not equal!  javaScore: " + javaScore + ", nativeScore: " + nativeScore
                            + ", mv: " + mv);
                    LOGGER.error("moving piece: " + mv.piece() + "; captured: " + mv.captured()
                            + "; ep?: " + mv.isEpCapture());
                    return false;
                }
                return true;
            } catch (IllegalStateException e) {
                LOGGER.error(e);
                throw e;
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        } else {
            return true;
        }
    }

}
