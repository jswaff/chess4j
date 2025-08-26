package dev.jamesswafford.chess4j.hash;

import dev.jamesswafford.chess4j.NativeEngineLib;
import dev.jamesswafford.chess4j.board.Board;
import dev.jamesswafford.chess4j.init.Initializer;
import dev.jamesswafford.chess4j.io.FENBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

public class PawnTranspositionTable extends AbstractTranspositionTable {

    private static final Logger LOGGER = LogManager.getLogger(PawnTranspositionTable.class);

    private static final long DEFAULT_SIZE_BYTES = 8 * 1024 * 1024;

    static {
        Initializer.init();
    }

    private PawnTranspositionTableEntry[] table;

    public static long getDefaultSizeBytes() {
        if (Initializer.nativeCodeInitialized()) {
            return 0;
        }
        return DEFAULT_SIZE_BYTES;
    }

    public PawnTranspositionTable() {
        this(getDefaultSizeBytes());
    }

    public PawnTranspositionTable(long sizeBytes) {
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

    @Override
    public long getNumCollisions() {
        if (Initializer.nativeCodeInitialized()) {
            return NativeEngineLib.getPawnHashCollisions();
        }
        return numCollisions;
    }

    @Override
    public long getNumHits() {
        if (Initializer.nativeCodeInitialized()) {
            return NativeEngineLib.getPawnHashHits();
        }
        return numHits;
    }

    @Override
    public long getNumProbes() {
        if (Initializer.nativeCodeInitialized()) {
            return NativeEngineLib.getPawnHashProbes();
        }
        return numProbes;
    }

    // when native code is enabled, probe in the native layer so that the native and Java searches
    // produce equivalent results.
    public PawnTranspositionTableEntry probe(Board board) {
        if (Initializer.nativeCodeInitialized()) {
            String fen = FENBuilder.createFen(board, false);
            long nativeVal = probeNative(fen);
            return nativeVal==0 ? null : new PawnTranspositionTableEntry(board.getPawnKey(), nativeVal);
        } else {
            return probe(board.getPawnKey());
        }
    }

    private PawnTranspositionTableEntry probe(long pawnKey) {
        numProbes++;
        PawnTranspositionTableEntry entry = table[getTableIndex(pawnKey)];

        if (entry != null) {
            // compare full signature to avoid collisions
            if (entry.getZobristKey() != pawnKey) {
                numCollisions++;
                return null;
            } else {
                numHits++;
            }
        }

        return entry;
    }

    public void store(Board board, int mgscore, int egscore) {
        PawnTranspositionTableEntry entry = new PawnTranspositionTableEntry(board.getPawnKey(), mgscore, egscore);
        if (Initializer.nativeCodeInitialized()) {
            String fen = FENBuilder.createFen(board, false);
            storeNative(fen, entry.getVal());
        } else {
            table[getTableIndex(board.getPawnKey())] = entry;
        }
    }

    @Override
    protected void createTable(long sizeBytes) {
        int numEntries = (int)(sizeBytes / sizeOfEntry());
        LOGGER.debug("# c4j pawn hash size: " + sizeBytes + " bytes ==> " + numEntries + " elements.");
        table = new PawnTranspositionTableEntry[numEntries];
    }

    @Override
    protected void resizeTable(long sizeBytes) {
        if (Initializer.nativeCodeInitialized()) {
            NativeEngineLib.resizePawnHashTable(sizeBytes);
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

    private native void clearNative();

    private native long probeNative(String fen);

    private native void storeNative(String fen, long val);

}
