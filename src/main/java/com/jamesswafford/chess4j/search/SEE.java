package com.jamesswafford.chess4j.search;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.jamesswafford.chess4j.Color;
import com.jamesswafford.chess4j.board.AttackDetector;
import com.jamesswafford.chess4j.board.Bitboard;
import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Magic;
import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.board.squares.Direction;
import com.jamesswafford.chess4j.board.squares.Square;
import com.jamesswafford.chess4j.eval.Eval;
import com.jamesswafford.chess4j.pieces.Bishop;
import com.jamesswafford.chess4j.pieces.King;
import com.jamesswafford.chess4j.pieces.Knight;
import com.jamesswafford.chess4j.pieces.Pawn;
import com.jamesswafford.chess4j.pieces.Piece;
import com.jamesswafford.chess4j.pieces.Queen;
import com.jamesswafford.chess4j.pieces.Rook;

public class SEE {

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
        scores[0] = Eval.getPieceValue(m.captured());
        int scoresInd = 1;

        // play out the sequence
        long whiteAttackersMap = AttackDetector.getAttackers(b,m.to(),Color.WHITE);
        long blackAttackersMap = AttackDetector.getAttackers(b,m.to(),Color.BLACK);

        Color sideToMove = b.getPlayerToMove();
        Square currentSq = m.from();
        Piece currentPiece = b.getPiece(m.to());
        int attackedPieceVal = Eval.getPieceValue(currentPiece);

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
            attackedPieceVal = Eval.getPieceValue(currentPiece);
            sideToMove = Color.swap(sideToMove);
        }

        // evaluate the sequence
        while (scoresInd > 1) {
            scoresInd--;
            scores[scoresInd-1] = -Math.max(-scores[scoresInd-1], scores[scoresInd]);
        }


        return scores[0];
    }

    /**
     * Returns the square with the least valuable piece for <sideToMove>, or NULL
     * if none of the squares have a piece of that color.
     *
     * @param attacker
     * @param sideToMove
     * @return
     */
    private static Square findLeastValuable(Board board,long attackers) {
        Square lvSq = null;
        int lvScore = 0;

        while (attackers != 0) {
            int sqInd = Bitboard.lsb(attackers);
            Square sq = Square.valueOf(sqInd);
            int myVal = Eval.getPieceValue(board.getPiece(sq));
            if (lvSq==null || myVal < lvScore) {
                lvSq = sq;
                lvScore = myVal;
            }
            attackers ^= Bitboard.squares[sqInd];
        }

        return lvSq;
    }

}
