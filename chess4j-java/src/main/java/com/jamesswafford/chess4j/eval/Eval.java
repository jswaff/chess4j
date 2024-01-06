package com.jamesswafford.chess4j.eval;

import com.jamesswafford.chess4j.Globals;
import com.jamesswafford.chess4j.board.Bitboard;
import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Color;
import com.jamesswafford.chess4j.board.squares.Square;
import com.jamesswafford.chess4j.hash.PawnTranspositionTableEntry;
import com.jamesswafford.chess4j.hash.TTHolder;
import com.jamesswafford.chess4j.init.Initializer;
import com.jamesswafford.chess4j.tuner.BoardToNetwork;
import com.jamesswafford.ml.nn.Network;
import io.vavr.Function3;
import io.vavr.Function4;
import io.vavr.Tuple2;
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

    public static int eval(Network network, Board board) {
        double[][] X = BoardToNetwork.transform(board);
        double[][] P = network.predict(X);
        int score = (int)(Math.round(P[0][0] * 100.0)); // convert to centi-pawns
        return board.getPlayerToMove().isWhite() ? score : -score;
    }

    public static int eval(EvalWeights weights, Board board) {
        return eval(weights, board, false, false);
    }

    public static int eval(EvalWeights weights, Board board, boolean materialOnly, boolean strict) {

        int evalScore = evalHelper(weights, board, materialOnly, strict);

        assert(verify(weights, evalScore, board, materialOnly, strict));

        return evalScore;
    }

    private static int evalHelper(EvalWeights weights, Board board, boolean materialOnly, boolean strict) {
        int matScore = EvalMaterial.evalMaterial(weights, board, strict);
        if (materialOnly) {
            return board.getPlayerToMove() == Color.WHITE ? matScore : -matScore;
        }

        // evaluate for a draw.  positions that are drawn by rule are immediately returned.  others
        // that are "drawish" are further evaluated but later tapered down.
//        MaterialType materialType = EvalMaterial.calculateMaterialType(board);
        int drawFactor = 1;
        // TODO
//        if (immediateDraws.contains(materialType)) {
//            return 0;
//        }
//        if (factor8Draws.contains(materialType)) {
//            drawFactor = 8;
//        }

        int mgScore = matScore;
        int egScore = mgScore;

        // eval pawns
        Tuple2<Integer, Integer> pawnsScore = evalPawns(weights, board);

        // eval knights
        Tuple2<Integer, Integer> knightsSore = evalPieces(weights,
                board.getWhiteKnights() | board.getBlackKnights(),
                board, EvalKnight::evalKnight);

        // eval bishops
        Tuple2<Integer, Integer> bishopsScore = evalPieces(weights,
                board.getWhiteBishops() | board.getBlackBishops(),
                board, EvalBishop::evalBishop);

        // eval rooks
        Tuple2<Integer, Integer> rooksScore = evalPieces(weights,
                board.getWhiteRooks() | board.getBlackRooks(),
                board, EvalRook::evalRook);

        // eval queens
        Tuple2<Integer, Integer> queensScore = evalPieces(weights,
                board.getWhiteQueens() | board.getBlackQueens(), board,
                EvalQueen::evalQueen);

        // eval kings
        Tuple2<Integer, Integer> wKingScore = evalKing(weights, board, board.getKingSquare(Color.WHITE));
        Tuple2<Integer, Integer> bKingScore = evalKing(weights, board, board.getKingSquare(Color.BLACK));

        mgScore += pawnsScore._1 + knightsSore._1 + bishopsScore._1 + rooksScore._1 + queensScore._1 +
                wKingScore._1 + bKingScore._1;
        egScore += pawnsScore._2 + knightsSore._2 + bishopsScore._2 + rooksScore._2 + queensScore._2 +
                wKingScore._2 + bKingScore._2;

        // blend the middle game score and end game score, and divide by the draw factor
        int taperedScore = EvalTaper.taper(board, mgScore, egScore) / drawFactor;

        // return the score from the perspective of the player on move
        return board.getPlayerToMove() == Color.WHITE ? taperedScore : -taperedScore;
    }

    private static Tuple2<Integer, Integer> evalPieces(EvalWeights weights, long pieceMap, Board board,
                                  Function3<EvalWeights, Board, Square, Tuple2<Integer, Integer>> evalFunc) {

        int mg=0, eg=0;

        while (pieceMap != 0) {
            int sqVal = Bitboard.lsb(pieceMap);
            Square sq = Square.valueOf(sqVal);
            Tuple2<Integer, Integer> score = evalFunc.apply(weights, board, sq);
            mg += score._1; eg += score._2;
            pieceMap ^= Bitboard.squares[sqVal];
        }

        return new Tuple2<>(mg, eg);
    }

    private static Tuple2<Integer, Integer> evalPawns(EvalWeights weights, Board board) {

        // try the pawn hash
        if (Globals.isPawnHashEnabled()) {
            PawnTranspositionTableEntry pte = TTHolder.getInstance().getPawnHashTable().probe(board);
            if (pte != null) {
                assert (pte.getScore().equals(
                        evalPieces(weights, board.getWhitePawns() | board.getBlackPawns(), board, EvalPawn::evalPawn)));
                return pte.getScore();
            }
        }

        Tuple2<Integer, Integer> pawnsScore = evalPieces(weights,
                board.getWhitePawns() | board.getBlackPawns(), board,
                EvalPawn::evalPawn);

        if (Globals.isPawnHashEnabled()) {
            TTHolder.getInstance().getPawnHashTable().store(board, pawnsScore._1, pawnsScore._2);
        }

        return pawnsScore;
    }

    public static native int evalNative(Board board, boolean materialOnly);

    @Override
    public int evaluateBoard(Board board) {
        return eval(Globals.getEvalWeights(), board);
    }

    @Override
    public int evaluateBoardWithNN(Board board) {
        Network network = Globals.getNetwork().orElseThrow(() -> new IllegalStateException("there is no network"));
        return eval(network, board);
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

    private static boolean verify(EvalWeights weights, int evalScore, Board board, boolean materialOnly, boolean strict) {

        // disable the hash to keep the stats from being inflated, which will cause equality checks to fail
        boolean pawnHashEnabled = Globals.isPawnHashEnabled();
        Globals.setPawnHashEnabled(false);

        boolean retVal = verifyEvalSymmetry(weights, evalScore, board, materialOnly, strict) &&
                //verifyNativeEvalIsEqual(evalScore, board, materialOnly) &&
                verifyExtractedFeatures(weights, evalScore, board, materialOnly);

        Globals.setPawnHashEnabled(pawnHashEnabled);

        return retVal;
    }

    /**
     * Helper method to test eval symmetry
     *
     * @param weights - eval terms vector
     * @param evalScore - the score the board has been evaulated at
     * @param board - the chess board
     * @param materialOnly - whether to evaulate material only
     * @param strict - if material only, strict mode doesn't allow knight/rook adjustments or a bishop pair bonus
     *
     * @return - true if the eval is symmetric in the given position
     */
    private static boolean verifyEvalSymmetry(EvalWeights weights, int evalScore, Board board, boolean materialOnly, boolean strict) {
        Board flipBoard = board.deepCopy();
        flipBoard.flipVertical();
        int flipScore = evalHelper(weights, flipBoard, materialOnly, strict);
        boolean retVal = flipScore == evalScore;
        flipBoard.flipVertical();
        assert(board.equals(flipBoard));
        return retVal;
    }

    // this method isn't going to work unless the pawn hash is disabled in the native code
    // alternatively, clear the pawn hash, but disable the search equality check
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
        int toInd = materialOnly ? EvalWeights.ROOK_KAUFMAN_ADJ+1 : features.length;
        for (int i=0;i<toInd;i++) {
            score += features[i] * weights.vals[i];
        }
        score = board.getPlayerToMove().equals(Color.WHITE) ? score : -score;

        // the difference should be very small, within rounding error, unless an immediate draw is found or
        // a drawish ending has scaled the value down
        if (Math.abs(score - evalScore) >= 1.0) {
            MaterialType materialType = EvalMaterial.calculateMaterialType(board);
            assert (immediateDraws.contains(materialType) || factor8Draws.contains(materialType));
        }

        return true;
    }


}
