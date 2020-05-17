package com.jamesswafford.chess4j.hash;

public abstract class AbstractTranspositionTable {

    protected long numProbes;
    protected long numHits;
    protected long numCollisions;

    public void clearStats() {
        numProbes = 0;
        numHits = 0;
        numCollisions = 0;
    }

    protected int getMaskedKey(long zobristKey) {
        return ((int)zobristKey) & (tableCapacity()-1);
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
        allocateTable(calculateNumEntries(maxBytes / sizeOfEntry()));
        clear();
    }

    protected int calculateNumEntries(int maxEntries) {
        int n = 2;

        // this loop will set n to a power of 2 > maxEntries
        while (n <= maxEntries) {
            n *= 2;
        }
        n /= 2;

        return n;
    }

    protected abstract void allocateTable(int capacity);

    public abstract int tableCapacity();

    public abstract int sizeOfEntry();

    public abstract void clear();
}
