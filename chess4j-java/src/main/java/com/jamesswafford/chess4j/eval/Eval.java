package com.jamesswafford.chess4j.eval;

import com.jamesswafford.chess4j.Globals;
import com.jamesswafford.chess4j.board.Bitboard;
import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Color;
import com.jamesswafford.chess4j.board.squares.Square;
import com.jamesswafford.chess4j.hash.PawnTranspositionTableEntry;
import com.jamesswafford.chess4j.hash.TTHolder;
import com.jamesswafford.chess4j.init.Initializer;
import io.vavr.Function4;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.jamesswafford.chess4j.eval.EvalKing.evalKing;
import static com.jamesswafford.chess4j.eval.MaterialType.*;

public final class Eval implements Evaluator {

    private static final  Logger LOGGER = LogManager.getLogger(Evaluator.class);

    private static final Set<MaterialType> immediateDraws = new HashSet<>();
    private static final Set<MaterialType> factor8Draws = new HashSet<>();

    static {
        immediateDraws.addAll(Arrays.asList(KK, KKN, KKNN, KKB, KNK, KNKN, KNNK, KNKB, KBK, KBKN, KBKB));
        factor8Draws.addAll(Arrays.asList(KPKN, KPKB, KNKP, KBKP));
    }

    static {
        Initializer.init();
    }

    public Eval() { }

    public static int eval(EvalWeightsVector etv, Board board) {
        return eval(etv, board, false);
    }

    public static int eval(EvalWeightsVector etv, Board board, boolean materialOnly) {

        int evalScore = evalHelper(etv, board, materialOnly);

        // if we are running with assertions enabled, test symmetry
        assert(ensureEvalSymmetry(etv, evalScore, board, materialOnly));

        // if we are running with assertions enabled and the native library is loaded, verify equality
        assert(evalsAreEqual(evalScore, board, materialOnly));

        return evalScore;
    }

    private static int evalHelper(EvalWeightsVector etv, Board board, boolean materialOnly) {
        int matScore = EvalMaterial.evalMaterial(board);
        if (materialOnly) {
            return board.getPlayerToMove() == Color.WHITE ? matScore : -matScore;
        }

        // evaluate for a draw.  positions that are drawn by rule are immediately returned.  others
        // that are "drawish" are further evaluated but later tapered down.
        MaterialType materialType = EvalMaterial.calculateMaterialType(board);
        int drawFactor = 1;
        if (immediateDraws.contains(materialType)) {
            return 0;
        }
        if (factor8Draws.contains(materialType)) {
            drawFactor = 8;
        }

        // calculate a middle game score and end game score based on positional features
        int mgScore = matScore;
        int egScore = matScore;

        mgScore += evalPawns(etv, board, false);
//        egScore += evalPawns(etv, board, true);

        mgScore += evalPieces(etv, board.getWhiteKnights(), board, false, EvalKnight::evalKnight)
                - evalPieces(etv, board.getBlackKnights(), board, false, EvalKnight::evalKnight);
//        egScore += evalPieces(etv, board.getWhiteKnights(), board, true, EvalKnight::evalKnight)
//                - evalPieces(etv, board.getBlackKnights(), board, true, EvalKnight::evalKnight);

        mgScore += evalPieces(etv, board.getWhiteBishops(), board, false, EvalBishop::evalBishop)
                - evalPieces(etv, board.getBlackBishops(), board, false, EvalBishop::evalBishop);
//        egScore += evalPieces(etv, board.getWhiteBishops(), board, true, EvalBishop::evalBishop)
//                - evalPieces(etv, board.getBlackBishops(), board, true, EvalBishop::evalBishop);

        mgScore += evalPieces(etv, board.getWhiteRooks(), board, false, EvalRook::evalRook)
                - evalPieces(etv, board.getBlackRooks(), board, false, EvalRook::evalRook);
//        egScore += evalPieces(etv, board.getWhiteRooks(), board, true, EvalRook::evalRook)
//                - evalPieces(etv, board.getBlackRooks(), board, true, EvalRook::evalRook);

        mgScore += evalPieces(etv, board.getWhiteQueens(), board, false, EvalQueen::evalQueen)
                - evalPieces(etv, board.getBlackQueens(), board, false, EvalQueen::evalQueen);
//        egScore += evalPieces(etv, board.getWhiteQueens(), board, true, EvalQueen::evalQueen)
//                - evalPieces(etv, board.getBlackQueens(), board, true, EvalQueen::evalQueen);

        egScore = mgScore;

        mgScore += evalKing(etv, board, board.getKingSquare(Color.WHITE), false)
                - evalKing(etv, board, board.getKingSquare(Color.BLACK), false);
        egScore += evalKing(etv, board, board.getKingSquare(Color.WHITE), true)
                - evalKing(etv, board, board.getKingSquare(Color.BLACK), true);

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

    private static int evalPieces(EvalWeightsVector etv, long pieceMap, Board board, boolean endgame,
                                  Function4<EvalWeightsVector, Board, Square, Boolean, Integer> evalFunc) {
        int score = 0;

        while (pieceMap != 0) {
            int sqVal = Bitboard.lsb(pieceMap);
            Square sq = Square.valueOf(sqVal);
            score += evalFunc.apply(etv, board, sq, endgame);
            pieceMap ^= Bitboard.squares[sqVal];
        }

        return score;
    }

    private static int evalPawns(EvalWeightsVector etv, Board board, boolean endgame) {

        // try the pawn hash
        if (Globals.isPawnHashEnabled()) {
            PawnTranspositionTableEntry pte = TTHolder.getInstance().getPawnHashTable().probe(board.getPawnKey());
            if (pte != null) {
                assert (pte.getScore() == evalPawnsNoHash(etv, board, endgame));
                return pte.getScore();
            }
        }

        int score = evalPawnsNoHash(etv, board, endgame);

        if (Globals.isPawnHashEnabled()) {
            TTHolder.getInstance().getPawnHashTable().store(board.getPawnKey(), score);
        }

        return score;
    }

    private static int evalPawnsNoHash(EvalWeightsVector etv, Board board, boolean endgame) {
        return evalPieces(etv, board.getWhitePawns(), board, endgame, EvalPawn::evalPawn)
                - evalPieces(etv, board.getBlackPawns(), board, endgame, EvalPawn::evalPawn);
    }

    public static native int evalNative(Board board, boolean materialOnly);

    @Override
    public int evaluateBoard(Board board) {
        return eval(Globals.getEvalTermsVector(), board);
    }

    /**
     * Helper method to test eval symmetry
     *
     * @param etv - eval terms vector
     * @param evalScore - the score the board has been evaulated at
     * @param board - the chess board
     * @param materialOnly - whether to evaulate material only
     *
     * @return - true if the eval is symmetric in the given position
     */
    private static boolean ensureEvalSymmetry(EvalWeightsVector etv, int evalScore, Board board, boolean materialOnly) {
        Board flipBoard = board.deepCopy();
        flipBoard.flipVertical();
        int flipScore = evalHelper(etv, flipBoard, materialOnly);
        boolean retVal = flipScore == evalScore;
        flipBoard.flipVertical();
        assert(board.equals(flipBoard));
        return retVal;
    }

}
