package com.jamesswafford.chess4j.hash;

public class TTHolder {

	public static int maxEntries = 1048576; // = 0x100000
	private static TranspositionTable transTable;

	public static int maxPawnEntries = 1048576;
	private static PawnTranspositionTable pawnTransTable;
	
	public static TranspositionTable getTransTable() {
		if (transTable==null) {
			transTable = new TranspositionTable(maxEntries);
		}
		return transTable;
	}
	
	public static PawnTranspositionTable getPawnTransTable() {
		if (pawnTransTable==null) {
			pawnTransTable = new PawnTranspositionTable(maxPawnEntries);
		}
		return pawnTransTable;
	}
	
	public static void initTables() {
		transTable = new TranspositionTable(maxEntries);
		pawnTransTable = new PawnTranspositionTable(maxPawnEntries);
	}
}
