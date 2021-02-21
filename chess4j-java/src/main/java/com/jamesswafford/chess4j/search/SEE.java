package com.jamesswafford.chess4j.search;

import com.jamesswafford.chess4j.board.Bitboard;
import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Color;
import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.board.squares.Direction;
import com.jamesswafford.chess4j.board.squares.Square;
import com.jamesswafford.chess4j.init.Initializer;
import com.jamesswafford.chess4j.io.DrawBoard;
import com.jamesswafford.chess4j.movegen.AttackDetector;
import com.jamesswafford.chess4j.movegen.Magic;
import com.jamesswafford.chess4j.pieces.*;
import com.jamesswafford.chess4j.utils.MoveUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

import static com.jamesswafford.chess4j.eval.EvalMaterial.*;

public class SEE {

    private static final Logger LOGGER = LogManager.getLogger(SEE.class);

    static {
        Initializer.init();
    }

    // note m should already be applied
    public static int see(Board b, Move m) {
        assert(m.captured() != null || m.promotion() != null);

        int score = 0;

        if (m.promotion() != null) {
            score = scorePromotion(m);
        }
        else if (m.captured() != null) {
            score = scoreCapture(b,m);
        }

        // if we are running with assertions enabled and the native library is loaded, verify equality
        assert(seesAreEqual(score, b, m));

        return score;
    }

    public static native int seeNative(Board b, long nativeMv);

    private static int scorePromotion(Move m) {
        return evalPiece(m.promotion()) - PAWN_VAL;
    }

    private static int scoreCapture(Board b, Move m) {
        assert(m.captured() != null);
        assert(b.getPiece(m.from())==null);

        int[] scores = new int[32];
        scores[0] = evalPiece(m.captured());
        int scoresInd = 1;

        // play out the sequence
        long whiteAttackersMap = AttackDetector.getAttackers(b, m.to(), Color.WHITE);
        long blackAttackersMap = AttackDetector.getAttackers(b, m.to(), Color.BLACK);

        Color sideToMove = b.getPlayerToMove();
        Square currentSq = m.from();
        Piece currentPiece = b.getPiece(m.to());
        int attackedPieceVal = evalPiece(currentPiece);

        while (true) {
            // add any x-ray attackers back in, behind currentPiece in
            // the direction of m.to -> currentSq
            if (!(currentPiece instanceof Knight) && !(currentPiece instanceof King)) {
                Optional<Direction> dir = Direction.getDirectionTo(m.to(), currentSq);

                assert(dir.isPresent());
                long targetSquares = Bitboard.rays[currentSq.value()][dir.get().value()];

                long xrays;
                if (dir.get().isDiagonal()) {
                    xrays = Magic.getBishopMoves(b,currentSq.value(),targetSquares)
                        & (b.getWhiteBishops() | b.getWhiteQueens() | b.getBlackBishops() | b.getBlackQueens());
                } else {
                    xrays = Magic.getRookMoves(b,currentSq.value(),targetSquares)
                            & (b.getWhiteRooks() | b.getWhiteQueens() | b.getBlackRooks() | b.getBlackQueens());
                }
                if ((xrays & b.getWhitePieces()) != 0) {
                    whiteAttackersMap |= xrays;
                } else if ((xrays & b.getBlackPieces()) != 0) {
                    blackAttackersMap |= xrays;
                }
            }

            currentSq = findLeastValuable(b,sideToMove==Color.WHITE ? whiteAttackersMap : blackAttackersMap);
            if (currentSq==null) break;

            if (sideToMove==Color.WHITE) {
                whiteAttackersMap ^= Bitboard.squares[currentSq.value()];
            } else {
                blackAttackersMap ^= Bitboard.squares[currentSq.value()];
            }
            currentPiece = b.getPiece(currentSq);
            assert(currentPiece != null);

            scores[scoresInd] = attackedPieceVal - scores[scoresInd-1];
            scoresInd++;
            attackedPieceVal = evalPiece(currentPiece);
            sideToMove = Color.swap(sideToMove);
        }

        // evaluate the sequence
        while (scoresInd > 1) {
            scoresInd--;
            scores[scoresInd-1] = -Math.max(-scores[scoresInd-1], scores[scoresInd]);
        }


        return scores[0];
    }

    private static Square findLeastValuable(Board board, long attackers) {
        Square lvSq = null;
        int lvScore = 0;

        while (attackers != 0) {
            int sqInd = Bitboard.lsb(attackers);
            Square sq = Square.valueOf(sqInd);
            int myVal = evalPiece(board.getPiece(sq));
            if (lvSq==null || myVal < lvScore) {
                lvSq = sq;
                lvScore = myVal;
            }
            attackers ^= Bitboard.squares[sqInd];
        }

        return lvSq;
    }

    private static boolean seesAreEqual(int javaScore, Board board, Move mv) {
        if (Initializer.nativeCodeInitialized()) {
            try {
                int nativeSccore = seeNative(board, MoveUtils.toNativeMove(mv));
                if (javaScore != nativeSccore) {
                    LOGGER.error("sees not equal!  javaScore: " + javaScore + ", nativeScore: " + nativeSccore
                            + ", mv: " + mv);
                    DrawBoard.drawBoard(board);
                    return false;
                }
                return true;
            } catch (IllegalStateException e) {
                LOGGER.error(e);
                throw e;
            }
        } else {
            return true;
        }
    }

}
