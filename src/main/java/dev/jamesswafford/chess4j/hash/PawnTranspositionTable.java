package dev.jamesswafford.chess4j.hash;

import dev.jamesswafford.chess4j.nativelib.NativeEngineLib;
import dev.jamesswafford.chess4j.board.Board;
import dev.jamesswafford.chess4j.nativelib.NativeLibraryLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

public class PawnTranspositionTable extends AbstractTranspositionTable {

    private static final Logger LOGGER = LogManager.getLogger(PawnTranspositionTable.class);

    private static final long DEFAULT_SIZE_BYTES = 8 * 1024 * 1024;

    static {
        NativeLibraryLoader.init();
    }

    private PawnTranspositionTableEntry[] table;

    public static long getDefaultSizeBytes() {
        if (NativeLibraryLoader.nativeCodeInitialized()) {
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
        if (NativeLibraryLoader.nativeCodeInitialized()) {
            NativeEngineLib.clearPawnHashTable();
        }
    }

    @Override
    public long getNumCollisions() {
        if (NativeLibraryLoader.nativeCodeInitialized()) {
            return NativeEngineLib.getPawnHashCollisions();
        }
        return numCollisions;
    }

    @Override
    public long getNumHits() {
        if (NativeLibraryLoader.nativeCodeInitialized()) {
            return NativeEngineLib.getPawnHashHits();
        }
        return numHits;
    }

    @Override
    public long getNumProbes() {
        if (NativeLibraryLoader.nativeCodeInitialized()) {
            return NativeEngineLib.getPawnHashProbes();
        }
        return numProbes;
    }

    // when native code is enabled, probe in the native layer so that the native and Java searches
    // produce equivalent results.
    public PawnTranspositionTableEntry probe(Board board) {
        if (NativeLibraryLoader.nativeCodeInitialized()) {
            return NativeEngineLib.probePawnHashTable(board);
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
        long key = board.getPawnKey();
        PawnTranspositionTableEntry entry = new PawnTranspositionTableEntry(key, mgscore, egscore);
        if (NativeLibraryLoader.nativeCodeInitialized()) {
            NativeEngineLib.storePawnHashTable(board, entry);
        } else {
            table[getTableIndex(key)] = entry;
        }
    }

    @Override
    protected void createTable(long sizeBytes) {
        int numEntries = (int)(sizeBytes / sizeOfEntry());
        table = new PawnTranspositionTableEntry[numEntries];
    }

    @Override
    protected void resizeTable(long sizeBytes) {
        if (NativeLibraryLoader.nativeCodeInitialized()) {
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

}
