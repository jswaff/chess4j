package com.jamesswafford.chess4j.hash;

public class TTHolder {

    private static final TTHolder ttHolder = new TTHolder();

    public static TTHolder getInstance() { return ttHolder; }

    private final TranspositionTable alwaysReplaceTransTable;
    private final TranspositionTable depthPreferredTransTable;
    private final PawnTranspositionTable pawnTransTable;

    private TTHolder() {
        alwaysReplaceTransTable = new TranspositionTable(false);
        depthPreferredTransTable = new TranspositionTable(true);
        pawnTransTable = new PawnTranspositionTable();
    }

    public TranspositionTable getAlwaysReplaceTransTable() {
        return alwaysReplaceTransTable;
    }

    public TranspositionTable getDepthPreferredTransTable() {
        return depthPreferredTransTable;
    }

    public PawnTranspositionTable getPawnTransTable() {
        return pawnTransTable;
    }

    public void clearTables() {
        getAlwaysReplaceTransTable().clear();
        getDepthPreferredTransTable().clear();
        getPawnTransTable().clear();
    }

    public void resizeMainTables(int maxBytes) {
        int maxBytesPerTable = maxBytes / 2;
        getAlwaysReplaceTransTable().resize(maxBytesPerTable);
        getDepthPreferredTransTable().resize(maxBytesPerTable);
    }

    public void resizePawnTable(int maxBytes) {
        getPawnTransTable().resize(maxBytes);
    }

    public void resizeAllTables(int maxBytes) {
        int maxBytesPerTable = maxBytes / 3;
        getAlwaysReplaceTransTable().resize(maxBytesPerTable);
        getDepthPreferredTransTable().resize(maxBytesPerTable);
        getPawnTransTable().resize(maxBytesPerTable);
    }

}
