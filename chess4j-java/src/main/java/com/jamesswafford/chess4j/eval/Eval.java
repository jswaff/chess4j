package com.jamesswafford.chess4j.eval;

import com.jamesswafford.chess4j.Color;
import com.jamesswafford.chess4j.board.Bitboard;
import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.io.FenBuilder;
import com.jamesswafford.chess4j.board.squares.Square;
import com.jamesswafford.chess4j.hash.PawnTranspositionTableEntry;
import com.jamesswafford.chess4j.hash.TTHolder;
import com.jamesswafford.chess4j.init.Initializer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import static com.jamesswafford.chess4j.eval.EvalPawn.*;
import static com.jamesswafford.chess4j.eval.EvalBishop.*;
import static com.jamesswafford.chess4j.eval.EvalKnight.*;
import static com.jamesswafford.chess4j.eval.EvalRook.*;
import static com.jamesswafford.chess4j.eval.EvalQueen.*;
import static com.jamesswafford.chess4j.eval.EvalKing.*;

public final class Eval {

    private static final Log LOGGER = LogFactory.getLog(Eval.class);

    static {
        Initializer.init();
    }

    private Eval() { }

    public static int eval(Board board) {
        return eval(board,false);
    }

    public static int eval(Board board, boolean materialOnly) {

        int score = EvalMaterial.evalMaterial(board);

        if (!materialOnly) {
            score += evalPawns(board);
            score += evalKnights(board);
            score += evalBishops(board);
            score += evalRooks(board);
            score += evalQueens(board);
            score += evalKing(board, board.getKingSquare(Color.WHITE))
                    - evalKing(board, board.getKingSquare(Color.BLACK));
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

    public static int scale(int score, int material) {
        final int ALL_NONPAWN_PIECES_VAL = EvalMaterial.QUEEN_VAL +
                EvalMaterial.ROOK_VAL*2 + EvalMaterial.KNIGHT_VAL*2 + EvalMaterial.BISHOP_VAL*2;

        return score * material / ALL_NONPAWN_PIECES_VAL;
    }

    public static native int evalNative(String boardFen, boolean materialOnly);

}
