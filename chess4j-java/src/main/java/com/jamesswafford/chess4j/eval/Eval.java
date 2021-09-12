package com.jamesswafford.chess4j.eval;

import com.jamesswafford.chess4j.board.Bitboard;
import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Color;
import com.jamesswafford.chess4j.board.squares.Square;
import com.jamesswafford.chess4j.hash.PawnTranspositionTableEntry;
import com.jamesswafford.chess4j.hash.TTHolder;
import com.jamesswafford.chess4j.init.Initializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.BiFunction;

import static com.jamesswafford.chess4j.eval.EvalKing.evalKing;
import static com.jamesswafford.chess4j.eval.MaterialType.*;

public final class Eval implements Evaluator {

    private static final  Logger LOGGER = LogManager.getLogger(Evaluator.class);

    static {
        Initializer.init();
    }

    public Eval() { }

    public static int eval(Board board) {
        return eval(board,false);
    }

    public static int eval(Board board, boolean materialOnly) {

        int evalScore = evalHelper(board, materialOnly);

        // if we are running with assertions enabled, test symmetry
        assert(ensureEvalSymmetry(evalScore, board, materialOnly));

        // if we are running with assertions enabled and the native library is loaded, verify equality
        assert(evalsAreEqual(evalScore, board, materialOnly));

        return evalScore;
    }

    private static int evalHelper(Board board, boolean materialOnly) {
        int matScore = EvalMaterial.evalMaterial(board);
        if (materialOnly) {
            return board.getPlayerToMove() == Color.WHITE ? matScore : -matScore;
        }

        // evaluate for a draw.  positions that are drawn by rule are immediately returned.  others
        // that are "drawish" are further evaluated but later tapered down.
        MaterialType materialType = EvalMaterial.calculateMaterialType(board);
        int drawFactor = 1;
        if (KK.equals(materialType) || KBK.equals(materialType) || KNK.equals(materialType)) {
            return 0;
        }
        if (KBKP.equals(materialType) || KNKP.equals(materialType)) {
            drawFactor = 8;
        }

        // calculate a middle game score and end game score based on positional features
        int mgScore = matScore;
        int egScore = matScore;

        mgScore += evalPawns(board);
        mgScore += evalPieces(board.getWhiteKnights(), board, EvalKnight::evalKnight)
                - evalPieces(board.getBlackKnights(), board, EvalKnight::evalKnight);
        mgScore += evalPieces(board.getWhiteBishops(), board, EvalBishop::evalBishop)
                - evalPieces(board.getBlackBishops(), board, EvalBishop::evalBishop);
        mgScore += evalPieces(board.getWhiteRooks(), board, EvalRook::evalRook)
                - evalPieces(board.getBlackRooks(), board, EvalRook::evalRook);
        mgScore += evalPieces(board.getWhiteQueens(), board, EvalQueen::evalQueen)
                - evalPieces(board.getBlackQueens(), board, EvalQueen::evalQueen);

        egScore = mgScore;
        mgScore += evalKing(board, board.getKingSquare(Color.WHITE), false)
                - evalKing(board, board.getKingSquare(Color.BLACK), false);
        egScore += evalKing(board, board.getKingSquare(Color.WHITE), true)
                - evalKing(board, board.getKingSquare(Color.BLACK), true);

        // blend the middle game score and end game score, and divide by the draw factor
        int taperedScore = EvalTaper.taper(board, mgScore, egScore) / drawFactor;

        // return the score from the perspective of the player on move
        return board.getPlayerToMove() == Color.WHITE ? taperedScore : -taperedScore;
    }

    private static boolean evalsAreEqual(int javaScore, Board board, boolean materialOnly) {
        if (Initializer.nativeCodeInitialized()) {
            try {
                int nativeSccore = evalNative(board, materialOnly);
                if (javaScore != nativeSccore) {
                    LOGGER.error("evals not equal!  javaScore: " + javaScore + ", nativeScore: " + nativeSccore +
                            ", materialOnly: " + materialOnly);
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
        PawnTranspositionTableEntry pte = TTHolder.getInstance().getPawnHashTable().probe(board.getPawnKey());
        if (pte != null) {
            assert(pte.getScore() == evalPawnsNoHash(board));
            return pte.getScore();
        }

        int score = evalPawnsNoHash(board);

        TTHolder.getInstance().getPawnHashTable().store(board.getPawnKey(), score);

        return score;
    }

    private static int evalPawnsNoHash(Board board) {
        return evalPieces(board.getWhitePawns(), board, EvalPawn::evalPawn)
                - evalPieces(board.getBlackPawns(), board, EvalPawn::evalPawn);
    }

    public static native int evalNative(Board board, boolean materialOnly);

    @Override
    public int evaluateBoard(Board board) {
        return eval(board);
    }

    /**
     * Helper method to test eval symmetry
     *
     * @param evalScore - the score the board has been evaulated at
     * @param board - the chess board
     * @param materialOnly - whether to evaulate material only
     *
     * @return - true if the eval is symmetric in the given position
     */
    private static boolean ensureEvalSymmetry(int evalScore, Board board, boolean materialOnly) {
        Board flipBoard = board.deepCopy();
        flipBoard.flipVertical();
        int flipScore = evalHelper(flipBoard, materialOnly);
        boolean retVal = flipScore == evalScore;
        flipBoard.flipVertical();
        assert(board.equals(flipBoard));
        return retVal;
    }

}
