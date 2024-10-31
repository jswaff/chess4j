package dev.jamesswafford.chess4j.hash;

import lombok.Getter;

@Getter
public abstract class AbstractTranspositionTable {

    protected long numProbes;
    protected long numHits;
    protected long numCollisions;

    public AbstractTranspositionTable(long maxBytes) {
        createTable(maxBytes);
    }

    public void clearStats() {
        numProbes = 0;
        numHits = 0;
        numCollisions = 0;
    }

    protected int getTableIndex(long zobristKey) {
        return (int)(zobristKey & 0x7FFFFFFF) % tableCapacity();
    }

    protected abstract void createTable(long maxBytes);

    protected abstract void resizeTable(long maxBytes);

    public abstract int tableCapacity();

    public abstract int sizeOfEntry();

    public abstract void clear();

}
