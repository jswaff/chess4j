package com.jamesswafford.chess4j.pieces;

import com.jamesswafford.chess4j.Color;

public final class King extends Piece {

	public static final King WHITE_KING = new King(Color.WHITE);
	public static final King BLACK_KING = new King(Color.BLACK);
	
	private King(Color color) {
		super(color);
	}
	
	public String toString() {
		return isWhite()?"K":"k";
	}

	@Override
	public Piece getOppositeColorPiece() {
		if (Color.WHITE.equals(getColor())) {
			return BLACK_KING;
		} else {
			return WHITE_KING;
		}
	}
}
