package com.jamesswafford.chess4j.hash;

public class TTHolder {

    public static int maxEntries = 1048576; // = 0x100000
    private static TranspositionTable alwaysReplaceTransTable;
    private static TranspositionTable depthPreferredTransTable;

    public static int maxPawnEntries = 1048576;
    private static PawnTranspositionTable pawnTransTable;

    public static TranspositionTable getAlwaysReplaceTransTable() {
        if (alwaysReplaceTransTable ==null) {
            alwaysReplaceTransTable = new TranspositionTable(false,maxEntries);
        }
        return alwaysReplaceTransTable;
    }

    public static TranspositionTable getDepthPreferredTransTable() {
        if (depthPreferredTransTable==null) {
            depthPreferredTransTable = new TranspositionTable(true,maxEntries);
        }
        return depthPreferredTransTable;
    }

    public static PawnTranspositionTable getPawnTransTable() {
        if (pawnTransTable==null) {
            pawnTransTable = new PawnTranspositionTable(maxPawnEntries);
        }
        return pawnTransTable;
    }

    public static void initTables() {
        alwaysReplaceTransTable = new TranspositionTable(false,maxEntries);
        depthPreferredTransTable = new TranspositionTable(true,maxEntries);
        pawnTransTable = new PawnTranspositionTable(maxPawnEntries);
    }
}
