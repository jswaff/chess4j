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
import static com.jamesswafford.chess4j.eval.EvalKing.extractKingFeatures;
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

    public static int eval(EvalWeightsVector weights, Board board) {
        return eval(weights, board, false);
    }

    public static int eval(EvalWeightsVector weights, Board board, boolean materialOnly) {

        int evalScore = evalHelper(weights, board, materialOnly);

        assert(verifyEvalSymmetry(weights, evalScore, board, materialOnly));
        assert(verifyNativeEvalIsEqual(evalScore, board, materialOnly));
        assert(verifyExtractedFeatures(weights, evalScore, board, materialOnly));

        return evalScore;
    }

    private static int evalHelper(EvalWeightsVector weights, Board board, boolean materialOnly) {
        int matScore = EvalMaterial.evalMaterial(weights, board);
        if (materialOnly) {
            return board.getPlayerToMove() == Color.WHITE ? matScore : -matScore;
        }

        // evaluate for a draw.  positions that are drawn by rule are immediately returned.  others
        // that are "drawish" are further evaluated but later tapered down.
//        MaterialType materialType = EvalMaterial.calculateMaterialType(board);
//        int drawFactor = 1;
//        if (immediateDraws.contains(materialType)) {
//            return 0;
//        }
//        if (factor8Draws.contains(materialType)) {
//            drawFactor = 8;
//        }

        // calculate a middle game score and end game score based on positional features
        int mgScore = matScore;

//        mgScore += evalPawns(weights, board, false);
//
//        mgScore += evalPieces(weights, board.getWhiteKnights(), board, false, EvalKnight::evalKnight)
//                - evalPieces(weights, board.getBlackKnights(), board, false, EvalKnight::evalKnight);
//
//        mgScore += evalPieces(weights, board.getWhiteBishops(), board, false, EvalBishop::evalBishop)
//                - evalPieces(weights, board.getBlackBishops(), board, false, EvalBishop::evalBishop);
//
//        mgScore += evalPieces(weights, board.getWhiteRooks(), board, false, EvalRook::evalRook)
//                - evalPieces(weights, board.getBlackRooks(), board, false, EvalRook::evalRook);
//
//        mgScore += evalPieces(weights, board.getWhiteQueens(), board, false, EvalQueen::evalQueen)
//                - evalPieces(weights, board.getBlackQueens(), board, false, EvalQueen::evalQueen);
//
//        int egScore = mgScore;
//
//        mgScore += evalKing(weights, board, board.getKingSquare(Color.WHITE), false)
//                - evalKing(weights, board, board.getKingSquare(Color.BLACK), false);
//
////        egScore += evalKing(weights, board, board.getKingSquare(Color.WHITE), true)
////                - evalKing(weights, board, board.getKingSquare(Color.BLACK), true);
//
//        // blend the middle game score and end game score, and divide by the draw factor
////        int taperedScore = EvalTaper.taper(board, mgScore, egScore) / drawFactor;

        // return the score from the perspective of the player on move
        return board.getPlayerToMove() == Color.WHITE ? mgScore : -mgScore;
    }

    private static int evalPieces(EvalWeightsVector weights, long pieceMap, Board board, boolean endgame,
                                  Function4<EvalWeightsVector, Board, Square, Boolean, Integer> evalFunc) {
        int score = 0;

        while (pieceMap != 0) {
            int sqVal = Bitboard.lsb(pieceMap);
            Square sq = Square.valueOf(sqVal);
            score += evalFunc.apply(weights, board, sq, endgame);
            pieceMap ^= Bitboard.squares[sqVal];
        }

        return score;
    }

    private static int evalPawns(EvalWeightsVector weights, Board board, boolean endgame) {

        // try the pawn hash
        if (Globals.isPawnHashEnabled()) {
            PawnTranspositionTableEntry pte = TTHolder.getInstance().getPawnHashTable().probe(board.getPawnKey());
            if (pte != null) {
                assert (pte.getScore() == evalPawnsNoHash(weights, board, endgame));
                return pte.getScore();
            }
        }

        int score = evalPawnsNoHash(weights, board, endgame);

        if (Globals.isPawnHashEnabled()) {
            TTHolder.getInstance().getPawnHashTable().store(board.getPawnKey(), score);
        }

        return score;
    }

    private static int evalPawnsNoHash(EvalWeightsVector weights, Board board, boolean endgame) {
        return evalPieces(weights, board.getWhitePawns(), board, endgame, EvalPawn::evalPawn)
                - evalPieces(weights, board.getBlackPawns(), board, endgame, EvalPawn::evalPawn);
    }

    public static native int evalNative(Board board, boolean materialOnly);

    @Override
    public int evaluateBoard(Board board) {
        return eval(Globals.getEvalWeightsVector(), board);
    }

    public static int[] extractFeatures(Board board) {

        int[] mgFeatures = new int[EvalWeightsVector.NUM_WEIGHTS];

        extractMaterialFeatures(mgFeatures, board);

//        extractFeatures(mgFeatures, board.getWhitePawns() | board.getBlackPawns(), board, false,
//                EvalPawn::extractPawnFeatures);
//
//        extractFeatures(mgFeatures, board.getWhiteKnights() | board.getBlackKnights(), board, false,
//                EvalKnight::extractKnightFeatures);
//
//        extractFeatures(mgFeatures, board.getWhiteBishops() | board.getBlackBishops(), board, false,
//                EvalBishop::extractBishopFeatures);
//
//        extractFeatures(mgFeatures, board.getWhiteRooks() | board.getBlackRooks(), board, false,
//                EvalRook::extractRookFeatures);
//
//        extractFeatures(mgFeatures, board.getWhiteQueens() | board.getBlackQueens(), board, false,
//                EvalQueen::extractQueenFeatures);
//
//        extractKingFeatures(mgFeatures, board, board.getKingSquare(Color.WHITE), false);
//        extractKingFeatures(mgFeatures, board, board.getKingSquare(Color.BLACK), false);

        return mgFeatures;
    }

    private static void extractFeatures(int[] features, long pieceMap, Board board, boolean endgame,
                                        Function4<int[], Board, Square, Boolean, Void> extractFunc) {
        while (pieceMap != 0) {
            int sqVal = Bitboard.lsb(pieceMap);
            Square sq = Square.valueOf(sqVal);
            extractFunc.apply(features, board, sq, endgame);
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
    private static boolean verifyEvalSymmetry(EvalWeightsVector weights, int evalScore, Board board, boolean materialOnly) {
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

    private static boolean verifyExtractedFeatures(EvalWeightsVector weights, int evalScore, Board board, boolean materialOnly) {

        int[] features = extractFeatures(board);
        int score = 0;
        int toInd = materialOnly ? EvalWeightsVector.BISHOP_PAIR_IND+1 : features.length;
        for (int i=0;i<toInd;i++) {
            score += features[i] * weights.weights[i];
        }
        score = board.getPlayerToMove().equals(Color.WHITE) ? score : -score;
        assert(score==evalScore);

        return true;
    }


}
