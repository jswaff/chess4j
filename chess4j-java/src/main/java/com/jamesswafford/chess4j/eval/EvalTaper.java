package com.jamesswafford.chess4j.eval;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.pieces.Bishop;
import com.jamesswafford.chess4j.pieces.Knight;
import com.jamesswafford.chess4j.pieces.Queen;
import com.jamesswafford.chess4j.pieces.Rook;

public class EvalTaper {

    /**
     * Calculate a tapered score.
     *
     * @param board - the board being evaluated
     * @param mgScore - the middle game score
     * @param egScore - the end game score
     * @return - the tapered (blended) score
     */
    public static int taper(Board board, int mgScore, int egScore) {
        int mgPhase = phase(board);
        int egPhase = 24 - mgPhase;

        return (mgScore * mgPhase + egScore * egPhase) / 24;
    }


    /**
     * Calculate the game phase.  The game phase is an integer in the range [0, 24].
     * 24 means "no pieces removed", and 0 means "all pieces removed".  More valuable pieces weight
     * the phase more heavily.
     *
     * @param board - the board being evaluated
     * @return - the phase
     */
    public static int phase(Board board) {
        int phase =
                board.getNumPieces(Queen.WHITE_QUEEN) * 4 +
                board.getNumPieces(Queen.BLACK_QUEEN) * 4 +
                board.getNumPieces(Rook.WHITE_ROOK) * 2 +
                board.getNumPieces(Rook.BLACK_ROOK) * 2 +
                board.getNumPieces(Bishop.WHITE_BISHOP) +
                board.getNumPieces(Bishop.BLACK_BISHOP) +
                board.getNumPieces(Knight.WHITE_KNIGHT) +
                board.getNumPieces(Knight.BLACK_KNIGHT);

        return Math.min(phase, 24);
    }

}
