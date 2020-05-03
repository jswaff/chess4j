package com.jamesswafford.chess4j.hash;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PawnTranspositionTable extends AbstractTranspositionTable {

    private static final  Logger LOGGER = LogManager.getLogger(PawnTranspositionTable.class);

    private static final int DEFAULT_ENTRIES = 1048576; // = 0x100000   ~1 million entries

    private final PawnTranspositionTableEntry[] table;

    public PawnTranspositionTable() {
        this(DEFAULT_ENTRIES);
    }

    public PawnTranspositionTable(int maxEntries) {
        LOGGER.debug("# initializing pawn transposition table.  maxEntries=" + maxEntries);

        setNumEntries(maxEntries);
        table = new PawnTranspositionTableEntry[numEntries];
        clear();

        LOGGER.info("# pawn transposition table initialized with " + numEntries + " entries.");
    }

    public void clear() {
        clearStats();
        for (int i=0; i<numEntries; i++) {
            table[i] = null;
        }
    }

    public PawnTranspositionTableEntry probe(long zobristKey) {
        numProbes++;
        PawnTranspositionTableEntry te = table[getMaskedKey(zobristKey)];

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

    public void store(long zobristKey,int score) {
        PawnTranspositionTableEntry te = new PawnTranspositionTableEntry(zobristKey,score);
        table[getMaskedKey(zobristKey)] = te;
    }

    @Override
    public int sizeOfEntry() {
        return PawnTranspositionTableEntry.sizeOf();
    }

}
