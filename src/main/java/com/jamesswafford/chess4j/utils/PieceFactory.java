package com.jamesswafford.chess4j.utils;

import java.util.HashMap;
import java.util.Map;

import com.jamesswafford.chess4j.pieces.Bishop;
import com.jamesswafford.chess4j.pieces.King;
import com.jamesswafford.chess4j.pieces.Knight;
import com.jamesswafford.chess4j.pieces.Pawn;
import com.jamesswafford.chess4j.pieces.Piece;
import com.jamesswafford.chess4j.pieces.Queen;
import com.jamesswafford.chess4j.pieces.Rook;

public final class PieceFactory {

	private static Map<String,Piece> charToPieceMap;
	
	private PieceFactory() {
		
	}
	
	static {
		charToPieceMap = new HashMap<String,Piece>();
		charToPieceMap.put("R", Rook.WHITE_ROOK);
		charToPieceMap.put("r", Rook.BLACK_ROOK);
		charToPieceMap.put("N", Knight.WHITE_KNIGHT);
		charToPieceMap.put("n", Knight.BLACK_KNIGHT);
		charToPieceMap.put("B", Bishop.WHITE_BISHOP);
		charToPieceMap.put("b", Bishop.BLACK_BISHOP);
		charToPieceMap.put("Q", Queen.WHITE_QUEEN);
		charToPieceMap.put("q", Queen.BLACK_QUEEN);
		charToPieceMap.put("K", King.WHITE_KING);
		charToPieceMap.put("k", King.BLACK_KING);
		charToPieceMap.put("P", Pawn.WHITE_PAWN);
		charToPieceMap.put("p", Pawn.BLACK_PAWN);
	}
	
	public static Piece getPiece(char p) {
		return getPiece(String.valueOf(p));
	}
	
	public static Piece getPiece(String p) {
		return charToPieceMap.get(p);
	}
	
	public static Piece getPiece(char p,boolean wtm) {
		return getPiece(String.valueOf(p),wtm);
	}
	
	public static Piece getPiece(String p,boolean wtm) {
		Piece piece = charToPieceMap.get(p);
		if (piece==null) { 
			return null;
		}
		if (wtm && !piece.isWhite()) {
			piece = piece.getOppositeColorPiece();
		} else if (!wtm && !piece.isBlack()) {
			piece = piece.getOppositeColorPiece();
		}
		return piece;
	}
}
