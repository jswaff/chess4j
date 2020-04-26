package com.jamesswafford.chess4j.search;

import com.jamesswafford.chess4j.board.Bitboard;
import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Color;
import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.board.squares.Direction;
import com.jamesswafford.chess4j.board.squares.Square;
import com.jamesswafford.chess4j.eval.EvalMaterial;
import com.jamesswafford.chess4j.movegen.AttackDetector;
import com.jamesswafford.chess4j.movegen.Magic;
import com.jamesswafford.chess4j.pieces.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SEE {

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

    // note m should already be applied
    public static int see(Board b,Move m) {
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
        assert(m.captured() != null);
        assert(b.getPiece(m.from())==null);

        int scores[] = new int[32];
        scores[0] = EvalMaterial.evalPiece(m.captured());
        int scoresInd = 1;

        // play out the sequence
        long whiteAttackersMap = AttackDetector.getAttackers(b,m.to(),Color.WHITE);
        long blackAttackersMap = AttackDetector.getAttackers(b,m.to(),Color.BLACK);

        Color sideToMove = b.getPlayerToMove();
        Square currentSq = m.from();
        Piece currentPiece = b.getPiece(m.to());
        int attackedPieceVal = EvalMaterial.evalPiece(currentPiece);

        while (true) {
            // add any x-ray attackers back in, behind currentPiece in
            // the direction of m.to -> currentSq
            if (!(currentPiece instanceof Knight) && !(currentPiece instanceof King)) {
                Optional<Direction> dir = Direction.getDirectionTo(m.to(),currentSq);

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
            attackedPieceVal = EvalMaterial.evalPiece(currentPiece);
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
            int myVal = EvalMaterial.evalPiece(board.getPiece(sq));
            if (lvSq==null || myVal < lvScore) {
                lvSq = sq;
                lvScore = myVal;
            }
            attackers ^= Bitboard.squares[sqInd];
        }

        return lvSq;
    }

}
