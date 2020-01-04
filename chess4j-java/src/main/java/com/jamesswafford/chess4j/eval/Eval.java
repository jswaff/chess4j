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

import java.util.function.BiFunction;

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
            score += evalPieces(board.getWhiteKnights(), board, EvalKnight::evalKnight)
                    - evalPieces(board.getBlackKnights(), board, EvalKnight::evalKnight);
            score += evalPieces(board.getWhiteBishops(), board, EvalBishop::evalBishop)
                    - evalPieces(board.getBlackBishops(), board, EvalBishop::evalBishop);
            score += evalPieces(board.getWhiteRooks(), board, EvalRook::evalRook)
                    - evalPieces(board.getBlackRooks(), board, EvalRook::evalRook);
            score += evalPieces(board.getWhiteQueens(), board, EvalQueen::evalQueen)
                    - evalPieces(board.getBlackQueens(), board, EvalQueen::evalQueen);
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

    private static int evalPieces(long pieceMap, Board board, BiFunction<Board, Square, Integer> evalFunc) {
        int score = 0;

        while (pieceMap != 0) {
            int sqVal = Bitboard.lsb(pieceMap);
            Square sq = Square.valueOf(sqVal);
            score += evalFunc.apply(board, sq);
            pieceMap ^= Bitboard.squares[sqVal];
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
        return evalPieces(board.getWhitePawns(), board, EvalPawn::evalPawn)
                - evalPieces(board.getBlackPawns(), board, EvalPawn::evalPawn);
    }

    public static int scale(int score, int material) {
        final int ALL_NONPAWN_PIECES_VAL = EvalMaterial.QUEEN_VAL +
                EvalMaterial.ROOK_VAL*2 + EvalMaterial.KNIGHT_VAL*2 + EvalMaterial.BISHOP_VAL*2;

        return score * material / ALL_NONPAWN_PIECES_VAL;
    }

    public static native int evalNative(String boardFen, boolean materialOnly);

}
