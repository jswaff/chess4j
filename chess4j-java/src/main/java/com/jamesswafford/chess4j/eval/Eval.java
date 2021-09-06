package com.jamesswafford.chess4j.eval;

import com.jamesswafford.chess4j.board.Bitboard;
import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Color;
import com.jamesswafford.chess4j.board.squares.Square;
import com.jamesswafford.chess4j.hash.PawnTranspositionTableEntry;
import com.jamesswafford.chess4j.hash.TTHolder;
import com.jamesswafford.chess4j.init.Initializer;
import com.jamesswafford.chess4j.pieces.Bishop;
import com.jamesswafford.chess4j.pieces.Knight;
import com.jamesswafford.chess4j.pieces.Queen;
import com.jamesswafford.chess4j.pieces.Rook;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.BiFunction;

import static com.jamesswafford.chess4j.eval.EvalKing.evalKing;

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
        int mgScore = EvalMaterial.evalMaterial(board);
        int egScore = mgScore;

        if (!materialOnly) {
            mgScore += evalPawns(board);
            mgScore += evalPieces(board.getWhiteKnights(), board, EvalKnight::evalKnight)
                    - evalPieces(board.getBlackKnights(), board, EvalKnight::evalKnight);
            mgScore += evalPieces(board.getWhiteBishops(), board, EvalBishop::evalBishop)
                    - evalPieces(board.getBlackBishops(), board, EvalBishop::evalBishop);
            mgScore += evalPieces(board.getWhiteRooks(), board, EvalRook::evalRook)
                    - evalPieces(board.getBlackRooks(), board, EvalRook::evalRook);
            mgScore += evalPieces(board.getWhiteQueens(), board, EvalQueen::evalQueen)
                    - evalPieces(board.getBlackQueens(), board, EvalQueen::evalQueen);
            // currently the only method that does a separate eval for middle game and end game is the king eval
            egScore = mgScore;
            mgScore += evalKing(board, board.getKingSquare(Color.WHITE), false)
                    - evalKing(board, board.getKingSquare(Color.BLACK), false);
            egScore += evalKing(board, board.getKingSquare(Color.WHITE), true)
                    - evalKing(board, board.getKingSquare(Color.BLACK), true);
        }

        // calculate the game phase
        int phase = phase(board);

        // calculate the overall evaluation in the range [mgScore, egScore], based on phase
        // http://www.talkchess.com/forum3/viewtopic.php?t=65466
        phase = (phase * 256 + 12) / 24; // [0, 256]
        int score = (mgScore * (256 - phase) + egScore * phase) / 256;

        return board.getPlayerToMove() == Color.WHITE ? score : -score;
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

    public static int scale(int score, int material) {
        final int ALL_NONPAWN_PIECES_VAL = EvalMaterial.QUEEN_VAL +
                EvalMaterial.ROOK_VAL*2 + EvalMaterial.KNIGHT_VAL*2 + EvalMaterial.BISHOP_VAL*2;

        return score * material / ALL_NONPAWN_PIECES_VAL;
    }

    /**
     * Calculate the game phase.  The game phase is an integer in the range [0, 24].
     * 0 means "no pieces removed", and 24 means "all pieces removed".  More valuable pieces weight
     * the phase more heavily.
     *
     * @param board - the board being evaluated
     * @return - the phase
     */
    public static int phase(Board board) {
        return 24 -
                board.getNumPieces(Queen.WHITE_QUEEN) * 4 -
                board.getNumPieces(Queen.BLACK_QUEEN) * 4 -
                board.getNumPieces(Rook.WHITE_ROOK) * 2 -
                board.getNumPieces(Rook.BLACK_ROOK) * 2 -
                board.getNumPieces(Bishop.WHITE_BISHOP) -
                board.getNumPieces(Bishop.BLACK_BISHOP) -
                board.getNumPieces(Knight.WHITE_KNIGHT) -
                board.getNumPieces(Knight.BLACK_KNIGHT);
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
