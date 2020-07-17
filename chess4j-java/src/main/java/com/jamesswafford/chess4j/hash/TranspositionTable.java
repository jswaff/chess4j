package com.jamesswafford.chess4j.hash;

import com.jamesswafford.chess4j.Constants;
import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.init.Initializer;
import com.jamesswafford.chess4j.io.FenBuilder;
import com.jamesswafford.chess4j.utils.MoveUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

public class TranspositionTable extends AbstractTranspositionTable {

    private static final Logger LOGGER = LogManager.getLogger(TranspositionTable.class);

    private static final int DEFAULT_ENTRIES = 8388608; // 128 MB

    static {
        Initializer.init();
    }

    private TranspositionTableEntry[] table;

    public static int getDefaultEntries() {
        if (Initializer.nativeCodeInitialized()) {
            return 0;
        }
        return DEFAULT_ENTRIES;
    }

    public TranspositionTable() {
        this(getDefaultEntries());
    }

    public TranspositionTable(int numEntries) {
        super(numEntries);
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
            String fen = FenBuilder.createFen(board, true);
            long nativeVal = probeNative(fen);
            return new TranspositionTableEntry(board.getZobristKey(), nativeVal);
        } else {
            return probe(board.getZobristKey());
        }
    }

    private native long probeNative(String fen);

    /**
     * Store an entry in the transposition table, Gerbil style.  Meaning, for now I'm skirting around
     * dealing with the headache that is storing mate scores by storing them as bounds only.
     */
    public void store(long zobristKey, TranspositionTableEntryType entryType, int score, int depth, Move move) {

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

        TranspositionTableEntry te = new TranspositionTableEntry(zobristKey, entryType, score, depth, move);
        table[getTableIndex(zobristKey)] = te;
    }

    /*
     * This is a convenience method, wrapping the previous "store".  It also serves as a hook into the native
     * code.  The only time this method would be used when native code is enabled is when assertions are on,
     * to verify search equality.
     */
    public void store(Board board, TranspositionTableEntryType entryType, int score, int depth, Move move) {

        if (Initializer.nativeCodeInitialized()) {
            String fen = FenBuilder.createFen(board, true);
            Long nativeMove = MoveUtils.toNativeMove(move);
            storeNative(fen, entryType.ordinal(), score, depth, nativeMove);
        } else {
            store(board.getZobristKey(), entryType, score, depth, move);
        }
    }

    private native void storeNative(String fen, int entryType, int score, int depth, long move);

    @Override
    protected void allocateTable(int capacity) {
        LOGGER.debug("# allocating " + capacity + " elements for hash table");
        table = new TranspositionTableEntry[capacity];
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

}
