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

    public TranspositionTableEntry probe(long zobristKey, int ply) {

        TranspositionTableEntry te = probe(zobristKey);

        // mate scores are stored relative to the position they occurred.  translate that into a score that is
        // relative to the root by inserting the distance from the root to the current position.
        if (te != null && ply > 0) {
            if (isMateScore(te.getScore())) {
                return new TranspositionTableEntry(
                        te.getZobristKey(), te.getType(), te.getScore()-ply, te.getDepth(), te.getMove());
            } else if (isMatedScore(te.getScore())) {
                return new TranspositionTableEntry(
                        te.getZobristKey(), te.getType(), te.getScore()+ply, te.getDepth(), te.getMove());
            }
        }

        return te;
    }

    public TranspositionTableEntry probe(Board board, int ply) {

        if (Initializer.nativeCodeInitialized()) {
            long nativeVal = probeNative(board); // TODO
            return new TranspositionTableEntry(board.getZobristKey(), nativeVal);
        } else {
            return probe(board.getZobristKey(), ply);
        }
    }

    private native long probeNative(Board board);

    /**
     * Store an entry in the transposition table
     */
    public void store(long zobristKey, TranspositionTableEntryType entryType, int score, int depth, Move move) {
        store(zobristKey, entryType, score, depth, move, 0);
    }

    public void store(long zobristKey, TranspositionTableEntryType entryType, int score, int depth, Move move, int ply) {
        table[getTableIndex(zobristKey)] = buildHashTableEntry(zobristKey, entryType, score, depth, move, ply);
    }

    /*
     * This is a convenience method, wrapping the previous "store".  It also serves as a hook into the native
     * code.  The only time this method would be used when native code is enabled is when assertions are on,
     * to verify search equality.
     */
    public void store(Board board, TranspositionTableEntryType entryType, int score, int depth, Move move) {
        store(board, entryType, score, depth, move, 0);
    }

    public void store(Board board, TranspositionTableEntryType entryType, int score, int depth, Move move, int ply) {
        if (Initializer.nativeCodeInitialized()) {
            TranspositionTableEntry entry = buildHashTableEntry(board.getZobristKey(), entryType, score, depth, move, ply);
            storeNative(board, entry.getVal()); // TODO
        } else {
            store(board.getZobristKey(), entryType, score, depth, move, ply);
        }
    }

    private TranspositionTableEntry buildHashTableEntry(long zobristKey, TranspositionTableEntryType entryType,
                                                        int score, int depth, Move move, int ply)
    {
        if (isMateScore(score)) {
            // this score is Mate in N from the root.  We want Mate in M from the current position.
            // e.g. Mate in 5 (from root) ==> Mate in 3 (from current position)
            // therefore, we remove the distance between the root and the current node
            score += ply;
        } else if (isMatedScore(score)) {
            // this score is Mated in N from the root, and we want Mated in M from the current position.
            // e.g. Mated in 6 (from root) ==> Mated in 4 (from current position)
            score -= ply;
        }

        return new TranspositionTableEntry(zobristKey, entryType, score, depth, move);
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
