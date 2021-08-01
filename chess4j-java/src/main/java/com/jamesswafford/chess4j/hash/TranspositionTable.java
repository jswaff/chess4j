package com.jamesswafford.chess4j.hash;

import com.jamesswafford.chess4j.Constants;
import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.init.Initializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

public class TranspositionTable extends AbstractTranspositionTable {

    private static final Logger LOGGER = LogManager.getLogger(TranspositionTable.class);

    private static final int DEFAULT_SIZE_BYTES = 128 * 1024 * 1024;

    static {
        Initializer.init();
    }

    private TranspositionTableEntry[] table;

    public static int getDefaultSizeBytes() {
        if (Initializer.nativeCodeInitialized()) {
            return 0;
        }
        return DEFAULT_SIZE_BYTES;
    }

    public TranspositionTable() {
        this(getDefaultSizeBytes());
    }

    public TranspositionTable(int sizeBytes) {
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

    private int getCheckMateBound() {
        return Constants.CHECKMATE - 500;
    }

    private int getCheckMatedBound() {
        return -getCheckMateBound();
    }

    private boolean isMatedScore(int score) {
        return score <= getCheckMatedBound();
    }

    private boolean isMateScore(int score) {
        return score >= getCheckMateBound();
    }

    public TranspositionTableEntry probe(long zobristKey) {
        numProbes++;
        TranspositionTableEntry te = table[getTableIndex(zobristKey)];

        if (te != null) {
            // compare full signature to avoid collisions
            if (te.getZobristKey() != zobristKey) {
                numCollisions++;
                return null;
            } else {
                numHits++;
            }
        }

        return te;
    }

    public TranspositionTableEntry probe(Board board) {

        if (Initializer.nativeCodeInitialized()) {
            long nativeVal = probeNative(board);
            return new TranspositionTableEntry(board.getZobristKey(), nativeVal);
        } else {
            return probe(board.getZobristKey());
        }
    }

    private native long probeNative(Board board);

    /**
     * Store an entry in the transposition table, Gerbil style.  Meaning, for now I'm skirting around
     * dealing with the headache that is storing mate scores by storing them as bounds only.
     */
    public void store(long zobristKey, TranspositionTableEntryType entryType, int score, int depth, Move move,
                      int hashAge) {
        table[getTableIndex(zobristKey)] = buildHashTableEntry(zobristKey, entryType, score, depth, move, hashAge);
    }

    /*
     * This is a convenience method, wrapping the previous "store".  It also serves as a hook into the native
     * code.  The only time this method would be used when native code is enabled is when assertions are on,
     * to verify search equality.
     */
    public void store(Board board, TranspositionTableEntryType entryType, int score, int depth, Move move,
                      int hashAge) {
        if (Initializer.nativeCodeInitialized()) {
            TranspositionTableEntry entry = buildHashTableEntry(board.getZobristKey(), entryType, score, depth, move,
                    hashAge);
            storeNative(board, entry.getVal());
        } else {
            store(board.getZobristKey(), entryType, score, depth, move, hashAge);
        }
    }

    private TranspositionTableEntry buildHashTableEntry(long zobristKey, TranspositionTableEntryType entryType,
                                                        int score, int depth, Move move, int hashAge)
    {
        if (isMateScore(score)) {
            if (entryType==TranspositionTableEntryType.UPPER_BOUND) {
                // failing low on mate.  don't allow a cutoff, just store any associated move
                entryType = TranspositionTableEntryType.MOVE_ONLY;
            } else {
                // convert to fail high
                entryType = TranspositionTableEntryType.LOWER_BOUND;
                score = getCheckMateBound();
            }
        } else if (isMatedScore(score)) {
            if (entryType==TranspositionTableEntryType.LOWER_BOUND) {
                // failing high on -mate.
                entryType = TranspositionTableEntryType.MOVE_ONLY;
            } else {
                // convert to fail low
                entryType = TranspositionTableEntryType.UPPER_BOUND;
                score = getCheckMatedBound();
            }
        }

        return new TranspositionTableEntry(zobristKey, entryType, score, depth, move, hashAge);
    }

    private native void storeNative(Board board, long val);

    @Override
    protected void createTable(int sizeBytes) {
        int numEntries = sizeBytes / sizeOfEntry();
        LOGGER.debug("# c4j hash size: " + sizeBytes + " bytes ==> " + numEntries + " elements.");
        table = new TranspositionTableEntry[numEntries];
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
        return TranspositionTableEntry.sizeOf();
    }

    private native long getNumCollisionsNative();

    private native long getNumHitsNative();

    private native long getNumProbesNative();

    private native void resizeNative(int sizeBytes);
}
