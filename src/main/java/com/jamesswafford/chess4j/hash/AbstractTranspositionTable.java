package com.jamesswafford.chess4j.hash;

public abstract class AbstractTranspositionTable {

    protected int numEntries;
    protected int mask;

    protected long numProbes;
    protected long numHits;
    protected long numCollisions;

    public void clearStats() {
        numProbes = 0;
        numHits = 0;
        numCollisions = 0;
    }

    protected int getMaskedKey(long zobristKey) {
        int iKey = ((int)zobristKey) & mask;
        return iKey;
    }

    public long getNumCollisions() {
        return numCollisions;
    }

    public int getNumEntries() {
        return numEntries;
    }

    public long getNumHits() {
        return numHits;
    }

    public long getNumProbes() {
        return numProbes;
    }

    protected void setNumEntries(int maxEntries) {
        numEntries = 2;

        // this loop will set numEntries to a power of 2 > maxEntries
        while (numEntries <= maxEntries) {
            numEntries *= 2;
        }
        numEntries /= 2;

        mask = numEntries - 1;
    }

}
