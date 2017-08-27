package com.jamesswafford.chess4j.search;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.board.squares.Rank;
import com.jamesswafford.chess4j.eval.Eval;
import com.jamesswafford.chess4j.pieces.Pawn;
import com.jamesswafford.chess4j.pieces.Piece;

public class Extend {

	public static int extendDepth(Board b) {
		assert(b.getUndos().size()>0);
		return extendDepth(b,b.getUndos().get(b.getUndos().size()-1).getMove());
	}
	
	/**
	 * Return amount to extend search by, given <board> with move <m> just played.
	 * @param b
	 * @param lastMove
	 * @return
	 */
	public static int extendDepth(Board b,Move lastMove) {
		return extendDepth(b,lastMove,b.isPlayerInCheck());
	}
	
	public static int extendDepth(Board b,Move lastMove,boolean inCheck) {
		assert(b.getUndos().get(b.getUndos().size()-1).getMove().equals(lastMove));
		
		if (inCheck) return 1;
		if (lastMove.promotion() != null) return 1;
		
		//int d = pawnTo7th(b,lastMove);
		//if (d > 0) return d;
		
		//d = recaptureExtension(b,lastMove);
		//return d;
		
		return 0;
	}
	
	private static int pawnTo7th(Board b,Move lastMove) {
		Piece p = b.getPiece(lastMove.to());
		if (! (p instanceof Pawn)) return 0;

		if (p.isWhite()) {
			if (lastMove.to().rank() == Rank.RANK_7) return 1;
		} else {
			if (lastMove.to().rank() == Rank.RANK_2) return 1;
		}
		
		return 0;
	}
	
	private static int recaptureExtension(Board b,Move lastMove) {
		if (b.getUndos().size() < 2) return 0;
		
		Piece captured1 = lastMove.captured();
		if (captured1==null) return 0;
		
		Piece captured2 = b.getUndos().get(b.getUndos().size()-2).getMove().captured();
		if (captured2==null) return 0;
		
		int val1 = Eval.getPieceValue(captured1);
		int val2 = Eval.getPieceValue(captured2);
		if (Math.abs(val1-val2) < Eval.PAWN_VAL) return 1;
		
		return 0;
	}
}
