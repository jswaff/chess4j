package com.jamesswafford.chess4j.hash;

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

    public long getNumCollisions() {
        return numCollisions;
    }

    public long getNumHits() {
        return numHits;
    }

    public long getNumProbes() {
        return numProbes;
    }

    protected abstract void createTable(long maxBytes);

    protected abstract void resizeTable(long maxBytes);

    public abstract int tableCapacity();

    public abstract int sizeOfEntry();

    public abstract void clear();

}
