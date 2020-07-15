package com.jamesswafford.chess4j.hash;

public class TTHolder {

    private static final TTHolder ttHolder = new TTHolder();

    public static TTHolder getInstance() { return ttHolder; }

    private final TranspositionTable hashTable;
    private final PawnTranspositionTable pawnHashTable;

    private TTHolder() {
        hashTable = new TranspositionTable();
        pawnHashTable = new PawnTranspositionTable();
    }

    public TranspositionTable getHashTable() {
        return hashTable;
    }

    public PawnTranspositionTable getPawnHashTable() {
        return pawnHashTable;
    }

    public void clearTables() {
        getHashTable().clear();
        getPawnHashTable().clear();
    }

    public void resizeMainTable(int maxBytes) {
        getHashTable().resize(maxBytes);
    }

    public void resizePawnTable(int maxBytes) {
        getPawnHashTable().resize(maxBytes);
    }

    public void resizeAllTables(int maxBytes) {
        int maxBytesPerTable = maxBytes / 2;
        getHashTable().resize(maxBytesPerTable);
        getPawnHashTable().resize(maxBytesPerTable);
    }

}
