package com.jamesswafford.chess4j.hash;

import com.jamesswafford.chess4j.Constants;
import com.jamesswafford.chess4j.board.Move;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

public class TranspositionTable extends AbstractTranspositionTable {

    private static final Logger LOGGER = LogManager.getLogger(TranspositionTable.class);

    public static final int DEFAULT_ENTRIES = 8388608; // 128 MB

    private TranspositionTableEntry[] table;

    public TranspositionTable() {
        this(DEFAULT_ENTRIES);
    }

    public TranspositionTable(int numEntries) {
        super(numEntries);
    }

    @Override
    public void clear() {
        clearStats();
        Arrays.fill(table, null);
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
}
