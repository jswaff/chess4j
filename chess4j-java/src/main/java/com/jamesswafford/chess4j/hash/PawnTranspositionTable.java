package com.jamesswafford.chess4j.hash;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.init.Initializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

public class PawnTranspositionTable extends AbstractTranspositionTable {

    private static final Logger LOGGER = LogManager.getLogger(PawnTranspositionTable.class);

    private static final int DEFAULT_SIZE_BYTES = 128 * 1024 * 1024;

    static {
        Initializer.init();
    }

    private PawnTranspositionTableEntry[] table;

    public static int getDefaultSizeBytes() {
        if (Initializer.nativeCodeInitialized()) {
            return 0;
        }
        return DEFAULT_SIZE_BYTES;
    }

    public PawnTranspositionTable() {
        this(getDefaultSizeBytes());
    }

    public PawnTranspositionTable(int sizeBytes) {
        super(sizeBytes);
    }

    @Override
    public void clear() {
        clearStats();
        Arrays.fill(table, null);
        if (Initializer.nativeCodeInitialized()) {
            clearNative();
        }
    }

    private native void clearNative();

    @Override
    public long getNumCollisions() {
        if (Initializer.nativeCodeInitialized()) {
            return getNumCollisionsNative();
        }
        return numCollisions;
    }

    @Override
    public long getNumHits() {
        if (Initializer.nativeCodeInitialized()) {
            return getNumHitsNative();
        }
        return numHits;
    }

    @Override
    public long getNumProbes() {
        if (Initializer.nativeCodeInitialized()) {
            return getNumProbesNative();
        }
        return numProbes;
    }

    public PawnTranspositionTableEntry probe(long pawnKey) {

        numProbes++;
        PawnTranspositionTableEntry te = table[getTableIndex(pawnKey)];

        if (te != null) {
            // compare full signature to avoid collisions
            if (te.getZobristKey() != pawnKey) {
                numCollisions++;
                return null;
            } else {
                numHits++;
            }
        }

        return te;
    }

    public PawnTranspositionTableEntry probe(Board board) {
        if (Initializer.nativeCodeInitialized()) {
            long nativeVal = probeNative(board);
            return nativeVal==0 ? null : new PawnTranspositionTableEntry(board.getPawnKey(), nativeVal);
        } else {
            return probe(board.getPawnKey());
        }
    }

    private native long probeNative(Board board);

    public void store(long pawnKey, int mgscore, int egscore) {
        PawnTranspositionTableEntry te = new PawnTranspositionTableEntry(pawnKey, mgscore, egscore);
        table[getTableIndex(pawnKey)] = te;
    }

    /*
     * This is a convenience method, wrapping the previous "store".  It also serves as a hook into the native
     * code.  The only time this method would be used when native code is enabled is when assertions are on,
     * to verify search equality.
     */
    public void store(Board board, int mgscore, int egscore) {
        if (Initializer.nativeCodeInitialized()) {
            PawnTranspositionTableEntry entry = new PawnTranspositionTableEntry(board.getPawnKey(), mgscore, egscore);
            storeNative(board, entry.getVal());
        } else {
            store(board.getPawnKey(), mgscore, egscore);
        }
    }

    private native void storeNative(Board board, long val);

    @Override
    protected void createTable(int sizeBytes) {
        int numEntries = sizeBytes / sizeOfEntry();
        LOGGER.debug("# c4j pawn hash size: " + sizeBytes + " bytes ==> " + numEntries + " elements.");
        table = new PawnTranspositionTableEntry[numEntries];
    }

    @Override
    protected void resizeTable(int sizeBytes) {
        if (Initializer.nativeCodeInitialized()) {
            resizeNative(sizeBytes);
        } else {
            createTable(sizeBytes);
        }
    }

    @Override
    public int tableCapacity() {
        return table.length;
    }

    @Override
    public int sizeOfEntry() {
        return PawnTranspositionTableEntry.sizeOf();
    }

    private native long getNumCollisionsNative();

    private native long getNumHitsNative();

    private native long getNumProbesNative();

    private native void resizeNative(int sizeBytes);

}
