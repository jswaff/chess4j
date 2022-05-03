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

import static com.jamesswafford.chess4j.eval.EvalKing.*;
import static com.jamesswafford.chess4j.eval.EvalMaterial.*;
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

    public static int eval(EvalWeights weights, Board board) {
        return eval(weights, board, false);
    }

    public static int eval(EvalWeights weights, Board board, boolean materialOnly) {

        int evalScore = evalHelper(weights, board, materialOnly);

        assert(verifyEvalSymmetry(weights, evalScore, board, materialOnly));
        assert(verifyNativeEvalIsEqual(evalScore, board, materialOnly));
        assert(verifyExtractedFeatures(weights, evalScore, board, materialOnly));

        return evalScore;
    }

    private static int evalHelper(EvalWeights weights, Board board, boolean materialOnly) {
        int matScore = EvalMaterial.evalMaterial(weights, board);
        if (materialOnly) {
            return board.getPlayerToMove() == Color.WHITE ? matScore : -matScore;
        }

        // evaluate for a draw.  positions that are drawn by rule are immediately returned.  others
        // that are "drawish" are further evaluated but later tapered down.
//        MaterialType materialType = EvalMaterial.calculateMaterialType(board);
        int drawFactor = 1;
//        if (immediateDraws.contains(materialType)) {
//            return 0;
//        }
//        if (factor8Draws.contains(materialType)) {
//            drawFactor = 8;
//        }

        int mgScore = matScore;
        int egScore = mgScore;

        // eval pawns
        mgScore += evalPawns(weights, board, false);
        egScore += evalPawns(weights, board, true);

        // eval knights
        mgScore += evalPieces(weights, board.getWhiteKnights(), board, false, EvalKnight::evalKnight)
                - evalPieces(weights, board.getBlackKnights(), board, false, EvalKnight::evalKnight);

        egScore += evalPieces(weights, board.getWhiteKnights(), board, true, EvalKnight::evalKnight)
                - evalPieces(weights, board.getBlackKnights(), board, true, EvalKnight::evalKnight);

        // eval bishops
        mgScore += evalPieces(weights, board.getWhiteBishops(), board, false, EvalBishop::evalBishop)
                - evalPieces(weights, board.getBlackBishops(), board, false, EvalBishop::evalBishop);

        egScore += evalPieces(weights, board.getWhiteBishops(), board, true, EvalBishop::evalBishop)
                - evalPieces(weights, board.getBlackBishops(), board, true, EvalBishop::evalBishop);

        // eval rooks
        mgScore += evalPieces(weights, board.getWhiteRooks(), board, false, EvalRook::evalRook)
                - evalPieces(weights, board.getBlackRooks(), board, false, EvalRook::evalRook);

        egScore += evalPieces(weights, board.getWhiteRooks(), board, true, EvalRook::evalRook)
                - evalPieces(weights, board.getBlackRooks(), board, true, EvalRook::evalRook);

        // eval queens
        mgScore += evalPieces(weights, board.getWhiteQueens(), board, false, EvalQueen::evalQueen)
                - evalPieces(weights, board.getBlackQueens(), board, false, EvalQueen::evalQueen);

        egScore += evalPieces(weights, board.getWhiteQueens(), board, true, EvalQueen::evalQueen)
                - evalPieces(weights, board.getBlackQueens(), board, true, EvalQueen::evalQueen);

        // eval kings
        mgScore += evalKing(weights, board, board.getKingSquare(Color.WHITE), false)
                - evalKing(weights, board, board.getKingSquare(Color.BLACK), false);

        egScore += evalKing(weights, board, board.getKingSquare(Color.WHITE), true)
                - evalKing(weights, board, board.getKingSquare(Color.BLACK), true);

        // blend the middle game score and end game score, and divide by the draw factor
        int taperedScore = EvalTaper.taper(board, mgScore, egScore) / drawFactor;

        // return the score from the perspective of the player on move
        return board.getPlayerToMove() == Color.WHITE ? taperedScore : -taperedScore;
    }

    private static int evalPieces(EvalWeights weights, long pieceMap, Board board, boolean endgame,
                                  Function4<EvalWeights, Board, Square, Boolean, Integer> evalFunc) {
        int score = 0;

        while (pieceMap != 0) {
            int sqVal = Bitboard.lsb(pieceMap);
            Square sq = Square.valueOf(sqVal);
            score += evalFunc.apply(weights, board, sq, endgame);
            pieceMap ^= Bitboard.squares[sqVal];
        }

        return score;
    }

    private static int evalPawns(EvalWeights weights, Board board, boolean endgame) {

        // try the pawn hash
        // FIXME
        /*if (Globals.isPawnHashEnabled()) {
            PawnTranspositionTableEntry pte = TTHolder.getInstance().getPawnHashTable().probe(board.getPawnKey());
            if (pte != null) {
                assert (pte.getScore() == evalPawnsNoHash(weights, board, endgame));
                return pte.getScore();
            }
        }*/

        int score = evalPawnsNoHash(weights, board, endgame);

        if (Globals.isPawnHashEnabled()) {
            TTHolder.getInstance().getPawnHashTable().store(board.getPawnKey(), score);
        }

        return score;
    }

    private static int evalPawnsNoHash(EvalWeights weights, Board board, boolean endgame) {
        return evalPieces(weights, board.getWhitePawns(), board, endgame, EvalPawn::evalPawn)
                - evalPieces(weights, board.getBlackPawns(), board, endgame, EvalPawn::evalPawn);
    }

    public static native int evalNative(Board board, boolean materialOnly);

    @Override
    public int evaluateBoard(Board board) {
        return eval(Globals.getEvalWeights(), board);
    }

    public static double[] extractFeatures(Board board) {

        EvalWeights evalWeights = new EvalWeights();
        double[] features = new double[evalWeights.vals.length];

        double phase = EvalTaper.phaseD(board);

        extractMaterialFeatures(features, board);

        extractFeatures(features, board.getWhitePawns() | board.getBlackPawns(), board, phase,
                EvalPawn::extractPawnFeatures);

        extractFeatures(features, board.getWhiteKnights() | board.getBlackKnights(), board, phase,
                EvalKnight::extractKnightFeatures);

        extractFeatures(features, board.getWhiteBishops() | board.getBlackBishops(), board, phase,
                EvalBishop::extractBishopFeatures);

        extractFeatures(features, board.getWhiteRooks() | board.getBlackRooks(), board, phase,
                EvalRook::extractRookFeatures);

        extractFeatures(features, board.getWhiteQueens() | board.getBlackQueens(), board, phase,
                EvalQueen::extractQueenFeatures);

        extractKingFeatures(features, board, board.getKingSquare(Color.WHITE), phase);
        extractKingFeatures(features, board, board.getKingSquare(Color.BLACK), phase);

        return features;
    }

    private static void extractFeatures(double[] features, long pieceMap, Board board, double phase,
                                        Function4<double[], Board, Square, Double, Void> extractFunc) {
        while (pieceMap != 0) {
            int sqVal = Bitboard.lsb(pieceMap);
            Square sq = Square.valueOf(sqVal);
            extractFunc.apply(features, board, sq, phase);
            pieceMap ^= Bitboard.squares[sqVal];
        }
    }

    /**
     * Helper method to test eval symmetry
     *
     * @param weights - eval terms vector
     * @param evalScore - the score the board has been evaulated at
     * @param board - the chess board
     * @param materialOnly - whether to evaulate material only
     *
     * @return - true if the eval is symmetric in the given position
     */
    private static boolean verifyEvalSymmetry(EvalWeights weights, int evalScore, Board board, boolean materialOnly) {
        Board flipBoard = board.deepCopy();
        flipBoard.flipVertical();
        int flipScore = evalHelper(weights, flipBoard, materialOnly);
        boolean retVal = flipScore == evalScore;
        flipBoard.flipVertical();
        assert(board.equals(flipBoard));
        return retVal;
    }

    private static boolean verifyNativeEvalIsEqual(int javaScore, Board board, boolean materialOnly) {
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

    private static boolean verifyExtractedFeatures(EvalWeights weights, int evalScore, Board board, boolean materialOnly) {

        double[] features = extractFeatures(board);
        double score = 0;
        int toInd = materialOnly ? EvalWeights.BISHOP_PAIR_IND+1 : features.length;
        for (int i=0;i<toInd;i++) {
            score += features[i] * weights.vals[i];
        }
        score = board.getPlayerToMove().equals(Color.WHITE) ? score : -score;

        assert(Math.abs(score - evalScore) < 1.0);

        return true;
    }


}
