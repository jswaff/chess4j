package com.jamesswafford.chess4j.hash;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

public class PawnTranspositionTable extends AbstractTranspositionTable {

    private static final Logger LOGGER = LogManager.getLogger(PawnTranspositionTable.class);

    private static final int DEFAULT_ENTRIES = 11184810; // 128 MB

    private PawnTranspositionTableEntry[] table;

    public PawnTranspositionTable() {
        this(DEFAULT_ENTRIES);
    }

    public PawnTranspositionTable(int numEntries) {
        super(numEntries);
    }

    @Override
    public void clear() {
        clearStats();
        Arrays.fill(table, null);
    }

    public PawnTranspositionTableEntry probe(long zobristKey) {
        numProbes++;
        PawnTranspositionTableEntry te = table[getTableIndex(zobristKey)];

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

    public void store(long zobristKey, int score) {
        PawnTranspositionTableEntry te = new PawnTranspositionTableEntry(zobristKey, score);
        table[getTableIndex(zobristKey)] = te;
    }

    @Override
    protected void allocateTable(int capacity) {
        LOGGER.debug("# allocating " + capacity + " elements for pawn table");

        table = new PawnTranspositionTableEntry[capacity];
    }

    @Override
    public int tableCapacity() {
        return table.length;
    }

    @Override
    public int sizeOfEntry() {
        return PawnTranspositionTableEntry.sizeOf();
    }

}
