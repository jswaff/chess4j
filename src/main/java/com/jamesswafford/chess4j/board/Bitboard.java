package com.jamesswafford.chess4j.board;

import com.jamesswafford.chess4j.Color;
import com.jamesswafford.chess4j.board.squares.Direction;
import com.jamesswafford.chess4j.board.squares.East;
import com.jamesswafford.chess4j.board.squares.File;
import com.jamesswafford.chess4j.board.squares.North;
import com.jamesswafford.chess4j.board.squares.NorthEast;
import com.jamesswafford.chess4j.board.squares.NorthWest;
import com.jamesswafford.chess4j.board.squares.Rank;
import com.jamesswafford.chess4j.board.squares.South;
import com.jamesswafford.chess4j.board.squares.SouthEast;
import com.jamesswafford.chess4j.board.squares.SouthWest;
import com.jamesswafford.chess4j.board.squares.Square;
import com.jamesswafford.chess4j.board.squares.West;

public class Bitboard {

	public static long[] squares = new long[64];
	public static long[] ranks = new long[8];
	public static long[] files = new long[8];
	public static long[][] rays = new long[64][8];
	public static long[] knightMoves = new long[64];
	public static long[] kingMoves = new long[64];
	public static long[][] pawnAttacks = new long[64][2];
	
	static {
		for (int i=0;i<64;i++) {
			squares[i] = 1L << i;
		}
		for (int i=0;i<8;i++) {
			files[i] = ranks[i] = 0;
			File f = File.file(i);
			Rank r = Rank.rank(i);
			for (int j=0;j<64;j++) {
				Square sq = Square.valueOf(j);
				if (sq.file()==f) {
					files[i] |= squares[j];
				}
				if (sq.rank()==r) {
					ranks[i] |= squares[j];
				}
			}
		}
	}
	
	// initialize rays
	static {
		for (int i=0;i<64;i++) {
			Square sq = Square.valueOf(i);
			for (int j=0;j<8;j++) {
				rays[i][j] = 0;
			}
			
			for (int j=0;j<64;j++) {
				Square sq2 = Square.valueOf(j);
				if (sq2 != sq) {
					Direction dir = Direction.directionTo[sq.value()][sq2.value()];
					if (dir != null) {
						rays[i][dir.value()] |= Bitboard.squares[j];
					}
				}
			}
		}
	}
	
	// initialize knight moves
	static {
		for (int i=0;i<64;i++) {
			knightMoves[i]=0;
			Square sq = Square.valueOf(i);
			if (sq.file().eastOf(File.FILE_A)) {
				if (sq.rank().southOf(Rank.RANK_7)) {
					knightMoves[i] |= squares[North.getInstance().next(NorthWest.getInstance().next(sq)).value()];
				}
				if (sq.rank().northOf(Rank.RANK_2)) {
					knightMoves[i] |= squares[South.getInstance().next(SouthWest.getInstance().next(sq)).value()];
				}
			}
			if (sq.file().eastOf(File.FILE_B)) {
				if (sq.rank().southOf(Rank.RANK_8)) {
					knightMoves[i] |= squares[NorthWest.getInstance().next(West.getInstance().next(sq)).value()];
				}
				if (sq.rank().northOf(Rank.RANK_1)) {
					knightMoves[i] |= squares[SouthWest.getInstance().next(West.getInstance().next(sq)).value()];
				}
			}
			if (sq.file().westOf(File.FILE_G)) {
				if (sq.rank().southOf(Rank.RANK_8)) {
					knightMoves[i] |= squares[NorthEast.getInstance().next(East.getInstance().next(sq)).value()];
				}
				if (sq.rank().northOf(Rank.RANK_1)) {
					knightMoves[i] |= squares[SouthEast.getInstance().next(East.getInstance().next(sq)).value()];
				}
			}
			if (sq.file().westOf(File.FILE_H)) {
				if (sq.rank().southOf(Rank.RANK_7)) {
					knightMoves[i] |= squares[North.getInstance().next(NorthEast.getInstance().next(sq)).value()];
				}
				if (sq.rank().northOf(Rank.RANK_2)) {
					knightMoves[i] |= squares[South.getInstance().next(SouthEast.getInstance().next(sq)).value()];
				}
			}
		}
	}
	
	// initialize king moves
	static {
		for (int i=0;i<64;i++) {
			kingMoves[i] = 0;
			Square sq = Square.valueOf(i);
			if (sq.rank().southOf(Rank.RANK_8)) {
				if (sq.file().eastOf(File.FILE_A)) {
					kingMoves[i] |= squares[NorthWest.getInstance().next(sq).value()];
				}
				kingMoves[i] |= squares[North.getInstance().next(sq).value()];
				if (sq.file().westOf(File.FILE_H)) {
					kingMoves[i] |= squares[NorthEast.getInstance().next(sq).value()];
				}
			}
			if (sq.file().westOf(File.FILE_H)) {
				kingMoves[i] |= squares[East.getInstance().next(sq).value()];
			}
			if (sq.rank().northOf(Rank.RANK_1)) {
				if (sq.file().westOf(File.FILE_H)) {
					kingMoves[i] |= squares[SouthEast.getInstance().next(sq).value()];
				}
				kingMoves[i] |= squares[South.getInstance().next(sq).value()];
				if (sq.file().eastOf(File.FILE_A)) {
					kingMoves[i] |= squares[SouthWest.getInstance().next(sq).value()];
				}
			}
			if (sq.file().eastOf(File.FILE_A)) {
				kingMoves[i] |= squares[West.getInstance().next(sq).value()];
			}
		}
	}
	
	// initialize pawn attacks
	static {
		for (int i=0;i<64;i++) {
			Square sq = Square.valueOf(i);

			pawnAttacks[i][0] = pawnAttacks[i][1] = 0;
			if (sq.rank() != Rank.RANK_1 && sq.rank() != Rank.RANK_8) {
				if (sq.file().eastOf(File.FILE_A)) {
					pawnAttacks[i][Color.WHITE.getColor()] |= squares[NorthWest.getInstance().next(sq).value()];
					pawnAttacks[i][Color.BLACK.getColor()] |= squares[SouthWest.getInstance().next(sq).value()];
				}
				if (sq.file().westOf(File.FILE_H)) {
					pawnAttacks[i][Color.WHITE.getColor()] |= squares[NorthEast.getInstance().next(sq).value()];
					pawnAttacks[i][Color.BLACK.getColor()] |= squares[SouthEast.getInstance().next(sq).value()];
				}
			}
		}
	}
	
	private long val;
	
	public Bitboard(int sq) {
		this.val = squares[sq];
	}
	
	public Bitboard(long val) {
		this.val = val;
	}
	
	public static long isolateLSB(long mask,int index) {
		int n=0;
		
		for (int i=0;i<64;i++) {
			if ((squares[i] & mask) != 0) {
				if (n==index) {
					return squares[i];
				}
				n++;
			}
		}

		return 0;
	}

	
	public int lsb() {
		return lsb(val);
	}
	
	public static int lsb(long val) {
		return Long.numberOfTrailingZeros(val);
	}
	
	public int msb() {
		return msb(val);
	}

	public static int msb(long val) {
		return 63 - Long.numberOfLeadingZeros(val);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("");
		
		for (int i=0;i<64;i++) {
			if ((squares[i] & val) == 0) {
				sb.append("0");
			} else {
				sb.append("1");
			}
			if (Square.valueOf(i).file()==File.FILE_H) {
				sb.append("\n");
			}
		}
		
		return sb.toString();
	}
	
}
