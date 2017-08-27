package com.jamesswafford.chess4j.board;

import com.jamesswafford.chess4j.Color;
import com.jamesswafford.chess4j.pieces.Bishop;
import com.jamesswafford.chess4j.pieces.Knight;
import com.jamesswafford.chess4j.pieces.Queen;
import com.jamesswafford.chess4j.pieces.Rook;
import com.jamesswafford.chess4j.utils.BoardUtils;

/**
 * Detects if zugzwang is likely, for either side.   Zugzwang is the condition in which making
 * any move will weaken your position.  
 * 
 * @author James
 *
 */
public class ZugzwangDetector {

	public static boolean isZugzwang(Board board) {
		
		int numWhite = board.getNumPieces(Queen.WHITE_QUEEN) + board.getNumPieces(Rook.WHITE_ROOK)
				+ board.getNumPieces(Knight.WHITE_KNIGHT) + board.getNumPieces(Bishop.WHITE_BISHOP);
		int numBlack = board.getNumPieces(Queen.BLACK_QUEEN) + board.getNumPieces(Rook.BLACK_ROOK)
				+ board.getNumPieces(Knight.BLACK_KNIGHT) + board.getNumPieces(Bishop.BLACK_BISHOP);
		
		assert(numWhite == BoardUtils.getNumNonPawns(board, Color.WHITE));
		assert(numBlack == BoardUtils.getNumNonPawns(board, Color.BLACK));
		
		return (numWhite==0 || numBlack==0);
	}
	
}
