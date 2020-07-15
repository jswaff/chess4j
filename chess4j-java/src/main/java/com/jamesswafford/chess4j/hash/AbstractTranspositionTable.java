package com.jamesswafford.chess4j.hash;

public abstract class AbstractTranspositionTable {

    protected long numProbes;
    protected long numHits;
    protected long numCollisions;

    public AbstractTranspositionTable(int numEntries) {
        allocateTable(numEntries);
        clear();
    }

    public void clearStats() {
        numProbes = 0;
        numHits = 0;
        numCollisions = 0;
    }

    protected int getTableIndex(long zobristKey) {
        return (int)(zobristKey & 0x7FFFFFFF) % tableCapacity();
    }

    public long getNumCollisions() {
        return numCollisions;
    }

    public long getNumHits() {
        return numHits;
    }

    public long getNumProbes() {
        return numProbes;
    }

    protected void resize(int maxBytes) {
        allocateTable(maxBytes / sizeOfEntry());
        clear();
    }

    protected abstract void allocateTable(int capacity);

    public abstract int tableCapacity();

    public abstract int sizeOfEntry();

    public abstract void clear();
}
