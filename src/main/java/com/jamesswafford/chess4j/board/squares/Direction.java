package com.jamesswafford.chess4j.board.squares;


public abstract class Direction {

	public abstract Square next(Square sq);	
	public abstract boolean isDiagonal();
	public abstract int value();
	public static Direction directionTo[][] = new Direction[64][64];
	
	static {
		for (int i=0;i<64;i++) {
			Square sq = Square.valueOf(i);
			for (int j=0;j<64;j++) {
				Square sq2 = Square.valueOf(j);
				directionTo[i][j] = directionTo(sq,sq2);
			}
		}
	}
	
	private static Direction directionTo(Square from,Square to) {
		int fDiff = from.file().getValue() - to.file().getValue();
		int rDiff = from.rank().getValue() - to.rank().getValue();
		
		if (fDiff == 0) {
			// same file
			if (rDiff < 0) return South.getInstance();
			if (rDiff > 0) return North.getInstance();
		} else if (fDiff < 0) {
			// to is east of from
			if (rDiff == 0) return East.getInstance();
			if (rDiff == fDiff) return SouthEast.getInstance();
			if (rDiff == -fDiff) return NorthEast.getInstance();
		} else { // fDiff > 0
			// to is west of from
			if (rDiff == 0) return West.getInstance();
			if (rDiff == fDiff) return NorthWest.getInstance();
			if (rDiff == -fDiff) return SouthWest.getInstance();
		}
		
		return null;
	}
	
}
