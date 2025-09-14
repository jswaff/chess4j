package dev.jamesswafford.chess4j.search;

import dev.jamesswafford.chess4j.Constants;
import dev.jamesswafford.chess4j.nativelib.NativeEngineLib;
import dev.jamesswafford.chess4j.board.*;
import dev.jamesswafford.chess4j.board.squares.Direction;
import dev.jamesswafford.chess4j.board.squares.Square;
import dev.jamesswafford.chess4j.nativelib.NativeLibraryLoader;
import dev.jamesswafford.chess4j.io.DrawBoard;
import dev.jamesswafford.chess4j.movegen.AttackDetector;
import dev.jamesswafford.chess4j.movegen.Magic;
import dev.jamesswafford.chess4j.pieces.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static dev.jamesswafford.chess4j.pieces.Bishop.BLACK_BISHOP;
import static dev.jamesswafford.chess4j.pieces.Bishop.WHITE_BISHOP;
import static dev.jamesswafford.chess4j.pieces.King.BLACK_KING;
import static dev.jamesswafford.chess4j.pieces.King.WHITE_KING;
import static dev.jamesswafford.chess4j.pieces.Knight.BLACK_KNIGHT;
import static dev.jamesswafford.chess4j.pieces.Knight.WHITE_KNIGHT;
import static dev.jamesswafford.chess4j.pieces.Pawn.BLACK_PAWN;
import static dev.jamesswafford.chess4j.pieces.Pawn.WHITE_PAWN;
import static dev.jamesswafford.chess4j.pieces.Queen.BLACK_QUEEN;
import static dev.jamesswafford.chess4j.pieces.Queen.WHITE_QUEEN;
import static dev.jamesswafford.chess4j.pieces.Rook.BLACK_ROOK;
import static dev.jamesswafford.chess4j.pieces.Rook.WHITE_ROOK;

public class SEE {

    private static final Logger LOGGER = LogManager.getLogger(SEE.class);

    // note- these values are for move ordering and pruning only, not material evaluation
    public static final int QUEEN_VAL  = 1000;
    public static final int ROOK_VAL   = 500;
    public static final int KNIGHT_VAL = 300;
    public static final int BISHOP_VAL = 300;
    public static final int PAWN_VAL   = 100;

    private static final Map<Piece, Integer> pieceValMap;

    static {
        pieceValMap = new HashMap<>();
        pieceValMap.put(WHITE_KING, Constants.INFINITY);
        pieceValMap.put(WHITE_QUEEN, QUEEN_VAL);
        pieceValMap.put(WHITE_ROOK, ROOK_VAL);
        pieceValMap.put(WHITE_BISHOP, BISHOP_VAL);
        pieceValMap.put(WHITE_KNIGHT, KNIGHT_VAL);
        pieceValMap.put(WHITE_PAWN, PAWN_VAL);

        pieceValMap.put(BLACK_KING, Constants.INFINITY);
        pieceValMap.put(BLACK_QUEEN, QUEEN_VAL);
        pieceValMap.put(BLACK_ROOK, ROOK_VAL);
        pieceValMap.put(BLACK_BISHOP, BISHOP_VAL);
        pieceValMap.put(BLACK_KNIGHT, KNIGHT_VAL);
        pieceValMap.put(BLACK_PAWN, PAWN_VAL);
    }

    static {
        NativeLibraryLoader.init();
    }

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

    private static int scorePromotion(Move m) {
        return seePieceVal(m.promotion()) - PAWN_VAL;
    }

    private static int scoreCapture(Board b, Move m) {
        assert(b.getPiece(m.from()) != null);
        assert(m.piece()==b.getPiece(m.from()));
        assert(m.captured() != null);

        int[] scores = new int[32];
        scores[0] = seePieceVal(m.captured());
        int scoresInd = 1;

        long whiteAttackersMap = AttackDetector.getAttackers(b, m.to(), Color.WHITE);
        long blackAttackersMap = AttackDetector.getAttackers(b, m.to(), Color.BLACK);
        if (b.getPlayerToMove()==Color.WHITE) {
            whiteAttackersMap ^= Bitboard.squares[m.from().value()];
        } else {
            blackAttackersMap ^= Bitboard.squares[m.from().value()];
        }

        Color sideToMove = Color.swap(b.getPlayerToMove());
        Square currentSq = m.from();
        Piece currentPiece = m.piece();
        int attackedPieceVal = seePieceVal(currentPiece);

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

            currentSq = findLeastValuable(b, sideToMove==Color.WHITE ? whiteAttackersMap : blackAttackersMap);
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
            attackedPieceVal = seePieceVal(currentPiece);
            sideToMove = Color.swap(sideToMove);
        }

        // evaluate the sequence
        while (scoresInd > 1) {
            scoresInd--;
            scores[scoresInd-1] = -Math.max(-scores[scoresInd-1], scores[scoresInd]);
        }

        return scores[0];
    }

    public static int seePieceVal(Piece piece) {
        return pieceValMap.get(piece);
    }

    private static Square findLeastValuable(Board board, long attackers) {
        Square lvSq = null;
        int lvScore = 0;

        while (attackers != 0) {
            int sqInd = Bitboard.lsb(attackers);
            Square sq = Square.valueOf(sqInd);
            int myVal = seePieceVal(board.getPiece(sq));
            if (lvSq==null || myVal < lvScore) {
                lvSq = sq;
                lvScore = myVal;
            }
            attackers ^= Bitboard.squares[sqInd];
        }

        return lvSq;
    }

    private static boolean seesAreEqual(int javaScore, Board board, Move mv) {
        if (NativeLibraryLoader.nativeCodeInitialized()) {
            int nativeScore = NativeEngineLib.see(board, mv);
            if (javaScore != nativeScore) {
                LOGGER.error("see not equal!  java: {}, native: {}, mv: {}", javaScore, nativeScore, mv);
                LOGGER.error("moving piece: {}; captured: {}; ep?: {}", mv.piece(), mv.captured(), mv.isEpCapture());
                DrawBoard.drawBoard(board);
                return false;
            }
            return true;
        } else {
            return true;
        }
    }
}
