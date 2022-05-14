package com.jamesswafford.chess4j.hash;

import com.jamesswafford.chess4j.init.Initializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

public class PawnTranspositionTable extends AbstractTranspositionTable {

    private static final Logger LOGGER = LogManager.getLogger(PawnTranspositionTable.class);

    private static final int DEFAULT_SIZE_BYTES = 128 * 1024 * 1024;

    private PawnTranspositionTableEntry[] table;

    public static int getDefaultSizeBytes() {
        if (Initializer.nativeCodeInitialized()) {
            return DEFAULT_SIZE_BYTES; // TODO - when pawn hash is implemented in P4 make this 0
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

    public void store(long zobristKey, int mgscore, int egscore) {
        PawnTranspositionTableEntry te = new PawnTranspositionTableEntry(zobristKey, mgscore, egscore);
        table[getTableIndex(zobristKey)] = te;
    }

    @Override
    protected void createTable(int sizeBytes) {
        int numEntries = sizeBytes / sizeOfEntry();
        LOGGER.debug("# c4j pawn hash size: " + sizeBytes + " bytes ==> " + numEntries + " elements.");
        table = new PawnTranspositionTableEntry[numEntries];
    }

    @Override
    protected void resizeTable(int sizeBytes) {
        createTable(sizeBytes);
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
