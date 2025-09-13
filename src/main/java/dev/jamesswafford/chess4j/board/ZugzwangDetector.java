package dev.jamesswafford.chess4j.board;

import dev.jamesswafford.chess4j.pieces.Bishop;
import dev.jamesswafford.chess4j.pieces.Knight;
import dev.jamesswafford.chess4j.pieces.Queen;
import dev.jamesswafford.chess4j.pieces.Rook;
import dev.jamesswafford.chess4j.utils.BoardUtils;

/**
 * Detects if zugzwang is likely, for either side.   Zugzwang is the condition in which making
 * any move will weaken your position.  
 * 
 */
public class ZugzwangDetector {

    public static boolean isZugzwang(Board board) {

        int numWhite = board.getNumPieces(Queen.WHITE_QUEEN) + board.getNumPieces(Rook.WHITE_ROOK)
                + board.getNumPieces(Knight.WHITE_KNIGHT) + board.getNumPieces(Bishop.WHITE_BISHOP);
        int numBlack = board.getNumPieces(Queen.BLACK_QUEEN) + board.getNumPieces(Rook.BLACK_ROOK)
                + board.getNumPieces(Knight.BLACK_KNIGHT) + board.getNumPieces(Bishop.BLACK_BISHOP);

        assert(numWhite == BoardUtils.countNonPawns(board, Color.WHITE));
        assert(numBlack == BoardUtils.countNonPawns(board, Color.BLACK));

        return (numWhite==0 || numBlack==0);
    }

}
