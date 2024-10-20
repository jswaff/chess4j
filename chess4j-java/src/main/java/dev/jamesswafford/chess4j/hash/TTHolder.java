package dev.jamesswafford.chess4j.hash;

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

    public void resizeMainTable(long maxBytes) {
        getHashTable().resizeTable(maxBytes);
    }

    public void resizePawnTable(long maxBytes) {
        getPawnHashTable().resizeTable(maxBytes);
    }

    public void resizeAllTables(long maxBytes) {
        long maxBytesPerTable = maxBytes / 2;
        getHashTable().resizeTable(maxBytesPerTable);
        getPawnHashTable().resizeTable(maxBytesPerTable);
    }

}
