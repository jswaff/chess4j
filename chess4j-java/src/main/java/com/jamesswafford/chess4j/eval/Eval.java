package com.jamesswafford.chess4j.eval;


import java.util.HashMap;
import java.util.Map;

import com.jamesswafford.chess4j.Color;
import com.jamesswafford.chess4j.board.Bitboard;
import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.io.FenBuilder;
import com.jamesswafford.chess4j.board.squares.Square;
import com.jamesswafford.chess4j.hash.PawnTranspositionTableEntry;
import com.jamesswafford.chess4j.hash.TTHolder;
import com.jamesswafford.chess4j.init.Initializer;
import com.jamesswafford.chess4j.pieces.Bishop;
import com.jamesswafford.chess4j.pieces.King;
import com.jamesswafford.chess4j.pieces.Knight;
import com.jamesswafford.chess4j.pieces.Pawn;
import com.jamesswafford.chess4j.pieces.Piece;
import com.jamesswafford.chess4j.pieces.Queen;
import com.jamesswafford.chess4j.pieces.Rook;
import com.jamesswafford.chess4j.utils.OrderedPair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import static com.jamesswafford.chess4j.pieces.Pawn.*;
import static com.jamesswafford.chess4j.pieces.Knight.*;
import static com.jamesswafford.chess4j.pieces.Bishop.*;
import static com.jamesswafford.chess4j.pieces.Rook.*;
import static com.jamesswafford.chess4j.pieces.Queen.*;
import static com.jamesswafford.chess4j.board.squares.File.*;
import static com.jamesswafford.chess4j.board.squares.Square.*;

import static com.jamesswafford.chess4j.eval.EvalPawn.*;
import static com.jamesswafford.chess4j.eval.EvalBishop.*;
import static com.jamesswafford.chess4j.eval.EvalKnight.*;
import static com.jamesswafford.chess4j.eval.EvalRook.*;
import static com.jamesswafford.chess4j.eval.EvalQueen.*;

public final class Eval {

    private static final Log LOGGER = LogFactory.getLog(Eval.class);

    public static final int QUEEN_VAL  = 900;
    public static final int ROOK_VAL   = 500;
    public static final int KNIGHT_VAL = 300;
    public static final int BISHOP_VAL = 320;
    public static final int PAWN_VAL   = 100;
    public static final int ALL_NONPAWN_PIECES_VAL = QUEEN_VAL + ROOK_VAL*2 + KNIGHT_VAL*2 + BISHOP_VAL*2;

    public static final int KING_SAFETY_PAWN_ONE_AWAY = -10;
    public static final int KING_SAFETY_PAWN_TWO_AWAY = -20;
    public static final int KING_SAFETY_PAWN_FAR_AWAY = -30;
    public static final int KING_SAFETY_MIDDLE_OPEN_FILE = -50;

    private static Map<Class<?>,Integer> pieceValMap;

    static {
        pieceValMap = new HashMap<>();
        pieceValMap.put(King.class, Integer.MAX_VALUE);
        pieceValMap.put(Queen.class, QUEEN_VAL);
        pieceValMap.put(Rook.class, ROOK_VAL);
        pieceValMap.put(Bishop.class, BISHOP_VAL);
        pieceValMap.put(Knight.class, KNIGHT_VAL);
        pieceValMap.put(Pawn.class, PAWN_VAL);
    }

    public static final int[] KING_PST = {
       -30,-30,-30,-30,-30,-30,-30,-30,
       -30,-30,-30,-30,-30,-30,-30,-30,
       -30,-30,-30,-30,-30,-30,-30,-30,
       -30,-30,-30,-30,-30,-30,-30,-30,
       -30,-30,-30,-30,-30,-30,-30,-30,
       -20,-20,-20,-20,-20,-20,-20,-20,
       -10,-10,-10,-10,-10,-10,-10,-10,
         0, 10, 20,-25,  0,-25, 20,  0
    };

    public static final int[] KING_ENDGAME_PST = {
         0,  0,  0,  0,  0,  0,  0,  0,
         0, 10, 10, 10, 10, 10, 10,  0,
         0, 10, 20, 20, 20, 20, 10,  0,
         0, 10, 20, 25, 25, 20, 10,  0,
         0, 10, 20, 25, 25, 20, 10,  0,
         0, 10, 20, 20, 20, 20, 10,  0,
         0, 10, 10, 10, 10, 10, 10,  0,
         0,  0,  0,  0,  0,  0,  0,  0 };

    static {
        Initializer.init();
    }

    private Eval() { }

    public static int eval(Board board) {
        return eval(board,false);
    }

    public static int eval(Board board, boolean materialOnly) {

        OrderedPair<Integer, Integer> matNPScore = getNonPawnMaterialScore(board);
        OrderedPair<Integer, Integer> matPScore = getPawnMaterialScore(board);

        int score = matNPScore.getE1() - matNPScore.getE2() + matPScore.getE1() - matPScore.getE2();
        assert ((board.getPlayerToMove() == Color.WHITE ? score : -score) == evalMaterial(board));

        if (!materialOnly) {
            score += evalPawns(board);
            score += evalKnights(board);
            score += evalBishops(board);
            score += evalRooks(board);
            score += evalQueens(board);
            score += evalKings(board, matNPScore);
        }

        int retVal = board.getPlayerToMove() == Color.WHITE ? score : -score;

        // if we are running with assertions enabled and the native library is loaded, verify equality
        assert(evalsAreEqual(retVal, board, materialOnly));

        return retVal;
    }

    private static boolean evalsAreEqual(int javaScore, Board board, boolean materialOnly) {
        if (Initializer.useNative()) {
            String fen = FenBuilder.createFen(board, false);
            try {
                int nativeSccore = evalNative(fen, materialOnly);
                if (javaScore != nativeSccore) {
                    LOGGER.error("evals not equal!  javaScore: " + javaScore + ", nativeScore: " + nativeSccore +
                            ", materialOnly: " + materialOnly + ", fen: " + fen);
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


    private static int evalBishops(Board board) {
        int score = 0;

        long bishopsMap = board.getWhiteBishops();
        while (bishopsMap != 0) {
            int bishopSqVal = Bitboard.msb(bishopsMap);
            Square bishopSq = Square.valueOf(bishopSqVal);
            score += evalBishop(true,bishopSq);
            bishopsMap ^= Bitboard.squares[bishopSqVal];
        }

        bishopsMap = board.getBlackBishops();
        while (bishopsMap != 0) {
            int bishopSqVal = Bitboard.lsb(bishopsMap);
            Square bishopSq = Square.valueOf(bishopSqVal);
            score -= evalBishop(false,bishopSq);
            bishopsMap ^= Bitboard.squares[bishopSqVal];
        }

        return score;
    }

    private static int evalKnights(Board board) {
        int score = 0;

        long knightsMap = board.getWhiteKnights();
        while (knightsMap != 0) {
            int knightSqVal = Bitboard.msb(knightsMap);
            Square knightSq = Square.valueOf(knightSqVal);
            score += evalKnight(board,true,knightSq);
            knightsMap ^= Bitboard.squares[knightSqVal];
        }

        knightsMap = board.getBlackKnights();
        while (knightsMap != 0) {
            int knightSqVal = Bitboard.lsb(knightsMap);
            Square knightSq = Square.valueOf(knightSqVal);
            score -= evalKnight(board,false,knightSq);
            knightsMap ^= Bitboard.squares[knightSqVal];
        }

        return score;
    }

    private static int evalPawns(Board board) {

        // try the pawn hash
        PawnTranspositionTableEntry pte = TTHolder.getPawnTransTable().probe(board.getPawnKey());
        if (pte != null) {
            assert(pte.getScore() == evalPawnsNoHash(board));
            return pte.getScore();
        }

        int score = evalPawnsNoHash(board);
        TTHolder.getPawnTransTable().store(board.getPawnKey(), score);

        return score;
    }

    private static int evalPawnsNoHash(Board board) {
        int score = 0;

        long pawnsMap = board.getWhitePawns();
        while (pawnsMap != 0) {
            int pawnSqVal = Bitboard.msb(pawnsMap);
            Square pawnSq = Square.valueOf(pawnSqVal);
            score += evalPawn(board,true,pawnSq);
            pawnsMap ^= Bitboard.squares[pawnSqVal];
        }

        pawnsMap = board.getBlackPawns();
        while (pawnsMap != 0) {
            int pawnSqVal = Bitboard.lsb(pawnsMap);
            Square pawnSq = Square.valueOf(pawnSqVal);
            score -= evalPawn(board,false,pawnSq);
            pawnsMap ^= Bitboard.squares[pawnSqVal];
        }

        return score;
    }

    private static int evalRooks(Board board) {
        int score = 0;

        long rooksMap = board.getWhiteRooks();
        while (rooksMap != 0) {
            int rookSqVal = Bitboard.msb(rooksMap);
            Square rookSq = Square.valueOf(rookSqVal);
            score += evalRook(board,true,rookSq);
            rooksMap ^= Bitboard.squares[rookSqVal];
        }

        rooksMap = board.getBlackRooks();
        while (rooksMap != 0) {
            int rookSqVal = Bitboard.lsb(rooksMap);
            Square rookSq = Square.valueOf(rookSqVal);
            score -= evalRook(board,false,rookSq);
            rooksMap ^= Bitboard.squares[rookSqVal];
        }

        return score;
    }

    // returns a score from the perspective of white
    private static int evalKings(Board b,OrderedPair<Integer,Integer> matNPScore) {
        int score = 0;

        final int ENDGAME_THRESHOLD = KNIGHT_VAL * 2 + ROOK_VAL;

        Square whiteKingSq = b.getKingSquare(Color.WHITE);
        Square blackKingSq = b.getKingSquare(Color.BLACK);

        // if black has a lot of material then eval white in middle game
        if (matNPScore.getE2() >= ENDGAME_THRESHOLD) {
            score += KING_PST[whiteKingSq.value()];
            score += scale(evalKingSafety(true,b),matNPScore.getE2());
        } else {
            score += KING_ENDGAME_PST[whiteKingSq.value()];
        }
        if (matNPScore.getE1() >= ENDGAME_THRESHOLD) {
            score -= KING_PST[blackKingSq.flipVertical().value()];
            score -= scale(evalKingSafety(false,b),matNPScore.getE1());
        } else {
            score -= KING_ENDGAME_PST[blackKingSq.flipVertical().value()];
        }

        return score;
    }

    // this will return a score from the perspective of the player
    private static int evalKingSafety(boolean isWhite,Board board) {
        int score = 0;

        Square kingSq;
        if (isWhite) {
            kingSq = board.getKingSquare(Color.WHITE);
            // which side are we on?
            if (kingSq.file().eastOf(FILE_E)) {
                // check that pawns on f,g,h are not too far away
                if (board.getPiece(F2) == WHITE_PAWN);
                else if (board.getPiece(F3) == WHITE_PAWN) {
                    score += KING_SAFETY_PAWN_ONE_AWAY;
                } else if (board.getPiece(F4) == WHITE_PAWN) {
                    score += KING_SAFETY_PAWN_TWO_AWAY;
                } else {
                    score += KING_SAFETY_PAWN_FAR_AWAY;
                }

                if (board.getPiece(G2) == WHITE_PAWN);
                else if (board.getPiece(G3) == WHITE_PAWN) {
                    score += KING_SAFETY_PAWN_ONE_AWAY;
                } else if (board.getPiece(G4) == WHITE_PAWN) {
                    score += KING_SAFETY_PAWN_TWO_AWAY;
                } else {
                    score += KING_SAFETY_PAWN_FAR_AWAY;
                }

                if (board.getPiece(H2) == WHITE_PAWN);
                else if (board.getPiece(H3) == WHITE_PAWN) {
                    score += KING_SAFETY_PAWN_ONE_AWAY /2;
                } else if (board.getPiece(H4) == WHITE_PAWN) {
                    score += KING_SAFETY_PAWN_TWO_AWAY /2;
                } else {
                    score += KING_SAFETY_PAWN_FAR_AWAY /2;
                }

            } else if (kingSq.file().westOf(FILE_D)) {
                if (board.getPiece(C2) == WHITE_PAWN);
                else if (board.getPiece(C3) == WHITE_PAWN) {
                    score += KING_SAFETY_PAWN_ONE_AWAY;
                } else if (board.getPiece(C4) == WHITE_PAWN) {
                    score += KING_SAFETY_PAWN_TWO_AWAY;
                } else {
                    score += KING_SAFETY_PAWN_FAR_AWAY;
                }

                if (board.getPiece(B2) == WHITE_PAWN);
                else if (board.getPiece(B3) == WHITE_PAWN) {
                    score += KING_SAFETY_PAWN_ONE_AWAY;
                } else if (board.getPiece(B4) == WHITE_PAWN) {
                    score += KING_SAFETY_PAWN_TWO_AWAY;
                } else {
                    score += KING_SAFETY_PAWN_FAR_AWAY;
                }

                if (board.getPiece(A2) == WHITE_PAWN);
                else if (board.getPiece(A3) == WHITE_PAWN) {
                    score += KING_SAFETY_PAWN_ONE_AWAY /2;
                } else if (board.getPiece(A4) == WHITE_PAWN) {
                    score += KING_SAFETY_PAWN_TWO_AWAY /2;
                } else {
                    score += KING_SAFETY_PAWN_FAR_AWAY /2;
                }
            } else {
                // check if open file
                if ( ((board.getWhitePawns() | board.getBlackPawns())
                        & Bitboard.files[kingSq.file().getValue()])==0)
                {
                    score += KING_SAFETY_MIDDLE_OPEN_FILE;
                }
            }
            // scale down with material?
        } else {
            kingSq = board.getKingSquare(Color.BLACK);
            if (kingSq.file().eastOf(FILE_E)) {
                if (board.getPiece(F7) == BLACK_PAWN);
                else if (board.getPiece(F6) == BLACK_PAWN) {
                    score += KING_SAFETY_PAWN_ONE_AWAY;
                } else if (board.getPiece(F5) == BLACK_PAWN) {
                    score += KING_SAFETY_PAWN_TWO_AWAY;
                } else {
                    score += KING_SAFETY_PAWN_FAR_AWAY;
                }

                if (board.getPiece(G7) == BLACK_PAWN);
                else if (board.getPiece(G6) == BLACK_PAWN) {
                    score += KING_SAFETY_PAWN_ONE_AWAY;
                } else if (board.getPiece(G5) == BLACK_PAWN) {
                    score += KING_SAFETY_PAWN_TWO_AWAY;
                } else {
                    score += KING_SAFETY_PAWN_FAR_AWAY;
                }

                if (board.getPiece(H7) == BLACK_PAWN);
                else if (board.getPiece(H6) == BLACK_PAWN) {
                    score += KING_SAFETY_PAWN_ONE_AWAY /2;
                } else if (board.getPiece(H5) == BLACK_PAWN) {
                    score += KING_SAFETY_PAWN_TWO_AWAY /2;
                } else {
                    score += KING_SAFETY_PAWN_FAR_AWAY /2;
                }
            } else if (kingSq.file().westOf(FILE_D)) {
                if (board.getPiece(C7) == BLACK_PAWN);
                else if (board.getPiece(C6) == BLACK_PAWN) {
                    score += KING_SAFETY_PAWN_ONE_AWAY;
                } else if (board.getPiece(C5) == BLACK_PAWN) {
                    score += KING_SAFETY_PAWN_TWO_AWAY;
                } else {
                    score += KING_SAFETY_PAWN_FAR_AWAY;
                }

                if (board.getPiece(B7) == BLACK_PAWN);
                else if (board.getPiece(B6) == BLACK_PAWN) {
                    score += KING_SAFETY_PAWN_ONE_AWAY;
                } else if (board.getPiece(B5) == BLACK_PAWN) {
                    score += KING_SAFETY_PAWN_TWO_AWAY;
                } else {
                    score += KING_SAFETY_PAWN_FAR_AWAY;
                }

                if (board.getPiece(A7) == BLACK_PAWN);
                else if (board.getPiece(A6) == BLACK_PAWN) {
                    score += KING_SAFETY_PAWN_ONE_AWAY /2;
                } else if (board.getPiece(A5) == BLACK_PAWN) {
                    score += KING_SAFETY_PAWN_TWO_AWAY /2;
                } else {
                    score += KING_SAFETY_PAWN_FAR_AWAY /2;
                }
            } else {
                // check if open file
                if ( ((board.getWhitePawns() | board.getBlackPawns())
                        & Bitboard.files[kingSq.file().getValue()])==0)
                {
                    score += KING_SAFETY_MIDDLE_OPEN_FILE;
                }
            }
        }

        return score;
    }

    private static int evalQueens(Board board) {
        int score = 0;

        long queensMap = board.getWhiteQueens();
        while (queensMap != 0) {
            int queenSqVal = Bitboard.msb(queensMap);
            Square queenSq = Square.valueOf(queenSqVal);
            score += evalQueen(board,true,queenSq);
            queensMap ^= Bitboard.squares[queenSqVal];
        }

        queensMap = board.getBlackQueens();
        while (queensMap != 0) {
            int queenSqVal = Bitboard.lsb(queensMap);
            Square queenSq = Square.valueOf(queenSqVal);
            score -= evalQueen(board,false,queenSq);
            queensMap ^= Bitboard.squares[queenSqVal];
        }

        return score;
    }

    public static int getPieceValue(Piece piece) {
        return pieceValMap.get(piece.getClass());
    }

    public static OrderedPair<Integer,Integer> getPawnMaterialScore(Board board) {
        return new OrderedPair<>(board.getNumPieces(WHITE_PAWN) * PAWN_VAL,
                board.getNumPieces(BLACK_PAWN) * PAWN_VAL);
    }

    // TODO: use Tuple?
    public static OrderedPair<Integer,Integer> getNonPawnMaterialScore(Board board) {
        int wScore = board.getNumPieces(WHITE_QUEEN) * QUEEN_VAL
                + board.getNumPieces(WHITE_ROOK) * ROOK_VAL
                + board.getNumPieces(WHITE_KNIGHT) * KNIGHT_VAL
                + board.getNumPieces(WHITE_BISHOP) * BISHOP_VAL;

        int bScore = board.getNumPieces(BLACK_QUEEN) * QUEEN_VAL
                + board.getNumPieces(BLACK_ROOK) * ROOK_VAL
                + board.getNumPieces(BLACK_KNIGHT) * KNIGHT_VAL
                + board.getNumPieces(BLACK_BISHOP) * BISHOP_VAL;

        return new OrderedPair<>(wScore,bScore);
    }

    public static int evalMaterial(Board board) {
        int score = 0;

        for (Square sq : Square.allSquares()) {
            Piece p = board.getPiece(sq);
            if (p != null) {
                if (p.isWhite()) {
                    score += getPieceValue(p);
                } else {
                    score -= getPieceValue(p);
                }
            }
        }

        return board.getPlayerToMove().equals(Color.WHITE)?score:-score;
    }

    public static int scale(int score,int material) {

        return score * material / ALL_NONPAWN_PIECES_VAL;
    }

    public static native int evalNative(String boardFen, boolean materialOnly);

}
