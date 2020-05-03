package com.jamesswafford.chess4j.hash;

import com.jamesswafford.chess4j.Constants;
import com.jamesswafford.chess4j.board.Move;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public class TranspositionTable extends AbstractTranspositionTable {

    private static final  Logger LOGGER = LogManager.getLogger(TranspositionTable.class);

    public static final int DEFAULT_ENTRIES = 1048576; // = 0x100000   ~1 million entries

    private final boolean depthPreferred;
    private final TranspositionTableEntry[] table;

    public TranspositionTable(boolean depthPreferred) {
        this(depthPreferred, DEFAULT_ENTRIES);
    }

    public TranspositionTable(boolean depthPreferred, int maxEntries) {
        LOGGER.debug("# initializing transposition table.  maxEntries=" + maxEntries);

        this.depthPreferred = depthPreferred;
        setNumEntries(maxEntries);
        table = new TranspositionTableEntry[numEntries];
        clear();

        LOGGER.info("# " + (depthPreferred?"depth preferred":"always replace")
                + " transposition table initialized with " + numEntries + " entries.");
    }

    public void clear() {
        clearStats();
        for (int i=0; i<numEntries; i++) {
            table[i] = null;
        }
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

    public Optional<TranspositionTableEntry> probe(long zobristKey) {
        numProbes++;
        TranspositionTableEntry te = table[getMaskedKey(zobristKey)];

        if (te != null) {
            // compare full signature to avoid collisions
            if (te.getZobristKey() != zobristKey) {
                numCollisions++;
                return Optional.empty();
            } else {
                numHits++;
            }
        }

        return Optional.ofNullable(te);
    }

    /**
     * Store an entry in the transposition table, Gerbil style.  Meaning, for now I'm skirting around
     * dealing with the headache that is storing mate scores by storing them as bounds only.
     */
    public boolean store(long zobristKey,TranspositionTableEntryType entryType,int score,int depth,Move move) {

        // if this is a depth preferred table, we don't overwrite entries stored from a deeper search
        if (depthPreferred) {
            TranspositionTableEntry currentEntry = table[getMaskedKey(zobristKey)];
            if (currentEntry != null &&  currentEntry.getDepth() > depth) {
                return false;
            }
        }

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

        TranspositionTableEntry te = new TranspositionTableEntry(zobristKey,entryType,score,depth,move);
        table[getMaskedKey(zobristKey)] = te;

        return true;
    }

    @Override
    public int sizeOfEntry() {
        return TranspositionTableEntry.sizeOf();
    }
}
