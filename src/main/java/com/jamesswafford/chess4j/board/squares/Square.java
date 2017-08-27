package com.jamesswafford.chess4j.board.squares;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public final class Square {

	public static final int NUM_SQUARES = 64;
	
	private static final List<Square> SQUARES = new ArrayList<Square>();
	private static final Map<File,List<Square>> FILE_SQUARES = new HashMap<File,List<Square>>();
	private static final Map<Rank,List<Square>> RANK_SQUARES = new HashMap<Rank,List<Square>>();

	private final Rank rank;
	private final File file;
	
	private static Square[][] squares_arr = new Square[8][8]; 
	
	private Square(File file,Rank rank) {
		this.file=file;
		this.rank=rank;
	}

	static {
		for (File file : File.values()) {
			List<Square> mySquares = new ArrayList<Square>();
			for (Rank rank : Rank.values()) {
				mySquares.add(new Square(file,rank));
			}
			FILE_SQUARES.put(file, mySquares);
		}
		
		for (Rank rank : Rank.values()) {
			List<Square> mySquares = new ArrayList<Square>();
			for (File file : File.values()) {
				mySquares.add(new Square(file,rank));
			}
			RANK_SQUARES.put(rank, mySquares);
		}
	}

	static {
		for (Rank rank : Rank.values()) {
			for (File file : File.values()) {
				Set<Square> intersection = new HashSet<Square>(FILE_SQUARES.get(file));
				intersection.retainAll(RANK_SQUARES.get(rank));
				assert(intersection.size()==1);
				Square sq = intersection.iterator().next();				
				
				SQUARES.add(sq);
				squares_arr[file.getValue()][rank.getValue()] = sq; 
			}
		}
	}

	public Rank rank() { return rank; }
	public File file() { return file; }

	@Override
	public String toString() {
		return file.getLabel() + rank.getLabel();
	}

	public boolean isLight() {
		int r = rank.getValue();
		int f = file.getValue();
		return (r%2) == (f%2);
	}
	
	public int value() {
		return rank.getValue()*8 + file.getValue();
	}

	public static Square valueOf(File file,Rank rank) {
		if (file==null || rank==null) {
			return null;
		}
		
		return squares_arr[file.getValue()][rank.getValue()];
	}

	public static Square valueOf(int sq) {
		return squares_arr[sq&7][sq/8];
	}
	
	public static List<Square> allSquares() {
		return Collections.unmodifiableList(SQUARES);
	}
	
	public Square flipVertical() {
		return Square.valueOf(file, rank.flip());
	}
	
	public Square flipHorizontal() {
		return Square.valueOf(file.flip(), rank);
	}
	
	public static List<Square> fileSquares(File file) {
		return Collections.unmodifiableList(FILE_SQUARES.get(file));
	}

	public static List<Square> rankSquares(Rank rank) {
		return Collections.unmodifiableList(RANK_SQUARES.get(rank));
	}
	
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Square)) {
			return false;
		}
		Square sq = (Square)o;
		return sq.file().equals(file) && sq.rank().equals(rank);
	}
	
	@Override
	public int hashCode() {
		int hash = 1;
		hash = hash * 13 + rank.hashCode();
		hash = hash * 17 + file.hashCode();
		return hash;
	}
}
